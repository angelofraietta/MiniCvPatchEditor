import java.awt.*;
import java.awt.event.*;
import javax.swing.*;


/**
 * <p>Title: Midi Controller Patch Editor</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Angelo Fraietta</p>
 * @author Angelo Fraietta
 * @version 1.0
 */


public class MidiDevice extends JPanel{
  final int ANA_1_PULSE_COUNT_CONFIG_ADDRESS = 14;
  final int ANA_IN_SAMPLE_RATE_CONFIG_ADDRESS = 5;
  JPanel contentPane;
  JComboBox cmbDevice = new JComboBox();
  JCheckBox chkMute = new JCheckBox();
  JComboBox cmbMessageType = new JComboBox();
  JComboBox cmbChannel = new JComboBox();
  JCheckBox chkInvert = new JCheckBox();
  JComboBox cmbData1 = new JComboBox();
  JComboBox cmbData2 = new JComboBox();
  ButtonGroup group = new ButtonGroup();

  boolean initialised = false;
  volatile boolean reading = false;
  JCheckBox chkHighRes = new JCheckBox();
  JComboBox cmbData3 = new JComboBox();
  JCheckBox chkScaleInput = new JCheckBox();
  int _device_num = 0;
  int _config_num = 0;
  JTextField lblDevice = new JTextField();
  JCheckBox ChkData1 = new JCheckBox();
  MidiConfig last_config = new MidiConfig();
  JComboBox cmbData4 = new JComboBox();

  public int getMessageTypeLeft()
  {
    return cmbMessageType.getX();
  }
  
  public int getChannelLeft()
  {
    return cmbChannel.getX();
  }

  public int getData1Left()
  {
    return cmbData1.getX();
  }

  public int getData2Left()
  {
    return cmbData2.getX();
  }  
  
  public int getData3Left()
  {
    return cmbData3.getX();
  }

  public int getMuteLeft()
  {
    return chkMute.getX();
  }
    
  public MidiDevice(int device) {
    try {
      _device_num = device;
      _config_num = device;

      jbInit();
      LoadCombos();
      initialised = true;
      ReadConfig();
    }
    catch(Exception e) {
    e.printStackTrace();
  }

  }

  int ScalingConfigNumber()
  {
    if (_device_num < 8)
    {
      return ANA_IN_SAMPLE_RATE_CONFIG_ADDRESS + _device_num;
    }
    else
    {
      return ANA_1_PULSE_COUNT_CONFIG_ADDRESS +  _device_num - 8;
    }
  }

  public void ReadConfig(){
    if (initialised)
    {
      boolean analog_enabled = false;
      reading = true;
      MidiConfig config = new MidiConfig();

      if (_device_num >= 8)
      {
        _config_num = _device_num - 8;

       analog_enabled = Midi.GetAnaOutEnabled(_config_num);

        int pulse_count = Midi.ReadConfigByte(ScalingConfigNumber()) & 0xff;

       cmbData4.setSelectedIndex (pulse_count);

        if (analog_enabled)
        {
          cmbDevice.setSelectedIndex(1);
        }
        else
        {
          cmbDevice.setSelectedIndex(0);
        }

        boolean is_analogue = cmbDevice.getSelectedIndex() > 0;

        if (is_analogue)
        {
          _config_num += 32;
        }
        else
        {
          _config_num += 16;
        }
      }
      config.GetMidiConfig(_config_num);

      if (last_config.compareTo(config) == 0)
      {
        reading = false;
        return;
      }

      last_config = config;

      chkMute.setSelected(config.mute);
      cmbMessageType.setSelectedIndex(config.midi_message);
      cmbChannel.setSelectedIndex(config.midi_channel);
      cmbData1.setSelectedIndex(config.data_1);
      cmbData2.setSelectedIndex(config.data_2);

      cmbData3.setSelectedIndex(config.lower_midi);

      if (config.scale_input  || config.high_resolution)
      {
        cmbData1.setEnabled(!config.mute);
        cmbData2.setEnabled(!config.mute);
      }
      else
      {
        if (config.data1_vary)
        {
          cmbData1.setEnabled(false);
          cmbData2.setEnabled(!config.mute);
        }
        else
        {
          cmbData1.setEnabled(!config.mute);
          cmbData2.setEnabled(false);
        }

      }

      chkScaleInput.setSelected(config.scale_input);

      //chkMidiOut2.setSelected(config.midi_out2);
      //chkMidiOut2.setVisible(!config.scale_input);

      ChkData1.setEnabled(!config.scale_input);

      //rdData1.setSelected(config.data1_vary);
      //rdData1.setVisible(!config.scale_input);
      ChkData1.setSelected(config.data1_vary);

      //rdData2.setSelected(!config.data1_vary);
      //rdData2.setVisible(!config.scale_input);

      chkInvert.setSelected(config.invert);
      chkInvert.setEnabled(!config.scale_input);

      //chkGenReset.setSelected(config.generate_initial);
      //chkGenReset.setVisible(!config.scale_input);

      chkHighRes.setSelected(config.high_resolution);
      chkHighRes.setEnabled(!config.scale_input);

      if (config.mute) { // disable everything
        ChkData1.setEnabled(false);
        chkScaleInput.setEnabled(false);
        chkInvert.setEnabled(false);
        chkScaleInput.setEnabled(false);
        chkHighRes.setEnabled(false);
        cmbMessageType.setEnabled(false);
        cmbChannel.setEnabled(false);
        cmbData1.setEnabled(false);
        cmbData2.setEnabled(false);
        cmbData3.setEnabled(false);
        cmbData4.setEnabled(false);
      }
      else {
        // set everything enabled to start with
        ChkData1.setEnabled(true);
        chkScaleInput.setEnabled(true);
        chkInvert.setEnabled(true);
        chkScaleInput.setEnabled(true);
        chkHighRes.setEnabled(true);
        cmbMessageType.setEnabled(true);
        cmbChannel.setEnabled(true);
        cmbData1.setEnabled(true);
        cmbData2.setEnabled(true);
        cmbData3.setEnabled( config.scale_input);
        cmbData4.setEnabled(_device_num >= 8 && analog_enabled);
      }
      
      if (_device_num >= 8 && analog_enabled)
      {
        cmbData3.setVisible(false);
        cmbData4.setVisible(true);
      }
      else
      {
        cmbData3.setVisible(true);
        cmbData4.setVisible(false);
      }
      reading = false;
      //group.(rdData1, config.data1_vary);
    } // initiaised
  }

  public void WriteConfig(){
    if (initialised && ! reading)
    {
      MidiConfig config = new MidiConfig();
      config.midi_message = cmbMessageType.getSelectedIndex();
      config.midi_channel = cmbChannel.getSelectedIndex();
      config.data_1 = cmbData1.getSelectedIndex();
      config.data_2 = cmbData2.getSelectedIndex();
      config.lower_midi = cmbData3.getSelectedIndex();

      config.midi_out2 = false;
      config.mute = chkMute.isSelected();
      config.data1_vary = ChkData1.isSelected();
      config.invert = chkInvert.isSelected();
      config.generate_initial = false;
      config.high_resolution = chkHighRes.isSelected();
      config.scale_input = chkScaleInput.isSelected();

      Midi.WriteConfigByte(ScalingConfigNumber(), (byte)cmbData4.getSelectedIndex());

      config.SetMidiConfig(_config_num);
      //MainFrame.Reload();
    }
  }
  private void LoadCombos (){
      //load the combo boxes
      if (_device_num < 8)
      {
        String PortName = new String ("C.V Input") + String.valueOf(_device_num + 1);
        cmbDevice.addItem (PortName);
        //lblDevice.setVisible(true);
        //lblDevice.setText(PortName);
        lblDevice.setVisible(false);
      }
      else
      {
        String PortName = new String ("Dig. Input") + String.valueOf(_device_num -7);
        cmbDevice.addItem (PortName);
        PortName = new String ("PWM Out") + String.valueOf(_device_num -7);
        cmbDevice.addItem (PortName);
        lblDevice.setVisible(false);
      }

      for(int i = 1; i <= 16; i++)
          cmbChannel.addItem(String.valueOf(i));//ad midi channels


      //load values in the channel box
      for (int i = 0; i <= 127; i++)
      {
      cmbData1.addItem(String.valueOf(i));
      cmbData2.addItem(String.valueOf(i));
      cmbData3.addItem(String.valueOf(i));
      }

      for (int i = 0; i < 255; i++)
      {
        cmbData4.addItem(String.valueOf(i));
      }
      cmbData4.addItem("Max");

      cmbMessageType.addItem(new String("Note Off"));
      cmbMessageType.addItem(new String("Note On"));
      cmbMessageType.addItem(new String("Polyphonic Pressure"));
      cmbMessageType.addItem(new String("Control Change"));
      cmbMessageType.addItem(new String("Program Change"));
      cmbMessageType.addItem(new String("Channel Pressure"));
      cmbMessageType.addItem(new String("Pitch Bend"));


      cmbChannel.setSelectedIndex(0);

  }
  private void jbInit() throws Exception  {
    contentPane = this;//(JPanel) this.getContentPane();
    contentPane.setLayout(new FlowLayout( FlowLayout.LEFT, 2, 2 ) );
    cmbDevice.addActionListener(new MidiDevice_cmbDevice_actionAdapter(this));
    chkMute.setText("M");
    chkMute.addActionListener(new MidiDevice_chkMute_actionAdapter(this));
    cmbMessageType.addActionListener(new MidiDevice_cmbMessageType_actionAdapter(this));
    cmbChannel.addActionListener(new MidiDevice_cmbChannel_actionAdapter(this));
    chkInvert.setText("I");
    chkInvert.addActionListener(new MidiDevice_chkInvert_actionAdapter(this));
    
    //contentPane.setMinimumSize(new Dimension(1, 1));
    //contentPane.setPreferredSize(new Dimension(300, 220));
    cmbData1.addActionListener(new MidiDevice_cmbData1_actionAdapter(this));
    cmbData2.addActionListener(new MidiDevice_cmbData2_actionAdapter(this));
    chkHighRes.setText("2M");
    chkHighRes.addActionListener(new MidiDevice_chkHighRes_actionAdapter(this));
    cmbData3.addActionListener(new MidiDevice_cmbData3_actionAdapter(this));
    chkScaleInput.setToolTipText("");
    chkScaleInput.setText("S");
    chkScaleInput.addActionListener(new MidiDevice_chkScaleInput_actionAdapter(this));
    lblDevice.setText("jLabel1");
    lblDevice.setEditable(false);
    ChkData1.setText("D1");
    ChkData1.addActionListener(new MidiDevice_ChkData1_actionAdapter(this));

    cmbData4.addActionListener(new MidiDevice_cmbData4_actionAdapter(this));
    contentPane.add(cmbDevice, null);
    contentPane.add(lblDevice, null);
    contentPane.add(cmbMessageType, null);
    
    contentPane.add(cmbChannel, null);
    contentPane.add(cmbData1, null);
    contentPane.add(cmbData2, null);
    
    contentPane.add(cmbData3, null);
    contentPane.add(cmbData4, null);
    
    contentPane.add(chkMute, null);
    contentPane.add(chkInvert, null);
    contentPane.add(ChkData1, null);
    
    
    contentPane.add(chkHighRes, null);
    contentPane.add(chkScaleInput, null);
    
  }

  void cmbDevice_actionPerformed(ActionEvent e) {
    if (initialised && ! reading)
    {
      int ana_device_num;

      if (_device_num >= 8 && _device_num < 16) {
        ana_device_num = _device_num - 8;
        boolean is_analogue = cmbDevice.getSelectedIndex() > 0;
        //System.out.println("Config " + ana_device_num);
        Midi.SetAnaOutEnabled(ana_device_num, is_analogue);
      }
      ReadConfig();
    }
  }

  void cmbMessageType_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void chkMidiOut2_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void chkMute_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void cmbChannel_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void cmbData1_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void cmbData2_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void rdData1_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void rdData2_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void chkInvert_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void chkGenReset_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void chkHighRes_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void cmbData3_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void chkScaleInput_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void ChkData1_actionPerformed(ActionEvent e) {
    WriteConfig();
  }

  void cmbData4_actionPerformed(ActionEvent e) {
    WriteConfig();
  }


}

class MidiDevice_cmbDevice_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_cmbDevice_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbDevice_actionPerformed(e);
  }
}

class MidiDevice_cmbMessageType_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_cmbMessageType_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbMessageType_actionPerformed(e);
  }
}

class MidiDevice_chkMute_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_chkMute_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.chkMute_actionPerformed(e);
  }
}

class MidiDevice_cmbChannel_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_cmbChannel_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbChannel_actionPerformed(e);
  }
}

class MidiDevice_cmbData1_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_cmbData1_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbData1_actionPerformed(e);
  }
}

class MidiDevice_cmbData2_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_cmbData2_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbData2_actionPerformed(e);
  }
}

class MidiDevice_chkInvert_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_chkInvert_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.chkInvert_actionPerformed(e);
  }
}

class MidiDevice_chkHighRes_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_chkHighRes_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.chkHighRes_actionPerformed(e);
  }
}

class MidiDevice_cmbData3_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_cmbData3_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbData3_actionPerformed(e);
  }
}

class MidiDevice_chkScaleInput_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_chkScaleInput_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.chkScaleInput_actionPerformed(e);
  }
}

class MidiDevice_ChkData1_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_ChkData1_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ChkData1_actionPerformed(e);
  }
}

class MidiDevice_cmbData4_actionAdapter implements java.awt.event.ActionListener {
  MidiDevice adaptee;

  MidiDevice_cmbData4_actionAdapter(MidiDevice adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbData4_actionPerformed(e);
  }
}
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.event.*;
import java.net.*;


class ConfigReader extends Thread {

    protected DatagramSocket socket = null;
    protected int _port = 1113;
    volatile boolean _quit = false;

    public ConfigReader(int port) throws IOException {
        super("");
        _port = port;
        socket = new DatagramSocket(_port);
    }

    public ConfigReader() throws IOException {
        super("");
        socket = new DatagramSocket(_port);
    }

    void DisplaySendLine(String chartype)
    {
      for (int i = 0; i < 40; i++)
      {
        System.out.print(chartype);
      }
      System.out.println(chartype);
    }

    void WriteDisplayText(String text)
    {
      DisplaySendLine("*");
      System.out.println (text);
      DisplaySendLine("*");
    }

    public void run() {

        while (!_quit) {
            try {
                byte[] buf = new byte[256];


                // receive request
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                InetAddress address = packet.getAddress();
                //System.out.println("Received Device Response from " + address.getHostAddress());
                int received_size = packet.getLength();
                byte[] response_buf = new byte [received_size];

                for (int i = 0; i < received_size; i++)
                {
                  response_buf[i] = buf[i];
                  Midi.ProcessConfigByte(buf[i]);
                }
                String response = new String(response_buf);
                System.out.println(response);

                // send the response to the client at "address" and "port"
                //
                //int port = packet.getPort();
                //packet = new DatagramPacket(buf, buf.length, address, port);
                //socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
                //_quit = true;
            }
        }
        socket.close();
    }

}

/**
 * <p>Title: Mini Midi Controller</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: Fraietta Discretionary Trust</p>
 * @author Angelo Fraietta
 * @version 1.0
 */

public class MainFraim extends JFrame {
  JPanel contentPane;
  JMenuBar jMenuBar1 = new JMenuBar();
  JMenu jMenuFile = new JMenu();
  JMenuItem jMenuFileExit = new JMenuItem();
  JMenu jMenuHelp = new JMenu();
  JMenuItem jMenuHelpAbout = new JMenuItem();
  ImageIcon image1;
  ImageIcon image2;
  ImageIcon image3;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel jPanel1 = new JPanel();
  JComboBox cmbMidiIn = new JComboBox();
  JComboBox cmbMidiOut = new JComboBox();
  JPanel ConfigContainer = new JPanel();
  //JPanel ConfigPanel = new JPanel();
  GridLayout gridLayout1 = new GridLayout();
  JPanel jPanel2 = new JPanel();
  JButton ReadButton = new JButton();
  Vector config_list = new Vector();
  JButton WriteConfigButton = new JButton();
  JProgressBar SendProgress = new JProgressBar();
  JButton cmdCancel = new JButton();
  JButton cmdFactory = new JButton();
  JPanel jPanel3 = new JPanel();
  JLabel jLabel11 = new JLabel();
  JButton cmdSendMidi = new JButton();
  JComboBox cmbChannel = new JComboBox();
  JComboBox cmbData1 = new JComboBox();
  JLabel jLabel12 = new JLabel();
  JComboBox cmbMessageType = new JComboBox();
  JLabel jLabel13 = new JLabel();
  JComboBox cmbData2 = new JComboBox();
  JLabel jLabel14 = new JLabel();
  SysexSender updater = new SysexSender(this, 10);
  final int ANA_RES = 3;
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JMenuItem mnuLoadConfig = new JMenuItem();
  JMenuItem mnuSaveConfig = new JMenuItem();
  JCheckBox chkMidiMerge = new JCheckBox();
  JComboBox cmbAnares = new JComboBox();
  JLabel lblPWMResolution = new JLabel();
  JLabel lblIoPort = new JLabel();
  JLabel lblMidiMessage = new JLabel();
  JLabel lblChan = new JLabel();
  JLabel lblData1 = new JLabel();
  JLabel lblData2 = new JLabel();
  JLabel lblHiThresh = new JLabel();
  JLabel lblLowThresh = new JLabel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea ReceivedMessages = new JTextArea();
  JButton btnClearInput = new JButton();
  JLabel lblChanBottom = new JLabel();
  JComboBox cmbCurrentDevice = new JComboBox();
  JComboBox cmbSendDeviceId = new JComboBox();
  JMenu mnuWireless = new JMenu();
  JMenuItem mnuSetWireless = new JMenuItem();
  ConfigReader config_reader = null;

  static boolean udp_tested = false;

  //Construct the frame
  public MainFraim() {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try {
      jbInit();
      Midi.Initialise();
      int form_width = 900;
      Point current_position = new Point(jPanel2.getLocation());
      current_position.y += jPanel2.getHeight();
      JPanel config_panel;
      for (int i = 0; i < 16; i++)
      {
        config_panel = new MidiDevice(i);
        ConfigContainer.add(config_panel, null);
        config_panel.setBounds(new Rectangle(current_position.x, current_position.y, form_width, 30));
        //config_panel.setLocation(current_position);
        current_position.y += config_panel.getHeight();
        config_panel.setVisible(true);
        config_list.add(config_panel);
      }
              
      ConfigContainer.setSize(form_width,  current_position.y);
      jScrollPane1.setBounds(new Rectangle(2, ConfigContainer.getHeight() + 150, form_width, 151));
      this.setSize(form_width, ConfigContainer.getHeight() + 200+ jScrollPane1.getHeight()) ;

      System.out.println("Input Devices");
      int num_dev = Midi.NumInputDevice();
      String dev_name;
      for (int i = 0; i < num_dev; i++)
      {
        dev_name = Midi.GetInputDeviceName(i);
        System.out.println(dev_name);
        cmbMidiIn.addItem(dev_name);
      }

      System.out.println("Output Devices");
      num_dev = Midi.NumOutputDevice();
      for (int i = 0; i < num_dev; i++)
      {
        dev_name = Midi.GetOutputDeviceName(i);
        System.out.println(dev_name);
        cmbMidiOut.addItem(dev_name);
      }
      LoadMidiMessages();

      for (int i = 1; i <= 8; i++)
      {
        cmbAnares.addItem(String.valueOf(i));
      }

      for (int i = 0; i < 255; i++)
      {
        cmbCurrentDevice.addItem(String.valueOf(i+1));
        cmbSendDeviceId.addItem(String.valueOf(i+1));

      }
      cmbCurrentDevice.addItem("All");
      cmbSendDeviceId.addItem("All");

      int device_index = Midi.GetDeviceNumber();
      cmbCurrentDevice.setSelectedIndex(device_index);


      //OpenMidiDevices();
      Midi.WriteConfigByte(ANA_RES, (byte)7); // set PWM Resolution to 7

      LoadData();
      SetSendStatus(false);
      updater.Start();

    }
    catch(Exception e) {
      e.printStackTrace();
    }
    resizeDisplay();
  }

  void resizeDisplay()
  {
    MidiDevice first_panel = (MidiDevice)config_list.get(0);
    lblMidiMessage.setLocation(first_panel.getMessageTypeLeft(), lblMidiMessage.getY());
    lblChan.setLocation(first_panel.getChannelLeft(), lblChan.getY());
    lblChanBottom.setLocation(first_panel.getChannelLeft(), lblChanBottom.getY());
    lblData1.setLocation(first_panel.getData1Left(), lblData1.getY());
    lblData2.setLocation(first_panel.getData2Left(), lblData2.getY());
    lblHiThresh.setLocation(first_panel.getData2Left(), lblHiThresh.getY());
    lblLowThresh.setLocation(first_panel.getData3Left(), lblLowThresh.getY());
    lblPWMResolution.setLocation(first_panel.getMuteLeft(), lblPWMResolution.getY());
    cmbAnares.setLocation(first_panel.getMuteLeft(), cmbAnares.getY());
    chkMidiMerge.setLocation(first_panel.getMuteLeft() +  lblPWMResolution.getWidth(), chkMidiMerge.getY());
    jPanel2.setPreferredSize(new Dimension(first_panel.getWidth(), jPanel2.getHeight()));
    jPanel2.setBounds(new Rectangle(0, 2, first_panel.getWidth(), 57));
    
    chkMidiMerge.setSize(150, chkMidiMerge.getY());
  }
  
  void ReadMidi(){
    MidiMessage message = new MidiMessage();
    while (message.GetMidiMessage())
    {

      ReceivedMessages.append(GetMessageType(message.midi_message));
      ReceivedMessages.append (" " + String.valueOf(message.midi_channel + 1));
      ReceivedMessages.append(" " + String.valueOf(message.data_1));

      if (message.midi_message != 4 && message.midi_message != 5
          && message.midi_message != -1 && message.midi_message != -2)
      {
        ReceivedMessages.append(" " + String.valueOf(message.data_2));
      }
      ReceivedMessages.append("\n");

    }
  }

  private String GetMessageType (int index){
  switch (index)
  {
    case 0:
      return new String("Note Off");
    case 1:
      return new String("Note On");
    case 2:
      return new String("Polyphonic Pressure");
    case 3:
      return new String("Control Change");
    case 4:
      return new String("Program Change");
    case 5:
      return new String("Channel Pressure");
    case 6:
      return new String("Pitch Bend");
    case -1:
      return new String("Wireless Analogue");
    case -2:
      return new String("Wireless Digital");
  }

  return new String("Unknown");

}

  private void OpenConfigFile(){
      System.out.println("OpenFromFile");
      //  Set Smart Controller Patches fileter
      JFileChooser OpenDialog = new JFileChooser ();
      //OpenDialog.setFileFilter(new PatchFileFilter());

      int ret = OpenDialog.showOpenDialog(MainFraim.this);
      if (ret == JFileChooser.APPROVE_OPTION)
      {
        File file = OpenDialog.getSelectedFile();
        String filename = file.getName();
        String filepath = OpenDialog.getCurrentDirectory().getAbsolutePath() + file.separatorChar;

        System.out.println("Path: " + filepath + filename );
        Midi.LoadConfig(filepath + filename);
        LoadData();
      }
    }


      private void SaveConfigFile(){
        System.out.println("Save");
        //  Set Smart Controller Patches fileter
        JFileChooser OpenDialog = new JFileChooser ();
        //OpenDialog.setFileFilter(new PatchFileFilter());

        int ret = OpenDialog.showSaveDialog(MainFraim.this);
        if (ret == JFileChooser.APPROVE_OPTION)
        {
          File file = OpenDialog.getSelectedFile();
          String filename = file.getName();
          String filepath = OpenDialog.getCurrentDirectory().getAbsolutePath() + file.separatorChar;

          System.out.println("Path: " + filepath + filename );
          Midi.SaveConfig(filepath + filename);
        }
      }


  private void LoadMidiMessages(){
  cmbMessageType.addItem(new String("Note Off"));
  cmbMessageType.addItem(new String("Note On"));
  cmbMessageType.addItem(new String("Polyphonic Pressure"));
  cmbMessageType.addItem(new String("Control Change"));
  cmbMessageType.addItem(new String("Program Change"));
  cmbMessageType.addItem(new String("Channel Pressure"));
  cmbMessageType.addItem(new String("Pitch Bend"));

  for (int i = 1; i <=16; i++)
  {
    cmbChannel.addItem(String.valueOf(i));
  }

  for (int i = 0; i <= 127; i++)
  {
    cmbData1.addItem(String.valueOf(i));
    cmbData2.addItem(String.valueOf(i));
  }

  // Now set the Message to Middle C
  cmbMessageType.setSelectedIndex(1); // Note on
  cmbChannel.setSelectedIndex(0);
  cmbData1.setSelectedIndex(60);
  cmbData2.setSelectedIndex(127);

}

public void ReadBytesTx(){
  int bytes_read = Midi.GetBytesRead();

  if (bytes_read >= SendProgress.getMaximum())
  {
    //updater.Stop();
    SetSendStatus(false);
  }
  else
  {
    SendProgress.setValue(bytes_read);
  }

  ReadMidi();
  if (Midi.ConfigChanged()){
    LoadData();
  }
}
  //Component initialization
  private void jbInit() throws Exception  {
    image1 = new ImageIcon(MainFraim.class.getResource("openFile.png"));
    image2 = new ImageIcon(MainFraim.class.getResource("closeFile.png"));
    image3 = new ImageIcon(MainFraim.class.getResource("help.png"));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setResizable(true);
    this.setSize(new Dimension(1400, 486));
    this.setTitle("Mini Midi Controller");
    jMenuFile.setText("File");
    jMenuFileExit.setText("Exit");
    jMenuFileExit.addActionListener(new MainFraim_jMenuFileExit_ActionAdapter(this));
    jMenuHelp.setText("Help");
    jMenuHelpAbout.setText("About");
    jMenuHelpAbout.addActionListener(new MainFraim_jMenuHelpAbout_ActionAdapter(this));
    jPanel1.setMinimumSize(new Dimension(50, 50));
    jPanel1.setPreferredSize(new Dimension(50, 50));
    jPanel1.setLayout(null);
    cmbMidiIn.setBounds(new Rectangle(5, 38, 139, 21));
    cmbMidiIn.addActionListener(new MainFraim_cmbMidiIn_actionAdapter(this));
    cmbMidiOut.setBounds(new Rectangle(124, 32, 85, 20));
    cmbMidiOut.setBounds(new Rectangle(151, 37, 42, 21));
    cmbMidiOut.addActionListener(new MainFraim_cmbMidiOut_actionAdapter(this));
    ConfigContainer.setBorder(BorderFactory.createLoweredBevelBorder());
    ConfigContainer.setPreferredSize(new Dimension(900, 900));
    ConfigContainer.setBounds(new Rectangle(5, 116, 740, 361));
    ConfigContainer.setLayout(null);

    jPanel2.setBorder(BorderFactory.createRaisedBevelBorder());
    jPanel2.setBounds(new Rectangle(0, 2, 736, 57));
    jPanel2.setLayout(null);
    ReadButton.setBounds(new Rectangle(298, 42, 115, 20));
    ReadButton.setText("Read Config");
    ReadButton.addActionListener(new MainFraim_ReadButton_actionAdapter(this));
    WriteConfigButton.setBounds(new Rectangle(413, 20, 111, 20));
    WriteConfigButton.setText("Write Config");
    WriteConfigButton.addActionListener(new MainFraim_WriteConfigButton_actionAdapter(this));
    SendProgress.setMaximum(256);
    SendProgress.setBounds(new Rectangle(413, 63, 111, 10));
    cmdCancel.setBounds(new Rectangle(413, 42, 111, 20));
    cmdCancel.setText("Cancel");
    cmdCancel.addActionListener(new MainFraim_cmdCancel_actionAdapter(this));
    cmdFactory.setBounds(new Rectangle(298, 20, 115, 20));
    cmdFactory.setText("Factory Default");
    cmdFactory.addActionListener(new MainFraim_cmdFactory_actionAdapter(this));
    jPanel3.setBorder(BorderFactory.createLoweredBevelBorder());
    jPanel3.setMaximumSize(new Dimension(32767, 32767));
    jPanel3.setBounds(new Rectangle(6, 75, 700, 40));
    jPanel3.setLayout(null);
    jLabel11.setBounds(new Rectangle(128, 1, 61, 16));
    jLabel11.setText("Midi Chan");
    cmdSendMidi.setText("Send Midi");
    cmbChannel.setBounds(new Rectangle(124, 19, 67, 19));
    jLabel12.setText("Data 2");
    jLabel13.setBounds(new Rectangle(2, 1, 118, 16));
    jLabel13.setText("Message Type");
    jLabel14.setText("Data 1");
    cmdSendMidi.setBounds(new Rectangle(350, 14, 133, 21));
    cmdSendMidi.setText("Send Midi");
    cmdSendMidi.addActionListener(new MainFraim_cmdSendMidi_actionAdapter(this));
    cmbChannel.setBounds(new Rectangle(125, 18, 67, 19));
    cmbData1.setBounds(new Rectangle(192, 18, 78, 19));
    jLabel12.setText("Data 2");
    jLabel12.setBounds(new Rectangle(268, 0, 41, 16));
    cmbMessageType.setBounds(new Rectangle(0, 18, 122, 19));
    jLabel13.setText("Message Type");
    jLabel13.setBounds(new Rectangle(3, 0, 118, 16));
    cmbData2.setBounds(new Rectangle(270, 18, 78, 19));
    jLabel14.setText("Data 1");
    jLabel14.setBounds(new Rectangle(196, 0, 41, 16));
    jLabel11.setText("Midi Chan");
    jLabel11.setBounds(new Rectangle(129, 0, 61, 16));
    jLabel1.setText("Input Device");
    jLabel1.setBounds(new Rectangle(6, 16, 133, 19));
    jLabel2.setRequestFocusEnabled(true);
    jLabel2.setToolTipText("");
    jLabel2.setText("Output Device");
    jLabel2.setBounds(new Rectangle(153, 18, 129, 16));
    mnuLoadConfig.setText("Load Config");
    mnuLoadConfig.addActionListener(new MainFraim_mnuLoadConfig_actionAdapter(this));
    mnuSaveConfig.setText("Save Config");
    mnuSaveConfig.addActionListener(new MainFraim_mnuSaveConfig_actionAdapter(this));
    chkMidiMerge.setText("Midi Merge");
    chkMidiMerge.setBounds(new Rectangle(565, 19, 100, 23));

    chkMidiMerge.addActionListener(new MainFraim_chkMidiMerge_actionAdapter(this));
    cmbAnares.setBounds(new Rectangle(481, 21, 74, 18));
    cmbAnares.addActionListener(new MainFraim_cmbAnares_actionAdapter(this));

    lblPWMResolution.setText("PWM Resolution");
    lblPWMResolution.setBounds(new Rectangle(481, 7, 116, 11));
    lblIoPort.setRequestFocusEnabled(true);
    lblIoPort.setText("IO Port");
    lblIoPort.setBounds(new Rectangle(25, 22, 54, 17));
    lblMidiMessage.setText("Message Type");
    lblMidiMessage.setBounds(new Rectangle(115, 22, 93, 16));
    lblChan.setText("MIDI");
    lblChan.setBounds(new Rectangle(222, 10, 63, 14));
    lblData1.setText("Data 1");
    lblData1.setBounds(new Rectangle(293, 24, 43, 13));
    lblData2.setText("Data2 /");
    lblData2.setBounds(new Rectangle(346, 8, 50, 18));
    lblHiThresh.setText("Hi Thresh");
    lblHiThresh.setBounds(new Rectangle(345, 24, 65, 13));
    lblLowThresh.setToolTipText("");
    lblLowThresh.setText("Lo Thresh");
    lblLowThresh.setBounds(new Rectangle(410, 22, 70, 17));
    btnClearInput.setBounds(new Rectangle(490, 15, 130, 20));
    btnClearInput.setText("Clear Messages");
    btnClearInput.addActionListener(new MainFraim_btnClearInput_actionAdapter(this));
    lblChanBottom.setText("Chan");
    lblChanBottom.setBounds(new Rectangle(219, 24, 63, 12));
    cmbCurrentDevice.setBounds(new Rectangle(550, 42, 77, 20));
    cmbCurrentDevice.addActionListener(new MainFraim_cmbCurrentDevice_actionAdapter(this));
    cmbSendDeviceId.setBounds(new Rectangle(632, 43, 77, 19));
    cmbSendDeviceId.addActionListener(new MainFraim_cmbSendDeviceId_actionAdapter(this));
    mnuWireless.setText("Wireless");
    mnuWireless.addActionListener(new MainFraim_mnuWireless_actionAdapter(this));
    mnuSetWireless.setText("Using Wireless");
    mnuSetWireless.addActionListener(new MainFraim_mnuSetWireless_actionAdapter(this));
    jMenuFile.add(mnuLoadConfig);
    jMenuFile.add(mnuSaveConfig);
    jMenuFile.add(jMenuFileExit);
    jMenuHelp.add(jMenuHelpAbout);
    jMenuBar1.add(jMenuFile);
    jMenuBar1.add(jMenuHelp);
    jMenuBar1.add(mnuWireless);
    this.setJMenuBar(jMenuBar1);
    contentPane.add(jPanel1,  BorderLayout.CENTER);
    jPanel1.add(ConfigContainer, null);


    jPanel1.add(jScrollPane1, null);
    jScrollPane1.setBounds(new Rectangle(2, 3, 461, 121));
    jScrollPane1.getViewport().add(ReceivedMessages, null);

    ConfigContainer.add(jPanel2, null);
    jPanel1.add(cmbMidiOut, null);
    jPanel1.add(jPanel3, null);
    jPanel3.add(jLabel13, null);
    jPanel3.add(jLabel11, null);
    jPanel3.add(cmbChannel, null);
    jPanel3.add(cmbData1, null);
    jPanel3.add(jLabel12, null);
    jPanel3.add(cmbMessageType, null);
    jPanel3.add(cmbData2, null);
    jPanel3.add(jLabel14, null);
    jPanel3.add(cmdSendMidi, null);
    jPanel3.add(btnClearInput, null);
    jPanel1.add(cmbMidiIn, null);
    jPanel1.add(cmbMidiOut, null);
    jPanel1.add(jLabel1, null);
    jPanel1.add(jLabel2, null);
    jPanel1.add(cmdFactory, null);
    jPanel1.add(ReadButton, null);
    jPanel1.add(SendProgress, null);
    jPanel1.add(WriteConfigButton, null);
    jPanel1.add(cmdCancel, null);
    jPanel1.add(cmbCurrentDevice, null);
    jPanel1.add(cmbSendDeviceId, null);
    jPanel2.add(lblIoPort, null);
    jPanel2.add(chkMidiMerge, null);
    jPanel2.add(cmbAnares, null);
    jPanel2.add(lblPWMResolution, null);
    jPanel2.add(lblHiThresh, null);
    jPanel2.add(lblData2, null);
    jPanel2.add(lblData1, null);
    jPanel2.add(lblChan, null);
    jPanel2.add(lblChanBottom, null);
    jPanel2.add(lblLowThresh, null);
    jPanel2.add(lblMidiMessage, null);
    mnuWireless.add(mnuSetWireless);
    //ConfigContainer.add(ConfigPanel, null);
  }

  private void SetSendStatus(boolean isSending)
  {
    WriteConfigButton.setEnabled(!isSending);
    ReadButton.setEnabled(!isSending);
    cmdFactory.setEnabled(!isSending);
    cmdSendMidi.setEnabled(!isSending);
    cmdCancel.setEnabled(isSending);
    SendProgress.setVisible(isSending);
  }

  public void LoadData(){
  chkMidiMerge.setSelected(Midi.GetMidiMerge());

  int read_ana_res = Midi.ReadConfigByte(ANA_RES);

  if (read_ana_res > 8)
  {
    read_ana_res = 7;
  }
  else if (read_ana_res <= 0)
  {
    read_ana_res = 7;
  }

  read_ana_res -= 1;

  cmbAnares.setSelectedIndex(read_ana_res);

  for (int i = 0; i < config_list.size(); i++)
  {
    MidiDevice config  = (MidiDevice) config_list.get(i);
    config.ReadConfig();
  }

}

  //File | Exit action performed
  public void jMenuFileExit_actionPerformed(ActionEvent e) {
    System.exit(0);
  }
  //Help | About action performed
  public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
    MainFraim_AboutBox dlg = new MainFraim_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.pack();
    dlg.show();
  }
  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      jMenuFileExit_actionPerformed(null);
    }
  }

  void cmbMidiIn_actionPerformed(ActionEvent e) {
    Midi.SetInputDevice(cmbMidiIn.getSelectedIndex());
  }

  void cmbMidiOut_actionPerformed(ActionEvent e) {
    Midi.SetOutputDevice(cmbMidiOut.getSelectedIndex());
  }

  void ReadButton_actionPerformed(ActionEvent e) {
    Midi.SendReadConfig();
    LoadData();
  }

  void WriteConfigButton_actionPerformed(ActionEvent e) {
    Midi.SendConfig();
    //updater.Start();
    SendProgress.setVisible(true);
    SetSendStatus(true);

  }

  void cmdFactory_actionPerformed(ActionEvent e) {
    Midi.SendFactoryDefault();
  }

  void cmdSendMidi_actionPerformed(ActionEvent e) {
    MidiMessage message = new MidiMessage();

    message.midi_message= cmbMessageType.getSelectedIndex();
    message.midi_channel = cmbChannel.getSelectedIndex();
    message.data_1 = cmbData1.getSelectedIndex();
    message.data_2 =  cmbData2.getSelectedIndex();

    message.SetMidiMessage();

  }

  void cmdCancel_actionPerformed(ActionEvent e) {
    Midi.CancelWrite();
    //updater.Stop();
    SetSendStatus(false);

  }

  void cmbAnares_actionPerformed(ActionEvent e) {
    byte value = (byte)(cmbAnares.getSelectedIndex() + 1);
    Midi.WriteConfigByte(ANA_RES, value);
  }

  void mnuLoadConfig_actionPerformed(ActionEvent e) {
    OpenConfigFile();
  }

  void mnuSaveConfig_actionPerformed(ActionEvent e) {
    SaveConfigFile();
  }

  void chkMidiMerge_actionPerformed(ActionEvent e) {
    Midi.SetMidiMerge(chkMidiMerge.isSelected());
  }

  void btnClearInput_actionPerformed(ActionEvent e) {
    ReceivedMessages.setText("");
  }

  void cmbCurrentDevice_actionPerformed(ActionEvent e) {
    int value = cmbCurrentDevice.getSelectedIndex();
    Midi.SetDeviceNumber(value);
  }

  void cmbSendDeviceId_actionPerformed(ActionEvent e) {
    Midi.SendDeviceNumber(cmbSendDeviceId.getSelectedIndex());
  }

  void mnuSetWireless_actionPerformed(ActionEvent e) {
    try
    {
      if (!Midi.SetUDPDestination("localhost", 1113))
      {
        ReceivedMessages.append("Unable To Set UDP\n");
      }
      else
      {
        mnuSetWireless.setSelected(true);

        if (config_reader == null)
        {
          config_reader = new ConfigReader(8000);
          config_reader.start();
        }

        ReceivedMessages.append("Sending Config through UDP\n");
      }
    }
    catch(Exception err) {
      err.printStackTrace();
       ReceivedMessages.append ("Wireless Not Supported\n");
    }

  }

  void mnuWireless_actionPerformed(ActionEvent e) {
  }

}


class MainFraim_jMenuFileExit_ActionAdapter implements ActionListener {
  MainFraim adaptee;

  MainFraim_jMenuFileExit_ActionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuFileExit_actionPerformed(e);
  }
}

class MainFraim_jMenuHelpAbout_ActionAdapter implements ActionListener {
  MainFraim adaptee;

  MainFraim_jMenuHelpAbout_ActionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.jMenuHelpAbout_actionPerformed(e);
  }
}

class MainFraim_cmbMidiIn_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmbMidiIn_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbMidiIn_actionPerformed(e);
  }
}

class MainFraim_cmbMidiOut_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmbMidiOut_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbMidiOut_actionPerformed(e);
  }
}

class MainFraim_ReadButton_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_ReadButton_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.ReadButton_actionPerformed(e);
  }
}

class MainFraim_WriteConfigButton_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_WriteConfigButton_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.WriteConfigButton_actionPerformed(e);
  }
}

class MainFraim_cmdFactory_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmdFactory_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmdFactory_actionPerformed(e);
  }
}

class MainFraim_cmdSendMidi_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmdSendMidi_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmdSendMidi_actionPerformed(e);
  }
}

class MainFraim_cmdCancel_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmdCancel_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmdCancel_actionPerformed(e);
  }
}

class MainFraim_mnuLoadConfig_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_mnuLoadConfig_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.mnuLoadConfig_actionPerformed(e);
  }
}

class MainFraim_mnuSaveConfig_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_mnuSaveConfig_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.mnuSaveConfig_actionPerformed(e);
  }
}

class MainFraim_cmbAnares_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmbAnares_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbAnares_actionPerformed(e);
  }
}

class MainFraim_chkMidiMerge_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_chkMidiMerge_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.chkMidiMerge_actionPerformed(e);
  }
}

class MainFraim_btnClearInput_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_btnClearInput_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.btnClearInput_actionPerformed(e);
  }
}

class MainFraim_cmbCurrentDevice_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmbCurrentDevice_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbCurrentDevice_actionPerformed(e);
  }
}

class MainFraim_cmbSendDeviceId_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_cmbSendDeviceId_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.cmbSendDeviceId_actionPerformed(e);
  }
}

class MainFraim_mnuSetWireless_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_mnuSetWireless_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.mnuSetWireless_actionPerformed(e);
  }
}

class MainFraim_mnuWireless_actionAdapter implements java.awt.event.ActionListener {
  MainFraim adaptee;

  MainFraim_mnuWireless_actionAdapter(MainFraim adaptee) {
    this.adaptee = adaptee;
  }
  public void actionPerformed(ActionEvent e) {
    adaptee.mnuWireless_actionPerformed(e);
  }
}

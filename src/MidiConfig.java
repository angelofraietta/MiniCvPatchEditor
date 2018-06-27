// Source file: D:/develop/MidicontrollerPatchEditor/MidiPatchEditor/src/MidiConfig.java


public class MidiConfig extends MidiMessage implements Comparable{
    public boolean data1_vary = false;
    public boolean mute = false;
    public boolean invert = false;
    public boolean midi_out2 = false;
    public boolean generate_initial = false;
    public boolean high_resolution = false;
    public boolean scale_input = false;
    public int lower_midi = 0; // this is the Lower Midi Byte

    MidiConfig() {
    }
    /**
       @roseuid 404B664F01F4
     */
    public native void GetMidiConfig(int index);

    /**
       @roseuid 404CB7820251
     */
    public native void SetMidiConfig(int index);

    public int compareTo(Object o)
    {
      MidiConfig right = (MidiConfig)o;
      if (
        data1_vary == right.data1_vary &&
        mute == right.mute &&
        invert == right.invert &&
        midi_out2 == right.midi_out2 &&
        generate_initial == right.generate_initial &&
        high_resolution == right.high_resolution &&
        scale_input == right.scale_input &&
        midi_message == right.midi_message &&
        midi_channel == right.midi_channel &&
        data_1 == right.data_1 &&
        data_2 == right.data_2

      )
      return 0;
    else
      return 1;
    }
}

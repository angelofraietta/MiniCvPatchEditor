// Source file: D:/develop/MidicontrollerPatchEditor/MidiPatchEditor/src/MidiMessage.java
public class MidiMessage {
    public int midi_message;
    public int midi_channel;
    public int data_1;
    public int data_2;

    MidiMessage() {
    }
    /**
       @roseuid 404E0E2200EA
     */
    public native boolean GetMidiMessage();

    /**
       @roseuid 404E0E31002E
     */
    public native boolean SetMidiMessage();
}

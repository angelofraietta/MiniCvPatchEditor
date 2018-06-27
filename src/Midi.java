// Source file: D:/develop/MidicontrollerPatchEditor/MidiPatchEditor/src/Midi.java

public class Midi {

    static {
      System.loadLibrary("midicontrollerlib");
    }
    Midi() {
    }
    /**
       @roseuid 403ED3970177
     */
    public static native void Initialise();

    /**
       @roseuid 403ED3EE0138
     */
    public static native void DeInitialisePatchEditor();

    /**
       @roseuid 403ED476006D
     */
    public static native boolean ConfigChanged();

    /**
       @roseuid 403ED49A0157
     */
    public static native boolean SetReferenceData();

    /**
       @roseuid 403ED4B10148
     */
    public static native boolean LoadConfig(String filename);

    /**
       @roseuid 403ED4CE0251
     */
    public static native boolean SaveConfig(String filename);

    /**
       @roseuid 403ED586003E
     */
    public static native boolean SendReadConfig();

    /**
       @roseuid 403ED5A1032C
     */
    public static native void CancelWrite();

    /**
       @roseuid 403ED5B5001F
     */
    public static native boolean SetOutputDevice(int device);

    /**
       @roseuid 403ED5BD0251
     */
    public static native boolean SetInputDevice(int device);

    /**
       @roseuid 4043950302EE
     */
    public static native int NumInputDevice();

    /**
       @roseuid 4043951400AB
     */
    public static native int NumOutputDevice();

    /**
       @roseuid 4043951E0196
     */
    public static native String GetInputDeviceName(int index);

    /**
       @roseuid 40439545032C
     */
    public static native String GetOutputDeviceName(int index);

    /**
       @roseuid 404CC8A002CE
     */
    public static native void SetMidiMerge(boolean merge);

    /**
       @roseuid 404CC8C60128
     */
    public static native boolean GetMidiMerge();

    /**
       @roseuid 404CCE3402CE
     */
    public static native void SendConfig();

    /**
       @roseuid 404F6C490128
     */
    public static native int GetBytesRead();

    /**
       Returns the Config Byte at a particular Index
       @roseuid 42123969005D
     */
    public static native byte ReadConfigByte(int index);

    /**
       Writes the Config Byte at a particular Index
       @roseuid 42124A460186
     */
    public static native void WriteConfigByte(int index, byte value);

    /**
       @roseuid 403ED54601F4
     */
    public static native boolean SendFactoryDefault();

    /**
       Returns true If Analogue In is Enabled
       @roseuid 4212626F02FD
     */
    public static native boolean GetAnaOutEnabled(int ana_number);

    /**
       Sets If Analogue In is Enabled
       @roseuid 421262A8007D
     */
    public static native void SetAnaOutEnabled(int ana_number, boolean enable);

    /**
       Set the Current Midi Device Number
       @roseuid 425C33F501F4
     */
    public static native boolean SetDeviceNumber(int device_number);

    /**
       Get The Current Configuration Device Number
       @roseuid 425C342301B5
     */
    public static native int GetDeviceNumber();

    /**
       Send The device Number to the device
       @roseuid 425C345B03C8
     */
    public static native boolean SendDeviceNumber(int target_device_num);

    public static native boolean SetUDPDestination(String target_address, int udp_port);

    public static native void ProcessConfigByte(byte udp_config_byte);
}

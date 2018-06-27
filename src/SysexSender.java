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

public class SysexSender implements ActionListener {
    javax.swing.Timer timer;
    MainFraim _parent;

    /**
       @roseuid 3EB1ED750021
     */
    SysexSender(MainFraim parent, int time) {
    timer = new javax.swing.Timer(time, this);
    _parent = parent;
    }

    /**
       @roseuid 3EB1ED750024
     */
    public void actionPerformed(ActionEvent e) {
      //Advance the animation frame.
        _parent.ReadBytesTx();
    }

    /**
       @roseuid 3EB1ED750026
     */
    public synchronized void Start() {
    if (!timer.isRunning())
    {
      timer.start();
    }
    }

    /**
       @roseuid 3EB1ED750027
     */
    public synchronized void Stop() {
    if (timer.isRunning())
    {
      timer.stop();
    }

    }
}

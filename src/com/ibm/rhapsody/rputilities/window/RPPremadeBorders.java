package com.ibm.rhapsody.rputilities.window;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

public class RPPremadeBorders {

    static Border blackline = BorderFactory.createLineBorder(Color.black);
    static Border raisedetched = BorderFactory.createEtchedBorder(0);
    static Border loweredetched = BorderFactory.createEtchedBorder(1);
    static Border raisedbevel = BorderFactory.createRaisedBevelBorder();
    static Border loweredbevel = BorderFactory.createLoweredBevelBorder();
    static Border empty = BorderFactory.createEmptyBorder();

    public RPPremadeBorders() {
    }

    public static Border getFramedBorder() {
        Border compound = null;
        compound = BorderFactory.createCompoundBorder(raisedbevel, loweredbevel);
        return compound;
    }

    public static Border getTextFramBorder(int space, Color c) {
        Border spaced = BorderFactory.createEmptyBorder(space, space, space, space);
        Border lined = BorderFactory.createLineBorder(c);
        Border compound = BorderFactory.createCompoundBorder(lined, spaced);
        return compound;
    }

    public static Border getNiceTitledBorder(String name) {
        return BorderFactory.createTitledBorder(raisedetched, name);
    }


}

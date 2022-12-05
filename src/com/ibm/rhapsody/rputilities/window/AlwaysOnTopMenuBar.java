package com.ibm.rhapsody.rputilities.window;

import javax.swing.*;

public class AlwaysOnTopMenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;
    protected JMenu optionsMenu_;
    protected JMenuItem alwaysOnTop_;

    public AlwaysOnTopMenuBar(JFrame frm) {
        optionsMenu_ = new JMenu("Options");
        add(optionsMenu_);
        alwaysOnTop_ = new AlwaysOnTopMenuItem(frm, optionsMenu_);
        optionsMenu_.add(alwaysOnTop_);
    }

    public JMenu getOptionsMenu_() {
        return optionsMenu_;
    }

}

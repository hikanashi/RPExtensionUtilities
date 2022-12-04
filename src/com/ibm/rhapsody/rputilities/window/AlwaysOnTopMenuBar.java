package com.ibm.rhapsody.rputilities.window;

import javax.swing.*;

public class AlwaysOnTopMenuBar extends JMenuBar {
    private static final long serialVersionUID = 1L;
    JMenu optionsMenu;
    JMenuItem aot;

    public AlwaysOnTopMenuBar(JFrame frm) {
        optionsMenu = new JMenu("Options");
        add(optionsMenu);
        aot = new AlwaysOnTopMenuItem(frm, optionsMenu);
        optionsMenu.add(aot);
    }

    public JMenu getOptionsMenu() {
        return optionsMenu;
    }

}

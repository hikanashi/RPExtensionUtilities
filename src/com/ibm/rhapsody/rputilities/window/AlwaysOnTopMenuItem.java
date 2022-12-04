package com.ibm.rhapsody.rputilities.window;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

public class AlwaysOnTopMenuItem extends JCheckBoxMenuItem {
    private static final long serialVersionUID = 1L;
    JFrame theForm;
    JMenu theMenu;
    
    public AlwaysOnTopMenuItem(JFrame frm, JMenu mnu) {
        super("Always on Top");
        theForm = frm;
        theMenu = mnu;
        init();
    }

    private void init() {
        theForm.setAlwaysOnTop(true);
        setSelected(true);
        theMenu.add(this);
        addItemListener( new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == 2)
                    theForm.setAlwaysOnTop(false);
                else
                    theForm.setAlwaysOnTop(true);
            }
        });
    }
}

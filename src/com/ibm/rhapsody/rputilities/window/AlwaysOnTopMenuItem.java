package com.ibm.rhapsody.rputilities.window;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;

public class AlwaysOnTopMenuItem extends JCheckBoxMenuItem {
    private static final long serialVersionUID = 1L;
    protected JFrame theForm_;
    protected JMenu theMenu_;

    public AlwaysOnTopMenuItem(JFrame form, JMenu menu) {
        super("Always on Top");
        theForm_ = form;
        theMenu_ = menu;
        init();
    }

    private void init() {
        theForm_.setAlwaysOnTop(true);
        setSelected(true);
        theMenu_.add(this);
        addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED)
                    theForm_.setAlwaysOnTop(false);
                else
                    theForm_.setAlwaysOnTop(true);
            }
        });
    }
}

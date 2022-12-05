package com.ibm.rhapsody.rputilities.window;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class AlwaysOnTopQuestionDialog extends JDialog {
    private String dialogMessages_[];
    private String title_;
    public static final char OK_CANCEL = 0;
    public static final char OK_ONLY = 1;
    AlwaysOnTopQuestionResponder responder_;
    JFrame theForm_;
    JPanel thePanel_;
    private ArrayList<JLabel> labels_;

    public AlwaysOnTopQuestionDialog(String messages[], char type, String title, AlwaysOnTopQuestionResponder r) {
        this.title_ = "";
        theForm_ = new JFrame();
        thePanel_ = new JPanel();
        dialogMessages_ = messages;
        responder_ = r;
        this.title_ = title;
        createDialogMessageLabels();
        if(type == OK_CANCEL) {
            createOKCancel();            
        }
        initUI();
    }

    private void initUI() {
        theForm_.setSize(500, 150);
        theForm_.add(thePanel_);
        theForm_.setTitle(title_);
        theForm_.setDefaultCloseOperation(2);
        theForm_.setResizable(false);
        theForm_.setLocationRelativeTo(null);
        theForm_.addWindowListener(new WindowListener() {

            public void windowOpened(WindowEvent windowevent) {
            }

            public void windowIconified(WindowEvent windowevent) {
            }

            public void windowDeiconified(WindowEvent windowevent) {
            }

            public void windowDeactivated(WindowEvent windowevent) {
            }

            public void windowClosing(WindowEvent e) {
                performCancel();
            }

            public void windowClosed(WindowEvent windowevent) {
            }

            public void windowActivated(WindowEvent windowevent) {
            }
        });
        theForm_.setVisible(true);
        theForm_.setAlwaysOnTop(true);
    }

    private JButton getOKButton() {
        JButton button = new JButton("OK");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(responder_ != null) {
                    theForm_.setVisible(false);
                    responder_.respondToOK();
                }
            }
        });
        return button;
    }

    private JButton getCancelButton() {
        JButton but = new JButton("Cancel");
        but.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                performCancel();
            }
        });
        return but;
    }

    private void createDialogMessageLabels() {
        labels_ = new ArrayList<JLabel>();
        String as[];
        int j = (as = dialogMessages_).length;
        for(int i = 0; i < j; i++) {
            String s = as[i];
            if(s != null) {
                JLabel l = new JLabel(s, 0);
                labels_.add(l);
            }
        }

    }

    private void performCancel() {
        theForm_.setVisible(false);
        if(responder_ != null) {
            responder_.respondToCancel();
        }
    }

    private void createOKCancel() {
        JPanel pnlText = new JPanel();
        pnlText.setLayout(new GridLayout(labels_.size(), 1));

        for(JLabel lbl : labels_) {
            pnlText.add(lbl);
        }

        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new GridLayout(1, 2));
        pnlButtons.add(getOKButton());
        pnlButtons.add(getCancelButton());
        thePanel_.setLayout(new BorderLayout());
        thePanel_.add(pnlText, "Center");
        thePanel_.add(pnlButtons, "South");
    }
}

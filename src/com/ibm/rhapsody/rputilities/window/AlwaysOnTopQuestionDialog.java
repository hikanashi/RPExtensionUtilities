package com.ibm.rhapsody.rputilities.window;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class AlwaysOnTopQuestionDialog extends JDialog {
    private String dialogMessages[];
    private String title;
    public static final char OK_CANCEL = 0;
    public static final char OK_ONLY = 1;
    AlwaysOnTopQuestionResponder responder;
    JFrame theForm;
    JPanel thePanel;
    private ArrayList<JLabel> labels;

    public AlwaysOnTopQuestionDialog(String messages[], char typ, String title, AlwaysOnTopQuestionResponder r) {
        this.title = "";
        theForm = new JFrame();
        thePanel = new JPanel();
        dialogMessages = messages;
        responder = r;
        this.title = title;
        createDialogMessageLabels();
        if(typ == 0) {
            createOKCancel();            
        }
        initUI();
    }

    private void initUI() {
        theForm.setSize(500, 150);
        theForm.add(thePanel);
        theForm.setTitle(title);
        theForm.setDefaultCloseOperation(2);
        theForm.setResizable(false);
        theForm.setLocationRelativeTo(null);
        theForm.addWindowListener(new WindowListener() {

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
        theForm.setVisible(true);
        theForm.setAlwaysOnTop(true);
    }

    private JButton getOKButton() {
        JButton but = new JButton("OK");
        but.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0)
            {
                if(responder != null)
                {
                    theForm.setVisible(false);
                    responder.respondToOK();
                }
            }
        });
        return but;
    }

    private JButton getCancelButton() {
        JButton but = new JButton("Cancel");
        but.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0)
            {
                performCancel();
            }
        });
        return but;
    }

    private void createDialogMessageLabels() {
        labels = new ArrayList<JLabel>();
        String as[];
        int j = (as = dialogMessages).length;
        for(int i = 0; i < j; i++)
        {
            String s = as[i];
            if(s != null) {
                JLabel l = new JLabel(s, 0);
                labels.add(l);
            }
        }

    }

    private void performCancel() {
        theForm.setVisible(false);
        if(responder != null)
            responder.respondToCancel();
        System.out.println("Cancelled");
    }

    private void createOKCancel() {
        JPanel pnlText = new JPanel();
        pnlText.setLayout(new GridLayout(labels.size(), 1));

        for(JLabel lbl : labels) {
            pnlText.add(lbl);
        }

        JPanel pnlButtons = new JPanel();
        pnlButtons.setLayout(new GridLayout(1, 2));
        pnlButtons.add(getOKButton());
        pnlButtons.add(getCancelButton());
        thePanel.setLayout(new BorderLayout());
        thePanel.add(pnlText, "Center");
        thePanel.add(pnlButtons, "South");
    }
}

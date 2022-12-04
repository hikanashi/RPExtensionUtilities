package com.ibm.rhapsody.rputilities.rpcommand.importer;

import com.ibm.rhapsody.rputilities.window.AlwaysOnTopMenuBar;
import com.telelogic.rhapsody.core.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;

public class ImportGUI {
    protected IRPApplication rpApp;
    protected IRPProject rpy;
    protected JFrame frm;
    protected AlwaysOnTopMenuBar menuBar;
    private JTextField txtGUID;
    private JTextField txtNewGUID;
    private IRPModelElement modelElement;
    private JTextField txtGetGUID;

    public ImportGUI(IRPApplication app) {
        rpApp = app;
        rpy = rpApp.activeProject();
        buildGui();
    }

    private void buildGui()
    {
        frm = new JFrame("Import API Specification");
        frm.setSize(600, 200);
        menuBar = new AlwaysOnTopMenuBar(frm);
        frm.setJMenuBar(menuBar);
        buildMainUI();
        frm.setVisible(true);
    }

    private void buildMainUI() {
        frm.setLayout(new FlowLayout());
        // frm.setLayout(new BoxLayout(frm, BoxLayout.Y_AXIS));
        frm.add(getMainPanel());
        
        // FileSelector file = new FileSelector();
        // String doxygenPath = file.GetOpenDirectoryDialog();
        // if(doxygenPath == null) {
        //     return false;
        // }
    }

    private void findGUID()
    {
        String guid = txtGUID.getText();
        if(guid.isEmpty())
            return;
        modelElement = rpy.findElementByGUID(guid);
        if(modelElement != null)
            modelElement.highLightElement();
        else
            JOptionPane.showMessageDialog(frm, "No Model Element Found");
    }

    private void replaceGUID()
    {
        String guid = txtNewGUID.getText();
        if(guid.isEmpty())
        {
            JOptionPane.showMessageDialog(frm, "Please Enter a New GUID");
            return;
        }
        if(modelElement == null)
        {
            JOptionPane.showMessageDialog(frm, "No Model Element Found");
            return;
        }
        IRPModelElement existingElt = rpy.findElementByGUID(guid);
        if(existingElt != null)
        {
            JOptionPane.showMessageDialog(frm, (new StringBuilder("Sorry - that GUID is Already in Use By ")).append(existingElt.getUserDefinedMetaClass()).append(" ").append(existingElt.getName()).toString());
            return;
        }
        try
        {
            modelElement.setGUID(guid);
            JOptionPane.showMessageDialog(frm, "GUID Replaced");
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(frm, "Could Not Set GUID");
            e.printStackTrace();
        }
    }

    private JPanel getMainPanel()
    {
        JPanel pnl = new JPanel();
        pnl.setLayout(new GridLayout(3, 2));
        txtGUID = new JTextField();
        JButton but = new JButton("Find Element with GUID:");
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0)
            {
                findGUID();
            }
        });

        txtNewGUID = new JTextField();
        JButton butNew = new JButton("Replace with New GUID:");
        butNew.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0)
            {
                replaceGUID();
            }
        });
        txtGetGUID = new JTextField();
        JButton butGet = new JButton("Show GUID of Selected Element");
        butGet.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0)
            {
                IRPModelElement elt = rpApp.getSelectedElement();
                if(elt == null)
                    JOptionPane.showMessageDialog(frm, "No Model Element Selected");
                else
                    txtGetGUID.setText(elt.getGUID());
            }
        });
        pnl.add(but);
        pnl.add(txtGUID);
        pnl.add(butNew);
        pnl.add(txtNewGUID);
        pnl.add(butGet);
        pnl.add(txtGetGUID);
        return pnl;
    }

}
package com.ibm.rhapsody.rputilities.rpcommand.importer;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenObjectManager;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParser;
import com.ibm.rhapsody.rputilities.rpcommand.RPDoxygenXML;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.ibm.rhapsody.rputilities.window.AlwaysOnTopMenuBar;
import com.ibm.rhapsody.rputilities.window.FileSelector;
import com.telelogic.rhapsody.core.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

public class ImportGUI extends ARPObject {
    protected static final String PROPERTY_NAME_PATH = "defaultPath";
    protected static final String PROPERTY_NAME_VERSION = "defaultVersion";

    protected RPDoxygenXML command_;
    protected String defaultPath_;
    protected String defaultVersion_;
    protected JFrame mainFrame_;
    protected AlwaysOnTopMenuBar menuBar_;
    private JTextField textImportPath_;
    private JFormattedTextField textVersion_;
    private JButton buttonPath_;
    private JButton buttonImport_;

    public ImportGUI(RPDoxygenXML command) {
        super(ImportGUI.class);
        command_ = command;
        defaultPath_ = command_.getProperty(PROPERTY_NAME_PATH);
        defaultVersion_ = command_.getProperty(PROPERTY_NAME_VERSION);

        buildGui();
    }

    public void setVisible(boolean value) {
        if (value == true) {
            IRPPackage rppackage = command_.getElement();
            if (RPFunctionImporter.isImportTarget(rppackage) != true) {
                JOptionPane.showMessageDialog(mainFrame_,
                        "Please select the one package that will be the base point for the import");
                return;
            }

            mainFrame_.setTitle("Import API Specification to " + rppackage.getName());
        }

        setUIEnable(value);
        mainFrame_.setVisible(value);
    }

    private void buildGui() {
        mainFrame_ = new JFrame();

        mainFrame_.setSize(600, 200);
        menuBar_ = new AlwaysOnTopMenuBar(mainFrame_);
        mainFrame_.setJMenuBar(menuBar_);
        buildMainUI();
        mainFrame_.setVisible(false);
    }

    private void buildMainUI() {
        mainFrame_.setLayout(new FlowLayout());
        mainFrame_.add(getMainPanel());
    }

    private void setUIEnable(boolean enable) {
        if (buttonImport_ != null) {
            buttonImport_.setEnabled(enable);
        }

        if (buttonPath_ != null) {
            buttonPath_.setEnabled(enable);
        }

        if (textImportPath_ != null) {
            textImportPath_.setEnabled(enable);
        }

        if (textVersion_ != null) {
            textVersion_.setEnabled(enable);
            ;
        }

    }

    synchronized private void ImportDoxygen() {
        info("Import Start");

        setUIEnable(false);
        String doxygenPath = textImportPath_.getText();
        String currentVersion = textVersion_.getText();

        if (doxygenPath.isEmpty()) {
            setUIEnable(true);
            JOptionPane.showMessageDialog(mainFrame_, "Please select import doxygen xml path");
            return;
        }

        if (currentVersion.isEmpty()) {
            setUIEnable(true);
            JOptionPane.showMessageDialog(mainFrame_, "Please input version");
            return;
        }

        boolean result = false;
        try {
            command_.setProperty(PROPERTY_NAME_PATH, doxygenPath);
            command_.setProperty(PROPERTY_NAME_VERSION, currentVersion);
            command_.saveProperties();

            DoxygenXMLParser xmlparser = new DoxygenXMLParser();
            DoxygenObjectManager manager = xmlparser.Parse(doxygenPath);
            ;
            if (manager == null) {
                setUIEnable(true);
                JOptionPane.showMessageDialog(mainFrame_, "Import Error(Parse) Path:" + doxygenPath);
                return;
            }

            IRPPackage rppackage = command_.getElement();
            RPFunctionImporter importer = new RPFunctionImporter();
            result = importer.importModel(rppackage, manager, currentVersion);
        } catch (Exception e) {
            error("Import Error:", e);
        }

        info("Import Fisnish result:" + (result == true ? "Success" : "Fail"));
        setUIEnable(true);

        if (result == true) {
            mainFrame_.setVisible(false);
            setUIEnable(true);
            JOptionPane.showMessageDialog(mainFrame_, "Import complete");
        } else {
            mainFrame_.setVisible(false);
            setUIEnable(true);
            JOptionPane.showMessageDialog(mainFrame_, "Import Error(Model)");
        }

    }

    synchronized private void selectImportPath() {
        setUIEnable(false);

        FileSelector file = new FileSelector(textImportPath_.getText());
        String path = file.GetOpenDirectoryDialog();
        if (path != null) {
            textImportPath_.setText(path);
        }

        setUIEnable(true);
    }

    private JPanel getMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(3, 2));

        // import path
        buttonPath_ = new JButton("Select Doxygen XML Path");
        buttonPath_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectImportPath();
            }
        });
        mainPanel.add(buttonPath_);
        textImportPath_ = new JTextField();
        if (defaultPath_ != null) {
            textImportPath_.setText(defaultPath_);
        }
        mainPanel.add(textImportPath_);

        // input version
        JLabel labelVersion = new JLabel("Input Import Version:");
        labelVersion.setHorizontalAlignment(JLabel.TRAILING);
        mainPanel.add(labelVersion);

        try {
            MaskFormatter versionFormat = new MaskFormatter("v##.##.##");
            versionFormat.setPlaceholderCharacter('_');
            textVersion_ = new JFormattedTextField(versionFormat);
            textVersion_.setColumns(9);
            if (defaultVersion_ != null) {
                textVersion_.setText(defaultVersion_);
            } else {
                textVersion_.setText("v00.00.00");
            }

            mainPanel.add(textVersion_);
        } catch (Exception e) {
            error("MaskFormatter error:", e);
        }

        // import button
        JLabel labelImport = new JLabel(" ");
        labelImport.setHorizontalAlignment(JLabel.TRAILING);
        mainPanel.add(labelImport);
        buttonImport_ = new JButton("Import");
        buttonImport_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ImportDoxygen();
            }
        });
        mainPanel.add(buttonImport_);

        return mainPanel;
    }

}
package com.ibm.rhapsody.rputilities.rpcommand.importer;

import com.ibm.rhapsody.rputilities.rpcommand.RPDoxygenXML;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenObjectManager;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParser;
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
    protected static final String PROPERTY_NAME_TARGET = "TargetName";

    protected RPDoxygenXML command_;
    protected ImportOption importOption_;

    protected JFrame mainFrame_;
    protected AlwaysOnTopMenuBar menuBar_;
    private JTextField textImportPath_;
    private JButton buttonPath_;
    private JFormattedTextField textVersion_;
    private JCheckBox checkDefine_;
    private JCheckBox checkEnum_;
    private JCheckBox checkStruct_;
    private JCheckBox checkUnion_;
    private JCheckBox checkTypedef_;
    private JCheckBox checkFunction_;
    private JButton buttonImport_;

    public ImportGUI(RPDoxygenXML command) {
        super(ImportGUI.class);
        command_ = command;
        importOption_ = new ImportOption();
        
        importOption_.importPath = command_.getProperty(PROPERTY_NAME_PATH);
        importOption_.importVersion = command_.getProperty(PROPERTY_NAME_VERSION);

        buildGui();
    }

    public void setVisible(boolean value) {
        if (value == true) {
            IRPPackage rppackage = command_.getElement();
            if (RPFunctionImporter.isImportTarget(rppackage) != true) {
                JOptionPane.showMessageDialog(mainFrame_,
                        "Please select the one writable package that will be the base point for the import");
                return;
            }

            mainFrame_.setTitle("Import API Specification to " + rppackage.getName());
        }

        setUIEnable(value);
        mainFrame_.setVisible(value);
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
        }

        if (checkDefine_ != null) {
            checkDefine_.setEnabled(enable);
        }

        if (checkEnum_ != null) {
            checkEnum_.setEnabled(enable);
        }

        if (checkStruct_ != null) {
            checkStruct_.setEnabled(enable);
        }

        if (checkUnion_ != null) {
            checkUnion_.setEnabled(enable);
        }

        if (checkTypedef_ != null) {
            checkTypedef_.setEnabled(enable);
        }

        if (checkFunction_ != null) {
            checkFunction_.setEnabled(enable);
        }

        if (mainFrame_ != null) {
            mainFrame_.setAlwaysOnTop(enable);
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
            importOption_.importPath = doxygenPath;
            importOption_.importVersion = currentVersion;
            importOption_.importDefine = checkDefine_.isSelected();
            importOption_.importEnum = checkEnum_.isSelected();
            importOption_.importStruct = checkStruct_.isSelected();
            importOption_.importUnion = checkUnion_.isSelected();
            importOption_.importTypedef = checkTypedef_.isSelected();
            importOption_.importFunction = checkFunction_.isSelected();

            command_.setProperty(PROPERTY_NAME_PATH, importOption_.importPath);
            command_.setProperty(PROPERTY_NAME_VERSION, importOption_.importVersion);
            command_.saveProperties();

            IRPPackage rppackage = command_.getElement();
            if(rppackage == null) {
                setUIEnable(true);
                JOptionPane.showMessageDialog(mainFrame_, "Unable to identify the import target. Please select one package to import.");
                return;
            }
            
            DoxygenXMLParser xmlparser = new DoxygenXMLParser();
            DoxygenObjectManager manager = xmlparser.Parse(importOption_.importPath);
            ;
            if (manager == null) {
                setUIEnable(true);
                JOptionPane.showMessageDialog(mainFrame_, "Import Error(Parse) Path:" + doxygenPath);
                return;
            }

            RPFunctionImporter importer = new RPFunctionImporter();
            result = importer.importModel(rppackage, manager, importOption_);

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


    private void buildGui() {
        mainFrame_ = new JFrame();

        mainFrame_.setSize(600, 200);
        menuBar_ = new AlwaysOnTopMenuBar(mainFrame_);
        mainFrame_.setJMenuBar(menuBar_);
        buildMainUI();
        mainFrame_.setVisible(false);
    }

    private void buildMainUI() {
        mainFrame_.setLayout(new GridLayout(4, 1));
        mainFrame_.add(getImportPathPanel());
        mainFrame_.add(getImportVersionPanel());
        mainFrame_.add(getImportTargetPanel());
        mainFrame_.add(getRunImportPanel());
    }

    private JPanel getImportPathPanel() {
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new GridLayout(1, 3));

        JLabel labelPath = new JLabel("Doxygen XML Path:");
        labelPath.setHorizontalAlignment(JLabel.TRAILING);
        pathPanel.add(labelPath);

        textImportPath_ = new JTextField();
        if (importOption_.importPath != null) {
            textImportPath_.setText(importOption_.importPath);
        }
        pathPanel.add(textImportPath_);

        buttonPath_ = new JButton("Select");
        buttonPath_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectImportPath();
            }
        });
        pathPanel.add(buttonPath_);

        return pathPanel;
    }

    private JPanel getImportVersionPanel() {
        JPanel versionPanel = new JPanel();
        versionPanel.setLayout(new GridLayout(1, 2));

        // input version
        JLabel labelVersion = new JLabel("Import Version:");
        labelVersion.setHorizontalAlignment(JLabel.TRAILING);
        versionPanel.add(labelVersion);

        try {
            MaskFormatter versionFormat = new MaskFormatter("v##.##.##");
            versionFormat.setPlaceholderCharacter('_');
            textVersion_ = new JFormattedTextField(versionFormat);
            textVersion_.setColumns(9);
            if (importOption_.importVersion != null) {
                textVersion_.setText(importOption_.importVersion);
            } else {
                textVersion_.setText("v00.00.00");
            }

            versionPanel.add(textVersion_);
        } catch (Exception e) {
            error("MaskFormatter error:", e);
        }

        return versionPanel;
    }

    private JPanel getImportTargetPanel() {
        JPanel targetPanel = new JPanel();
        targetPanel.setLayout(new GridLayout(1, 2));

        JLabel labelTaget = new JLabel("Import Target:");
        labelTaget.setHorizontalAlignment(JLabel.TRAILING);
        targetPanel.add(labelTaget);

        // import target check
        JPanel checkListPanel = new JPanel();
        checkListPanel.setLayout(new GridLayout(3, 2));

        checkDefine_ = new JCheckBox("Define", importOption_.importDefine);
        checkEnum_ = new JCheckBox("Enumeration", importOption_.importEnum);
        checkStruct_ = new JCheckBox("Structure", importOption_.importStruct);
        checkUnion_ = new JCheckBox("Union", importOption_.importUnion);
        checkTypedef_ = new JCheckBox("Typedef(include callback)", importOption_.importTypedef);
        checkFunction_ = new JCheckBox("Function", importOption_.importFunction);
        checkListPanel.add(checkDefine_);
        checkListPanel.add(checkEnum_);
        checkListPanel.add(checkStruct_);
        checkListPanel.add(checkUnion_);
        checkListPanel.add(checkTypedef_);
        checkListPanel.add(checkFunction_);

        targetPanel.add(checkListPanel);
        return targetPanel;
    }

    private JPanel getRunImportPanel() {
        JPanel runImportPanel = new JPanel();
        runImportPanel.setLayout(new GridLayout(1, 2));

        JLabel labelRunImport = new JLabel("Run Import:");
        labelRunImport.setHorizontalAlignment(JLabel.TRAILING);
        runImportPanel.add(labelRunImport);

        // import button
        buttonImport_ = new JButton("Import");
        buttonImport_.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ImportDoxygen();
            }
        });
        runImportPanel.add(buttonImport_);

        return runImportPanel;
    }

}
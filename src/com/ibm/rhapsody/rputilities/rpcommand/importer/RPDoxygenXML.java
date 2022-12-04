package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.lang.Runtime;

import com.ibm.rhapsody.rputilities.RPExtensionUtilities;
import com.ibm.rhapsody.rputilities.doxygen.*;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenObjectManager;
import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;
// import com.ibm.rhapsody.rputilities.rpcore.RPLog;
// import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;

public class RPDoxygenXML extends IRPUtilityCommmand {

    /**
     * Doxygen's XML import class
     * @param element Elements selected when right-clicked
     */
    public RPDoxygenXML(IRPModelElement element) 
    {
        super(RPDoxygenXML.class,element);
    }

    /* 
     * Import Doxygen XML in the specified directory
     * @see com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) 
    {
        info("Import Start");

        // RPLog.setLevel(RPLogLevel.DEBUG);

        IRPPackage rootPackage = getElement(); 
        if(rootPackage == null) {
            error("name[" + argment[0] + "] is need select element.\n"
                + "Please select one Element.");
            return false;
        }

        ImportGUI ui = new ImportGUI(RPExtensionUtilities.getApplication());
        
        String doxygenPath = "E:\\Rhapsody\\Doxygen\\out\\xml";
        String currentVersion = "v01.00.00";

        // DoxygenXMLParser xmlparser = new DoxygenXMLParser();
        // DoxygenObjectManager manager = xmlparser.Parse(doxygenPath);;
        // if( manager == null) {
        //     return false;
        // }
        
        // RPFunctionImporter importer = new RPFunctionImporter();
        // boolean result = false;
        // result  = importer.importModel(rootPackage,manager,currentVersion);
        // return result;
        info("Import Finish");
        return true;
    }




}

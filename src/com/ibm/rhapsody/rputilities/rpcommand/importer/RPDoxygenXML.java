package com.ibm.rhapsody.rputilities.rpcommand.importer;

import com.ibm.rhapsody.rputilities.doxygen.*;
import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;
import com.ibm.rhapsody.rputilities.rpcore.RPLog;
import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.ibm.rhapsody.rputilities.window.FileSelector;
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
        info("Doxygen import");

        RPLog.setLevel(RPLogLevel.DEBUG);

        IRPPackage rppackage = getElement(); 
        if(rppackage == null) {
            error("name[" + argment[0] + "] is need select element.\n"
                + "Please select one Element.");
            return false;
        }

/* 
        FileSelector file = new FileSelector();
        String doxygenPath = file.GetOpenDirectoryDialog();
        if(doxygenPath == null) {
            return false;
        }
         */

        String doxygenPath = "E:\\Rhapsody\\Doxygen\\out\\xml";

        DoxygenObjectManager manager = Parse(doxygenPath);;
        if( manager == null) {
            return false;
        }
        
        String tag = "memberdef";
        // String tag = "compounddef";
        DoxygenObjectManager.TagMap map = manager.getMap(tag);
        info("ParseXML success:" + map.size());

        // int index = 0;
        // for(DoxygenType value : map.values()) {
        //     if(!(value instanceof DoxygenTypeCompound)) {
        //         continue;
        //     }

        //     value.debugout(0);
        //     index++;
        //     if(index > 10) {
        //         break;
        //     }
        // }

        return true;
    }

    protected DoxygenObjectManager Parse(String doxygenPath) {
        
        String xsltPhath = doxygenPath + "\\combine.xslt";
        String sourcePath = doxygenPath+ "\\index.xml";

        String formatNowDate = RPFileSystem.CreateDateTimeString(null);
        String resultPath = RPFileSystem.getActiveProjectPath() + "\\result_" + formatNowDate + ".xml";

        DoxygenXMLParser xmlparser = new DoxygenXMLParser();
        boolean result = xmlparser.Parse(xsltPhath, sourcePath, resultPath);
        if(result != true) {
            return null;
        }
        
        return xmlparser.getManager();
    }


}

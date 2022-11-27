package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.List;

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
                
        boolean result = false;
        result  = importModel(rppackage,manager);

        return result;
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

        info("ParseXML success:" + xmlparser.getManager().size());
        
        return xmlparser.getManager();
    }

    protected boolean importModel(IRPPackage rppackage, DoxygenObjectManager manager) {
        // String tag = "compounddef";
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

        RPFunctionImporter importer = new RPFunctionImporter();
        boolean result = false;
        List <DoxygenType> list = null;

        list = manager.getList(TAGTYPE.TYPEDEF);
        info("Typedef:"+ list.size());

        for(DoxygenType value : list) {
            DoxygenTypeTypedef obj = getObject(value);
            result = importer.importTypedef(rppackage, obj);
            if( result != true ) {
                return result;
            }
        }

        list = manager.getList(TAGTYPE.FUNCTION);
        info("Function:"+ list.size());

        for(DoxygenType value : list) {
            DoxygenTypeFunction obj = getObject(value);
            result = importer.importAPI(rppackage, obj);
            if( result != true ) {
                return result;
            }
        }



        info("importModel Finish");
        return result;
    }

}

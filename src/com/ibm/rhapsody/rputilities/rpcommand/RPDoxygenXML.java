package com.ibm.rhapsody.rputilities.rpcommand;

import com.ibm.rhapsody.rputilities.rpcommand.importer.ImportGUI;
// import com.ibm.rhapsody.rputilities.rpcore.RPLog;
// import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.telelogic.rhapsody.core.IRPModelElement;

public class RPDoxygenXML extends IRPUtilityCommmand {
    protected static final String COMMAND_IMPORT = "Import";
    protected static final String COMMAND_REPLACECHECK = "ReplaceCheck";

    protected ImportGUI mainGUI_ = null;
    

    /**
     * Doxygen's XML import class
     * @param element Elements selected when right-clicked
     */
    public RPDoxygenXML(IRPModelElement element) {
        super(RPDoxygenXML.class,element);
        mainGUI_ = new ImportGUI(this);
    }

    /* 
     * Import Doxygen XML in the specified directory
     * @see com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) {
        // RPLog.setLevel(RPLogLevel.DEBUG);

        if(argment.length < 2) {
            error("RPDoxygenXML\\[" + COMMAND_IMPORT + " or " + COMMAND_REPLACECHECK 
                    + "]. Please check RPExtensionUtilities.hep");
            return false;
        }

        String command = argment[1];
        if(command.equals(COMMAND_IMPORT) == true) {
            mainGUI_.setVisible(true);
            return true;
        }
        else if(command.equals(COMMAND_REPLACECHECK) == true) {
            
        }
        
        return false;
    }
}

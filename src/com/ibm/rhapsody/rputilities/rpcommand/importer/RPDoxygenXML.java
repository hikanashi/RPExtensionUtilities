package com.ibm.rhapsody.rputilities.rpcommand.importer;

import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
// import com.ibm.rhapsody.rputilities.rpcore.RPLog;
// import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.telelogic.rhapsody.core.IRPModelElement;

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
        // RPLog.setLevel(RPLogLevel.DEBUG);
        ImportGUI ui = new ImportGUI(getElement());

        return true;
    }




}

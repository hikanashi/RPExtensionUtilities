package com.ibm.rhapsody.rputilities.rpcommand.doxygenimporter;

import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
import com.ibm.rhapsody.rputilities.window.FileSelector;
import com.telelogic.rhapsody.core.IRPModelElement;

public class RPDoxygenImporter extends IRPUtilityCommmand {

    /**
     * Doxygen's XML import class
     * @param element Elements selected when right-clicked
     */
    public RPDoxygenImporter(IRPModelElement element) 
    {
        super(RPDoxygenImporter.class,element);
    }

    /* 
     * Import Doxygen XML in the specified directory
     * @see com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) 
    {
        info("Doxygen import");
        FileSelector file = new FileSelector();
        file.GetOpenDirectoryDialog();

        return true;
    }
}

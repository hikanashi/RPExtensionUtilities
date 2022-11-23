package com.ibm.rhapsody.rputilities.rpcommand;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPModelElement;

public abstract class IRPUtilityCommmand extends ARPObject {
    
    protected IRPModelElement   m_element = null;

    /**
     * Constructor of Rhapsody utility command class
     * @param element Elements selected when right-clicked
     */
    protected IRPUtilityCommmand(Class<?> clazz,IRPModelElement element) {
        super(clazz);
        m_element = element;
    }

    /**
     * Commands executed when right-clicke
     * @param argment Menu selected when right-clicked (array of names in hep file divided by delimiters)
     * @return Result of command execution (true: success false: failure)
     */
    protected abstract boolean command(String[] argment);
 
    /**
     * Get elements selected when right-clicked
     * @param <T> Type to cast
     * @return Elements selected when right-clicked(null when multiple elements are selected)
     */
    public <T> T getElement() 
    {
        return getObject(m_element);
    }
}

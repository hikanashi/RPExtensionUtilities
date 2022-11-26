package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeEnum extends DoxygenType {

    public DoxygenTypeEnum() {
        super(DoxygenTypeEnum.class);
    }

    protected DoxygenType createElementInternal(XMLStreamReader reader, String tag) {
        trace("createElementInternal");
        return this;
    }

    protected DoxygenType startElementInternal(XMLStreamReader reader, String tag) {
        trace("startElementInternal");
        return this;
    }

    protected DoxygenType charactersInternal(String tag, String text) {
        trace("charactersInternal");
        return this;
    }

    protected DoxygenType endElementInternal(String tag) {
        trace("endElementInternal");
        return this;
    }
    
    protected void debugoutInternal(StringBuffer logbuffer) {
        
    }
}

package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeParam extends DoxygenType {
    protected String direction_ = "In";

    public DoxygenTypeParam() {
        super(DoxygenTypeParam.class);
    }
    
    public String getDirection() {
        return direction_;
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
        if(tag.equals("declname")) {
            append(name_,text);
        }

        return this;
    }

    protected DoxygenType endElementInternal(String tag) {
        trace("endElementInternal");
        DoxygenType target = this;
        return target;
    }
    
    protected void debugoutInternal(StringBuffer logbuffer) {
        
    }
}

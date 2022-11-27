package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeFunction extends DoxygenType {
    public DoxygenTypeFunction() {
        super(DoxygenTypeFunction.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.PARAM) == true) {
            return true;
        }

        if(type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if(option.breforettag.toString().equals("type") == true) {
            return true;
        }
        
        return false;
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

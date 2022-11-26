package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeDefilne extends DoxygenType {
    protected StringBuffer initializer_ = new StringBuffer(); 
    

    public DoxygenTypeDefilne() {
        super(DoxygenTypeDefilne.class);
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

        if(tag.equals("initializer")) {
            initializer_.append(text);
        }
        
        return this;
    }

    protected DoxygenType endElementInternal(String tag) {
        trace("endElementInternal");
        return this;
    }


    protected void debugoutInternal(StringBuffer logbuffer) {

    }
}

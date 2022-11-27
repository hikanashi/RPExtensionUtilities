package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeCompound extends DoxygenType {

    public DoxygenTypeCompound() {
        super(DoxygenTypeCompound.class);
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

        if(tag.equals("compoundname")) {
            append(name_,text);
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

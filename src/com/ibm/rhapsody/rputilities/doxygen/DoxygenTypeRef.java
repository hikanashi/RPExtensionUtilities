package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeRef extends DoxygenType {
    protected String kindref_ = "null";
    protected DoxygenType refobj = null;

    public DoxygenTypeRef() {
        super(DoxygenTypeRef.class);
    }


    protected DoxygenType createElementInternal(XMLStreamReader reader, String tag) {
        trace("createElementInternal");

        id_ = reader.getAttributeValue(null, "refid");
        kindref_ = reader.getAttributeValue(null, "kindref");

        return this;
    }

    protected void linkObject() {
        
        getParent().type_.append(" " + getText());

        if(manager_ != null) {
            refobj = manager_.getObject(kindref_ + "def", id_);
        }

        // debug("ref index:"+ getIndent() + " parent:"+ getParent().getIndent());
        return;
    }

    protected DoxygenType startElementInternal(XMLStreamReader reader, String tag) {
        trace("startElementInternal");
        return this;
    }

    protected DoxygenType charactersInternal(String tag, String text) {
        trace("charactersInternal");
        append(text_,text);
        return this;
    }

    protected DoxygenType endElementInternal(String tag) {
        trace("endElementInternal");
        return this;
    }

    
    protected void debugoutInternal(StringBuffer logbuffer) {
        logbuffer.append(",kindref:"+ kindref_ 
            + ",ref:" + (refobj != null ? refobj.getName() : "null"));
    }
}


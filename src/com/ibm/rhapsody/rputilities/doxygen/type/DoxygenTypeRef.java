package com.ibm.rhapsody.rputilities.doxygen.type;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeRef extends DoxygenType {
    protected String kindref_ = "null";
    protected DoxygenType refobj = null;

    public DoxygenTypeRef() {
        super(DoxygenTypeRef.class);
    }


    protected void createElementInternal(XMLStreamReader reader, String tag) {
        trace("createElementInternal");

        id_ = reader.getAttributeValue(null, "refid");
        kindref_ = reader.getAttributeValue(null, "kindref");

        return;
    }

    public void linkObject() {

        if(manager_ != null) {
            refobj = manager_.getObject(kindref_ + "def", id_);
        }

        // debug("ref index:"+ getIndent() + " parent:"+ getParent().getIndent());
        return;
    }

    protected void endThisElementInternal(String tag) {
        trace("endElementInternal");
        
        if(getParent().type_.length() > 0) {
            getParent().type_.append(" " + getText());
        } else {
            getParent().type_.append(getText());
        }

        return;
    }
    
    protected void debugoutInternal(StringBuffer logbuffer) {
        logbuffer.append(",kindref:"+ kindref_ 
            + ",ref:" + (refobj != null ? refobj.getName() : "null"));
    }
}


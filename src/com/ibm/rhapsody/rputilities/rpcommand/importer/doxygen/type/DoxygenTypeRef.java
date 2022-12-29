package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import javax.xml.stream.XMLStreamReader;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;

public class DoxygenTypeRef extends DoxygenType {
    protected String kindref_ = "null";
    protected DoxygenType refobj = null;

    public DoxygenTypeRef() {
        super(DoxygenTypeRef.class);
    }

    @Override
    protected void createElementInternal(DoxygenXMLParseOption option) {
        trace("createElementInternal");

        id_.setLength(0);
        id_.append(option.reader.getAttributeValue(null, "refid"));
        kindref_ = new String(option.reader.getAttributeValue(null, "kindref"));

        return;
    }

    @Override
    protected void linkObjectInternal() {

        if (manager_ != null) {
            refobj = manager_.getObject(kindref_ + "def", id_.toString());
        }

        // debug("ref index:"+ getIndent() + " parent:"+ getParent().getIndent());
        return;
    }

    @Override
    protected void endThisElementInternal(String tag) {
        trace("endElementInternal");

        if (getParent().type_.length() > 0) {
            getParent().type_.append(" " + getText());
        } else {
            getParent().type_.append(getText());
        }

        return;
    }

    @Override
    protected void debugoutInternal(StringBuffer logbuffer) {
        logbuffer.append(",kindref:" + kindref_
                + ",ref:" + (refobj != null ? refobj.getName() : "null"));
    }
}

package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenTypeFile extends DoxygenType {

    public DoxygenTypeFile() {
        super(DoxygenTypeFile.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        return true;
    }

}

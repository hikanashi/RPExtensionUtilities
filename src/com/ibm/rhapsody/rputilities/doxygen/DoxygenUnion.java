package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenUnion extends DoxygenType {

    public DoxygenUnion() {
        super(DoxygenUnion.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.VARIABLE)) {
            return true;
        }

        if(type.equals(TAGTYPE.ENUM)) {
            return true;
        }

        if(type.equals(TAGTYPE.TYPEDEF)) {
            return true;
        }

        if(type.equals(TAGTYPE.DEFINE)) {
            return true;
        }

        return false;
    }
}

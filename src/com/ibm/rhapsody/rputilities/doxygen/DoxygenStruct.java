package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenStruct extends DoxygenType {

    public DoxygenStruct() {
        super(DoxygenStruct.class);
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

        return false;
    }
}

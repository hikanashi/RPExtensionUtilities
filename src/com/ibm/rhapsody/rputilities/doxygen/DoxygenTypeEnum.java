package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenTypeEnum extends DoxygenType {


    public DoxygenTypeEnum() {
        super(DoxygenTypeEnum.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.ENUMVAL)) {
            return true;
        }

        return false;
    }
    
}

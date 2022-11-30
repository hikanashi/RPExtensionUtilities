package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenTypeVariable extends DoxygenType {
 
    public DoxygenTypeVariable() {
        super(DoxygenTypeVariable.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if(option.getBeforetTag().equals("type") == true) {
            return true;
        }
        
        return false;
    }

}

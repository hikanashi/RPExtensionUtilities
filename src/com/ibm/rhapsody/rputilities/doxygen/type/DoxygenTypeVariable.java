package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeVariable extends DoxygenType {
 
    public DoxygenTypeVariable() {
        super(DoxygenTypeVariable.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if(option.getBeforeTagWithoutPara().equals("type") == true) {
            return true;
        }
        
        return false;
    }

}

package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeDetailRetval extends DoxygenType {

    public DoxygenTypeDetailRetval() {
        super(DoxygenTypeDetailRetval.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.PARAMITEM)) {
            return true;
        }

        return false;
    }

}

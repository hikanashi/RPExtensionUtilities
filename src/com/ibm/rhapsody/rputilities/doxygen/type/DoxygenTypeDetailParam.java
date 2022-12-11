package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeDetailParam extends DoxygenType {

    public DoxygenTypeDetailParam() {
        super(DoxygenTypeDetailParam.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.PARAMITEM)) {
            return true;
        }

        return false;
    }

}

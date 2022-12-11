package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeStruct extends DoxygenType {

    public DoxygenTypeStruct() {
        super(DoxygenTypeStruct.class);
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

        if(type.equals(TAGTYPE.DETAILPARAM) == true) {
            return true;
        }

        if(type.equals(TAGTYPE.DETAILRETVAL) == true) {
            return true;
        }

        return false;
    }
}

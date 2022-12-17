package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeUnion extends DoxygenType {

    public DoxygenTypeUnion() {
        super(DoxygenTypeUnion.class);
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

        if(type.equals(TAGTYPE.DETAILPARAM) == true) {
            return true;
        }

        if(type.equals(TAGTYPE.DETAILRETVAL) == true) {
            return true;
        }

        if(type.equals(TAGTYPE.DETAILRETUEN) == true) {
            return true;
        }


        return false;
    }
}

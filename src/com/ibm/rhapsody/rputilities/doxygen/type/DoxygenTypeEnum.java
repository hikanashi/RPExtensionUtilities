package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeEnum extends DoxygenType {

    public DoxygenTypeEnum() {
        super(DoxygenTypeEnum.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.ENUMVAL)) {
            return true;
        }

        return false;
    }

}

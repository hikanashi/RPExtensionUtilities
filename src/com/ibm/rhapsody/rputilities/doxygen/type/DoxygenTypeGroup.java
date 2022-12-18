package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeGroup extends DoxygenType {

    public DoxygenTypeGroup() {
        super(DoxygenTypeGroup.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        return true;
    }

}

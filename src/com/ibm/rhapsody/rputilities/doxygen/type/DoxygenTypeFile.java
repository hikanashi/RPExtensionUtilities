package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeFile extends DoxygenType {

    public DoxygenTypeFile() {
        super(DoxygenTypeFile.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        return true;
    }

}

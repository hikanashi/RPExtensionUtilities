package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;

public class DoxygenTypeDetailParam extends DoxygenType {

    public DoxygenTypeDetailParam() {
        super(DoxygenTypeDetailParam.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.PARAMITEM)) {
            return true;
        }

        return false;
    }

}

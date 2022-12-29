package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;

public class DoxygenTypeGroup extends DoxygenType {

    public DoxygenTypeGroup() {
        super(DoxygenTypeGroup.class);
    }

    @Override
    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        return true;
    }

}

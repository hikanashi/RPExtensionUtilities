package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;

public class DoxygenTypeEnum extends DoxygenType {

    public DoxygenTypeEnum() {
        super(DoxygenTypeEnum.class);
    }

    @Override
    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.ENUMVAL)) {
            return true;
        }

        return false;
    }

}

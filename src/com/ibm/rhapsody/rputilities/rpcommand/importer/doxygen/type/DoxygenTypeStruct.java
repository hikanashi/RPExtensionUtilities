package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;

public class DoxygenTypeStruct extends DoxygenType {

    public DoxygenTypeStruct() {
        super(DoxygenTypeStruct.class);
    }

    @Override
    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.VARIABLE)) {
            return true;
        }

        if (type.equals(TAGTYPE.ENUM)) {
            return true;
        }

        if (type.equals(TAGTYPE.TYPEDEF)) {
            return true;
        }

        if (type.equals(TAGTYPE.DETAILPARAM) == true) {
            return true;
        }

        if (type.equals(TAGTYPE.DETAILRETVAL) == true) {
            return true;
        }

        if (type.equals(TAGTYPE.DETAILRETUEN) == true) {
            return true;
        }

        return false;
    }
}

package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;

public class DoxygenTypeDetailRetval extends DoxygenType {

    public DoxygenTypeDetailRetval() {
        super(DoxygenTypeDetailRetval.class);
    }

    @Override
    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.PARAMITEM)) {
            return true;
        }

        return false;
    }

    @Override
    protected void linkObjectInternal() {
        trace("linkObjectInternal:" + getName());

        if (getText().length() < 1) {
            return;
        }

        DoxygenType parent = getParent();
        if (parent == null) {
            return;
        }

        if (parent instanceof DoxygenTypeFunction) {
            DoxygenTypeFunction function = getObject(parent);
            function.setReturnDescription(getText());
        }

        return;
    }

}

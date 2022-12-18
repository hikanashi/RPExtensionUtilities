package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeFunction extends DoxygenType {
    protected StringBuffer returndescription_ = new StringBuffer();

    public DoxygenTypeFunction() {
        super(DoxygenTypeFunction.class);
    }

    public String getReturnDescription() {
        return returndescription_.toString();
    }

    public void setReturnDescription(String description) {
        if (description == null) {
            return;
        }

        appendText(returndescription_, description);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.PARAM) == true) {
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

        if (type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if (option.getBeforeTagWithoutPara().equals("type") == true) {
            return true;
        }

        debug(String.format("name:%s(%s) Type:%s is current:%s before:%s isn't type",
                getName(),
                getId(),
                type.toString(),
                option.getCurrentTag(),
                option.getBeforeTagWithoutPara()));

        return false;
    }

    protected void linkObjectInternal() {
        // logoutdebug(0);
        return;
    }

    protected void debugoutInternal(StringBuffer logbuffer) {
        logbuffer.append(",returnDesc:" + returndescription_.toString());
    }
}

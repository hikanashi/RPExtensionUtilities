package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;

public class DoxygenTypeParam extends DoxygenType {
    protected String direction_ = "";

    public DoxygenTypeParam() {
        super(DoxygenTypeParam.class);
    }

    public String getDirection() {
        return direction_;
    }

    public void setDirection(String direction) {
        if (direction == null) {
            return;
        }

        direction_ = new String(direction);
    }

    public void setDescription(String description) {
        if (description == null) {
            return;
        }

        appendText(briefdescription_, description);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if (option.getBeforeTagWithoutPara().equals("type") == true) {
            return true;
        }

        return false;
    }

    protected void charactersSubInternal(String tag, String text) {

        if (tag.equals("declname")) {
            appendPlane(name_, text);
        }

        return;
    }

}

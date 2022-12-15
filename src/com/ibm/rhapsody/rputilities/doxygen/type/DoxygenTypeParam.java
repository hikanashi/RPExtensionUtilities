package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeParam extends DoxygenType {
    protected String direction_ = "In";

    public DoxygenTypeParam() {
        super(DoxygenTypeParam.class);
    }
    
    public String getDirection() {
        return direction_;
    }

    public void setDirection(String direction) {
        if(direction == null) {
            return;
        }

        direction_ = new String(direction);
    }

    public void setDescription(String description) {
        if(description == null) {
            return;
        }

        briefdescription_.append(description);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if(option.getBeforeTagWithoutPara().equals("type") == true) {
            return true;
        }
        
        return false;
    }

    protected void charactersSubInternal(String tag, String text) {

        if(tag.equals("declname")) {
            append(name_,text);
        }

        return;
    }
   
}

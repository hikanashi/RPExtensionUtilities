package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenTypeParam extends DoxygenType {
    protected String direction_ = "In";

    public DoxygenTypeParam() {
        super(DoxygenTypeParam.class);
    }
    
    public String getDirection() {
        return direction_;
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if(option.getBeforetTag().equals("type") == true) {
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

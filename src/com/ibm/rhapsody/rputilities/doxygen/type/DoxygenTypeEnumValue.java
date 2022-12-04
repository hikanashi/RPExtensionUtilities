package com.ibm.rhapsody.rputilities.doxygen.type;

public class DoxygenTypeEnumValue extends DoxygenType {
    protected StringBuffer initializer_ = new StringBuffer(); 

    public DoxygenTypeEnumValue() {
        super(DoxygenTypeEnumValue.class);
    }

    public String getInitializer() {
        return initializer_.toString();
    }

    protected void charactersSubInternal(String tag, String text) {
        trace("charactersInternal");

        if(tag.equals("initializer")) {
            initializer_.append(text);
        }
        
        return;
    }

}


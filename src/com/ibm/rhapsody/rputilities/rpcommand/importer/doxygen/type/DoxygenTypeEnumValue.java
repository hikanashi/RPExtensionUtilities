package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

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

        if (tag.equals("initializer")) {
            appendText(initializer_, text);
        }

        return;
    }

}

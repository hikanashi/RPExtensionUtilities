package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

public class DoxygenTypeDefilne extends DoxygenType {
    protected StringBuffer initializer_ = new StringBuffer();

    public DoxygenTypeDefilne() {
        super(DoxygenTypeDefilne.class);
    }

    public String getInitializer() {
        return initializer_.toString();
    }

    @Override
    protected void charactersSubInternal(String tag, String text) {
        trace("charactersInternal");

        if (tag.equals("initializer")) {
            appendText(initializer_, text);
        }

        return;
    }

}

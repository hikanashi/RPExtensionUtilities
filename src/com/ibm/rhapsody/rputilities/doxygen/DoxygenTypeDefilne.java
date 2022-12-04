package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenTypeDefilne extends DoxygenType {
    protected StringBuffer initializer_ = new StringBuffer(); 
    

    public DoxygenTypeDefilne() {
        super(DoxygenTypeDefilne.class);
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

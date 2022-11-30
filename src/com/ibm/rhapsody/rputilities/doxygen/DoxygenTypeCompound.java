package com.ibm.rhapsody.rputilities.doxygen;

public class DoxygenTypeCompound extends DoxygenType {

    public DoxygenTypeCompound() {
        super(DoxygenTypeCompound.class);
    }


    protected void charactersSubInternal(String tag, String text) {
        trace("charactersInternal");

        if(tag.equals("compoundname")) {
            append(name_,text);
        }

        return;
    }

}

package com.ibm.rhapsody.rputilities.doxygen.type;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeFunction extends DoxygenType {
    public DoxygenTypeFunction() {
        super(DoxygenTypeFunction.class);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.PARAM) == true) {
            return true;
        }

        if(type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if(option.getBeforetTag().equals("type") == true) {
            return true;
        }
 
        debug(String.format("name:%s(%s) Type:%s is current:%s before:%s isn't type",
                            getName(), 
                            getId(),
                            type.toString(),
                            option.getCurrentTag(),
                            option.getBeforetTag()));
        
        return false;
    }
    
}

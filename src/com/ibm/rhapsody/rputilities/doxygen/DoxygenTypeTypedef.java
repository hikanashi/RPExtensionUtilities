package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenTypeTypedef extends DoxygenType {
    protected StringBuffer argsstring_ = new StringBuffer();
    
    public DoxygenTypeTypedef() {
        super(DoxygenTypeTypedef.class);
    }

    public boolean isCallback() {
        if( type_.toString().contains("(") == true ) {
            return true;
        }
        return false;
    }

    public String getType() {
        String type = type_.toString();
        if(isCallback() == true) {
            return type.substring(0, type.indexOf("("));
        } else {
            return type;
        }
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if(type.equals(TAGTYPE.PARAM) == true) {
            return true;
        }

        if(type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if(option.breforettag.toString().equals("type") == true) {
            return true;
        }
        
        return false;
    }

    protected DoxygenType createElementInternal(XMLStreamReader reader, String tag) {
        trace("createElementInternal");
        return this;
    }

    protected DoxygenType startElementInternal(XMLStreamReader reader, String tag) {
        trace("startElementInternal");
        return this;
    }

    protected DoxygenType charactersInternal(String tag, String text) {
        trace("charactersInternal");

        if(tag.equals("argsstring")) {
            append(argsstring_,text);
        }

        return this;
    }

    protected DoxygenType endElementInternal(String tag) {
        DoxygenType target = this;

        if(tag.equals(getTag()) != true) {
            return target;
        }

        if(argsstring_.length() < 1) {
            return target;
        }

        trace("endElementInternal:"+ argsstring_.toString());

        CreateParameter(argsstring_.toString());
        return this;
    }

    protected void debugoutInternal(StringBuffer logbuffer) {
        
    }

    protected void CreateParameter(String argstring) {
        String value = argstring.toString().replaceAll("\\(|\\)", "");

        trace("args:"+ value);

        String[] all_args =  value.split(",");
        
        for(int allindex = 0; allindex < all_args.length; allindex++) {

            String[] one_arg = all_args[allindex].split(" ");
            TAGTYPE paramtype = TAGTYPE.PARAM;
            DoxygenType param = paramtype.newInstance();

            for(int oneindex = 0; oneindex < one_arg.length; oneindex++) {
                String argelement = one_arg[oneindex].trim();
                if( one_arg.length-1 <= oneindex) {
                    if(argelement.charAt(0) == '*') {
                        if(argelement.length() > 1 ) {
                            param.name_.append(argelement.substring(1));
                        }
                    }
                    else {
                        param.name_.append(argelement);
                    }
                }
                else {
                    if(oneindex == 0) {
                        param.type_.append(argelement);
                    }
                    else {
                        param.type_.append(" "+ argelement);
                    }

                    if(one_arg[oneindex+1].charAt(0) == '*') {
                        param.type_.append(" *");
                    }
                }
            }

            param.setParent(this);
        }
    }
}

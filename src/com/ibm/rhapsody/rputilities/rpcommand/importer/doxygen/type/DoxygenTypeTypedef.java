package com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;

public class DoxygenTypeTypedef extends DoxygenType {
    protected StringBuffer argsstring_ = new StringBuffer();

    public DoxygenTypeTypedef() {
        super(DoxygenTypeTypedef.class);
    }

    public boolean isCallback() {
        if (type_.toString().contains("(") == true) {
            return true;
        }
        return false;
    }

    public String getType() {
        String type = type_.toString();
        if (isCallback() == true) {
            return type.substring(0, type.indexOf("("));
        } else {
            return type;
        }
    }

    @Override
    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        if (type.equals(TAGTYPE.PARAM) == true) {
            return true;
        }

        if (type.equals(TAGTYPE.DETAILPARAM) == true) {
            return true;
        }

        if (type.equals(TAGTYPE.DETAILRETVAL) == true) {
            return true;
        }

        if (type.equals(TAGTYPE.DETAILRETUEN) == true) {
            return true;
        }

        if (type.equals(TAGTYPE.REF) != true) {
            return false;
        }

        if (option.getBeforeTagWithoutPara().equals("type") == true) {
            return true;
        }

        return false;
    }

    @Override
    protected void charactersSubInternal(String tag, String text) {
        trace("charactersSubInternal");

        if (tag.equals("argsstring")) {
            appendPlane(argsstring_, text);
        }

        return;
    }

    @Override
    protected void endThisElementInternal(String tag) {
        if (argsstring_.length() < 1) {
            return;
        }

        trace("endThisElementInternal:" + argsstring_.toString());

        CreateParameter(argsstring_.toString());
        return;
    }

    protected void CreateParameter(String argstring) {
        String value = argstring.toString().replaceAll("\\(|\\)", "");

        trace("args:" + value);

        String[] all_args = value.split(",");

        for (int allindex = 0; allindex < all_args.length; allindex++) {

            String[] one_arg = all_args[allindex].split(" ");
            TAGTYPE paramtype = TAGTYPE.PARAM;
            DoxygenType param = paramtype.newDoxygenInstance();

            for (int oneindex = 0; oneindex < one_arg.length; oneindex++) {
                String argelement = one_arg[oneindex].trim();
                if (one_arg.length - 1 <= oneindex) {
                    if( argelement.length() <= 0) {

                    }
                    else if (argelement.charAt(0) == '*') {
                        if (argelement.length() > 1) {
                            param.name_.append(argelement.substring(1));
                        }
                    } else {
                        param.name_.append(argelement);
                    }
                } else {
                    if (oneindex == 0) {
                        param.type_.append(argelement);
                    } else {
                        param.type_.append(" " + argelement);
                    }
                    
                    if(one_arg[oneindex + 1].length() > 0) {
                        if (one_arg[oneindex + 1].charAt(0) == '*') {
                            param.type_.append(" *");
                        }    
                    }
                }
            }

            if (param.name_.length() < 1) {
                param.name_.append("argument_" + allindex);
            }

            param.setParent(this);
        }
    }
}

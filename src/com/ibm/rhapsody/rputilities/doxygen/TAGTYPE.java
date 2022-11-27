package com.ibm.rhapsody.rputilities.doxygen;

import java.lang.reflect.Constructor;

public enum TAGTYPE {
    DEFINE("memberdef",KEYTYPE.KEY_ATTR_KIND,"define",false,DoxygenTypeDefilne.class),
    ENUM("memberdef",KEYTYPE.KEY_ATTR_KIND,"enum",false,DoxygenTypeEnum.class),
    FUNCTION("memberdef",KEYTYPE.KEY_ATTR_KIND,"function",false,DoxygenTypeFunction.class),
    TYPEDEF("memberdef",KEYTYPE.KEY_ATTR_KIND,"typedef",false,DoxygenTypeTypedef.class),
    VARIABLE("memberdef",KEYTYPE.KEY_ATTR_KIND,"variable",false,DoxygenTypeVariable.class),
    PARAM("param",KEYTYPE.KEY_TAG,"",true,DoxygenTypeParam.class),
    ENUMVAL("enumvalue",KEYTYPE.KEY_TAG,"",true,DoxygenTypeEnumValue.class),
    REF("ref",KEYTYPE.KEY_TAG,"",true,DoxygenTypeRef.class),
    COMPOUND("compounddef",KEYTYPE.KEY_TAG,"",false,DoxygenTypeCompound.class),
    ;

    public enum KEYTYPE {
        KEY_TAG,
        KEY_ATTR_KIND,
    }

    private final String tag_;
    private final String attrvalue_;
    private final KEYTYPE type_;
    private final boolean needParent_;
    private final Class<?> clazz_;

    TAGTYPE(final String tag, final KEYTYPE type, final String attrvalue, boolean needParent, Class<?> clazz) {
        tag_ = tag;
        type_ = type;
        attrvalue_ = attrvalue;
        needParent_ = needParent;
        clazz_ = clazz;
    }

    public KEYTYPE getKeytype() {
        return type_;
    }

    public String getTag() {
        return tag_;
    }

    public String getAttrName() {
        if(type_ == KEYTYPE.KEY_ATTR_KIND) {
            return "kind";
        }

        return tag_;
    }

    public String getAttrValue() {
        return attrvalue_;
    }

    public boolean isNeedParent() {
        return needParent_;
    }

    public DoxygenType newInstance() {
        DoxygenType type = null;

        try {
            Constructor<?> constructor = clazz_.getDeclaredConstructor();
            type = (DoxygenType) constructor.newInstance();
            type.tag_ = tag_;
            if(type_ == KEYTYPE.KEY_ATTR_KIND) {
                type.kind_ = attrvalue_;
            }

        } catch(Exception e) {   
        }

        return type;
    }

}

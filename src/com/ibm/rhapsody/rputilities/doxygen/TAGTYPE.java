package com.ibm.rhapsody.rputilities.doxygen;

import java.lang.reflect.Constructor;
import com.ibm.rhapsody.rputilities.doxygen.type.*;

public enum TAGTYPE {
    DEFINE("memberdef",KEYTYPE.KEY_ATTR_KIND,"define",false,DoxygenTypeDefilne.class),
    ENUM("memberdef",KEYTYPE.KEY_ATTR_KIND,"enum",false,DoxygenTypeEnum.class),
    FUNCTION("memberdef",KEYTYPE.KEY_ATTR_KIND,"function",false,DoxygenTypeFunction.class),
    TYPEDEF("memberdef",KEYTYPE.KEY_ATTR_KIND,"typedef",false,DoxygenTypeTypedef.class),
    VARIABLE("memberdef",KEYTYPE.KEY_ATTR_KIND,"variable",true,DoxygenTypeVariable.class),
    PARAM("param",KEYTYPE.KEY_TAG,"",true,DoxygenTypeParam.class),
    DETAILPARAM("parameterlist",KEYTYPE.KEY_ATTR_KIND,"param",true,DoxygenTypeDetailParam.class),
    DETAILRETVAL("parameterlist",KEYTYPE.KEY_ATTR_KIND,"retval",true,DoxygenTypeDetailRetval.class),
    PARAMITEM("parameteritem",KEYTYPE.KEY_TAG,"",true,DoxygenTypeParamItem.class),
    ENUMVAL("enumvalue",KEYTYPE.KEY_TAG,"",true,DoxygenTypeEnumValue.class),
    REF("ref",KEYTYPE.KEY_TAG,"",true,DoxygenTypeRef.class),
    STRUCT("compounddef",KEYTYPE.KEY_ATTR_KIND,"struct",false,DoxygenTypeStruct.class),
    UNION("compounddef",KEYTYPE.KEY_ATTR_KIND,"union",false,DoxygenTypeUnion.class),
    GROUP("compounddef",KEYTYPE.KEY_ATTR_KIND,"group",false,DoxygenTypeGroup.class),
    FILE("compounddef",KEYTYPE.KEY_ATTR_KIND,"file",false,DoxygenTypeFile.class),
    ;

    public enum KEYTYPE {
        KEY_TAG,
        KEY_ATTR_KIND,
    }

    private final String tag_;
    private final String attrvalue_;
    private final KEYTYPE type_;
    private final boolean needParent_;
    private final Class<?> doxygenclazz_;

    TAGTYPE(final String tag, final KEYTYPE type, final String attrvalue, boolean needParent, Class<?> doxygenclazz) {
        tag_ = tag;
        type_ = type;
        attrvalue_ = attrvalue;
        needParent_ = needParent;
        doxygenclazz_ = doxygenclazz;
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

    public DoxygenType newDoxygenInstance() {
        DoxygenType type = null;

        try {
            Constructor<?> constructor = doxygenclazz_.getDeclaredConstructor();
            type = (DoxygenType) constructor.newInstance();
            type.setTag(tag_);
            if(type_ == KEYTYPE.KEY_ATTR_KIND) {
                type.setKind(attrvalue_);
            }

        } catch(Exception e) {   
        }

        return type;
    }



}

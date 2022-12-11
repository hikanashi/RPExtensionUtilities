package com.ibm.rhapsody.rputilities.doxygen.type;

import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;

public class DoxygenTypeParamItem extends DoxygenType {
    protected String direction_ = "";

    public DoxygenTypeParamItem() {
        super(DoxygenTypeParamItem.class);
    }

    public String getDirection() {
        return direction_;
    }

    protected void startSubElementInternal(DoxygenXMLParseOption option) {
        String tag = new String(option.getCurrentTag());
        if(tag.equals("parametername") == true) {
            String direction = option.reader.getAttributeValue(null, "direction");
            if(direction != null) {
                direction_ = new String(direction);
            }
        }
        return;
    }

    protected void charactersSubInternal(String tag, String text) {
        if(tag.equals("parametername")) {
            name_.setLength(0);
            append(name_,text);
        }
        else if(tag.equals("parameterdescription")) {
            briefdescription_.append(text);
        }
    }

    protected void charactersChildInternal(String tag, String text) {
        charactersSubInternal(tag,text);
        return;
    }

    protected void linkObjectInternal() {
        info("linkObjectInternal:"+ getName());

        DoxygenTypeParam param = GetRelateParam();
        if(param == null) {
            return;
        }

        info( getName() + " set bref:"+ briefdescription_.toString());
        param.setDirection(direction_);
        param.setDescription(briefdescription_.toString());
        return;
    }




    protected DoxygenTypeParam GetRelateParam() {
        DoxygenType parent = getParent();
        if( parent == null) {
            warn(getName() +":first parent");
            return null;
        }

        parent = parent.getParent();
        if( parent == null) {
            warn(getName() +":2nd parent");
            return null;
        }
        
        List<DoxygenType> params = parent.getChildlen(TAGTYPE.PARAM);
        for(DoxygenType param : params) {
            if(param.getName().equals(getName()) == true) {
                return getObject(param);
            }
        }
        warn(getName() +" not found:" + parent.getName());

        return null;
    }

    protected void debugoutInternal(StringBuffer logbuffer) {
        logbuffer.append(",direction:"+ ( direction_ != null ? direction_ : "none")); 
        return;
    }
}
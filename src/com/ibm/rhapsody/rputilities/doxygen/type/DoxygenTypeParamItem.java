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
        trace("linkObjectInternal:"+ getName());

        DoxygenType parent = getParent();
        if(parent == null) {
            return;
        }

        DoxygenType relatedtype = GetRelateParam();
        if(relatedtype == null) {
            return;
        }

        if(relatedtype instanceof DoxygenTypeFunction) {
            DoxygenTypeFunction function = getObject(relatedtype);
            function.setReturnDescription(briefdescription_.toString());
        }

        if(relatedtype instanceof DoxygenTypeParam) {
            DoxygenTypeParam param = getObject(relatedtype);
            param.setDirection(direction_);
            param.setDescription(briefdescription_.toString());
        }

        return;
    }

    protected DoxygenType GetRelateParam() {
        DoxygenType parent = getParent();
        if( parent == null) {
            warn(getName() +":first parent");
            return null;
        }

        DoxygenType pparent = parent.getParent();
        if( pparent == null) {
            warn(getName() +":2nd parent");
            return null;
        }
        

        if( parent instanceof DoxygenTypeDetailRetval) {
            if(pparent instanceof DoxygenTypeFunction) {
                return pparent;
            }
        }
        
        List<DoxygenType> params = pparent.getChildlen(TAGTYPE.PARAM);
        for(DoxygenType param : params) {
            if(param.getName().equals(getName()) == true) {
                return getObject(param);
            }
        }
        warn(getName() +" not found:" + pparent.getName());

        return null;
    }

    protected void debugoutInternal(StringBuffer logbuffer) {
        logbuffer.append(",direction:"+  direction_ ); 
        return;
    }
}
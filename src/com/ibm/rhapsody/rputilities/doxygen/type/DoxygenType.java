package com.ibm.rhapsody.rputilities.doxygen.type;


import java.util.List;
import java.util.ArrayList;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenObjectManager;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenXMLParseOption;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;

public abstract class DoxygenType extends ARPObject {
    protected DoxygenObjectManager manager_ = null;
    protected DoxygenType parent_ = null;
    protected List<DoxygenType> children_ = new ArrayList<DoxygenType>();
    protected StringBuffer id_ = new StringBuffer();
    protected StringBuffer kind_ = new StringBuffer();
    protected StringBuffer tag_ = new StringBuffer();
    protected StringBuffer type_ = new StringBuffer(); 
    protected StringBuffer name_ = new StringBuffer(); 
    protected StringBuffer qualifiedname_ = new StringBuffer(); 
    protected StringBuffer text_ = new StringBuffer(); 
    protected StringBuffer briefdescription_ = new StringBuffer(); 
    protected StringBuffer detaileddescription_ = new StringBuffer();
    protected StringBuffer inbodydescription_ = new StringBuffer();

    
    protected DoxygenType(Class<?> clazz) {
        super(clazz);
    }

    public boolean isCreateChildlen(TAGTYPE type, DoxygenXMLParseOption option) {
        return false;
    }

    public void setManager(DoxygenObjectManager manager) {
        manager_ = manager;
    }
    
    public DoxygenType getParent() {
        return parent_;
    }

    public List<DoxygenType> getChildlen() {
        return children_;
    }

    public List<DoxygenType> getChildlen(TAGTYPE type) {
        List<DoxygenType> list = new ArrayList<DoxygenType>();
        
        for(DoxygenType child : children_) {
            if(child.equals(type)) {
                list.add(child);
            }
        }

        return list;
    }

    public void setParent(DoxygenType parent) {
        if(parent == null ) {
            return;
        }

        parent_ = parent;
        parent.children_.add(this);
    }

    public String getId() {
        return id_.toString();
    }
    
    public String getKind() {
        return kind_.toString();
    }

    public void setKind(String kind) {
        kind_.append(kind);
    }

    public String getTag() {
        return tag_.toString();
    }

    public void setTag(String tag) {
        tag_.append(tag);
    }

    public String getText() {
        return text_.toString().trim();
    }

    public String getType() {
        return type_.toString().trim();
    }

    public String getName() {
        return name_.toString().trim();
    }

    public String getQualifiedName() {
        if(qualifiedname_.toString().length() > 0) {
            return qualifiedname_.toString().trim();
        } else {
            return getName();
        }
    }
    
    public String getBriefdescription() {
        return briefdescription_.toString().trim();
    }

    public String getDetaileddescription() {
        return detaileddescription_.toString().trim();
    }

    public String getInbodydescription() {
        return inbodydescription_.toString().trim();
    }

    public boolean equals(TAGTYPE type) {
        if(type.getKeytype() == TAGTYPE.KEYTYPE.KEY_ATTR_KIND) {
            if(getKind().equals(type.getAttrValue())) {
                return true;
            }
        }
        else {
            if(getTag().equals(type.getTag())) {
                return true;
            }
        }
        return false;
    }

 
    public DoxygenType createElement(DoxygenXMLParseOption option) {
        DoxygenType target = this;

        String tag = option.getCurrentTag();
        if(tag != null) {
            tag_.setLength(0);
            tag_.append(tag);
        }

        String id = option.reader.getAttributeValue(null, "id");
        if(id != null) {
            id_.setLength(0);
            id_.append(id);
        }

        String kind = option.reader.getAttributeValue(null, "kind");
        if(kind != null) {
            kind_.setLength(0);
            kind_.append(kind);    
        }

        createElementInternal(option);
        trace("createElement tag:"+ getTag());        
        return target;
    }

    protected void createElementInternal(DoxygenXMLParseOption option) {
        return;
    }

    public DoxygenType startSubElement(DoxygenXMLParseOption option) {
        // trace("\tEvent:START_ELEMENT"  + " Name:"+ reader.getLocalName() );
        // for(int index = 0; index < reader.getAttributeCount(); index++ ) {
        //     trace("\t\t Name:" + reader.getAttributeName(index) 
        //         + " Type:"+ reader.getAttributeType(index) 
        //         + " Value:" + reader.getAttributeValue(index));
        // }

        DoxygenType target = this;
        startSubElementInternal(option);

        return target;
    }

    protected void startSubElementInternal(DoxygenXMLParseOption option) {
        return;
    }

    public DoxygenType characters(DoxygenXMLParseOption option) {
        DoxygenType target = this;
        // trace("\tEvent:CHARACTERS"  + " Tag:" + tag + " Text:"+ reader.getText() );
        String tag = new String(option.getCurrentTagWithoutPara());
        if(tag.isEmpty()) {        
            return target;
        }
        String text = new String(option.reader.getText()).trim();

        if(tag.equals(getTag())) {
            text_.append(text);
        }
        else if(option.getBeforeTagWithoutPara().equals(getTag())) {
            if(tag.equals("name")) {
                append(name_,text);
            }
            else if(tag.equals("compoundname")) {
                append(name_,text);
            }
            else if(tag.equals("qualifiedname")) {
                append(qualifiedname_,text);
            }
            else if(tag.equals("type")) {
                append(type_,text);
            }
            else if(tag.equals("briefdescription")) {
                briefdescription_.append(text);
            }
            else if(tag.equals("detaileddescription")) {
                detaileddescription_.append(text);
            }
            else if(tag.equals("inbodydescription")) {
                inbodydescription_.append(text);
            }
            else {
                charactersSubInternal(tag, text);
            }
        }
        else {
            charactersChildInternal(tag, text);
        }
        
        return target;
    }




    protected void charactersSubInternal(String tag, String text) {
        return;
    }

    protected void charactersChildInternal(String tag, String text) {
        return;
    }

    public DoxygenType endElement(DoxygenXMLParseOption option) {
        // trace("\tEvent:END_ELEMENT"  + " Name:"+ reader.getLocalName() );
        DoxygenType target = this;
        String tag = new String(option.reader.getName().getLocalPart());

        trace("endElement tag:" + tag + " this:"+ getTag());        
        if(tag.equals(getTag()) != true) {
            endSubElementInternal(tag);
            return target;
        }

        endThisElementInternal(tag);

        target = target.getParent();
        return target;
    }

    protected void endThisElementInternal(String tag) {
        return;
    }

    protected void endSubElementInternal(String tag) {

    }

    public void linkObject() {
        deleteSameNameObject();

        for(DoxygenType child : getChildlen()) {
            if(child.getId().length() < 1) {
                child.linkObject();
            }
        }

        linkObjectInternal();

        return;
    }

    protected void linkObjectInternal() {
        return;
    }

    protected void deleteSameNameObject() {
        if(getId().length() < 1 || getTag().length() < 1 ) {
            return;
        }

        List<DoxygenType> names = manager_.getObjectByName(getTag(), getName());
        if( names.size() < 2) {
            return;
        }
        
        DoxygenType onlyType = null;

        for(int index = 0; index < names.size(); index++) {
            DoxygenType type = names.get(index);
            DoxygenType parent = type.getLastParent();

            if(parent instanceof DoxygenTypeGroup) {
                onlyType = type;
                break;
            }

            if(parent instanceof DoxygenTypeFile) {
                if(type.getName().endsWith(".h") == true) {
                    onlyType = type;
                }
            }
        }

        if(onlyType != null) {
            names.remove(onlyType);
        } else {
            onlyType = names.remove(0);
        }

        for(DoxygenType type : names) {
            debug(type.getName() + "(" + type.getId() + ") is duplicate. so delete remain " 
                + onlyType.getName() + "(" + onlyType.getId() + ")");
            manager_.removeList(type);
        }

        return;
    }


    protected DoxygenType getLastParent() {
        DoxygenType parent = this;
        while(parent != null ) {
            DoxygenType pparent = parent.getParent();
            if(pparent == null) {
                break;
            }
            parent = pparent;
        }

        return parent;
    }

    public void logoutdebug(int index) {        
        StringBuffer logbuffer = new StringBuffer();
        for(int count=0; count < index; count++) {
            logbuffer.append("\t");
        }

        logbuffer.append( clazz_.getSimpleName()
            + ",id:"+ getId()
            + ",Name:" + (getName() != null ? getName() : "none" )
            + ",Type:" + (getType() != null ? getType() : "none" )
            + ",Text:" + getText()
            + ",Brief:" + getBriefdescription()
            + ",Detail:" + getDetaileddescription()
            + ",Inbody:" + getInbodydescription() );

        debugoutInternal(logbuffer);
        info(logbuffer.toString());
        
        for(DoxygenType child : getChildlen()) {
            child.logoutdebug(index+1);
        }
    }

    protected void debugoutInternal(StringBuffer logbuffer) {
        return;
    }


    protected void append(StringBuffer menber, String text) {
        String value = text.replaceAll("\\r\\n|\\r|\\n", "");
        menber.append(value);
    }


}

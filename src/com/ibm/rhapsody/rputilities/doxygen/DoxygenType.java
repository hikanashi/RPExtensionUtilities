package com.ibm.rhapsody.rputilities.doxygen;


import java.util.List;
import java.util.ArrayList;

import javax.xml.stream.XMLStreamReader;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;

public abstract class DoxygenType extends ARPObject {
    protected DoxygenObjectManager manager_ = null;
    protected DoxygenType parent_ = null;
    protected List<DoxygenType> children_ = new ArrayList<DoxygenType>();
    protected String id_ = null;
    protected String kind_ = null;
    protected String tag_ = null;
    protected StringBuffer type_ = new StringBuffer(); 
    protected StringBuffer name_ = new StringBuffer(); 
    protected StringBuffer text_ = new StringBuffer(); 
    protected StringBuffer briefdescription_ = new StringBuffer(); 
    protected StringBuffer detaileddescription_ = new StringBuffer();
    protected StringBuffer inbodydescription_ = new StringBuffer();
    protected int indent_ = 0;

    
    protected DoxygenType(Class<?> clazz) {
        super(clazz);
    }

    public void setIndent(int indent) {
        indent_ = indent;
    }
    
    public int getIndent() {
        return indent_;
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
        if( id_ != null) {
            return id_;
        } else {
            return "";
        }
    }
    
    public String getKind() {
        if( kind_ != null) {
            return kind_;
        } else {
            return "";
        }
    }

    public String getTag() {
        if( tag_ != null) {
            return tag_;
        } else {
            return "";
        }
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

    abstract protected DoxygenType createElementInternal(XMLStreamReader reader, String tag);

    public DoxygenType createElement(XMLStreamReader reader, String tag) {
        DoxygenType target = this;

        // trace("\tEvent:START_ELEMENT"  + " Name:"+ reader.getLocalName() );
        // for(int index = 0; index < reader.getAttributeCount(); index++ ) {
        //     trace("\t\t Name:" + reader.getAttributeName(index) 
        //         + " Type:"+ reader.getAttributeType(index) 
        //         + " Value:" + reader.getAttributeValue(index));
        // }

        tag_ = tag;

        id_ = reader.getAttributeValue(null, "id");
        kind_ = reader.getAttributeValue(null, "kind");
        target = createElementInternal(reader, tag);
        return target;
    }

    abstract protected DoxygenType startElementInternal(XMLStreamReader reader, String tag);

    public DoxygenType startElement(XMLStreamReader reader, String tag) {
        // trace("\tEvent:START_ELEMENT"  + " Name:"+ reader.getLocalName() );
        // for(int index = 0; index < reader.getAttributeCount(); index++ ) {
        //     trace("\t\t Name:" + reader.getAttributeName(index) 
        //         + " Type:"+ reader.getAttributeType(index) 
        //         + " Value:" + reader.getAttributeValue(index));
        // }

        DoxygenType target = this;
        target = startElementInternal(reader, tag);

        return target;
    }

    abstract protected DoxygenType charactersInternal(String tag, String text);

    public DoxygenType characters(XMLStreamReader reader, String tag) {
        DoxygenType target = this;
        if(tag == null) {        
            return target;
        }
        
        // trace("\tEvent:CHARACTERS"  + " Tag:" + tag + " Text:"+ reader.getText() );
        String text = new String(reader.getText()).trim();
        if(tag.equals(getTag())) {
            text_.append(text);
        }
        else if(tag.equals("name")) {
            append(name_,text);
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
            target = charactersInternal(tag, text);
        }

        return target;
    }

    abstract protected DoxygenType endElementInternal(String tag);

    public DoxygenType endElement(XMLStreamReader reader) {
        // trace("\tEvent:END_ELEMENT"  + " Name:"+ reader.getLocalName() );
        DoxygenType target = this;
        String tag = reader.getName().getLocalPart();
        
        target = endElementInternal(tag);

        if(tag.equals(getTag()) != true) {
            return target;
        }

        target = target.getParent();
        return target;
    }

    protected void linkObject() {
        return;
    }

    abstract protected void debugoutInternal(StringBuffer logbuffer);

    public void debugout(int index) {        
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
        debug(logbuffer.toString());
        
        for(DoxygenType child : getChildlen()) {
            child.debugout(index+1);
        }
    }

    protected void append(StringBuffer menber, String text) {
        String value = text.replaceAll("\\r\\n|\\r|\\n", "");
        menber.append(value);
    }

}

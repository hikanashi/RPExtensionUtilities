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
    protected String tag_ = null;
    protected StringBuffer type_ = new StringBuffer(); 
    protected StringBuffer name_ = new StringBuffer(); 
    protected StringBuffer text_ = new StringBuffer(); 
    protected StringBuffer briefdescription_ = new StringBuffer(); 
    protected StringBuffer detaileddescription_ = new StringBuffer();
    protected StringBuffer inbodydescription_ = new StringBuffer();

    
    protected DoxygenType(Class<?> clazz) {
        super(clazz);
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

    public void setParent(DoxygenType parent) {
        if(parent == null ) {
            return;
        }

        parent_ = parent;
        parent.children_.add(this);
    }

    public String getId() {
        return id_;
    }

    public String getTag() {
        return tag_;
    }

    public String getText() {
        return text_.toString();
    }

    public String getType() {
        return type_.toString();
    }

    public String getName() {
        return name_.toString();
    }

    public String getBriefdescription() {
        return briefdescription_.toString();
    }

    public String getDetaileddescription() {
        return detaileddescription_.toString();
    }

    public String getInbodydescription() {
        return inbodydescription_.toString();
    }

    abstract protected DoxygenType createElementInternal(XMLStreamReader reader, String tag);
    abstract protected DoxygenType startElementInternal(XMLStreamReader reader, String tag);
    abstract protected DoxygenType charactersInternal(String tag, String text);
    abstract protected DoxygenType endElementInternal(String tag);
    abstract protected void debugoutInternal(StringBuffer logbuffer);

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

        target = createElementInternal(reader, tag);

        return target;
    }

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

    protected void append(StringBuffer menber, String text) {
        String value = text.replaceAll("\\r\\n|\\r|\\n", "");
        menber.append(value);
    }

    public DoxygenType endElement(XMLStreamReader reader) {
        // trace("\tEvent:END_ELEMENT"  + " Name:"+ reader.getLocalName() );
        DoxygenType target = this;
        String tag = reader.getName().getLocalPart();
        
        target = endElementInternal(tag);

        if(tag.equals(getTag()) != true) {
            return target;
        }

        for(DoxygenType child : getChildlen()) {
            if( child instanceof DoxygenTypeRef) {
                type_.append(" " + child.getText());
            }
        }

        target = target.getParent();
        return target;
    }

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
}

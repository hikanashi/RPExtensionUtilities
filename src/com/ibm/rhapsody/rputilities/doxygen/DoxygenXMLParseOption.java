package com.ibm.rhapsody.rputilities.doxygen;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamReader;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;

public class DoxygenXMLParseOption extends ARPObject {
    public XMLStreamReader reader;
    public int eventType;
    public DoxygenType parent = null;
    protected List<StringBuffer> taglist_ = new ArrayList<StringBuffer>();

    public DoxygenXMLParseOption() {
        super(DoxygenXMLParseOption.class);
    }

    public String getCurrentTag() {
        if(taglist_.size() > 0) {
            return taglist_.get(taglist_.size()-1).toString();
        }

        return "";
    }

    public String getBeforetTag() {
        if(taglist_.size() > 1) {
            return taglist_.get(taglist_.size()-2).toString();
        }

        return "";
        
    }

    public int getIndent() {
        return taglist_.size();
    }


    public void startElement( String tag ) {
        taglist_.add(new StringBuffer(tag));
    }

    public void endElement() {
        if(taglist_.size() > 0 ) { 
            taglist_.remove(taglist_.size()-1);
        }
    }

    public void endDocument() {
        parent = null;
    }
}

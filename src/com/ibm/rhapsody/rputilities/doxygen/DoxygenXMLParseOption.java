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
        int index = taglist_.size() - 1;
        if (index > 0) {
            String tag = taglist_.get(index).toString();
            return tag;
        }

        return "";
    }

    public String getCurrentTagWithoutPara() {
        for (int index = taglist_.size() - 1; index > 0; index--) {
            String tag = taglist_.get(index).toString();
            if (tag.equals("para") == true) {
                continue;
            }
            return tag;
        }
        return "";
    }

    public String getBeforeTagWithoutPara() {
        String currentTag = null;
        for (int index = taglist_.size() - 1; index > 0; index--) {
            String tag = taglist_.get(index).toString();
            if (tag.equals("para") == true) {
                continue;
            }

            if (currentTag == null) {
                currentTag = tag;
                continue;
            }

            return tag;
        }
        return "";
    }

    public int getIndent() {
        return taglist_.size();
    }

    public void startElement(String tag) {
        String starttag = "";
        if (tag != null) {
            starttag = tag;
        }

        trace("startElement:" + starttag + " indent:" + taglist_.size());
        taglist_.add(new StringBuffer(starttag));
    }

    public void endElement(String tag) {
        String endtag = "";
        if (tag != null) {
            endtag = tag;
        }

        trace("endElement:" + endtag + " indent:" + taglist_.size());
        while (taglist_.size() > 0) {
            StringBuffer deltag = taglist_.remove(taglist_.size() - 1);
            if (endtag.equals(deltag.toString()) == true) {
                break;
            }
        }
    }

    public void endDocument() {
        parent = null;
    }
}

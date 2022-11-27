package com.ibm.rhapsody.rputilities.doxygen;

import javax.xml.stream.XMLStreamReader;

public class DoxygenXMLParseOption {
    public XMLStreamReader reader;
    public int eventType;
    public DoxygenType parent = null;
    public StringBuffer breforettag = new StringBuffer();
    public StringBuffer currenttag = new StringBuffer();
    public Integer indent = 0;

    public void startElement( String tag ) {
        if(breforettag.length() > 0) {
            breforettag.delete(0,breforettag.length());
        }
        breforettag.append(currenttag);

        if(currenttag.length() > 0) {
            currenttag.delete(0,currenttag.length());
        }
        currenttag.append(tag);
        indent++;
    }

    public void endElement() {
        indent--;
    }

    public void endDocument() {
        parent = null;
        currenttag = null;
        indent = 0;
    }
}

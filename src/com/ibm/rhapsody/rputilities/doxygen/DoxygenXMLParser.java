package com.ibm.rhapsody.rputilities.doxygen;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import java.io.FileInputStream;
import java.nio.file.Paths;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class DoxygenXMLParser extends ARPObject {
    protected DoxygenObjectManager manager_ = new DoxygenObjectManager();

    public DoxygenXMLParser() 
    {
        super(DoxygenXMLParser.class);
    }

    public DoxygenObjectManager getManager() {
        return manager_;
    }


    public boolean Parse(String xsltPath, String sourceTreePath, String outputPath) 
    {
        boolean result = false;

		try {
            result = Transform(xsltPath, sourceTreePath, outputPath); 
            if( result != true ) {
                return false;
            }
    
            result = XmlParse(outputPath);
            if(result != true) {
                return false;
            }
        } 
        catch(Exception e) {
            error("Parse Error" + sourceTreePath , e);
            result = false;
		} finally {
            RPFileSystem fileSystem = new RPFileSystem();
            result = fileSystem.Delete(outputPath);
		}

        return result;
    }

    public boolean Transform(String xsltPath, String sourceTreePath, String outputPath) 
    {
        info("Transform xslt" + xsltPath + " source:" + sourceTreePath + " output:" + outputPath);

        try {
            RPFileSystem filesystem = new RPFileSystem();
            
            if(filesystem.IsReadable(xsltPath) != true) {
                error("Path[" + xsltPath + "] can't read. so check permission.");
                return false;
            }

            if(filesystem.IsReadable(sourceTreePath) != true) {
                error("Path[" + sourceTreePath + "] can't read. so check permission.");
                return false;
            }

            if(filesystem.isExists(outputPath) == true) {
                error("Path[" + outputPath + "] is exist. so delete file.");
                return false;
            }

            FileOutputStream outputStream = new FileOutputStream(outputPath);
            // XSLT Stream Source
            StreamSource xsltSource = new StreamSource(xsltPath);
            // Stream sources in the source tree
            StreamSource sourceTree  = new StreamSource(sourceTreePath);
            // Stream Results in the Results Tree
            StreamResult resultStream  = new StreamResult(outputStream);
            
            // XSLT processor factory generation
            TransformerFactory tFactory = TransformerFactory.newInstance();
            // Generate XSLT processor
            Transformer transformer = tFactory.newTransformer(xsltSource);
            transformer.setOutputProperty("indent","yes");
            // Conversion and output of result tree
            transformer.transform(sourceTree, resultStream);

            outputStream.close();
        } 
        catch (Exception e) {
            error("Transform Error xslt:" + xsltPath 
                + " sourceTree:" + sourceTreePath
                + " output:" + outputPath ,e);
            return false;
        }

        return true;
    }

    public boolean XmlParse(String xmlPath) 
    {
        RPFileSystem filesystem = new RPFileSystem();
            
        if(filesystem.IsReadable(xmlPath) != true) {
            error("Path[" + xmlPath + "] can't read. so check permission.");
            return false;
        }

        info("XmlParse Start:" + xmlPath);

        XMLStreamReader reader = null;
        boolean result = false;

		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
            FileInputStream inputStream = new FileInputStream(Paths.get(xmlPath).toFile());
			reader = factory.createXMLStreamReader(inputStream);

            DoxygenType target = null;
            StringBuffer lasttag = new StringBuffer();
			while (reader.hasNext()) {
                int eventType = reader.next();
                target = parseNode(reader, eventType, target, lasttag);
			}

            debug("XmlParse finish");

            result = true;
        } 
        catch(Exception e) {
            error("XmlParse Error" + xmlPath , e);
            result = false;
		} finally {
            try {
                if (reader != null) {
                    debug("close XMLStreamReader");
                    reader.close();
                }
            }
            catch(Exception e) {
                error("XMLStreamReader close Error" + xmlPath , e);
                result = false;
            }
		}

        return result;
    }

    protected DoxygenType parseNode(XMLStreamReader reader, int eventType, DoxygenType parent, StringBuffer lasttag) {
        DoxygenType target = parent;

        switch (eventType) {
        case XMLStreamConstants.START_ELEMENT:
            DoxygenType createTarget = null;
            if(lasttag.length() > 0) {
                lasttag.delete(0,lasttag.length());
            }
            lasttag.append(reader.getName().getLocalPart());

            createTarget = CreateNode(lasttag.toString(), reader, target);
            if(createTarget != null ) { 
                target = createTarget.createElement(reader, lasttag.toString());
            }
            else if(target != null) {
                target = target.startElement(reader, lasttag.toString());
            }

            if( createTarget != null ) {
                manager_.append(createTarget);
            }
            break;
        case XMLStreamConstants.CHARACTERS:
            if(target != null) {
                target = target.characters(reader, lasttag.toString());
            }
            break;
        case XMLStreamConstants.END_ELEMENT:
            if(target != null) {
                target = target.endElement(reader);
            }
            break;
        case XMLStreamConstants.END_DOCUMENT:
            debug("End Document");
            lasttag = null;
            target = null;
            break;
        default:
            warn("\tUnknown Event:" + eventType);
            break;
        }

        return target;
    }

    protected DoxygenType CreateNode(String tag, XMLStreamReader reader, DoxygenType parent) {

        DoxygenType typeobj = null;

        if (tag.equals("memberdef")) {
            String kind = reader.getAttributeValue(null, "kind");

            if(kind.equals("define")) {
                typeobj = new DoxygenTypeDefilne();
            }
            else if(kind.equals("enum")) {
                typeobj = new DoxygenTypeEnum();
            }
            else if(kind.equals("function")) {
                typeobj = new DoxygenTypeFunction();
            }
            else if(kind.equals("typedef")) {
                typeobj = new DoxygenTypeTypedef();
            }
            else if(kind.equals("variable")) {
                typeobj = new DoxygenTypeVariable();
            }
        }
        else if(tag.equals("param")) {
            if(parent instanceof DoxygenTypeFunction) {
                typeobj = new DoxygenTypeParam();
            }
        }
        else if(tag.equals("enumvalue")) {
            if(parent instanceof DoxygenTypeEnum) {
                typeobj = new DoxygenTypeEnumValue();
            }
        }
        else if(tag.equals("ref")) {
            if(parent != null) {
                typeobj = new DoxygenTypeRef();
            }
        }
        else if(tag.equals("compounddef")) {
            typeobj = new DoxygenTypeCompound();
        }

        if(typeobj != null) {
            typeobj.setManager(manager_);
            typeobj.setParent(parent);
        }

        return typeobj;
    }
}

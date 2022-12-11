package com.ibm.rhapsody.rputilities.doxygen;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.ibm.rhapsody.rputilities.rpcore.RPFileSystem;

import java.io.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;

import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;

public class DoxygenXMLParser extends ARPObject {
    protected DoxygenObjectManager manager_ = new DoxygenObjectManager();

    public DoxygenXMLParser() 
    {
        super(DoxygenXMLParser.class);
    }

    public DoxygenObjectManager getManager() {
        return manager_;
    }

    public DoxygenObjectManager Parse(String doxygenPath) {
        debugMemory("Start Parse");
 
        String xsltPhath = doxygenPath + "\\combine.xslt";
        String sourcePath = doxygenPath+ "\\index.xml";

        String formatNowDate = RPFileSystem.CreateDateTimeString(null);
        String resultPath = RPFileSystem.getActiveProjectPath() + "\\result_" + formatNowDate + ".xml";

        boolean result = ParseInternal(xsltPhath, sourcePath, resultPath);
        if(result != true) {
            return null;
        }

        info("ParseXML success:" + getManager().size());

        return getManager();
    }

    protected boolean ParseInternal(String xsltPath, String sourceTreePath, String outputPath) {
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

    protected boolean Transform(String xsltPath, String sourceTreePath, String outputPath) 
    {
        info("Transform xslt:" + xsltPath + " source:" + sourceTreePath + " output:" + outputPath);

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

    protected boolean XmlParse(String xmlPath) 
    {
        RPFileSystem filesystem = new RPFileSystem();
            
        if(filesystem.IsReadable(xmlPath) != true) {
            error("Path[" + xmlPath + "] can't read. so check permission.");
            return false;
        }

        info("XmlParse Start:" + xmlPath);

        DoxygenXMLParseOption option = new DoxygenXMLParseOption();
        boolean result = false;

		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
            FileInputStream inputStream = new FileInputStream(Paths.get(xmlPath).toFile());

			option.reader = factory.createXMLStreamReader(inputStream);

            while (option.reader.hasNext()) {
                option.eventType = option.reader.next();
                option.parent =  parseNode(option);
            }

            debug("XmlParse finish");

            List<DoxygenType> lists = manager_.getAllType();
            debug("Link reference:" + lists.size());
            for(DoxygenType type : lists) {
                type.linkObject();
            }
            lists = null;

            result = true;
        } 
        catch(Exception e) {
            error("XmlParse Error" + xmlPath , e);
            result = false;
		} finally {
            try {
                if (option.reader != null) {
                    debug("close XMLStreamReader");
                    option.reader.close();
                }
                option = null;
            }
            catch(Exception e) {
                error("XMLStreamReader close Error" + xmlPath , e);
                result = false;
            }
		}

        return result;
    }

    protected DoxygenType parseNode(DoxygenXMLParseOption option) {
        DoxygenType target = option.parent;

        switch (option.eventType) {
        case XMLStreamConstants.START_ELEMENT:
            option.startElement(option.reader.getName().getLocalPart());

            DoxygenType createTarget = null;
            createTarget = CreateNode(option);
            if(createTarget != null ) { 
                target = createTarget.createElement(option);
            }
            else if(target != null) {
                target = target.startSubElement(option);
            }

            if( createTarget != null ) {
                manager_.append(createTarget);
            }
            break;
        case XMLStreamConstants.CHARACTERS:
            if(target != null) {
                target = target.characters(option);
            }
            break;
        case XMLStreamConstants.END_ELEMENT:
            if(target != null) {
                target = target.endElement(option);
            }
            option.endElement(option.reader.getName().getLocalPart());
            break;
        case XMLStreamConstants.END_DOCUMENT:
            debug("End Document");
            option.endDocument();
            break;
        default:
            warn("\tUnknown Event:" + option.eventType);
            break;
        }

        return target;
    }

    protected DoxygenType CreateNode(DoxygenXMLParseOption option) {

        DoxygenType typeobj = null;
        
        for (TAGTYPE type : TAGTYPE.values()) {
            if(type.getTag().equals(option.getCurrentTag()) != true ) {
                continue;
            }

            if( option.parent != null ) {
                if( option.parent.isCreateChildlen(type,option) != true ) {
                    continue;
                }
            } else {
                if(type.isNeedParent()) {
                    continue;
                }    
            }

            if(type.getKeytype() == TAGTYPE.KEYTYPE.KEY_ATTR_KIND) {
                String attrvalue = option.reader.getAttributeValue(null, type.getAttrName());
                if(type.getAttrValue().equals(attrvalue)) {
                    typeobj = type.newDoxygenInstance();
                    break;
                }
            }
            else {
                typeobj = type.newDoxygenInstance();
                break;
            }
        };

        
        if(typeobj == null) {
            return typeobj;
        }

        typeobj.setManager(manager_);
        typeobj.setParent(option.parent);

        return typeobj;
    }
}

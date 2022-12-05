package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenObjectManager;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeTypedef;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.ARPBridge;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.RPBridgeDefine;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.RPBridgeEnum;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.RPBridgeEvent;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.RPBridgeStateChart;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.RPBridgeStruct;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.RPBridgeTypedef;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.RPBridgeUnion;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;

public class RPFunctionImporter extends ARPObject {
    protected final int ELEMENT_IMPORT_LIMIT = 1000;

    public RPFunctionImporter() {
        super(RPFunctionImporter.class);
    }

    public boolean importModel(IRPPackage rootPackage, DoxygenObjectManager manager,String currentVersion) {
        boolean result = false;

        // debugMemory("Start Define");
        // result = importModel(rootPackage, manager, currentVersion, TAGTYPE.DEFINE);
        // if(result != true ) {
        //     return result;
        // }

        debugMemory("Start Enum");
        result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.ENUM);
        if(result != true ) {
            return result;
        }

        debugMemory("Start Union");
        result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.UNION);
        if(result != true ) {
            return result;
        }

        debugMemory("Start Struct");
        result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.STRUCT);
        if(result != true ) {
            return result;
        }

        debugMemory("Start Typedef");
        result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.TYPEDEF);
        if(result != true ) {
            return result;
        }

        debugMemory("Start Function");
        result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.FUNCTION);
        if(result != true ) {
            return result;
        }

        debugMemory("importModel Finish");
        info("importModel Finish");
        return result;
    }

    protected boolean importModelbyType(IRPPackage rootPackage, DoxygenObjectManager manager,String currentVersion,TAGTYPE tagtype) {
        if(rootPackage == null || manager == null || currentVersion == null || tagtype == null) {
            error(String.format("importModel argument is illegal. package:%s manager:%d version:%s type:%s",
                    (rootPackage != null ? rootPackage.getName() : "null"),
                    (manager != null ? manager.size() : "-1"),
                    (currentVersion != null ? currentVersion : "null"),
                    (tagtype != null ? tagtype.getClass().getName() : "null")));
            return false;
        }

        List <DoxygenType> list = null;

        list = manager.popList(tagtype);
        if(list == null) {
            return false;
        }

        info(String.format("importModel tag:%s/%s(%s) count:%d",
                tagtype.getTag(),
                tagtype.getAttrName(),
                tagtype.getAttrValue(),
                list.size()));

        int index = 0;
        for(DoxygenType obj : list) {
            debug(String.format("importModel tag:%s/%s(%s) type:%s name:%s",
                    tagtype.getTag(),
                    tagtype.getAttrName(),
                    tagtype.getAttrValue(),
                    obj.getType(),
                    obj.getName()));

            ARPBridge bridge = newBridgeInstance(obj, rootPackage, tagtype);
            if(bridge == null) {
                continue;
            }

            IRPModelElement element = bridge.importElement(currentVersion);
            if(element == null) {
                return false;
            }

            if(++index >= ELEMENT_IMPORT_LIMIT) {
                warn(String.format("importModel tag:%s/%s(%s) is over the maximum number of imports(%d)",
                            tagtype.getTag(),
                            tagtype.getAttrName(),
                            tagtype.getAttrValue(),
                            ELEMENT_IMPORT_LIMIT));
                warn("Therefore, data beyond the maximum number will not be imported count:"
                    + (list.size() - ELEMENT_IMPORT_LIMIT));
                break;
            }
        }

        ARPBridge bridge = newBridgeInstance(null, rootPackage, tagtype);        
        bridge.replaceOldElement(currentVersion);

        // For typedefs other than callbacks
        if( tagtype == TAGTYPE.TYPEDEF ) {
            DoxygenTypeTypedef typedef = new DoxygenTypeTypedef();
            bridge = newBridgeInstance(typedef, rootPackage, tagtype);        
            bridge.replaceOldElement(currentVersion);
        }

        return true;
    }

    protected ARPBridge newBridgeInstance(DoxygenType doxygen, IRPPackage rootPackage, TAGTYPE tagtype) {
        ARPBridge bridge = null;

        if( tagtype == TAGTYPE.FUNCTION ) {
            bridge = new RPBridgeStateChart(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.ENUM ) {
            bridge = new RPBridgeEnum(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.DEFINE ) {
            bridge = new RPBridgeDefine(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.STRUCT ) {
            bridge = new RPBridgeStruct(doxygen,rootPackage);
            return bridge;
        }


        if( tagtype == TAGTYPE.UNION ) {
            bridge = new RPBridgeUnion(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.TYPEDEF ) {
            DoxygenTypeTypedef typedef = getObject(doxygen);
            if(typedef == null) {
                bridge = new RPBridgeEvent(doxygen, rootPackage);
            }
            // If it contains "(", it is a callback.
            else if(typedef.isCallback() == true) {
                bridge = new RPBridgeEvent(doxygen, rootPackage);
            } else {
                bridge = new RPBridgeTypedef(doxygen, rootPackage);
            }

            return bridge;
        }

        return bridge;
    }
}
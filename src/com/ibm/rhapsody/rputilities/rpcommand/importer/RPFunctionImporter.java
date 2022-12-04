package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenObjectManager;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeTypedef;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;

public class RPFunctionImporter extends ARPObject {
    protected final int ELEMENT_IMPORT_LIMIT = 1000;

    public RPFunctionImporter() {
        super(RPFunctionImporter.class);
    }

    protected boolean importModel(IRPPackage rootPackage, DoxygenObjectManager manager,String currentVersion,TAGTYPE tagtype) {
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

            if(++index > ELEMENT_IMPORT_LIMIT) {
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
            bridge = new RPStateChartBridge(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.ENUM ) {
            bridge = new RPEnumBridge(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.DEFINE ) {
            bridge = new RPDefineBridge(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.STRUCT ) {
            bridge = new RPStructBridge(doxygen,rootPackage);
            return bridge;
        }


        if( tagtype == TAGTYPE.UNION ) {
            bridge = new RPUnionBridge(doxygen,rootPackage);
            return bridge;
        }

        if( tagtype == TAGTYPE.TYPEDEF ) {
            DoxygenTypeTypedef typedef = getObject(doxygen);
            if(typedef == null) {
                bridge = new RPEventBridge(doxygen, rootPackage);
            }
            // If it contains "(", it is a callback.
            else if(typedef.isCallback() == true) {
                bridge = new RPEventBridge(doxygen, rootPackage);
            } else {
                bridge = new RPTypedefBridge(doxygen, rootPackage);
            }

            return bridge;
        }

        return bridge;
    }
}
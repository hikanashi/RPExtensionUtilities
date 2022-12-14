package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.List;

import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.*;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.DoxygenObjectManager;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type.DoxygenTypeTypedef;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPUnit;

public class RPFunctionImporter extends ARPObject {
    // protected static final int IMPORT_SAVE_CYCLE = 50;

    public RPFunctionImporter() {
        super(RPFunctionImporter.class);
    }

    public static boolean isImportTarget(IRPUnit rpelement) {
        if (rpelement == null) {
            return false;
        }

        if(rpelement.isReadOnly() == 1) {
            return false;
        }

        if(rpelement.isReferenceUnit() == 1) {
            return false;
        }

        for (IRPModelElement checkElement = rpelement; checkElement != null; checkElement = checkElement.getOwner()) {

            if (!(checkElement instanceof IRPPackage)) {
                continue;
            }

            IRPModelElement versiontag = checkElement.getTag(ARPBridge.TAG_VERSION_PACKAGE);
            if (versiontag == null) {
                continue;
            }

            return false;
        }

        return true;
    }

    public boolean importModel(IRPPackage rootPackage, DoxygenObjectManager manager, ImportOption option) {
        boolean result = false;
        String currentVersion = option.importVersion;

        if (option.importDefine == true) {
            debugMemory("Start Define");
            result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.DEFINE);
            if (result != true) {
                return result;
            }
        }

        if (option.importEnum == true) {
            debugMemory("Start Enum");
            result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.ENUM);
            if (result != true) {
                return result;
            }
        }

        if (option.importUnion == true) {
            debugMemory("Start Union");
            result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.UNION);
            if (result != true) {
                return result;
            }
        }

        if (option.importStruct == true) {
            debugMemory("Start Struct");
            result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.STRUCT);
            if (result != true) {
                return result;
            }
        }

        if (option.importTypedef == true) {
            debugMemory("Start Typedef");
            result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.TYPEDEF);
            if (result != true) {
                return result;
            }
        }

        if (option.importFunction == true) {
            debugMemory("Start Function");
            result = importModelbyType(rootPackage, manager, currentVersion, TAGTYPE.FUNCTION);
            if (result != true) {
                return result;
            }
        }

        debugMemory("importModel Finish");
        info("importModel Finish");
        return result;
    }

    protected boolean importModelbyType(IRPPackage rootPackage, DoxygenObjectManager manager, String currentVersion,
            TAGTYPE tagtype) {
        if (rootPackage == null || manager == null || currentVersion == null || tagtype == null) {
            error(String.format("importModel argument is illegal. package:%s manager:%d version:%s type:%s",
                    (rootPackage != null ? rootPackage.getName() : "null"),
                    (manager != null ? manager.size() : "-1"),
                    (currentVersion != null ? currentVersion : "null"),
                    (tagtype != null ? tagtype.getClass().getName() : "null")));
            return false;
        }

        List<DoxygenType> list = null;

        list = manager.popList(tagtype);
        if (list == null) {
            return false;
        }

        info(String.format("importModel tag:%s/%s(%s) count:%d",
                tagtype.getTag(),
                tagtype.getAttrName(),
                tagtype.getAttrValue(),
                list.size()));

        int index = 0;
        // int save_count = 0;
        for (DoxygenType obj : list) {
            info(String.format("importModel tag:%s/%s(%s) type:%s name:%s %d/%d",
                    tagtype.getTag(),
                    tagtype.getAttrName(),
                    tagtype.getAttrValue(),
                    obj.getType(),
                    obj.getName(),
                    index + 1, list.size()));

            ARPBridge bridge = newBridgeInstance(obj, rootPackage, tagtype);
            if (bridge == null) {
                continue;
            }

            IRPModelElement element = bridge.importElement(currentVersion);
            if (element == null) {
                return false;
            }

            // if (++save_count > IMPORT_SAVE_CYCLE) {
            //     rootPackage.save(1);
            //     save_count = 0;
            // }

            index++;
        }

        if (manager.isFullImport() != true) {
            rootPackage.save(1);
            return true;
        }

        ARPBridge bridge = newBridgeInstance(null, rootPackage, tagtype);
        bridge.replaceOldElement(currentVersion);

        // For typedefs other than callbacks
        if (tagtype == TAGTYPE.TYPEDEF) {
            DoxygenTypeTypedef typedef = new DoxygenTypeTypedef();
            bridge = newBridgeInstance(typedef, rootPackage, tagtype);
            bridge.replaceOldElement(currentVersion);
        }

        // For other Types used in arguments
        if (tagtype == TAGTYPE.FUNCTION) {
            bridge = newBridgeInstance(null, rootPackage, TAGTYPE.PARAM);
            bridge.replaceOldElement(currentVersion);
        }

        rootPackage.save(1);

        return true;
    }

    protected ARPBridge newBridgeInstance(DoxygenType doxygen, IRPPackage rootPackage, TAGTYPE tagtype) {
        ARPBridge bridge = null;

        if (tagtype == TAGTYPE.FUNCTION) {
            bridge = new RPBridgeStateChart(doxygen, rootPackage);
            // bridge = new RPBridgeOperation(doxygen, rootPackage);
            return bridge;
        }

        if (tagtype == TAGTYPE.ENUM) {
            bridge = new RPBridgeEnum(doxygen, rootPackage);
            return bridge;
        }

        if (tagtype == TAGTYPE.DEFINE) {
            bridge = new RPBridgeDefine(doxygen, rootPackage);
            return bridge;
        }

        if (tagtype == TAGTYPE.STRUCT) {
            bridge = new RPBridgeStruct(doxygen, rootPackage);
            return bridge;
        }

        if (tagtype == TAGTYPE.UNION) {
            bridge = new RPBridgeUnion(doxygen, rootPackage);
            return bridge;
        }

        if (tagtype == TAGTYPE.TYPEDEF) {
            DoxygenTypeTypedef typedef = getObject(doxygen);
            if (typedef == null) {
                bridge = new RPBridgeEvent(doxygen, rootPackage);
            }
            // If it contains "(", it is a callback.
            else if (typedef.isCallback() == true) {
                bridge = new RPBridgeEvent(doxygen, rootPackage);
            } else {
                bridge = new RPBridgeTypedef(doxygen, rootPackage);
            }

            return bridge;
        }

        if (tagtype == TAGTYPE.PARAM) {
            bridge = new RPBridgeParamType(doxygen, rootPackage);
            return bridge;
        }

        return bridge;
    }
}
package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcommand.importer.RPTYPE_KIND;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPBridgeTypedef extends ARPBridge {
    protected final String DECLARATION_PREFIX = "typedef";

    protected String name_ = null;
    protected RPTYPE_KIND kind_ = RPTYPE_KIND.TYPEDEF;

    public RPBridgeTypedef(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPBridgeTypedef.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if( doxygen == null ) {
            name_ = "";
            return;
        }

        name_ = convertAvailableName(kind_.getImplicitName(doxygen_.getName()));
    }

    protected String getName() {
        return name_;
    }
    
    protected String GetKind() {
        return kind_.getString();
    }

    protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage) {
        List<IRPModelElement> list = new ArrayList<>(toList(rpPackage.getTypes()));
        list.removeIf(element -> isTargetType(element) != true  );

        return list;
    }

    protected boolean isTargetType(IRPModelElement element) {
        IRPType rpType = getObject(element);
        if( rpType == null) {
            return false;
        }

        if( rpType.isKindTypedef() != 1) {
            return false;
        }

        return false;
    }

    public IRPModelElement findElementByType(IRPPackage rppackage) {
        IRPModelElement element = null;
        element = rppackage.findType(getName());
        return element;
    }


    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        debug("create " + kind_.getString() +":" + getName() + " in package:" + modulePackage.getName());
        IRPType rpType = null;
        try {
            rpType = modulePackage.addType(getName()); 
            setStereoType(rpType, STEREOTYPE_VALUETYPE);
        } catch (Exception e) {
            error("createElementByType Error name:" + getName(), e);
            doxygen_.logoutdebug(0);
        }
        return rpType;


    }

    public boolean isUpdate(IRPModelElement element) {
        IRPType rpType = getObject(element);

        if(rpType.getIsPredefined() != 0 ) {
            return false;
        }

        if(checkUpdate(getName(), rpType.getName()) == true) {
            trace(kind_.getString() +" change Name "+ rpType.getName() + "->" + getName());
            return true;
        }

        if(checkUpdate(GetKind(), rpType.getKind()) == true ) {
            trace(getName() + " change Kind "+ rpType.getKind() + "->" + GetKind());
            return true;
        }

        return false;
    }

    public void apply(IRPModelElement element, IRPPackage modulePackage, String currentVersion, boolean isupdate) {
        IRPType rpType = getObject(element);
        if(rpType.getIsPredefined() != 0 ) {
            return;
        }

        super.apply(element, modulePackage, currentVersion, isupdate);
    }


    public void applyByType(IRPModelElement element, String currentVersion, boolean isupdate) {
        IRPType rpType = getObject(element);

        if(checkUpdate(getName(), rpType.getDisplayName()) == true) {
            trace(getName() + " apply DisplayName "+ rpType.getDisplayName() + "->" + getName());
            rpType.setDisplayName(getName());
        }

        if(checkUpdate(getName(), rpType.getName()) == true) {
            trace(getName() + " apply Name "+ rpType.getName() + "->" + getName());
            rpType.setName(getName());
        }

        if(checkUpdate(GetKind(), rpType.getKind()) == true ) {
            trace(getName() + " change Kind "+ rpType.getKind() + "->" + GetKind());
            rpType.setKind(GetKind());
        }

        IRPType baseType = CreateType(doxygen_, currentVersion);
        if( baseType != null ) {
            rpType.setTypedefBaseType(baseType);
        }        

        return;
    }
}

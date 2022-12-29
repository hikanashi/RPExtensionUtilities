package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.rpcommand.importer.RPTYPE_KIND;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type.DoxygenType;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPBridgeUnion extends ARPBridge {
    protected String name_ = null;
    protected RPTYPE_KIND kind_ = RPTYPE_KIND.UNION;

    public RPBridgeUnion(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPBridgeUnion.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if (doxygen == null) {
            name_ = "";
            return;
        }

        name_ = convertAvailableName(kind_.getImplicitName(doxygen_.getQualifiedName()));
    }

    protected String getName() {
        return name_;
    }

    protected String GetKind() {
        return kind_.getString();
    }

    @Override
    protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage) {
        List<IRPModelElement> list = new ArrayList<>(toList(rpPackage.getTypes()));
        list.removeIf(element -> isTargetType(element) != true);

        return list;
    }

    protected boolean isTargetType(IRPModelElement element) {
        IRPType rpType = getObject(element);
        if (rpType == null) {
            return false;
        }

        if (rpType.isUnion() == 1) {
            return true;
        }

        return false;
    }

    @Override
    public IRPModelElement findElementByType(IRPPackage rppackage) {
        IRPModelElement element = null;
        element = rppackage.findType(getName());
        return element;
    }

    @Override
    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        debug("create " + kind_.getString() + ":" + getName() + " in package:" + modulePackage.getName());
        IRPType rpType = null;
        try {
            rpType = modulePackage.addType(getName());
            setStereoType(rpType, STEREOTYPE_DATATYPE);
        } catch (Exception e) {
            error("createElementByType Error name:" + getName(), e);
            doxygen_.logoutdebug(0);
        }
        return rpType;

    }

    @Override
    public boolean isUpdate(IRPModelElement element) {
        IRPType rpType = getObject(element);

        if (rpType.getIsPredefined() != 0) {
            return false;
        }

        if (checkUpdate(getName(), rpType.getName()) == true) {
            trace("Union change Name " + rpType.getName() + "->" + getName());
            return true;
        }

        // if (checkUpdate(GetKind(), rpType.getKind()) == true) {
        //     trace(getName() + " change Kind " + rpType.getKind() + "->" + GetKind());
        //     return true;
        // }

        return false;
    }

    @Override
    public void apply(IRPModelElement element, IRPPackage modulePackage, String currentVersion, boolean isupdate) {
        IRPType rpType = getObject(element);
        if (rpType.getIsPredefined() != 0) {
            return;
        }

        super.apply(element, modulePackage, currentVersion, isupdate);
    }

    @Override
    public void applyByType(IRPModelElement element, String currentVersion, boolean isupdate) {
        IRPType rpType = getObject(element);

        if (checkUpdate(getName(), rpType.getDisplayName()) == true) {
            trace(getName() + " apply DisplayName " + rpType.getDisplayName() + "->" + getName());
            rpType.setDisplayName(getName());
        }

        if (checkUpdate(getName(), rpType.getName()) == true) {
            trace(getName() + " apply Name " + rpType.getName() + "->" + getName());
            rpType.setName(getName());
        }

        if (checkUpdate(GetKind(), rpType.getKind()) == true) {
            trace(getName() + " change Kind " + rpType.getKind() + "->" + GetKind());
            rpType.setKind(GetKind());
        }

        List<IRPAttribute> attributes = toList(rpType.getAttributes());
        for (IRPAttribute attribute : attributes) {
            rpType.deleteAttribute(attribute);
        }

        List<DoxygenType> variables = doxygen_.getChildlen(TAGTYPE.VARIABLE);
        for (DoxygenType variable : variables) {
            IRPAttribute rpAttribute = rpType.addAttribute(variable.getName());
            applyStructMember(rpAttribute, variable, currentVersion);
        }

        return;
    }

    protected void applyStructMember(IRPAttribute rpAttribute, DoxygenType value, String currentVersion) {

        IRPType type = CreateType(value, currentVersion);
        if (type == null) {
            return;
        }

        rpAttribute.setType(type);
        return;
    }

}
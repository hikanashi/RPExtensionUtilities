package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.rpcommand.importer.RPTYPE_KIND;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type.DoxygenTypeDefilne;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPBridgeDefine extends ARPBridge {
    protected static final String DECLARATION_PREFIX = "#define";

    protected String name_ = null;
    protected RPTYPE_KIND kind_ = RPTYPE_KIND.LANG;

    public RPBridgeDefine(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPBridgeDefine.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if (doxygen == null) {
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

        if (rpType.isKindLanguage() != 1) {
            return false;
        }

        String declaration = rpType.getDeclaration();
        if (declaration.startsWith(DECLARATION_PREFIX) == true) {
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
        debug("create define:" + getName() + " in package:" + modulePackage.getName());
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

    @Override
    public boolean isUpdate(IRPModelElement element) {
        IRPType rpType = getObject(element);

        if (rpType.getIsPredefined() != 0) {
            return false;
        }

        if (checkUpdate(getName(), rpType.getName()) == true) {
            trace("define change Name " + rpType.getName() + "->" + getName());
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
            debug(getName() + " apply DisplayName " + rpType.getDisplayName() + "->" + getName());
            rpType.setDisplayName(getName());
        }

        if (checkUpdate(getName(), rpType.getName()) == true) {
            debug(getName() + " apply Name " + rpType.getName() + "->" + getName());
            rpType.setName(getName());
        }

        if (checkUpdate(GetKind(), rpType.getKind()) == true) {
            debug(getName() + " change Kind " + rpType.getKind() + "->" + GetKind());
            rpType.setKind(GetKind());
        }

        DoxygenTypeDefilne define = getObject(doxygen_);
        String declaration = String.format("%s\t%s\t%s",
                DECLARATION_PREFIX,
                getName(),
                define.getInitializer());

        rpType.setDeclaration(declaration);

        return;
    }
}

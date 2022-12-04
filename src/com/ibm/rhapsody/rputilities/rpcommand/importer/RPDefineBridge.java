package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeDefilne;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPDefineBridge extends ARPBridge {
    protected final String DECLARATION_PREFIX = "#define";

    String name_ = null;

    public RPDefineBridge(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPDefineBridge.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if( doxygen == null ) {
            name_ = "";
            return;
        }

        name_ = doxygen_.getName().replaceAll("@","impliedname");
    }

    protected String getName() {
        return name_;
    }
    
    protected String GetKind() {
        return "Language";
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

        if( rpType.isKindLanguage() != 1) {
            return false;
        }

        String declaration = rpType.getDeclaration();
        if( declaration.startsWith(DECLARATION_PREFIX) == true){
            return true;
        }


        return false;
    }

    public IRPModelElement findElementByType(IRPPackage rppackage) {
        IRPModelElement element = null;
        element = rppackage.findType(getName());
        return element;
    }


    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        debug("create Enum:" + getName() + " in package:" + modulePackage.getName());
        doxygen_.logoutdebug(0);
        IRPType rpType = modulePackage.addType(getName());   
        return rpType;
    }

    public boolean isUpdate(IRPModelElement element) {
        IRPType rpType = getObject(element);

        if(rpType.getIsPredefined() != 0 ) {
            return false;
        }

        if(getName().length() > 0 && getName().equals(rpType.getName()) != true) {
            trace("define change Name "+ rpType.getName() + "->" + getName());
            return true;
        }

        if(GetKind().equals(rpType.getKind()) != true ) {
            trace(getName() + " change Kind "+ rpType.getKind() + "->" + GetKind());
            return true;
        }

        return false;
    }

    public void apply(IRPModelElement element, IRPPackage modulePackage, String currentVersion) {
        IRPType rpType = getObject(element);
        if(rpType.getIsPredefined() != 0 ) {
            return;
        }

        super.apply(element, modulePackage, currentVersion);
    }


    public void applyByType(IRPModelElement element, String currentVersion) {
        IRPType rpType = getObject(element);

        if(getName().equals(rpType.getDisplayName()) != true) {
            trace(getName() + " apply DisplayName "+ rpType.getDisplayName() + "->" + getName());
            rpType.setDisplayName(getName());
        }

        if(getName().equals(rpType.getName()) != true && getName().length() > 0) {
            trace(getName() + " apply Name "+ rpType.getName() + "->" + getName());
            rpType.setName(getName());
        }

        if(GetKind().equals(rpType.getKind()) != true ) {
            trace(getName() + " change Kind "+ rpType.getKind() + "->" + GetKind());
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

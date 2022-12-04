package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenType;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPParamTypeBridge extends ARPBridge {
    protected String full_type_ = "";
    protected String type_ = "";
    protected String base_type_ = "";
    protected RPTYPE_KIND kind_ = RPTYPE_KIND.LANG;
    protected int isConstant_ = 0;
    protected int isReference_ = 0;

    public RPParamTypeBridge(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPParamTypeBridge.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    public String getFullType() {
        return full_type_;
    }

    public String getType() {
        return type_;
    }

    public String getBaseType() {
        return base_type_;
    }

    public String GetKind() {
        return kind_.getString();
    }

    public boolean isReference() {
        return (isReference_ == 1);
    }

    protected void initialize(DoxygenType doxygen) {
        if( doxygen == null ) {
            return;
        }
        
        full_type_ = doxygen.getType().trim();
        type_ = new String(full_type_);
        base_type_ = new String(full_type_);

        if(type_ .contains("struct")) {
            kind_ = RPTYPE_KIND.STRUCT;
            base_type_  = base_type_ .replaceAll("struct", "");
        }

        if(type_ .contains("const")) {
            kind_ = RPTYPE_KIND.TYPEDEF;
            isConstant_ = 1;
            base_type_  = base_type_ .replaceAll("const", "");
        }

        String patternPointer = "\\*";
        if(type_ .contains("*")) {
            kind_ = RPTYPE_KIND.TYPEDEF;
            isReference_ = 1;
            base_type_  = base_type_ .replaceAll(patternPointer, "");
        }

        String patternVariableArgument ="\\.\\.\\.";
        if(type_ .contains(patternVariableArgument)) {
            kind_ = RPTYPE_KIND.LANG;
            base_type_  = base_type_ .replaceAll(patternVariableArgument, "VariableArgument");
        }

        String patternIllegalCharcter = " |,|\\(|\\)";
        // if(type_ .matches(patternIllegalCharcter) ) {
        //     kind_ = RPTYPE_KIND.LANG;
        // }
        base_type_ = base_type_.trim();
        base_type_  = base_type_ .replaceAll(patternIllegalCharcter, "_");

        type_ = type_.trim();
        type_  = type_ .replaceAll(patternPointer, "pointer");
        type_  = type_ .replaceAll(patternVariableArgument, "VariableArgument");
        type_  = type_ .replaceAll(patternIllegalCharcter, "_");
    }
    
    protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage) {
        List<IRPModelElement> list = toList(rpPackage.getTypes());
        return list;
    }

    public IRPModelElement findElementByType(IRPPackage rppackage) {
        IRPModelElement element = null;
        // for predefine type
        if(isReference() == true ) {
            element = rppackage.findType(getBaseType() + " *");
        } else {
            element = rppackage.findType(getFullType()); 
        }

        // for user-defined type
        if(element == null) {
            element = rppackage.findType(getType());
        }

        return element;
    }

    public IRPType searchBaseType(IRPPackage rppackage) {

        if(getType().equals(getBaseType()) == true) {
            return null;
        }

        // for typedef base type
        IRPType basetype = rppackage.findType(getBaseType());

        if( basetype != null ) {
            return basetype;
        }

        List<IRPPackage> packages = toList(rppackage.getPackages());

        for(IRPPackage subPackage :  packages) {
            basetype = searchBaseType(subPackage);
            if(basetype != null) {
                return basetype;
            }
        }

        return null;
    }

    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        debug("create Type:" + getType() + " in package:" + modulePackage.getName());
        IRPType rpType = modulePackage.addType(getType());   
        return rpType;
    }

    public IRPType createBaseType(IRPPackage modulePackage, String version) {
        if(getType().equals(getBaseType()) == true) {
            return null;
        }
        debug("create BaseType:" + getBaseType() + " in package:" + modulePackage.getName());
        IRPType rpBaseType = modulePackage.addType(getBaseType());   
        setApplicableVersion(rpBaseType, version);  
        return rpBaseType;
    }


    public boolean isUpdate(IRPModelElement element) {
        IRPType rpType = getObject(element);

        if(rpType.getIsPredefined() != 0 ) {
            return false;
        }

        if(type_.length() > 0 && type_.equals(rpType.getName()) != true) {
            trace(full_type_ + " change Name "+ rpType.getName() + "->" + type_);
            return true;
        }

        if(GetKind().equals(rpType.getKind()) != true ) {
            trace(full_type_ + " change Kind "+ rpType.getKind() + "->" + GetKind());
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

        if(full_type_.equals(rpType.getDisplayName()) != true) {
            trace(full_type_ + " apply DisplayName "+ rpType.getDisplayName() + "->" + full_type_);
            rpType.setDisplayName(full_type_);
        }

        if(type_.equals(rpType.getName()) != true && type_.length() > 0) {
            trace(full_type_ + " apply Name "+ rpType.getName() + "->" + type_);
            rpType.setName(type_);
        }

        if(GetKind().equals(rpType.getKind()) != true ) {
            trace(full_type_ + " change Kind "+ rpType.getKind() + "->" + GetKind());
            rpType.setKind(GetKind());
        }

        switch(kind_) {
        case TYPEDEF:
            applyTypedef(rpType, currentVersion);
            break;
        case ENUM:
            // TODO : enum make
        case STRUCT:
            // TODO : struct make
        case UNION:
            // TODO : union make
        case LANG:
        default:
            break;            
        }

        return;
    }

    protected void applyTypedef(IRPType rpType, String currentVersion) {
        IRPType rpbasetype = null;

        if(getType().equals(getBaseType()) != true) {
            IRPPackage versionPackage = GetBaseVersionPackage(rpType);
            rpbasetype = searchBaseType(versionPackage);

            if( rpbasetype == null ) { 
                IRPPackage modulePackage = GetBaseVersionPackage(rpType);
                rpbasetype = createBaseType(modulePackage,currentVersion);
            }
        }

        String baseTypeName = "";
        if(rpbasetype != null ) {
            baseTypeName = rpbasetype.getName();
        }

        String myBaseName = "";
        if(rpType.getTypedefBaseType() != null ) {
            myBaseName = rpType.getTypedefBaseType().getName();
        }

        if(baseTypeName.equals(myBaseName) != true ) {
            trace(full_type_ + " apply BaseType "+ myBaseName + "->" + baseTypeName);
            rpType.setTypedefBaseType(rpbasetype);
        }

        if(isConstant_ != rpType.getIsTypedefConstant() ) {
            trace(full_type_+ " apply Constant "+ rpType.getIsTypedefConstant() + "->" + isConstant_);
            rpType.setIsTypedefConstant(isConstant_);
        }

        if(isReference_ != rpType.getIsTypedefReference() ) {
            trace(full_type_+ " apply Reference "+ rpType.getIsTypedefReference() + "->" + isReference_);
            rpType.setIsTypedefReference(isReference_);
        }
        return;
    }

}

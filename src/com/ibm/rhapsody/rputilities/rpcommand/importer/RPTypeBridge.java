package com.ibm.rhapsody.rputilities.rpcommand.importer;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPType;

public class RPTypeBridge extends ARPObject {
    protected String full_type_ = "";
    protected String type_ = "";
    protected String base_type_ = "";
    protected RPTYPE_KIND kind_ = RPTYPE_KIND.LANG;
    protected int isConstant_ = 0;
    protected int isReference_ = 0;

    public RPTypeBridge(DoxygenType value) {
        super(RPTypeBridge.class);
        initialize(value);
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

    protected void initialize(DoxygenType value) {
        full_type_ = value.getType().trim();
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

        if(type_ .contains("*")) {
            kind_ = RPTYPE_KIND.TYPEDEF;
            isReference_ = 1;
            base_type_  = base_type_ .replaceAll("\\*", "");
        }

        base_type_ = base_type_.trim();

        type_ = type_.trim();
        type_  = type_ .replaceAll(" ", "_");
        type_  = type_ .replaceAll("\\*", "pointer");
    }
    
    public boolean apply(IRPType rptype, IRPType rpbasetype) {
        boolean result = true;
        if(rptype.getIsPredefined() != 0 ) {
            return false;
        }

        if(full_type_.equals(rptype.getDisplayName()) != true) {
            debug(full_type_ + " change DisplayName "+ rptype.getDisplayName() + "->" + full_type_);
            rptype.setDisplayName(full_type_);
        }

        if(type_.equals(rptype.getName()) != true) {
            debug(full_type_ + " change Name "+ rptype.getName() + "->" + type_);
            rptype.setName(type_);
        }

        if(GetKind().equals(rptype.getKind()) != true ) {
            debug(full_type_ + " change Kind "+ rptype.getKind() + "->" + GetKind());
            rptype.setKind(GetKind());
        }

        switch(kind_) {
        case TYPEDEF:
            result = applyTypedef(rptype,rpbasetype);
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

        return result;
    }


    protected boolean applyTypedef(IRPType rptype, IRPType rpbasetype) {
        String baseTypeName = "";
        if(rpbasetype != null ) {
            baseTypeName = rpbasetype.getName();
        }

        String myBaseName = "";
        if(rptype.getTypedefBaseType() != null ) {
            myBaseName = rptype.getTypedefBaseType().getName();
        }

        if(baseTypeName.equals(myBaseName) != true ) {
            debug(full_type_ + " change BaseType "+ myBaseName + "->" + baseTypeName);
            rptype.setTypedefBaseType(rpbasetype);
        }

        if(isConstant_ != rptype.getIsTypedefConstant() ) {
            debug(full_type_+ " change Constant "+ rptype.getIsTypedefConstant() + "->" + isConstant_);
            rptype.setIsTypedefConstant(isConstant_);
        }

        if(isReference_ != rptype.getIsTypedefReference() ) {
            debug(full_type_+ " change Reference "+ rptype.getIsTypedefReference() + "->" + isReference_);
            rptype.setIsTypedefReference(isReference_);
        }

        return true;
    }

}

package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.rpcommand.importer.RPTYPE_KIND;
import com.telelogic.rhapsody.core.IRPAttribute;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPBridgeStruct extends ARPBridge {
    protected String name_ = null;
    protected RPTYPE_KIND kind_ = RPTYPE_KIND.STRUCT;

    public RPBridgeStruct(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPBridgeStruct.class, doxygen, rootPackage);
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

        if( rpType.isStruct() == 1) {
            return true;
        }

        return false;
    }

    public IRPModelElement findElementByType(IRPPackage rppackage) {
        IRPModelElement element = null;
        element = rppackage.findType(getName());
        // info("findElementByType:" + getName() + " in "+ rppackage.getName());
        return element;
    }


    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        debug("create " + kind_.getString() +":" + getName() + " in package:" + modulePackage.getName());
        IRPType rpType = null;
        try {
            rpType = modulePackage.addType(getName()); 
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

        if(getName().length() > 0 && getName().equals(rpType.getName()) != true) {
            trace("Struct change Name "+ rpType.getName() + "->" + getName());
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

        List<IRPAttribute> attributes = toList(rpType.getAttributes());
        for(IRPAttribute attribute : attributes) {
            rpType.deleteAttribute(attribute);
        }

        List<DoxygenType> variables = doxygen_.getChildlen(TAGTYPE.VARIABLE);
        for( DoxygenType variable : variables) {
            IRPAttribute rpAttribute = createAttribute(rpType, variable);
            applyStructMember(rpAttribute, variable, currentVersion);
        }

        return;
    }

    protected IRPAttribute createAttribute(IRPType rpType, DoxygenType value) {
 
        String attributeName = null;
        IRPAttribute rpAttribute = null;


        for(int index = 0; ;index++) {
            if(index == 0) {
                attributeName = value.getName();
            }
            else {
                attributeName = value.getName()+ Integer.toString(index);
            }

            rpAttribute = rpType.findAttribute(attributeName);
            if(rpAttribute == null) {
                break;
            }
        }

        rpAttribute = rpType.addAttribute(attributeName);
        return rpAttribute;
    }

    protected void applyStructMember(IRPAttribute rpAttribute, DoxygenType value, String currentVersion) {
 
        IRPType type = CreateType(value, currentVersion);
        if( type == null ) {
            return;
        }

        rpAttribute.setType(type);
        return;
    }
}

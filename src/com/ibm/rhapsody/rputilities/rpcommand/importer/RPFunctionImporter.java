package com.ibm.rhapsody.rputilities.rpcommand.importer;

import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeFunction;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeParam;
import com.ibm.rhapsody.rputilities.doxygen.DoxygenTypeTypedef;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPFunctionImporter extends ARPObject {

    public RPFunctionImporter() {
        super(RPFunctionImporter.class);
    }

    public boolean importAPI(IRPPackage rppackage, DoxygenTypeFunction function) {
        boolean result = false;

        if(rppackage == null) {
            error("Package: is null");
            return false;
        }

        if(function == null) {
            error("Package:"+ rppackage.getDisplayName() + " function is null");
            return false;
        }

        trace("Function is API:" + function.getType() + " " + function.getName());
        //result = importActivity(rppackage, function);
        result = true;

        return result;
    }

    public boolean importTypedef(IRPPackage rppackage, DoxygenTypeTypedef typedef) {
        boolean result = false;

        if(rppackage == null) {
            error("Package: is null");
            return false;
        }

        if(typedef == null) {
            error("Package:"+ rppackage.getDisplayName() + " typedef is null");
            return false;
        }

        // If it contains "(", it is a callback.
        if(typedef.isCallback()) {
            debug("typedef is Callback:" + typedef.getType()+ " " + typedef.getName());
            result = importEvent(rppackage, typedef);
        }
        else {
            debug("typedef is unkown:" + typedef.getType() + " " + typedef.getName());
            //result = importActivity(rppackage, function);
            result = true;
        }

        return result;
    }

    
    protected boolean importEvent(IRPPackage rppackage, DoxygenTypeTypedef typedef) {
        // function.debugout(0);
        IRPEvent rpEventNew = rppackage.addEvent(typedef.getName());
       
        List<DoxygenType> params = typedef.getChildlen(TAGTYPE.PARAM);
        for(DoxygenType value : params ) {
            DoxygenTypeParam param = getObject(value);
            IRPArgument	rpArgment = rpEventNew.addArgument(param.getName());
            rpArgment.setArgumentDirection(param.getDirection());

            IRPType type = SearchDataType(rppackage, param);
            if( type != null ) {
                rpArgment.setType(type);
            }
        }

        return true;
    }

    protected IRPType SearchDataType(IRPPackage rppackage, DoxygenType value) {
        IRPType type = null;
        IRPType basetype = null;

        RPTypeBridge rpbridge = new RPTypeBridge(value);

        // for predefine type
        if(rpbridge.isReference() == true ) {
            type = rppackage.findType(rpbridge.getBaseType() + " *");
        } else {
            type = rppackage.findType(rpbridge.getFullType()); 
        }

        // for user-defined type
        if(type == null) {
            type = rppackage.findType(rpbridge.getType());
        }

        // for typedef base type
        if(rpbridge.getType().equals(rpbridge.getBaseType()) != true) {
            basetype = rppackage.findType(rpbridge.getBaseType());

            if(basetype == null) {
                debug("add BaseType:"+ rpbridge.getBaseType());
                basetype = rppackage.addType(rpbridge.getBaseType());
            }
        }

        if(type == null) {
            debug("add Type:"+ rpbridge.getType());
            value.debugout(0);
            type = rppackage.addType(rpbridge.getType());
        }

        rpbridge.apply(type,basetype);
        return type;
    }

}

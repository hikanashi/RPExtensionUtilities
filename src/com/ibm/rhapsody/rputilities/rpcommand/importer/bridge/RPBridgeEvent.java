package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeParam;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeTypedef;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPEvent;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPBridgeEvent extends ARPBridge {
    protected String name_ = null;

    public RPBridgeEvent(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPBridgeEvent.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if( doxygen == null ) {
            return;
        }

        name_ = convertAvailableName(doxygen.getName());
    }

    protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage) {
        List<IRPModelElement> list = toList(rpPackage.getEvents());
        return list;
    }

    protected IRPModelElement findElementByType(IRPPackage rpPackage) {
        IRPModelElement rpEvent = rpPackage.findEvent(name_);
        return rpEvent;
    }


    protected IRPModelElement createElementByType(IRPPackage modulePackage) {
        // If it contains "(", it is a callback.
        DoxygenTypeTypedef typedef = getObject(doxygen_);
        if(typedef.isCallback() != true) {
            warn("typedef is unkown:" + typedef.getType() + " " + typedef.getName());
            return null;
        }

        debug("create Event:" + name_ + " in package:" + modulePackage.getName());
        IRPEvent rpEvent = null;
        try {
            rpEvent = modulePackage.addEvent(name_); 
        } catch (Exception e) {
            error("createElementByType Error name:" + name_, e);
            doxygen_.logoutdebug(0);
        }
        return rpEvent;
    }

    protected boolean isUpdate(IRPModelElement element) {
        IRPEvent rpevent = getObject(element);

        if(doxygen_.getName().equals(rpevent.getName()) != true) {
            trace("Event Name is change "+ rpevent.getName() + "->" + doxygen_.getName());
            return true;
        }

        List<IRPArgument> args = toList(rpevent.getArguments());
        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);

        if(args.size() != params.size()) {
            trace("Event:" + doxygen_.getName() 
                + " Argment count is change "+ args.size() + "->" + params.size());
            return true;
        }

        for(int index = 0; index < params.size(); index++){
            IRPArgument rpArgment = args.get(index);
            DoxygenType param = params.get(index);

            if(isUpdateArgment(doxygen_.getName(), rpArgment, param) == true ) {
                return true;
            }
        }

        return false;
    }

    protected boolean isUpdateArgment(String eventName, IRPArgument rpArgment, DoxygenType type) {
        DoxygenTypeParam param = getObject(type);

        if(param.getName().equals(rpArgment.getName()) != true) {
            trace(eventName + " Argment Name is change "+ rpArgment.getName() + "->" + param.getName());
            return true;
        }

        if(param.getDirection().equals(rpArgment.getArgumentDirection()) != true) {
            trace(eventName + " Direction is change "+ rpArgment.getArgumentDirection() + "->" + param.getDirection());
            return true;
        }

        IRPClassifier rpType = rpArgment.getType();
        String rpTypeName = "";
        if( rpType != null) {
            rpTypeName = rpType.getDisplayName();
        }

        if(rpTypeName.equals(param.getType()) != true ) {
            return true;
        }

        return false;
    }

    protected void applyByType(IRPModelElement element, String currentVersion) {
        IRPEvent rpevent = getObject(element);

        if(doxygen_.getName().equals(rpevent.getName()) != true) {
            trace("Event Name is apply "+ rpevent.getName() + "->" + doxygen_.getName());
            rpevent.setName(doxygen_.getName());
        }

        rpevent.setDescription(doxygen_.getBriefdescription());

        List<IRPArgument> args = toList(rpevent.getArguments());

        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);
        int argument_index = 0;
        int delete_count = 0;
        for(DoxygenType value : params ) {
            DoxygenTypeParam param = getObject(value);
            String argmentName = param.getName();
            int	find_index = findArgment(args, argument_index, argmentName);

            IRPArgument rpArgment = null;
            // argument is already exist
            if( find_index >= 0) {
                delete_count = find_index - argument_index - 1;
                deleteArgment(args, argument_index, delete_count);
                rpArgment =  args.get(argument_index);
                trace(String.format("Event:%s argment:%s delete(from:%d, to:%d)",
                        rpevent.getDisplayName() , 
                        rpArgment.getName(),
                        argument_index, delete_count));
            } 
            else if( argument_index < args.size()  ) {
                trace(String.format("Event:%s add(%s, %d)",
                        rpevent.getDisplayName() , argmentName, argument_index + 1));
                rpArgment = rpevent.addArgumentBeforePosition(argmentName, argument_index + 1);
            }
            else {
                trace(String.format("Event:%s add(%s, last)",
                        rpevent.getDisplayName() , argmentName));
                rpArgment = rpevent.addArgument(argmentName);
            }

            applyArgment(rpArgment, param, currentVersion);

            argument_index++;
        }

        delete_count = args.size() - argument_index;
        trace(String.format("Event:%s delete(from:%d, to:%d)",
                rpevent.getDisplayName() , 
                argument_index, delete_count));
        deleteArgment(args, argument_index, args.size() - argument_index);

        return;
    }

    protected int findArgment(List<IRPArgument> args, int startindex, String key) {
        trace(String.format("\targs:%d start:%d key:%s", 
                args.size(), startindex, key));

        for(int index = startindex; index < args.size(); index++){
            IRPArgument rpArgment = args.get(index);
            if( key.equals(rpArgment.getName()) == true ) {
                return index;
            }
        }
        return -1;
    }

    protected void deleteArgment(List<IRPArgument> args, int startindex, int deletenumber ) {
        if(deletenumber <= 0) {
            return;
        }

        for(int index = startindex; index < deletenumber; index++){
            IRPArgument rpArgment = args.get(startindex);
            trace("\tdelete argment:"+ rpArgment.getName());
            rpArgment.deleteFromProject();
        }
        
        return;
    }

    protected void applyArgment(IRPArgument rpArgment, DoxygenTypeParam param, String currentVersion) {
        rpArgment.setArgumentDirection(param.getDirection());
        rpArgment.setDescription(param.getBriefdescription());

        IRPType type = CreateType(param, currentVersion);
        if( type != null ) {
            rpArgment.setType(type);
        }
        return;
    }

}
package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeParam;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPOperationBridge extends ARPBridge {
    protected String name_ = null;

    public RPOperationBridge(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPOperationBridge.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if( doxygen == null ) {
            return;
        }
        
        name_ = doxygen.getName();
    }

    protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage) {
        List<IRPModelElement> list = toList(rpPackage.getGlobalFunctions());
        return list;
    }

    public IRPModelElement findElementByType(IRPPackage rppackage) {
        IRPModelElement rpOperation = rppackage.findGlobalFunction(name_);
        return rpOperation;
    }

    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        debug("create Operation:" + name_ + " in package:" + modulePackage.getName());

        IRPOperation rpOperation = modulePackage.addGlobalFunction(name_); 
        return rpOperation;
    }

    protected void updateOwner(IRPModelElement element, IRPPackage modulePackage) {
        List<Object> functions = toList(modulePackage.getGlobalFunctions());

        // TODO: Fixed problem with package not being replaced
        IRPOperation rpTmpOperation = null;
        IRPModelElement rpOwner = null;
        if( functions.size() < 1) {
            rpTmpOperation = modulePackage.addGlobalFunction("tmp_____");
            rpOwner = rpTmpOperation.getOwner();
        } else {
            IRPModelElement rpCurrent = getObject(functions.get(0));
            rpOwner = rpCurrent.getOwner();
        }

        super.updateOwner(element, rpOwner);

        // IRPPackage ownerPackage = getPackage(element);
        // String ownerID = "";
        // String ownerName = "";
        // if(getPackage(element) != null) {
        //     ownerID = ownerPackage.getGUID();
        //     ownerName = ownerPackage.getName();
        // }
        // if( ownerID.equals(modulePackage.getGUID()) != true ) {
        //     debug(String.format("element:%s's owner %s(%s)->%s(%s)",
        //             element.getName(),
        //             ownerName,
        //             ownerID,
        //             modulePackage.getName(),
        //             modulePackage.getGUID()));
        //     element.setOwner(modulePackage);
        // }

        if( rpTmpOperation != null ) {
            rpTmpOperation.deleteFromProject();
        }
    }

    public boolean isUpdate(IRPModelElement element) {
        IRPOperation rpOperation = getObject(element);

        if(doxygen_.getName().equals(rpOperation.getName()) != true) {
            debug("Operation Name is change "+ rpOperation.getName() + "->" + doxygen_.getName());
            return true;
        }

        List<IRPArgument> args = toList(rpOperation.getArguments());
        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);

        if(args.size() != params.size()) {
            debug("Operation:" + doxygen_.getName() 
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

    public boolean isUpdateArgment(String operationName, IRPArgument rpArgment, DoxygenType type) {
        DoxygenTypeParam param = getObject(type);

        if(param.getName().equals(rpArgment.getName()) != true) {
            debug(operationName + " Argment Name is change "+ rpArgment.getName() + "->" + param.getName());
            return true;
        }

        if(param.getDirection().equals(rpArgment.getArgumentDirection()) != true) {
            debug(operationName + " Direction is change "+ rpArgment.getArgumentDirection() + "->" + param.getDirection());
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

    public void applyByType(IRPModelElement element, String currentVersion) {
        IRPOperation rpOperation = getObject(element);

        if(doxygen_.getName().equals(rpOperation.getName()) != true) {
            debug("Operation Name is apply "+ rpOperation.getName() + "->" + doxygen_.getName());
            rpOperation.setName(doxygen_.getName());
        }

        List<IRPArgument> args = toList(rpOperation.getArguments());

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
                debug(String.format("Operation:%s argment:%s delete(from:%d, to:%d)",
                        rpOperation.getDisplayName() , 
                        rpArgment.getName(),
                        argument_index, delete_count));
            } 
            else if( argument_index < args.size()  ) {
                debug(String.format("Operation:%s add(%s, %d)",
                        rpOperation.getDisplayName() , argmentName, argument_index + 1));
                rpArgment = rpOperation.addArgumentBeforePosition(argmentName, argument_index + 1);
            }
            else {
                debug(String.format("Operation:%s add(%s, last)",
                        rpOperation.getDisplayName() , argmentName));
                rpArgment = rpOperation.addArgument(argmentName);
            }

            applyArgment(rpArgment, param, currentVersion);

            argument_index++;
        }

        delete_count = args.size() - argument_index;
        debug(String.format("Operation:%s delete(from:%d, to:%d)",
                rpOperation.getDisplayName() , 
                argument_index, delete_count));
        deleteArgment(args, argument_index, args.size() - argument_index);

        return;
    }

    protected int findArgment(List<IRPArgument> args, int startindex, String key) {
        debug(String.format("\targs:%d start:%d key:%s", 
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
            debug("\tdelete argment:"+ rpArgment.getName());
            rpArgment.deleteFromProject();
        }
        
        return;
    }

    protected void applyArgment(IRPArgument rpArgment, DoxygenTypeParam param, String currentVersion) {
        rpArgment.setArgumentDirection(param.getDirection());

        IRPType type = CreateType(param, currentVersion);
        if( type != null ) {
            rpArgment.setType(type);
        }
        return;
    }

}
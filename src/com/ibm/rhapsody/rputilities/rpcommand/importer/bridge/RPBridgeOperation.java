package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcommand.importer.doxygen.type.DoxygenTypeParam;
import com.telelogic.rhapsody.core.IRPArgument;
import com.telelogic.rhapsody.core.IRPOperation;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPType;

public class RPBridgeOperation extends ARPBridge {
    protected String name_ = null;

    public RPBridgeOperation(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPBridgeOperation.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if (doxygen == null) {
            return;
        }

        name_ = convertAvailableName(doxygen.getName());
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

        IRPOperation rpOperation = null;
        try {
            rpOperation = modulePackage.addGlobalFunction(name_);
        } catch (Exception e) {
            error("createElementByType Error name:" + name_, e);
            doxygen_.logoutdebug(0);
        }
        return rpOperation;
    }

    public boolean isUpdate(IRPModelElement element) {
        IRPOperation rpOperation = getObject(element);

        if (checkUpdate(name_, rpOperation.getName()) == true) {
            debug("Operation Name is change " + rpOperation.getName() + "->" + name_);
            return true;
        }

        List<IRPArgument> args = toList(rpOperation.getArguments());
        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);

        if (args.size() != params.size()) {
            debug("Operation:" + name_ + " Argment count is change " + args.size() + "->" + params.size());
            return true;
        }

        for (int index = 0; index < params.size(); index++) {
            IRPArgument rpArgment = args.get(index);
            DoxygenType param = params.get(index);

            if (isUpdateArgment(doxygen_.getName(), rpArgment, param) == true) {
                return true;
            }
        }

        return false;
    }

    public boolean isUpdateArgment(String operationName, IRPArgument rpArgment, DoxygenType type) {
        DoxygenTypeParam param = getObject(type);

        if (checkUpdate(param.getName(), rpArgment.getName()) == true) {
            debug(operationName + " Argment Name is change " + rpArgment.getName() + "->" + param.getName());
            return true;
        }

        String convDirection = convertDirection(param.getDirection(), param.getType());
        if (checkUpdate(convDirection, rpArgment.getArgumentDirection()) == true) {
            debug(operationName + " Direction is change " + rpArgment.getArgumentDirection() + "->" + convDirection);
            return true;
        }

        // IRPClassifier rpType = rpArgment.getType();
        // String rpTypeName = "";
        // if( rpType != null) {
        // rpTypeName = rpType.getDisplayName();
        // }

        // if(rpTypeName.equals(param.getType()) != true ) {
        // return true;
        // }

        return false;
    }

    public void applyByType(IRPModelElement element, String currentVersion, boolean isupdate) {
        IRPOperation rpOperation = getObject(element);

        if (checkUpdate(name_, rpOperation.getName()) == true) {
            debug("Operation Name is apply " + rpOperation.getName() + "->" + name_);
            rpOperation.setName(name_);
        }

        rpOperation.setDescription(doxygen_.getBriefdescription());

        List<IRPArgument> args = getArguments(rpOperation);

        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);
        int argument_index = 0;
        for (DoxygenType value : params) {
            DoxygenTypeParam param = getObject(value);
            String argmentName = param.getName();
            int find_index = findArgument(args, 0, argmentName);

            IRPArgument rpArgment = null;
            // argument is already exist
            if (find_index >= 0) {
                rpArgment = args.remove(find_index);
                deleteArgument(args, find_index);
                debug(String.format("Event:%s argment:%s delete(count:%d)",
                        rpOperation.getDisplayName(),
                        rpArgment.getName(),
                        find_index));
            } else {
                if (args.size() > 0) {
                    trace(String.format("Event:%s add(%s, %d)",
                            rpOperation.getDisplayName(), argmentName, argument_index + 1));
                    rpArgment = rpOperation.addArgumentBeforePosition(argmentName, argument_index + 1);
                } else {
                    trace(String.format("Event:%s add(%s, last)",
                            rpOperation.getDisplayName(), argmentName));
                    rpArgment = rpOperation.addArgument(argmentName);
                }
            }

            applyArgment(rpArgment, param, currentVersion);

            argument_index++;
        }

        deleteArgument(args, args.size());

        return;
    }

    protected List<IRPArgument> getArguments(IRPOperation rpevent) {
        List<IRPArgument> org_args = toList(rpevent.getArguments());
        List<IRPArgument> ret_args = new ArrayList<IRPArgument>();

        for (IRPArgument arg : org_args) {
            ret_args.add(arg);
        }

        return ret_args;
    }

    protected int findArgument(List<IRPArgument> args, int startindex, String key) {
        trace(String.format("\targs:%d start:%d key:%s",
                args.size(), startindex, key));

        for (int index = startindex; index < args.size(); index++) {
            IRPArgument rpArgment = args.get(index);
            if (key.equals(rpArgment.getName()) == true) {
                return index;
            }
        }
        return -1;
    }

    protected void deleteArgument(List<IRPArgument> args, int deletenumber) {
        if (deletenumber <= 0) {
            return;
        }

        for (int count = 0; count < deletenumber; count++) {
            IRPArgument rpArgument = args.remove(0);
            debug("\tdelete argument:" + rpArgument.getName());
            rpArgument.deleteFromProject();
        }

        return;
    }

    protected void applyArgment(IRPArgument rpArgment, DoxygenTypeParam param, String currentVersion) {
        String convDirection = convertDirection(param.getDirection(), param.getType());
        rpArgment.setArgumentDirection(convDirection);
        rpArgment.setDescription(param.getBriefdescription());

        IRPType type = CreateType(param, currentVersion);
        if (type != null) {
            rpArgment.setType(type);
        }
        return;
    }

}
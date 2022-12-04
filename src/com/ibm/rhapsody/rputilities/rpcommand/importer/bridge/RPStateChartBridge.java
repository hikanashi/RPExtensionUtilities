package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeParam;
import com.telelogic.rhapsody.core.IRPActivityDiagram;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPPin;
import com.telelogic.rhapsody.core.IRPType;

public class RPStateChartBridge extends ARPBridge {
    protected final String PIN_RETURN_NAME = "RETURN";
    protected final String PIN_RETURN_DIRECTION = "Out";

    public RPStateChartBridge(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPStateChartBridge.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if( doxygen == null ) {
            return;
        }

        // doxygen.debugout(0);
    }

    protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage) {
        List<IRPModelElement> list = new ArrayList<>(toList(rpPackage.getAllNestedElements()));
        list.removeIf(element -> !(element instanceof IRPFlowchart));
        return list;
    }


    public IRPModelElement findElementByType(IRPPackage rppackage) {
        // trace("find ActivityDiagram key:" + doxygen_.getName() + " package:"+ rppackage.getName());
        IRPModelElement rpelement = rppackage.findNestedElementRecursive(doxygen_.getName(),"ActivityDiagram");
        return rpelement;
    }

    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        IRPFlowchart rpFlowchart = modulePackage.addActivityDiagram();
        debug("create " + rpFlowchart.getDisplayName() 
            + " name:" + doxygen_.getName() 
            + " meta:"+ rpFlowchart.getMetaClass());
        if(doxygen_.getName().length() > 0) {
            rpFlowchart.setName(doxygen_.getName());
        }
        IRPActivityDiagram diagram = rpFlowchart.getFlowchartDiagram();
        diagram.createGraphics();
        return rpFlowchart;
    }

    public boolean isUpdate(IRPModelElement element) {
        IRPFlowchart rpActivity = getObject(element);

        if(doxygen_.getName().equals(rpActivity.getName()) != true) {
            trace("Activity Name is change "+ rpActivity.getName() + "->" + doxygen_.getName());
            return true;
        }

        List<IRPPin> pins = getActivityParameters(rpActivity);
        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);

        if(pins.size() != params.size() + 1) {
            trace("Activity:" + doxygen_.getName() 
                + " Argment count is change "+ pins.size() + "->" + params.size()+1);
            return true;
        }

        for(int index = 0; index < params.size(); index++){
            IRPPin rpPin = pins.get(index);
            DoxygenType param = params.get(index);

            if(isUpdateActivityPin(doxygen_.getName(), rpPin, param) == true ) {
                return true;
            }
        }

        IRPPin rpReturnPin = pins.get(params.size());
        if( isUpdateReturnPin(doxygen_.getName(), rpReturnPin, doxygen_ ) == true ) { 
            return true;
        }

        return false;
    }

    protected boolean isUpdateActivityPin(String activityName, IRPPin rpPin, DoxygenType type) {
        DoxygenTypeParam param = getObject(type);

        if(param.getName().length() > 0 && param.getName().equals(rpPin.getName()) != true) {
            trace(activityName + " Pin Name is change "+ rpPin.getName() + "->" + param.getName());
            return true;
        }

        if(param.getDirection().equals(rpPin.getPinDirection()) != true) {
            trace(rpPin.getName() + " Direction is change "+ rpPin.getPinDirection() + "->" + param.getDirection());
            return true;
        }

        IRPClassifier rpType = rpPin.getPinType();
        String rpTypeName = "";
        if( rpType != null) {
            rpTypeName = rpType.getDisplayName();
        }

        if(rpTypeName.equals(param.getType()) != true ) {
            return true;
        }

        return false;
    }

    protected boolean isUpdateReturnPin(String activityName, IRPPin rpPin, DoxygenType type) {

        if(rpPin.getName().equals(PIN_RETURN_NAME) != true) {
            trace(activityName + " Pin Name is change "+ rpPin.getName() + "->" + PIN_RETURN_NAME);
            return true;
        }

        if(rpPin.getPinDirection().equals(PIN_RETURN_DIRECTION) != true) {
            trace(rpPin.getName() + " Direction is change "+ rpPin.getPinDirection() + "->" + PIN_RETURN_DIRECTION);
            return true;
        }

        IRPClassifier rpType = rpPin.getPinType();
        String rpTypeName = "";
        if( rpType != null) {
            rpTypeName = rpType.getDisplayName();
        }

        if(rpTypeName.equals(type.getType()) != true ) {
            return true;
        }

        return false;
    }


    public void applyByType(IRPModelElement element, String currentVersion) {
        IRPFlowchart rpActivity = getObject(element);

        if(doxygen_.getName().equals(rpActivity.getName()) != true) {
            trace("Activity Name is apply "+ rpActivity.getName() + "->" + doxygen_.getName());
            rpActivity.setName(doxygen_.getName());
        }

        List<IRPPin> pins = getActivityParameters(rpActivity);
        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);
        for(DoxygenType value : params ) {
            DoxygenTypeParam param = getObject(value);
            String argmentName = param.getName();
            int	find_index = findPin(pins, argmentName);

            IRPPin rpPin = null;
            // pin is already exist
            if( find_index >= 0) {
                rpPin =  pins.get(find_index);
                pins.remove(find_index);
                trace(String.format("Activity:%s pin:%s is exist(index:%d)",
                        rpActivity.getDisplayName() , 
                        rpPin.getName(),
                        find_index));
            } 
            else {
                trace(String.format("Activity:%s pin:%s is create",
                        rpActivity.getDisplayName() , argmentName));
                rpPin = rpActivity.addActivityParameter(argmentName);
            }

            applyPin(doxygen_.getName(), rpPin, param, currentVersion);
        }

        IRPPin rpReturnPin = null;
        int return_index = findPin(pins, PIN_RETURN_NAME);
        if(return_index < 0 ){
            rpReturnPin = rpActivity.addActivityParameter(PIN_RETURN_NAME);
        } else {
            rpReturnPin = pins.get(return_index);
        }

        applyReturnPin(doxygen_.getName(), rpReturnPin, doxygen_, currentVersion);

        deletePins(pins);

        return;
    }

    protected int findPin(List<IRPPin> args, String key) {
        for(int index = 0; index < args.size(); index++){
            IRPPin rpArgment = args.get(index);
            if( key.equals(rpArgment.getName()) == true ) {
                return index;
            }
        }
        return -1;
    }

    protected void deletePins(List<IRPPin> args ) {
        for(int index = args.size() - 1; index > 0; index--){
            IRPPin rpPin = args.get(index);
            trace("\tdelete pin:"+ rpPin.getName());
            rpPin.deleteFromProject();
        }
        
        return;
    }

    protected void applyPin(String activityName, IRPPin rpPin, DoxygenTypeParam param, String currentVersion) {
        if( rpPin == null || param == null ){
            return;
        }

        if(param.getName().length() > 0 && rpPin.getName().equals(param.getName()) != true) {
            trace(activityName + " Pin Name is apply "+ rpPin.getName() + "->" + param.getName());
            rpPin.setName(param.getName());
        }

        if(rpPin.getPinDirection().equals(param.getDirection()) != true) {
            trace(activityName + " Direction is apply "+ rpPin.getPinDirection() + "->" + param.getDirection());
            rpPin.setPinDirection(param.getDirection());
        }

        IRPType type = CreateType(param, currentVersion);
        if( type != null ) {
            rpPin.setPinType(type);
        }
        return;
    }

    protected void applyReturnPin(String activityName, IRPPin rpPin, DoxygenType param, String currentVersion) {
        if(rpPin.getName().equals(PIN_RETURN_NAME) != true) {
            trace(activityName + " Pin Name is apply "+ rpPin.getName() + "->" + PIN_RETURN_NAME);
            rpPin.setName(PIN_RETURN_NAME);
        }

        if(rpPin.getPinDirection().equals(PIN_RETURN_DIRECTION) != true) {
            trace(activityName + " Direction is apply "+ rpPin.getPinDirection() + "->" + PIN_RETURN_DIRECTION);
            rpPin.setPinDirection(PIN_RETURN_DIRECTION);
        }

        IRPType type = CreateType(param, currentVersion);
        if( type != null ) {
            rpPin.setPinType(type);
        }
        return;
    }

    protected List<IRPPin> getActivityParameters(IRPFlowchart rpFlowchart) {
        List<IRPPin> activitypins = new ArrayList<IRPPin>();

        List<Object> flowElements = toList(rpFlowchart.getElementsInDiagram());
        for(Object obj : flowElements) {
            if(!(obj instanceof IRPPin)) {
                continue;
            }

            IRPPin rpPin = getObject(obj);
            activitypins.add(rpPin);
        }

        return activitypins;
    }

}
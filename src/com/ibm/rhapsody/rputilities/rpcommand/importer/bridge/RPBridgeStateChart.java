package com.ibm.rhapsody.rputilities.rpcommand.importer.bridge;

import java.util.ArrayList;
import java.util.List;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.doxygen.TAGTYPE;
import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenTypeParam;
import com.telelogic.rhapsody.core.IRPActivityDiagram;
import com.telelogic.rhapsody.core.IRPClassifier;
import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPGraphElement;
import com.telelogic.rhapsody.core.IRPGraphicalProperty;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPPin;
import com.telelogic.rhapsody.core.IRPType;

public class RPBridgeStateChart extends ARPBridge {
    protected final String PIN_RETURN_NAME = "RETURN";
    protected final String PIN_RETURN_DIRECTION = "Out";

    protected String name_ = null;

    public RPBridgeStateChart(DoxygenType doxygen, IRPPackage rootPackage) {
        super(RPBridgeStateChart.class, doxygen, rootPackage);
        initialize(doxygen);
    }

    protected void initialize(DoxygenType doxygen) {
        if( doxygen == null ) {
            return;
        }

        name_ = convertAvailableName(doxygen_.getName());
    }

    protected List<IRPModelElement> getElementsByType(IRPPackage rpPackage) {
        List<IRPModelElement> list = new ArrayList<>(toList(rpPackage.getAllNestedElements()));
        list.removeIf(element -> !(element instanceof IRPFlowchart));
        return list;
    }


    public IRPModelElement findElementByType(IRPPackage rppackage) {
        // trace("find ActivityDiagram key:" + name_ + " package:"+ rppackage.getName());
        IRPModelElement rpelement = rppackage.findNestedElementRecursive(name_,"ActivityDiagram");
        return rpelement;
    }

    public IRPModelElement createElementByType(IRPPackage modulePackage) {
        IRPFlowchart rpFlowchart = null;

        try {
            rpFlowchart = modulePackage.addActivityDiagram();
            debug("create " + rpFlowchart.getDisplayName() 
                + " name:" + name_ 
                + " meta:"+ rpFlowchart.getMetaClass());
            
            if(name_.length() > 0) {
                rpFlowchart.setName(name_);
            }

            rpFlowchart.createGraphics();
            rpFlowchart.setShowDiagramFrame(1);
            
            
        } catch (Exception e) {
            error("createElementByType Error name:" + name_, e);
            doxygen_.logoutdebug(0);
        }
        return rpFlowchart;
    }

    public boolean isUpdate(IRPModelElement element) {
        IRPFlowchart rpActivity = getObject(element);

        if(name_.equals(rpActivity.getName()) != true) {
            trace("Activity Name is change "+ rpActivity.getName() + "->" + name_);
            return true;
        }

        List<IRPPin> pins = getActivityParameters(rpActivity);
        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);

        if(pins.size() != params.size() + 1) {
            trace("Activity:" + name_ 
                + " Argment count is change "+ pins.size() + "->" + params.size()+1);
            return true;
        }

        for(int index = 0; index < params.size(); index++){
            IRPPin rpPin = pins.get(index);
            DoxygenType param = params.get(index);

            if(isUpdateActivityPin(name_, rpPin, param) == true ) {
                return true;
            }
        }

        IRPPin rpReturnPin = pins.get(params.size());
        if( isUpdateReturnPin(name_, rpReturnPin, doxygen_ ) == true ) { 
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

        if(name_.equals(rpActivity.getName()) != true) {
            trace("Activity Name is apply "+ rpActivity.getName() + "->" + doxygen_.getName());
            rpActivity.setName(name_);
        }

        List<IRPPin> pins = getActivityParameters(rpActivity);
        List<DoxygenType> params = doxygen_.getChildlen(TAGTYPE.PARAM);
        doxygen_.logoutdebug(0);

        int index = 0;
        for(DoxygenType value : params ) {
            DoxygenTypeParam param = getObject(value);
            String argmentName = param.getName();
            int	find_index = findPin(pins, argmentName);

            IRPPin rpPin = null;
            // pin is already exist
            if( find_index >= 0) {
                rpPin = pins.remove(find_index);
                debug(String.format("Activity:%s pin:%s is exist(index:%d)",
                        rpActivity.getDisplayName() , 
                        rpPin.getName(),
                        find_index));
            } 
            else {
                debug(String.format("Activity:%s pin:%s is create",
                        rpActivity.getDisplayName() , argmentName));
                rpPin = rpActivity.addActivityParameter(argmentName);
            }

            applyPin(rpActivity, rpPin, param, currentVersion, index);
            index++;
        }

        IRPPin rpReturnPin = null;
        int return_index = findPin(pins, PIN_RETURN_NAME);
        if(return_index < 0 ){
            rpReturnPin = rpActivity.addActivityParameter(PIN_RETURN_NAME);
        } else {
            rpReturnPin = pins.get(return_index);
        }

        applyReturnPin(rpActivity, rpReturnPin, doxygen_, currentVersion, index);

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

    protected void applyPin(IRPFlowchart flowchart, IRPPin rpPin, DoxygenTypeParam param, String currentVersion, int index) {
        if( rpPin == null || param == null ){
            return;
        }

        if(param.getName().length() > 0 && rpPin.getName().equals(param.getName()) != true) {
            trace(flowchart.getName() + " Pin Name is apply "+ rpPin.getName() + "->" + param.getName());
            rpPin.setName(param.getName());
        }

        if(rpPin.getPinDirection().equals(param.getDirection()) != true) {
            trace(flowchart.getName() + " Direction is apply "+ rpPin.getPinDirection() + "->" + param.getDirection());
            rpPin.setPinDirection(param.getDirection());
        }

        setPosition(flowchart, rpPin, 0, (index+1) * 50);

        IRPType type = CreateType(param, currentVersion);
        if( type != null ) {
            rpPin.setPinType(type);
        }
        return;
    }

    protected void applyReturnPin(IRPFlowchart flowchart, IRPPin rpPin, DoxygenType param, String currentVersion, int index) {
        if(rpPin == null) {
            return;
        }

        if(rpPin.getName().equals(PIN_RETURN_NAME) != true) {
            trace(flowchart.getName() + " Pin Name is apply "+ rpPin.getName() + "->" + PIN_RETURN_NAME);
            rpPin.setName(PIN_RETURN_NAME);
        }

        if(rpPin.getPinDirection().equals(PIN_RETURN_DIRECTION) != true) {
            trace(flowchart.getName() + " Direction is apply "+ rpPin.getPinDirection() + "->" + PIN_RETURN_DIRECTION);
            rpPin.setPinDirection(PIN_RETURN_DIRECTION);
        }

        setPosition(flowchart, rpPin, 0, (index+1) * 50);

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

    public void setPosition(IRPFlowchart flowchart, IRPModelElement element, Integer x, Integer y) {

        IRPGraphElement graphElement = null;
        graphElement = getGraphElement(flowchart, element);
        if (graphElement != null) {
            graphElement.setGraphicalProperty("Position", x + ", " + y);
            trace(element.getName() + " position at "+ graphElement.getGraphicalProperty("Position").getValue());
        }
    }

    public IRPGraphElement getGraphElement(IRPFlowchart fc, IRPModelElement element) {        
        List<IRPGraphElement> graphList = toList(fc.getGraphicalElements());
        if (!graphList.isEmpty()) {
            for (IRPGraphElement graphElement : graphList) {
                if (graphElement.getModelObject() != null) {
                    if(graphElement.getModelObject().getGUID().equals(element.getGUID())) {
                        return graphElement;
                    }
                }
            }
        }

        return null;
    }



}
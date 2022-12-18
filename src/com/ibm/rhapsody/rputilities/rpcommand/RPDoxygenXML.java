package com.ibm.rhapsody.rputilities.rpcommand;

import java.util.List;

import com.ibm.rhapsody.rputilities.rpcommand.importer.ImportGUI;
import com.ibm.rhapsody.rputilities.rpcommand.importer.bridge.ARPBridge;
import com.ibm.rhapsody.rputilities.rpcore.RPActivityFacade;
// import com.ibm.rhapsody.rputilities.rpcore.RPLog;
// import com.ibm.rhapsody.rputilities.rpcore.RPLogLevel;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPFlowchart;

public class RPDoxygenXML extends IRPUtilityCommmand {
    protected static final String COMMAND_IMPORT = "Import";
    protected static final String COMMAND_REPLACECHECK = "Check Unavailable Activities";

    protected ImportGUI mainGUI_ = null;

    /**
     * Doxygen's XML import class
     * 
     * @param element Elements selected when right-clicked
     */
    public RPDoxygenXML(IRPModelElement element) {
        super(RPDoxygenXML.class, element);
        mainGUI_ = new ImportGUI(this);
    }

    /*
     * Import Doxygen XML in the specified directory
     * 
     * @see
     * com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) {
        // RPLog.setLevel(RPLogLevel.DEBUG);

        if (argment.length < 2) {
            error("RPDoxygenXML\\[" + COMMAND_IMPORT + " or " + COMMAND_REPLACECHECK
                    + "]. Please check RPExtensionUtilities.hep");
            return false;
        }

        String command = argment[1];
        if (command.equals(COMMAND_IMPORT) == true) {
            mainGUI_.setVisible(true);
            return true;
        } else if (command.equals(COMMAND_REPLACECHECK) == true) {
            boolean checkresult = CheckUnavailableActivities();
            return checkresult;
        }

        return false;
    }

    protected boolean CheckUnavailableActivities() {
        IRPModelElement rproot = getElement();
        if(rproot == null) {
            error("Need select element. Please select one Element.");
            return false;
        }

        info("---------------------- " + COMMAND_REPLACECHECK + " Start Root["+ rproot.getDisplayName() + "]");
        CheckUnavailableActivities(rproot);
        info("---------------------- " + COMMAND_REPLACECHECK + " End Root["+ rproot.getDisplayName() + "]");

        return true;
    }

    protected void CheckUnavailableActivities(IRPModelElement rproot) {
        debug("root:" + rproot.getDisplayName());

        RPActivityFacade activityFacade = new RPActivityFacade();
        List<IRPFlowchart> activityCollection = activityFacade.CollectActivity(rproot, 1);
        for (IRPFlowchart rpflowchart : activityCollection) {
            List<Object> flowElements = toList(rpflowchart.getElementsInDiagram());
            CheckUnavailableElements(rpflowchart, flowElements);
        }

        return;
    }

    protected void CheckUnavailableElements(IRPFlowchart rpflowchart, List<Object> elements) {
        trace("\tElements:" + elements.size());

        for (Object flowobj : elements) {
            IRPModelElement model = getObject(flowobj);
            trace("\t\tcontents:" + model.getMetaClass()
                    + " Name:" + model.getDisplayName()
                    + " class:" + model.getClass().toString());

            if (flowobj instanceof IRPState) {
                IRPState rpstate = getObject(flowobj);
                CheckUnavailableState(rpflowchart, rpstate);
            } else {
                continue;
            }
        }

        return;
    }

    protected void CheckUnavailableState(IRPFlowchart rpflowchart, IRPState rpstate) {

        if(rpstate.getIsReferenceActivity() != 1) {
            return;
        }

        IRPModelElement refActivity = rpstate.getReferenceToActivity();

        if(refActivity == null) {
            trace("\t\t\tState Not Reference " + rpstate.getDisplayName()
                + " class:" + rpstate.getClass().toString());
            return;   
        }

        IRPModelElement rpTag = refActivity.getTag(ARPBridge.TAG_VERSION_UNAVAILABLE);
        if(rpTag == null) {
            trace("\t\t\tTag[" + ARPBridge.TAG_VERSION_UNAVAILABLE + "] is not set" + rpstate.getDisplayName());
            return;
        }

        info(String.format("Use [unavailable activity],%s,%s,%s",
            getPathToProject(rpflowchart, "/"),
            rpstate.getName(),
            getPathToProject(refActivity, "/")
        ));

        return;
    }

}

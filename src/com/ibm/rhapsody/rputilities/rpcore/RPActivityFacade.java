package com.ibm.rhapsody.rputilities.rpcore;

import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPModelElement;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RPActivityFacade extends ARPObject {

    /**
     * constructor
     */
    public RPActivityFacade() {
        super(RPActivityFacade.class);
    }

    /**
     * Get a list of activity diagrams under the specified root element.
     * 
     * @param rproot    Root element as a base point
     * @param recursive 1 is search recursively, 0 is not search recursively
     * @return List of activity diagrams retrieved. If there is no target, an empty
     *         list is returned.
     */
    public List<IRPFlowchart> CollectActivity(IRPModelElement rproot, int recursive) {
        Set<IRPFlowchart> activitylist = new LinkedHashSet<IRPFlowchart>();

        if (rproot == null) {
            return newList(activitylist);
        }

        // Collect ActivityDiagram
        List<Object> activityCollection = toList(
                rproot.getNestedElementsByMetaClass("ActivityDiagram", recursive));
        for (Object obj : activityCollection) {
            if (!(obj instanceof IRPFlowchart)) {
                continue;
            }

            IRPFlowchart rpflowchart = getObject(obj);
            append(activitylist, rpflowchart);

            trace("CollectActivity:Package," + rproot.getDisplayName()
                    + ",Owner,"
                    + (rpflowchart.getOwner() != null ? rpflowchart.getOwner().getDisplayName() : "--None--")
                    + ",Activity," + rpflowchart.getDisplayName());
        }

        return newList(activitylist);
    }

    /**
     * Register an activity diagram with deduplication
     * 
     * @param set Set of registered destinations
     * @param obj Activity diagram for registration
     */
    private void append(Set<IRPFlowchart> set, IRPFlowchart obj) {
        trace("append :" + obj.getDisplayName()
                + ",GUID," + obj.getGUID()
                + ",Owner," + (obj.getOwner() != null ? obj.getOwner().getDisplayName() : "--None--"));

        if (set.contains(obj) != true) {
            set.add(obj);
        }
    }

    /**
     * Generate a List from a Set in an activity diagram.
     * 
     * @param set Set of Activity Diagrams
     * @return List of Activity Diagrams
     */
    private List<IRPFlowchart> newList(Set<IRPFlowchart> set) {
        List<IRPFlowchart> list = new ArrayList<IRPFlowchart>(set);
        return list;
    }
}

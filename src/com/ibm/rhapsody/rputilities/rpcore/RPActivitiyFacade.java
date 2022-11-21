package com.ibm.rhapsody.rputilities.rpcore;


import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPPackage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RPActivitiyFacade extends ARPObject {
    

    public static List<IRPFlowchart> CollectActivity(IRPPackage rppackage,int recursive)
    {
        Set<IRPFlowchart> activitylist = new LinkedHashSet<IRPFlowchart>();

        if(rppackage == null)
        {
            return toList(activitylist);
        }

        // Collect ActivityDiagram
        List<Object> activityCollection = toList(
                rppackage.getNestedElementsByMetaClass("ActivityDiagram", recursive));
        for(Object obj : activityCollection)
        {
            if(!(obj instanceof IRPFlowchart))
            {
                continue;
            }

            IRPFlowchart rpflowchart = getObject(obj);
            append(activitylist,rpflowchart);

            RPLog.Detail("CollectActivity:Package," + rppackage.getDisplayName()
                + ",Owner," + (rpflowchart.getOwner() != null ? rpflowchart.getOwner().getDisplayName() : "--None--")
                + ",Activity," + rpflowchart.getDisplayName());            
        }

        return toList(activitylist);
    }


    private static void append(Set<IRPFlowchart> set, IRPFlowchart obj)
    {
        RPLog.Detail("append :" + obj.getDisplayName()
            + ",GUID," + obj.getGUID()
            + ",Owner," + (obj.getOwner() != null ? obj.getOwner().getDisplayName() : "--None--"));  
        if(set.contains(obj) != true)
        {
            set.add(obj);
        }
    }

    private static List<IRPFlowchart> toList(Set<IRPFlowchart> set)
    {
        List<IRPFlowchart> list = new ArrayList<IRPFlowchart>(set);
        return list;
    }
}

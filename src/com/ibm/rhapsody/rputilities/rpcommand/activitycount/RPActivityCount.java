package com.ibm.rhapsody.rputilities.rpcommand.activitycount;

import com.ibm.rhapsody.rputilities.rpcommand.IRPUtilityCommmand;
import com.ibm.rhapsody.rputilities.rpcore.RPActivityFacade;
import com.telelogic.rhapsody.core.IRPConnector;
import com.telelogic.rhapsody.core.IRPFlowchart;
import com.telelogic.rhapsody.core.IRPModelElement;
import com.telelogic.rhapsody.core.IRPPackage;
import com.telelogic.rhapsody.core.IRPState;
import com.telelogic.rhapsody.core.IRPStateVertex;
import com.telelogic.rhapsody.core.IRPSwimlane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RPActivityCount extends IRPUtilityCommmand {
     /**
     * アクティビティ図のアクションカウントクラス
     * @param element 右クリック時に選択された要素
     */
    public RPActivityCount(IRPModelElement element) 
    {
        super(RPActivityCount.class,element);
    }

    /* 
     * 選択されたパッケージのアクティビティをカウントする
     * @see com.ibm.rhapsody.rputilities.IRPUtilityCommmand#command(java.lang.String[])
     */
    public boolean command(String[] argment) 
    {
        IRPModelElement element = getElement();
        if(element == null) {
            error("name[" + argment[0] + "] is need select element.\n"
                + "Please select one Element.");
            return false;
        }

        info("---------------------- Element["+ element.getDisplayName() + "]");
        
        if(element instanceof IRPPackage)
        {
            IRPPackage rppackage = getElement();
            List<ActiounCounter> counter_list = CountActivity(rppackage);
            outputActivityCount(counter_list);
        }
        else if(element instanceof IRPFlowchart)
        {
            List<ActiounCounter> counter_list = new ArrayList<ActiounCounter>();

            IRPFlowchart rpActivity = getElement();
            ActiounCounter counter = CountStateChart(rpActivity);
            if(counter != null ) {
                counter_list.add(counter);
            }

            outputActivityCount(counter_list);
        }
        else
        {
            error("select element["+ element.getDisplayName() 
                + "]("+ element.getClass().toString() + ") is not target element. ");
            return false;
        }

        return true;
    }

    /**
     * 選択されたパッケージ以下のアクティビティ図のアクションをカウントする。
     * @param rppackage 選択されたパッケージ
     * @param targetSwimlane    カウント対象のスイムレーン名
     * @return アクションのカウント結果の配列
     */
    protected List<ActiounCounter> CountActivity(IRPPackage rppackage) 
    {
        List<ActiounCounter> counter_list = new ArrayList<ActiounCounter>();

        debug("Package:" + rppackage.getDisplayName());
        
        RPActivityFacade activityFacade = new RPActivityFacade();
        List<IRPFlowchart> activityCollection = activityFacade.CollectActivity(rppackage,1);
        for(IRPFlowchart rpflowchart : activityCollection)
        {
            ActiounCounter counter = CountStateChart(rpflowchart);
            counter_list.add(counter);
        }

        return counter_list;
    }


    protected ActiounCounter CountStateChart(IRPFlowchart chart) 
    {
        if(chart == null )
        {
            return null;
        }

        debug("Count StateChart:" + chart.getDisplayName());

        List<Object> flowElements = toList(chart.getElementsInDiagram());
        ActiounCounter counter = new ActiounCounter( getPackageName(chart,"/"), chart.getDisplayName());
        List<IRPSwimlane> swimlanes = toList(chart.getSwimlanes());
        for(IRPSwimlane swimlane : swimlanes) {
            counter.InitAction(swimlane.getDisplayName());
        }        

        CountElements(flowElements,counter);

        return counter;
    }

    protected void CountElements(List<Object> elements, ActiounCounter counter) 
    {
        trace("\t\tElements:" + elements.size() );

        for(Object flowobj : elements)
        {
            IRPModelElement model = getObject(flowobj);
            trace("\t\tcontents:" + model.getMetaClass()
                 + " Name:"+ model.getDisplayName()
                 + " class:"+ model.getClass().toString());

            if(flowobj instanceof IRPState)
            {
                IRPState rpstate = getObject(flowobj);
                CountState(rpstate,counter);
            }
            else if(flowobj instanceof IRPConnector)
            {
                IRPConnector rpConnector = getObject(flowobj);
                CountConnector(rpConnector,counter);
            }
            else
            {
                continue;
            }
        }

        return;
    }

    protected void CountConnector(IRPConnector rpConnector, ActiounCounter counter) 
    {

        if(rpConnector.isConditionConnector() != 1)
        {
            return;
        }

        String swimlaneName = getSwimlaneName(rpConnector);   
        counter.CountAction(swimlaneName);

        debug("\t\t\tConnector:" 
            + " Name:"+ rpConnector.getDisplayName()
            + " Swimlane:"+ swimlaneName
            + " Owner:"+ (rpConnector.getOwner() == null ? "-" : rpConnector.getOwner().getDisplayName()));

        return;
    }
                     
    protected void CountState(IRPState rpstate, ActiounCounter counter) 
    {
        String stateType = rpstate.getStateType();

        String swimlaneName = getSwimlaneName(rpstate);

        if( stateType.equals("Action") ||
            stateType.equals("EventState") ||
            stateType.equals("AcceptEventAction") ||
            stateType.equals("ReferenceActivity") ||
            stateType.equals("CallOperation") ||
            stateType.equals("TimeEvent")) 
        {
            counter.CountAction(swimlaneName);
        }
        else if( stateType.equals("LocalTermination") )
        {
            counter.CountActivityFinal(swimlaneName);
        }
        else if( stateType.equals("FlowFinal") )
        {
            counter.CountFlowFinal(swimlaneName);
        }
        else
        {
            trace("\t\t\tState Not Count " + rpstate.getDisplayName()
                + " class:"+ rpstate.getClass().toString());
            return;
        }

        debug("\t\t\tState:" + rpstate.getStateType()
            + " Name:"+ rpstate.getDisplayName()
            + " Swimlane:"+ swimlaneName
            + " Owner:"+ (rpstate.getParent() == null ? "-" : rpstate.getParent().getDisplayName()));

        return;
    }

    protected String getSwimlaneName(IRPStateVertex rpstatevertex)
    {
        String swimlaneName = "";
        IRPStateVertex swimcheckstate = rpstatevertex;

        while(swimcheckstate != null)
        {
            IRPSwimlane swimlane = null;
            if(swimcheckstate instanceof IRPState) {
                IRPState rpstate = getObject(swimcheckstate);
                swimlane = rpstate.getItsSwimlane();
            }
            else if(swimcheckstate instanceof IRPConnector) {
                IRPConnector rpconnector = getObject(swimcheckstate);
                swimlane = rpconnector.getItsSwimlane();
            }

            if( swimlane != null )
            {
                swimlaneName = swimlane.getDisplayName();
                break;
            }
            
            swimcheckstate = swimcheckstate.getParent();
        }

        return swimlaneName;
    } 
    
    protected void outputActivityCount(List<ActiounCounter> count_action)
    {
        ActiounCounter summary_counter = new ActiounCounter("[ALL]","[ALL]");

        String title = "Package,Activity";
        for(ActiounCounter count : count_action) {
            summary_counter.merge(count);
        }

        Map<String,Integer> summary = summary_counter.getMap();
        Set<String> keys = summary.keySet();
        for(String key  : keys) {
            title += "," + key;
        }
        
        info(title);


        for(ActiounCounter count : count_action) {
            outputActivityCount(keys, count);
        }
        
        info(title);
        outputActivityCount(keys, summary_counter);
    } 


    protected void outputActivityCount(Set<String> keys, ActiounCounter counter)
    {
        String logmessage = counter.getPackageName() + "," + counter.getActivityName();
        Map<String,Integer> count = counter.getMap();

        for(String key :  keys) {
            logmessage += "," + count.getOrDefault(key,0);
        }

        info(logmessage);
    } 
}

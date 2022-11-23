package com.ibm.rhapsody.rputilities.rpcommand;

import com.ibm.rhapsody.rputilities.rpcore.RPActivitiyFacade;
import com.telelogic.rhapsody.core.*;
import java.util.List;

class RPActivityCount extends IRPUtilityCommmand {
    public enum COUNT_INDEX {
        ACTION(0),
        FLOWFINAL(1),
        ACTIVITYFINAL(2),
        ;
    
        private final int id;
    
        private COUNT_INDEX(final int id) {
            this.id = id;
        }
    
        public int getInt() {
            return this.id;
        }
    }
    
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
        if( argment.length < 2 )
        {
            error("name[" + argment[0] + "] is invaild.\n"
                + "Please RPActivityCount/[SwimlaneName].");
            return false;
        }

        String targetSwimlane = argment[1];
        IRPModelElement element = getElement();
        if(element == null)
        {
            error("name[" + argment[0] + "] is need select element.\n"
                + "Please select one Element.");
            return false;
        }

        if(element instanceof IRPPackage)
        {
            IRPPackage rppackage = getElement();
            info("---------------------- Element["+ element.getDisplayName() 
                + "] Count Activity[" + targetSwimlane + "]");

            outputActivityCountTitle();
            int[] count_action = CountActivity(rppackage, targetSwimlane);
            outputActivityCount(null,targetSwimlane,count_action);
        }
        else if(element instanceof IRPFlowchart)
        {
            IRPFlowchart rpActivity = getElement();
            outputActivityCountTitle();
            CountStateChart(rpActivity, targetSwimlane);
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
    protected int[] CountActivity(IRPPackage rppackage, String targetSwimlane) 
    {
        int[] all_count_action = {0,0,0};
        debug("Package:" + rppackage.getDisplayName()
                + " Count Activity target:" + targetSwimlane);
        
        //List<Object> activityCollection = rppackage.getBehavioralDiagrams().toList();
        List<IRPFlowchart> activityCollection = RPActivitiyFacade.CollectActivity(rppackage,1);
        for(IRPFlowchart rpflowchart : activityCollection)
        {
            String swimlaneName = String.copyValueOf(targetSwimlane.toCharArray());
            if( rpflowchart.getSwimlanes().getCount() <= 0)
            {
                swimlaneName = "";
            }

            int[] count_action = CountStateChart(rpflowchart,swimlaneName);
            for(int index = 0; index < count_action.length; index++)
            {
                all_count_action[index] += count_action[index];
            }
        }

        return all_count_action;
    }


    protected int[] CountStateChart(IRPStatechart chart, String targetSwimlane) 
    {
        int[] count_action = {0,0,0};

        if(chart == null )
        {
            return count_action;
        }

        debug("Count StateChart:" + chart.getDisplayName() + " Swimlane:"+targetSwimlane);

        List<Object> flowElements = toList(chart.getElementsInDiagram());
        int[] count_state = CountElements(flowElements,targetSwimlane);
        for(int index = 0; index < count_action.length; index++)
        {
            count_action[index] += count_state[index];
        }

        outputActivityCount(chart,targetSwimlane,count_action);

        return count_action;
    }

    protected int[] CountElements(List<Object> elements, String targetSwimlane) 
    {
        int[] count_action = {0,0,0};

        trace("\t\tElements:" + elements.size() );

        for(Object flowobj : elements)
        {
            int[] count_state = {0,0,0};
            IRPModelElement model = getObject(flowobj);
            trace("\t\tcontents:" + model.getMetaClass()
                 + " Name:"+ model.getDisplayName()
                 + " class:"+ model.getClass().toString());

            if(flowobj instanceof IRPState)
            {
                IRPState rpstate = getObject(flowobj);
                count_state = CountState(rpstate,targetSwimlane);
            }
            else if(flowobj instanceof IRPConnector)
            {
                IRPConnector rpConnector = getObject(flowobj);
                count_state = CountConnector(rpConnector,targetSwimlane);
            }
            else
            {
                continue;
            }

            for(int index = 0; index < count_action.length; index++)
            {
                count_action[index] += count_state[index];
            }
        }

        return count_action;
    }

    protected int[] CountConnector(IRPConnector rpConnector, String targetSwimlane) 
    {
        int[] count_action = {0,0,0};

        if(rpConnector.isConditionConnector() != 1)
        {
            return count_action;
        }

        String swimlaneName = null;
        if(targetSwimlane.length() > 0 )
        {
            if(rpConnector.getItsSwimlane() == null)
            {
                swimlaneName = "";
            }
            else
            {
                swimlaneName = rpConnector.getItsSwimlane().getDisplayName();
            }
                        
            if(swimlaneName.length() > 0 &&
            swimlaneName.equals(targetSwimlane) != true)
            {
                return count_action;
            }
        }

        count_action[COUNT_INDEX.ACTION.getInt()]++;

        debug("\t\t\tConnector:" 
            + " Name:"+ rpConnector.getDisplayName()
            + " Swimlane:"+ swimlaneName
            + " Owner:"+ (rpConnector.getOwner() == null ? "-" : rpConnector.getOwner().getDisplayName()));

        return count_action;
    }
                     
    protected int[] CountState(IRPState rpstate, String targetSwimlane) 
    {
        int[] count_action = {0,0,0};
        String stateType = rpstate.getStateType();

        String swimlaneName = getSwimlaneName(rpstate);

        if(targetSwimlane.length() > 0 )
        {       
            if(swimlaneName.equals(targetSwimlane) != true)
            {
                return count_action;
            }
        }

        if( stateType.equals("Action") ||
            stateType.equals("EventState") ||
            stateType.equals("AcceptEventAction") ||
            stateType.equals("ReferenceActivity") ||
            stateType.equals("CallOperation") ||
            stateType.equals("TimeEvent")) 
        {
            count_action[COUNT_INDEX.ACTION.getInt()]++;
        }
        else if( stateType.equals("LocalTermination") )
        {
            count_action[COUNT_INDEX.ACTIVITYFINAL.getInt()]++;

        }
        else if( stateType.equals("FlowFinal") )
        {
            count_action[COUNT_INDEX.FLOWFINAL.getInt()]++;
        }
        else
        {
            trace("\t\t\tState Not Count " + rpstate.getDisplayName()
                + " class:"+ rpstate.getClass().toString());
            return count_action;
        }

        debug("\t\t\tState:" + rpstate.getStateType()
            + " Name:"+ rpstate.getDisplayName()
            + " Swimlane:"+ swimlaneName
            + " Owner:"+ (rpstate.getParent() == null ? "-" : rpstate.getParent().getDisplayName()));

        return count_action;
    }

    protected String getSwimlaneName(IRPState rpstate)
    {
        String swimlaneName = "-";
        IRPState swimcheckstate = rpstate;

        while(swimcheckstate != null)
        {
            if( swimcheckstate.getItsSwimlane() != null )
            {
                swimlaneName = swimcheckstate.getItsSwimlane().getDisplayName();
                break;
            }
            
            swimcheckstate = swimcheckstate.getParent();
        }

        return swimlaneName;
    } 

    protected void outputActivityCountTitle()
    {
        info("Package,Activity,Action,FlowFinal,ActivityFinal,Swimlane,Owner");
    } 

    protected void outputActivityCount(IRPStatechart chart, String targetSwimlane,int[] count_action)
    {
        String packageName = "[ALL]";
        String ownerName = "[ALL]";
        String activityName = "[ALL]";

        if(chart != null)
        {
            packageName = getPackageName(chart,"/");
            ownerName = (chart.getOwner() != null ? chart.getOwner().getDisplayName() : "--None--");
            activityName = chart.getDisplayName();
        }


        info(packageName + "," + activityName 
                + "," + count_action[COUNT_INDEX.ACTION.getInt()]
                + "," + count_action[COUNT_INDEX.FLOWFINAL.getInt()]
                + "," + count_action[COUNT_INDEX.ACTIVITYFINAL.getInt()]
                + "," + (targetSwimlane.length() > 0 ? targetSwimlane : "--None--")
                + "," + ownerName);
    } 
}

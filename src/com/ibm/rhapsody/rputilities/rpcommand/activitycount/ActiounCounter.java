package com.ibm.rhapsody.rputilities.rpcommand.activitycount;

import java.util.LinkedHashMap;
import java.util.Map;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;

public class ActiounCounter extends ARPObject {
    protected static final String NONE_NAME = "-None-";
    protected static final String FLOWINAL_NAME = "-flowfinal-";
    protected static final String ACTIVITYINAL_NAME = "-activityfinal-";

    protected String packageName_ = "";
    protected String activityName_ = "";
    protected Map<String, Integer> count_action_ = new LinkedHashMap<String, Integer>();
    protected Integer count_flowfinal_ = 0;
    protected Integer count_activityfinal_ = 0;

    public ActiounCounter(String packagename, String activityName) {
        super(ActiounCounter.class);
        packageName_ = packagename;
        activityName_ = activityName;
    }

    public String getPackageName() {
        return packageName_;
    }

    public String getActivityName() {
        return activityName_;
    }

    public Map<String, Integer> getMap() {
        Map<String, Integer> counter = new LinkedHashMap<String, Integer>(count_action_);
        counter.put(FLOWINAL_NAME, count_flowfinal_);
        counter.put(ACTIVITYINAL_NAME, count_activityfinal_);
        return counter;
    }

    public void InitAction(String swimlane) {
        CountAction(swimlane, 0);
    }

    public void CountAction(String swimlane) {
        CountAction(swimlane, 1);
    }

    public void CountFlowFinal(String swimlane) {
        count_flowfinal_++;
    }

    public void CountActivityFinal(String swimlane) {
        count_activityfinal_++;
    }

    public void merge(ActiounCounter source) {
        if (source == null) {
            return;
        }

        count_flowfinal_ += source.count_flowfinal_;
        count_activityfinal_ += source.count_activityfinal_;

        source.count_action_.forEach((key, value) -> {
            CountAction(key, value);
        });
    }

    protected void CountAction(String swimlane, Integer increase) {
        trace("Swimlane:" + swimlane + " count:" + increase);

        String register_name;
        if (swimlane != null && swimlane.length() > 0) {
            register_name = swimlane;
        } else {
            register_name = NONE_NAME;
        }

        Integer count = count_action_.getOrDefault(register_name, 0);
        count += increase;
        count_action_.put(register_name, count);
    }

}
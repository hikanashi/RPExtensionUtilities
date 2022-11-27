package com.ibm.rhapsody.rputilities.doxygen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;

public class DoxygenObjectManager extends ARPObject {
    private class TagMap extends HashMap<String, DoxygenType> {}
    protected Map<String, TagMap> objectMap_ = new HashMap<String, TagMap>();

    public DoxygenObjectManager() {
        super(DoxygenObjectManager.class);
    }

    public void append(DoxygenType type) {
        if( type == null ) {
            return;
        }

        if(type.getId() == null || type.getTag() == null ) {
            return;
        }

        trace("Create Node Type:" + type.getClass().getSimpleName() + " id:" + type.getId());

        TagMap map = objectMap_.getOrDefault(type.getTag(), null);
        if(map == null) {
            map = new TagMap();
        }

        map.put(type.getId(),type);
        objectMap_.put(type.getTag(), map);
    }

    public int size() {
        int count = 0;
        for(TagMap value : objectMap_.values()) {
            count += value.size();
        }
        return count;
    }

    public Map<String, DoxygenType> getMap(String tag) {
        return objectMap_.getOrDefault(tag, null);
    }

    public List<DoxygenType> getList(TAGTYPE type) {
        TagMap map = null;
    
        map = objectMap_.getOrDefault(type.getTag(), null);
        if(map == null) {
            return null;
        }

        List<DoxygenType> list = new ArrayList<DoxygenType>();
        for(DoxygenType child : map.values()) {
            if(child.equals(type)) {
                list.add(child);
            }
        }  

        return list;
    }

    public DoxygenType getObject(String tag, String id) {
        TagMap map = null;
        map = objectMap_.getOrDefault(tag, null);
        if(map == null) {
            return null;
        }

        DoxygenType obj = map.getOrDefault(id, null);

        return obj;
    }
}

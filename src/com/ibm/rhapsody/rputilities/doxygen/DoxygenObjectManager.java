package com.ibm.rhapsody.rputilities.doxygen;

import java.util.HashMap;
import java.util.Map;

import com.ibm.rhapsody.rputilities.rpcore.ARPObject;

public class DoxygenObjectManager extends ARPObject {
    public class TagMap extends HashMap<String, DoxygenType> {}
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

    public TagMap getMap(String tag) {
        return objectMap_.getOrDefault(tag, null);
    }

    public DoxygenType getObject(String tag, String id) {
        TagMap map = getMap(tag);
        if(map == null) {
            return null;
        }

        DoxygenType obj = map.getOrDefault(id, null);

        return obj;
    }
}

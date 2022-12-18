package com.ibm.rhapsody.rputilities.doxygen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ibm.rhapsody.rputilities.doxygen.type.DoxygenType;
import com.ibm.rhapsody.rputilities.rpcore.ARPObject;

public class DoxygenObjectManager extends ARPObject {
    private class TagMap extends HashMap<String, DoxygenType> {
    }

    protected Map<String, TagMap> objectMap_ = new HashMap<String, TagMap>();
    protected boolean fullimport_ = false;

    public DoxygenObjectManager() {
        super(DoxygenObjectManager.class);
    }

    public boolean isFullImport() {
        return fullimport_;
    }

    public void setFullImport(boolean value) {
        fullimport_ = value;
    }

    public void append(DoxygenType type) {
        if (type == null) {
            return;
        }

        if (type.getId().length() < 1 || type.getTag().length() < 1) {
            return;
        }

        TagMap map = objectMap_.getOrDefault(type.getTag(), null);
        if (map == null) {
            map = new TagMap();
            objectMap_.put(type.getTag(), map);
        }

        trace("add " + type.getClass().getSimpleName() + " name:" + type.getName() + " id:" + type.getId());
        map.put(type.getId(), type);

    }

    public int size() {
        int count = 0;
        for (TagMap value : objectMap_.values()) {
            count += value.size();
        }
        return count;
    }

    public List<DoxygenType> getAllType() {
        List<DoxygenType> list = new ArrayList<DoxygenType>();

        for (TagMap map : objectMap_.values()) {
            for (DoxygenType child : map.values()) {
                trace("doxygen " + child.getClass().getSimpleName() + " name:" + child.getName() + " id:"
                        + child.getId());
                list.add(child);
            }
        }

        return list;
    }

    public Map<String, DoxygenType> getMap(String tag) {
        return objectMap_.getOrDefault(tag, null);
    }

    public List<DoxygenType> popList(TAGTYPE type) {
        TagMap map = null;

        map = objectMap_.getOrDefault(type.getTag(), null);
        if (map == null) {
            return null;
        }

        List<DoxygenType> list = new ArrayList<DoxygenType>();
        for (DoxygenType child : map.values()) {
            if (child.equals(type)) {
                list.add(child);
            }
        }

        for (DoxygenType child : list) {
            map.remove(child.getId());
        }

        return list;
    }

    public DoxygenType removeList(DoxygenType type) {
        TagMap map = null;
        String tag = type.getTag();
        String id = type.getId();

        map = objectMap_.getOrDefault(tag, null);
        if (map == null) {
            return null;
        }

        DoxygenType obj = map.remove(id);
        return obj;
    }

    public DoxygenType getObject(String tag, String id) {
        TagMap map = null;
        map = objectMap_.getOrDefault(tag, null);
        if (map == null) {
            return null;
        }

        DoxygenType obj = map.getOrDefault(id, null);

        return obj;
    }

    public List<DoxygenType> getObjectByName(String tag, String name) {
        List<DoxygenType> names = new ArrayList<DoxygenType>();

        TagMap map = null;
        map = objectMap_.getOrDefault(tag, null);
        if (map == null) {
            return names;
        }

        for (DoxygenType type : map.values()) {
            if (type.getName().equals(name) == true) {
                names.add(type);
            }
        }

        return names;
    }
}

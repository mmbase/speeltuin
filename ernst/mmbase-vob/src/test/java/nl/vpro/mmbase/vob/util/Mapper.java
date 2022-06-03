package nl.vpro.mmbase.vob.util;

import java.util.HashMap;
import java.util.Map;

import org.mmbase.bridge.Cloud;
import org.mmbase.bridge.util.MapNode;

public  class Mapper {
    private Map<String, Object> wrapped = new HashMap<String, Object>();

    public Mapper put(String key, Object value) {
        wrapped.put(key, value);
        return this;
    }

    public Map<String, Object> getMap() {
        return wrapped;
    }
    
    public MapNode getNode(){
        return new MapNode(wrapped, (Cloud)null);
    }
    
    public MapNode getNode(Cloud cloud){
        return new MapNode(wrapped, cloud);
    }
}
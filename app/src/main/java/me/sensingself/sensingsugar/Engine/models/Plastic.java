package me.sensingself.sensingsugar.Engine.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by liujie on 12/15/17.
 */

public class Plastic {
    private Map<String, Object> data = new HashMap<String, Object>();
    private boolean error = false;

    public void init(int width, int height){
        this.data.put("width", width);
        this.data.put("height", height);
        this.data.put("plastic_error", 0);
    }

    private Object get(String key)
    {
        return this.data.containsKey(key)?this.data.get(key):null;
    }

    public void set(String key, Object value)
    {
        this.data.put(key, value);
    }

    public Object getStatus() {
        return this.get("plastic_error");
    }

    public void setError(int error)
    {
        this.error = true;
        this.set("plastic_error", error);
    }

    public void setPoint(String prefix, Map<String, Integer> point)
    {
        this.set(prefix + "_x", point.get("x"));
        this.set(prefix + "_y", point.get("y"));
    }

    public boolean hasError() {
        return this.error;
    }

    public Map<String, Object> toArray()
    {
        return this.data;
    }
}

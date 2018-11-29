package org.medvedev.nikita.services;

import java.util.Map;
import java.util.TreeMap;

public class FluentBuilderStringMap extends TreeMap<String, String> {
    public FluentBuilderStringMap()
    {
        super();
    }
    public FluentBuilderStringMap(Map<? extends String, ? extends String> map)
    {
        super(map);
    }
    public FluentBuilderStringMap fluentPut(String key, Object value)
    {
        this.put(key, value.toString());
        return this;
    }
}

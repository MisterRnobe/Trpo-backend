package org.medvedev.nikita.services;

import java.util.TreeMap;

public class FluidBuilderStringMap extends TreeMap<String, String> {
    public FluidBuilderStringMap fluidPut(String key, Object value)
    {
        this.put(key, value.toString());
        return this;
    }
}

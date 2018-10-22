package org.medvedev.nikita.commands;

import java.util.Map;
import java.util.TreeMap;

public class NotFoundCommand implements AjaxCommand {

    public Object doCommand(Map parameters) {
        return new TreeMap<String, String>(){
            {
                this.put("status", "error");
                this.put("message", "Command not found!");
            }
        };
    }
}

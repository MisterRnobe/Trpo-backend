package org.medvedev.nikita.commands;

import java.util.Map;
import java.util.TreeMap;

public class NotFoundCommand extends AjaxCommand {

    public NotFoundCommand() {
        super(new String[0]);
    }

    public Object doCommand(Map<String, String> parameters) throws HandleError {
        throw new HandleError("Command not found!");
    }
}

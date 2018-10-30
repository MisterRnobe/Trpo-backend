package org.medvedev.nikita.commands;

import org.medvedev.nikita.services.Errors;

import java.util.Map;
import java.util.TreeMap;

public class NotFoundCommand extends AjaxCommand {

    public NotFoundCommand() {
        super(new String[0], false);
    }

    public Object doCommand(Map<String, String> parameters) throws HandleError {
        throw new HandleError(Errors.WRONG_FUNCTION);
    }
}

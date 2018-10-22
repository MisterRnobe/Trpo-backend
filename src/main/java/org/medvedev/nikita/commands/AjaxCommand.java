package org.medvedev.nikita.commands;

import java.util.Map;

public interface AjaxCommand {
    Object doCommand(Map parameters);
}

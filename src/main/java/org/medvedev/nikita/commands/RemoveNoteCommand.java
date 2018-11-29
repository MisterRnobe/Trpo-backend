package org.medvedev.nikita.commands;

import org.medvedev.nikita.services.FluentBuilderStringMap;

import java.sql.SQLException;
import java.util.Map;

public class RemoveNoteCommand extends AjaxCommand {
    public RemoveNoteCommand() {
        super(new String[]{"noteId"}, true);
    }

    @Override
    protected Object doCommand(Map<String, String> parameters) throws HandleError, SQLException {
        int noteId = Integer.parseInt(parameters.get("noteId"));
        connector.delete("notes", "id", noteId);
        return new FluentBuilderStringMap().fluentPut("noteId", noteId);
    }
}

package org.medvedev.nikita.commands;

import org.medvedev.nikita.services.Errors;
import org.medvedev.nikita.services.FluentBuilderStringMap;

import java.sql.SQLException;
import java.util.Map;

public class EditNoteCommand extends AjaxCommand {
    public EditNoteCommand() {
        super(new String[]{"noteId"}, true);
    }

    @Override
    protected Object doCommand(Map<String, String> parameters) throws HandleError, SQLException {
        String title = parameters.get("title");
        String note = parameters.get("note");
        int noteId = Integer.parseInt(parameters.get("noteId"));

        if (note == null && title == null)
            throw new HandleError(Errors.WRONG_REQUEST_PARAMETERS);
        FluentBuilderStringMap map = new FluentBuilderStringMap();
        if (note!=null)
            map.fluentPut("note", note);
        if (title!=null)
            map.fluentPut("title", title);
        connector.update("id", Integer.toString(noteId),
                    map, "notes");
        return new FluentBuilderStringMap(map).fluentPut("noteId", noteId);
    }
}

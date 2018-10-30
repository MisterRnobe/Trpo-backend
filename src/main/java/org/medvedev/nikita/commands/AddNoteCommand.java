package org.medvedev.nikita.commands;

import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.services.Errors;
import org.medvedev.nikita.services.FluidBuilderStringMap;
import org.medvedev.nikita.services.SQLCommon;

import java.sql.SQLException;
import java.util.Map;

public class AddNoteCommand extends AjaxCommand {
    private final MysqlConnector connector = MysqlConnector.getInstance();
    public AddNoteCommand() {
        super(new String[]{"title", "text", "login"}, true);
    }

    @Override
    protected Object doCommand(Map<String, String> parameters) throws HandleError, SQLException {

        String login = parameters.get("login");
        long time = System.currentTimeMillis();
        int id = insertNote(parameters.get("title"), parameters.get("text"), login, time);
        if (id == -1)
            throw new HandleError(Errors.INTERNAL_ERROR);
        return new FluidBuilderStringMap().fluidPut("id", id).fluidPut("time",time);
    }
    private int insertNote(String title, String text, String login, long timeCreated) throws SQLException
    {
        return connector.insert("notes", new FluidBuilderStringMap()
                .fluidPut("title", title).fluidPut("note", text)
                .fluidPut("login", login).fluidPut("time_created", timeCreated));
    }
}

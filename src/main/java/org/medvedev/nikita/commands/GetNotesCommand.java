package org.medvedev.nikita.commands;

import org.apache.log4j.Logger;
import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.objects.Note;
import org.medvedev.nikita.services.Errors;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GetNotesCommand extends AjaxCommand {
    private static final Logger logger = Logger.getLogger(GetNotesCommand.class);
    public GetNotesCommand() {
        super(new String[]{"login", "count", "offset"}, true);
    }

    @Override
    protected Object doCommand(Map<String, String> parameters) throws HandleError, SQLException {
        List<Note> list;
        try {
            list = getNotes(parameters.get("login"), Integer.parseInt(parameters.get("count")), Integer.parseInt(parameters.get("offset")));
        }catch (NumberFormatException e)
        {
            throw new HandleError(Errors.WRONG_REQUEST_PARAMETERS);
        }
        return list;
    }
    private List<Note> getNotes(String login, int count, int offset) throws SQLException {
        return connector.select("SELECT id,title, note, time_created FROM notes WHERE login = ? ORDER BY time_created LIMIT "+offset+", "+count+";",
                rs->{
                    List<Note> list = new LinkedList<>();
                    try{
                        while (rs.next())
                        {
                            Note n = new Note().setId(rs.getInt("id"))
                                    .setTitle(rs.getString("title"))
                                    .setNote(rs.getString("note"))
                                    .setCreated(rs.getLong("time_created"));
                            list.add(n);
                        }
                    } catch (SQLException e) {
                        logger.error("Sql error when selecting notes", e);
                    }
                    return list;
                }, login);
    }
}

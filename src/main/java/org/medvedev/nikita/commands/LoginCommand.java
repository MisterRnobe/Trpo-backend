package org.medvedev.nikita.commands;

import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.objects.Token;
import org.medvedev.nikita.services.FluidBuilderStringMap;
import org.medvedev.nikita.services.Utils;

import java.sql.SQLException;
import java.util.Map;

public class LoginCommand extends AjaxCommand {
    private MysqlConnector connector;
    public LoginCommand() {
        super(new String[]{"login", "password"});
        connector = MysqlConnector.getInstance();
    }

    @Override
    public Object doCommand(Map<String, String> parameters) throws HandleError, SQLException {
        String login = parameters.get("login").toLowerCase();
        String password = Utils.sha256(parameters.get("password"));

        verifyLoginAndPassword(login, password);
        String token = getToken(login);
        return new Token().setToken(token);

    }
    private void verifyLoginAndPassword(String login, String password) throws HandleError, SQLException {
        boolean isOk = connector.select("SELECT login FROM users WHERE login = ? AND password = ?;", rs -> {
            boolean correct = false;
            try {
                if (rs.next())
                    correct = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return correct;
        }, login, password);
        if (!isOk)
            throw new HandleError("Wrong login or password!");

    }
    private String getToken(String login) throws SQLException
    {
        long currTime = System.currentTimeMillis();
        String token = connector.select("SELECT token FROM tokens WHERE expires > "+currTime+" AND login = ?;", rs->
        {
           String t = null;
           try {
               if (rs.next())
                   t = rs.getString(1);
           } catch (SQLException e) {
               e.printStackTrace();
           }
           return t;
        }, login);
        if (token == null)
        {
            token = Utils.generateToken(login);
            long newTime = currTime + Utils.WEEK;
            connector.update("login", login, new FluidBuilderStringMap().fluidPut("token", token).fluidPut("expires", newTime), "tokens");
        }
        return token;

    }
}

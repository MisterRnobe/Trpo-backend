package org.medvedev.nikita.commands;

import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.objects.Token;
import org.medvedev.nikita.services.FluidBuilderStringMap;
import org.medvedev.nikita.services.Utils;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterCommand extends AjaxCommand {
    private static MysqlConnector connector = MysqlConnector.getInstance();
    public RegisterCommand() {
        super(new String[]{"login", "first_name", "second_name", "email", "password"});
    }

    @Override
    public Object doCommand(Map<String, String> parameters) throws HandleError, SQLException {
        String login = parameters.get("login").toLowerCase();
        String email = parameters.get("email").toLowerCase();
        String password =parameters.get("password");
        checkLogin(login);
        checkEmail(email);
        checkPassword(password);
        parameters.computeIfPresent("password", (key, val) -> Utils.sha256(val));

        connector.insert("users", parameters);

        String token = Utils.generateToken(login);
        connector.insert("tokens", new FluidBuilderStringMap().fluidPut("login", login).fluidPut("token", token)
                .fluidPut("expires", System.currentTimeMillis() + Utils.WEEK));
        return new Token().setToken(token);
    }
    private void checkLogin(String login) throws HandleError, SQLException
    {
        if (login.length()> 15)
            throw new HandleError("Login is too long!");
        if (login.length() < 6)
            throw new HandleError("Login is too short!");

        final String regex = "^[A-z_-]*$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(login);

        if (!matcher.find()) {
            throw new HandleError("Wrong login!");
        }

        boolean exists = connector.select("SELECT login FROM users WHERE login = ?;", rs->{
            boolean ok = true;
            try
            {
                ok = rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ok;
        }, login);
        if (exists)
            throw new HandleError("Login exists!");
    }
    private void checkEmail(String email) throws HandleError, SQLException {
        final String regex = "^[-\\w.]+@([A-z0-9][-A-z0-9]+\\.)+[A-z]{2,4}$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(email);

        if (!matcher.find()) {
            throw new HandleError("Wrong email!");
        }

        boolean exists = connector.select("SELECT login FROM users WHERE email = ?;", rs->{
            boolean ok = true;
            try
            {
                ok = rs.next();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return ok;
        }, email);
        if (exists)
            throw new HandleError("Email exists!");

    }
    private void checkPassword(String password) throws HandleError
    {
        if (password.length() < 6)
            throw new HandleError("Password is too short!");
    }

}

package org.medvedev.nikita.commands;

import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.objects.Token;
import org.medvedev.nikita.objects.UserData;
import org.medvedev.nikita.services.Errors;
import org.medvedev.nikita.services.FluentBuilderStringMap;
import org.medvedev.nikita.services.Utils;

import java.sql.SQLException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterCommand extends AjaxCommand {
    private static MysqlConnector connector = MysqlConnector.getInstance();
    public RegisterCommand() {
        super(new String[]{"login", "first_name", "second_name", "email", "password"}, false);
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

        UserData userData = new UserData()
                .setLogin(login)
                .setEmail(email)
                .setFirstName(parameters.get("first_name"))
                .setSecondName(parameters.get("second_name"));

        String token = Utils.generateToken(userData);
        //connector.insert("tokens", new FluentBuilderStringMap().fluentPut("login", login).fluentPut("token", token)
        //        .fluentPut("expires", System.currentTimeMillis() + Utils.WEEK));
        return new Token().setToken(token);
    }
    private void checkLogin(String login) throws HandleError, SQLException
    {
        if (login.length()> 15)
            throw new HandleError(Errors.LONG_LOGIN);
        if (login.length() < 6)
            throw new HandleError(Errors.SHORT_LOGIN);

        final String regex = "^[A-z_-]*$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(login);

        if (!matcher.find()) {
            throw new HandleError(Errors.WRONG_LOGIN_SYMBOLS);
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
            throw new HandleError(Errors.LOGIN_EXISTS);
    }
    private void checkEmail(String email) throws HandleError, SQLException {
        final String regex = "^[-\\w.]+@([A-z0-9][-A-z0-9]+\\.)+[A-z]{2,4}$";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(email);

        if (!matcher.find()) {
            throw new HandleError(Errors.WRONG_MAIL);
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
            throw new HandleError(Errors.MAIL_EXISTS);

    }
    private void checkPassword(String password) throws HandleError
    {
        if (password.length() < 6)
            throw new HandleError(Errors.SHORT_PASSWORD);
    }

}

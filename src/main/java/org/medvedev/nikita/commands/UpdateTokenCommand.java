package org.medvedev.nikita.commands;

import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.objects.Token;
import org.medvedev.nikita.objects.UserData;
import org.medvedev.nikita.services.Errors;
import org.medvedev.nikita.services.FluentBuilderStringMap;
import org.medvedev.nikita.services.SQLCommon;
import org.medvedev.nikita.services.Utils;

import java.sql.SQLException;
import java.util.Map;

public class UpdateTokenCommand extends AjaxCommand {
    public UpdateTokenCommand() {
        super(new String[]{"token"}, false);
    }

    @Override
    protected Object doCommand(Map<String, String> parameters) throws HandleError, SQLException {
        String token = parameters.get("token");
        UserData userData = Utils.decodeToken(token);

        String newToken = LoginCommand.getToken(userData.getLogin());

        return new Token().setToken(newToken);
    }
}

package org.medvedev.nikita.commands;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.services.Errors;
import org.medvedev.nikita.services.SQLCommon;

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AjaxCommand {
    private final String[] requiredFields;
    public static final int OK = 0;
    public static final int ERROR = 1;
    public final boolean needToken;
    private static final Logger logger = Logger.getLogger(AjaxCommand.class);
    protected MysqlConnector connector = MysqlConnector.getInstance();

    AjaxCommand(String[] requiredFields, boolean needToken)
    {
        this.requiredFields = requiredFields;
        this.needToken = needToken;
    }

    protected abstract Object doCommand(Map<String, String> parameters) throws HandleError, SQLException;
    public final String execute(Map<?,?> parameters)
    {
        JSONObject response = new JSONObject();
        Map<String, String> stringMap = convertToStringMap(parameters);
        if (parameters.isEmpty())
        {
            response.put("status", ERROR);
            response.put("message", Errors.EMPTY_BODY);
            return JSON.toJSONString(response);
        }

        if (needToken)
        {
            try{
                String login = checkToken(stringMap);
                stringMap.put("login", login);
            } catch (HandleError handleError) {
                response.put("status", ERROR);
                response.put("message", handleError.getCode());
            } catch (SQLException e) {
                response.put("status", ERROR);
                response.put("message", Errors.INTERNAL_ERROR);
                logger.error("SQL error", e);
            }
        }

        boolean missing = false;
        for (String s: requiredFields) {
            if (stringMap.get(s) == null) {
                missing = true;
                break;
            }
        }

        if (missing)
        {
            response.put("status", ERROR);
            response.put("message", Errors.WRONG_REQUEST_PARAMETERS);
        }
        else {
            try {
                Object o = doCommand(stringMap);
                response.put("status", OK);
                response.put("body", o);
            }
            catch (HandleError e)
            {
                response.put("status", ERROR);
                response.put("message", e.getCode());
            }
            catch (SQLException sqlException)
            {
                response.put("status", ERROR);
                response.put("message", Errors.INTERNAL_ERROR);
                logger.error("DB error occurred "+sqlException.getMessage());
            }

        }
        return JSON.toJSONString(response);
    }
    private Map<String, String> convertToStringMap(Map<?,?> map)
    {
        return map.entrySet().stream().map(e-> new AbstractMap.SimpleEntry<>((String) e.getKey(), ((String[]) e.getValue())[0]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
    private String checkToken(Map<String, String> map) throws SQLException, HandleError
    {
        String token = map.get("token");
        if (token == null)
            throw new HandleError(Errors.REQUIRES_TOKEN);
        if (SQLCommon.isTokenExpired(token))
            throw new HandleError(Errors.EXPIRED_TOKEN);
        String login = SQLCommon.getUserByToken(token);
        if (login == null)
            throw new HandleError(Errors.BAD_TOKEN);
        logger.info("Login is "+login);
        return login;
    }
}

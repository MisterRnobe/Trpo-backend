package org.medvedev.nikita.commands;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureException;
import org.apache.log4j.Logger;
import org.medvedev.nikita.database.MysqlConnector;
import org.medvedev.nikita.objects.UserData;
import org.medvedev.nikita.services.Errors;
import org.medvedev.nikita.services.SQLCommon;
import org.medvedev.nikita.services.Utils;

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

    AjaxCommand(String[] requiredFields, boolean needToken) {
        this.requiredFields = requiredFields;
        this.needToken = needToken;
    }

    protected abstract Object doCommand(Map<String, String> parameters) throws HandleError, SQLException;

    public final String execute(Map<?, ?> parameters) {
        JSONObject response = new JSONObject();
        Map<String, String> stringMap = convertToStringMap(parameters);
        if (parameters.isEmpty()) {
            response.put("status", ERROR);
            response.put("message", Errors.EMPTY_BODY);
            return JSON.toJSONString(response);
        }

        if (needToken) {
            boolean failed = false;
            try {
                String login = checkToken(stringMap);
                stringMap.put("login", login);
            } catch (HandleError handleError) {
                response.put("status", ERROR);
                response.put("message", handleError.getCode());
                logger.warn("Handle error occurred!", handleError);
                failed = true;
            } catch (SQLException e) {
                response.put("status", ERROR);
                response.put("message", Errors.INTERNAL_ERROR);
                logger.error("SQL error", e);
                failed = true;
            }
            if (failed)
                return JSON.toJSONString(response);
        }

        boolean missing = false;
        for (String s : requiredFields) {
            if (stringMap.get(s) == null) {
                missing = true;
                break;
            }
        }

        if (missing) {
            response.put("status", ERROR);
            response.put("message", Errors.WRONG_REQUEST_PARAMETERS);
        } else {
            try {
                Object o = doCommand(stringMap);
                response.put("status", OK);
                response.put("body", o);
            } catch (HandleError e) {
                response.put("status", ERROR);
                response.put("message", e.getCode());
            } catch (SQLException sqlException) {
                response.put("status", ERROR);
                response.put("message", Errors.INTERNAL_ERROR);
                logger.error("DB error occurred " + sqlException.getMessage());
            }

        }
        return JSON.toJSONString(response);
    }

    private Map<String, String> convertToStringMap(Map<?, ?> map) {
        return map.entrySet().stream().map(e -> new AbstractMap.SimpleEntry<>((String) e.getKey(), ((String[]) e.getValue())[0]))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    private String checkToken(Map<String, String> map) throws SQLException, HandleError {
        String token = map.get("token");
        String login = null;
        try {
            UserData userData = Utils.decodeToken(token);
            //language=SQL
            String sqlLogin = connector.select("SELECT login FROM users WHERE login = ?",
                    rs -> {
                        String loginSql = null;
                        try {
                            if (rs.next())
                                loginSql = rs.getString(1);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return loginSql;
                    }, userData.getLogin());
            if (userData.getLogin().equalsIgnoreCase(sqlLogin))
                login = sqlLogin;
        } catch (ExpiredJwtException expired) {
            logger.warn("Expired token received", expired);
            throw new HandleError(Errors.EXPIRED_TOKEN);
        } catch (JwtException e) {
            logger.warn("Unable to decode token", e);
            throw new HandleError(Errors.BAD_TOKEN);
        }
        if (login == null) {
            throw new HandleError(Errors.BAD_TOKEN);
        }
        return login;
    }
}

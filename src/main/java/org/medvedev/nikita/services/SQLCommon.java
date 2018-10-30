package org.medvedev.nikita.services;

import org.apache.log4j.Logger;
import org.medvedev.nikita.database.MysqlConnector;

import java.sql.SQLException;

public class SQLCommon {
    private static final Logger logger = Logger.getLogger(SQLCommon.class);
    public static String getUserByToken(String token) throws SQLException {
        return MysqlConnector.getInstance().select("SELECT login FROM tokens WHERE token = ?;", rs->{
            String login = null;
            try {
                if (rs.next())
                    login = rs.getString(1);
            } catch (SQLException e) {
                logger.error("SQL error occurred: ", e);
            }
            return login;
        } ,token);
    }
    public static boolean isTokenExpired(String token) throws SQLException
    {
        return MysqlConnector.getInstance().select("SELECT expires FROM tokens WHERE token = ?;", rs->{
            boolean expired = true;
            try {
                if (rs.next()) {
                    long expires = rs.getLong(1);
                    expired = System.currentTimeMillis() > expires;
                }
            } catch (SQLException e) {
                logger.error("SQL error occurred: ", e);
            }
            return expired;
        } ,token);
    }
}

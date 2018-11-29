package org.medvedev.nikita.database;

import com.alibaba.fastjson.JSON;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MysqlConnector {
    private static MysqlConnector instance;
    private static final Logger logger = Logger.getLogger(MysqlConnector.class);

    public static MysqlConnector getInstance() {
        if (instance == null)
            instance = new MysqlConnector();
        return instance;
    }

    private Connection connection;

    private MysqlConnector() {
        ConnectionData connectionData = readFromFile();
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionData.getUrl(), connectionData.getLogin(), connectionData.getPassword());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class ConnectionData {
        private String url, login, password;

        public String getUrl() {
            return url;
        }

        public ConnectionData setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getLogin() {
            return login;
        }

        public ConnectionData setLogin(String login) {
            this.login = login;
            return this;
        }

        public String getPassword() {
            return password;
        }

        public ConnectionData setPassword(String password) {
            this.password = password;
            return this;
        }
    }

    public static ConnectionData readFromFile() {
        StringBuilder file = new StringBuilder();
        try (FileInputStream outputStream = new FileInputStream(MysqlConnector.class.getResource("connection.txt").getFile());
             Scanner scanner = new Scanner(outputStream)) {

            while (scanner.hasNext())
                file.append(scanner.nextLine());

        } catch (FileNotFoundException e) {
            logger.error("FILE NOT FOUND WHEN READING FROM FILE", e);
        } catch (IOException e) {
            logger.error("IOEXCEPTION OCCURRED WHEN READING FROM FILE", e);
        }
        if (file.length() == 0)
            return new ConnectionData();
        else
            return JSON.parseObject(file.toString(), ConnectionData.class);
    }

    public void update(String field, String id, Map<String, String> values, String table) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            String newValues = values.entrySet().stream()
                    .map(e -> e.getKey() + " = '" + e.getValue() + "'")
                    .collect(Collectors.joining(", "));
            String query = "UPDATE " + table + " SET " + newValues + " WHERE " + field + " = '" + id + "';";
            logger.info("Executing update: " + query);
            statement.executeUpdate(query);
        }
    }

    public synchronized int insert(String table, Map<String, String> values) throws SQLException {
        final List<String> keys = new ArrayList<>(values.keySet());

        String fields = "(" + String.join(",", keys) + ")";

        StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ");
        builder.append(table);
        builder.append(" ");
        builder.append(fields);
        builder.append(" VALUES (");
        for (int i = 0; i < keys.size(); i++) {
            builder.append("?,");
        }
        builder.setCharAt(builder.length() - 1, ')');
        builder.append(";");
        logger.info("Executing insert: " + builder.toString());
        PreparedStatement preparedStatement = connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);
        int lastKey = -1;
        for (int i = 0; i < keys.size(); i++) {
            preparedStatement.setString(i + 1, values.get(keys.get(i)));
        }
        preparedStatement.executeUpdate();
        try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
            if (resultSet.next())
                lastKey = resultSet.getInt(1);
        }
        preparedStatement.close();
        return lastKey;

    }


    public <E> E select(String query, Function<ResultSet, E> consumer, String... values) throws SQLException {
        E result;
        logger.info("Executing select: " + query);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (int i = 0; i < values.length; i++) {
                statement.setString(i + 1, values[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                result = consumer.apply(resultSet);
            }
        }
        return result;
    }

    public void delete(String table, String column, Object value) throws SQLException {
        //language=SQL
        String query = "DELETE FROM " + table + " WHERE " + column + " = ?;";
        logger.info("Executing delete: " + query);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, value.toString());
            statement.execute();
        }
    }

}

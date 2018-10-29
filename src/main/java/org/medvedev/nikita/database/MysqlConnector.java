package org.medvedev.nikita.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MysqlConnector {
    private static MysqlConnector instance;

    public static MysqlConnector getInstance() {
        if (instance == null)
            instance = new MysqlConnector();
        return instance;
    }

    private Connection connection;

    private MysqlConnector()
    {
        String url = "jdbc:mysql://localhost:3306/trpo";
        String login = "root";
        String pass = "nikita";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, login, pass);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void update(String field, String id, Map<String, String> values, String table) throws SQLException {
        try(Statement statement = connection.createStatement())
        {
            String newValues = values.entrySet().stream()
                    .map(e -> e.getKey() + " = '" + e.getValue()+"'")
                    .collect(Collectors.joining(", "));
            String query = "UPDATE " + table + " SET " + newValues + " WHERE " + field + " = '" + id + "';";
            System.out.println(query);
            statement.executeUpdate(query);
        }
    }

    public synchronized int insert(String table, Map<String, String> values) throws SQLException
    {
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
        builder.setCharAt(builder.length()-1, ')');
        builder.append(";");
        System.out.println(builder.toString());
        PreparedStatement preparedStatement = connection.prepareStatement(builder.toString(), Statement.RETURN_GENERATED_KEYS);
        int lastKey = -1;
        for(int i = 0; i < keys.size(); i++)
        {
            preparedStatement.setString(i+1, values.get(keys.get(i)));
        }
        preparedStatement.executeUpdate();
        try( ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
            if (resultSet.next())
                lastKey = resultSet.getInt(1);
        }
        preparedStatement.close();
        return lastKey;

    }


    public synchronized  <E> E select(String query, Function<ResultSet, E> consumer, String... values) throws SQLException {
        E result;
        System.out.println(query);
        try(PreparedStatement statement = connection.prepareStatement(query))
        {
            for (int i = 0; i < values.length; i++) {
                statement.setString(i+1, values[i]);
            }
            try(ResultSet resultSet = statement.executeQuery())
            {
                result = consumer.apply(resultSet);
            }
        }
        return result;
    }

}

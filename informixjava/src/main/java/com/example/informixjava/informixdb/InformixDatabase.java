package com.example.informixjava.informixdb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public final class InformixDatabase implements AutoCloseable {
    private final Connection connection;

    private InformixDatabase(Connection connection) {
        this.connection = connection;
    }

    public static InformixDatabase connect(InformixConnectionConfig config) throws SQLException {
        Objects.requireNonNull(config, "La configuración de conexión es obligatoria.");
        Properties properties = new Properties();
        properties.setProperty("user", config.username());
        properties.setProperty("password", config.password());
        properties.setProperty("INFORMIXSERVER", config.serverName());
        return new InformixDatabase(DriverManager.getConnection(config.jdbcUrl(), properties));
    }

    public synchronized InformixQueryResult execute(String sql, List<?> parameters) throws SQLException {
        Objects.requireNonNull(parameters, "La lista de parámetros no puede ser nula.");
        try (PreparedStatement statement = prepare(sql, parameters)) {
            if (statement.execute()) {
                try (ResultSet resultSet = statement.getResultSet()) {
                    return readResultSet(resultSet);
                }
            }
            return new InformixQueryResult(List.of(), List.of(), statement.getUpdateCount());
        }
    }

    public synchronized InformixQueryResult executeQuery(String sql, List<?> parameters)
            throws SQLException {
        Objects.requireNonNull(parameters, "La lista de parámetros no puede ser nula.");
        try (PreparedStatement statement = prepare(sql, parameters);
                ResultSet resultSet = statement.executeQuery()) {
            return readResultSet(resultSet);
        }
    }

    public synchronized int executeUpdate(String sql, List<?> parameters) throws SQLException {
        Objects.requireNonNull(parameters, "La lista de parámetros no puede ser nula.");
        try (PreparedStatement statement = prepare(sql, parameters)) {
            return statement.executeUpdate();
        }
    }

    private PreparedStatement prepare(String sql, List<?> parameters) throws SQLException {
        Objects.requireNonNull(sql, "La consulta SQL no puede ser nula.");
        if (sql.isBlank()) {
            throw new IllegalArgumentException("La consulta SQL no puede estar vacía.");
        }

        PreparedStatement statement = connection.prepareStatement(sql);
        try {
            for (int index = 0; index < parameters.size(); index++) {
                statement.setObject(index + 1, parameters.get(index));
            }
            return statement;
        } catch (SQLException | RuntimeException exception) {
            try {
                statement.close();
            } catch (SQLException closeException) {
                exception.addSuppressed(closeException);
            }
            throw exception;
        }
    }

    private InformixQueryResult readResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metadata = resultSet.getMetaData();
        int columnCount = metadata.getColumnCount();
        List<String> columns = new ArrayList<>(columnCount);
        for (int index = 1; index <= columnCount; index++) {
            columns.add(metadata.getColumnLabel(index));
        }

        List<List<Object>> rows = new ArrayList<>();
        while (resultSet.next()) {
            List<Object> row = new ArrayList<>(columnCount);
            for (int index = 1; index <= columnCount; index++) {
                row.add(resultSet.getObject(index));
            }
            rows.add(row);
        }
        return new InformixQueryResult(columns, rows, -1);
    }

    @Override
    public synchronized void close() throws SQLException {
        connection.close();
    }
}

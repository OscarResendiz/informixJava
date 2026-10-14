package com.example.informixjava.informixdb;

import java.util.Objects;

public record InformixConnectionConfig(
        String nombreConexion,
        String host,
        int port,
        String database,
        String serverName,
        String username,
        String password) {

    public InformixConnectionConfig {
        nombreConexion = requireText(nombreConexion, "El nombre de la conexión es obligatorio.");
        host = requireText(host, "El host es obligatorio.");
        database = requireIdentifier(database, "La base de datos");
        serverName = requireIdentifier(serverName, "El nombre del servidor Informix");
        username = requireText(username, "El usuario es obligatorio.");
        password = Objects.requireNonNull(password, "La contraseña no puede ser nula.");

        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 1 y 65535.");
        }
        if (host.contains("/") || host.contains(";") || host.chars().anyMatch(Character::isWhitespace)) {
            throw new IllegalArgumentException("El host no puede contener espacios, '/' ni ';'.");
        }
    }

    public String jdbcUrl() {
        String jdbcHost = host.contains(":") && !host.startsWith("[") ? "[" + host + "]" : host;
        return "jdbc:informix-sqli://" + jdbcHost + ":" + port + "/" + database;
    }

    @Override
    public String toString() {
        return "InformixConnectionConfig[nombreConexion=" + nombreConexion
                + ", host=" + host
                + ", port=" + port
                + ", database=" + database
                + ", serverName=" + serverName
                + ", username=" + username
                + ", password=****]";
    }

    private static String requireText(String value, String message) {
        Objects.requireNonNull(value, message);
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    private static String requireIdentifier(String value, String fieldName) {
        String identifier = requireText(value, fieldName + " es obligatoria.");
        if (!identifier.matches("[A-Za-z0-9_$.-]+")) {
            throw new IllegalArgumentException(
                    fieldName + " solo puede contener letras, números, '_', '$', '.' y '-'.");
        }
        return identifier;
    }
}

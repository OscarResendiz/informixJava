package com.example.informixjava.AdministradorConexiones;

import java.util.Objects;

public record CDatosConexion(
        String nombreConexion,
        String host,
        int puerto,
        String baseDatos,
        String servidorInformix,
        String usuario,
        String contrasena) {

    public CDatosConexion {
        nombreConexion = requireText(nombreConexion, "El nombre de la conexión es obligatorio.");
        host = requireText(host, "El host es obligatorio.");
        baseDatos = requireText(baseDatos, "La base de datos es obligatoria.");
        servidorInformix = requireText(servidorInformix, "El servidor Informix es obligatorio.");
        usuario = requireText(usuario, "El usuario es obligatorio.");
        contrasena = Objects.requireNonNull(contrasena, "La contraseña no puede ser nula.");
        if (puerto < 1 || puerto > 65535) {
            throw new IllegalArgumentException("El puerto debe estar entre 1 y 65535.");
        }
    }

    private static String requireText(String value, String message) {
        Objects.requireNonNull(value, message);
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }
}

package com.example.informixjava.AdministradorConexiones;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.regex.Pattern;

public class CAdministradorConexiones {
    private static CAdministradorConexiones AdministradorConexiones;
    private static final String EXTENSION = ".con";
    private static final Pattern CARACTERES_INVALIDOS =
            Pattern.compile("[\\\\/:*?\"<>|\\p{Cntrl}]");
    private static final Pattern NOMBRE_RESERVADO =
            Pattern.compile("(?i)^(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(\\..*)?$");

    private final Path carpetaConexiones;
    private final ObjectMapper objectMapper;

    private  CAdministradorConexiones() {
        this(Path.of("conexionesInformix"));
    }

    public static  CAdministradorConexiones DameAdministradorConexiones()
    {
        if(AdministradorConexiones == null)
        {
            AdministradorConexiones=new CAdministradorConexiones();
        }
        return AdministradorConexiones;
    }
    public CAdministradorConexiones(Path carpetaConexiones) {
        this.carpetaConexiones = carpetaConexiones.toAbsolutePath().normalize();
        this.objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    }

    public void agregarConexion(
            String nombreConexion,
            String host,
            int puerto,
            String baseDatos,
            String servidorInformix,
            String usuario,
            String contrasena) throws IOException {
        CDatosConexion datos = new CDatosConexion(
                validarNombre(nombreConexion),
                host,
                puerto,
                baseDatos,
                servidorInformix,
                usuario,
                contrasena);
        Files.createDirectories(carpetaConexiones);

        Path archivo = archivoConexion(datos.nombreConexion());
        try (OutputStream output = Files.newOutputStream(
                archivo, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
            objectMapper.writeValue(output, datos);
        }
    }

    public List<String> listaConexiones() throws IOException {
        Files.createDirectories(carpetaConexiones);
        try (var archivos = Files.list(carpetaConexiones)) {
            return archivos
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(nombre -> nombre.endsWith(EXTENSION))
                    .map(nombre -> nombre.substring(0, nombre.length() - EXTENSION.length()))
                    .sorted(String.CASE_INSENSITIVE_ORDER)
                    .toList();
        }
    }

    public CDatosConexion dameConexion(String nombreConexion) throws IOException {
        String nombreValidado = validarNombre(nombreConexion);
        Files.createDirectories(carpetaConexiones);
        Path archivo = archivoConexion(nombreValidado);
        if (!Files.isRegularFile(archivo)) {
            throw new NoSuchFileException(archivo.toString());
        }
        return objectMapper.readValue(archivo.toFile(), CDatosConexion.class);
    }

    private Path archivoConexion(String nombreConexion) {
        return carpetaConexiones.resolve(nombreConexion + EXTENSION);
    }

    private String validarNombre(String nombreConexion) {
        if (nombreConexion == null || nombreConexion.isBlank()) {
            throw new IllegalArgumentException("El nombre de la conexión es obligatorio.");
        }

        String nombre = nombreConexion.trim();
        if (nombre.equals(".")
                || nombre.equals("..")
                || nombre.endsWith(".")
                || nombre.endsWith(" ")
                || CARACTERES_INVALIDOS.matcher(nombre).find()
                || NOMBRE_RESERVADO.matcher(nombre).matches()) {
            throw new IllegalArgumentException(
                    "El nombre de la conexión contiene caracteres no permitidos para un archivo.");
        }
        return nombre;
    }
    public void ActualizarConexion(
            String nombreConexion,
            String host,
            int puerto,
            String baseDatos,
            String servidorInformix,
            String usuario,
            String contrasena) throws IOException {
        CDatosConexion datos = new CDatosConexion(
                validarNombre(nombreConexion),
                host,
                puerto,
                baseDatos,
                servidorInformix,
                usuario,
                contrasena);
        Files.createDirectories(carpetaConexiones);

        Path archivo = archivoConexion(datos.nombreConexion());
        try (OutputStream output = Files.newOutputStream(
                archivo, StandardOpenOption.WRITE, StandardOpenOption.WRITE)) {
            objectMapper.writeValue(output, datos);
        }
    }
    public void eliminarConexion(String nombreConexion) throws IOException
    {
        Path archivo = archivoConexion(nombreConexion);
        Files.deleteIfExists(archivo);
    }
}

# Informix JavaFX

Proyecto de escritorio Java creado con Maven y JavaFX. La ventana incluye un menú superior, una barra de herramientas, un panel izquierdo para controles de navegación y un área de trabajo principal. Las opciones visibles son ejemplos sin acciones implementadas.

## Conexión a Informix

En **Archivo > Conectar a Informix...**, captura el host, puerto (9088 por defecto),
base de datos, nombre del servidor Informix (`INFORMIXSERVER`), usuario y contraseña.
Al conectar se abre una consola SQL; los valores de contraseña se mantienen en memoria
y no se guardan en archivos.

La consola acepta una consulta SQL y valores de parámetros enlazados con `?`, uno por
línea y en el orden correspondiente. Escribe `NULL` para enviar un valor SQL nulo;
los demás valores se enlazan como texto. Para consultas `SELECT` se muestran los
resultados; para otras operaciones se informa el número de filas afectadas.

El paquete `com.example.informixjava.informixdb` también expone `InformixDatabase`,
con los métodos `execute`, `executeQuery` y `executeUpdate`, que aceptan la sentencia
SQL y una lista de parámetros JDBC.

## Requisitos

- JDK 25 (LTS)
- Maven 3.8 o posterior
- Controlador JDBC de Informix 4.50.11.2 (Maven lo descarga automáticamente)

## Ejecutar

Desde esta carpeta, ejecuta:

```powershell
mvn javafx:run
```

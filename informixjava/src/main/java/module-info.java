module com.example.informixjava {
    requires transitive javafx.controls;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    uses java.sql.Driver;

    exports com.example.informixjava;
    exports com.example.informixjava.informixdb;
    exports com.example.informixjava.AdministradorConexiones;
}

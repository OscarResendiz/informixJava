module com.example.informixjava {
    requires transitive javafx.controls;
    requires java.sql;

    uses java.sql.Driver;

    exports com.example.informixjava;
    exports com.example.informixjava.informixdb;
}

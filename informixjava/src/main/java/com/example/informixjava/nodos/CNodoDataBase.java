package com.example.informixjava.nodos;

import com.example.informixjava.AdministradorConexiones.CDatosConexion;
import com.example.informixjava.informixdb.InformixConnectionConfig;
import com.example.informixjava.informixdb.InformixDatabase;
import com.example.informixjava.informixdb.InformixSqlConsole;

import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import java.util.function.Consumer;

public class CNodoDataBase extends CNodoBase{
    private final String NombreDB;
    private final Consumer<String> textoHandler;
    CDatosConexion datosConexion;
    public CNodoDataBase(String nombreDB, CDatosConexion datosConexionServidor, Consumer<String> textoHandler)
    {
        NombreDB=nombreDB;
        super(nombreDB);
        this.textoHandler = textoHandler;
        datosConexion= new CDatosConexion(NombreDB, datosConexionServidor.host(),datosConexionServidor.puerto(),NombreDB,datosConexionServidor.servidorInformix(),datosConexionServidor.usuario(),datosConexionServidor.contrasena());
        creaIcono("Ð", "tree-icon-connection");
        creaMenu();
        CargaTablas();
        CargaProcedimientos();
    }
    private void creaMenu() {
        addMenuItem("Hacer consulta", "↻", "menu-icon-connect", () -> consultar());
        //addMenuItem("Editar conexión", "✎", "menu-icon-edit", () -> editarConexion());
        //addMenuItem("Eliminar conexión", "✕", "menu-icon-delete", () -> eliminarConexion());
    }
    private void CargaTablas() {
        TreeItem<String> tables = new TreeItem<>("Tablas");
        Label tablesIcon = new Label("▤");
        tablesIcon.getStyleClass().addAll("tree-icon", "tree-icon-category");
        tables.setGraphic(tablesIcon);
        getChildren().add(tables);
       // setExpanded(true);

    }
    private void CargaProcedimientos() {
        CNodoProcedimientosAlmacenados nodoProcedimientosAlmacenados=
                new CNodoProcedimientosAlmacenados(NombreDB, datosConexion, textoHandler);
        getChildren().add(nodoProcedimientosAlmacenados);
    }
    private void consultar()
    {
        InformixConnectionConfig config = new InformixConnectionConfig(datosConexion.nombreConexion(), datosConexion.host(),
                    datosConexion.puerto(), datosConexion.baseDatos(), datosConexion.servidorInformix(), datosConexion.usuario(), datosConexion.contrasena());

        Task<InformixDatabase> connectionTask = new Task<>() {
            @Override
            protected InformixDatabase call() throws Exception {
                return InformixDatabase.connect(config);
            }
        };
        connectionTask.setOnSucceeded(event -> {
            new InformixSqlConsole(obtenerVentana(), connectionTask.getValue()).show();
        });
        connectionTask.setOnFailed(event -> {
            mostrarError("Error", connectionTask.getException().toString());
        });
        Thread connectionThread = new Thread(connectionTask, "informix-connection");
        connectionThread.setDaemon(true);
        connectionThread.start();

    }
}

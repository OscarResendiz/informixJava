package com.example.informixjava.nodos;

import com.example.informixjava.AdministradorConexiones.CAdministradorConexiones;
import com.example.informixjava.informixdb.InformixConnectionConfig;
import com.example.informixjava.informixdb.InformixConnectionDialog;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.Objects;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Window;
import com.example.informixjava.nodos.CNodoConexion;

public class CNodoConexiones extends CNodoBase {
    private final CAdministradorConexiones administradorConexiones = CAdministradorConexiones.DameAdministradorConexiones();
    private final Consumer<String> textoHandler;
    private Consumer<InformixConnectionConfig> conexionCreadaHandler = config -> {
    };

    public CNodoConexiones(Consumer<String> textoHandler) {
        super("Conexiones");
        this.textoHandler = Objects.requireNonNull(textoHandler);
        creaIcono("◆", "tree-icon-connection");
        creaMenu();
        cargarConexionesGuardadas();
    }

    private void creaMenu() {
        addMenuItem("Agregar conexión", "+", "menu-icon-connect", () -> AgregarConexion());
    }

    public void setConexionCreadaHandler(Consumer<InformixConnectionConfig> handler) {
        conexionCreadaHandler = Objects.requireNonNull(handler);
    }

    public void AgregarConexion() {
        Optional<InformixConnectionConfig> resultado = InformixConnectionDialog.show(obtenerVentana());
        resultado.ifPresent(this::guardarConexion);
    }

    private void guardarConexion(InformixConnectionConfig config) {
        try {
            administradorConexiones.agregarConexion(
                    config.nombreConexion(),
                    config.host(),
                    config.port(),
                    config.database(),
                    config.serverName(),
                    config.username(),
                    config.password());
        } catch (IOException | IllegalArgumentException exception) {
            mostrarError("No se pudo guardar la conexión.", exception);
            return;
        }

        agregarNodoConexion(config.nombreConexion());
        conexionCreadaHandler.accept(config);
    }

    private void cargarConexionesGuardadas() {
        try {
            for (String nombre : administradorConexiones.listaConexiones()) {
                agregarNodoConexion(nombre);
            }
        } catch (IOException | IllegalArgumentException exception) {
            mostrarError("No se pudieron cargar las conexiones guardadas.", exception);
        }
    }

    private void agregarNodoConexion(String connectionName) {
        if (getChildren().stream().anyMatch(child -> child.getValue().equals(connectionName))) {
            return;
        }

        CNodoConexion connection = new CNodoConexion(connectionName, textoHandler);
        getChildren().add(connection);
        setExpanded(true);
    }


}

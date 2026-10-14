package com.example.informixjava.nodos;

import com.example.informixjava.AdministradorConexiones.CAdministradorConexiones;
import com.example.informixjava.informixdb.InformixConnectionConfig;
import com.example.informixjava.informixdb.InformixConnectionDialog;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.stage.Window;

public class CNodoConexiones extends TreeItem<String> {
    private final CAdministradorConexiones administradorConexiones =
            new CAdministradorConexiones();
    private final ContextMenu contextMenu;
    private Consumer<InformixConnectionConfig> conexionCreadaHandler = config -> { };

    public CNodoConexiones() {
        super("Conexiones");
        Label icon = new Label("◆");
        icon.setAccessibleText("Raíz de conexiones");
        icon.getStyleClass().addAll("tree-icon", "tree-icon-root");
        setGraphic(icon);
        contextMenu = creaMenu();
        cargarConexionesGuardadas();
    }

    private ContextMenu creaMenu() {
        MenuItem addConnectionItem = new MenuItem("Agregar conexión");
        Label addIcon = new Label("+");
        addIcon.getStyleClass().addAll("menu-icon", "menu-icon-connect");
        addConnectionItem.setGraphic(addIcon);
        addConnectionItem.setOnAction(event -> AgregarConexion());
        return new ContextMenu(addConnectionItem);
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    public void setConexionCreadaHandler(Consumer<InformixConnectionConfig> handler) {
        conexionCreadaHandler = Objects.requireNonNull(handler);
    }

    public void AgregarConexion() {
        Optional<InformixConnectionConfig> resultado =
                InformixConnectionDialog.show(obtenerVentana());
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

        TreeItem<String> connection = new TreeItem<>(connectionName);
        Label connectionIcon = new Label("●");
        connectionIcon.getStyleClass().addAll("tree-icon", "tree-icon-connection");
        connection.setGraphic(connectionIcon);

        TreeItem<String> tables = new TreeItem<>("Tablas");
        Label tablesIcon = new Label("▤");
        tablesIcon.getStyleClass().addAll("tree-icon", "tree-icon-category");
        tables.setGraphic(tablesIcon);

        TreeItem<String> procedures = new TreeItem<>("Procedimientos almacenados");
        Label proceduresIcon = new Label("⚙");
        proceduresIcon.getStyleClass().addAll("tree-icon", "tree-icon-category");
        procedures.setGraphic(proceduresIcon);

        connection.getChildren().add(tables);
        connection.getChildren().add(procedures);
        connection.setExpanded(true);
        getChildren().add(connection);
        setExpanded(true);
    }

    private Window obtenerVentana() {
        return getGraphic() != null && getGraphic().getScene() != null
                ? getGraphic().getScene().getWindow()
                : null;
    }

    private void mostrarError(String encabezado, Exception exception) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Window owner = obtenerVentana();
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle("Administración de conexiones");
        alert.setHeaderText(encabezado);
        alert.setContentText(exception.getMessage() == null
                ? exception.toString()
                : exception.getMessage());
        alert.showAndWait();
    }
}

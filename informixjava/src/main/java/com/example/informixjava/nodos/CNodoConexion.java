package com.example.informixjava.nodos;

import javafx.scene.control.TreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class CNodoConexion extends CNodoBase {
    private final String nombreConexion;

    public CNodoConexion(String nombreConexion) {
        super(nombreConexion);
        this.nombreConexion = nombreConexion;
        creaIcono("●", "tree-icon-connection");
        creaMenu();
        CargaTablas();
        CargaProcedimientos();
    }

    public String getNombreConexion() {
        return nombreConexion;
    }

    private Label menuIcon(String symbol, String styleClass) {
        Label icon = new Label(symbol);
        icon.getStyleClass().addAll("menu-icon", styleClass);
        return icon;
    }

    private void creaMenu() {
        addMenuItem("Reconectar", "↻", "menu-icon-connect", () -> Reconectar());
        addMenuItem("Editar conexión", "✎", "menu-icon-edit", () -> editarConexion());
        addMenuItem("Eliminar conexión", "✕", "menu-icon-delete", () -> eliminarConexion());
    }

    private void CargaTablas() {
        TreeItem<String> tables = new TreeItem<>("Tablas");
        Label tablesIcon = new Label("▤");
        tablesIcon.getStyleClass().addAll("tree-icon", "tree-icon-category");
        tables.setGraphic(tablesIcon);
        getChildren().add(tables);
        setExpanded(true);

    }

    private void CargaProcedimientos() {
        TreeItem<String> procedures = new TreeItem<>("Procedimientos almacenados");
        Label proceduresIcon = new Label("⚙");
        proceduresIcon.getStyleClass().addAll("tree-icon", "tree-icon-category");
        procedures.setGraphic(proceduresIcon);
        getChildren().add(procedures);
        setExpanded(true);
    }
    private void editarConexion() {
        // Implementa la lógica para editar la conexión aquí
    }
    private void eliminarConexion() {
        // Implementa la lógica para eliminar la conexión aquí
    }
    private void Reconectar() {
        // Implementa la lógica para reconectar la conexión aquí
    }
}

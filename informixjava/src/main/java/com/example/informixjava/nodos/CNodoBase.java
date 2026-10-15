package com.example.informixjava.nodos;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.stage.Window;
import javafx.scene.control.Label;

public class CNodoBase extends TreeItem<String> {
    protected final ContextMenu contextMenu = new ContextMenu();

    public CNodoBase(String nombre) {
        super(nombre);
    }

    protected void creaIcono(String simbolo, String styleClass) {
        Label icon = new Label(simbolo);
        // icon.setAccessibleText("Raíz de conexiones");
        icon.getStyleClass().addAll(styleClass, "tree-icon-root");
        setGraphic(icon);
    }

    public ContextMenu getContextMenu() {
        return contextMenu;
    }

    protected void addMenuItem(String text, String symbol, String styleClass, Runnable action) {
        Label icon = new Label(symbol);
        icon.getStyleClass().addAll("menu-icon", styleClass);
        javafx.scene.control.MenuItem menuItem = new javafx.scene.control.MenuItem(text, icon);
        menuItem.setOnAction(event -> action.run());
        contextMenu.getItems().add(menuItem);
    }

    protected Window obtenerVentana() {
        return getGraphic() != null && getGraphic().getScene() != null
                ? getGraphic().getScene().getWindow()
                : null;
    }

    protected void mostrarError(String encabezado, Exception exception) {
        mostrarError(encabezado,exception.getMessage() == null
                ? exception.toString()
                : exception.getMessage());
    }
    protected void mostrarError(String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        Window owner = obtenerVentana();
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(encabezado);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    protected void mostrarYesNoDialog(String titulo,String mensaje, Runnable acctionYes, Runnable acctionNo) {
        // 1. Crear el diálogo de tipo CONFIRMATION
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(titulo);
        dialog.setHeaderText(null); // Opcional: define un encabezado o déjalo null para omitirlo
        dialog.setContentText(mensaje);

        // 2. Personalizar los botones a "Sí" y "No"
        ButtonType btnSi = new ButtonType("Sí");
        ButtonType btnNo = new ButtonType("No");

        dialog.getButtonTypes().setAll(btnSi, btnNo);

        // 3. Mostrar el diálogo y esperar la respuesta del usuario (bloquea hasta
        // responder)
        Optional<ButtonType> resultado = dialog.showAndWait();

        // 4. Evaluar la respuesta
        if (resultado.isPresent() && resultado.get() == btnSi) {
            // El usuario presionó "Sí" -> Lógica para eliminar
            acctionYes.run();
        } else {
            // El usuario presionó "No" o cerró la ventana
           acctionNo.run();
        }
    }
    protected void mostrarMensaje(String encabezado, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        Window owner = obtenerVentana();
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(encabezado);
        alert.setHeaderText(encabezado);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}

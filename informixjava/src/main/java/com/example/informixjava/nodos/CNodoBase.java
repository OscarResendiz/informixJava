package com.example.informixjava.nodos;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Label;

public class CNodoBase extends TreeItem<String> {
    protected final ContextMenu contextMenu = new ContextMenu();

    public CNodoBase(String nombre) {
        super(nombre);
    }

    protected void creaIcono(String simbolo, String styleClass) {
        Label icon = new Label(simbolo);
        //icon.setAccessibleText("Raíz de conexiones");
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
}

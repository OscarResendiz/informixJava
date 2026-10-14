package com.example.informixjava;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Separator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import com.example.informixjava.informixdb.InformixConnectionConfig;
import com.example.informixjava.informixdb.InformixDatabase;
import com.example.informixjava.informixdb.InformixSqlConsole;
import com.example.informixjava.nodos.CNodoConexion;
import com.example.informixjava.nodos.CNodoConexiones;

public class MainApp extends Application {
    private Stage primaryStage;
    private MenuItem connectMenuItem;
    private Label statusLabel;
    private CNodoConexiones connectionsNode;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        BorderPane root = new BorderPane();
        root.getStyleClass().add("app-root");
        root.setTop(createHeader());

        SplitPane content = new SplitPane(createSidebar(), createWorkspace());
        content.getStyleClass().add("main-split-pane");
        content.setDividerPositions(0.24);
        root.setCenter(content);

        Scene scene = new Scene(root, 1120, 740);
        scene.getStylesheets().add(
                MainApp.class.getResource("/styles.css").toExternalForm());

        stage.setTitle("Informix JavaFX");
        stage.setMinWidth(820);
        stage.setMinHeight(560);
        stage.setScene(scene);
        stage.show();
    }

    private VBox createHeader() {
        MenuBar menuBar = new MenuBar(
                createFileMenu(),
                createMenu("Editar", "Deshacer", "Rehacer", "Preferencias..."),
                createMenu("Ver", "Panel izquierdo", "Barra de herramientas", "Restablecer vista"),
                createMenu("Herramientas", "Opción de ejemplo", "Configuración"),
                createMenu("Ayuda", "Contenido de ayuda", "Acerca de"));

        ToolBar toolBar = new ToolBar(
                toolbarButton("Nuevo"),
                toolbarButton("Abrir"),
                toolbarButton("Guardar"),
                new Separator(),
                toolbarButton("Buscar"),
                toolbarButton("Actualizar"));
        toolBar.getStyleClass().add("main-toolbar");

        return new VBox(menuBar, toolBar);
    }

    private Menu createFileMenu() {
        Menu menu = new Menu("Archivo");
        menu.getItems().addAll(
                new MenuItem("Nuevo"),
                new MenuItem("Abrir..."),
                new MenuItem("Guardar"),
                new SeparatorMenuItem());

        connectMenuItem = new MenuItem("Agregar conexión Informix...");
        connectMenuItem.setOnAction(event -> connectionsNode.AgregarConexion());
        menu.getItems().addAll(connectMenuItem, new SeparatorMenuItem(), new MenuItem("Salir"));
        return menu;
    }

    private void connectToInformix(InformixConnectionConfig config) {
        connectMenuItem.setDisable(true);
        statusLabel.setText("Conectando a Informix...");

        Task<InformixDatabase> connectionTask = new Task<>() {
            @Override
            protected InformixDatabase call() throws Exception {
                return InformixDatabase.connect(config);
            }
        };
        connectionTask.setOnSucceeded(event -> {
            connectMenuItem.setDisable(false);
            statusLabel.setText("Conectado a " + config.database());
            new InformixSqlConsole(primaryStage, connectionTask.getValue()).show();
        });
        connectionTask.setOnFailed(event -> {
            connectMenuItem.setDisable(false);
            statusLabel.setText("No se pudo conectar a Informix.");
            showConnectionError(connectionTask.getException());
        });
        Thread connectionThread = new Thread(connectionTask, "informix-connection");
        connectionThread.setDaemon(true);
        connectionThread.start();
    }

    private void showConnectionError(Throwable error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(primaryStage);
        alert.setTitle("Error de conexión");
        alert.setHeaderText("No fue posible conectar a Informix.");
        alert.setContentText(error.getMessage() == null ? error.toString() : error.getMessage());
        alert.showAndWait();
    }

    private Menu createMenu(String title, String... options) {
        Menu menu = new Menu(title);
        for (String option : options) {
            if (option.equals("Salir")) {
                menu.getItems().add(new SeparatorMenuItem());
            }
            menu.getItems().add(new MenuItem(option));
        }
        return menu;
    }

    private Button toolbarButton(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("toolbar-button");
        return button;
    }

    private VBox createSidebar() {
        Label heading = new Label("CONEXIONES");
        heading.getStyleClass().add("section-eyebrow");

        connectionsNode = new CNodoConexiones();
        connectionsNode.setConexionCreadaHandler(this::connectToInformix);
        connectionsNode.setExpanded(true);

        TreeView<String> connectionTree = new TreeView<>(connectionsNode);
        connectionTree.getStyleClass().add("connections-tree");

        connectionTree.setCellFactory(tree -> {
            TreeCell<String> cell = new TreeCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty ? null : item);
                    TreeItem<String> treeItem = getTreeItem();
                    setGraphic(empty || treeItem == null ? null : treeItem.getGraphic());
                }
            };
            cell.setOnContextMenuRequested(event -> {

                TreeItem<String> selectedItem = cell.getTreeItem();

                if (selectedItem == null
                        || selectedItem.getParent() == null
                        || selectedItem.getParent().getParent() != null) {
                    return;
                }

                if (selectedItem instanceof CNodoConexiones rootNode) {
                    connectionTree.getSelectionModel().select(rootNode);
                    rootNode.getContextMenu().show(cell, event.getScreenX(), event.getScreenY());
                    event.consume();
                    return;
                }

                if (selectedItem instanceof CNodoConexion rootNode) {
                    connectionTree.getSelectionModel().select(rootNode);
                    rootNode.getContextMenu().show(cell, event.getScreenX(), event.getScreenY());
                    event.consume();
                    return;
                }


                connectionTree.getSelectionModel().select(selectedItem);
               // ContextMenu contextMenu = new ContextMenu();
                
               
                //contextMenu.getItems().addAll(connectItem, editItem);
               // contextMenu.show(cell, event.getScreenX(), event.getScreenY());
                event.consume();
            });
            return cell;
        });
        VBox.setVgrow(connectionTree, Priority.ALWAYS);

        Label note = new Label("Nombres de ejemplo para tablas\ny procedimientos almacenados.");
        note.getStyleClass().add("sidebar-note");

        VBox sidebar = new VBox(14, heading, connectionTree, note);
        sidebar.setPadding(new Insets(24, 16, 20, 16));
        sidebar.getStyleClass().add("sidebar");
        return sidebar;
    }

    private Label menuIcon(String symbol, String styleClass) {
        Label icon = new Label(symbol);
        icon.getStyleClass().addAll("menu-icon", styleClass);
        return icon;
    }

    private void showConnectionMenuAction(String action, String connectionName) {
        statusLabel.setText("Opción de ejemplo \"" + action + "\" para " + connectionName);
    }

    private VBox createWorkspace() {
        Label eyebrow = new Label("ESPACIO DE TRABAJO");
        eyebrow.getStyleClass().add("section-eyebrow");

        Label title = new Label("Bienvenido");
        title.getStyleClass().add("workspace-title");

        Label subtitle = new Label(
                "Esta es el área principal de la aplicación. Aquí podrás incorporar tus funcionalidades.");
        subtitle.getStyleClass().add("workspace-subtitle");
        subtitle.setWrapText(true);

        VBox welcomeCard = new VBox(12,
                new Label("ÁREA DE CONTENIDO"),
                new Label("Tu espacio para trabajar"),
                new Label("Selecciona una opción del panel izquierdo o agrega aquí los controles de tu aplicación."));
        welcomeCard.getStyleClass().add("welcome-card");
        welcomeCard.getChildren().get(0).getStyleClass().add("section-eyebrow");
        welcomeCard.getChildren().get(1).getStyleClass().add("card-title");
        welcomeCard.getChildren().get(2).getStyleClass().add("workspace-subtitle");
        ((Label) welcomeCard.getChildren().get(2)).setWrapText(true);
        welcomeCard.setMaxWidth(Double.MAX_VALUE);

        statusLabel = new Label("Listo");
        HBox statusBar = new HBox(statusLabel, new Region(), new Label("Aplicación de ejemplo"));
        statusBar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(statusBar.getChildren().get(1), Priority.ALWAYS);
        statusBar.getStyleClass().add("status-bar");

        VBox workspace = new VBox(12, eyebrow, title, subtitle, welcomeCard, statusBar);
        workspace.setPadding(new Insets(30, 34, 18, 34));
        VBox.setVgrow(welcomeCard, Priority.ALWAYS);
        workspace.getStyleClass().add("workspace");
        return workspace;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

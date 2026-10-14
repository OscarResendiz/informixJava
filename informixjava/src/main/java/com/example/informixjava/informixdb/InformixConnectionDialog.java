package com.example.informixjava.informixdb;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Window;

import java.util.Optional;

public final class InformixConnectionDialog {
    private InformixConnectionDialog() {
    }

    public static Optional<InformixConnectionConfig> show(Window owner) {
        Dialog<InformixConnectionConfig> dialog = new Dialog<>();
        dialog.setTitle("Conectar a Informix");
        dialog.setHeaderText("Ingresa los datos de conexión a la base de datos.");
        if (owner != null) {
            dialog.initOwner(owner);
        }

        TextField connectionName = new TextField();
        connectionName.setPromptText("Informix Desarrollo");
        TextField host = new TextField();
        host.setPromptText("localhost");
        TextField port = new TextField("9088");
        TextField database = new TextField();
        TextField serverName = new TextField();
        TextField username = new TextField();
        PasswordField password = new PasswordField();
        Label error = new Label();
        error.setStyle("-fx-text-fill: #b42318;");
        error.setWrapText(true);

        GridPane fields = new GridPane();
        fields.setHgap(12);
        fields.setVgap(10);
        fields.setPadding(new Insets(8, 4, 4, 4));
        addField(fields, 0, "Nombre de la conexión", connectionName);
        addField(fields, 1, "Host", host);
        addField(fields, 2, "Puerto", port);
        addField(fields, 3, "Base de datos", database);
        addField(fields, 4, "Servidor Informix", serverName);
        addField(fields, 5, "Usuario", username);
        addField(fields, 6, "Contraseña", password);
        fields.add(error, 0, 7, 2, 1);
        dialog.getDialogPane().setContent(fields);

        ButtonType connect = new ButtonType("Conectar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connect, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(connect).addEventFilter(ActionEvent.ACTION, event -> {
            try {
                createConfig(connectionName, host, port, database, serverName, username, password);
                error.setText("");
            } catch (IllegalArgumentException exception) {
                error.setText(exception.getMessage());
                event.consume();
            }
        });
        dialog.setResultConverter(button -> {
            if (button != connect) {
                return null;
            }
            return createConfig(connectionName, host, port, database, serverName, username, password);
        });
        return dialog.showAndWait();
    }

    private static void addField(GridPane fields, int row, String label, TextField field) {
        fields.add(new Label(label + ":"), 0, row);
        field.setPrefColumnCount(28);
        fields.add(field, 1, row);
    }

    private static InformixConnectionConfig createConfig(
            TextField connectionName,
            TextField host,
            TextField port,
            TextField database,
            TextField serverName,
            TextField username,
            PasswordField password) {
        int portNumber;
        try {
            portNumber = Integer.parseInt(port.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("El puerto debe ser un número entre 1 y 65535.");
        }
        return new InformixConnectionConfig(
                connectionName.getText(),
                host.getText(),
                portNumber,
                database.getText(),
                serverName.getText(),
                username.getText(),
                password.getText());
    }
}

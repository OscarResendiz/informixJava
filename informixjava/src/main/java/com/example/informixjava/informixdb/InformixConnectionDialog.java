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
    public static Optional<InformixConnectionConfig> show(Window owner    
        ,String connectionName
        ,String host 
        ,Integer port
        ,String database
        ,String serverName
        ,String username 
        ,String password 
    ) {
        
        Dialog<InformixConnectionConfig> dialog = new Dialog<>();
        dialog.setTitle("Conectar a Informix");
        dialog.setHeaderText("Ingresa los datos de conexión a la base de datos.");
        if (owner != null) {
            dialog.initOwner(owner);
        }

        TextField tconnectionName = new TextField();
        tconnectionName.setPromptText("Informix Desarrollo");
        TextField thost = new TextField();
        thost.setPromptText("localhost");
        TextField tport = new TextField("9088");
        TextField tdatabase = new TextField();
        TextField tserverName = new TextField();
        TextField tusername = new TextField();
        PasswordField tpassword = new PasswordField();
        //asigno valores
        tconnectionName.setText(connectionName);
        if(!connectionName.equals(""))
        {
            //ya tiene un nombre asignado, por lo que no se puede editar
            tconnectionName.setEditable(false);
            tconnectionName.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #555555;");
        }
        thost.setText(host);
        tport.setText(port.toString());
        tdatabase.setText(database);
        tserverName.setText(serverName);
        tusername.setText(username);
        tpassword.setText(password);

        Label error = new Label();
        error.setStyle("-fx-text-fill: #b42318;");
        error.setWrapText(true);

        GridPane fields = new GridPane();
        fields.setHgap(12);
        fields.setVgap(10);
        fields.setPadding(new Insets(8, 4, 4, 4));
        addField(fields, 0, "Nombre de la conexión", tconnectionName);
        addField(fields, 1, "Host", thost);
        addField(fields, 2, "Puerto", tport);
        addField(fields, 3, "Base de datos", tdatabase);
        addField(fields, 4, "Servidor Informix", tserverName);
        addField(fields, 5, "Usuario", tusername);
        addField(fields, 6, "Contraseña", tpassword);
        fields.add(error, 0, 7, 2, 1);
        dialog.getDialogPane().setContent(fields);

        ButtonType connect = new ButtonType("Conectar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(connect, ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(connect).addEventFilter(ActionEvent.ACTION, event -> {
            try {
                createConfig(tconnectionName, thost, tport, tdatabase, tserverName, tusername, tpassword);
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
            return createConfig(tconnectionName, thost, tport, tdatabase, tserverName, tusername, tpassword);
        });
        return dialog.showAndWait();
    }

    public static Optional<InformixConnectionConfig> show(Window owner) {
        return show(owner,"","",9088,"","","","");
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

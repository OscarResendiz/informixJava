package com.example.informixjava.informixdb;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class InformixSqlConsole {
    private final Stage stage = new Stage();
    private final InformixDatabase database;
    private final TextArea sql = new TextArea();
    private final TextArea parameters = new TextArea();
    private final TableView<List<Object>> results = new TableView<>();
    private final Button executeButton = new Button("Ejecutar");
    private final Label status = new Label("Usa ? para los parámetros enlazados.");

    public InformixSqlConsole(Window owner, InformixDatabase database) {
        this.database = database;
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.setTitle("Consola SQL - Informix");
        stage.setMinWidth(760);
        stage.setMinHeight(560);
        stage.setScene(new Scene(createContent(), 1000, 720));
        stage.setOnHidden(event -> closeDatabase());
    }

    public void show() {
        stage.show();
    }

    private BorderPane createContent() {
        sql.setPromptText("Escribe una consulta SQL...");
        sql.setPrefRowCount(7);
        parameters.setPromptText("Un valor por línea; escribe NULL para un valor SQL NULL.");
        parameters.setPrefRowCount(3);
        results.setPlaceholder(new Label("Los resultados de la consulta aparecerán aquí."));
        results.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        executeButton.setDefaultButton(true);
        executeButton.setOnAction(event -> executeSql());
        HBox actions = new HBox(12, executeButton, status);
        actions.setPadding(new Insets(8, 0, 0, 0));

        VBox editor = new VBox(
                8,
                new Label("Consulta SQL"),
                sql,
                new Label("Parámetros (en el mismo orden que los signos ?; uno por línea)"),
                parameters,
                actions);
        VBox.setVgrow(sql, Priority.ALWAYS);

        BorderPane content = new BorderPane();
        content.setPadding(new Insets(18));
        content.setTop(editor);
        content.setCenter(results);
        BorderPane.setMargin(results, new Insets(16, 0, 0, 0));
        return content;
    }

    private void executeSql() {
        final String sqlText = sql.getText();
        final List<Object> parameterValues;
        try {
            parameterValues = parseParameters(parameters.getText());
        } catch (IllegalArgumentException exception) {
            showError("Parámetros no válidos", exception);
            return;
        }

        executeButton.setDisable(true);
        status.setText("Ejecutando consulta...");
        Task<InformixQueryResult> queryTask = new Task<>() {
            @Override
            protected InformixQueryResult call() throws SQLException {
                return database.execute(sqlText, parameterValues);
            }
        };
        queryTask.setOnSucceeded(event -> {
            executeButton.setDisable(false);
            displayResult(queryTask.getValue());
        });
        queryTask.setOnFailed(event -> {
            executeButton.setDisable(false);
            Throwable error = queryTask.getException();
            status.setText("La consulta falló.");
            showError("Error al ejecutar la consulta", error);
        });
        Thread queryThread = new Thread(queryTask, "informix-query");
        queryThread.setDaemon(true);
        queryThread.start();
    }

    private void displayResult(InformixQueryResult result) {
        results.getColumns().clear();
        results.getItems().clear();
        if (!result.hasRows()) {
            status.setText("Operación completada. Filas afectadas: " + result.updateCount());
            return;
        }

        for (int index = 0; index < result.columnNames().size(); index++) {
            final int columnIndex = index;
            TableColumn<List<Object>, Object> column =
                    new TableColumn<>(result.columnNames().get(index));
            column.setCellValueFactory(cell ->
                    new ReadOnlyObjectWrapper<>(cell.getValue().get(columnIndex)));
            results.getColumns().add(column);
        }
        results.setItems(FXCollections.observableArrayList(result.rows()));
        status.setText("Consulta completada. Filas devueltas: " + result.rows().size());
    }

    private List<Object> parseParameters(String text) {
        if (text.isEmpty()) {
            return List.of();
        }

        String[] lines = text.split("\\R", -1);
        int parameterCount = lines.length;
        if (lines[lines.length - 1].isEmpty()) {
            parameterCount--;
        }
        List<Object> values = new ArrayList<>(parameterCount);
        for (int index = 0; index < parameterCount; index++) {
            String value = lines[index];
            values.add(value.equalsIgnoreCase("NULL") ? null : value);
        }
        return values;
    }

    private void showError(String title, Throwable error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(error.getMessage() == null ? error.toString() : error.getMessage());
        alert.showAndWait();
    }

    private void closeDatabase() {
        try {
            database.close();
        } catch (SQLException exception) {
            showError("Error al cerrar la conexión", exception);
        }
    }
}

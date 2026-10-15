package com.example.informixjava.nodos;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import com.example.informixjava.AdministradorConexiones.CAdministradorConexiones;
import com.example.informixjava.AdministradorConexiones.CDatosConexion;
import com.example.informixjava.InformixSystemData.InformixSystemData;

import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.TreeItem;

public class CNodoProcedimientosAlmacenados extends CNodoBase {
    private final String NombreDB;
    private final Consumer<String> textoHandler;
    CDatosConexion datosConexion;

    public CNodoProcedimientosAlmacenados(
            String nombreDB, CDatosConexion datosConexionServidor, Consumer<String> textoHandler) {
        NombreDB = nombreDB;
        super("Procedimientos almacenados");
        this.textoHandler = textoHandler;
        datosConexion = new CDatosConexion(NombreDB, datosConexionServidor.host(), datosConexionServidor.puerto(),
                NombreDB, datosConexionServidor.servidorInformix(), datosConexionServidor.usuario(),
                datosConexionServidor.contrasena());
        creaIcono("⚙", "tree-icon-connection");
        creaMenu();
        // CargaTablas();
        // CargaProcedimientos();
    }

    private void creaMenu() {
        addMenuItem("Recargar", "↻", "menu-icon-connect", () -> CargaProcedimientos());
        addMenuItem("Ver codigo fuente SP", "✎", "menu-icon-edit", () -> verCodigoFuenteSP());
        // addMenuItem("Eliminar conexión", "✕", "menu-icon-delete", () ->
        // eliminarConexion());
    }

    private void CargaProcedimientos() {
        try {
            getChildren().clear();
            InformixSystemData systemData = new InformixSystemData(datosConexion);
            List<String> lista = systemData.getStroresProcedure();
            for (String nombreSP : lista) {
                CNodoSP nodo = new CNodoSP(nombreSP, datosConexion, textoHandler);
                getChildren().add(nodo);
            }
        } catch (Exception ex) {
            mostrarError("Error", ex);
        }
    }

    private void verCodigoFuenteSP() {
        // 1. Crear la instancia del diálogo pasándole un valor por defecto (o vacío "")
        TextInputDialog dialog = new TextInputDialog("Texto por defecto");

        // 2. Personalizar títulos y mensajes
        dialog.setTitle("Entrada de Texto");
        dialog.setHeaderText("Por favor ingresa la información requerida");
        dialog.setContentText("Nombre:");

        // 3. Mostrar el diálogo y esperar la respuesta del usuario
        Optional<String> result = dialog.showAndWait();

        // 4. Procesar el resultado
        result.ifPresent(textoIngresado -> {
            verCodigoFuente(textoIngresado);
        });
    }

    private void verCodigoFuente(String NombreSP) {
        try {
            // getChildren().clear();
            InformixSystemData systemData = new InformixSystemData(datosConexion);
            String codigo = systemData.getSourceCodeSP(NombreSP);
            if(codigo.equals(""))
            {
                mostrarError("Error", "No se encontró el SP "+NombreSP);   
                return ; 
            }
            muestraTexto(codigo);
            // copiarAlPortapapeles(codigo);
            // mostrarMensaje("código fuente", "el código fuente del procedimiento
            // almacenado fue copiado en el porta papeles");

        } catch (Exception ex) {
            mostrarError("Error", ex);
        }
    }

    public void muestraTexto(String texto) {
        textoHandler.accept(Objects.requireNonNull(texto));
    }
}

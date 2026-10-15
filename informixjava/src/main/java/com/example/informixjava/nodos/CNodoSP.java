package com.example.informixjava.nodos;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import com.example.informixjava.AdministradorConexiones.CDatosConexion;
import com.example.informixjava.InformixSystemData.InformixSystemData;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class CNodoSP extends CNodoBase {
    private final String NombreSP;
    private final Consumer<String> textoHandler;
    CDatosConexion datosConexion;

    public CNodoSP(String nombresp, CDatosConexion datosConexiondb, Consumer<String> textoHandler) {
        super(nombresp);
        NombreSP = nombresp;
        datosConexion = datosConexiondb;
        this.textoHandler = Objects.requireNonNull(textoHandler);
        creaIcono("ƒ", "tree-icon-connection");
        creaMenu();
        // CargaTablas();
        // CargaProcedimientos();
    }

    private void creaMenu() {
        addMenuItem("ver codigo fuente", "¶", "menu-icon-connect", () -> verCodigoFuente());
        // addMenuItem("Editar conexión", "✎", "menu-icon-edit", () ->
        // editarConexion());
        // addMenuItem("Eliminar conexión", "✕", "menu-icon-delete", () ->
        // eliminarConexion());
    }

    private void verCodigoFuente() {
        try {
            //getChildren().clear();
            InformixSystemData systemData = new InformixSystemData(datosConexion);
            String codigo = systemData.getSourceCodeSP(NombreSP);
            muestraTexto(codigo);
            //copiarAlPortapapeles(codigo);
            //mostrarMensaje("código fuente", "el código fuente del procedimiento almacenado fue copiado en el porta papeles");

        } catch (Exception ex) {
            mostrarError("Error", ex);
        }
    }

    public void muestraTexto(String texto) {
        textoHandler.accept(Objects.requireNonNull(texto));
    }

public static void copiarAlPortapapeles(String texto) {
        // 1. Obtener el portapapeles del sistema en JavaFX
        Clipboard clipboard = Clipboard.getSystemClipboard();

        // 2. Crear el contenedor de contenido
        ClipboardContent content = new ClipboardContent();
        content.putString(texto);

        // 3. Asignar el contenido al portapapeles
        clipboard.setContent(content);
    }
}

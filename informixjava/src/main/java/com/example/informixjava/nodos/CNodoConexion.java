package com.example.informixjava.nodos;

import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.example.informixjava.AdministradorConexiones.CAdministradorConexiones;
import com.example.informixjava.AdministradorConexiones.CDatosConexion;
import com.example.informixjava.InformixSystemData.InformixSystemData;
import com.example.informixjava.informixdb.InformixConnectionConfig;
import com.example.informixjava.informixdb.InformixConnectionDialog;
import com.example.informixjava.informixdb.InformixDatabase;
import com.example.informixjava.informixdb.InformixSqlConsole;

import javafx.concurrent.Task;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

public class CNodoConexion extends CNodoBase {
    private final String nombreConexion;
    private final Consumer<String> textoHandler;
    private final CAdministradorConexiones administradorConexiones = CAdministradorConexiones
            .DameAdministradorConexiones();

    public CNodoConexion(String nombreConexion, Consumer<String> textoHandler) {
        super(nombreConexion);
        this.nombreConexion = nombreConexion;
        this.textoHandler = textoHandler;
        creaIcono("●", "tree-icon-connection");
        creaMenu();
        Recargar();
    }

    public String getNombreConexion() {
        return nombreConexion;
    }


    private void creaMenu() {
        addMenuItem("Recargar", "↻", "menu-icon-connect", () -> Recargar());
        addMenuItem("Editar conexión", "✎", "menu-icon-edit", () -> editarConexion());
        addMenuItem("Eliminar conexión", "✕", "menu-icon-delete", () -> eliminarConexion());
    }

    private void editarConexion() {
        try {
            // me traigo mis datos
            CDatosConexion datos = administradorConexiones.dameConexion(nombreConexion);
            Optional<InformixConnectionConfig> resultado = InformixConnectionDialog.show(obtenerVentana(),
                    datos.nombreConexion(), datos.host(), datos.puerto(), datos.baseDatos(), datos.servidorInformix(),
                    datos.usuario(), datos.contrasena());
            resultado.ifPresent(this::guardarConexion);
        } catch (Exception ex) {
            mostrarError("No se pudo guardar la conexión.", ex);

        }
    }

    private void guardarConexion(InformixConnectionConfig config) {
        try {
            administradorConexiones.ActualizarConexion(
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
    }

    private void eliminarConexion() {
        // Implementa la lógica para eliminar la conexión aquí
        mostrarYesNoDialog("Eliminar", "¿Seguro que desea eliminar la conexion " + nombreConexion + "?",
                () -> confirmacionEliminar(), () -> {
                });
    }

    private void confirmacionEliminar() {
        try {
            administradorConexiones.eliminarConexion(nombreConexion);
            this.getParent().getChildren().remove(this);
        } catch (Exception ex) {
            mostrarError("No se pudo guardar la conexión.", ex);
        }
    }

    private void Recargar() {
        try {
            getChildren().clear();
            CDatosConexion datos = administradorConexiones.dameConexion(nombreConexion);
            InformixSystemData systemData=new InformixSystemData(datos);
            List<String> lista=systemData.getDataBases();
            for(String base: lista)
            {
               CNodoDataBase nodo=new CNodoDataBase(base,datos,textoHandler) ;
               getChildren().add(nodo);
            }
        } catch (Exception ex) {
            mostrarError("Error", ex);
        }
    }
}

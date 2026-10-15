package com.example.informixjava.nodos;

import java.util.List;

import com.example.informixjava.AdministradorConexiones.CAdministradorConexiones;
import com.example.informixjava.AdministradorConexiones.CDatosConexion;
import com.example.informixjava.InformixSystemData.InformixSystemData;

import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;

public class CNodoProcedimientosAlmacenados extends CNodoBase{
    private final String NombreDB;
    CDatosConexion datosConexion;
    public CNodoProcedimientosAlmacenados(String nombreDB, CDatosConexion datosConexionServidor)
    {
        NombreDB=nombreDB;
        super("Procedimientos almacenados");
        datosConexion= new CDatosConexion(NombreDB, datosConexionServidor.host(),datosConexionServidor.puerto(),NombreDB,datosConexionServidor.servidorInformix(),datosConexionServidor.usuario(),datosConexionServidor.contrasena());
        creaIcono("⚙", "tree-icon-connection");
        creaMenu();
//        CargaTablas();
       // CargaProcedimientos();
    }
    private void creaMenu() {
        addMenuItem("Recargar", "↻", "menu-icon-connect", () -> CargaProcedimientos());
        //addMenuItem("Editar conexión", "✎", "menu-icon-edit", () -> editarConexion());
        //addMenuItem("Eliminar conexión", "✕", "menu-icon-delete", () -> eliminarConexion());
    }

    private void CargaProcedimientos() {
        try {
            getChildren().clear();
            InformixSystemData systemData=new InformixSystemData(datosConexion);
            List<String> lista=systemData.getStroresProcedure();
            for(String nombreSP: lista)
            {
               CNodoSP nodo=new CNodoSP(nombreSP,datosConexion) ;
               getChildren().add(nodo);
            }
        } catch (Exception ex) {
            mostrarError("Error", ex);
        }
    }

}

package com.example.informixjava.InformixSystemData;

import com.example.informixjava.informixdb.InformixConnectionConfig;
import com.example.informixjava.informixdb.InformixDatabase;
import com.example.informixjava.informixdb.InformixQueryResult;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.example.informixjava.AdministradorConexiones.CDatosConexion;

public class InformixSystemData {
    private final CDatosConexion DatosConexion;
    private final InformixDatabase Database;
    public InformixSystemData(CDatosConexion datosConexion)throws Exception {
        DatosConexion = datosConexion;
        InformixConnectionConfig config = new InformixConnectionConfig(DatosConexion.nombreConexion(), DatosConexion.host(),
                    DatosConexion.puerto(), DatosConexion.baseDatos(), DatosConexion.servidorInformix(), DatosConexion.usuario(), DatosConexion.contrasena());
        Database=InformixDatabase.connect(config);
    }

    public List<String> getDataBases() throws Exception {
        String sqlText="SELECT name AS nombre_bd FROM sysmaster:sysdatabases order by nombre_bd;";
        List<String> lista = new ArrayList<>();
        InformixQueryResult result=Database.execute(sqlText, List.of());
        if(!result.hasRows())
        {
            return lista;
        }
        for (List<Object> row : result.rows())
        {
            for (Object cell : row)
            {
                lista.add(cell.toString());
            }
        }
        return lista;
    }
    public List<String> getStroresProcedure() throws Exception {
        String sqlText="select distinct procname AS nombre_procedimiento from sysprocedures  ORDER BY procname;";
        List<String> lista = new ArrayList<>();
        InformixQueryResult result=Database.execute(sqlText, List.of());
        if(!result.hasRows())
        {
            return lista;
        }
        for (List<Object> row : result.rows())
        {
            for (Object cell : row)
            {
                lista.add(cell.toString());
            }
        }
        return lista;
    }
    public String getSourceCodeSP(String nombreSP)throws Exception 
    {
        String codigo="";
        String sqlText="";
        sqlText=sqlText+"select b.data \r\n";
        sqlText=sqlText+"FROM sysprocedures p \r\n";
        sqlText=sqlText+"JOIN sysprocbody b ON p.procid = b.procid \r\n";
        sqlText=sqlText+"WHERE p.procname = '"+nombreSP+"'\r\n";
        sqlText=sqlText+"AND b.datakey = 'T' \r\n";
        sqlText=sqlText+"ORDER BY b.seqno; \r\n";

        InformixQueryResult result=Database.execute(sqlText, List.of());
        if(!result.hasRows())
        {
            return codigo;
        }
        for (List<Object> row : result.rows())
        {
            for (Object cell : row)
            {
                codigo=codigo+cell.toString();
            }
        }
        return codigo;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recetario;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Gestiona las categorías de recetas del sistema
 * @author Vi
 */
public class Categoria {
    
    private final basedatos.Conexion con = Recetario.getCon();
    
    /** Agrega una categoría
     * @param titulo */
    public void agregar(String titulo) {
        con.ejecutar("INSERT INTO categorias (titulo) VALUES ('" + titulo + "')");
    }
    
    /** Modifica el nombre de una categoría
     * @param titulo */
    public void modificar( String titulo) {
        con.ejecutar("UPDATE categorias SET titulo='" + titulo + " WHERE titulo="+titulo);
    }
    
    /** Borra una categoría
     * @param titulo */
    public void borrar( String titulo) {
        con.ejecutar("DELETE FROM categorias WHERE titulo='"+titulo+"'");
    }
    
    /**
     * Obtiene el listado de categorías
     * @return ObservableList de Strings con categorias
     */
    public ObservableList<String> obtenerLista() {
    ResultSet rs;
        try {
            rs = con.devolver("SELECT titulo FROM categorias ORDER BY titulo");
        
    
    ObservableList<String> items = FXCollections.observableArrayList();
            while (rs.next()) {
                String rtitulo = rs.getString("titulo");
                items.add(rtitulo);
            }
        return items;
    } catch (SQLException ex) {
            Logger.getLogger(Categoria.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    
    
}

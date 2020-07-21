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
/**
 *
 * @author Vi
 */
public class Receta {
    private final basedatos.Conexion con = Recetario.getCon();
    
    /**
     * Título de la receta
     */
    public String titulo="";

    /**
     *Categoría de la receta
     */
    public String categoria="";

    /**
     * Contenido de la receta
     */
    public String contenido="";
    
Receta(String seleccionado) {

    ResultSet rs;
        try {
            rs = con.devolver("SELECT r.id id, r.titulo titulo, r.contenido contenido, c.titulo ccategoria "
                    + "FROM recetas as r, categorias as c "
                    + "WHERE r.idc=c.id AND r.titulo='" + seleccionado + "'");

            if (rs.next()) {

                titulo = rs.getString("titulo");
                contenido = rs.getString("contenido");
                categoria = rs.getString("ccategoria");


            }
            System.out.println("Receta cargada");

        } catch (SQLException ex) {
            Logger.getLogger(VistaReceta_Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    
}
    
    
}
//hola
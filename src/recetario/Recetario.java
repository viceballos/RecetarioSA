/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recetario;

import basedatos.Conexion;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Clase principal del proyecto RecetarioSA
 * @author Vi
 */
public class Recetario extends Application {

    /**
     * Estos atributos los usaremos en toda la App. Los dejamos private para
     * protegerlos así que tenemos que crear getters y setters
     */
    private static String apariencia;
    private static String recetaSeleccionada;
    private static final basedatos.Conexion con = new basedatos.Conexion();

    /**
     * @return the con Establece la Conexión a la DB para toda la App
     */
    public static Conexion getCon() {
        return con;
    }
///GETTERS y SETTERS de los atributos de la clase///

    public static String getRecetaSeleccionada() {
        return recetaSeleccionada;
    }

    public static void setRecetaSeleccionada(String recetaSeleccionada) {
        Recetario.recetaSeleccionada = recetaSeleccionada;
    }

    /**
     * @return the apariencia
     */
    public static String getApariencia() {
        return apariencia;
    }

    /**
     * @param aApariencia the apariencia to set
     */
    public static void setApariencia(String aApariencia) {
        apariencia = aApariencia;
    }


    /**
     * Aquí se echa andar todo
     *
     * @param stage
     * @throws java.lang.Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        
         try {
            DatabaseMetaData dbmd;
            dbmd = con.conectar().getMetaData();
            ResultSet rs = dbmd.getTables(null, null, "RECETAS", null);
            
            if (rs.next()) {
                System.out.println("La base de datos ya existe.");
            } else {
            System.out.println("No Existe la base de datos. La creamos...");
            creardb();
            }
        } catch (SQLException ex) {
            Logger.getLogger(Recetario.class.getName()).log(Level.SEVERE, null, ex);
        }
               
        
        
//Apariencia por defecto

        apariencia = new Preferencias().leer();
        Parent root = FXMLLoader.load(getClass().getResource("InterfazPrincipal.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Mis Recetas");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
        
    }

    //Crea la base de datos si no existe
    private void creardb() {
    con.ejecutar("CREATE TABLE categorias(id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),titulo VARCHAR(256) NOT NULL,CONSTRAINT categorias PRIMARY KEY (id))");
    con.ejecutar("CREATE TABLE recetas(id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),idc INTEGER DEFAULT 1 NOT NULL, titulo VARCHAR(512) NOT NULL,contenido LONG VARCHAR,CONSTRAINT recetas PRIMARY KEY (id))");
    con.ejecutar("insert into categorias (titulo) values ('Postres'),('Entradas'),('Almuerzos')");
    }
}

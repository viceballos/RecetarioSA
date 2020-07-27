/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package basedatos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
//Hola
/**
 * Clase para conectarse a una base de datos con cambios
 * @author Vi
 */
public class Conexion {

    /**
     * Constructor de la clase, que carga el driver de la base de datos
     */
    public Conexion() {
        try {
            //Class.forName("com.mysql.jdbc.Driver");
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("No está el driver MySQL JDBC !!");
        }
    }

    //

    /**
     * Devuelve conexión
     * @return objeto Connection
     * @throws SQLException
     */
        public Connection conectar() throws SQLException {
        Connection connection = null;
        try {
            //connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/recetario", "recetario", "7UMxuAGRPysUrayZ");
            //connection = DriverManager.getConnection("jdbc:derby:recetario?create=true", "recetario", "7UMxuAGRPysUrayZ");
            connection = DriverManager.getConnection("jdbc:derby:recetario;create=true", "recetario", "7UMxuAGRPysUrayZ");
            System.out.println("Conexión a base de datos establecida correctamente.");

        } catch (SQLException e) {
            System.out.println("Falló la conexión.");
            //e.printStackTrace();
        }
        return connection;
    }

    //Devuelve consulta

    /**
     * Realiza una consulta 
     * @return objeto Statement
     * @throws SQLException
     */
    
    public Statement consultar() throws SQLException {
        Statement stmt = conectar().createStatement();
        return stmt;
    }

    //Devuelve recordset

    /**
     * Sentencia sql que devuelve resultados
     * @param consulta SQl con consulta tipo Select
     * @return Recordset
     */
        public ResultSet devolver(String consulta) {
        ResultSet rs = null;
        try {
            rs = consultar().executeQuery(consulta);
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rs;
    }

    //

    /**
     * Ejecuta sin devolver nada
     * @param consulta SQL tipo Insert, Update, Delete
     */
        public void ejecutar(String consulta) {

        try {
            consultar().execute(consulta);
        } catch (SQLException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

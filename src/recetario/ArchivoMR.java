/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recetario;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Vi
 */
public class ArchivoMR {
    
/**
     *Título de la receta
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
    /**
     * Guarda las preferencias de la aplicación en un archivo, en formato JSON
     */
    public void guardar() {
//Contenido de receta seleccionada
        Receta rec = new Receta(Recetario.getRecetaSeleccionada());

        titulo = rec.titulo;
        contenido = rec.contenido;
        categoria = rec.categoria;
        
        //Guardar documento como...
        Stage ventanaArchivo = new Stage(StageStyle.DECORATED);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Receta...");
        fileChooser.setInitialFileName("Receta_de_" + titulo.replaceAll(" ", "_") + ".mr");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MR (Mi Receta)", "*.mr"));
        File file = fileChooser.showSaveDialog(ventanaArchivo);

        //si el usuario eligió un archivo...
        if (file != null) {
        
        
        Gson gson = new Gson();
        //Collection<String> datos = new ArrayList<>();
        
        Map<String, String> datos = new HashMap<>();
        
        datos.put("titulo",titulo);
        datos.put("contenido",contenido);
        datos.put("categoria",categoria);
        
        String jsonString = gson.toJson(datos);
        System.out.println("Datos JSON: " + jsonString);

        FileWriter fichero;
        try {
            
            
            
            fichero = new FileWriter(file);
            PrintWriter pw = new PrintWriter(fichero);
            pw.println(jsonString);
            fichero.close();
            System.out.println("Archivo guardado exitosamente");
        } catch (IOException ex) {
            Logger.getLogger(ArchivoMR.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    }

    public boolean leer() {
        
        File archivo;
        FileReader fr = null;
        boolean resultado=false;
        
        //Abrir documento...
        Stage ventanaArchivo = new Stage(StageStyle.DECORATED);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Abrir Receta...");
        //fileChooser.setInitialFileName("Receta_de_" + titulo.replaceAll(" ", "_") + ".mr");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos MR (Mi Receta)", "*.mr"));
        File file = fileChooser.showOpenDialog(ventanaArchivo);
        
        //si el usuario eligió un archivo...
        if (file != null) {
        

        try {
         // Apertura del archivo de configuración, si existe.
            fr = new FileReader(file);

                
        JsonParser parser = new JsonParser();
        JsonElement datos = parser.parse(fr);
        
        if (datos.isJsonObject()) {
        System.out.println("Es objeto");
        JsonObject obj = datos.getAsJsonObject();
        java.util.Set<java.util.Map.Entry<String,JsonElement>> entradas = obj.entrySet();
        java.util.Iterator<java.util.Map.Entry<String,JsonElement>> iter = entradas.iterator();
        while (iter.hasNext()) {
            java.util.Map.Entry<String,JsonElement> entrada = iter.next();
           
            switch(entrada.getKey()) {
                case "titulo":
                    titulo = entrada.getValue().getAsString();
                    break;
                case "categoria":
                    categoria = entrada.getValue().getAsString();
                    break;
                case "contenido":
                    contenido= entrada.getValue().getAsString();
                    break;
                    
            }
            
            
        }
        resultado = true;
        }
        
        } catch (FileNotFoundException | JsonIOException | JsonSyntaxException e) {
            System.out.println("No encontré el archivo de configuración o está dañado. Usaremos los valores por defecto.");
        } finally {
         // En el finally cerramos el fichero, para asegurarnos
            // que se cierra tanto si todo va bien como si salta 
            // una excepcion.
            try {
                if (null != fr) {
                    fr.close();
                }
            } catch (Exception e2) {
            }
        }
        }
        return resultado;
    }
}
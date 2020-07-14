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
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vi
 */
public class Preferencias {

    /**
     * Guarda las preferencias de la aplicación en un archivo, en formato JSON
     */
    public void guardar() {
        Gson gson = new Gson();
        Collection<String> datos = new ArrayList<>();
        datos.add(Recetario.getApariencia());
        String jsonString = gson.toJson(datos);
        System.out.println("Preferencias guardadas: " + jsonString);

        FileWriter fichero;
        try {
            fichero = new FileWriter("preferencias.json");
            PrintWriter pw = new PrintWriter(fichero);
            pw.println(jsonString);
            fichero.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferencias.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String leer() {
        File archivo;
        FileReader fr = null;
        String linea = "";

        try {
         // Apertura del archivo de configuración, si existe.
            archivo = new File("preferencias.json");
            fr = new FileReader(archivo);

                
        JsonParser parser = new JsonParser();
        JsonElement datos = parser.parse(fr);
          
        if (datos.isJsonArray()) {
        JsonArray array = datos.getAsJsonArray();
        java.util.Iterator<JsonElement> iter = array.iterator();
        while (iter.hasNext()) {
            JsonElement entrada = iter.next();
            System.out.println(entrada);
            linea=entrada.getAsString();
        }
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
        return linea;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recetario;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Víctor
 */
public class VistaPDF extends Application {

    private final String titulo;
    private final String contenido;
    private final String categoria;

    public VistaPDF() {

        Receta rec = new Receta(Recetario.getRecetaSeleccionada());

        this.titulo = rec.titulo;
        this.contenido = rec.contenido;
        this.categoria = rec.categoria;
    }

    public VistaPDF(String seleccionado) {

        Receta rec = new Receta(seleccionado);

        this.titulo = rec.titulo;
        this.contenido = rec.contenido;
        this.categoria = rec.categoria;
    }

    public void crear() {

        //Guardar documento como...
        Stage ventanaArchivo = new Stage(StageStyle.DECORATED);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF...");
        fileChooser.setInitialFileName("Receta_de_" + titulo.replaceAll(" ", "_") + ".pdf");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Archivos PDF", "*.pdf"));
        File file = fileChooser.showSaveDialog(ventanaArchivo);

        //si el usuario eligió un archivo...
        if (file != null) {

            Rectangle pagina = new Rectangle(PageSize.LETTER.getWidth(), PageSize.LETTER.getHeight());
            pagina.setBackgroundColor(new BaseColor(255, 229, 153));

            Document documento = new Document(pagina, 50, 50, 50, 50);
            FileOutputStream ficheroPdf;
            try {
                ficheroPdf = new FileOutputStream(file);
// Se asocia el documento al OutputStream y se indica que el espaciado entre
// lineas sera de 20. Esta llamada debe hacerse antes de abrir el documento
                PdfWriter escritor = PdfWriter.getInstance(documento, ficheroPdf);
                escritor.setInitialLeading(20);

                //Voy a definir estilos de texto antes.
                Font etitulo = FontFactory.getFont(FontFactory.TIMES, // fuente
                        22, // tamaño
                        Font.BOLD, // estilo
                        new BaseColor(0x00, 0x47, 0x85));

                Font ecategoria = FontFactory.getFont(FontFactory.TIMES, 14, Font.UNDERLINE, new BaseColor(0x00, 0x47, 0x85));

// Se abre el documento.
                documento.open();

                //LOGO
                Image logo = Image.getInstance(getClass().getResource("img/logoMisRecetas.png").toExternalForm());
                logo.scaleToFit(100, 100);
                logo.setAlignment(Chunk.ALIGN_MIDDLE);
                documento.add(logo);

                //CATEGORIA
                Paragraph pcategoria = new Paragraph(categoria, ecategoria);
                pcategoria.setAlignment(Element.ALIGN_CENTER);
                documento.add(pcategoria);

                //TITULO
                Paragraph ptitulo = new Paragraph(titulo, etitulo);
                ptitulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(ptitulo);

                //IMAGEN SEPARADORA
                Image imagen = Image.getInstance(getClass().getResource("img/orla.png").toExternalForm());
                imagen.scaleToFit(200, 200);
                imagen.setAlignment(Chunk.ALIGN_MIDDLE);
                documento.add(imagen);

                //CONTENIDO HTML
                String contenidox = contenido.replace("<head><style>body {background-color: #ffe4ae;color: #AC3000;background-image: url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADwAAAA8CAMAAAANIilAAAAAA3NCSVQICAjb4U/gAAAA6lBMVEX1sB75tTL4syr7vjX7uh75uy3/yEH7vjz8vR76vTT0sSb7wzD0si79xUP7xDX9xh78xR73uTT7xT73th76uSr6ti/7vC78vTn3uCn7wx75wS/6vDj4sR77xUT5tS//xjf/yEf4uzj9xDH9vzb9vz77tSr6uy79ux76tx79vh74syf7uir9vjr6vTz9xTb+x0D9vS/3szD7tTL5tSr6sx7/xR72syn7uzb+x0T8vTr7tzD7uyr5uR79xj/3sR79vzz5syb7uR79vR7/ySb5sx73sy75ujX9xkT5tx73sx73tTD/vx7/xTD9xUQgKO7OAAAACXBIWXMAAAsSAAALEgHS3X78AAAAFnRFWHRDcmVhdGlvbiBUaW1lADA1LzI0LzE10DbJPgAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNui8sowAAAMsSURBVEiJlZfrTttAEEYXJ+HSkkDc4LQxrZ0GQkut4EqgFAuQkFDD+z9R57a7s+uY0PMHWnH8fTO7KMZMp9M8zzPmc8w18duyF2Lye2TJ8mQyYWki7JLzvKqqzMmedvJeLM/nmFy/p3bs7pkpsS1Z1z5GuuSlxfYFfgEsH3cl58RgUNfdyccdtsjL5du1dyQPMFzv6j1yluGofeAKeI0wxHWLS8Lwmq+YH45PQignSXKpELnP8usz0oqOYhOLlRWQ5wqcn4dyErBFRsKZP2wPbst22E/thSUx4cKeNTvk1sJkz35f5zR0LIPHyUdHR71e75ChJ/TtoQMvhDjfHE3T4BfTIw4tbnaWn56e2nJjMQsiVq0dJJ8BTmzgHyD75Oi8QL4lRG4CsLZOjjormW5jEyMz/2Ss6J5yS7XlKqvCjFk42a1byS8vKtlJZ7K5cGHWVnK0MFmZJLujctPqnQUyH646ryg5PqtATloLW9BZycLw5x8At207s6qtCJPjfbVmDi+Zkjn84UHXDi6JvtcsP+4jd8xpDDeRzxP+HVp4zOOjkluPYFk+TXg9oYycbA8+5fIiByLJN8SW1vQ/vPkBdIYPBboRsQzJtvEXweqcnNlkZeO3Jk1TkE9PPLoDz1zzx/dCeYTMjD9Ipqqs5cFA5J7G3PDCrBz6d8FRLWJUshU0fS3LvD03uynLr2VZpindlQIZjUbFCoCvoyyrganlwnJAmI0F5VFB2sr6K44M1AMPJBPcng226SufEb8A5KL4Z4jMV+VmFcNvDDbZRUp5U3LplJNx4FHh5SBZtRZ5U+LKMBZm9qXpIUXBdwtS4S2xOogxflv7+ykJunZdq21fxPhtb6A2xkkmn9nSJsdT62QJ+hjDb4N/mWmM+UN0ymSvkfGY3swJJQ+Hwy6ZX+XQlexcg7W9vJrNvgMzL+vaVDyQ49ro8iPwm4lLht7Udotc8G0EQXWeKRlz8a0+XNiQ6F6YmrmqSLln5vO52XAy0yXzxFXrqDhZLmR72/TqrBf237U7L8nmTZmPire9tvPOCfhN2ZHs5PF4vc75b7AquCSALGwWy8G2XW8r/wORr5FnBZiDHgAAAABJRU5ErkJggg==\");font-family: Cambria, serif;}</style></head><body contenteditable=\"true\">", "<body style=\"color: #AC3000;font-family: Cambria, serif;\">");
                contenidox = contenidox.replaceAll("<hr>", "<hr />");
                System.out.println(contenidox);
                InputStream is = new ByteArrayInputStream(contenidox.getBytes());
                XMLWorkerHelper.getInstance().parseXHtml(escritor, documento, is);

                //CODIGO PARA AGREGAR UNA TABLA
                /*PdfPTable tabla = new PdfPTable(3); // El número indica la cantidad de columnas de la tabla

                 for (int i = 0; i < 15; i++) {
                 tabla.addCell("celda " + i);
                 }
                 documento.add(tabla);*/
                //Cerrar documento
                documento.close();

                //abrir
                if (new Utiles().confirma("¿Desea abrir el PDF creado?")) {
                    getHostServices().showDocument(file.toString());
                }

            } catch (FileNotFoundException | DocumentException ex) {
                //Logger.getLogger(VistaPDF.class.getName()).log(Level.SEVERE, null, ex);
                new Utiles().error("El archivo está en uso o no se encuentra");

            } catch (IOException ex) {
                Logger.getLogger(VistaPDF.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

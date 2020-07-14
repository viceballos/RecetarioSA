/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recetario;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

/**
 * FXML Controller class
 *
 * @author Vi
 */
public class VistaReceta_Controlador implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button cambiaRecetario;
    @FXML
    private Label titulo;
    @FXML
    private Label categoria;
    @FXML
    private WebView contenido;
    @FXML
    private AnchorPane fondo;
    @FXML
    private VBox copiable;

    //Variables compartidas
    final basedatos.Conexion con = Recetario.getCon();
    final String seleccionado = Recetario.getRecetaSeleccionada();

    //Hojas de estilo disponibles
    private final String estilo1 = getClass().getResource("css/estilo.css").toExternalForm();
    private final String estilo2 = getClass().getResource("css/estilo2.css").toExternalForm();
    private final String estilo3 = getClass().getResource("css/estilo3.css").toExternalForm();
    private final String estilo4 = getClass().getResource("css/estilo4.css").toExternalForm();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cambiarApariencia(Recetario.getApariencia());
        ResultSet rs;
        try {
            rs = con.devolver("SELECT r.id id, r.titulo titulo, r.contenido contenido, c.titulo ccategoria "
                    + "FROM recetas as r, categorias as c "
                    + "WHERE r.idc=c.id AND r.titulo='" + seleccionado + "'");

            if (rs.next()) {

                String ttitulo = rs.getString("titulo");
                String tcontenido = rs.getString("contenido");
                String ccategoria = rs.getString("ccategoria");

                titulo.setText(ttitulo);
                categoria.setText(ccategoria);
                contenido.getEngine().setUserStyleSheetLocation(getClass().getResource("css/estiloweb.css").toExternalForm());
                contenido.getEngine().loadContent(tcontenido);

            }
            System.out.println("Receta cargada");

        } catch (SQLException ex) {
            Logger.getLogger(VistaReceta_Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * cierra esta ventana
     */
    @FXML
    private void cerrarVista(ActionEvent event) throws IOException {
        Stage stage = (Stage) cambiaRecetario.getScene().getWindow();
        stage.close();
    }

     @FXML
    private void crearPDF(ActionEvent event) throws IOException {
        VistaPDF vpdf = new VistaPDF(seleccionado);
        vpdf.crear();
    }
    
    /**
     * toma una captura de la ventana donde está la receta y la pone en el
     * portapapeles
     */
    @FXML
    private void copiarReceta() {
        try {
            // Obtenemos las coordenadas para la captura a partir de los bordes de un objeto
            Bounds b = copiable.getBoundsInParent();
            Stage stage = (Stage) cambiaRecetario.getScene().getWindow();
            Scene scene = cambiaRecetario.getScene();

            int x = (int) Math.round(stage.getX() + scene.getX() + b.getMinX()) + 10;
            int y = (int) Math.round(stage.getY() + scene.getY() + b.getMinY()) + 8;
            int w = (int) Math.round(b.getWidth()) - 20;
            int h = (int) Math.round(b.getHeight()) - 16;
            // Usamos ATW robot para capturar la imagen
            java.awt.Robot robot = new java.awt.Robot();
            java.awt.image.BufferedImage bi = robot.createScreenCapture(new java.awt.Rectangle(x, y, w, h));
            // Convertimos la BufferedImage en javafx.scene.image.Image
            java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
            ImageIO.write(bi, "png", stream);
            Image image = new Image(new java.io.ByteArrayInputStream(stream.toByteArray()), w, h, true, true);
            // la colocamos en el portapapeles
            ClipboardContent cc = new ClipboardContent();
            cc.putImage(image);
            Clipboard.getSystemClipboard().setContent(cc);
            System.out.println("Imagen copiada");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Imprime la receta V2
     */
    @FXML
    private void imprimirReceta() {
        System.out.println("Comenzando impresión");
        
        //Ventana nueva para preguntar impresora y tamaño de página
        Stage ventanaImpresion = new Stage(StageStyle.DECORATED);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            //boolean showDialog = job.showPageSetupDialog(ventanaImpresion); //Ajuste de página. No lo usaré.
            boolean dialogoImpresion = job.showPrintDialog(ventanaImpresion);

            if (dialogoImpresion) {
                //Cálculo de la escala para imprimir                       
                double ancho = job.getJobSettings().getPageLayout().getPrintableWidth();
                double alto = job.getJobSettings().getPageLayout().getPrintableHeight();
                //Depende de si la página está pará o botá, usamos la escala X o Y       
                if (alto > ancho) {
                    double escala = ancho / copiable.getBoundsInParent().getWidth();
                    copiable.getTransforms().add(new Scale(escala, escala));
                } else {
                    double escala = alto / copiable.getBoundsInParent().getHeight();
                    copiable.getTransforms().add(new Scale(escala, escala));
                }

                //Comprobamos la impresión exitosa
                if (job.printPage(copiable)) {
                    job.endJob();
                    System.out.println("Impresión exitosa");
                }
                //Volvemos todo a la normalidad
                copiable.getTransforms().clear();

            }
        }

    }

    private void cambiarApariencia(String valor) {
        switch (valor) {
            case "Ninguno":
                fondo.getStylesheets().clear();
                break;
            case "":
                fondo.getStylesheets().clear();
                break;
            case "Es mi vida":
                fondo.getStylesheets().clear();
                fondo.getStylesheets().add(estilo1);
                break;
            case "Otoño":
                fondo.getStylesheets().clear();
                fondo.getStylesheets().add(estilo2);
                break;
            case "Soy Batman":
                fondo.getStylesheets().clear();
                fondo.getStylesheets().add(estilo3);
                break;
            case "No veo":
                fondo.getStylesheets().clear();
                fondo.getStylesheets().add(estilo4);
                break;
        }
    }
}

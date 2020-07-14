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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.DepthTest;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Vi
 */
public class InterfazPrincipal_Controlador implements Initializable {

    /**
     * Campos y Nodos inyectados desde el FXML
     */
    @FXML
    private ComboBox<String> estilos, listaCategorias;
    @FXML
    private Accordion categorias;
    @FXML
    private Pane panelCategorias;
    @FXML
    private TextField titulo, textoCategoria;
    @FXML
    private ListView<String> listadoCategorias;
    @FXML
    private HTMLEditor contenido;
    @FXML
    private AnchorPane fondo;
    @FXML
    private Button verReceta;
    @FXML
    private Box cubo;
    @FXML
    private HBox barrabaja, barraalta;

    //Creamos la conexión para todos los métodos
    private final basedatos.Conexion con = Recetario.getCon();

    //Objeto Categoría. Una clase orientada a objetos.
    private final Categoria cat = new Categoria();

    //Algunas variables para usar
    private int id;
    private String seleccionado = "nada";

    //Hojas de estilo disponibles
    private final String estilo1 = getClass().getResource("css/estilo.css").toExternalForm();
    private final String estilo2 = getClass().getResource("css/estilo2.css").toExternalForm();
    private final String estilo3 = getClass().getResource("css/estilo3.css").toExternalForm();
    private final String estilo4 = getClass().getResource("css/estilo4.css").toExternalForm();

//////////////COMIENZAN LOS MÉTODOS//////////////
    /**
     * Inicialización. Esto se ejecuta al cargar la escena.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ocultabarras();
        obtenerListaRecetas();
        //Llenado de los estilos
        ObservableList<String> items = FXCollections.observableArrayList("Ninguno", "Es mi vida", "Otoño", "Soy Batman", "No veo");
        estilos.setItems(items);

        //Si hay algún estilo en uso, lo asignamos
        cambiarApariencia(Recetario.getApariencia());

        try {
//Llenado de la lista de categorias
            ObservableList<String> item = FXCollections.observableArrayList();
            ResultSet rs = con.devolver("SELECT titulo FROM categorias ORDER BY titulo");
            while (rs.next()) {
                String rtitulo = rs.getString("titulo");
                item.add(rtitulo);
            }
            listaCategorias.setItems(item);
        } catch (SQLException ex) {
            Logger.getLogger(InterfazPrincipal_Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Llenado de la lista de categorías. Estilo OO.
        listadoCategorias.setItems(cat.obtenerLista());

        //Ejemplo de un Tooltip. Para el botón de verReceta.
        verReceta.setTooltip(new Tooltip("Muestra la Receta"));

        //Material para el cubito
        final PhongMaterial amarillo = new PhongMaterial();
        amarillo.setSpecularColor(Color.web("#FFFFFF"));
        amarillo.setDiffuseColor(Color.web("#FFDC73"));
        amarillo.setSpecularPower(10.0);
        amarillo.setDiffuseMap(new Image(getClass().getResource("img/cocina.png").toExternalForm()));

        //asignación del material al cubo
        cubo.setDepthTest(DepthTest.DISABLE);
        cubo.setMaterial(amarillo);

        //Vinculamos el movimiento del mouse con la rotación del cubo
        fondo.addEventFilter(MouseEvent.MOUSE_MOVED, (MouseEvent event) -> {
            cubo.setRotate((event.getSceneX() + event.getSceneY()) / 5);
        });

    }

    ////LOS MÉTODOS INYECTADOS DESDE EL FXML////   
    ///MÉTODOS ESTILO ORIENTADO A OBJETOS. RECOMENDABLE.//
    @FXML
    private void modificarCategoria(ActionEvent event) {
        String catsel = listadoCategorias.getSelectionModel().getSelectedItem();
        cat.modificar(catsel);
        listadoCategorias.setItems(cat.obtenerLista());
    }

    @FXML
    private void agregarCategoria(ActionEvent event) {
        cat.agregar(textoCategoria.getText());
        listadoCategorias.setItems(cat.obtenerLista());
    }

    @FXML
    private void seleccionaCategoria() {
        String catsel = listadoCategorias.getSelectionModel().getSelectedItem();
        textoCategoria.setText(catsel);
    }

    @FXML
    private void borraCategoria(ActionEvent event) {
        String catsel = listadoCategorias.getSelectionModel().getSelectedItem();
        cat.borrar(catsel);
        listadoCategorias.setItems(cat.obtenerLista());
    }

    ///MÉTODOS ESTILO ESTRUCTURADO. NO RECOMENDABLE PERO FUNCIONAL.///
    /**
     * Recoje la orden desde el FXML y llama al método de acá. Recoge el valor
     * seleccionado en la lista
     */
    @FXML
    private void cambiarApariencia(ActionEvent event) {
        String valor = estilos.getValue();
        cambiarApariencia(valor);

    }

    /**
     * Permite almacenar las preferencias del usuario
     */
    @FXML
    private void guardarPreferencias(ActionEvent event) {
        if (new Utiles().confirma("¿Desea guardar la Apariencia actual como aspecto de inicio?")) {
            new Preferencias().guardar();
        }
    }

    /**
     * Permite guardar un archivo MR
     */
    @FXML
    private void guardarMR(ActionEvent event) {
        new ArchivoMR().guardar();
    }

    /**
     * Permite abrir un archivo MR
     */
    @FXML
    private void abrirMR(ActionEvent event) {
        ArchivoMR mr = new ArchivoMR();
        if (mr.leer()) {
            titulo.setText(mr.titulo);
            contenido.setHtmlText(mr.contenido);
            listaCategorias.setValue(mr.categoria);

            if (new Utiles().confirma("¿Desea guardar esta receta en sus contenidos personales?")) {
                con.ejecutar("INSERT INTO recetas (titulo, contenido, idc) VALUES ('" + titulo.getText() + "','" + contenido.getHtmlText() + "',(SELECT id FROM categorias WHERE titulo='" + listaCategorias.getValue() + "'))");
                obtenerListaRecetas();
            }

        }
    }

    /**
     * Abre una nueva ventana y muestra la receta en un formato completo
     */
    @FXML
    private void mostrarReceta(ActionEvent event) throws IOException {
        //Stage stage = (Stage) verReceta.getScene().getWindow();
        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getResource("VistaReceta.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Deja los campos limpios para crear una nueva receta
     */
    @FXML
    private void limpiarFormulario(ActionEvent event) {
        ocultabarras();
        titulo.setText("");
        contenido.setHtmlText("<html><head><style>body {background-color: #ffe4ae;color: #AC3000;background-image: url(\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAADwAAAA8CAMAAAANIilAAAAAA3NCSVQICAjb4U/gAAAA6lBMVEX1sB75tTL4syr7vjX7uh75uy3/yEH7vjz8vR76vTT0sSb7wzD0si79xUP7xDX9xh78xR73uTT7xT73th76uSr6ti/7vC78vTn3uCn7wx75wS/6vDj4sR77xUT5tS//xjf/yEf4uzj9xDH9vzb9vz77tSr6uy79ux76tx79vh74syf7uir9vjr6vTz9xTb+x0D9vS/3szD7tTL5tSr6sx7/xR72syn7uzb+x0T8vTr7tzD7uyr5uR79xj/3sR79vzz5syb7uR79vR7/ySb5sx73sy75ujX9xkT5tx73sx73tTD/vx7/xTD9xUQgKO7OAAAACXBIWXMAAAsSAAALEgHS3X78AAAAFnRFWHRDcmVhdGlvbiBUaW1lADA1LzI0LzE10DbJPgAAABx0RVh0U29mdHdhcmUAQWRvYmUgRmlyZXdvcmtzIENTNui8sowAAAMsSURBVEiJlZfrTttAEEYXJ+HSkkDc4LQxrZ0GQkut4EqgFAuQkFDD+z9R57a7s+uY0PMHWnH8fTO7KMZMp9M8zzPmc8w18duyF2Lye2TJ8mQyYWki7JLzvKqqzMmedvJeLM/nmFy/p3bs7pkpsS1Z1z5GuuSlxfYFfgEsH3cl58RgUNfdyccdtsjL5du1dyQPMFzv6j1yluGofeAKeI0wxHWLS8Lwmq+YH45PQignSXKpELnP8usz0oqOYhOLlRWQ5wqcn4dyErBFRsKZP2wPbst22E/thSUx4cKeNTvk1sJkz35f5zR0LIPHyUdHR71e75ChJ/TtoQMvhDjfHE3T4BfTIw4tbnaWn56e2nJjMQsiVq0dJJ8BTmzgHyD75Oi8QL4lRG4CsLZOjjormW5jEyMz/2Ss6J5yS7XlKqvCjFk42a1byS8vKtlJZ7K5cGHWVnK0MFmZJLujctPqnQUyH646ryg5PqtATloLW9BZycLw5x8At207s6qtCJPjfbVmDi+Zkjn84UHXDi6JvtcsP+4jd8xpDDeRzxP+HVp4zOOjkluPYFk+TXg9oYycbA8+5fIiByLJN8SW1vQ/vPkBdIYPBboRsQzJtvEXweqcnNlkZeO3Jk1TkE9PPLoDz1zzx/dCeYTMjD9Ipqqs5cFA5J7G3PDCrBz6d8FRLWJUshU0fS3LvD03uynLr2VZpindlQIZjUbFCoCvoyyrganlwnJAmI0F5VFB2sr6K44M1AMPJBPcng226SufEb8A5KL4Z4jMV+VmFcNvDDbZRUp5U3LplJNx4FHh5SBZtRZ5U+LKMBZm9qXpIUXBdwtS4S2xOogxflv7+ykJunZdq21fxPhtb6A2xkkmn9nSJsdT62QJ+hjDb4N/mWmM+UN0ymSvkfGY3swJJQ+Hwy6ZX+XQlexcg7W9vJrNvgMzL+vaVDyQ49ro8iPwm4lLht7Udotc8G0EQXWeKRlz8a0+XNiQ6F6YmrmqSLln5vO52XAy0yXzxFXrqDhZLmR72/TqrBf237U7L8nmTZmPire9tvPOCfhN2ZHs5PF4vc75b7AquCSALGwWy8G2XW8r/wORr5FnBZiDHgAAAABJRU5ErkJggg==\");font-family: Cambria, serif;}</style></head><body contenteditable=\"true\"></body></html>");
    }

    /**
     * Agrega una nueva receta con los contenidos del formulario
     */
    @FXML
    private void agregarReceta(ActionEvent event) throws SQLException {
        con.ejecutar("INSERT INTO recetas (titulo, contenido, idc) VALUES ('" + titulo.getText() + "','" + contenido.getHtmlText() + "',(SELECT id FROM categorias WHERE titulo='" + listaCategorias.getValue() + "'))");
        obtenerListaRecetas();
    }

    /**
     * Actualiza la receta seleccionada *
     */
    @FXML
    private void modificarReceta(ActionEvent event) throws SQLException {
        con.ejecutar("UPDATE recetas SET titulo='" + titulo.getText() + "',contenido='" + contenido.getHtmlText() + "', idc=(SELECT id FROM categorias WHERE titulo='" + listaCategorias.getValue() + "') WHERE id=" + id + "");
        obtenerListaRecetas();
    }

    /**
     * Elimina la receta seleccionada *
     */
    @FXML
    private void borrarReceta(ActionEvent event) throws SQLException {
//Mensaje de confirmación del borrado. Funciona con la última versión del JDK, 8u40 o superior.
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Está seguro de eliminar esta receta?");
        alert.setContentText("No es posible deshacer esta acción");
        Optional<ButtonType> result = alert.showAndWait();
        //Vemos si el usuario acepta borra o no
        if (result.get() == ButtonType.OK) {
            //Acepta el borrado
            String orden = "DELETE FROM recetas WHERE titulo='" + seleccionado + "'";
            System.out.println(orden);
            con.ejecutar(orden);
            obtenerListaRecetas();
        } else {
            //Cancela el borrado; no hacemos nada
        }
    }

    @FXML
    private void aparece(ActionEvent event) {

        FadeTransition ft = new FadeTransition(Duration.millis(500), panelCategorias);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.play();
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        final KeyValue kv;
        kv = new KeyValue(panelCategorias.translateXProperty(), 0, Interpolator.SPLINE(0.7007, 0.0000, 0.3020, 1.0000));
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

    }

    @FXML
    private void oculta(ActionEvent event) {

        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        final KeyValue kv;
        kv = new KeyValue(panelCategorias.translateXProperty(), 200, Interpolator.SPLINE(0.7007, 0.0000, 0.3020, 1.0000));
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();
        obtenerListaRecetas();

    }

    ////LOS MÉTODOS INTERNOS DEL CONTROLADOR////
    /**
     * Carga una hoja de estilos a la escena.
     */
    private void cambiarApariencia(String valor) {
        Recetario.setApariencia(valor);
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

    private void muestrabarras() {
        barrabaja.setVisible(true);
        barraalta.setVisible(true);
    }

    private void ocultabarras() {
        barrabaja.setVisible(false);
        barraalta.setVisible(false);
    }

    /**
     * Obtiene el listado de títulos de las recetas y sus categorías. Genera
     * dinámicamente TitledPane (Paneles colapsables) según las categorías en la
     * BD Luego obtiene las recetas de cada categoría y crea un ListView para
     * cada una. En seguida llena cada TitledPane con su ListView
     */
    private void obtenerListaRecetas() {

        try {
            // Contamos cuántas categorías hay
            int totalCategorias = 0;
            ResultSet rsc = con.devolver("SELECT count(id) id FROM categorias");
            if (rsc.next()) {
                totalCategorias = rsc.getInt("id");
            }
            System.out.println("Cantidad categorías: " + totalCategorias);
//Obtenemos las categorías
            ResultSet rs = con.devolver("SELECT id, titulo FROM categorias ORDER BY titulo");
//Generamos un Array de TitledPane. Aquí hay que decir la cantidad de elementos que tendrá.
            TitledPane[] tps = new TitledPane[totalCategorias];
//Ciclo para crear y llenar, según la cantidad de categorías
            int i = 0;
            while (rs.next()) {
//Recupero las recetas de esta categoría en particular
                ResultSet rsr = con.devolver("SELECT titulo FROM recetas WHERE idc=" + rs.getInt("id") + " ORDER BY titulo");

                String ttitulo = rs.getString("titulo");
                System.out.println(ttitulo);
//Creo una lista con las recetas
                ObservableList<String> itemc = FXCollections.observableArrayList();
                while (rsr.next()) {
                    String rtitulo = rsr.getString("titulo");
                    itemc.add(rtitulo);
                }
                //Genero un nuevo ListView con la lista de recetas
                ListView<String> listado = new ListView<>(itemc);
//Agregamos un "listener", algo que detonará acciones al hacer click.
                listado.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends String> ov, String old_val, String seleccion) -> {
                    System.out.println("Seleccionado:" + seleccion);
                    //Ponemos lo seleccionado en esta variable para tenerlo en otras operaciones
                    seleccionado = seleccion;
                    Recetario.setRecetaSeleccionada(seleccion);
                    try {
                        //Llamamos otro método para que cargue la receta completa al hacer click
                        obtenerReceta(seleccion);
                    } catch (SQLException ex) {
                        Logger.getLogger(InterfazPrincipal_Controlador.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
//Llenamos uno de los TitledPane con la lista recién creada
                tps[i] = new TitledPane(ttitulo, listado);
                i++;
                System.out.println("Creado TPS" + i);
            }
            //Borramos todo lo que pueda tener el Accordion llamado categorías
            categorias.getPanes().clear();
            //Agregamos nuestros TitledPanes
            categorias.getPanes().addAll(tps);
            //Abrimos el primer TitledPane
            categorias.setExpandedPane(tps[0]);
            System.out.println(tps.length);
        } catch (SQLException ex) {
            Logger.getLogger(InterfazPrincipal_Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Carga una receta y la muestra en el formulario
     */
    private void obtenerReceta(String seleccionado) throws SQLException {
// Obtiene los datos de ambas tablas
        ResultSet rs = con.devolver("SELECT r.id id, r.titulo titulo, r.contenido contenido, c.titulo ccategoria "
                + "FROM recetas as r, categorias as c "
                + "WHERE r.idc=c.id AND r.titulo='" + seleccionado + "'");

        if (rs.next()) {

            id = rs.getInt("id");
            String ttitulo = rs.getString("titulo");
            String tcontenido = rs.getString("contenido");
            String ccategoria = rs.getString("ccategoria");

            titulo.setText(ttitulo);
            contenido.setHtmlText(tcontenido);
            listaCategorias.setValue(ccategoria);

        }
        System.out.println("Receta cargada");
        muestrabarras();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recetario;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Vi
 */
public class Utiles {
    
    public void error(String mensaje) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("Ocurrió un error");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public boolean confirma(String mensaje) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CONFIRMACION");
        alert.setHeaderText(mensaje);
        //alert.setContentText(mensaje);
        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.get() == ButtonType.OK;
        
    }
    
}

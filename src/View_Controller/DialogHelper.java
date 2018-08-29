/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

/**
 *
 * @author Ryan Simpson
 */
public class DialogHelper {
    
    public void openDialog(AlertType type, String dialogMessage) {
        Alert alert = new Alert(type);
        alert.setTitle(type.toString());
        alert.setHeaderText(null);
        alert.setContentText(dialogMessage);

        alert.showAndWait();    
    }
    
    public boolean openConfirmationDialog(AlertType type, String dialogMessage) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(type.toString());
        alert.setHeaderText("Confirmation");
        alert.setContentText(dialogMessage);

        Optional<ButtonType> result = alert.showAndWait();
        // Return true if OK button was pressed
        // Return false if Cancel button was pressed
        return result.get() == ButtonType.OK;
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulerapp;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Ryan Simpson
 */
public class ChangeSceneHelper {
    
    public static void changeScene(Stage stage, Parent root) {
        // create a new scene with root and set the stage
        if (root != null && stage != null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }   
}
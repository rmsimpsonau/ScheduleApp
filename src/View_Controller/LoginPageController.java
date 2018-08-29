/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import schedulerapp.DatabaseQuery;
import Model.LoggedInUser;
import schedulerapp.ChangeSceneHelper;
import schedulerapp.LogWriter;

/**
 * FXML Controller class
 *
 * @author sim59419
 */
public class LoginPageController implements Initializable {
    
    @FXML private Label loginLabel;
    @FXML private TextField loginUsernameField;
    @FXML private PasswordField loginPasswordField;
    @FXML private Button loginButton;
    @FXML private Label loginErrorMessage;
    @FXML private Label usernameError;
    @FXML private Label passwordError;

    ResourceBundle loginProperties;
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
// LOCALE TESTING
//        Locale locale = new Locale("fr");
//        Locale.setDefault(locale); 
// END LOCALE TESTING
        
        loginProperties = ResourceBundle.getBundle("resources/loginPage", Locale.getDefault());
        
        // Set fields, labels, and text based on locale
        loginLabel.setText(loginProperties.getString("title"));
        loginUsernameField.setPromptText(loginProperties.getString("username"));
        loginPasswordField.setPromptText(loginProperties.getString("password"));
        loginButton.setText(loginProperties.getString("loginButton"));
        
        // Lambda used to listen to text being entered into username/password field and keeping the login button
        // disabled until text is entered into both fields. 
        loginButton.setDisable(true);
        loginUsernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || loginPasswordField.getText().trim().isEmpty());
        });
        loginPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty() || loginUsernameField.getText().trim().isEmpty());
        });
    }    
    
    
    // Executes when loginButton is pressed
    @FXML
    private void loginAttempt(ActionEvent event) throws IOException, SQLException {
        
        
        // Clear any error messages everytime user tries to login
        hideLoginErrorMessage();
        hideUsernameErrorMessage();
        hidePasswordErrorMessage();
        
        // Get contents of text fields
        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();
        
        if (username.equals("") || password.equals(""))
        {
            if (username.equals("")) showUsernameErrorMessage(loginProperties.getString("emptyUsername"));
            if (password.equals("")) showPasswordErrorMessage(loginProperties.getString("emptyPassword"));
        } else {
        
            // Execute sql query to get all user data from DB
            DatabaseQuery dbQuery = new DatabaseQuery("select * from user");
            
            try {
                dbQuery.execute();
            } catch (SQLException e) {
                System.out.println("SQLException selecting all from user table: " + e.getMessage());
            }
            ResultSet results = dbQuery.getResults();

            if (results != null)
            {
                boolean foundUser = false;
                ResultSet userInfo = null;

                while (results.next())
                {
                    /* 
                    user schema
                        userId INT
                        userName VARCHAR
                        password VARCHAR
                        active TINYINT
                        createdBy VARCHAR
                        createDate DATETIME
                        lastUpdate TIMESTAMP
                        lastUpdatedBy VARCHAR
                    */

                    String dbUsername = results.getString("user.username");

                    if (username.equals(dbUsername))
                    {
                        foundUser = true;
                        userInfo = results;
                        break;
                    }
                }

                // Check if username and password matched and user info is not null
                if (foundUser && results.getString("user.password").equals(password) && userInfo != null)
                {

                    // Populate LoggedInUser object with data from the user DB record
                    LoggedInUser.setUserId(userInfo.getInt("user.userId"));
                    LoggedInUser.setUsername(userInfo.getString("user.username"));
                    LoggedInUser.setPassword(userInfo.getString("user.password"));
                    LoggedInUser.setActive(userInfo.getInt("user.active"));
                    LoggedInUser.setCreatedBy(userInfo.getString("user.createBy"));
                    LoggedInUser.setCreateDate(userInfo.getString("user.createDate"));
                    LoggedInUser.setLastUpdate(userInfo.getString("user.lastUpdate"));
                    LoggedInUser.setLastUpdatedBy(userInfo.getString("user.lastUpdatedBy"));
                    LoggedInUser.setCheckedNextAppointments(false);
                    
                    // Record user login in log file
                    LogWriter.logUserLogin();

                    // Show Schedule View
                    showScheduleView();
                } else {
                    showLoginErrorMessage(loginProperties.getString("incorrectUsernamePassword"));
                }
            } else {
                System.out.println("There was a problem getting your results");
            }
        }
    }
    
    private void showLoginErrorMessage(String errorString) {
        loginErrorMessage.setText(errorString);
        loginErrorMessage.setVisible(true);
    }
   
    private void hideLoginErrorMessage() {
        loginErrorMessage.setText("");
        loginErrorMessage.setVisible(false);
    }
    
    private void showUsernameErrorMessage(String errorString) {
        usernameError.setText(errorString);
        usernameError.setVisible(true);
    }
    
    private void hideUsernameErrorMessage() {
        usernameError.setText("");
        usernameError.setVisible(false);
    }
    
    private void showPasswordErrorMessage(String errorString) {
        passwordError.setText(errorString);
        passwordError.setVisible(true);
    }
    
    private void hidePasswordErrorMessage() {
        passwordError.setText("");
        passwordError.setVisible(false);
    }
    
    
    private void showScheduleView() throws IOException {
        //get reference to the button's stage
        Stage stage=(Stage)loginLabel.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/ScheduleView.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
}

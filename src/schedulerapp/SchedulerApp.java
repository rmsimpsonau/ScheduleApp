/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulerapp;

import Model.Appointment;
import static Model.LoggedInUser.DEFAULT_LOCALE;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author sim59419
 */
public class SchedulerApp extends Application {
    
    private static Connection dbConnection;
    public static boolean editAppointmentMode = false;
    public static Appointment selectedAppointment;
    
    // Formatters
    public static final DateTimeFormatter LOCAL_DATE_TIME_AMPM_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy h:mm a", DEFAULT_LOCALE);
    public static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", DEFAULT_LOCALE);
    public static final DateTimeFormatter LOCAL_DATE_TIME_MILLISECONDS_AMPM_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", DEFAULT_LOCALE);
    public static final DateTimeFormatter LOCAL_DATE_TIME_SECONDS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", DEFAULT_LOCALE);
    public static final DateTimeFormatter LOCAL_DATE_TIME_MILLISECONDS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S", DEFAULT_LOCALE);
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", DEFAULT_LOCALE);
    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    
    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException, SQLException, NullPointerException {
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/LoginPage.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Scheduler App");
        primaryStage.setScene(scene);
        // Set On Close Request with Lambda Expression
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            try {
                DatabaseConnection.closeConnection();
            } catch (SQLException ex) {
                System.out.println("Exception when closing connection");
            } catch (NullPointerException ex) {
                System.out.println("Nullpointer when closing connection");
            }
            System.exit(0);
        });
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     * @throws java.lang.ClassNotFoundException
     * @throws java.sql.SQLException
     */
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        // Initialize database connection
        DatabaseConnection.connect();
        dbConnection = DatabaseConnection.getConnection();
        launch(args);
    }
    
    public static boolean getEditAppointmentMode()
    {
        return editAppointmentMode;
    }
    
    public static void setEditAppointmentMode(boolean newEditAppointmentMode)
    {
        editAppointmentMode = newEditAppointmentMode;
    }
    
    // Selected appointment
    public static Appointment getSelectedAppointment()
    {
        return selectedAppointment;
    }
    
    public static void setSelectedAppointment(Appointment newSelectedAppointment)
    {
        selectedAppointment = newSelectedAppointment;
    }
    
}

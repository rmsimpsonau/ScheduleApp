/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import java.io.IOException;
import Model.LoggedInUser;
import java.net.URL;
import java.sql.SQLException;
import java.util.ListIterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import schedulerapp.ChangeSceneHelper;
import schedulerapp.DatabaseConnection;
import schedulerapp.DatabaseQuery;
import schedulerapp.SchedulerApp;
import schedulerapp.Utilities;

/**
 * FXML Controller class
 *
 * @author sim59419
 */
public class ScheduleViewController implements Initializable
{
    
    @FXML private Label userWelcomeText;
    @FXML private Button editCustomersButton;
    @FXML private Button newAppointmentButton;
    @FXML private Button editAppointmentButton;
    @FXML private Button deleteAppointmentButton;
    @FXML private Button logoutButton;
    @FXML private Button reportsViewButton;
    @FXML private RadioButton monthViewRadio;
    @FXML private RadioButton weekViewRadio;
    @FXML private ToggleGroup scheduleView;
    // Schedule TableView
    @FXML private TableView<Appointment> scheduleTableView;
    @FXML private TableColumn<Appointment, String> titleCol;
    @FXML private TableColumn<Appointment, String> descriptionCol;
    @FXML private TableColumn<Appointment, String> customerCol;
    @FXML private TableColumn<Appointment, String> typeCol;
    @FXML private TableColumn<Appointment, String> locationCol;
    @FXML private TableColumn<Appointment, String> contactCol;;
    @FXML private TableColumn<Appointment, String> urlCol;
    @FXML private TableColumn<Appointment, String> dateCol;
    @FXML private TableColumn<Appointment, String> startCol;
    @FXML private TableColumn<Appointment, String> endCol;
    
    private ObservableList<Appointment> appointmentsThisWeek = FXCollections.observableArrayList();
    private ObservableList<Appointment> appointmentsThisMonth = FXCollections.observableArrayList();
    
    String currentViewMode = "month";

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        SchedulerApp.setEditAppointmentMode(false);
        SchedulerApp.setSelectedAppointment(null);
        userWelcomeText.setText("Welcome, " + LoggedInUser.USERNAME);
        
        // Lambda expression implementation
        // The lambda expression was better in this case because it's much cleaner
        // and easier to read than a method reference or regular binding actions
        monthViewRadio.setOnAction((event) -> {
            // Set view to month view
            populateTableView("month");
        });
        
        // Lambda expression implementation
        weekViewRadio.setOnAction((event) -> {
            // Set view to week view
            populateTableView("week");
        });
        
        try {
            populateAppointmentLists();
            populateTableView("month");
            checkNextAppointments();
        } catch (SQLException ex) {
            System.out.println("Exception populating tables: " + ex.getMessage());
        }
    }
    
    @FXML
    public void newCustomer() throws IOException
    {
        SchedulerApp.setEditAppointmentMode(false);
        showAppointmentEditorView();
    }
    
    @FXML
    public void logoutUser() throws IOException
    {
        //get reference to the button's stage
        Stage stage=(Stage)editCustomersButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/LoginPage.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
    
    @FXML
    public void showEditCustomersView() throws IOException {
        //get reference to the button's stage
        Stage stage=(Stage)editCustomersButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/EditCustomers.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
    
    @FXML
    public void showAppointmentEditorView() throws IOException
    {
        //get reference to the button's stage
        Stage stage=(Stage)editCustomersButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/AppointmentEditor.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
    
    @FXML
    public void showReportsView() throws IOException
    {
        //get reference to the button's stage
        Stage stage=(Stage)editCustomersButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/ReportsView.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
    
    @FXML
    public void editAppointmentButtonClicked() throws IOException
    {
        Appointment selectedAppointment = scheduleTableView.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null)
        {
            DialogHelper dialog = new DialogHelper();
            dialog.openDialog(Alert.AlertType.ERROR, "No appointment selected. Please select an appointment first.");
        }
        else
        {
            SchedulerApp.setEditAppointmentMode(true);
            SchedulerApp.setSelectedAppointment(scheduleTableView.getSelectionModel().getSelectedItem());
            showAppointmentEditorView();
        }
    }
    
    private void populateAppointmentLists() throws SQLException
    {
        appointmentsThisMonth = DatabaseConnection.getUserAppointments(LoggedInUser.USER_ID, Utilities.generateTimestamp().plusDays(30));
        appointmentsThisWeek = DatabaseConnection.getUserAppointments(LoggedInUser.USER_ID, Utilities.generateTimestamp().plusDays(7));
    }
    
    private void populateTableView(String view)
    {
        currentViewMode = view;
        if (view.equals("month"))
        {
            // Populate table view
            scheduleTableView.setItems(appointmentsThisMonth);
        }
        else if (view.equals("week"))
        {
            // Populate table view
            scheduleTableView.setItems(appointmentsThisWeek);
        }
        // Populate table view cells
        titleCol.setCellValueFactory(cellData -> cellData.getValue().getTitle());
        descriptionCol.setCellValueFactory(cellData -> cellData.getValue().getDescription());
        customerCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerName());
        typeCol.setCellValueFactory(cellData -> cellData.getValue().getType());
        locationCol.setCellValueFactory(cellData -> cellData.getValue().getLocation());
        contactCol.setCellValueFactory(cellData -> cellData.getValue().getContact());
        urlCol.setCellValueFactory(cellData -> cellData.getValue().getUrl());
        dateCol.setCellValueFactory(cellData -> cellData.getValue().getStartDateFormatted());
        startCol.setCellValueFactory(cellData -> cellData.getValue().getStartTimeFormatted());
        endCol.setCellValueFactory(cellData -> cellData.getValue().getEndTimeFormatted());
        // Sort ASCENDING by date
        dateCol.setSortType(TableColumn.SortType.ASCENDING);
        scheduleTableView.getSortOrder().add(dateCol);
    }
    
    @FXML
    private void deleteAppointment()
    {
        Appointment selectedAppointment = scheduleTableView.getSelectionModel().getSelectedItem();
        if (selectedAppointment == null)
        {
            DialogHelper dialog = new DialogHelper();
            dialog.openDialog(Alert.AlertType.ERROR, "No appointment selected. Please select an appointment first.");
        }
        else
        {
            int selectedAppointmentId = selectedAppointment.getAppointmentId().getValue();
            DatabaseQuery addressDbQuery = new DatabaseQuery(
                "DELETE from appointment "
                + "WHERE appointmentId=" + selectedAppointmentId + ";"
            );
            try {
                addressDbQuery.executeUpdate();
                populateAppointmentLists();
                populateTableView(currentViewMode);
            } catch (SQLException ex) {
                System.out.println("Exception occurred populating appointments and table view: " + ex.getMessage());
            }
        }
    }
    
    private void checkNextAppointments() throws SQLException
    {
        if (!LoggedInUser.CHECKED_NEXT_APPOINTMENTS)
        {
            ObservableList<Appointment> nextAppointments = DatabaseConnection.getUserNextAppointments(LoggedInUser.USER_ID);
            if (!nextAppointments.isEmpty())
            {
                String appointments = "\n\n";
                ListIterator<Appointment> appointmentIterator = nextAppointments.listIterator();
                while(appointmentIterator.hasNext())
                {
                    Appointment appointment = appointmentIterator.next();
                    // Title
                    String title = appointment.getTitle().getValue();
                    appointments = appointments.concat(" Title: " + title);
                    
                    // Description
                    String description = appointment.getDescription().getValue();
                    if (!description.equals("")) appointments = appointments.concat("\n Description: " + description);
                    
                    // Customer
                    String customer = appointment.getCustomerName().getValue();
                    appointments = appointments.concat("\n Customer: " + customer);
                    
                    // Location
                    String location = appointment.getLocation().getValue();
                    if (!location.equals("")) appointments = appointments.concat("\n Location: " + location);
                    
                    // Contact
                    String appointmentContact = appointment.getContact().getValue();
                    if (!appointmentContact.equals("")) appointments = appointments.concat("\n Contact: " + appointmentContact);
                    
                    // Type
                    String type = appointment.getType().getValue();
                    appointments = appointments.concat("\n Type: " + type);
                    
                    // URL
                    String url = appointment.getUrl().getValue();
                    if (!url.equals("")) appointments = appointments.concat("\n URL: " + url);
                    
                    // Start / End Time
                    String startDate = appointment.getStartDateFormatted().getValue();
                    String startTime = appointment.getStartTimeFormatted().getValue();
                    String endTime = appointment.getEndTimeFormatted().getValue();
                    appointments = appointments.concat("\n Date/Time: " + startDate + " " + startTime + "-" + endTime);
                        
                }
                DialogHelper dialog = new DialogHelper();
                dialog.openDialog(Alert.AlertType.INFORMATION, "You have the following appointments starting in the next 15 minutes:" + appointments);
            }
            LoggedInUser.setCheckedNextAppointments(true);
        }
    }
}

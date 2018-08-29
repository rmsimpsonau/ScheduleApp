/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Appointment;
import Model.LoggedInUser;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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
public class AppointmentEditorController implements Initializable {
    
    @FXML private TextField titleTextField;
    @FXML private TextArea descriptionTextField;
    @FXML private ComboBox<String> customerComboBox;
    @FXML private TextField locationTextField;
    @FXML private TextField contactTextField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private TextField urlTextField;
    @FXML private DatePicker meetingDate;
    @FXML private ComboBox<String> startTimeComboBox;
    @FXML private ComboBox<String> endTimeComboBox;
    @FXML private Button backToScheduleViewButton;
    @FXML private Button saveAppointmentButton;
    
    String errors = new String();
    
    // Map to store customer names and their Database IDs
    Map<String,Long> customersMap;
    
    public AppointmentEditorController() {
        this.customersMap = new HashMap<>();
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // Populate customersMap with values from Database
        try {
            ResultSet customerResults = DatabaseConnection.getCustomers();
            while (customerResults.next())
            {
                String customerName = customerResults.getString("customerName");
                long customerId = customerResults.getLong("customerId");
                customersMap.put(customerName, customerId);
                populateForm();
            }
        } catch (SQLException ex) {
            System.out.println("SQLException in appointment editor initialize");
        }
    }
    
    @FXML
    private void showScheduleView() throws IOException {
        //get reference to the button's stage
        Stage stage=(Stage)backToScheduleViewButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/ScheduleView.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
    
    private void populateForm()
    {
        // Using a lambda expression here for mainly readability. I could have done a for loop
        // and added each customer but using a lambda expression was easier to implement and
        // easier to read in my opinion.
        ObservableList<String> customerNamesList = FXCollections.observableArrayList();
        customersMap.keySet().forEach((customerName) -> {
            customerNamesList.add(customerName);
        });
        customerComboBox.setItems(customerNamesList);
        
        // Types combo box
        ObservableList<String> typesList = FXCollections.observableArrayList();
        typesList.add("New Client Initial Meeting");
        typesList.add("Consultation");
        typesList.add("Follow Up Appointment");
        typesList.add("Closing Account");
        typeComboBox.setItems(typesList);
        
        // Start and end time combo boxes
        ObservableList<String> startStopTimes = FXCollections.observableArrayList();
        LocalTime time = LocalTime.of(8, 00);
        while (time.isBefore(LocalTime.of(17, 15)))
        {
            startStopTimes.add(time.format(SchedulerApp.TIME_FORMAT));
            time = time.plusMinutes(15);
        }
        
        startTimeComboBox.setItems(startStopTimes);
        endTimeComboBox.setItems(startStopTimes);
        
        // If we are editing an existing appointment
        if (SchedulerApp.getEditAppointmentMode())
        {
            // Pull out selected appointment data
            Appointment selectedAppointment = SchedulerApp.getSelectedAppointment();
            String selectedTitle = selectedAppointment.getTitle().getValue();
            String selectedDescription = selectedAppointment.getDescription().getValue();
            String selectedCustomer = selectedAppointment.getCustomerName().getValue();
            String selectedLocation = selectedAppointment.getLocation().getValue();
            String selectedContact = selectedAppointment.getContact().getValue();
            String selectedUrl = selectedAppointment.getUrl().getValue();
            String selectedType = selectedAppointment.getType().getValue();
            LocalDate selectedDate = LocalDate.parse(selectedAppointment.getStartDateFormatted().getValue(), SchedulerApp.LOCAL_DATE_FORMATTER);
            String selectedStartTime = selectedAppointment.getStartTimeFormatted().getValue();
            String selectedEndTime = selectedAppointment.getEndTimeFormatted().getValue();
            
            
            // Populate form with selected appointment data
            titleTextField.setText(selectedTitle);
            descriptionTextField.setText(selectedDescription);
            customerComboBox.setValue(selectedCustomer);
            locationTextField.setText(selectedLocation);
            contactTextField.setText(selectedContact);
            urlTextField.setText(selectedUrl);
            typeComboBox.setValue(selectedType);
            meetingDate.setValue(selectedDate);
            startTimeComboBox.setValue(selectedStartTime);
            endTimeComboBox.setValue(selectedEndTime);
        }
    }
    
    private boolean checkForErrors()
    {
        boolean errorsFound = false;
        errors = "";
        
        errors = "The following problems were found:";
        if (titleTextField.getText().isEmpty())
        {
            errors = errors.concat("\n - Title is empty");
            errorsFound = true;
        }
        if (customerComboBox.getSelectionModel().isEmpty())
        {
            errors = errors.concat("\n - Customer is not selected");
            errorsFound = true;
        }
        if (typeComboBox.getSelectionModel().isEmpty())
        {
            errors = errors.concat("\n - Type is not selected");
            errorsFound = true;
        }
        if (startTimeComboBox.getSelectionModel().isEmpty())
        {
            errors = errors.concat("\n - Start time is empty");
            errorsFound = true;
        }
        if (endTimeComboBox.getSelectionModel().isEmpty())
        {
            errors = errors.concat("\n - Stop time is empty");
            errorsFound = true;
        }
        if (meetingDate.getValue() == null)
        {
            errors = errors.concat("\n - Date is empty");
            errorsFound = true;
        }
        else
        {
            if (LocalDate.parse(meetingDate.getValue().toString()).isBefore(LocalDate.now()))
            {
                // Date entered is BEFORE today
                errors = errors.concat("\n - Date is earlier than current date");
                errorsFound = true;
            }
            if (meetingDate.getValue().getDayOfWeek().getValue() == 6 ||
                meetingDate.getValue().getDayOfWeek().getValue() == 7)
            {
                // Date entered is NOT a weekday
                errors = errors.concat("\n - Date is on a weekend");
                errorsFound = true;
            }
        }
        
        // Start time / end time combo boxes
        // Do not allow user to enter end time that is the same or before
        // the start time because appointments should be at least 15 minutes long.
        String startTime = startTimeComboBox.getValue();
        String endTime = endTimeComboBox.getValue() ;
        if (startTime != null && endTime != null)
        {
            LocalTime startDatetime = LocalTime.parse(startTime, SchedulerApp.LOCAL_TIME_FORMATTER);
            LocalTime endDatetime = LocalTime.parse(endTime, SchedulerApp.LOCAL_TIME_FORMATTER);
            if (endDatetime.isBefore(startDatetime.plusMinutes(15)))
            {
                errors = errors.concat("\n - End time is not later than Start time");
            }
        }
        
        return errorsFound;
    }
    
    @FXML
    private void saveAppointment()
    {
        boolean errorsFound = checkForErrors();
        
        if (errorsFound)
        {
            DialogHelper dialog = new DialogHelper();
            dialog.openDialog(Alert.AlertType.ERROR, errors);
        }
        else
        {
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            Long customerId = customersMap.get(customerComboBox.getSelectionModel().getSelectedItem());
            String location = locationTextField.getText();
            String contact = contactTextField.getText();
            String type = typeComboBox.getValue();
            String url = urlTextField.getText();
            
            LocalDate date = LocalDate.parse(meetingDate.getValue().toString());
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
            LocalTime startTime = LocalTime.parse(startTimeComboBox.getSelectionModel().getSelectedItem(), formatter);
            LocalTime endTime = LocalTime.parse(endTimeComboBox.getSelectionModel().getSelectedItem(), formatter);
            
            LocalDateTime startTimeUtc = Utilities.generateZonedDateTime(date, startTime);
            LocalDateTime endTimeUtc = Utilities.generateZonedDateTime(date, endTime);
            
            LocalDateTime timestamp = Utilities.generateTimestamp();
            
            String overlappingAppointments = new String();
            
            try {
                overlappingAppointments = DatabaseConnection.checkOverlappingAppointments(startTimeUtc, endTimeUtc);
            } catch (SQLException ex) {
                System.out.println("SQLException with overlapping appointments: " + ex.getMessage());
            }
            
            // If appointment will overlap existing appointments, do not create it or save it
            if (!overlappingAppointments.equals(""))
            {
                DialogHelper dialog = new DialogHelper();
                dialog.openDialog(Alert.AlertType.ERROR, "Appointment cannot overlap with existing appointments:\n" + overlappingAppointments);
            }
            else
            {
                // Database Update
                if (SchedulerApp.getEditAppointmentMode())
                {
                    int appointmentId = SchedulerApp.getSelectedAppointment().getAppointmentId().getValue();

                    DatabaseQuery addressDbQuery = new DatabaseQuery(
                        "UPDATE appointment SET "
                        + "customerId=" + customerId + ", "
                        + "title=\"" + title + "\", "
                        + "description=\"" + description + "\", "
                        + "type=\"" + type + "\", "
                        + "location=\"" + location + "\", "
                        + "contact=\"" + contact + "\", "
                        + "url=\"" + url + "\", "
                        + "start=\"" + startTimeUtc + "\", "
                        + "end=\"" + endTimeUtc + "\", "
                        + "lastUpdate=\"" + timestamp + "\", "
                        + "lastUpdateBy=\"" + LoggedInUser.USERNAME + "\" "
                        + "WHERE appointmentId=" + appointmentId + ";"
                    );
                    try {
                        addressDbQuery.executeUpdate();
                    } catch (SQLException ex) {
                        System.out.println("Exception occurred updating appointment: " + ex.getMessage());
                    }
                }
                else
                {
                    // Add to appointment table
                    DatabaseQuery addressDbQuery = new DatabaseQuery(
                        "INSERT INTO appointment (customerId, title, description, type, location, contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy, userId) "
                        + "VALUES ("
                        + customerId + ", "
                        + "\"" + title + "\", "
                        + "\"" + description + "\", "
                        + "\"" + type + "\", "
                        + "\"" + location + "\", "
                        + "\"" + contact + "\", "
                        + "\"" + url + "\", "
                        + "\"" + startTimeUtc + "\", "
                        + "\"" + endTimeUtc + "\", "
                        + "\"" + timestamp + "\", "
                        + "\"" + LoggedInUser.USERNAME + "\", "
                        + "\"" + timestamp + "\", "
                        + "\"" + LoggedInUser.USERNAME + "\", "
                        + LoggedInUser.USER_ID + ");"      
                    );
                    try {
                        addressDbQuery.executeInsert();
                    } catch (SQLException ex) {
                        System.out.println("Exception occurred adding appointment: " + ex.getMessage());
                    }
                }
                try {
                    showScheduleView();
                } catch (IOException ex) {
                        System.out.println("Exception occurred showing schedule view: " + ex.getMessage());
                }
            }
        }
    }
}
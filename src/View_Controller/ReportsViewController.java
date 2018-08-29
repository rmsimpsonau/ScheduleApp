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
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import schedulerapp.ChangeSceneHelper;
import schedulerapp.DatabaseQuery;
import schedulerapp.SchedulerApp;
import schedulerapp.Utilities;

/**
 * FXML Controller class
 *
 * @author sim59419
 */
public class ReportsViewController implements Initializable {

    @FXML private Button consultantSchedulesButton;
    @FXML private Button appointmentsByMonthButton;
    @FXML private Button appointmentsByCustomerButton;
    @FXML private TextArea resultTextArea;
    
    public enum Report {
        CONSULTANT_SCHEDULES,
        APPOINTMENTS_BY_MONTH,
        APPOINTMENTS_BY_CUSTOMER
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Lamba Expression used so that I didn't have to write 3 different functions
        // that call the generate function with different parameters or different code
        consultantSchedulesButton.setOnAction((event) -> {
            try {
                generateReport(Report.CONSULTANT_SCHEDULES);
            } catch (SQLException ex) {
                System.out.println("Exception generating consultant report: " + ex.getMessage());
            }
        });
        appointmentsByMonthButton.setOnAction((event) -> {
            try {
                generateReport(Report.APPOINTMENTS_BY_MONTH);
            } catch (SQLException ex) {
                System.out.println("Exception generating appointments by month report: " + ex.getMessage());
            }
        });
        appointmentsByCustomerButton.setOnAction((event) -> {
            try {
                generateReport(Report.APPOINTMENTS_BY_CUSTOMER);
            } catch (SQLException ex) {
                System.out.println("Exception generating appointments by customer report: " + ex.getMessage());
            }
        });
    }
    
    @FXML
    public void backToScheduleView() throws IOException
    {
        //get reference to the button's stage
        Stage stage=(Stage)consultantSchedulesButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/ScheduleView.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
    
    @FXML
    public void generateReport(Report reportName) throws SQLException
    {
        String reportString = "";
        String reportTitle = "";
        switch (reportName)
        {
            case CONSULTANT_SCHEDULES:
                reportTitle = "Consultant Schedules";
                reportString = generateConsultantSchedulesReport();
                break;
            case APPOINTMENTS_BY_MONTH:
                reportTitle = "Appointment Types By Month";
                reportString = generateAppointmentTypesByMonthReport();
                break;
            case APPOINTMENTS_BY_CUSTOMER:
                reportTitle = "Appointments By Customer";
                reportString = generateAppointmentByCustomerReport();
                break;
            default:
                // Exception Assert statement
                assert false: "Invalid report type";
        }
        
        if (reportString != null)
        {
            reportTitle = reportTitle.concat("\n-------------------------------------------------------------------\n");
            resultTextArea.setText(reportTitle + reportString);
        }
        else
        {
            resultTextArea.setText("No data to display");
        }
    }
    
    private String generateConsultantSchedulesReport() throws SQLException
    {
        String reportString = "";
        LocalDateTime timestamp = Utilities.generateTimestamp();
        
        DatabaseQuery usersDbQuery = new DatabaseQuery("SELECT * FROM user;");
        try {
            usersDbQuery.execute();
        } catch (SQLException ex) {
            System.out.println("Exception occurred getting all users: " + ex.getMessage());
        }
        ResultSet usersResults = usersDbQuery.getResults();
        while (usersResults.next())
        {
            int userId = usersResults.getInt("userId");
            String userName = usersResults.getString("userName");
            
            reportString = reportString.concat(userName.toUpperCase() + "'S Appointments:");
            
            DatabaseQuery appointmentsDbQuery = new DatabaseQuery("SELECT * FROM appointment INNER JOIN customer ON appointment.customerId=customer.customerId WHERE userId = " + userId + " AND appointment.start > '" + timestamp + "';");
            try {
                appointmentsDbQuery.execute();
            } catch (SQLException ex) {
                System.out.println("Exception occurred getting appointments and user info: " + ex.getMessage());
            }
            ResultSet appointmentsResults = appointmentsDbQuery.getResults();
            while (appointmentsResults.next())
            {
                reportString = reportString.concat("\n-------------------------------------------------------------------");
                reportString = reportString.concat("\n  ID: " + appointmentsResults.getString("appointment.appointmentId"));
                reportString = reportString.concat("\n  Title: " + appointmentsResults.getString("appointment.title"));
                reportString = reportString.concat("\n  Description: " + appointmentsResults.getString("appointment.description"));
                reportString = reportString.concat("\n  Type: " + appointmentsResults.getString("appointment.type"));
                reportString = reportString.concat("\n  Customer: " + appointmentsResults.getString("customer.customerName"));
                reportString = reportString.concat("\n  Location: " + appointmentsResults.getString("appointment.location"));
                reportString = reportString.concat("\n  Contact: " + appointmentsResults.getString("appointment.contact"));
                reportString = reportString.concat("\n  URL: " + appointmentsResults.getString("appointment.url"));
                reportString = reportString.concat("\n  Start: " + Utilities.convertFromUtcToLocalFormatted(
                        LocalDateTime.parse(appointmentsResults.getString("appointment.start"), SchedulerApp.LOCAL_DATE_TIME_MILLISECONDS_FORMATTER))
                );
                reportString = reportString.concat("\n  End: " + Utilities.convertFromUtcToLocalFormatted(
                        LocalDateTime.parse(appointmentsResults.getString("appointment.end"), SchedulerApp.LOCAL_DATE_TIME_MILLISECONDS_FORMATTER))
                );
            }
            reportString = reportString.concat("\n-------------------------------------------------------------------\n\n\n");
        }
        
        return reportString;
    }
    
    private String generateAppointmentTypesByMonthReport() throws SQLException
    {
        StringBuilder reportString = new StringBuilder();
        LocalDateTime timestamp = Utilities.generateTimestamp();
        Map<Integer, ArrayList> typesByMonth = new HashMap<>();
        
        DatabaseQuery appointmentsDbQuery = new DatabaseQuery(
                "SELECT MONTH(start), type, COUNT(*) FROM appointment WHERE start > '" + timestamp + "' GROUP BY type, MONTH(start);"
        );
        try {
            appointmentsDbQuery.execute();
        } catch (SQLException ex) {
            System.out.println("Exception occurred getting appointments for report: " + ex.getMessage());
        }
        ResultSet results = appointmentsDbQuery.getResults();
        while (results.next())
        {
            String type = results.getString("appointment.type");
            int month = results.getInt("MONTH(start)");
            String count = results.getString("COUNT(*)");
            ArrayList types = new ArrayList();
            
            if (typesByMonth.containsKey(month))
            {
                types = typesByMonth.get(month);
            }
            types.add(type + " - " + count);
            typesByMonth.put(month, types);
        }
        typesByMonth.keySet().forEach((month) -> {
            String monthString = Month.of(month).name();
            reportString.append("\n\n-------------------------------------------------------------------");
            reportString.append("\n ").append(monthString);
            reportString.append("\n-------------------------------------------------------------------");
            
            ArrayList types = typesByMonth.get(month);
            for (int i = 0; i < types.size(); i++)
            {
                reportString.append("\n  ").append(types.get(i));
            }
            reportString.append("\n");
        });
        
        return reportString.toString();
    }
    
    private String generateAppointmentByCustomerReport() throws SQLException
    {
        StringBuilder reportString = new StringBuilder();
        LocalDateTime timestamp = Utilities.generateTimestamp();
        Map<String, Integer> appointmentsbyCustomer = new HashMap<>();
        
        // Appointments Database Query
        DatabaseQuery appointmentsDbQuery = new DatabaseQuery(
            "SELECT COUNT(*), customer.customerName, appointment.customerId FROM appointment "
            + "INNER JOIN customer ON customer.customerId=appointment.customerId "
            + "WHERE start > '" + timestamp + "' GROUP BY appointment.customerId;"
        );
        
        // Appointments Database Query
        DatabaseQuery customersDbQuery = new DatabaseQuery(
            "SELECT * FROM customer;"
        );
        
        try {
            appointmentsDbQuery.execute();
            customersDbQuery.execute();
        } catch (SQLException ex) {
            System.out.println("Exception occurred getting customer and appointments for report: " + ex.getMessage());
        }
       
        Map<Integer, String> customerNames = new HashMap<>();
        ResultSet customersResults = customersDbQuery.getResults();
        while (customersResults.next())
        {
            // Create map of customer IDs and customer names
            int customerIdKey = customersResults.getInt("customer.customerId");
            String customerName = customersResults.getString("customer.customerName");
            
            customerNames.put(customerIdKey, customerName);
        }
        
        ResultSet appointmentsResults = appointmentsDbQuery.getResults();
        while (appointmentsResults.next())
        {
            int customerId = appointmentsResults.getInt("appointment.customerId");
            int count = appointmentsResults.getInt("COUNT(*)");
            String customerNameString = customerNames.get(customerId);
            appointmentsbyCustomer.put(customerNameString, count);
        }
        appointmentsbyCustomer.keySet().forEach((customerNameString) -> {
            int numAppointments = appointmentsbyCustomer.get(customerNameString);
            reportString.append("\n ").append(customerNameString).append(" - ").append(numAppointments);
        });
        
        return reportString.toString();
    }
    
}

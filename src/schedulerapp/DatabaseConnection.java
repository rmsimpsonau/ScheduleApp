/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulerapp;

import Model.Appointment;
import Model.LoggedInUser;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author sim59419
 */
public class DatabaseConnection {
    
    public enum SCHEDULE_VIEW_DATE_TYPE {
        WEEK {
            @Override
            public String toString() {
              return "WEEK";
            }
        },
        MONTH {
            @Override
            public String toString() {
              return "MONTH";
            }
        }
    }

    private static Connection conn;

    /**
     *
     * @throws java.lang.ClassNotFoundException
     */
    public static void connect() throws ClassNotFoundException {

        String driver = "com.mysql.jdbc.Driver";
        String db = "U04MTJ";
        String url = "jdbc:mysql://52.206.157.109:3306/U04MTJ";
        String user = "U04MTJ";
        String pass = "53688282140";

        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, pass);
            System.out.println("Connected to database: " + db);
        } catch (SQLException e) {
            printSqlException(e);
        }
    }

    public static Connection getConnection() {
        return conn;
    }

    public static void closeConnection() throws SQLException {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("ERROR closing connection to database: " + e.getMessage());
        }
    }

    public static ResultSet getCities() {
        DatabaseQuery query = new DatabaseQuery(
                "SELECT cityId, city "
                + "FROM city "
                + "ORDER BY city;"
        );
        try {
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SQLException getting cities: " + ex.getMessage());
        }
        ResultSet results = query.getResults();
        return results;
    }

    public static ResultSet getCountries() {
        DatabaseQuery query = new DatabaseQuery(
                "SELECT countryId, country "
                + "FROM country "
                + "ORDER BY country;"
        );
        try {
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SQLException getting countries: " + ex.getMessage());
        }
        ResultSet results = query.getResults();
        return results;
    }

    public static ResultSet getCustomers() {
        DatabaseQuery query = new DatabaseQuery(
                "SELECT customerId, customerName "
                + "FROM customer "
                + "ORDER BY customerName;"
        );
        try {
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SQLException getting customer: " + ex.getMessage());
        }
        ResultSet results = query.getResults();
        return results;
    }

    public static long addCityIfMissing(String cityName, long countryIdKey) throws SQLException {
        long cityIdKey = 0;

        // Check if city already exists
        DatabaseQuery cityExists = new DatabaseQuery(
                "SELECT city, cityId "
                + "FROM city "
                + "WHERE city=\"" + cityName + "\";"
        );
        try {
            cityExists.execute();
        } catch (SQLException ex) {
            System.out.println("Exception occurred getting city exists: " + ex.getMessage());
        }

        ResultSet results = cityExists.getResults();
        boolean cityAlreadyExists = false;

        while (results.next()) {
            if (results.getString("city").equalsIgnoreCase(cityName)) {
                cityAlreadyExists = true;
                cityIdKey = results.getLong("cityId");
            }
        }

        if (!cityAlreadyExists) {
            LocalDateTime timestamp = Utilities.generateTimestamp();
            // Add to city table
            DatabaseQuery cityDbQuery = new DatabaseQuery(
                    "INSERT INTO city (city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES ("
                    + "\"" + cityName + "\", "
                    + countryIdKey + ", "
                    + "\"" + timestamp + "\", "
                    + "\"" + LoggedInUser.USERNAME + "\", "
                    + "\"" + timestamp + "\", "
                    + "\"" + LoggedInUser.USERNAME + "\");"
            );
            try {
                cityDbQuery.executeInsert();
                cityIdKey = cityDbQuery.getGeneratedKey();
            } catch (SQLException ex) {
                System.out.println("Exception occurred inserting city: " + ex.getMessage());
            }
        }
        return cityIdKey;
    }
    
    public static String getCustomerAppointments(int customerIdRequested) throws SQLException {
        String appointmentsList = "";

        DatabaseQuery query = new DatabaseQuery(
                "SELECT * FROM appointment "
                + "WHERE customerId = " + customerIdRequested + ";"
        );
        try {
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SqlException getting customer appointment: " + ex.getMessage());
        }

        ResultSet results = query.getResults();

        while (results.next()) {
            // Appointment Object Creation
            String title = results.getString("appointment.title");
            String startTime = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.start").toLocalDateTime()).toString();
            String endTime = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.end").toLocalDateTime()).toString();
            
            appointmentsList = appointmentsList.concat("\n\n " + title + "\n   " + startTime + " - " + endTime);
        }
        return appointmentsList;
    }

    public static ObservableList<Appointment> getUserNextAppointments(int userIdRequested) throws SQLException {
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
        LocalDateTime timeNow = Utilities.generateTimestamp();

        DatabaseQuery query = new DatabaseQuery(
                "SELECT appointment.appointmentId, "
                + "appointment.title, "
                + "appointment.description, "
                + "appointment.location, "
                + "appointment.contact, "
                + "appointment.url, "
                + "appointment.start, "
                + "appointment.end, "
                + "appointment.type, "
                + "appointment.createDate, "
                + "appointment.createdBy, "
                + "appointment.lastUpdate, "
                + "appointment.lastUpdateBy, "
                + "appointment.userId, "
                + "customer.customerId, "
                + "customer.customerName "
                + "FROM appointment "
                + "INNER JOIN customer ON appointment.customerId=customer.customerId "
                + "WHERE appointment.userId=" + userIdRequested + " "
                + "AND (appointment.start BETWEEN '" + timeNow.toString() + "' "
                + "AND '" + timeNow.plusMinutes(15).toString() + "');"
        );
        try {
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SqlException getting next user appointment: " + ex.getMessage());
        }

        ResultSet results = query.getResults();

        while (results.next()) {
            // Appointment Object Creation
            IntegerProperty appointmentId = new SimpleIntegerProperty(results.getInt("appointment.appointmentId"));
            IntegerProperty customerId = new SimpleIntegerProperty(results.getInt("customer.customerId"));
            StringProperty customerName = new SimpleStringProperty(results.getString("customer.customerName"));
            IntegerProperty userId = new SimpleIntegerProperty(results.getInt("appointment.userId"));
            StringProperty title = new SimpleStringProperty(results.getString("appointment.title"));
            StringProperty description = new SimpleStringProperty(results.getString("appointment.description"));
            StringProperty location = new SimpleStringProperty(results.getString("appointment.location"));
            StringProperty contact = new SimpleStringProperty(results.getString("appointment.contact"));
            StringProperty type = new SimpleStringProperty(results.getString("appointment.type"));
            StringProperty url = new SimpleStringProperty(results.getString("appointment.url"));
            StringProperty start = new SimpleStringProperty(Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.start").toLocalDateTime()).toString());
            StringProperty end = new SimpleStringProperty(Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.end").toLocalDateTime()).toString());
            LocalDateTime createDate = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.createDate").toLocalDateTime());
            StringProperty createdBy = new SimpleStringProperty(results.getString("appointment.createdBy"));
            LocalDateTime lastUpdate = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.lastUpdate").toLocalDateTime());
            StringProperty lastUpdateBy = new SimpleStringProperty(results.getString("lastUpdateBy"));

            Appointment appointmentObj = new Appointment(
                    appointmentId,
                    customerId,
                    customerName,
                    userId,
                    title,
                    description,
                    location,
                    contact,
                    type,
                    url,
                    start,
                    end,
                    createDate,
                    createdBy,
                    lastUpdate,
                    lastUpdateBy
            );
            appointmentsList.add(appointmentObj);
        }
        return appointmentsList;
    }
    
    public static ObservableList<Appointment> getAppointmentsByDateSelected(int userIdRequested, LocalDate dateSelected, SCHEDULE_VIEW_DATE_TYPE dateType) throws SQLException
    {
        
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();

        DatabaseQuery query = new DatabaseQuery(
                "SELECT appointment.appointmentId, "
                + "appointment.title, "
                + "appointment.description, "
                + "appointment.location, "
                + "appointment.contact, "
                + "appointment.url, "
                + "appointment.start, "
                + "appointment.end, "
                + "appointment.type, "
                + "appointment.createDate, "
                + "appointment.createdBy, "
                + "appointment.lastUpdate, "
                + "appointment.lastUpdateBy, "
                + "appointment.userId, "
                + "customer.customerId, "
                + "customer.customerName "
                + "FROM appointment "
                + "INNER JOIN customer ON appointment.customerId=customer.customerId "
                + "WHERE appointment.userId=" + userIdRequested + " "
                + "AND " + dateType + "(appointment.start) = " + dateType.toString() + "('" + dateSelected + "');"
        );
        try {
            query.printQuery();
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SqlException getting customer appointments: " + ex.getMessage());
        }

        ResultSet results = query.getResults();

        while (results.next()) {
            // Appointment Object Creation
            IntegerProperty appointmentId = new SimpleIntegerProperty(results.getInt("appointment.appointmentId"));
            IntegerProperty customerId = new SimpleIntegerProperty(results.getInt("customer.customerId"));
            StringProperty customerName = new SimpleStringProperty(results.getString("customer.customerName"));
            IntegerProperty userId = new SimpleIntegerProperty(results.getInt("appointment.userId"));
            StringProperty title = new SimpleStringProperty(results.getString("appointment.title"));
            StringProperty description = new SimpleStringProperty(results.getString("appointment.description"));
            StringProperty location = new SimpleStringProperty(results.getString("appointment.location"));
            StringProperty contact = new SimpleStringProperty(results.getString("appointment.contact"));
            StringProperty type = new SimpleStringProperty(results.getString("appointment.type"));
            StringProperty url = new SimpleStringProperty(results.getString("appointment.url"));
            StringProperty start = new SimpleStringProperty(Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.start").toLocalDateTime()).toString());
            StringProperty end = new SimpleStringProperty(Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.end").toLocalDateTime()).toString());
            LocalDateTime createDate = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.createDate").toLocalDateTime());
            StringProperty createdBy = new SimpleStringProperty(results.getString("appointment.createdBy"));
            LocalDateTime lastUpdate = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.lastUpdate").toLocalDateTime());
            StringProperty lastUpdateBy = new SimpleStringProperty(results.getString("lastUpdateBy"));

            Appointment appointmentObj = new Appointment(
                    appointmentId,
                    customerId,
                    customerName,
                    userId,
                    title,
                    description,
                    location,
                    contact,
                    type,
                    url,
                    start,
                    end,
                    createDate,
                    createdBy,
                    lastUpdate,
                    lastUpdateBy
            );
            appointmentsList.add(appointmentObj);
        }
        return appointmentsList;
    }
    
    public static ObservableList<Appointment> getUserAppointments(int userIdRequested, LocalDateTime beforeDate) throws SQLException {
        ObservableList<Appointment> appointmentsList = FXCollections.observableArrayList();
        LocalDateTime timeNow = Utilities.generateTimestamp();

        DatabaseQuery query = new DatabaseQuery(
                "SELECT appointment.appointmentId, "
                + "appointment.title, "
                + "appointment.description, "
                + "appointment.location, "
                + "appointment.contact, "
                + "appointment.url, "
                + "appointment.start, "
                + "appointment.end, "
                + "appointment.type, "
                + "appointment.createDate, "
                + "appointment.createdBy, "
                + "appointment.lastUpdate, "
                + "appointment.lastUpdateBy, "
                + "appointment.userId, "
                + "customer.customerId, "
                + "customer.customerName "
                + "FROM appointment "
                + "INNER JOIN customer ON appointment.customerId=customer.customerId "
                + "WHERE appointment.userId=" + userIdRequested + " "
                + "AND appointment.end < '" + beforeDate + "' "
                + "AND appointment.end > '" + timeNow.toString() + "';"
        );
        try {
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SqlException getting customer appointments: " + ex.getMessage());
        }

        ResultSet results = query.getResults();

        while (results.next()) {
            // Appointment Object Creation
            IntegerProperty appointmentId = new SimpleIntegerProperty(results.getInt("appointment.appointmentId"));
            IntegerProperty customerId = new SimpleIntegerProperty(results.getInt("customer.customerId"));
            StringProperty customerName = new SimpleStringProperty(results.getString("customer.customerName"));
            IntegerProperty userId = new SimpleIntegerProperty(results.getInt("appointment.userId"));
            StringProperty title = new SimpleStringProperty(results.getString("appointment.title"));
            StringProperty description = new SimpleStringProperty(results.getString("appointment.description"));
            StringProperty location = new SimpleStringProperty(results.getString("appointment.location"));
            StringProperty contact = new SimpleStringProperty(results.getString("appointment.contact"));
            StringProperty type = new SimpleStringProperty(results.getString("appointment.type"));
            StringProperty url = new SimpleStringProperty(results.getString("appointment.url"));
            StringProperty start = new SimpleStringProperty(Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.start").toLocalDateTime()).toString());
            StringProperty end = new SimpleStringProperty(Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.end").toLocalDateTime()).toString());
            LocalDateTime createDate = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.createDate").toLocalDateTime());
            StringProperty createdBy = new SimpleStringProperty(results.getString("appointment.createdBy"));
            LocalDateTime lastUpdate = Utilities.convertFromUtcToLocal(results.getTimestamp("appointment.lastUpdate").toLocalDateTime());
            StringProperty lastUpdateBy = new SimpleStringProperty(results.getString("lastUpdateBy"));

            Appointment appointmentObj = new Appointment(
                    appointmentId,
                    customerId,
                    customerName,
                    userId,
                    title,
                    description,
                    location,
                    contact,
                    type,
                    url,
                    start,
                    end,
                    createDate,
                    createdBy,
                    lastUpdate,
                    lastUpdateBy
            );
            appointmentsList.add(appointmentObj);
        }
        return appointmentsList;
    }

    public static String checkOverlappingAppointments(LocalDateTime start, LocalDateTime end) throws SQLException {
        String overlappingAppointments = "";
        String dbQueryString = "SELECT * FROM appointment "
                + "WHERE userId = " + LoggedInUser.USER_ID + " "
                + "AND (start BETWEEN '" + start.plusMinutes(15) + "' AND '" + end.minusMinutes(15) + "' "
                + "OR '" + start.plusMinutes(15) + "' BETWEEN start AND end)";

        if (SchedulerApp.getEditAppointmentMode()) {
            int appointmentId = SchedulerApp.getSelectedAppointment().getAppointmentId().getValue();
            dbQueryString = dbQueryString.concat(" AND appointmentId != " + appointmentId);
        }

        dbQueryString = dbQueryString.concat(";");

        DatabaseQuery query = new DatabaseQuery(dbQueryString);
        try {
            query.execute();
        } catch (SQLException ex) {
            System.out.println("SqlException checking for overlapping appointments: " + ex.getMessage());
        }

        ResultSet results = query.getResults();

        while (results.next()) {
            String appointmentTitle = results.getString("title");
            String appointmentStart = Utilities.convertFromUtcToLocalFormatted(LocalDateTime.parse(results.getString("start"), SchedulerApp.LOCAL_DATE_TIME_MILLISECONDS_FORMATTER));
            String appointmentEnd = Utilities.convertFromUtcToLocalFormattedTime(LocalDateTime.parse(results.getString("end"), SchedulerApp.LOCAL_DATE_TIME_MILLISECONDS_FORMATTER));
            overlappingAppointments = overlappingAppointments.concat("\n" + appointmentTitle + "\n  " + appointmentStart + " - " + appointmentEnd + "\n");
        }
        return overlappingAppointments;
    }

    /* Private Methods */
    private static void printSqlException(SQLException e) {
        System.out.println("SQLException: " + e.getMessage());
        System.out.println("SQLState: " + e.getSQLState());
        System.out.println("VendorError: " + e.getErrorCode());
    }
}

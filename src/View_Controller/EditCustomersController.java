/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Address;
import Model.City;
import Model.Customer;
import Model.Country;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import schedulerapp.ChangeSceneHelper;
import schedulerapp.DatabaseConnection;
import schedulerapp.DatabaseQuery;

/**
 * FXML Controller class
 *
 * @author sim59419
 */
public class EditCustomersController implements Initializable {

    
    @FXML private TableView<Customer> customersTable;
    @FXML private TableColumn<Customer, Integer> customerIdCol;
    @FXML private TableColumn<Customer, String> customerNameCol;
    @FXML private TableColumn<Customer, String> addressCol;
    @FXML private TableColumn<Customer, String> addressTwoCol;
    @FXML private TableColumn<Customer, String> cityCol;
    @FXML private TableColumn<Customer, String> postalCodeCol;
    @FXML private TableColumn<Customer, String> countryCol;
    @FXML private TableColumn<Customer, String> phoneCol;
    @FXML private Button addUserButton;
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;
    @FXML private Button backToScheduleViewButton;
    
    public static Customer selectedCustomer;
        
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            populateCustomersTable();
        } catch (SQLException ex) {
            System.out.println("Exception while populating customers table: " + ex.getMessage());
        }
    }
    
    private void populateCustomersTable() throws SQLException {
        DatabaseQuery query = new DatabaseQuery(
                "SELECT customer.customerId, customer.customerName, customer.active, "
                + "address.addressId, address.address, address.address2, address.postalCode, address.phone, "
                + "city.cityId, city.city, "
                + "country.countryId, country.country "
                + "FROM customer, address, city, country "
                + "WHERE customer.addressId = address.addressId AND address.cityId = city.cityId AND city.countryId = country.countryId "
                + "ORDER BY customer.customerId;"
        );
        query.execute();
        
        ResultSet results = query.getResults();
        
        ObservableList<Customer> customersList = FXCollections.observableArrayList();
        
        while (results.next()) {
            
            // Customer Country
            IntegerProperty countryId = new SimpleIntegerProperty(results.getInt("country.countryId"));
            StringProperty countryName = new SimpleStringProperty(results.getString("country.country"));
            
            Country country = new Country(
                    countryId,
                    countryName
            );
            
            // Customer City
            IntegerProperty cityId = new SimpleIntegerProperty(results.getInt("city.cityId"));
            StringProperty cityName = new SimpleStringProperty(results.getString("city.city"));
            Country cityCountry = country;
            
            City city = new City(
                    cityId,
                    cityName,
                    cityCountry
            );
            
            // Customer Address
            IntegerProperty addressId = new SimpleIntegerProperty(results.getInt("address.addressId"));
            StringProperty addressOne = new SimpleStringProperty(results.getString("address.address"));
            StringProperty addressTwo = new SimpleStringProperty(results.getString("address.address2"));
            City addressCity = city;
            StringProperty postalCode = new  SimpleStringProperty(results.getString("address.postalCode"));
            StringProperty phone = new SimpleStringProperty(results.getString("address.phone"));
            
            Address address = new Address(
                    addressId,
                    addressOne,
                    addressTwo,
                    addressCity,
                    postalCode,
                    phone
            );
            
            // Customer
            IntegerProperty customerId = new SimpleIntegerProperty(results.getInt("customer.customerId"));
            StringProperty customerName = new SimpleStringProperty(results.getString("customer.customerName"));
            Address customerAddress = address;
            IntegerProperty active = new SimpleIntegerProperty(results.getInt("customer.active"));
            
            Customer newCustomer = new Customer(
                    customerId,
                    customerName,
                    customerAddress,
                    active
            );
            customersList.add(newCustomer);
        }
        // Populate table view
        customersTable.setItems(customersList);
        customerIdCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerId().asObject());
        customerNameCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerName());
        addressCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerAddress().getAddress());
        addressTwoCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerAddress().getAddressTwo());
        postalCodeCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerAddress().getPostalCode());
        cityCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerAddress().getAddressCity().getCityName());
        countryCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerAddress().getAddressCity().getCityCountry().getCountryName());
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().getCustomerAddress().getPhone());
        
    }
    
    private void updateCustomersTable() throws SQLException
    {
        populateCustomersTable();
    }
    
    public static Customer getSelectedCustomer()
    {
        return selectedCustomer;
    }
    
    public static void setSelectedCustomer(Customer customer)
    {
        selectedCustomer = customer;
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
    
    @FXML
    private void showUpdateCustomerView() throws IOException 
    {
        //get reference to the button's stage
        Stage stage=(Stage)backToScheduleViewButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/UpdateCustomerView.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
    
    @FXML
    private void addCustomer(ActionEvent event) throws IOException
    {
        showUpdateCustomerView();
    }
    
    @FXML
    private void editCustomer(ActionEvent event) throws IOException
    {
        setSelectedCustomer(customersTable.getSelectionModel().getSelectedItem());
        showUpdateCustomerView();
    }
    
    @FXML
    private void deleteCustomer(ActionEvent event) throws SQLException
    {
        setSelectedCustomer(customersTable.getSelectionModel().getSelectedItem());
        int customerId = selectedCustomer.getCustomerId().getValue();
        
        String appointmentsList = DatabaseConnection.getCustomerAppointments(customerId);
        if (appointmentsList.isEmpty())
        {
            removeCustomerPlusDependencies(customerId);
        }
        else
        {   
            boolean userConfirmed;
            DialogHelper dialog = new DialogHelper();
            userConfirmed = dialog.openConfirmationDialog(Alert.AlertType.WARNING, "The following appointments will be deleted when you delete this customer. Do you wish to continue deleting this customer?" + appointmentsList);
            if (userConfirmed) {
                removeCustomerPlusDependencies(customerId);
            }
        }
    }
    
    private void removeCustomerPlusDependencies(int customerId) throws SQLException
    {
        DatabaseQuery deleteCustomerQuery = new DatabaseQuery(
            // Delete customer
            "DELETE FROM customer "
            + "WHERE customerId = " + customerId + "; "
        );
        DatabaseQuery deleteAddressQuery = new DatabaseQuery (
            // Delete address
            "DELETE FROM address "
            + "WHERE addressId = " + selectedCustomer.getCustomerAddress().getAddressId().getValue() + ";"
        );
        DatabaseQuery deleteAppointmentQuery = new DatabaseQuery (
            // Delete appointments that rely on this customer
            "DELETE FROM appointment "
            + "WHERE customerId = " + customerId + ";"
        );
        
        try {   
            deleteCustomerQuery.executeUpdate();
            deleteAddressQuery.executeUpdate();
            deleteAppointmentQuery.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("SQLException deleting from tables: " + ex.getMessage());
        }
        updateCustomersTable();
    }
    
}
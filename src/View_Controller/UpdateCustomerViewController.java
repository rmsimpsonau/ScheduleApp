/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Model.Customer;
import Model.LoggedInUser;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import schedulerapp.ChangeSceneHelper;
import schedulerapp.DatabaseConnection;
import schedulerapp.DatabaseQuery;
import schedulerapp.Utilities;

/**
 * FXML Controller class
 *
 * @author sim59419
 */
public class UpdateCustomerViewController implements Initializable {

    
    @FXML private Label title;
    @FXML private TextField nameTextField;
    @FXML private TextField addressTextField;
    @FXML private TextField addressTwoTextField;
    @FXML private TextField cityTextField;
    @FXML private ComboBox<String> countryComboBox;
    @FXML private TextField postalCodeTextField;
    @FXML private TextField phoneTextField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private Customer selectedCustomer;
    private StringBuilder errorString;
    private boolean newUserMode = false;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectedCustomer = EditCustomersController.getSelectedCustomer();
        try {
            populateForm();
        } catch (SQLException ex) {
            System.out.println("Exception populating form: " + ex.getMessage());
        }
    }

    private void populateForm() throws SQLException
    {
        // Populate Countries Combo Box
        ResultSet countryResults = DatabaseConnection.getCountries();
        while (countryResults.next()) {
            countryComboBox.getItems().add(countryResults.getString("country"));
        }        
            
        if (selectedCustomer == null)
        {
            // New User - Set state to 
            title.setText("Add New User");
            newUserMode = true;
        }
        else
        {
            // Existing User
            title.setText("Update User");
            nameTextField.setText(selectedCustomer.getCustomerName().getValue());
            addressTextField.setText(selectedCustomer.getCustomerAddress().getAddress().getValue());
            addressTwoTextField.setText(selectedCustomer.getCustomerAddress().getAddressTwo().getValue());
            cityTextField.setText(selectedCustomer.getCustomerAddress().getAddressCity().getCityName().getValue());
            postalCodeTextField.setText(selectedCustomer.getCustomerAddress().getPostalCode().getValue());
            countryComboBox.setValue(selectedCustomer.getCustomerAddress().getAddressCity().getCityCountry().getCountryName().getValue());
            phoneTextField.setText(selectedCustomer.getCustomerAddress().getPhone().getValue());
            newUserMode = false;
        }
        
        
    }
    
    @FXML
    private void cancelCustomerUpdate(ActionEvent event) throws IOException
    {
        EditCustomersController.setSelectedCustomer(null);
        showEditCustomersView();
    }
    
    @FXML
    private void saveCustomerUpdate(ActionEvent event) throws IOException, SQLException
    {
        if (checkFormErrors())
        {
            DialogHelper dialog = new DialogHelper();
            dialog.openDialog(Alert.AlertType.ERROR, errorString.toString());
        }
        else
        {
            
            DatabaseQuery countryIdQuery = new DatabaseQuery(
                "SELECT country, countryId "
                + "FROM country "
                + "WHERE country = \"" + countryComboBox.getValue() + "\";"
            );
            
            try
            {
                countryIdQuery.execute();
            }
            catch (SQLException ex)
            {
                System.out.println("Exception occurred gettign countries: " + ex.getMessage());
            }

            ResultSet countryIdResults = countryIdQuery.getResults();
            long countryIdKey = 0;
            while (countryIdResults.next())
            {
                   countryIdKey = countryIdResults.getLong("countryId");
            }
            
            if (newUserMode)
            {
                LocalDateTime timestamp = Utilities.generateTimestamp();

                long addressIdKey = 0;
                long customerIdKey = 0;                
                long cityIdKey = DatabaseConnection.addCityIfMissing(cityTextField.getText(), countryIdKey);
                
                // Add to address table
                DatabaseQuery addressDbQuery = new DatabaseQuery(
                    "INSERT INTO address (address, address2, cityId, postalCode, phone, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES ("
                    + "\"" + addressTextField.getText() + "\", "
                    + "\"" + addressTwoTextField.getText() + "\", "
                    + cityIdKey + ", "
                    + "\"" + postalCodeTextField.getText() + "\", "
                    + "\"" + phoneTextField.getText() + "\", "
                    + "\"" + timestamp + "\", "
                    + "\"" + LoggedInUser.USERNAME + "\", "
                    + "\"" + timestamp + "\", "
                    + "\"" + LoggedInUser.USERNAME + "\");"
                );
                try {
                    addressDbQuery.executeInsert();
                    addressIdKey = addressDbQuery.getGeneratedKey();
                } catch (SQLException ex) {
                    System.out.println("Exception occurred getting addresses: " + ex.getMessage());
                }
                
                // Add to customer table
                DatabaseQuery customerDbQuery = new DatabaseQuery(
                    "INSERT INTO customer (customerName, addressId, active, createDate, createdBy, lastUpdate, lastUpdateBy) "
                    + "VALUES ("
                    + "\"" + nameTextField.getText() + "\", "
                    + addressIdKey + ", "
                    + 1 + ", "
                    + "\"" + timestamp + "\", "
                    + "\"" + LoggedInUser.USERNAME + "\", "
                    + "\"" + timestamp + "\", "
                    + "\"" + LoggedInUser.USERNAME + "\");"
                );
                try {
                    customerDbQuery.executeInsert();
                    customerIdKey = customerDbQuery.getGeneratedKey();
                } catch (SQLException ex) {
                    System.out.println("Exception occurred getting customers: " + ex.getMessage());
                }
            } else {
                
                long cityIdKey = DatabaseConnection.addCityIfMissing(cityTextField.getText(), countryIdKey);
                LocalDateTime timestamp = Utilities.generateTimestamp();
                
                DatabaseQuery dbQuery = new DatabaseQuery(
                    "UPDATE customer, address "
                    + "SET customer.customerName = \"" + nameTextField.getText() + "\", "
                    + "customer.lastUpdate = \"" + timestamp + "\", "
                    + "customer.lastUpdateBy = \"" + LoggedInUser.USERNAME + "\", "
                    + "address.address = \"" + addressTextField.getText() + "\", "
                    + "address.address2 = \"" + addressTwoTextField.getText() + "\", "
                    + "address.postalCode = \"" + postalCodeTextField.getText() + "\", "
                    + "address.phone = \"" + phoneTextField.getText() + "\", "
                    + "address.cityId = " + cityIdKey + ", "
                    + "address.lastUpdate = \"" + timestamp + "\", "
                    + "address.lastUpdateBy = \"" + LoggedInUser.USERNAME + "\" "
                    + "WHERE customer.customerId = " + selectedCustomer.getCustomerId().getValue() + " "
                    + "AND address.addressId = " + selectedCustomer.getCustomerAddress().getAddressId().getValue() + ";");
                try {
                        dbQuery.executeUpdate();
                } catch (SQLException ex) {
                    System.out.println("Exception occurred updating address table: " + ex.getMessage());
                }
            }
            EditCustomersController.setSelectedCustomer(null);
            showEditCustomersView();
        }
    }
    
    private boolean checkFormErrors()
    {
        boolean errorsFound = false;
        // Build up a string of errors
         errorString = new StringBuilder();
         
        if (nameTextField.getText().equals("")) errorString.append(" - " + "Customer name field is empty\n");
        if (addressTextField.getText().equals("")) errorString.append(" - " + "Address field is empty\n");
        if (cityTextField.getText().equals("")) errorString.append(" - " + "City field is empty\n");
        if (postalCodeTextField.getText().equals("")) errorString.append(" - " + "Postal code field is empty\n"); 
        if (countryComboBox.getValue().equals("")) errorString.append(" - " + "Country field is empty\n");
        if (phoneTextField.getText().equals("")) errorString.append(" - " + "Phone field is empty\n");
        
        // If error string is not empty, errors were found
        if (errorString.length() > 0)
        {
            errorString.insert(0, "The following errors were found"
                    + ":\n");
            errorsFound = true;
        }
        
        return errorsFound;
    }
    
    
    private void showEditCustomersView() throws IOException {
        //get reference to the button's stage
        Stage stage=(Stage)cancelButton.getScene().getWindow();
        //load up Add Parts FXML Doc
        Parent root = FXMLLoader.load(getClass().getResource("/View_Controller/EditCustomers.fxml"));
        // Call change scene helper
        ChangeSceneHelper.changeScene(stage, root);
    }
}

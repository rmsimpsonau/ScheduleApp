/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author sim59419
 */
public class Customer {
    
    private IntegerProperty customerId;
    private StringProperty customerName;
    private Address customerAddress;
    private IntegerProperty customerActive;
    
    public Customer(IntegerProperty customerId, StringProperty customerName, Address customerAddress, IntegerProperty customerActive)
    {
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerActive = customerActive;
    }
    
    // Customer ID
    public IntegerProperty getCustomerId()
    {
        return customerId;
    }
    
    public void setCustomerId(IntegerProperty newId)
    {
        customerId = newId;
    }
    
    // Customer Name
    public StringProperty getCustomerName()
    {
        return customerName;
    }
    
    public void setCustomerName(StringProperty newName)
    {
        customerName = newName;
    }
    
    // Customer Address
    public Address getCustomerAddress()
    {
        return customerAddress;
    }
    
    public void setCustomerAddress(Address newAddress)
    {
        customerAddress = newAddress;
    }
    
    // Customer Active
    public IntegerProperty getCustomerActive()
    {
        return customerActive;
    }
    
    public void setCustomerActive(IntegerProperty newCustomerActive)
    {
        customerActive = newCustomerActive;
    }
    
}

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
public class Address {
    
    private IntegerProperty addressId;
    private StringProperty address;
    private StringProperty addressTwo;
    private City addressCity;
    private StringProperty postalCode;
    private StringProperty phone;
    
    public Address(IntegerProperty addressId, StringProperty address, StringProperty addressTwo, City addressCity, StringProperty postalCode, StringProperty phone)
    {
        this.addressId = addressId;
        this.address = address;
        this.addressTwo = addressTwo;
        this.addressCity = addressCity;
        this.postalCode = postalCode;
        this.phone = phone;
    }
    
    // Address ID
    public IntegerProperty getAddressId()
    {
        return addressId;
    }
    
    public void setAddressId(IntegerProperty newAddressId)
    {
        addressId = newAddressId;
    }
    
    // Address
    public StringProperty getAddress()
    {
        return address;
    }
    
    public void setAddress(StringProperty newAddress)
    {
        address = newAddress;
    }
    
    // Address Two
    public StringProperty getAddressTwo()
    {
        return addressTwo;
    }
    
    public void setAddressTwo(StringProperty newAddressTwo)
    {
        addressTwo = newAddressTwo;
    }
    
    // Address City
    public City getAddressCity()
    {
        return addressCity;
    }
    
    public void setAddressCity(City newAddressCity)
    {
        addressCity = newAddressCity;
    }
    
    // Postal Code
    public StringProperty getPostalCode()
    {
        return postalCode;
    }
    
    public void setPostalCode(StringProperty newPostalCode)
    {
        postalCode = newPostalCode;
    }
    
    // Phone
    public StringProperty getPhone()
    {
        return phone;
    }
    
    public void setPhone(StringProperty newPhone)
    {
        phone = newPhone;
    }
}

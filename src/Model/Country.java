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
public class Country {
    
    private IntegerProperty countryId;
    private StringProperty countryName;
    
    public Country(IntegerProperty countryId, StringProperty countryName)
    {
        this.countryId = countryId;
        this.countryName = countryName;
    }
    
    // Country ID
    public IntegerProperty getCountryId()
    {
        return countryId;
    }
    
    public void setCountryId(IntegerProperty newCountryId)
    {
        countryId = newCountryId;
    }
    
    // Country Name
    public StringProperty getCountryName()
    {
        return countryName;
    }
    
    public void setCountryName(StringProperty newCountryName)
    {
        countryName = newCountryName;
    }
}

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
public class City {
    
    private IntegerProperty cityId;
    private StringProperty cityName;
    private Country cityCountry;
    
    public City(IntegerProperty cityId, StringProperty cityName, Country cityCountry)
    {
        this.cityId = cityId;
        this.cityName = cityName;
        this.cityCountry = cityCountry;
    }
    
    // City ID
    public IntegerProperty getCityId()
    {
        return cityId;
    }
    
    public void setCityId(IntegerProperty newCityId)
    {
        cityId = newCityId;
    }
    
    // City Name
    public StringProperty getCityName()
    {
        return cityName;
    }
    
    public void setCityName(StringProperty newCityName)
    {
        cityName = newCityName;
    }
    
    // City Country
    public Country getCityCountry()
    {
        return cityCountry;
    }
    
    public void setCityCountry(Country newCityCountry)
    {
        cityCountry = newCityCountry;
    }
}
    


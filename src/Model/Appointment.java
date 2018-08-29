/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.time.LocalDateTime;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import schedulerapp.SchedulerApp;

/**
 *
 * @author sim59419
 */
public class Appointment {
    
    private IntegerProperty appointmentId;
    private IntegerProperty customerId;
    private StringProperty customerName; 
    private IntegerProperty userId;
    private StringProperty title;
    private StringProperty description;
    private StringProperty location;
    private StringProperty contact;
    private StringProperty type;
    private StringProperty url;
    private StringProperty start;
    private StringProperty end;
    private LocalDateTime createDate;
    private StringProperty createdBy;
    private LocalDateTime lastUpdate;
    private StringProperty lastUpdateBy;
    
    public Appointment(
        IntegerProperty appointmentId,
        IntegerProperty customerId,
        StringProperty customerName,
        IntegerProperty userId,
        StringProperty title,
        StringProperty description,
        StringProperty location,
        StringProperty contact,
        StringProperty type,
        StringProperty url,
        StringProperty start,
        StringProperty end,
        LocalDateTime createDate,
        StringProperty createdBy,
        LocalDateTime lastUpdate,
        StringProperty lastUpdateBy
    )
    {
        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.type = type;
        this.url = url;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
    }
    
    // Appointment ID
    public IntegerProperty getAppointmentId()
    {
        return appointmentId;
    }
    
    public void setAppointmentId(IntegerProperty newId)
    {
        appointmentId = newId;
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
    
    // User ID
    public IntegerProperty getUserId()
    {
        return userId;
    }
    
    public void setUserId(IntegerProperty newId)
    {
        userId = newId;
    }
    
    // Title
    public StringProperty getTitle()
    {
        return title;
    }
    
    public void setTitle(StringProperty newTitle)
    {
        title = newTitle;
    }
    
    // Description
    public StringProperty getDescription()
    {
        return description;
    }
    
    public void setDescription(StringProperty newDescription)
    {
        description = newDescription;
    }
    
    // Location
    public StringProperty getLocation()
    {
        return location;
    }
    
    public void setLocation(StringProperty newLocation)
    {
        location = newLocation;
    }
    
    // Contact
    public StringProperty getContact()
    {
        return contact;
    }
    
    public void setContact(StringProperty newContact)
    {
        contact = newContact;
    }
    
    // Type
    public StringProperty getType()
    {
        return type;
    }
    
    public void setType(StringProperty newType)
    {
        type = newType;
    }
    
    // URL
    public StringProperty getUrl()
    {
        return url;
    }
    
    public void setUrl(StringProperty newUrl)
    {
        url = newUrl;
    }
    
    // Start
    public StringProperty getStart()
    {
        return start;
    }
    
    public StringProperty getStartTimeFormatted()
    {
        LocalDateTime startLocalDateTime = LocalDateTime.parse(start.getValue(), SchedulerApp.LOCAL_DATE_TIME_FORMATTER);
        String formattedTime = startLocalDateTime.format(SchedulerApp.LOCAL_TIME_FORMATTER);
        return new SimpleStringProperty(formattedTime);
    }
    
    public StringProperty getStartDateFormatted()
    {
        LocalDateTime startLocalDateTime = LocalDateTime.parse(start.getValue(), SchedulerApp.LOCAL_DATE_TIME_FORMATTER);
        String formattedDate = startLocalDateTime.format(SchedulerApp.LOCAL_DATE_FORMATTER);
        return new SimpleStringProperty(formattedDate);
    }
    
    public void setStart(StringProperty newStart)
    {
        start = newStart;
    }
    
    // End
    public StringProperty getEnd()
    {
        return end;
    }
    
    public void setEnd(StringProperty newEnd)
    {
        end = newEnd;
    }
    
    public StringProperty getEndTimeFormatted()
    {
        LocalDateTime endLocalDateTime = LocalDateTime.parse(end.getValue(), SchedulerApp.LOCAL_DATE_TIME_FORMATTER);
        String formattedTime = endLocalDateTime.format(SchedulerApp.LOCAL_TIME_FORMATTER);
        return new SimpleStringProperty(formattedTime);
    }
    
    // Create Date
    public LocalDateTime getCreateDate()
    {
        return createDate;
    }
    
    public void setCreateDate(LocalDateTime newCreateDate)
    {
        createDate = newCreateDate;
    }
    
    // Created By
    public StringProperty getCreatedBy()
    {
        return createdBy;
    }
    
    public void setCreatedBy(StringProperty newCreatedBy)
    {
        createdBy = newCreatedBy;
    }
    
    // Last Update
    public LocalDateTime getLastUpdate()
    {
        return lastUpdate;
    }
    
    public void setLastUpdate(LocalDateTime newLastUpdate)
    {
        lastUpdate = newLastUpdate;
    }
    
    // Last Updated By
    public StringProperty getLastUpdatedBy()
    {
        return lastUpdateBy;
    }
    
    public void setLastUpdatedBy(StringProperty newLastUpdateBy)
    {
        lastUpdateBy = newLastUpdateBy;
    }   
}
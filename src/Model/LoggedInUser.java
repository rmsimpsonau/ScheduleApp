/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.time.ZoneId;
import java.util.Locale;

/**
 *
 * @author sim59419
 */
public class LoggedInUser {

    public static int USER_ID;
    public static String USERNAME;
    public static String PASSWORD;
    public static int ACTIVE;
    public static String CREATED_BY;
    public static String CREATE_DATE;
    public static String LAST_UPDATE;
    public static String LAST_UPDATED_BY;
    public static boolean CHECKED_NEXT_APPOINTMENTS;
    public static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static final Locale DEFAULT_LOCALE = Locale.getDefault();
    
    // User ID
    public static void setUserId(int newUserId)
    {
        USER_ID = newUserId;
    }
    
    // Username
    public static void setUsername(String newUsername)
    {
        USERNAME = newUsername;
    }
    
    // Password
    public static void setPassword(String newPassword)
    {
        PASSWORD = newPassword;
    }
    
    // Active
    public static void setActive(int newActive)
    {
        ACTIVE = newActive;
    }
    
    // Created By
    public static void setCreatedBy(String createdBy)
    {
        CREATED_BY = createdBy;
    }
    
    // Create Date
    public static void setCreateDate(String newCreateDate)
    {
        CREATE_DATE = newCreateDate;
    }
    
    // Last Update
    public static void setLastUpdate(String newLastUpdate)
    {
        LAST_UPDATE = newLastUpdate;
    }
    
    // Last Update By
    public static void setLastUpdatedBy(String lastUpdatedBy)
    {
        LAST_UPDATED_BY = lastUpdatedBy;
    }
    
    // Checked Next Appointments to see if there are appointments
    // in the next 15 minutes
    public static void setCheckedNextAppointments(boolean newCheckedNextAppointments)
    {
        CHECKED_NEXT_APPOINTMENTS = newCheckedNextAppointments;
    }
}

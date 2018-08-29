/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulerapp;

import Model.LoggedInUser;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 *
 * @author sim59419
 */
public class Utilities {
    
    
    public static LocalDateTime generateTimestamp()
    {
        return LocalDateTime.from(ZonedDateTime.now(LoggedInUser.ZONE_ID).withZoneSameInstant(ZoneId.of("UTC")));
    }
    
     public static LocalDateTime generateZonedDateTime(LocalDate date, LocalTime time)
    {
        return LocalDateTime.from(ZonedDateTime.of(date, time, LoggedInUser.ZONE_ID).withZoneSameInstant(ZoneId.of("UTC")));
    }
     
    public static LocalDateTime convertFromUtcToLocal(LocalDateTime utcDateTime)
    {
        return LocalDateTime.from(ZonedDateTime.of(utcDateTime, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(LoggedInUser.ZONE_ID.toString())));
    }

    public static String convertFromUtcToLocalFormatted(LocalDateTime utcDateTime)
    {
        LocalDateTime convertedUtcDateTime = LocalDateTime.from(ZonedDateTime.of(utcDateTime, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(LoggedInUser.ZONE_ID.toString())));
        return convertedUtcDateTime.format(SchedulerApp.LOCAL_DATE_TIME_AMPM_FORMATTER);
    }
    
    public static String convertFromUtcToLocalFormattedTime(LocalDateTime utcDateTime)
    {
        LocalDateTime convertedUtcDateTime = LocalDateTime.from(ZonedDateTime.of(utcDateTime, ZoneId.of("UTC")).withZoneSameInstant(ZoneId.of(LoggedInUser.ZONE_ID.toString())));
        return convertedUtcDateTime.format(SchedulerApp.LOCAL_TIME_FORMATTER);
    }
}

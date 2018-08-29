/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulerapp;

import Model.LoggedInUser;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;

/**
 *
 * @author sim59419
 */
public class LogWriter {
    
    private static final String LOG_FILE = "loginLog.txt";
    private static final String LOG_PATH = FileSystems.getDefault().getPath("").toAbsolutePath().toString() + "/src/schedulerapp/";
    
    public static void logUserLogin() throws IOException
    {
        String username = LoggedInUser.USERNAME;
        LocalDateTime currentUtcTime = Utilities.generateTimestamp();
        
        // Create log file if it doesn't exist
        File logFile = new File(LOG_PATH + LOG_FILE);
        logFile.createNewFile();
            
          // Exception control TRY WITH RESOURCES
          // Closes BufferedWriter and FileWriter automatically
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_PATH + LOG_FILE, true))) {
            writer.append(currentUtcTime.toString() + ": " + username + " logged in");
            writer.newLine();
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
        }
    }
    
}

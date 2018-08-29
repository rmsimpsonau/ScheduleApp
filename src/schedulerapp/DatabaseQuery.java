/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package schedulerapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author sim59419
 */
public class DatabaseQuery {

    private final String query;
    public ResultSet results;
    public long generatedKey;   
    
    /**
     *
     * @param queryString
     */
    public DatabaseQuery(
        String queryString
        )
    {
        this.query = queryString;
    }
    
    public void execute() throws SQLException {
        try {
            Connection dbConn = DatabaseConnection.getConnection();
            if(dbConn != null)
            {
                Statement stmt = DatabaseConnection.getConnection().createStatement();
                results = stmt.executeQuery(query);      
            }
        } catch (SQLException ex) {
            System.out.println("Exception executing sql query: " + ex.getMessage());
        }
    }
    
    public void executeUpdate() throws SQLException {
        Connection dbConn = DatabaseConnection.getConnection();            
        if(dbConn != null)
        {
            PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Sql command failed, no rows affected.");
            }
        }
    }
    
    public void executeInsert() throws SQLException {
        Connection dbConn = DatabaseConnection.getConnection();            
        if(dbConn != null)
        {
            PreparedStatement preparedStmt = DatabaseConnection.getConnection().prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            int affectedRows = preparedStmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Sql command failed, no rows affected.");
            }

            try (ResultSet generatedKeys = preparedStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedKey = generatedKeys.getLong(1);
                }
                else {
                    throw new SQLException("No keys generated");
                }
            }
        }
    }
    
    public void printQuery()
    {
        System.out.println("Query: " + query);
    }
    
    // Assertion to verify results are not null
    public ResultSet getResults() {
        assert isResultsNull(): "Results are null";
        return results;
    }
    
    public long getGeneratedKey() {
        return generatedKey;
    }
    
    private boolean isResultsNull()
    {
        return results != null;
    }
}

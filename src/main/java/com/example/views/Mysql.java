package com.example.views;

import java.lang.constant.Constable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.ArrayList;

import com.example.models.BaseEntity;
import com.example.models.Player;

//Mysql connection class
public class Mysql {
    protected Connection connection;
    protected PreparedStatement statement;
    protected ResultSet result;
    private static final String url = "jdbc:mysql://127.0.0.1:3306/javaxo";
    private static final String username = "JavaUser";
    private static final String password = "password";

    protected ArrayList<PreparedStatement> inserts;
    protected ArrayList<PreparedStatement> updates;
    protected ArrayList<PreparedStatement> deletes;

    public Mysql()  {
        try {
            this.connection = DriverManager.getConnection(url, username, password); // connect to the db
            this.inserts = new ArrayList<PreparedStatement>();
            this.updates = new ArrayList<PreparedStatement>();
            this.deletes = new ArrayList<PreparedStatement>();        
        } 
        
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void create(BaseEntity selected)
    {
        try {
            selected.setId(this.result.getInt("playerId"));
        } 
        
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Execute the select query and store the result set
    public ArrayList<BaseEntity> select(){
        ArrayList<BaseEntity> AllSelected = new ArrayList<BaseEntity>();
        try {
            this.result = this.statement.executeQuery();
            while (result.next()) {
                BaseEntity selected = new BaseEntity();
                create(selected);
                AllSelected.add(selected);
            }

            return AllSelected;
        } 

        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void saveChanges() {
        try {
            // Execute all insert statements
            for (PreparedStatement insert : this.inserts) {
                insert.executeUpdate();
            }

            // Execute all update statements
            for (PreparedStatement update : this.updates) {
                update.executeUpdate();
            }

            // Execute all delete statements
            for (PreparedStatement delete : this.deletes) {
                delete.executeUpdate();
            }
        } 

        catch (Exception e) {
            e.printStackTrace();
        } 
        
        // Clear the lists of statements regardless of success or failure
        finally {
            this.inserts.clear();
            this.updates.clear();
            this.deletes.clear();
        }
    }

    //close the connection to the db
    public void close() throws SQLException {
        this.connection.close();
    }
}
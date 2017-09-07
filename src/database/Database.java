/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import static database.Connection.conn;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Modality;
/**
 *
 * @author Subash99
 */
//here run the apache server on port 85 and mysql sever on 3008
public class Database {
     public boolean isUserPresent(String user){
    Boolean flag=null;
        try{
            PreparedStatement stmt;
            String query = "SELECT Username FROM `user` WHERE `Username` = '" + user + "'";
            stmt = conn.prepareStatement(query);
            ResultSet result = stmt.executeQuery(query);
            if(result.next()){
                flag = true;
            }
            else{
                
                flag = false;
            }
        }
        catch(SQLException ex){
         Alert alert = new Alert(Alert.AlertType.ERROR);
         alert.setHeaderText("Username check error!!");
         alert.setContentText(ex.getLocalizedMessage());
         alert.setTitle("Error");
         alert.initModality(Modality.APPLICATION_MODAL);
         alert.showAndWait();   
        }
        return flag;
    }    
    public boolean isUserAdmin(String username,String password){
        Boolean flag=null;
        try{
            PreparedStatement stmt;
            String query = "SELECT Username,Password,Type FROM user WHERE Username='" +username +"' AND Password='"+password +"' AND Type = 'Admin'";
            stmt = conn.prepareStatement(query);
            ResultSet result = stmt.executeQuery(query);
            if(result.next()){
                flag = true;
            }
            else{
                
                flag = false;
            }
        }
        catch(Exception ex){
         Alert alert = new Alert(AlertType.ERROR);
         alert.setHeaderText("Admin Checking Error !!");
         alert.setContentText("Error while checking the if the person who is adding a new user is an admin");
         alert.setTitle("Error");
         alert.initModality(Modality.APPLICATION_MODAL);
         alert.showAndWait();
        }
        return flag;
    }
    public boolean loginBox(String username, String password){
       
         Boolean flag=null;
        try{
            PreparedStatement stmt;
            String query = "SELECT Username,Password FROM user WHERE Username='" +username +"' AND Password='"+password +"'";
            stmt = conn.prepareStatement(query);
            ResultSet result = stmt.executeQuery(query);
            if(result.next()){
                flag = true;
            }
            else{
                
                flag = false;
            }
        }
        catch(Exception ex){
         Alert alert = new Alert(AlertType.ERROR);
         alert.setHeaderText("Login Error !!");
         alert.setContentText("Error during Login Process");
         alert.setTitle("Error");
         alert.initModality(Modality.APPLICATION_MODAL);
         alert.showAndWait();
        }
        return flag;
    }
 
    public ResultSet getUsers(){
        ResultSet result=null;
        Statement statement;
      try{
        statement = conn.createStatement();
        String query = "select *from user";
        result = statement.executeQuery(query);
        
    } 
      catch(Exception err){
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(err.toString());
            error.setHeaderText("Connection Error !!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
    }
      return result;
    }
    public ResultSet getDeletedUsers(){
        ResultSet result=null;
        Statement statement;
      try{
        statement = conn.createStatement();
        String query = "select *from deleted_user";
        result = statement.executeQuery(query);
        
    } 
      catch(Exception err){
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(err.toString());
            error.setHeaderText("Connection Error !!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
    }
      return result;
    }
    
    
    public void insert(String name,String grade,String sex,
            String date_of_birth,String address,String contact,String father,String mother,
            String guardian, String previous_school, int bill_no
    ){
        PreparedStatement stmt;
        try {
            //converting the received dob(in string format) to date format
            java.sql.Date dob=java.sql.Date.valueOf(date_of_birth);
            stmt = conn.prepareStatement("INSERT INTO `students`"
            + " (`Roll`, `Name`, `Grade`, `Section`, `Sex`, `DOB`, `Address`, `Contact`, `Father`, `Mother`, `Guardian`, `Previous School`, `Bill no`) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
            stmt.setInt(1,0);
            stmt.setString(2,name);
            stmt.setString(3,grade);
            stmt.setString(4,null);
            stmt.setString(5,sex);
            stmt.setDate(6,dob);
            stmt.setString(7,address);
            stmt.setString(8,contact);
            stmt.setString(9,father);
            stmt.setString(10,mother);
            stmt.setString(11,guardian);
            stmt.setString(12,previous_school);
            stmt.setInt(13,bill_no);
            stmt.execute();
            Alert message = new Alert(AlertType.INFORMATION);
            message.setContentText("New data has been successfully inserted");
            message.setHeaderText("Insertion Successful");
            message.setTitle("Successful");
            message.initModality(Modality.APPLICATION_MODAL);
            message.showAndWait();    
                    
        } catch (SQLException err) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(err.getMessage());
            error.setHeaderText("Insertion Error !!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
        } 
    }
    
    public void update(int id,int roll,String name,String grade, String section,String sex,
            Date dob,String address,String contact,String father,String mother,
            String guardian, String previous_school, int bill_no){
        try {
            PreparedStatement stmt = conn.prepareStatement("UPDATE `students` SET `ID`=?,"
                    + "`Roll`=?,`Name`=?,`Grade`=?,`Section`=?,"
                    + "`Sex`=?,`DOB`=?,`Address`=?,`Contact`=?,"
                    + "`Father`=?,"
                    + "`Mother`=?,`Guardian`=?,"
                    + "`Previous School`=?,`Bill no`=? WHERE ID=?");
            stmt.setInt(1,id);
            stmt.setInt(2,roll);
            stmt.setString(3,name);
            stmt.setString(4,grade);
            stmt.setString(5,section);
            stmt.setString(6,sex);
            stmt.setDate(7,dob);
            stmt.setString(8,address);
            stmt.setString(9,contact);
            stmt.setString(10,father);
            stmt.setString(11,mother);
            stmt.setString(12,guardian);
            stmt.setString(13,previous_school);
            stmt.setInt(14,bill_no);
            stmt.setInt(15,id);
            stmt.execute();
            Alert message = new Alert(AlertType.INFORMATION);
            message.setContentText("Data with id: " + id + "has been successfully updated");
            message.setHeaderText("Update Successful");
            message.setTitle("Successful");
            message.initModality(Modality.APPLICATION_MODAL);
            message.showAndWait();
                    } 
        catch (SQLException ex) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(ex.getMessage());
            error.setHeaderText("Update Error !!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();    
        }
    }
    public void delete(int id){
        try {
            
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `deleted_students`"
            + " (`Roll`, `Name`, `Grade`, `Section`, `Sex`, `DOB`, `Address`, `Contact`, `Father`, `Mother`, `Guardian`, `Previous School`, `Bill no`) SELECT"
                    + "`Roll`, `Name`, `Grade`, `Section`, `Sex`, `DOB`, `Address`, `Contact`, `Father`, `Mother`, `Guardian`, `Previous School`, `Bill no` FROM students"
                    + "where id = ?");
            stmt.setInt(1, id);
            stmt.execute();
            stmt = conn.prepareStatement("DELETE FROM students where id=?");
            stmt.setInt(1,id);
            stmt.executeUpdate();            
            Alert message = new Alert(AlertType.INFORMATION);
            message.setContentText("Data with id: " + id + "has been successfully Deleted");
            message.setHeaderText("Deletion Successful");
            message.setTitle("Successful");
            message.initModality(Modality.APPLICATION_MODAL);
            message.showAndWait();
        } catch (SQLException ex) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(ex.getMessage());
            error.setHeaderText("Deletion Error !!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
        }
    }
    public void deleteUser(int id){
        try {        
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO deleted_user (ID,Name,Username,Password,Contact,Type) SELECT ID,Name,Username,Password,Contact,Type FROM user WHERE id = ?");
            stmt.setInt(1,id);
            stmt.execute();            
            stmt = conn.prepareStatement("DELETE FROM user where id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            Alert message = new Alert(AlertType.INFORMATION);
            message.setContentText("Data with id: " + id + " has been successfully Deleted");
            message.setHeaderText("Deletion Successful");
            message.setTitle("Successful");
            message.initModality(Modality.APPLICATION_MODAL);
            message.showAndWait();
            
            
        } catch (SQLException ex) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(ex.getMessage());
            error.setHeaderText("Deletion Error for "+ id +"!!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
        }
    }
    public void recoverUser(int id){
        try {        
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO user (ID,Name,Username,Password,Contact,Type) SELECT ID,Name,Username,Password,Contact,Type FROM deleted_user WHERE id = ?");
            stmt.setInt(1,id);
            stmt.execute();            
            stmt = conn.prepareStatement("DELETE FROM deleted_user where id=?");
            stmt.setInt(1, id);
            stmt.executeUpdate();
            Alert message = new Alert(AlertType.INFORMATION);
            message.setContentText("Data with id: " + id + " has been successfully Recoverd");
            message.setHeaderText("Recovery Successful");
            message.setTitle("Successful");
            message.initModality(Modality.APPLICATION_MODAL);
            message.showAndWait();
            
            
        } catch (SQLException ex) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(ex.getMessage());
            error.setHeaderText("Deletion Error for "+ id +"!!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
        }
    }
          
  
    public void recoverData(int id){
        try {
            
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO `students`"
            + " (`Roll`, `Name`, `Grade`, `Section`, `Sex`, `DOB`, `Address`, `Contact`, `Father`, `Mother`, `Guardian`, `Previous School`, `Bill no`) SELECT"
                    + "`Roll`, `Name`, `Grade`, `Section`, `Sex`, `DOB`, `Address`, `Contact`, `Father`, `Mother`, `Guardian`, `Previous School`, `Bill no` FROM deleted_students"
                    + "where id = ?");
            stmt.setInt(1, id);
            stmt.execute();
            stmt = conn.prepareStatement("DELETE FROM deleted_students where id=?");
            stmt.setInt(1,id);
            stmt.executeUpdate();            
            Alert message = new Alert(AlertType.INFORMATION);
            message.setContentText("Data with id: " + id + "has been successfully recovered");
            message.setHeaderText("Recovery Successful");
            message.setTitle("Successful");
            message.initModality(Modality.APPLICATION_MODAL);
            message.showAndWait();
        } catch (SQLException ex) {
            Alert error = new Alert(AlertType.ERROR);
            error.setContentText(ex.getMessage());
            error.setHeaderText("Deletion Error !!");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
        }
    }
    
}

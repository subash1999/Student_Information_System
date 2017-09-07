/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

/**
 *
 * @author subas
 */
public class Connection {
    public static String servername;
    private static String username;
    private static  String password;
    public static java.sql.Connection conn;
    public static Statement statement;
    public static void connect(){                 
        try{ 
        Class.forName("com.mysql.jdbc.Driver");    
        servername = "jdbc:mysql://localhost:3308/school?zeroDateTimeBehavior=convertToNull";
        username = "root";
        password = "";
        conn = DriverManager.getConnection(servername , username, password);
        
        }
        catch(SQLException err){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText(err.getMessage()+"\n"+ err.getLocalizedMessage());
            error.setHeaderText("Connection Error while connecting");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
          
        }   
        catch(ClassNotFoundException err){
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText(err.getMessage()+"\n"+ err.getLocalizedMessage());
            error.setHeaderText("Class  not found");
            error.setTitle("Error");
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
        }
    
   
    }
    
   
}

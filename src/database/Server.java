/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import javafx.stage.Modality;

/**
 *
 * @author subas
 */
public class Server {
    public void startServer(){
       boolean apache_running = isServerRunning(85);
       boolean my_sql_running = isServerRunning(3308);
       if(!apache_running || !my_sql_running){
           runApache();
           runMySql();
           try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setContentText(ex.getMessage());
            error.setHeaderText("Error while running the thread 1st time");
            error.setTitle("Error");
            
            error.initModality(Modality.APPLICATION_MODAL);
            error.showAndWait();
        }
       }       
      apache_running = isServerRunning(85);
      my_sql_running = isServerRunning(3308);
      if(apache_running && my_sql_running){
      }       
      else {
          try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
        }
      }
       database.Connection.connect();
    }
    
    public boolean isServerRunning(int port){
        boolean is_server_on=false;
        try{
            Socket socket = new Socket("127.0.0.1",port);
            is_server_on=true;
            socket.close();            
        }
        catch(IOException ex){
            
        }
        return is_server_on;
    }
    public void runMySql(){
        try {
            Process process1 = Runtime.getRuntime().exec("C:\\xampp\\xampp_start.exe");
            //Process process2 = Runtime.getRuntime().exec("C:\\xampp\\mysql\\bin\\mysqld.exe");
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("My SQL cannot be started");
            alert.setContentText(ex.getMessage());
            alert.setTitle("My SQL Error");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.show();
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex1);
            }
            System.exit(0);
        }
    }
     public void runApache(){
        try {
            Process process = Runtime.getRuntime().exec("C:\\xampp\\apache\\bin\\httpd.exe");
        } catch (IOException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Apache Server cannot be started");
            alert.setContentText(ex.getMessage());
            alert.setTitle("Apache Error");            
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.show();
            try {
                Thread.sleep(6000);
            } catch (InterruptedException ex1) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex1);
            }
            System.exit(0);
        }
    }
}

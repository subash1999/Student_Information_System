

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import database.Server;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import login.LoginController;

/**
 *
 * @author subas
 */
public class Student_result_calculator extends Application {
private Stage primaryStage = null;
    @Override
    public void start(Stage primaryStage)  {
//        Runnable r = new Runnable(){
//            @Override
//        public void run(){
//        Server server = new Server();
//        server.startServer();
//            }
//        };
//        new Thread(r).start();
       Server server = new Server();
       server.startServer();
        try {
            LoginController.current_year="2074";
            Parent root = FXMLLoader.load(getClass().getResource("/ledger/ledger.fxml"));
//           Parent root = FXMLLoader.load(getClass().getResource("/teacher/addteacher.fxml"));
//           Parent root = FXMLLoader.load(getClass().getResource("/login/login.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
              //primaryStage.setResizable(flse);
            primaryStage.toFront();
            primaryStage.show();
            
           //this.primaryStage=primaryStage;
            
        } catch (Exception ex) {
            System.out.println("File not found" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
       
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}

package User.Student;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author subas
 */
public class Student_result_calculator extends Application {
    
    @Override
    public void start(Stage primaryStage)  {       
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/User/Teacher/teacher.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
           
            primaryStage.setOnCloseRequest(e->{
                e.consume();
                Alert close = new Alert(AlertType.CONFIRMATION);
                close.setTitle("Confirm Close Request");
                close.setHeaderText("Do you want to close all open windows");
                close.setContentText("Closing this window will cause all the open windows to close "
                        + "because this is the main window of this software");
                Optional<ButtonType> result = close.showAndWait();
                if (result.get() == ButtonType.OK){
                Platform.exit();
                // ... user chose OK
                } 
                else{
                    close.close();
                }
             
                });
            //primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception ex) {
            System.out.println("File not found");
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

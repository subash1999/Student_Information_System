package User.Student;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author subas
 */
public class Student_result_calculator extends Application {
    
    @Override
    public void start(Stage primaryStage)  {
       
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/User/Teacher/teachers_of_student.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            //primaryStage.setMaximized(true);
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

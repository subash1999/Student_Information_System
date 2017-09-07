/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student_result_calculator;

import database.Database;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class LoginController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button login;
    @FXML
    private ChoiceBox<String> choice_box;
     ObservableList<String> list = FXCollections.observableArrayList("Regular User","Admin");
    
   public  void login_btn(){
       Database database  = new Database();
       
        if("1".equals(username.getText().toString())){
            
           Parent root;
           try {
               Stage window = new Stage();
               FXMLLoader loader = new FXMLLoader();
               root = loader.load(getClass().getResource("login.fxml").openStream());
               LoginController login_controller = (LoginController)loader.getController();
               
               Scene scene = new Scene(root);
               window.setScene(scene);
               window.setTitle(login_controller.username.toString() + ":- SRC " + "(User Mode)");
               window.show();
           } catch (IOException ex) {
               ex.printStackTrace();
               System.out.println("mainwindow.fxml file not found");
           }
           
        }
        else{
           Alert a =  new Alert(AlertType.INFORMATION);
           a.setContentText("Wrong" + "Username is : " + username.getText().toString());
           a.show();
        }
   }
    public void cancel(){
        System.exit(0);
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choice_box.setItems(list);
        choice_box.getSelectionModel().select("Regular User");
    }    
    
}

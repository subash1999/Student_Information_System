/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
    private  TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private Button login;
    @FXML
    private Button cancel_btn;
    @FXML
    private ChoiceBox<String> choice_box;
    
    public static String current_year;
    public static String current_user;
    Scene scene;
    
   @FXML
   public  void login_btn(ActionEvent event){
        if(!"".equals(username.getText())){
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        try {
            result = conn.createStatement().executeQuery("select * from `user` where Username='" +
                    username.getText() + "' and Password='" + password.getText() + "'");
            if(result.next()){
               Parent root=null;
               //setting the value of current year before the fxml file is called
               //if fxml file is called first then before value is set, then main window will 
               //receive the current_year value as null
               current_year = choice_box.getSelectionModel().getSelectedItem();
               current_user = username.getText();
               Stage window = new Stage();
               FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main/mainwindow.fxml"));
               root = loader.load();
               // primaryStage.setMaximized(true);
           
           
                //getting the current stage with the help of the button 
                //present in the current scene. i.e scene of the login box
                //assign the window by casting it as the stage to the current stage
                //use the stage to close the window of the stage
               window = (Stage) login.getScene().getWindow();
               window.close();
               //again assing a new stage to remove all the properties of the login box window
               window= new Stage();
               scene = new Scene(root);
               window.setMaximized(true);
               window.setScene(scene);
               window.setTitle("Student Result Calculator /   USER :  " + current_user + "   /   SESSION : " + current_year );
                window.setOnCloseRequest(e->{
                e.consume();
                Alert close = new Alert(AlertType.CONFIRMATION);
                close.setTitle("Confirm Close Request");
                close.setHeaderText("Do you want to close this application");
                close.setContentText("Closing this window will cause all the open windows to close "
                        + "because this is the main window of this application");
                Optional<ButtonType> res = close.showAndWait();
                if (res.get() == ButtonType.OK){
                Platform.exit();
              // ... user chose OK
                } 
                else{
                    close.close();
                }
             
                });
               window.show();
           
            }
            else{
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Wrong Username or Password");
            
            alert.show();
            }
        } catch (Exception ex) {ex.printStackTrace();
            System.out.println("Exception at login "  + ex.getMessage());
        }
        }
        else{
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Username is null");
            alert.setContentText("Username cannot be null. So please enter the username");
            alert.show();
        }
       
   }
   
    @FXML
   public  void cancel_btn(ActionEvent event){
    System.exit(0);
   }
   public void onEnterPressed(KeyEvent e){          
        if(e.getCode()==KeyCode.ENTER){          
            if(login.isFocused())
                login.fire();       
            else if(cancel_btn.isFocused())
                cancel_btn.fire();
           }
            
                
    }
   public void choiceBox(){
       Connection conn = database.Connection.conn;
       ResultSet result = null;
       ResultSet result_2 = null;
       try{
           result_2 = conn.createStatement().executeQuery("select max(`Year`) from `session`");
           result= conn.createStatement().executeQuery("select `Year` from session");
           while(result.next()){
               if(!"0".equals(result.getString("Year"))){
                choice_box.getItems().add(result.getString("Year"));
               }
            }
           while(result_2.next()){
               choice_box.getSelectionModel().select(result_2.getString("max(`Year`)"));
           }
        choice_box.getSelectionModel().selectedItemProperty().addListener(e->{
            current_year = choice_box.getSelectionModel().getSelectedItem().toString();
        });
       }
       catch(Exception e){
           System.out.println("Exception at login : while adding items to"
                   + "choice box : " + e.getMessage());
       }
      
   }
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        choiceBox();
        username.textProperty().addListener(e->{
            current_user=username.getText();
        });
    }    
    
}
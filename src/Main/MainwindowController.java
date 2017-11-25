/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Main;

import student.StudentController;
import teacher.TeacherController;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import ledger.LedgerController;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class MainwindowController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label current_time;
    @FXML
    private Label current_date;
    @FXML
    private GridPane left_gridpane;
    @FXML
    private AnchorPane calendar_pane;
    @FXML
    private Label current_user;
    @FXML
    private Label current_year;
    @FXML
    private MenuItem settings_menu_item;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {     
     dateAndTime(); 
     System.out.println(LoginController.current_year);
     current_user.setText("User : " + LoginController.current_user);
     current_year.setText("Session : " + LoginController.current_year);
    } 
    @FXML
    public void viewSettingsMenuItemClicked(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/settings/settings.fxml"));
            Parent root1;            
            root1 =  fxmlLoader.load();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();           
            stage.setTitle("Settings");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file : " + ex.getMessage());
        }

    }
    @FXML
    public void closeMenuItemClicked(ActionEvent event){
                Stage window = (Stage) current_date.getScene().getWindow();
                Alert close = new Alert(Alert.AlertType.CONFIRMATION);
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
             
        
    }
    @FXML
    private void addTeacherMenuItemOnAction(ActionEvent event) {
         try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/teacher/addteacher.fxml"));
            Parent root1;            
            root1 =  fxmlLoader.load();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();           
            stage.setTitle("Add Teacher");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file addteacher.fxml at MainWindow : " + ex.getMessage());
        }

    }
    @FXML
    public void studentClicked(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/student/student.fxml"));
            
            Parent root1 = null;            
            root1 =  fxmlLoader.load();
            StudentController controller = (StudentController)fxmlLoader.getController();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();           
            stage.setTitle("Students");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file : " + ex.getMessage());
        }
    }
    @FXML
    public void teacherClicked(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/teacher/teacher.fxml"));
            Parent root1 = null;            
            root1 =  fxmlLoader.load();
            TeacherController controller = (TeacherController)fxmlLoader.getController();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();           
            stage.setTitle("Teacher");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file : " + ex.getMessage());
        }
    }
    @FXML
    public void ledgerClicked(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ledger/ledger.fxml"));
            
            Parent root1 = null;            
            root1 =  fxmlLoader.load();
            LedgerController controller = (LedgerController)fxmlLoader.getController();
            //passing the id of the selected student to the studentdetail.fxml file
            Stage stage = new Stage();           
            stage.setTitle("Ledger");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (Exception ex) {
            System.out.println("Error while loading the file : " + ex.getMessage());
        }
    }
    
//GADGETS PORTION AT THE END OF THE CODE i.e The gadgets included are
//i) date and time display
//ii)Calculator
   //date and time portion of the main window
    public void dateAndTime(){
         //making the digital clock and assigning to the current_time label
         current_time.setFont(Font.font("Times New Roman", 32));
         
        
        final Timeline digitalTime = new Timeline(
      new KeyFrame(Duration.seconds(0), (ActionEvent actionEvent) -> {
          Date date = new Date();
          DateFormat time_format = new SimpleDateFormat("hh:mm:ss a");
          current_time.setText(time_format.format(date));
         }),
      new KeyFrame(Duration.seconds(1))
    );
        digitalTime.setCycleCount(Animation.INDEFINITE);
        digitalTime.play();
        //assiging the current data to the current date label
        Date date = new Date();
        DateFormat date_format;
        date_format = new SimpleDateFormat("dd/MM/yyyy");        
        current_date.setFont(Font.font("Times New Roman",21));
        Calendar c = Calendar.getInstance();
        String temp_date = date_format.format(date);
        date= c.getTime();
        String day_of_week = new SimpleDateFormat("EEEE",Locale.ENGLISH).format(date.getTime());
        current_date.setText(day_of_week + ", " + temp_date);
    }   
        
    //date and time portion ended
    
    //calculator job below
    private BigDecimal left;
    private String selectedOperator;
    private boolean numberInputting;

    @FXML
    private TextField display;  
   

    @FXML
    public void handleOnAnyButtonClicked(ActionEvent evt) {
        Button button = (Button)evt.getSource();
        final String buttonText = button.getText();
        if (buttonText.equals("C") || buttonText.equals("AC")) {
            if (buttonText.equals("AC")) {
                left = BigDecimal.ZERO;
            }
            selectedOperator = "";
            numberInputting = false;
            display.setText("0");;
            return;
        }
        if (buttonText.matches("[0-9\\.]")) {
            if (!numberInputting) {
                numberInputting = true;
                display.clear();
            }
            display.appendText(buttonText);
            return;
        }
        if (buttonText.matches("[＋－×÷]")) {
            left = new BigDecimal(display.getText());
            selectedOperator = buttonText;
            numberInputting = false;
            return;
        }
        if (buttonText.equals("=")) {
            final BigDecimal right = numberInputting ? new BigDecimal(display.getText()) : left;
            left = calculate(selectedOperator, left, right);
            display.setText(left.toString());
            numberInputting = false;
            return;
        }
    }

   private BigDecimal calculate(String operator, BigDecimal left, BigDecimal right) {
        switch (operator) {
            case "＋":
                return left.add(right);
            case "－":
                return left.subtract(right);
            case "×":
                return left.multiply(right);
            case "÷":
                return left.divide(right);
            default:
        }
        return right;
    }
//calculator code portion ended
    
}

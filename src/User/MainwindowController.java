/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User;

import User.Student.StudentController;
import static User.Student.StudentController.student_id;
import com.sun.javafx.scene.control.skin.DatePickerSkin;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private AnchorPane anchor;
    @FXML
    private AnchorPane outer_anchor;
    @FXML
    private Label current_time;
    @FXML
    private Label current_date;
    @FXML
    private GridPane left_gridpane;
    @FXML
    private AnchorPane calendar_pane;
    @FXML
    public void calender(ActionEvent event){
        DatePickerSkin datePickerSkin = new DatePickerSkin(new DatePicker(LocalDate.now()));
       calendar_pane = (AnchorPane) datePickerSkin.getPopupContent();
       
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {     
     dateAndTime(); 
     
    } 
    public void student_clicked(){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/User/Student/student.fxml"));
            
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
//GADGETS PORTION AT THE END OF THE CODE i.e The gadgets included are
//i) date and time display
//ii)Calculator
   //date and time portion of the main window
    public void dateAndTime(){
         //making the digital clock and assigning to the current_time label
         current_time.setFont(Font.font("Times New Roman", 35));
         
        
        final Timeline digitalTime = new Timeline(
      new KeyFrame(Duration.seconds(0),
        new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent actionEvent) {
              Date date = new Date();
             DateFormat time_format = new SimpleDateFormat("hh:mm:ss a");            
            current_time.setText(time_format.format(date));          
            
        
          }
        }
      ),
      new KeyFrame(Duration.seconds(1))
    );
        digitalTime.setCycleCount(Animation.INDEFINITE);
        digitalTime.play();
        //assiging the current data to the current date label
        Date date = new Date();
        DateFormat date_format;
        date_format = new SimpleDateFormat("yyyy/MM/dd");        
        current_date.setFont(Font.font("Times New Roman",40));
        current_date.setText(date_format.format(date));       
                    
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

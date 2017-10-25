/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledger;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class NewexamController implements Initializable {

    /**
     * Initializes the controller class.
     */
     @FXML
    private TextField exam_name;

    @FXML
    private Button create_btn;

    @FXML
    private Label label;

    @FXML
    private TextField result_date;    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        label.setVisible(false);
        create_btn.setDisable(true);
        listnerForTextBox();
        
    }   
    private void listnerForTextBox(){
        exam_name.textProperty().addListener(e->{
            String name = exam_name.getText();
            if("".equals(name)){
                create_btn.setDisable(true);
            }
            else{
            String query ="SELECT * FROM year_"+LoginController.current_year+"_exam "
                    + " WHERE `Name` = '"+name+"'";
            ResultSet result = null;
            Connection conn = database.Connection.conn;
            try{
                result = conn.createStatement().executeQuery(query);
                int i=0;
                while(result.next()){
                    i++;                    
                }   
                if(i>0){
                    create_btn.setDisable(true);
                    label.setVisible(true);
                }
                else{
                    create_btn.setDisable(false);
                    label.setVisible(false);
                    
                }
            }   
            catch(Exception ex){
                System.out.println("Exception at listnerForTextBox at NewexamController : "+ ex.getMessage());
                create_btn.setDisable(true);
            }
            }
        });
    }
    @FXML
    private void clickCreateBtn(MouseEvent event) {       
        String name = exam_name.getText();
        if(name.contains(" ")){
            Alert alert = new Alert(AlertType.WARNING);
            alert.setHeaderText("Space present in the exam name");
            alert.setContentText("Putting a space in the middle of a exam name may cause problems."
                    + "So please use underscore ' _ ' instead of spacing to avoid problems.");
            alert.show();
        }
    else{
        String date = result_date.getText();
        if("".equals(date)){
            date = "0000/00/00";
        }
        String query = "INSERT INTO year_"+LoginController.current_year+"_exam (Name,Result_date)"
                + " VALUES ('"+name+"','"+date+"')";
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        try{
             conn.createStatement().execute(query);
             Alert al = new Alert(AlertType.INFORMATION);
             al.setHeaderText("New exam Created Successfully");
             al.setContentText("New exam of name : '" + name + "' was created successfully");
             al.show();
             exam_name.clear();
             result_date.clear();
             
        }
        catch(Exception e){ 
            System.out.println("Exception at clickCreateBtn() at NewexamController :" + e.getMessage());
        }    
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class EditteacherController implements Initializable {

    @FXML
    private Label teacher_id;

    @FXML
    private JFXTextField name;

    @FXML
    private RadioButton male;

    @FXML
    private ToggleGroup gender_group;

    @FXML
    private RadioButton female;

    @FXML
    private JFXTextField phone;

    @FXML
    private JFXTextField address;

    @FXML
    private JFXTextField qualification;

    @FXML
    private Button undo_all_btn;

    @FXML
    private Button save_btn;
    
    public IntegerProperty id = new SimpleIntegerProperty();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       id.set(0);
       listenerForId();
    }
    private void listenerForId(){
        id.addListener(e->{
            if(id.get()!=0){
                setValues(id.get());
            }
        });
    }
    private boolean areRequiredFieldsFilled() {
        boolean filled = !name.getText().isEmpty()
                && !(!male.isSelected() && !female.isSelected())
                && !phone.getText().isEmpty()
                && !address.getText().isEmpty();
        return filled;
    }

    private void setValues(int teacher_id) {
        try{
            String year = LoginController.current_year;
            int id = teacher_id;
            String query = "SELECT * FROM year_"+year+"_teacher "
                    + "WHERE Teacher_id = "+id+" WHERE Active = 'yes' ;";
            Connection conn =database.Connection.conn;
            ResultSet result = conn.createStatement().executeQuery(query);
            while(result.next()){
                this.teacher_id.setText(String.valueOf(result.getInt("Teacher_id")));
                this.name.setText(result.getString("Name"));
                this.phone.setText(result.getString("Phone"));
                this.address.setText("Address");
                String qualification = result.getString("Qualification");
                if(result.wasNull()){
                    qualification = "";
                }
                this.qualification.setText(qualification);
                String gender = result.getString("Gender");
                if("Male".equalsIgnoreCase(gender)||"M".equalsIgnoreCase(gender)){
                    male.setSelected(true);
                }
                else if ("Female".equalsIgnoreCase(gender)||"F".equalsIgnoreCase(gender)){
                    female.setSelected(true);
                }
            }
        }
        catch(Exception e){
            System.out.println("Exception at setValues() at EditteacherController "
                    + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void clickSaveBtn(MouseEvent event) {
        if (areRequiredFieldsFilled()) {
            String year = LoginController.current_year;
            int id = Integer.valueOf(this.teacher_id.getText());
            String name = this.name.getText();
            String gender = null;
            if (male.isSelected()) {
                gender = "Male";
            } else if (female.isSelected()) {
                gender = "Female";
            }
            String phone = this.phone.getText();
            String address = this.address.getText();
            String qualification = "null";
            if (!this.qualification.getText().isEmpty()) {
                qualification = this.qualification.getText();
            }
            String query = "UPDATE year_" + year + "_teacher SET "
                    + "Name = ?,"
                    + "Gender = ?,"
                    + "Phone = ?,"
                    + "Address=?,"
                    + "Qualification = ? "
                    + "WHERE Teacher_id = ? ;";
            try {
                database.Connection.connect();
                Connection conn = database.Connection.conn;
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, gender);
                pst.setString(3, phone);
                pst.setString(4, address);
                pst.setString(5, qualification);
                pst.setInt(6, id);
                pst.execute();
                Stage stage = (Stage) save_btn.getScene().getWindow();
                stage.close();
                Alert a = new Alert(AlertType.INFORMATION);
                a.setHeaderText("Updation Successful !!!");
                a.setContentText("The teacher with id : " + id
                        + "\n was updated successfuly ");
                a.show();
            } catch (Exception e) {
                System.out.println("Exception at clickSaveBtn() at "
                        + "EditteacherController : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Fill Required Fields (*)");
            a.setContentText("Fields with star(*) are required fields");
            a.show();
        }
    }

    @FXML
    private void clickUndoAllBtn(MouseEvent event) {
        if(id.get()!=0){
            setValues(id.get());
        }
    }
}

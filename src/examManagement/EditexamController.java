/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examManagement;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class EditexamController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField exam_name;

    @FXML
    private Button save_btn;

    @FXML
    private Label label;

    @FXML
    private TextField full_exam_name;

    @FXML
    private TextField result_date;

    public IntegerProperty exam_id = new SimpleIntegerProperty();
    private String current_exam = "";
    private String current_full_name = "";
    private String current_release_date = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        label.setVisible(false);
        save_btn.setDisable(true);
        listnerForTextBox();
        listenerForExamId();

    }

    private void listenerForExamId() {
        exam_id.addListener(e -> {
            if (exam_id.get() != 0) {
                int id = exam_id.get();
                setValue(id);
            }
        });
    }

    private void setValue(int id) {
        if (id != 0) {
            try {
                String year = LoginController.current_year;
                Connection conn = database.Connection.conn;
                String query = "SELECT * FROM year_" + year + "_exam WHERE"
                        + "  Exam_id = " + id;
                ResultSet result = conn.createStatement().executeQuery(query);
                while (result.next()) {
                    current_exam = result.getString("Name");
                    current_full_name = result.getString("Full_name");
                    current_release_date = result.getString("Result_date");
                    full_exam_name.setText(result.getString("Full_name"));
                    exam_name.setText(result.getString("Name"));
                    result_date.setText(result.getString("Result_date"));
                }
            } catch (Exception e) {
                System.out.println("Exception at EditexamController "
                        + "at EditexamController : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void listnerForTextBox() {
        exam_name.textProperty().addListener(e -> {
            String name = exam_name.getText();
            if ("".equals(name)) {
                save_btn.setDisable(true);
            } else if (name.equals(current_exam)) {
                save_btn.setDisable(false);
                label.setVisible(false);
            } else {
                String year = LoginController.current_year;
                String query = "SELECT * FROM year_" + year + "_exam "
                        + " WHERE `Name` = '" + name + "' ";
                ResultSet result = null;
                Connection conn = database.Connection.conn;
                try {
                    result = conn.createStatement().executeQuery(query);
                    int i = 0;
                    save_btn.setDisable(false);
                    label.setVisible(false);
                    while (result.next()) {
                        save_btn.setDisable(true);
                        label.setVisible(true);
                    }
                } catch (Exception ex) {
                    System.out.println("Exception at listnerForTextBox at EditexamController : " + ex.getMessage());
                    save_btn.setDisable(true);
                }
            }
        });
    }

    @FXML
    private void clickSaveBtn(MouseEvent event) {
        String name = exam_name.getText();
        if (name.contains(" ")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Space present in the exam name");
            alert.setContentText("Putting a space in the middle of a exam name may cause problems."
                    + "So please use underscore ' _ ' instead of spacing to avoid problems.");
            alert.show();
        } else {
            String date = result_date.getText();
            if (date.isEmpty()) {
                date = "0000/00/00";
            }
            String full_name = full_exam_name.getText();
            if (full_name.isEmpty()) {
                full_name = "null";
            }
            String year = LoginController.current_year;
            Connection conn = database.Connection.conn;
            ResultSet result = null;
            try {
                String query = "UPDATE year_" + year + "_exam SET"
                    + " Name = ? , "
                    + " Result_date = ? , "
                    + " Full_name = ? "
                    + " WHERE Exam_id = " + exam_id.get() + " ;";            
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, date);
                pst.setString(3, full_name);
                pst.execute();
                Alert al = new Alert(Alert.AlertType.INFORMATION);
                al.setHeaderText("Exam Updated Successfully");
                al.setContentText("Exam of name : '" + name + "' was updated successfully");
                al.show();
                exam_name.clear();
                result_date.clear();

            } catch (Exception e) {
                System.out.println("Exception at clickSaveBtn() at EditexamController :" + e.getMessage());
            }
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class AddstudentController implements Initializable {
  
    @FXML
    private JFXTextField name_textfield;

    @FXML
    private RadioButton male_radio_btn;

    @FXML
    private ToggleGroup gender_group;

    @FXML
    private RadioButton female_radio_btn;

    @FXML
    private JFXDatePicker dob_datepicker;

    @FXML
    private JFXTextField address_textfield;

    @FXML
    private JFXTextField contact_textfield;

    @FXML
    private JFXTextField father_textfield;

    @FXML
    private JFXTextField mother_textfield;

    @FXML
    private JFXTextField guardian_textfield;

    @FXML
    private JFXTextField previous_school_textfield;

    @FXML
    private ComboBox grade_combobox;

    @FXML
    private ComboBox section_combobox;

    @FXML
    private JFXTextField roll_textfield;

    @FXML
    private Button register_btn;

    @FXML
    private Button clear_all_btn;

    @FXML
    private Label roll_no_label;

    private ObservableList<ObservableList> table_data = FXCollections.observableArrayList();
    private boolean edit_ongoing = false;
    private boolean register_ongoing = true;
    private String gender = " ";
    private int student_id = 0;
    @FXML
    private JFXTextField student_id_textfield;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database.Connection.connect();
        formatDatePicker();
        refresh();
         contact_textfield.setStyle("-fx-prompt-text-fill:gray");
    }

    private void refresh() {
//        getStudentId();
        makeRollAndSectionCombobox();
        dob_datepicker.setValue(null);
        listenerForGender();
        listenerForRoll();
        makeContactTextFieldNumeric();
        makeRollTextFieldNumeric();
    }

//    private void getStudentId() {
//        try {
//            Stage stage = (Stage) register_btn.getScene().getWindow();
//        
//            stage.setOnCloseRequest(e->{
//            if(!areAllFieldsBlank()){
//            Alert a = new Alert(AlertType.CONFIRMATION);
//            a.setHeaderText("Seems you are entering the datas !!!");
//            a.setContentText("DO you want to clear all the feilds");
//            Optional<ButtonType> btn = a.showAndWait();
//            if(btn.get()==ButtonType.OK){
//                
//            }
//            else{
//                e.consume();
//            }
//            }
//        });
//
//            Connection conn = database.Connection.conn;
//            String year = LoginController.current_year;
//            String query = "SELECT MAX(Student_id) FROM year_" + year + "_student ";
//            ResultSet result = conn.createStatement().executeQuery(query);
//            while (result.next()) {
//                student_id_textfield.setText(String.valueOf(result.getInt(1) + 1));
//            }
//        } catch (Exception e) {
//            System.out.println("Exception at getStudentId() at "
//                    + "AddstudentController : " + e.getMessage());
//            e.printStackTrace();
//        }
//    }

    private void setRoll() {
        if (!(grade_combobox.getSelectionModel().getSelectedItem() == null)
                && !(section_combobox.getSelectionModel().getSelectedItem() == null)) {
            try {
                String grade = grade_combobox.getSelectionModel().getSelectedItem().toString();
                String section = section_combobox.getSelectionModel().getSelectedItem().toString();
                Connection conn = database.Connection.conn;
                String year = LoginController.current_year;
                String query = "SELECT MAX(Roll) FROM year_" + year + "_student "
                        + "WHERE Grade = ? AND Section = ? AND Active = 'yes'";
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, grade);
                pst.setString(2, section);
                ResultSet result = pst.executeQuery();
                int roll = 0;
                while (result.next()) {
                    roll = result.getInt(1) + 1;
                }
                roll_textfield.setText(String.valueOf(roll));
                roll_textfield.setEditable(true);
            } catch (Exception e) {
                System.out.println("Exception at setRoll() at "
                        + "AddstudentController : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void makeRollAndSectionCombobox() {
        try {
            Connection conn = database.Connection.conn;
            String year = LoginController.current_year;
            //adding values in the grade_combobox
            String query = "SELECT DISTINCT Grade FROM year_" + year + "_grade ";
            ResultSet result = conn.createStatement().executeQuery(query);
            if (!grade_combobox.getItems().isEmpty()) {
                grade_combobox.getItems().clear();
            }
            while (result.next()) {
                grade_combobox.getItems().add(result.getString(1));
            }
            //listener for grade_combobox , inside which we will make the section 
            //combobox
            grade_combobox.getSelectionModel().selectedItemProperty().addListener(e -> {
                if (!(grade_combobox.getSelectionModel().getSelectedItem() == null)) {
                    roll_textfield.setText(null);
                    roll_textfield.setEditable(false);
                    String grade = grade_combobox.getSelectionModel().getSelectedItem().toString();
                    String sql = "SELECT DISTINCT Section FROM year_" + year + "_grade "
                            + "WHERE Grade = ?";
                    PreparedStatement pst;
                    try {
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, grade);
                        ResultSet res = pst.executeQuery();
                        if (!section_combobox.getItems().isEmpty()) {
                            section_combobox.getItems().clear();
                        }
                        while (res.next()) {
                            section_combobox.getItems().add(res.getString(1));
                        }
                        if (!section_combobox.getItems().isEmpty()) {
                            section_combobox.getSelectionModel().selectFirst();
                        }
                    } catch (SQLException ex) {
                        System.out.println("Exception while"
                                + "inside listener of grade_combobox"
                                + " at makeRollAndSectionCombobox() at "
                                + "AddstudentController : " + ex.getMessage());
                        ex.printStackTrace();
                    }

                }
            });
            //listener for section_combobox
            section_combobox.getSelectionModel().selectedItemProperty().addListener(e -> {
                if (!(section_combobox.getSelectionModel().getSelectedItem() == null)) {
                    roll_textfield.setText(null);

                    setRoll();
                }
            });
        } catch (Exception e) {
            System.out.println("Exception at listenerRollNo() at "
                    + "AddstudentController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void formatDatePicker() {
        dob_datepicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) {
                    return "";
                }
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return null;
                }
                return LocalDate.parse(dateString, dateTimeFormatter);
            }
        });
    }

    private void listenerForRoll() {
        roll_textfield.textProperty().addListener(e -> {
            String grade = "";
            String section = "";
            if (grade_combobox.getSelectionModel().getSelectedItem() != null) {
                grade = grade_combobox.getSelectionModel().getSelectedItem().toString();
            }
            if (section_combobox.getSelectionModel().getSelectedItem() != null) {
                section = section_combobox.getSelectionModel().getSelectedItem().toString();
            }
            if (roll_textfield.getText() != null) {
                try {
                    int roll = Integer.valueOf(roll_textfield.getText());
                    String year = LoginController.current_year;
                    Connection conn = database.Connection.conn;
                    String query = "SELECT Roll FROM year_" + year + "_student WHERE "
                            + "Roll =? AND Grade = ? AND Section = ? "
                            + "AND Active = 'yes'";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setInt(1, roll);
                    pst.setString(2, grade);
                    pst.setString(3, section);
                    ResultSet result = pst.executeQuery();
                    boolean already_present = false;
                    while (result.next()) {
                        already_present = true;
                    }
                    if (already_present) {
                        roll_no_label.setText("X");
                        roll_no_label.setStyle("-fx-text-fill:red;");
                        register_btn.setDisable(true);
                    } else {
                        roll_no_label.setText("√");
                        roll_no_label.setStyle("-fx-text-fill:green;");
                        register_btn.setDisable(false);
                    }
                }
                catch(NumberFormatException ex){
                    System.out.println("Exception Number Format :"+ex.getMessage());
                }
                catch (Exception ex) {
                    System.out.println("Exception at listenerForRoll() at "
                            + "AddstudentController : " + ex.getMessage());
                    ex.printStackTrace();
                    roll_no_label.setText("X");
                    roll_no_label.setStyle("-fx-text-fill:red;");
                    register_btn.setDisable(true);
                }
            }

        });
    }

    private void listenerForGender() {
        gender_group.selectedToggleProperty().addListener(e -> {
            if (male_radio_btn.isSelected()) {
                gender = "Male";
            } else if (female_radio_btn.isSelected()) {
                gender = "Female";
            }
        });
    }

    public boolean areAllFieldsBlank() {
        boolean is_blank = name_textfield.getText().isEmpty()
                && address_textfield.getText().isEmpty()
                && contact_textfield.getText().isEmpty()
                && father_textfield.getText().isEmpty()
                && mother_textfield.getText().isEmpty()
                && guardian_textfield.getText().isEmpty()
                && previous_school_textfield.getText().isEmpty()
                && roll_textfield.getText().isEmpty();
        return is_blank;
    }

    private boolean areRequiredFieldFilled() {
        boolean filled = !name_textfield.getText().isEmpty()
                && !gender.isEmpty()
                && !(!male_radio_btn.isSelected() && !female_radio_btn.isSelected())
                && !address_textfield.getText().isEmpty()
                && !contact_textfield.getText().isEmpty()
                && !father_textfield.getText().isEmpty()
                && !mother_textfield.getText().isEmpty()
                && !guardian_textfield.getText().isEmpty()
                && !roll_textfield.getText().isEmpty();
        if(dob_datepicker.getValue()!=null){
            filled = filled && true;
        }
        else{
            filled = filled && false;
        }
        return filled;
    }

    private void makeContactTextFieldNumeric() {
        contact_textfield.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = contact_textfield.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9' || ch == '+' || ch == '-' || ch == ',')) {
                        // if it's not number then just setText to previous one
                        contact_textfield.setText(contact_textfield.getText().substring(0, contact_textfield.getText().length() - 1));
                    }
                }
            }
        });

    }

    private void makeRollTextFieldNumeric() {
        roll_textfield.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = roll_textfield.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9')) {
                        // if it's not number then just setText to previous one
                        roll_textfield.setText(roll_textfield.getText().substring(0, roll_textfield.getText().length() - 1));
                    }
                }
            }
        });

    }

    @FXML
    private void clickRegisterBtn(MouseEvent event) {
        if (areRequiredFieldFilled()) {
            String year = LoginController.current_year;
            String name = name_textfield.getText();
            String gender = this.gender;
            String dob = dob_datepicker.getValue().toString();
            String address = address_textfield.getText();
            String phone = contact_textfield.getText();
            String father = father_textfield.getText();
            String mother = mother_textfield.getText();
            String guardian = guardian_textfield.getText();
            String previous_school = "null";
            if (previous_school_textfield.getText() != null) {
                previous_school = previous_school_textfield.getText();
            }
            String grade = grade_combobox.getSelectionModel().getSelectedItem().toString();
            String section = section_combobox.getSelectionModel().getSelectedItem().toString();
            int roll = Integer.valueOf(roll_textfield.getText());
            Connection conn = database.Connection.conn;
            //firstly getting the grade_id
            String query = "SELECT Grade_id FROM year_" + year + "_grade "
                    + "WHERE Grade =? AND Section = ?";
            try {
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, grade);
                pst.setString(2, section);
                ResultSet result = pst.executeQuery();
                int grade_id = 0;
                while (result.next()) {
                    grade_id = result.getInt(1);
                }
                //finally inserting the values
                query = "INSERT INTO year_" + year + "_student(Name,Gender,DOB"
                        + ",Address,Phone,Father_name,Mother_name,Guardian_name"
                        + ",Previous_school,grade,section,Roll,Grade_id) VALUES ("
                        + "?,?,?,?,?,?,?,?,?,?,?,?,?);";
                pst = conn.prepareStatement(query);
                pst.setString(1, name);
                pst.setString(2, gender);
                pst.setString(3, dob);
                pst.setString(4, address);
                pst.setString(5, phone);
                pst.setString(6, father);
                pst.setString(7, mother);
                pst.setString(8, guardian);
                pst.setString(9, previous_school);
                pst.setString(10, grade);
                pst.setString(11, section);
                pst.setInt(12, roll);
                pst.setInt(13, grade_id);
                pst.execute();
                query = "SELECT Max(Student_id) FROM year_" + year + "_student ";
                pst = conn.prepareStatement(query);
                result = pst.executeQuery();
                while (result.next()) {
                    student_id = result.getInt(1);
                }
                Alert a = new Alert(AlertType.INFORMATION);
                a.setHeaderText("Student Registered successfully");
                a.setContentText("The new Student : " + name + " is registered for"
                        + "\n Grade : " + grade + "\nSection : " + section
                        + "\nRoll NO : " + roll + "\nwas given Student_id : "
                        + student_id);
                a.show();
                //clearing all the fiels now
                name_textfield.clear();
                male_radio_btn.setSelected(false);
                female_radio_btn.setSelected(false);
                dob_datepicker.setValue(null);
                address_textfield.clear();
                contact_textfield.clear();
                father_textfield.clear();
                mother_textfield.clear();
                guardian_textfield.clear();
                previous_school_textfield.clear();
                roll_textfield.clear();
                section_combobox.getItems().clear();
                grade_combobox.getItems().clear();
                refresh();
            } catch (Exception e) {
                System.out.println("Exception at clickRegisterBtn() "
                        + "at AddstudentController : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Fill All Required Fields(*)");
            a.setContentText("The fields with * are the required fields");
            a.show();
        }
    }

    @FXML
    private void clickClearAllBtn(MouseEvent event) {
        if (areAllFieldsBlank()) {
            name_textfield.clear();
            male_radio_btn.setSelected(false);
            female_radio_btn.setSelected(false);
            dob_datepicker.setValue(null);
            address_textfield.clear();
            contact_textfield.clear();
            father_textfield.clear();
            mother_textfield.clear();
            guardian_textfield.clear();
            previous_school_textfield.clear();
            roll_textfield.clear();
            section_combobox.getItems().clear();
            grade_combobox.getItems().clear();
            refresh();
        } else {
            Alert a = new Alert(AlertType.CONFIRMATION);
            a.setHeaderText("Seems you are entering the datas !!!");
            a.setContentText("DO you want to clear all the feilds");
            Optional<ButtonType> btn = a.showAndWait();
            if (btn.get() == ButtonType.OK) {
                name_textfield.clear();
                male_radio_btn.setSelected(false);
                female_radio_btn.setSelected(false);
                dob_datepicker.setValue(null);
                address_textfield.clear();
                contact_textfield.clear();
                father_textfield.clear();
                mother_textfield.clear();
                guardian_textfield.clear();
                previous_school_textfield.clear();
                roll_textfield.clear();
                section_combobox.getItems().clear();
                grade_combobox.getItems().clear();
                refresh();
            }
        }
    }
}

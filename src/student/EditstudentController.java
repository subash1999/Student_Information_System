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
import java.util.ResourceBundle;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
public class EditstudentController implements Initializable {

    /**
     * Initializes the controller class.
     */
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
    private JFXTextField roll_textfield;

    @FXML
    private Label roll_no_label;

    @FXML
    private Button undo_all_btn;

    @FXML
    private Button save_btn;

    @FXML
    private ComboBox section_combobox;

    @FXML
    private Label student_id_label;

    public IntegerProperty student_id = new SimpleIntegerProperty();
    private String year = LoginController.current_year;
    private String gender = "";
    private int current_roll = 0;
    private String current_section = " ";
    private String current_grade = " ";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listenerForStudentId();
        makeRollAndSectionCombobox();
        makeRollTextFieldNumeric();
        makeContactTextFieldNumeric();
        formatDatePicker();
        listenerForRoll();

    }

    private void listenerForStudentId() {
        student_id.addListener(e -> {
            if (student_id.get() != 0) {
                int id = student_id.get();
                setValues(id);
            }
        });
    }

    private void refresh() {
        if (student_id.get() != 0) {
            setValues(student_id.get());
        }
    }

    private void setRoll() {
        if (!(grade_combobox.getSelectionModel().getSelectedItem() == null)
                && !(section_combobox.getSelectionModel().getSelectedItem() == null)) {
            try {
                String grade = grade_combobox.getSelectionModel().getSelectedItem().toString();
                String section = section_combobox.getSelectionModel().getSelectedItem().toString();
                if (current_section.equalsIgnoreCase(section)
                        && current_grade.equalsIgnoreCase(grade)) {
                    roll_textfield.setText(String.valueOf(current_roll));
                } else {
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
                }
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
                    setRoll();
                }
            });
        } catch (Exception e) {
            System.out.println("Exception at listenerRollNo() at "
                    + "AddstudentController : " + e.getMessage());
            e.printStackTrace();
        }
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
            String roll_no = roll_textfield.getText();
            if (!roll_no.isEmpty()) {
                try {
                    int roll = Integer.valueOf(roll_textfield.getText());
                    if (roll != current_roll) {
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
                            save_btn.setDisable(true);
                        } else {
                            roll_no_label.setText("√");
                            roll_no_label.setStyle("-fx-text-fill:green;");
                            save_btn.setDisable(false);
                        }
                    } else {
                        roll_no_label.setText("√");
                        roll_no_label.setStyle("-fx-text-fill:green;");
                        save_btn.setDisable(false);
                    }
                } catch (NumberFormatException ex) {
                    System.out.println("Exception Number Format :" + ex.getMessage());
                } catch (Exception ex) {
                    System.out.println("Exception at listenerForRoll() at "
                            + "AddstudentController : " + ex.getMessage());
                    ex.printStackTrace();
                    roll_no_label.setText("X");
                    roll_no_label.setStyle("-fx-text-fill:red;");
                    save_btn.setDisable(true);
                }
            }

        });
    }

    private void setValues(int student_id) {
        int id = student_id;
        String query = "Select Name,Grade,Section,Gender"
                + ",Address,Phone,Father_name,Mother_name,Guardian_name"
                + ",Previous_school"
                + ",Student_id,Roll,Grade_id"
                + ",DOB "
                + " FROM year_" + year + "_student "
                + " WHERE Student_id = ?";
        try {
            Connection conn = database.Connection.conn;
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                student_id_label.setText(String.valueOf(result.getInt("Student_id")));
                name_textfield.setText(result.getString("Name"));
                gender = result.getString("Gender");
                if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("M")) {
                    male_radio_btn.setSelected(true);
                } else if (gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("f")) {
                    female_radio_btn.setSelected(true);
                }
                //getitng the date converting it to local date and assigning it to 
                //date picker
                String dob = result.getString("DOB");
                if (!dob.equals("null")) {
                    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(dob, dateTimeFormatter);
                    dob_datepicker.setValue(date);
                } else {
                    dob_datepicker.setValue(null);
                }
                address_textfield.setText(result.getString("Address"));
                contact_textfield.setText(result.getString("Phone"));
                father_textfield.setText(result.getString("Father_name"));
                mother_textfield.setText(result.getString("Mother_name"));
                guardian_textfield.setText(result.getString("Guardian_name"));
                previous_school_textfield.setText(result.getString("Previous_school"));
                current_grade = result.getString("Grade");
                current_section = result.getString("Section");
                current_roll = result.getInt("Roll");
                grade_combobox.getSelectionModel().select(result.getString("Grade"));
                section_combobox.getSelectionModel().select(result.getString("Section"));
                roll_textfield.setText(String.valueOf(result.getInt("Roll")));
            }
        } catch (Exception e) {
            System.out.println("Exception at setValues() at EditStudentController "
                    + e.getMessage());
        }

    }

    private void updateValues(int student_id) {
        int id = student_id;
        if (areRequiredFieldFilled()) {
            String year = LoginController.current_year;
            String name = name_textfield.getText();
            if (male_radio_btn.isSelected()) {
                this.gender = "Male";
            } else if (female_radio_btn.isSelected()) {
                this.gender = "Female";
            }
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
                //finally updating the values
                query = "UPDATE year_" + year + "_student SET"
                        + " Name = ?,"
                        + " Gender = ?,"
                        + " DOB = ?,"
                        + " Address = ?,"
                        + " Phone = ?,"
                        + " Father_name = ?,"
                        + " Mother_name = ?,"
                        + " Guardian_name = ?,"
                        + " Previous_school = ?,"
                        + " Grade = ?,"
                        + " Section = ?,"
                        + " Roll = ?,"
                        + " Grade_id = ? "
                        + " WHERE Student_id = ? ";
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
                pst.setInt(14, id);
                pst.execute();
                query = "SELECT Max(Student_id) FROM year_" + year + "_student ";
                pst = conn.prepareStatement(query);
                result = pst.executeQuery();
                while (result.next()) {
                    student_id = result.getInt(1);
                }
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText("Student Data Updated Successfully");
                a.setContentText("The Student : " + name + " data is updated for"
                        + "\n Grade : " + grade + "\nSection : " + section
                        + "\nRoll NO : " + roll + "\n Student_id : "
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
                //Stage stage = (Stage) save_btn.getScene().getWindow();
                //stage.close();               
            } catch (Exception e) {
                System.out.println("Exception at updateValues() "
                        + "at EditstudentController : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setHeaderText("Fill All Required Fields(*)");
            a.setContentText("The fields with * are the required fields");
            a.show();
        }
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
        if (dob_datepicker.getValue() != null) {
            filled = filled && true;
        } else {
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

    @FXML
    private void clickUndoAllBtn(MouseEvent event) {
        if (student_id.get() != 0) {
            setValues(student_id.get());
        }
    }

    @FXML
    private void clickSaveBtn(MouseEvent event) {
        if (student_id.get() != 0) {
            updateValues(student_id.get());
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class AddteacherController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextField name;

    @FXML
    private TextField address;

    @FXML
    private TextField phone;

    @FXML
    private RadioButton male;

    @FXML
    private ToggleGroup gender_tooglegroup;

    @FXML
    private RadioButton female;

    @FXML
    private TextField qualification;

    @FXML
    private TableView<TableOfAddTeacherController> table;

    @FXML
    private ChoiceBox subject;

    @FXML
    private ChoiceBox grade;

    @FXML
    private ChoiceBox section;

    @FXML
    private Button add;

    @FXML
    private Button delete;

    @FXML
    private Label label;

    @FXML
    private Label label1;

    private String[] subjects, grades, sections;
    private String gender;
    private ObservableList<ObservableList> list = FXCollections.observableArrayList();
    private ObservableList<TableOfAddTeacherController> ob = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database.Connection.connect();
        label.setVisible(false);
        label1.setVisible(false);
        add.setDisable(true);
        makePhoneTextFieldNumeric();
        choiceBox();
        columnsForTable();
        listnerForToogleGroup();
    }

    public void listnerForToogleGroup() {
        gender_tooglegroup.selectedToggleProperty().addListener(e -> {
            if (male.isSelected()) {
                gender = "Male";
            } else if (female.isSelected()) {
                gender = "Female";
            }
        });

    }

    @FXML
    private void addBtnOnAction(ActionEvent event) {
        TableOfAddTeacherController t = new TableOfAddTeacherController(this.subject.getSelectionModel().getSelectedItem().toString(),
                this.grade.getSelectionModel().getSelectedItem().toString(),
                this.section.getSelectionModel().getSelectedItem().toString());
        String subject = this.subject.getSelectionModel().getSelectedItem().toString();
        String grade = this.grade.getSelectionModel().getSelectedItem().toString();
        String section = this.section.getSelectionModel().getSelectedItem().toString();
        boolean present = false;
        for (TableOfAddTeacherController table : ob) {
            if (table.getSubject().equals(subject)
                    && table.getGrade().equals(grade)
                    && table.getSection().equals(section)) {
                present = true;
            }
        }
        if (!present) {
            ob.add(t);
        }
        table.refresh();
        add.setDisable(true);
    }

    @FXML
    private void deleteBtnOnAction(ActionEvent event) {
        try {
            table.getItems().remove(table.getSelectionModel().getSelectedIndex());
        } catch (Exception e) {

        }
    }

    @FXML
    private void registerBtnOnAction(ActionEvent event) {
        if (name.getText().equals("")
                || "".equals(address.getText())
                || "".equals(phone.getText())
                || (!male.isSelected() && !female.isSelected())) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Fill all the required fields");
            alert.setContentText("The fields with the star(*) sign are the required field."
                    + "Please fill up all the required fields with the correct information to register "
                    + "a new teacher");
            alert.show();
        } else {
            if (male.isSelected()) {
                gender = "Male";
            } else if (female.isSelected()) {
                gender = "Female";
            }
            Connection conn = database.Connection.conn;
            ResultSet result = null;
            String query = null;
            String year = LoginController.current_year;
            try {
                //adding the teacher 
                query = "INSERT INTO year_" + year + "_teacher(Name,Phone,Address,Qualification,Gender) VALUES ('"
                        + name.getText() + "','" + phone.getText() + "','" + address.getText() + "','" + qualification.getText()
                        + "','" + gender + "')";
                conn.createStatement().execute(query);
                //getting the teacher id for the new 
                query = "SELECT Teacher_id FROM year_" + year + "_teacher WHERE Name='"
                        + name.getText() + "' AND Phone='" + phone.getText() + "' AND Address='" + address.getText()
                        + "' AND Qualification='" + qualification.getText()
                        + "' AND Gender ='" + gender + "'";
                result = conn.createStatement().executeQuery(query);
                int teacher_id = 0;
                while (result.next()) {
                    teacher_id = result.getInt(1);
                }
                //verifying if the obtained teacher_id is recently added
                query = "SELECT MAX(Teacher_id) FROM year_" + year + "_teacher";
                result = conn.createStatement().executeQuery(query);
                while (result.next()) {
                    if (teacher_id != result.getInt(1)) {
                        teacher_id = result.getInt(1);
                    }
                }
                for (int i = 0; i < table.getItems().size(); i++) {
                    //getting the subject,grade and section of a row 
                    String subject = table.getItems().get(i).getSubject();
                    String grade = table.getItems().get(i).getGrade();
                    String section = table.getItems().get(i).getSection();
                    //finding the subject_id of the selected subject
                    query = "SELECT Subject_id FROM year_" + year + "_subject "
                            + "WHERE Subject_name = ? AND Grade = ?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, subject);
                    pst.setString(2, grade);
                    result = pst.executeQuery();
                    int subject_id = 0;
                    while (result.next()) {
                        subject_id = result.getInt(1);
                    }
                    //finding the grade_id
                    query = "SELECT Grade_id FROM year_" + year + "_grade "
                            + "WHERE Grade = ? AND Section = ?";
                    pst = conn.prepareStatement(query);
                    pst.setString(1, grade);
                    pst.setString(2, section);
                    result = pst.executeQuery();
                    int grade_id = 0;
                    while (result.next()) {
                        grade_id = result.getInt(1);
                    }
                    //assigning the teacher the subjects
                    query = "INSERT INTO year_" + year + "_teacher_teaches"
                            + "(Teacher_id,Subject_id,Grade_id) VALUES(?,?,?)";
                    pst = conn.prepareStatement(query);
                    pst.setInt(1, teacher_id);
                    pst.setInt(2, subject_id);
                    pst.setInt(3, grade_id);
                    pst.execute();
                }
                name.clear();
                address.clear();
                phone.clear();
                male.setSelected(false);
                female.setSelected(false);
                qualification.clear();
                table.getItems().clear();
                subject.getItems().clear();
                grade.getItems().clear();
                section.getItems().clear();
                choiceBox();
                Alert a = new Alert(AlertType.INFORMATION);
                a.setHeaderText("Registration Successful !!");
                a.setContentText("The task of registering a new teacher was successful");
                a.showAndWait();
            } catch (Exception e) {
                System.out.println("Error at AddteacherController Register button : " + e.getMessage());
            }
        }
    }

    private void choiceBox() {
        String query = null;
        database.Connection.connect();
        ResultSet result = null;
        String year = LoginController.current_year;
        Connection conn = database.Connection.conn;
        //getting the subject name for subject choice box
        query = "SELECT DISTINCT Subject_name  FROM year_" + year + "_subject "
                + "WHERE Active='yes';";
        try {
            result = conn.createStatement().executeQuery(query);
            if (!this.subject.getItems().isEmpty()) {
                this.subject.getItems().clear();
            }
            if (!this.section.getItems().isEmpty()) {
                this.section.getItems().clear();
            }
            if (!this.grade.getItems().isEmpty()) {
                this.grade.getItems().clear();
            }
            while (result.next()) {
                this.subject.getItems().add(result.getString(1));
            }
            this.subject.setDisable(false);
        } catch (Exception e) {
            System.out.println("Exception at choiceBox() while adding subject name "
                    + "to the subject choicebox at AddteacherController : " + e.getMessage());
        }
        //adding the listner for choice box
        this.subject.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (this.subject.getSelectionModel().getSelectedItem() != null) {
                if (!this.grade.getItems().isEmpty()) {
                    this.grade.getItems().clear();
                }
                if (!this.section.getItems().isEmpty()) {
                    this.section.getItems().clear();
                }
                String subj = this.subject.getSelectionModel().getSelectedItem().toString();
                String sql;
                sql = "SELECT DISTINCT year_" + year + "_grade.Grade FROM `year_" + year + "_subject` INNER JOIN  year_" + year + "_grade"
                        + " ON year_" + year + "_subject.Grade=year_" + year + "_grade.Grade "
                        + " LEFT JOIN year_" + year + "_teacher_teaches "
                        + "ON year_" + year + "_teacher_teaches.Grade_id=year_" + year + "_grade.Grade_id  "
                        + "AND year_" + year + "_teacher_teaches.Subject_id=year_" + year + "_subject.Subject_id"
                        + " WHERE year_" + year + "_subject.Subject_name='" + subj + "' "
                        + " AND year_" + year + "_teacher_teaches.Teacher_id IS NULL ";
                Connection connection = database.Connection.conn;
                try {
                    ResultSet res = connection.createStatement().executeQuery(sql);
                    while (res.next()) {
                        this.grade.getItems().add(res.getString(1));
                    }
                    this.grade.setDisable(false);
                    this.add.setDisable(true);
                } catch (Exception ex) {
                    System.out.println("Exception at listener for subject choicebox "
                            + "at choiceBox() at AddteacherController : " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        this.grade.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (!this.grade.getItems().isEmpty()) {
                if (!this.section.getItems().isEmpty()) {
                    this.section.getItems().clear();
                }
                try {
                    String subj = this.subject.getSelectionModel().getSelectedItem().toString();
                    String grd = this.grade.getSelectionModel().getSelectedItem().toString();
                    String sql = "SELECT DISTINCT year_" + year + "_grade.Section FROM `year_" + year + "_subject` "
                            + "INNER JOIN  year_" + year + "_grade"
                            + " ON year_" + year + "_subject.Grade=year_" + year + "_grade.Grade "
                            + " LEFT JOIN year_" + year + "_teacher_teaches "
                            + "ON year_" + year + "_teacher_teaches.Grade_id =year_" + year + "_grade.Grade_id  "
                            + "AND year_" + year + "_teacher_teaches.Subject_id=year_" + year + "_subject.Subject_id"
                            + " WHERE year_" + year + "_subject.Subject_name='" + subj + "' "
                            + " AND year_" + year + "_teacher_teaches.Teacher_id IS NULL "
                            + "AND year_" + year + "_grade.Grade = '" + grd + "' ;";
                    Connection connection = database.Connection.conn;
                    ResultSet res = connection.createStatement().executeQuery(sql);
                    while (res.next()) {
                        this.section.getItems().add(res.getString(1));
                    }
                    this.section.setDisable(false);
                    this.add.setDisable(true);
                } catch (Exception ex) {
                    System.out.println("Exception at listener for grade choicebox "
                            + "at choiceBox() at AddteacherController : " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        this.section.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (!this.section.getItems().isEmpty()) {
                this.add.setDisable(false);
            }
        });
    }

    /*
    private void listnerForChoiceBoxes() {
        String section = this.section.getSelectionModel().getSelectedItem().toString();
        String subject = this.subject.getSelectionModel().getSelectedItem().toString();
        String grade = this.grade.getSelectionModel().getSelectedItem().toString();
        String year = LoginController.current_year;
        String query = "SELECT * FROM year_" + year + "_teacher_teaches NATURAL JOIN "
                + "year_" + year + "_grade WHERE "
                + "year_" + year + "_teacher_teaches.Grade_id = year_" + year + "_grade.Grade_id "
                + "AND Subject_name = '" + subject + "' AND Grade = '" + grade + "' AND "
                + "Section = '" + section + "' ;";

        ResultSet result;
        Connection conn = database.Connection.conn;
        String[] subject_pre, grade_id_pre;
        try {
            result = conn.createStatement().executeQuery(query);
            int i = 0;
            while (result.next()) {
                i++;
            }
            subject_pre = new String[i];
            grade_id_pre = new String[i];
            i = 0;
            result.beforeFirst();
            while (result.next()) {
                grade_id_pre[i] = String.valueOf(result.getInt(1));
                subject_pre[i] = result.getString(2);
                i++;
            }
            query = "SELECT Grade_id FROM year_2074_grade WHERE Grade=" + grade
                    + " AND Section = '" + section + "'";
            result = conn.createStatement().executeQuery(query);
            String grade_id = null;
            while (result.next()) {
                grade_id = String.valueOf(result.getInt(1));
            }
            ObservableList temp_list = FXCollections.observableArrayList();
            temp_list.add(subject);
            temp_list.add(grade);
            temp_list.add(section);
            boolean hasSubject = true;
            if (!list.contains(temp_list)) {
                hasSubject = false;
            }
            boolean bool = true;
            for (int j = 0; j < grade_id_pre.length; j++) {
                if (grade_id.equals(grade_id_pre[j]) && subject.equals(subject_pre[j])) {
                    bool = false;
                }
            }
            if (hasSubject) {
                if (bool) {
                    add.setDisable(false);
                    label.setVisible(false);
                    label1.setVisible(false);
                } else {
                    label.setVisible(true);
                    label1.setVisible(false);
                    add.setDisable(true);
                }
            } else {
                add.setDisable(true);
                label.setVisible(false);
                label1.setVisible(true);
            }
        } catch (Exception e) {
            System.out.println("listnerForChoiceBoxes() Getting the already present subject : " + e.getMessage());
            label.setDisable(true);
        }

    }
     */
    private void columnsForTable() {
        TableColumn<TableOfAddTeacherController, String> subject_col = new TableColumn<>("Subject");
        subject_col.setCellValueFactory(new PropertyValueFactory("subject"));
        table.getColumns().add(subject_col);
        TableColumn<TableOfAddTeacherController, String> grade_col = new TableColumn<>("Grade");
        grade_col.setCellValueFactory(new PropertyValueFactory("grade"));
        table.getColumns().add(grade_col);
        TableColumn<TableOfAddTeacherController, String> section_col = new TableColumn<>("Section");
        section_col.setCellValueFactory(new PropertyValueFactory("section"));
        table.getColumns().add(section_col);
        table.setItems(ob);
        //making table columns resizeable
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //making the table menu button visible which will help to show or hide columns
        table.setTableMenuButtonVisible(true);
    }

    private void makePhoneTextFieldNumeric() {
        phone.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = phone.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9' || ch=='+'||ch=='-' || ch==',')) {
                        // if it's not number then just setText to previous one
                        phone.setText(phone.getText().substring(0, phone.getText().length() - 1));
                    }
                }
            }
        });

    }

    public static class TableOfAddTeacherController {

        public StringProperty subject;
        public StringProperty grade;
        public StringProperty section;

        TableOfAddTeacherController(String subject, String grade, String section) {
            this.subject = new SimpleStringProperty(subject);
            this.grade = new SimpleStringProperty(grade);
            this.section = new SimpleStringProperty(section);
        }

        public String getSubject() {
            return subject.get();
        }

        public String getGrade() {
            return grade.get();
        }

        public String getSection() {
            return section.get();
        }

        public void setSubject(String subject) {
            this.subject.set(subject);
        }

        public void setGrade(String grade) {
            this.grade.set(grade);
        }

        public void setSection(String section) {
            this.section.set(section);
        }

    }

}

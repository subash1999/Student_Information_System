/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class SettingsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private AnchorPane session_anchorpane;

    @FXML
    public TableView session_table;

    @FXML
    private Button session_delete;

    @FXML
    private Button session_add;

    @FXML
    private AnchorPane port_anchorpane;

    @FXML
    private TableView grade_table;

    @FXML
    private Button grade_add;

    @FXML
    private TextField grade_section;

    @FXML
    private TextField grade_grade;

    @FXML
    private Button grade_delete;

    @FXML
    private TableView subject_table;

    @FXML
    private Button subject_delete;

    @FXML
    private TextField subject_subject_name;

    @FXML
    private Button subject_add;

    @FXML
    private ChoiceBox subject_grade;

    @FXML
    private TableView division_table;

    @FXML
    private Button division_delete;

    @FXML
    private TextField division_division;

    @FXML
    private TextField division_from;

    @FXML
    private TextField division_to;

    @FXML
    private TextField division_division_name;

    @FXML
    private Button division_add;

    @FXML
    private TableView grading_table;

    @FXML
    private Button grading_delete;

    @FXML
    private TextField grading_grade_letter;

    @FXML
    private TextField grading_from;

    @FXML
    private TextField grading_to;

    @FXML
    private TextField grading_grade_point;

    @FXML
    public TabPane settings_tabpane;

    @FXML
    public AnchorPane subject_modify_anchorpane;

    @FXML
    public AnchorPane grade_modify_anchorpane;

    private ObservableList<ObservableList> grade_data = FXCollections.observableArrayList();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        session();
        grade();
        subject();
        division();
        grading();
        if (!LoginController.user_type.equalsIgnoreCase("Admin")) {
            session_anchorpane.setVisible(false);
            grade_add.setVisible(false);
            grade_section.setVisible(false);
            grade_grade.setVisible(false);
            grade_delete.setVisible(false);
            subject_delete.setVisible(false);
            subject_subject_name.setVisible(false);
            subject_add.setVisible(false);
            subject_grade.setVisible(false);
            subject_modify_anchorpane.setVisible(false);
            grade_modify_anchorpane.setVisible(false);
        }
    }

//Session Management
    private void session() {
        makeSessionTable();
    }

    @FXML
    private void addSessionBtnClicked(MouseEvent event) {
        try {
            Stage window = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/settings/newSession/addsession1.fxml"));
            //Parent root = FXMLLoader.load(getClass().getResource("/login/login.fxml"));
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.setResizable(false);
            window.toFront();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setOnCloseRequest(f -> {
                Stage stage = new Stage();
                try {
                    Parent parent = FXMLLoader.load(getClass().getResource("/settings/settings.fxml"));
                    Scene sc = new Scene(parent);
                    stage.setScene(sc);
                    stage.show();
                } catch (IOException ex) {
                    System.out.println("Exception at addSessionBtnClicked() "
                            + "at SettingsController : " + ex.getMessage());
                    ex.printStackTrace();
                }
            });
            window.show();
            window = (Stage) session_add.getScene().getWindow();
            window.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("File not found" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }

    }

    @FXML
    private void deleteSessionBtnClicked(MouseEvent e) {
        ObservableList<TableColumn> cols = session_table.getColumns();
        TableColumn col = cols.get(0);
        String id = (String) col.getCellData(session_table.getSelectionModel().getSelectedIndex());
        if (id != null) {
            String year = (String) cols.get(1).getCellData(session_table.getSelectionModel().getSelectedIndex());
            Alert al = new Alert(Alert.AlertType.CONFIRMATION);
            al.setHeaderText("Are you sure you want to delete session with id : " + id);
            al.setContentText("The all the information related to this session will be permanently deleted");
            Optional<ButtonType> res = al.showAndWait();
            ResultSet result = null;
            if (res.get() == ButtonType.OK) {
                Connection conn = database.Connection.conn;
                try {

                    Statement statement = conn.createStatement();
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_student`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_grading`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_percentage`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_marks`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_teacher_teaches`;");
                    statement.executeBatch();
                    Statement st = conn.createStatement();
                    String query = "SELECT Table_name FROM `year_" + year + "_ledger";
                    result = st.executeQuery(query);
                    while (result.next()) {
                        String table = result.getString(1);
                        statement.addBatch("DROP TABLE IF EXISTS `" + table + "`;");
                    }
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_ledger`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_exam`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_grade`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_subject`;");
                    statement.addBatch("DROP TABLE IF EXISTS `Year_" + year + "_teacher`;");
                    statement.addBatch("DELETE FROM `session` WHERE session.Year='" + year + "';");
                    statement.addBatch("DELETE FROM `table_details` WHERE table_details.Year='" + year + "';");
                    statement.executeBatch();
                    ObservableList temp = FXCollections.observableArrayList();
                    temp = session_table.getItems();
                    temp.removeAll(session_table.getSelectionModel().getSelectedItems());
                    session_table.setItems(temp);
                } catch (Exception ex) {
                    System.out.println("Exception at onDeleteBtnClicked :" + ex.getMessage());
                }
            }
        }
    }

    protected void makeSessionTable() {
        database.Connection.connect();

        Connection conn = database.Connection.conn;

        //SQL FOR SELECTING ALL OF session
        String SQL;
        ResultSet rs = null;
        if (session_table != null) {
            session_table.getColumns().clear();
            session_table.getItems().clear();
        }
        data.clear();
        try {
            SQL = "SELECT * FROM `session`";

            rs = conn.createStatement().executeQuery(SQL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Query Error");
        }

        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                session_table.getColumns().addAll(col);
            }
        } catch (SQLException ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

                    row.add(rs.getString(i));

                }
                if (!"0".equals(row.get(1))) {
                    data.add(row);
                }

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        session_table.setItems(data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        session_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        session_table.setTableMenuButtonVisible(true);
    }

///////////////////////////////////////////////////////////////////////////////////   
////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////    
//Grade Management
    private void grade() {
        makeGradeTable();
    }

    @FXML
    private void addGradeBtnClicked(MouseEvent event) {
        String grade = grade_grade.getText();
        String section = grade_section.getText();
        String year = LoginController.current_year;
        if (!grade.isEmpty()) {
            try {
                //check if the grade and section is already present
                String query = "SELECT * FROM year_" + year + "_grade WHERE Grade = ? AND Section = ?";
                Connection conn = database.Connection.conn;
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, grade);
                if (section.isEmpty()) {
                    section = "null";
                }
                pst.setString(2, section);
                ResultSet result = pst.executeQuery();
                boolean already_present = false;
                while (result.next()) {
                    already_present = true;
                }
                if (already_present) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("Grade already Present");
                    alert.setContentText("The grade and section is already present");
                    alert.show();
                } else {
                    if (section.isEmpty()) {
                        section = "null";
                    }
                    query = "INSERT INTO year_" + year + "_grade(Grade,Section)"
                            + " VALUES "
                            + "('" + grade + "' , '" + section + "')";
                    conn.createStatement().execute(query);
                    grade_grade.clear();
                    grade_section.clear();
                    grade_table.refresh();
                    grade_table.getColumns().clear();
                    grade_table.getItems().clear();
                }
            } catch (Exception e) {
                System.out.println("add Grade Btn clicked in settings :" + e.getMessage());
            } finally {
                makeGradeTable();
                subject();
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Please Enter Grade");
            alert.setContentText("Enter Grade and section. If there is no "
                    + "section leave it empty, "
                    + "the system will assign 'null' value to the section ");
            alert.show();
        }
    }

    @FXML
    private void deleteGradeBtnClicked(MouseEvent event) {
        ObservableList<TableColumn> cols = grade_table.getColumns();
        TableColumn col = cols.get(0);
        String id = (String) col.getCellData(grade_table.getSelectionModel().getSelectedIndex());
        if (id != null) {
            Alert al = new Alert(Alert.AlertType.CONFIRMATION);
            al.setHeaderText("Are you sure you want to delete grade with id : " + id);
            al.setContentText("All the information related to this grade will be permanently deleted."
                    + "All students, grade,subjects,Marks record i.e ledger"
                    + "will be deleted and the teachers will be unassigned to the grade."
                    + "");
            Optional<ButtonType> res = al.showAndWait();
            if (res.get() == ButtonType.OK) {
                Connection conn = database.Connection.conn;
                try {
                    String year = LoginController.current_year;
                    Statement statement = conn.createStatement();
                    //the commented region also deletes the ledger of the class which we want to prevent
//                String query = "SELECT Table_name FROM `year_" + year + "_ledger` WHERE Grade_id = " + id + ";";
//                result = st.executeQuery(query);
//                while (result.next()) {
//                    String table = result.getString(1);
//                    statement.addBatch("DROP TABLE `" + table + "`;");
//                }

                    String query = "SELECT Grade FROM year_" + year + "_grade  "
                            + "WHERE Grade IN (SELECT Grade FROM "
                            + "year_" + year + "_grade WHERE Grade_id = " + id + ") "
                            + "AND Grade_id != " + id + " ;";
                    ResultSet result = conn.createStatement().executeQuery(query);
                    if (!result.next()) {
                        query = "UPDATE  year_" + year + "_subject "
                                + "SET Active = 'No' "
                                + " WHERE Grade IN (SELECT  Grade "
                                + "FROM year_" + year + "_grade "
                                + "WHERE Grade_id = " + id + ") ;";
                        statement.addBatch(query);
                    }
                    query = "DELETE FROM year_" + year + "_teacher_teaches "
                            + "WHERE Grade_id = " + id + " ;";
                    statement.addBatch(query);
                    statement.addBatch("UPDATE year_" + year + "_student SET "
                            + "Active='No' WHERE Grade_id = " + id + " "
                            + " ;");
                    statement.addBatch("DELETE FROM year_" + year + "_grade WHERE "
                            + "Grade_id = " + id + " ;");
                    statement.executeBatch();
                    grade_table.refresh();
                    grade_table.getColumns().clear();
                    grade_table.getItems().clear();
                    makeGradeTable();
                } catch (Exception e) {
                    System.out.println("Exception at delete Grade button :" + e.getMessage());
                } finally {
                    makeGradeTable();
                    subject();
                }
            }
        }
    }

    public void makeGradeTable() {
        database.Connection.connect();

        Connection conn = database.Connection.conn;

        //SQL FOR SELECTING ALL OF STUDENT
        String SQL;
        ResultSet rs = null;
        grade_table.getColumns().clear();
        grade_table.getItems().clear();
        grade_data.clear();
        try {
            String year = LoginController.current_year;
            SQL = "SELECT * FROM year_" + year + "_grade";

            rs = conn.createStatement().executeQuery(SQL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Query Error");
        }

        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });
                if ("Male".equals(col.getText())
                        || "Female".equals(col.getText())
                        || "No_of_students".equals(col.getText())) {
                    col.setVisible(false);
                }
                grade_table.getColumns().addAll(col);
            }
        } catch (SQLException ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

                    row.add(rs.getString(i));

                }
                grade_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        grade_table.setItems(grade_data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        grade_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        grade_table.setTableMenuButtonVisible(true);
    }

///////////////////////////////////////////////////////////////////////////////////   
////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
// Subject Management
    @FXML
    private ChoiceBox subject_grade_filter;

    @FXML
    private Label subject_label;

    private ObservableList<ObservableList> subject_data = FXCollections.observableArrayList();

    private void subject() {
        subject_label.setVisible(false);
        makeSubjectTable();
        gradeFilterChoiceBox();
        gradeChoiceBox();
        listeningChangesWhileAddingSubject();
    }

    private void makeSubjectTable() {
        database.Connection.connect();

        Connection conn = database.Connection.conn;

        //SQL FOR SELECTING ALL OF STUDENT
        String SQL;
        ResultSet rs = null;
        subject_table.getColumns().clear();
        subject_table.getItems().clear();
        subject_data.clear();
        try {
            String year = LoginController.current_year;
            SQL = "SELECT Subject_id,Subject_name,Grade "
                    + " FROM year_" + year + "_subject "
                    + "WHERE Active = 'yes'";

            rs = conn.createStatement().executeQuery(SQL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Query Error");
        }

        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });
                if ("Grade_id".equals(col.getText())
                        || "Teacher_id".equals(col.getText())
                        || "No_of_students".equals(col.getText())
                        || "Active".equalsIgnoreCase(col.getText())) {
                    col.setVisible(false);
                }
                subject_table.getColumns().addAll(col);
            }
        } catch (SQLException ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

                    row.add(rs.getString(i));

                }
                subject_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        subject_table.setItems(subject_data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        subject_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        subject_table.setTableMenuButtonVisible(true);
        subject_table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void gradeFilterChoiceBox() {
        Connection conn = database.Connection.conn;

        String query;
        ResultSet rs = null;
        try {
            subject_grade_filter.getItems().clear();
            subject_grade_filter.getItems().add("All");
            String year = LoginController.current_year;
            query = "SELECT DISTINCT Grade FROM year_" + year + "_grade ;";
            rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                subject_grade_filter.getItems().add(rs.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Exception at gradeFilterchoiceBox() SettingsController "
                    + ": " + e.getMessage());
        }
        subject_grade_filter.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
                if (!subject_grade_filter.getSelectionModel().getSelectedItem().toString().equalsIgnoreCase("all")) {
                    ObservableList tableItems = FXCollections.observableArrayList();
                    ObservableList<TableColumn> cols = subject_table.getColumns();
                    for (int i = 0; i < subject_data.size(); i++) {
                        TableColumn col = new TableColumn();
                        for (TableColumn c : cols) {
                            if ("Grade".equalsIgnoreCase(c.getText())) {
                                col = c;
                            }
                        }
                        String cellValue = col.getCellData(subject_data.get(i)).toString();

                        cellValue = cellValue.toLowerCase();

                        if (cellValue.contains(subject_grade_filter.getSelectionModel().getSelectedItem().toString().toLowerCase())) {

                            tableItems.add(subject_data.get(i));
                        }

                    }
                    subject_table.setItems(tableItems);
                } else {
                    subject_table.setItems(subject_data);
                }
            }
        });

    }

    private void gradeChoiceBox() {
        Connection conn = database.Connection.conn;

        String query;
        ResultSet rs = null;
        try {
            subject_grade.getItems().clear();
            String year = LoginController.current_year;
            query = "SELECT DISTINCT Grade FROM year_" + year + "_grade ;";
            rs = conn.createStatement().executeQuery(query);
            while (rs.next()) {
                subject_grade.getItems().add(rs.getString(1));
            }
            subject_grade.getSelectionModel().selectFirst();
        } catch (Exception e) {
            System.out.println("Exception at gradeFilterchoiceBox() SettingsController "
                    + ": " + e.getMessage());
        }
    }

    @FXML
    private void addSubjectBtnClicked(MouseEvent event) {
        String subject = subject_subject_name.getText();
        if (!subject.isEmpty()) {
            Connection conn = database.Connection.conn;
            ResultSet result;
            String query;
            String year = LoginController.current_year;
            try {
                String grade = subject_grade.getSelectionModel().getSelectedItem().toString();
                //check if the grade and section is already present
                query = "SELECT * FROM year_" + year + "_subject WHERE Grade = ? AND Subject_name = ? ";
                conn = database.Connection.conn;
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, grade);
                pst.setString(2, subject);
                result = pst.executeQuery();
                boolean already_present = false;
                while (result.next()) {
                    already_present = true;
                }
                if (already_present) {

                    query = "UPDATE year_" + year + "_subject SET Active = 'yes', "
                            + "Subject_name = ?"
                            + " WHERE Grade = ? AND Subject_name = ? ";
                    pst = conn.prepareStatement(query);
                    pst.setString(1, subject);
                    pst.setString(2, grade);
                    pst.setString(3, subject);
                    pst.execute();
                } else {
                    query = "INSERT INTO year_" + year + "_subject "
                            + "(Subject_name,Grade) VALUES ('" + subject + "','" + grade + "')";
                    conn.createStatement().executeUpdate(query);
                    subject_subject_name.clear();
                    subject_grade.getSelectionModel().selectFirst();
                }
            } catch (Exception e) {
                System.out.println("Exception at addSubjectBtnClicked() "
                        + "at SettingsController : " + e.getMessage());
            } finally {
                makeSubjectTable();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Fill all the fields");
            a.show();
        }
    }

    private void listeningChangesWhileAddingSubject() {
        subject_subject_name.textProperty().addListener(e -> {
            listnerForAddingSubject();
        });
        subject_grade.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
                listnerForAddingSubject();
            }
        });

    }

    private void listnerForAddingSubject() {
        Connection conn = database.Connection.conn;
        ResultSet result;
        String query;
        String subject = subject_subject_name.getText();
        try {
            String grade = subject_grade.getSelectionModel().getSelectedItem().toString();
            String year = LoginController.current_year;
            query = "SELECT * FROM year_" + year + "_subject WHERE Grade = '" + grade + "' AND "
                    + "Subject_name = '" + subject + "' AND Active = 'yes' ; ";
            result = conn.createStatement().executeQuery(query);
            if (result.next()) {
                subject_label.setVisible(true);
                subject_add.setDisable(true);
            } else {
                subject_label.setVisible(false);
                subject_add.setDisable(false);
            }
        } catch (Exception e) {
            System.out.println("Exception at listnerForAddingSubject() at "
                    + "SettingController : " + e.getMessage());
        }
    }

    @FXML
    private void deleteSubjectBtnClicked(MouseEvent event) {
        Connection conn = database.Connection.conn;
        ResultSet result;
        String query;
        try {
            String year = LoginController.current_year;
            int index = subject_table.getSelectionModel().getSelectedIndex();
            TableColumn col = (TableColumn) subject_table.getColumns().get(0);
            String id = col.getCellData(index).toString();
            String grade = subject_grade.getSelectionModel().getSelectedItem().toString();
            if (id != null) {
                Alert al = new Alert(Alert.AlertType.CONFIRMATION);
                al.setHeaderText("Are you sure you want to delete Subject with id : " + id);
                al.setContentText("The subject when deleted will result in unassigning"
                        + " teachers to the subject also");
                Optional<ButtonType> res = al.showAndWait();
                if (res.get() == ButtonType.OK) {
                    query = "UPDATE  year_" + year + "_subject "
                            + "SET Active = 'No' "
                            + " WHERE Subject_id = " + id + " ;";
                    Statement st = conn.createStatement();
                    st.addBatch(query);
                    query = "DELETE FROM year_" + year + "_teacher_teaches "
                            + "WHERE Subject_id = " + id + " ;";
                    st.addBatch(query);
                    st.executeBatch();
                    makeSubjectTable();
                }
            }
        } catch (Exception e) {
            System.out.println("Exception at deleteSubjectBtnClicked() "
                    + "at SettingsController : " + e.getMessage());
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////   
////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
// Division Management
    private ObservableList<ObservableList> division_data = FXCollections.observableArrayList();

    private void division() {
        makeDivisionTable();
    }

    private void makeDivisionTable() {
        database.Connection.connect();

        Connection conn = database.Connection.conn;

        //SQL FOR SELECTING ALL OF STUDENT
        String SQL;
        ResultSet rs = null;
        division_table.getItems().clear();
        division_table.getColumns().clear();
        division_data.clear();
        try {
            String year = LoginController.current_year;
            SQL = "SELECT *"
                    + " FROM year_" + year + "_percentage "
                    + "ORDER BY `from`";

            rs = conn.createStatement().executeQuery(SQL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Query Error");
        }

        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                division_table.getColumns().addAll(col);
            }
        } catch (SQLException ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

                    row.add(rs.getString(i));

                }
                division_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        division_table.setItems(division_data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        division_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        division_table.setTableMenuButtonVisible(true);

    }

    @FXML
    private void addDivisionBtnClicked(MouseEvent event) {
        String division = division_division.getText();
        String from = division_from.getText();
        String to = division_to.getText();
        String division_name = division_division_name.getText();
        String year = LoginController.current_year;
        if (!division.isEmpty() && !from.isEmpty() && !to.isEmpty()
                && !division_name.isEmpty()) {
            Connection conn = database.Connection.conn;
            try {
                //check if the grade and section is already present
                String query = "SELECT * FROM year_" + year + "_percentage WHERE `from` = ? "
                        + "OR `to` = ? OR `division` = ? OR `Division_name` = ?";
                conn = database.Connection.conn;
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, from);
                pst.setString(2, to);
                pst.setString(3, division);
                pst.setString(4, division_name);
                ResultSet result = pst.executeQuery();
                boolean already_present = false;
                while (result.next()) {
                    already_present = true;
                }
                if (already_present) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("Division already Present");
                    alert.setContentText("One or many of the from,to,Division or Division_name "
                            + " is already persent. \n All the values must be unique");
                    alert.show();
                } else {

                    query = "INSERT INTO year_" + year + "_percentage VALUES "
                            + "('" + division + "','" + from + "','" + to + "','" + division_name + "')";
                    conn.createStatement().executeUpdate(query);

                }
            } catch (Exception e) {
                System.out.println("Exception at addDivisionBtnClicked() at "
                        + "SettingsController : " + e.getMessage());
            } finally {
                makeDivisionTable();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Fill all the fields");
            a.show();
        }
    }

    @FXML
    private void deleteDivisionBtnClicked(MouseEvent event) {
        Connection conn = database.Connection.conn;
        ResultSet result;
        String query;
        try {
            int index = division_table.getSelectionModel().getSelectedIndex();
            TableColumn col = (TableColumn) division_table.getColumns().get(0);
            String division = col.getCellData(index).toString();
            String year = LoginController.current_year;
            query = "DELETE FROM  year_" + year + "_percentage "
                    + " WHERE Division = '" + division + "' ;";
            conn.createStatement().executeUpdate(query);
            makeDivisionTable();
        } catch (Exception e) {
            System.out.println("Exception at deleteDivisionBtnClicked() "
                    + "at SettingsController : " + e.getMessage());
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////   
////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////
// Grading Management
    private ObservableList<ObservableList> grading_data = FXCollections.observableArrayList();

    private void grading() {
        makeGradingTable();
    }

    private void makeGradingTable() {
        database.Connection.connect();

        Connection conn = database.Connection.conn;

        //SQL FOR SELECTING ALL OF STUDENT
        String SQL;
        ResultSet rs = null;
        grading_table.getColumns().clear();
        grading_table.getItems().clear();
        try {
            String year = LoginController.current_year;
            SQL = "SELECT *"
                    + " FROM year_" + year + "_grading "
                    + "ORDER BY per_from";

            rs = conn.createStatement().executeQuery(SQL);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Query Error");
        }

        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                grading_table.getColumns().addAll(col);
            }
        } catch (SQLException ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (rs.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {

                    row.add(rs.getString(i));

                }
                grading_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        grading_table.setItems(grading_data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        grading_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        grading_table.setTableMenuButtonVisible(true);

    }

    @FXML
    private void addGradingBtnClicked(MouseEvent event) {
        String grade_letter = grading_grade_letter.getText();
        String from = grading_from.getText();
        String to = grading_to.getText();
        String grade_point = grading_grade_point.getText();
        if (!grade_letter.isEmpty() && !from.isEmpty() && !to.isEmpty()
                && !grade_point.isEmpty()) {
            Connection conn = database.Connection.conn;
            try {
                String year = LoginController.current_year;
                //check if the grade and section is already present
                String query = "SELECT * FROM year_" + year + "_grading WHERE Per_from = ? OR per_to = ? "
                        + "OR grade_letter = ? OR grade_point=?";
                conn = database.Connection.conn;
                PreparedStatement pst = conn.prepareStatement(query);
                pst.setString(1, from);
                pst.setString(2, to);
                pst.setString(3, grade_letter);
                pst.setString(4, grade_point);
                ResultSet result = pst.executeQuery();
                boolean already_present = false;
                while (result.next()) {
                    already_present = true;
                }
                if (already_present) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("Grading already Present");
                    alert.setContentText("One or many of from,to,grade_letter or grade_point "
                            + "is repeated.\n All values must be unique");
                    alert.show();
                } else {
                    query = "INSERT INTO year_" + year + "_grading VALUES "
                            + "('" + from + "','" + to + "','" + grade_letter + "','" + grade_point + "')";
                    conn.createStatement().executeUpdate(query);
                    grading_grade_letter.clear();
                    grading_from.clear();
                    grading_to.clear();
                    grading_grade_point.clear();

                }
            } catch (Exception e) {
                System.out.println("Exception at addGradingBtnClicked() at "
                        + "SettingsController : " + e.getMessage());
            } finally {
                makeGradingTable();
            }
        } else {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Fill all the fields");
            a.show();
        }
    }

    @FXML
    private void deleteGradingBtnClicked(MouseEvent event) {
        Connection conn = database.Connection.conn;
        ResultSet result;
        String query;
        try {
            int index = grading_table.getSelectionModel().getSelectedIndex();
            TableColumn col = (TableColumn) grading_table.getColumns().get(2);
            String grade_letter = col.getCellData(index).toString();
            String year = LoginController.current_year;
            query = "DELETE FROM  year_" + year + "_grading "
                    + " WHERE Grade_letter = '" + grade_letter + "' ;";
            conn.createStatement().executeUpdate(query);
            makeGradingTable();
        } catch (Exception e) {
            System.out.println("Exception at deleteGradingBtnClicked() "
                    + "at SettingsController : " + e.getMessage());
        }
    }

}

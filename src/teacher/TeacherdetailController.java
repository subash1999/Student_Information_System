/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class TeacherdetailController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private MenuBar menubar;

    @FXML
    private JFXTextField search_field;

    @FXML
    private TableView teacher_table;

    @FXML
    private Label teacher_id;

    @FXML
    private Label name;

    @FXML
    private Label gender;

    @FXML
    private Label address;

    @FXML
    private Label phone;

    @FXML
    private ComboBox subject;

    @FXML
    private ComboBox grade;

    @FXML
    private ComboBox section;

    @FXML
    private Button add_btn;

    @FXML
    private Button delete_selected_btn;

    @FXML
    private TableView subject_table;

    @FXML
    private Button edit_btn;

    TeacherIdStore store_teacher_id = new TeacherIdStore();
    ObservableList<ObservableList> subject_data = FXCollections.observableArrayList();
    ObservableList<ObservableList> teacher_data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        display_given_teacher();
        //display_subject_table();
        display_teacher_table();
        searchTable();
        onTeacherTableRowClick();
    }

    public void setTeacherId(String s) {
        store_teacher_id.set(s);
    }

    public void searchTable() {
        search_field.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {

                if (search_field.getText() == null) {

                    teacher_table.setItems(teacher_data);

                    return;

                }
                if (search_field.getText().isEmpty()) {

                    teacher_table.setItems(teacher_data);

                    return;

                }
                ObservableList tableItems = FXCollections.observableArrayList();
                ObservableList<TableColumn> cols = teacher_table.getColumns();
                for (int i = 0; i < teacher_data.size(); i++) {
                    TableColumn col = new TableColumn();
                    for (int j = 0; j < cols.size(); j++) {
                        col = cols.get(j);
                        if (col.isVisible()) {
                            String cellValue = col.getCellData(teacher_data.get(i)).toString();

                            cellValue = cellValue.toLowerCase();

                            if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                tableItems.add(teacher_data.get(i));

                                break;

                            }
                        }

                    }
                }
                teacher_table.setItems(tableItems);
            }
        });

    }

    public void display_given_teacher() {
        teacher_id.setText("Teacher ID : " + store_teacher_id.get());
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        try {
            String year = LoginController.current_year;
            query = "select Name,Gender,Phone,Address from Year_" + year + "_teacher where Teacher_id=" + store_teacher_id.get()
                    + " AND Active='yes'";
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                name.setText("Name : " + result.getString("Name"));
                gender.setText("Gender : " + result.getString("Gender"));
                phone.setText("Phone : " + result.getString("Phone"));
                address.setText("Address : " + result.getString("Address"));
            }

        } catch (Exception e) {
            System.out.println("Exception in assigning given teacher : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void display_teacher_table() {
        teacher_table.getItems().clear();
        teacher_table.getColumns().clear();
        teacher_data.clear();
        Connection conn = database.Connection.conn;
        String year = LoginController.current_year;
        String query = "SELECT Teacher_id,Name,Gender,Phone,Address FROM  Year_" + year + "_teacher "
                + "WHERE Active = 'yes'";
        ResultSet rs = null;
        try {
            rs = conn.createStatement().executeQuery(query);

        } catch (Exception e) {
            System.out.println("Displaying teacher table  error while executing query :" + e.getMessage());
        }
        /**
         * ********************************
         * TABLE COLUMN ADDED DYNAMICALLY * ********************************
         */
        try {
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

                if ("Gender".equals(col.getText()) || "Address".equals(col.getText())
                        || "Phone".equals(col.getText())) {
                    col.setVisible(false);
                }
                teacher_table.getColumns().addAll(col);

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
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                teacher_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        teacher_table.setItems(teacher_data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        teacher_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        teacher_table.setTableMenuButtonVisible(true);
    }

    private void onTeacherTableRowClick() {
        teacher_table.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ObservableList<TableColumn> cols = teacher_table.getColumns();
                    TableColumn col = cols.get(0);
                    ObservableList<ObservableList> table_data = FXCollections.observableArrayList();
                    table_data = teacher_table.getItems();
                    store_teacher_id.set(col.getCellData(row.getIndex()).toString());
                    //the two code below do the same job as the code above
                    //store_student_id = col.getCellData(table.getSelectionModel().getSelectedIndex()).toString();
                    //store_student_id = col.getCellData(table_data.get(table.getSelectionModel().getSelectedIndex())).toString();
                }
            });
            return row;
        });
    }

    public void display_subject_table() {
        subject_table.getItems().clear();
        subject_table.getColumns().clear();
        subject_data.clear();
        Connection conn = database.Connection.conn;
        String query = "SET @a=0 ; ";
        ResultSet rs = null;
        try {
            String year = LoginController.current_year;
            Statement st = conn.createStatement();
            st.execute(query);
            query = "SELECT @a:=@a+1 AS 'S.N',year_" + year + "_subject.Subject_name "
                    + "AS Subject "
                    + ",year_" + year + "_grade.Grade "
                    + ",year_" + year + "_grade.Section FROM "
                    + "year_" + year + "_teacher "
                    + "INNER JOIN year_" + year + "_teacher_teaches "
                    + "ON year_" + year + "_teacher.Teacher_id = "
                    + " year_" + year + "_teacher_teaches.Teacher_id "
                    + " AND year_" + year + "_teacher.Teacher_id = " + store_teacher_id.get()
                    + " INNER JOIN year_" + year + "_subject ON "
                    + " year_" + year + "_subject.Subject_id = "
                    + "year_" + year + "_teacher_teaches.Subject_id "
                    + "INNER JOIN year_" + year + "_grade "
                    + "ON year_" + year + "_grade.Grade_id = "
                    + "year_" + year + "_teacher_teaches.Grade_id ;";
            rs = conn.createStatement().executeQuery(query);

        } catch (Exception e) {
            System.out.println("Displaying subject of a teacher error :" + e.getMessage());
        }
        /**
         * ********************************
         * TABLE COLUMN ADDED DYNAMICALLY * ********************************
         */
        try {
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
                    //Iterate Column
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

    }

    private void comboboxes() {
        String query = null;
        database.Connection.connect();
        ResultSet result = null;
        String year = LoginController.current_year;
        Connection conn = database.Connection.conn;
        //getting the subject name for subject choice box
        query = "SELECT DISTINCT Subject_name  FROM year_" + year + "_subject "
                + " WHERE Active='yes';";
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
                    this.add_btn.setDisable(true);
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
                    this.add_btn.setDisable(true);
                } catch (Exception ex) {
                    System.out.println("Exception at listener for grade choicebox "
                            + "at choiceBox() at AddteacherController : " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        this.section.getSelectionModel().selectedItemProperty().addListener(e -> {
            if (!this.section.getItems().isEmpty()) {
                this.add_btn.setDisable(false);
            }
        });
    }

    private void refresh() {
        display_teacher_table();
        display_given_teacher();
        display_subject_table();
        String search = search_field.getText();
        search_field.setText(null);
        search_field.setText(search);
    }

    @FXML
    void clickAddBtn(MouseEvent event) {
        try {
            if (this.section.getSelectionModel().getSelectedItem() != null
                    && this.grade.getSelectionModel().getSelectedItem() != null
                    && this.subject.getSelectionModel().getSelectedItem() != null) {
                String year = LoginController.current_year;
                Connection conn = database.Connection.conn;
                String subject = this.subject.getSelectionModel().getSelectedItem().toString();
                String grade = this.grade.getSelectionModel().getSelectedItem().toString();
                String section = this.section.getSelectionModel().getSelectedItem().toString();
                int teacher_id = Integer.valueOf(store_teacher_id.get());

                ResultSet result = null;
                String query = "SELECT Subject_id FROM year_" + year + "_subject "
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
                display_subject_table();
                add_btn.setDisable(true);
            }
        } catch (Exception e) {
            System.out.println("Exception at clickAddBtn() "
                    + "at TeacherdetailController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void clickDeleteSelectedBtn(MouseEvent event) {
        try{
            if(subject_table.getSelectionModel().getSelectedItem()!=null){
                int teacher_id = Integer.valueOf(store_teacher_id.get());
                int subject_id = 0;
                int grade_id = 0;
                int index = subject_table.getSelectionModel().getSelectedIndex();
                TableColumn col = (TableColumn) subject_table.getColumns().get(1);
                String subject = col.getCellData(index).toString();
                col = (TableColumn) subject_table.getColumns().get(2);
                String grade = col.getCellData(index).toString();
                col = (TableColumn) subject_table.getColumns().get(3);
                String section = col.getCellData(index).toString();
                Alert a = new Alert(AlertType.CONFIRMATION);
                a.setHeaderText("Confirm Deletion");
                a.setContentText("Here you will unassign the subject to "
                        + "the teacher if you click OK");
                Optional<ButtonType> btn = a.showAndWait();
                if(btn.get()==ButtonType.OK){
                    Connection conn = database.Connection.conn;
                    ResultSet result = null;
                    String year = LoginController.current_year;
                    //finding subject_id
                    String query = "SELECT Subject_id FROM year_"+year+"_subject "
                            + "WHERE Subject_name = ? AND Grade = ? ;";
                    PreparedStatement pst = conn.prepareStatement(query);
                    pst.setString(1, subject);
                    pst.setString(2, grade);
                    result = pst.executeQuery();
                    while(result.next()){
                        subject_id = result.getInt(1);
                    }
                    //finding grade_id
                    query = "SELECT Grade_id FROM year_"+year+"_grade "
                            + "WHERE Grade = ? AND Section = ? ;";
                    pst = conn.prepareStatement(query);
                    pst.setString(1, grade);
                    pst.setString(2, section);
                    result = pst.executeQuery();
                    while(result.next()){
                        grade_id = result.getInt(1);
                    }
                    //deleting the relation of teacher and subject from the 
                    //teacher_teaches table
                    query = "DELETE FROM year_"+year+"_teacher_teaches "
                            + "WHERE Teacher_id = ? "
                            + "AND Subject_id = ? "
                            + "AND Grade_id = ? ;";
                    pst = conn.prepareStatement(query);
                    pst.setInt(1, teacher_id);
                    pst.setInt(2, subject_id);
                    pst.setInt(3, grade_id);
                    pst.execute();
                    display_subject_table();
                    comboboxes();
                }
            }
        }
        catch(Exception e){
            System.out.println("Exception at clickDeleteSelectedBtn() at "
                    + "TeacherdetailController : "+e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void clickEditBtn(MouseEvent event) {
        if (!this.store_teacher_id.get().equalsIgnoreCase("0")) {
            int teacher_id = 0;
            try {
                teacher_id = Integer.valueOf(this.store_teacher_id.get());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("editteacher.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Edit Teacher");
                stage.setScene(scene);
                EditteacherController controller = loader.<EditteacherController>getController();
                controller.id.set(teacher_id);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setOnCloseRequest(e -> {
                    refresh();
                });
                stage.show();
            } catch (IOException ex) {
                System.out.println("Exception at clickEditStudent() at StudentController "
                        + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    private class TeacherIdStore {

        private String s = "0";

        public void set(String s) {
            this.s = s;
            display_given_teacher();
            display_subject_table();
            comboboxes();
        }

        public String get() {
            return s;
        }
    }
}

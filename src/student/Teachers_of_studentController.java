/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package student;

import com.jfoenix.controls.JFXTextField;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class Teachers_of_studentController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private MenuBar menubar;

    @FXML
    private JFXTextField search_field;

    @FXML
    private TableView student_table;

    @FXML
    private Label student_id;

    @FXML
    private Label name;

    @FXML
    private Label grade;

    @FXML
    private Label section;

    @FXML
    private Label roll;

    @FXML
    private TableView teacher_table;

    StudentIdStore store_student_id = new StudentIdStore();
    ObservableList<ObservableList> teacher_data = FXCollections.observableArrayList();
    ObservableList<ObservableList> student_data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        display_student_table();
        display_given_student();
        display_teacher_table();
        searchStudentTable();
        onStudentRowClick();
    }

    public void display_given_student() {
        student_id.setText("Student ID : " + store_student_id.get());
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        try {
            String year = LoginController.current_year;
            query = "select Name,Grade,Section,Roll from Year_" + year + "_student where Student_id=" + store_student_id.get()
                    + "  AND Active = 'yes' ";
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                name.setText("Name : " + result.getString("Name"));
                grade.setText("Grade : " + result.getString("Grade"));
                section.setText("Section : " + result.getString("Section"));
                roll.setText("Roll : " + result.getInt("Roll"));
            }
        } catch (Exception e) {
            System.out.println("Exception in assigning given student : " + e.getMessage());
        }
    }

    private void onStudentRowClick() {
        student_table.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ObservableList<TableColumn> cols = student_table.getColumns();
                    TableColumn col = cols.get(0);
                    ObservableList<ObservableList> table_data = FXCollections.observableArrayList();
                    table_data = student_table.getItems();
                    store_student_id.set(col.getCellData(row.getIndex()).toString());
                    //the two code below do the same job as the code above
                    //store_student_id = col.getCellData(table.getSelectionModel().getSelectedIndex()).toString();
                    //store_student_id = col.getCellData(table_data.get(table.getSelectionModel().getSelectedIndex())).toString();
                }
            });
            return row;
        });
    }

    private void display_teacher_table() {
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String year = LoginController.current_year;
        try {
            Statement st = conn.createStatement();
            String query = "SET @a=0";
            st.addBatch(query);
            st.executeBatch();
            query = "SELECT @a:=@a+1 'S.N',"
                    + " year_" + year + "_subject.Subject_name  'Subject' "
                    + ",  year_" + year + "_teacher.Name  'Teacher'  "
                    + " FROM year_" + year + "_teacher "
                    + "INNER JOIN year_" + year + "_teacher_teaches "
                    + "ON year_" + year + "_teacher_teaches.Teacher_id"
                    + "=year_" + year + "_teacher.Teacher_id AND  "
                    + "year_" + year + "_teacher.Active='yes'"
                    + "RIGHT JOIN year_" + year + "_subject ON "
                    + "year_" + year + "_teacher_teaches.Subject_id=year_" + year + "_subject.Subject_id "
                    + "INNER JOIN year_" + year + "_student ON year_" + year + "_subject.Grade"
                    + "=year_" + year + "_student.Grade AND "
                    + "year_" + year + "_student.Student_id=" + 
                    store_student_id.get()+ " ;";
            result = st.executeQuery(query);
            try {
                teacher_table.getColumns().clear();
                teacher_table.getItems().clear();
                /**
                 * ********************************
                 * TABLE COLUMN ADDED DYNAMICALLY *
                 * ********************************
                 */
                for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                    //We are using non property style for making dynamic table
                    final int j = i;
                    TableColumn col = new TableColumn(result.getMetaData().getColumnName(i + 1));
                    col.setStyle("-fx-alignment : center;");

                    col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                            return new SimpleStringProperty(param.getValue().get(j).toString());

                        }
                    });
                    if(i==0){
                        col.setText("S.N");
                    }
                    else if(i==1){
                        col.setText("Subject");
                    }
                    else {
                        col.setText("Teacher Name");
                    }

                    teacher_table.getColumns().addAll(col);
                }
            } catch (Exception ex) {
                System.out.println("Getting metadata error");
                ex.printStackTrace();
            }

            try {
                /**
                 * ******************************
                 * Data added to ObservableList * ******************************
                 */
                while (result.next()) {
                    //Iterate Row
                    ObservableList<String> row = FXCollections.observableArrayList();
                    for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                        //Iterate Column
                        String data = result.getString(i);
                        if (result.wasNull()) {
                            data = " ";
                        }
                        row.add(data);
                    }
                    teacher_data.add(row);

                }
            } catch (Exception ex) {
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
        } catch (Exception e) {
            System.out.println("Exception at display_teacher_table() "
                    + "at Teachers_of_studentController : " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void display_student_table() {
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        try {
            String year = LoginController.current_year;
            query = "SELECT Student_id,Grade,Section,Roll,Name FROM Year_" + year + "_student "
                    + "WHERE Active='yes' ORDER BY Grade,Section,Roll;";
            result = conn.createStatement().executeQuery(query);
        } catch (Exception ex) {
            System.out.println("Error obtaning data from sql" + ex.getMessage());
        }
        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(result.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");

                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                if ("previous_school".equalsIgnoreCase(col.getText()) || "mother_name".equalsIgnoreCase(col.getText())) {
                    col.setVisible(false);
                }
                student_table.getColumns().addAll(col);
            }
        } catch (Exception ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /**
             * ******************************
             * Data added to ObservableList * ******************************
             */
            while (result.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    row.add(result.getString(i));
                }
                student_data.add(row);

            }
        } catch (Exception ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        student_table.setItems(student_data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        student_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        student_table.setTableMenuButtonVisible(true);
    }

    public void searchStudentTable() {
        search_field.textProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(javafx.beans.Observable observable) {

                if (search_field.textProperty().get().isEmpty()) {

                    student_table.setItems(student_data);

                    return;

                }
                ObservableList filteredItems = FXCollections.observableArrayList();

                ObservableList<TableColumn> cols = student_table.getColumns();

                for (int i = 0; i < student_data.size(); i++) {

                    TableColumn col = new TableColumn();
                    for (int j = 0; j < cols.size(); j++) {
                        col = cols.get(j);

                        String cellValue = col.getCellData(student_data.get(i)).toString();

                        cellValue = cellValue.toLowerCase();

                        if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                            filteredItems.add(student_data.get(i));

                            break;

                        }

                    }
                }
                student_table.setItems(filteredItems);
            }

        });
    }

    public void setStudentId(String s) {
        store_student_id.set(s);
    }

    private class StudentIdStore {

        private String s = "0";

        public void set(String s) {
            this.s = s;
            display_teacher_table();
            display_given_student();
        }

        public String get() {
            return s;
        }
    }
}

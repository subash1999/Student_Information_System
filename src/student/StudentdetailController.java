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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
public class StudentdetailController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private MenuBar menubar;

    @FXML
    private JFXTextField search_field;

    @FXML
    private TableView table;

    @FXML
    private Label student_id;

    @FXML
    private Label name;

    @FXML
    private Label grade;

    @FXML
    private Label section;

    @FXML
    private Label gender;

    @FXML
    private Label roll;

    @FXML
    private Label dob;

    @FXML
    private Label father;

    @FXML
    private Label mother;

    @FXML
    private Label guardian;

    @FXML
    private Label address;

    @FXML
    private Label phone;

    @FXML
    private Label previous_school;

    @FXML
    private Button edit_btn;

    @FXML
    private Button teachers_btn;

    @FXML
    private Button subjects_btn;
    private String[] column_name;
    private int column_count;
    private StudentIdStore store_student_id = new StudentIdStore();
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        makeTable();
        displayDetails();
        onRowClick();
        searchTable();
    }

    public void refresh() {
        makeTable();
        String search = search_field.getText();
        search_field.setText(null);
        search_field.setText(search);
        String student_id = store_student_id.get();
        store_student_id.set(student_id);
    }

    @FXML
    private void clickEditBtn(MouseEvent event) {
        if (!store_student_id.get().equals("0")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("editstudent.fxml"));
                Parent root = fxmlLoader.load();
                EditstudentController controller = (EditstudentController) fxmlLoader.getController();
                controller.student_id.set(Integer.valueOf(store_student_id.get()));
                Scene scene = new Scene(root);
                Stage primaryStage = new Stage();
                primaryStage.setTitle("Edit Student");
                primaryStage.setScene(scene);
                primaryStage.initModality(Modality.APPLICATION_MODAL);
                primaryStage.setResizable(false);
                primaryStage.setOnCloseRequest(e -> {
                    refresh();
                });
                primaryStage.show();
            } catch (Exception e) {
                System.out.println("Error in loading fxml file "
                        + "at clickEditBtn at StudentdetailControllelr:"
                        + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void teacher_btn_clicked(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("teachers_of_student.fxml"));
            Parent root = null;
            root = fxmlLoader.load();
            Teachers_of_studentController controller = (Teachers_of_studentController) fxmlLoader.getController();
            controller.setStudentId(store_student_id.get());
            Scene scene = new Scene(root);
            Stage primaryStage = new Stage();
            primaryStage.setTitle("Teachers and Subject of a Student ");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.out.println("Error in loading fxml file :" + e.getMessage());
        }
    }

    public void searchTable() {
        search_field.textProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(javafx.beans.Observable observable) {
                if (search_field.getText() == null) {
                    table.setItems(data);
                    return;
                }
                if (search_field.textProperty().get().isEmpty()) {
                    table.setItems(data);
                    return;
                }
                ObservableList filteredItems = FXCollections.observableArrayList();

                ObservableList<TableColumn> cols = table.getColumns();

                for (int i = 0; i < data.size(); i++) {

                    TableColumn col = new TableColumn();
                    for (int j = 0; j < cols.size(); j++) {
                        col = cols.get(j);

                        String cellValue = col.getCellData(data.get(i)).toString();

                        cellValue = cellValue.toLowerCase();

                        if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                            filteredItems.add(data.get(i));

                            break;

                        }

                    }
                }
                table.setItems(filteredItems);
            }

        });
    }

    private void onRowClick() {
        table.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ObservableList<TableColumn> cols = table.getColumns();
                    TableColumn col = cols.get(0);
                    ObservableList<ObservableList> table_data = FXCollections.observableArrayList();
                    table_data = table.getItems();
                    store_student_id.set(col.getCellData(row.getIndex()).toString());
                    //the two code below do the same job as the code above
                    //store_student_id = col.getCellData(table.getSelectionModel().getSelectedIndex()).toString();
                    //store_student_id = col.getCellData(table_data.get(table.getSelectionModel().getSelectedIndex())).toString();
                }
            });
            return row;
        });
    }

    public void makeTable() {
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        table.getColumns().clear();
        table.getItems().clear();
        data.clear();
        try {
            String year = LoginController.current_year;
            query = "SELECT Student_id,Grade,Roll,Name FROM Year_" + year + "_student "
                    + "WHERE Active='yes' ORDER BY Grade,Section,Roll;";
            result = conn.createStatement().executeQuery(query);
        } catch (Exception ex) {
            System.out.println("Error obtaning data from sql at makeTable() "
                    + "at StudentdetailController" + ex.getMessage());
        }
        try {
            /**
             * ********************************
             * TABLE COLUMN ADDED DYNAMICALLY * ********************************
             */
            column_count = result.getMetaData().getColumnCount();
            column_name = new String[column_count];
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
                column_name[i] = col.getText();
                if ("previous_school".equalsIgnoreCase(col.getText()) || "mother_name".equalsIgnoreCase(col.getText())) {
                    col.setVisible(false);
                }

                table.getColumns().addAll(col);
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
                data.add(row);

            }
        } catch (Exception ex) {
            System.out.println("No next data error");
        }
        //FINALLY ADDED TO TableView
        table.setItems(data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        table.setTableMenuButtonVisible(true);
    }

    public void setStudentId(String s) {
        store_student_id.set(s);
    }

    public void displayDetails() {
        Connection conn = database.Connection.conn;
        String year = LoginController.current_year;
        String query = "SELECT * FROM Year_" + year + "_student WHERE Student_id = " + store_student_id.get();
        ResultSet result = null;
        try {
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                this.student_id.setText("Student ID : " + String.valueOf(result.getBigDecimal("Student_id")));
                name.setText("Name : " + result.getString("Name"));
                grade.setText("Grade : " + result.getString("Grade"));
                section.setText("Section : " + result.getString("Section"));
                gender.setText("Gender : " + result.getString("Gender"));
                roll.setText("Roll no : " + String.valueOf(result.getInt("Roll")));
                dob.setText("Date of Birth : " + result.getString("DOB"));
                father.setText("Father's Name : " + result.getString("Father_name"));
                mother.setText("Mother's Name : " + result.getString("Mother_name"));
                guardian.setText("Guardian's Name : " + result.getString("Guardian_name"));
                address.setText("Address : " + result.getString("Address"));
                phone.setText("Phone : " + result.getString("Phone"));
                previous_school.setText("Previous School : " + result.getString("Previous_school"));
            }

        } catch (Exception ex) {
            System.out.println("Exception occured during displaying details");
        }

    }

    private class StudentIdStore {

        private String s = "0";

        public void set(String s) {
            if (!s.equalsIgnoreCase("0")) {
                this.s = s;
                displayDetails();
            }
        }

        public String get() {
            return s;
        }
    }
}

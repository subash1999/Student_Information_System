/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package examManagement;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import login.LoginController;
import org.controlsfx.control.spreadsheet.GridBase;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class ExamManagementController implements Initializable {

    @FXML
    private JFXTextField search_field;

    @FXML
    private TableView exam_table;

    @FXML
    private Label exam_id_label;

    @FXML
    private Label exam_name_label;

    @FXML
    private Label full_name_label;

    @FXML
    private Label result_date_label;

    @FXML
    private Button edit_btn;

    @FXML
    private Button delete_btn;

    @FXML
    private TableView ledger_table;

    @FXML
    private Button delete_ledger_btn;

    @FXML
    private Button add_exam_btn;

    private IntegerProperty exam_id = new SimpleIntegerProperty();
    private ObservableList<ObservableList> exam_data = FXCollections.observableArrayList();
    private ObservableList<ObservableList> ledger_data = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        exam_id.set(0);
        listenerForExamId();
        listenerForSearchField();
        onExamTableRowClick();
        makeExamTable();
    }

    private void listenerForExamId() {
        exam_id.addListener(e -> {
            if (exam_id.get() != 0) {
                refresh();
            }
        });
    }

    private void listenerForSearchField() {
        search_field.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {

                if (search_field.getText() == null) {

                    exam_table.setItems(exam_data);

                    return;

                }
                if (search_field.getText().isEmpty()) {

                    exam_table.setItems(exam_data);

                    return;

                }
                ObservableList tableItems = FXCollections.observableArrayList();
                ObservableList<TableColumn> cols = exam_table.getColumns();
                for (int i = 0; i < exam_data.size(); i++) {
                    TableColumn col = new TableColumn();
                    for (int j = 0; j < cols.size(); j++) {
                        col = cols.get(j);
                        if (col.isVisible()) {
                            String cellValue = col.getCellData(exam_data.get(i)).toString();

                            cellValue = cellValue.toLowerCase();

                            if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                tableItems.add(exam_data.get(i));

                                break;

                            }
                        }

                    }
                }
                exam_table.setItems(tableItems);
            }
        });

    }

    private void refresh() {
        String search = search_field.getText();
        makeExamTable();
        makeLedgerTable();
        fillDetails();
        search_field.setText("");
        search_field.setText(search);
    }

    private void fillDetails() {
        if (exam_id.get() != 0) {
            Connection conn = database.Connection.conn;
            int id = exam_id.get();
            try {
                String year = LoginController.current_year;
                String sql = "SELECT * FROM year_" + year + "_exam WHERE Exam_id = "
                        + id + " ;";
                ResultSet result = conn.createStatement().executeQuery(sql);
                while (result.next()) {
                    exam_id_label.setText(String.valueOf(result.getInt("Exam_id")));
                    exam_name_label.setText(result.getString("Name"));
                    full_name_label.setText(result.getString("Full_name"));
                    result_date_label.setText(result.getString("Result_date"));
                }
            } catch (Exception e) {
                System.out.println("Exception at fillDetails "
                        + "at ExamManagementController : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void makeExamTable() {
        exam_table.getItems().clear();
        exam_table.getColumns().clear();
        exam_data.clear();
        Connection conn = database.Connection.conn;
        String query = "SET @a=0 ; ";
        ResultSet rs = null;
        try {
            String year = LoginController.current_year;
            Statement st = conn.createStatement();
            st.execute(query);
            query = "SELECT @a:=@a+1 as 'S.N',Exam_id,Name,Full_name,Result_date "
                    + "FROM year_" + year + "_exam ;";
            rs = conn.createStatement().executeQuery(query);

        } catch (Exception e) {
            System.out.println("Displaying exam in a ExamManagementController "
                    + " error :" + e.getMessage());
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

                exam_table.getColumns().addAll(col);
            }
        } catch (SQLException ex) {
            System.out.println("Metadata error at makeExamTable "
                    + "at ExamManagementController ");
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
                exam_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error at makeExamTable "
                    + "at ExamManagementController ");
        }
        //FINALLY ADDED TO TableView
        exam_table.setItems(exam_data);
        //getting rid of the extra column in the javafx tableview
        // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        //but in this case we need the extra column for proper resizing of the columns so
        // the table is being made UNCONSTRAINED_RESIZE_POLICY
        exam_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //setting the table menu button visible which lets user to select the column to view or hide
        exam_table.setTableMenuButtonVisible(true);

    }

    private void onExamTableRowClick() {
        exam_table.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    ObservableList<TableColumn> cols = exam_table.getColumns();
                    TableColumn col = cols.get(1);
                    ObservableList<ObservableList> table_data = FXCollections.observableArrayList();
                    table_data = exam_table.getItems();
                    exam_id.set(Integer.valueOf(String.valueOf(col.getCellData(row.getIndex()))));
                    //the two code below do the same job as the code above
                    //store_student_id = col.getCellData(table.getSelectionModel().getSelectedIndex()).toString();
                    //store_student_id = col.getCellData(table_data.get(table.getSelectionModel().getSelectedIndex())).toString();
                }
            });
            return row;
        });
    }

    private void makeLedgerTable() {
        if (exam_id.get() != 0) {
            int id = exam_id.get();
            ledger_table.getItems().clear();
            ledger_table.getColumns().clear();
            ledger_data.clear();
            Connection conn = database.Connection.conn;
            String query = "SET @a=0 ; ";
            ResultSet rs = null;
            try {
                String year = LoginController.current_year;
                Statement st = conn.createStatement();
                st.execute(query);
                query = "SELECT @a:=@a+1 as 'S.N',Ledger_id,Grade,Section "
                        + "FROM year_" + year + "_ledger INNER JOIN "
                        + "year_" + year + "_grade"
                        + " ON  year_" + year + "_grade.Grade_id = "
                        + " year_" + year + "_ledger.Grade_id AND Exam_id = " + id + " "
                        + ";";
                rs = conn.createStatement().executeQuery(query);

            } catch (Exception e) {
                System.out.println("Displaying ledger in a ExamManagementController "
                        + " error :" + e.getMessage());
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

                    ledger_table.getColumns().addAll(col);
                }
            } catch (SQLException ex) {
                System.out.println("Metadata error at makeLedgerTable "
                        + "at ExamManagementController ");
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
                    ledger_data.add(row);

                }
            } catch (SQLException ex) {
                System.out.println("No next data error at makeLedgerTable "
                        + "at ExamManagementController ");
            }
            //FINALLY ADDED TO TableView
            ledger_table.setItems(ledger_data);
            //getting rid of the extra column in the javafx tableview
            // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            //but in this case we need the extra column for proper resizing of the columns so
            // the table is being made UNCONSTRAINED_RESIZE_POLICY
            ledger_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            //setting the table menu button visible which lets user to select the column to view or hide
            ledger_table.setTableMenuButtonVisible(true);

        }
    }

    @FXML
    private void clickDeleteBtn(MouseEvent event) {
        if (exam_id.get() != 0) {
            Connection conn = database.Connection.conn;
            try {
                Alert a = new Alert(AlertType.CONFIRMATION);
                a.setHeaderText("Please read below before confirming !!!");
                a.setContentText("Deleteting a exam will result in deleting all the"
                        + " ledger associated with that exam, so be carefull");
                Optional<ButtonType> btn = a.showAndWait();
                if (btn.get() == ButtonType.OK) {
                    String year = LoginController.current_year;
                    String query = "SELECT Table_name FROM year_" + year + "_ledger WHERE "
                            + "Exam_id = " + exam_id.get() + " ;";
                    ResultSet result = conn.createStatement().executeQuery(query);
                    Statement st = conn.createStatement();
                    while (result.next()) {
                        query = "DROP TABLE " + result.getString(1) + " ;";
                        st.addBatch(query);
                    }
                    query = "DELETE FROM year_" + year + "_ledger WHERE "
                            + "Exam_id = " + exam_id.get() + " ;";
                    st.addBatch(query);
                    query = "DELETE FROM year_" + year + "_exam WHERE "
                            + "Exam_id = " + exam_id.get() + " ;";
                    st.addBatch(query);
                    st.executeBatch();
                    exam_id_label.setText("");
                    exam_name_label.setText("");
                    full_name_label.setText("");
                    result_date_label.setText("");
                }
            } catch (Exception e) {
                System.out.println("Exception at clickDeleteBtn "
                        + "at ExamManagementController : " + e.getMessage());
                e.printStackTrace();
            } finally {
                refresh();
            }
        }
    }

    @FXML
    private void clickDeleteLedgerBtn(MouseEvent event) {
        int index = ledger_table.getSelectionModel().getSelectedIndex();
        TableColumn col = (TableColumn) ledger_table.getColumns().get(1);
        int ledger_id = Integer.valueOf(String.valueOf(col.getCellData(index)));
        if (ledger_id != 0) {
            col = (TableColumn) ledger_table.getColumns().get(2);
            String current_grade = String.valueOf(col.getCellData(index));
            col = (TableColumn) ledger_table.getColumns().get(3);
            String current_section = String.valueOf(col.getCellData(index));
            String table_name = null;
            String current_exam = exam_name_label.getText();
            Connection conn = database.Connection.conn;
            ResultSet result = null;
            String sql = null;
            String year = LoginController.current_year;
            try {
                sql = "SELECT Table_name FROM year_" + year + "_ledger "
                        + " WHERE Ledger_id = " + ledger_id + " ;";
                result = conn.createStatement().executeQuery(sql);
                while (result.next()) {
                    table_name = result.getString(1);
                }
            } catch (Exception e) {
                System.out.println("Exception at clickDeleteLedgerBtn() "
                        + "at ExamManagementController : " + e.getMessage());
                e.printStackTrace();
            }
            Alert al = new Alert(AlertType.CONFIRMATION);
            al.setHeaderText("Do you want to confirm the deletion of the ledger \n of exam : " + current_exam
                    + "\n of grade : " + current_grade
                    + "\n of section : " + current_section);
            al.setContentText("Deleting a ledger will only affect the single ledger and none other factors are "
                    + "effected.!!!!IMPORTANT But Remember you have to recalculate the marks "
                    + "to get the correct ranking of the another section of same class"
                    + " if it have any other section......");
            Optional<ButtonType> button = al.showAndWait();
            if (button.get() == ButtonType.OK) {
                try {
                    Statement st = conn.createStatement();
                    sql = "DROP TABLE " + table_name + " ;";
                    st.addBatch(sql);
                    sql = "DELETE FROM year_" + year + "_ledger WHERE Ledger_id =  " + ledger_id + " ;";
                    st.addBatch(sql);
                    sql = "DELETE FROM year_" + year + "_marks WHERE Ledger_id =  " + ledger_id + " ;";
                    st.addBatch(sql);
                    st.executeBatch();
                    al = new Alert(AlertType.INFORMATION);
                    al.setContentText("Deletion of the ledger of exam : " + current_exam
                            + ", of grade : " + current_grade
                            + " of section : " + current_section
                            + " was successful");
                    al.setHeaderText("Deletion Operation was successful ");
                    al.show();
                    makeLedgerTable();
                } catch (Exception e) {
                    System.out.println("Exception at clickDeleteLedgerBtn() at ExamManagementController : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void clickEditBtn(MouseEvent event) {
        if (exam_id.get() != 0) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/examManagement/editexam.fxml"));
                Parent root = loader.load();
                EditexamController controller = loader.<EditexamController>getController();
                controller.exam_id.set(exam_id.get());
                Scene scene = new Scene(root);
                Stage window = new Stage();
                window.setScene(scene);
                window.setTitle("Edit Exam");
                window.initModality(Modality.APPLICATION_MODAL);
                window.setOnCloseRequest(e -> {
                    refresh();
                });
                window.show();
            } catch (Exception e) {
                System.out.println("Error at clickEditExam at LedgerController : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void clickAddExamBtn(MouseEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("newexam.fxml"));
            Scene scene = new Scene(root);
            Stage window = new Stage();
            window.setScene(scene);
            window.setTitle("New Exam");
            window.initModality(Modality.APPLICATION_MODAL);
            window.setOnCloseRequest(e -> {
                refresh();
            });
            window.show();
        } catch (Exception e) {
            System.out.println("Error at clickAddExam at LedgerController : " + e.getMessage());
        }
    }
}

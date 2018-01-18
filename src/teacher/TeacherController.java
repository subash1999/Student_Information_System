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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
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
public class TeacherController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    protected MenuItem menuitem_close;

    @FXML
    protected MenuItem menuitem_about;

    @FXML
    protected JFXTextField search_field;

    @FXML
    protected ChoiceBox search_choice;

    @FXML
    protected TableView table;

    @FXML
    private ImageView add_teacher_image;

    @FXML
    private ImageView edit_teacher_image;

    @FXML
    private ImageView delete_teacher_image;

    @FXML
    private Label add_teacher_label;

    @FXML
    private Label edit_teacher_label;

    @FXML
    private Label delete_teacher_label;
    
    @FXML
    private Label total_label;

    public static String teacher_id = "0";
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private String[] column_name;
    private int column_count;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //populating the table with the data and column from the database
        makeTable();
        searchTable();
        choiceBox();
        onRowClick();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        if (!LoginController.user_type.equalsIgnoreCase("Admin")) {
            add_teacher_image.setVisible(false);
            edit_teacher_image.setVisible(false);
            delete_teacher_image.setVisible(false);
            add_teacher_label.setVisible(false);
            edit_teacher_label.setVisible(false);
            delete_teacher_label.setVisible(false);
        }
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
                    teacher_id = col.getCellData(row.getIndex()).toString();
                    //the two code below do the same job as the code above
                    //String student_id = col.getCellData(table.getSelectionModel().getSelectedIndex()).toString();
                    //String student_id = col.getCellData(table_data.get(table.getSelectionModel().getSelectedIndex())).toString();
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("teacherdetail.fxml"));

                        Parent root1 = null;
                        root1 = fxmlLoader.load();
                        TeacherdetailController controller = (TeacherdetailController) fxmlLoader.getController();
                        //passing the id of the selected student to the studentdetail.fxml file
                        controller.setTeacherId(teacher_id);
                        Stage stage = new Stage();
                        stage.setTitle("Teacher Detail");
                        stage.setScene(new Scene(root1));
                        stage.setOnCloseRequest(e -> {
                            refresh();
                        });
                        stage.show();
                    } catch (IOException ex) {
                        System.out.println("FXML loader error" + ex.getMessage());
                    }
                }
            });
            return row;
        });
    }

    private void choiceBox() {
        ObservableList<String> choice_list = FXCollections.observableArrayList();
        choice_list.add("All");
        choice_list.addAll(Arrays.asList(column_name));
        search_choice.getSelectionModel().select("All");
        search_choice.setItems(choice_list);
        search_choice.getSelectionModel().selectFirst();
        //adding listner to the choice box so that when ever the value changes
        //the searchbox will search accordingly the search_choice of the user
        search_choice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
                if (!search_choice.getSelectionModel().getSelectedItem().toString().isEmpty()) {
                    updateFilterTable(search_choice.getSelectionModel().getSelectedItem().toString());
                }
            }
        });
    }

    private void searchTable() {
        search_field.textProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(javafx.beans.Observable observable) {
                if (search_field.getText() == null) {
                    table.setItems(data);
                    return;
                }
                if (search_field.getText().isEmpty()) {
                    table.setItems(data);
                    return;
                }
                updateFilterTable(search_choice.getSelectionModel().getSelectedItem().toString());
            }

        });
    }

    private void updateFilterTable(String choice) {
        ObservableList tableItems = FXCollections.observableArrayList();

        ObservableList<TableColumn> cols = table.getColumns();

        for (int i = 0; i < data.size(); i++) {

            TableColumn col = new TableColumn();
            switch (choice.toLowerCase()) {

                case "all":
                    for (int j = 0; j < cols.size(); j++) {
                        col = cols.get(j);
                        if (col.isVisible()) {
                            String cellValue = col.getCellData(data.get(i)).toString();

                            cellValue = cellValue.toLowerCase();

                            if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                tableItems.add(data.get(i));

                                break;

                            }
                        }
                    }
                    break;
                case "name":
                    for (TableColumn c : cols) {
                        if ("name".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    String cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));

                    }

                    break;

                case "phone":
                    for (TableColumn c : cols) {
                        if ("phone".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));

                    }

                    break;
                case "teacher_id":
                    for (TableColumn c : cols) {
                        if ("teacher_id".equalsIgnoreCase(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));

                    }

                    break;

                case "address":
                    for (TableColumn c : cols) {
                        if ("address".equals(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));
                    }
                    break;
                case "gender":
                    for (TableColumn c : cols) {
                        if ("gender".equalsIgnoreCase(c.getText().toLowerCase())) {
                            col = c;
                        }
                    }

                    cellValue = col.getCellData(data.get(i)).toString();

                    cellValue = cellValue.toLowerCase();

                    if (cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                        tableItems.add(data.get(i));
                    }
                    break;
                default:
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("Error search by Type");
                    alert.setContentText("The search type didnot matched."
                            + "Please check the code for the proper search type");
                    alert.show();
                    break;
            }

        }

        table.setItems(tableItems);
        
    }

    private void makeTable() {
        database.Connection.connect();

        Connection conn = database.Connection.conn;

        //SQL FOR SELECTING ALL OF STUDENT
        String SQL;
        ResultSet rs = null;
        try {
            table.getColumns().clear();
            table.getItems().clear();
            data.clear();
            String year = LoginController.current_year;
            SQL = "SELECT Teacher_id,Name,Gender,Phone,Address FROM  Year_" + year + "_teacher "
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
            column_count = rs.getMetaData().getColumnCount();
            column_name = new String[column_count];
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");

                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                column_name[i] = col.getText();

                table.getColumns().addAll(col);
            }

        } catch (Exception ex) {
            System.out.println("Getting metadata error  " + ex.getMessage());
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
                data.add(row);

            }
        } catch (SQLException ex) {
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
        total_label.setText("Total : "+table.getItems().size());                     

    }

    private void refresh() {
        String search = search_field.getText();
        searchTable();
        makeTable();
        search_field.setText(null);
        search_field.setText(search);
    }

    @FXML
    private void clickAddTeacher(MouseEvent event) {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/teacher/addteacher.fxml"));
            Parent root = fxml.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Register Teacher ");
            stage.setResizable(false);
            stage.setOnCloseRequest(e -> {
                refresh();
            });
            stage.show();
        } catch (Exception e) {
            System.out.println("Exception at clickAddTeacher() "
                    + "at TeacherController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void clickDeleteTeacher(MouseEvent event) {
        if (!table.getSelectionModel().getSelectedItems().isEmpty()) {
            Alert delete_alert = new Alert(AlertType.CONFIRMATION);
            delete_alert.setTitle("Confirm Teachers Delete");
            delete_alert.setHeaderText("Do you want to delete data of teachers below?");
            String content = "Teacher id : ";
            List<Integer> teacher_id = new ArrayList<Integer>();
            ObservableList list = table.getSelectionModel().getSelectedIndices();
            TableColumn col = (TableColumn) table.getColumns().get(0);
            for (int i = 0; i < list.size(); i++) {
                int index = (Integer) list.get(i);
                teacher_id.add(Integer.valueOf(String.valueOf(col.getCellData(index))));
                if (i != list.size() - 1) {
                    content = content + " " + col.getCellData(index) + ",";
                } else {
                    content = content + " " + col.getCellData(index);
                }
            }
            delete_alert.setContentText(content);
            delete_alert.setResizable(true);
            Optional<ButtonType> btn = delete_alert.showAndWait();
            if (btn.get() == ButtonType.OK) {
                try {
                    String year = LoginController.current_year;
                    Connection conn = database.Connection.conn;
                    String query = "UPDATE year_" + year + "_teacher SET Active = 'No'"
                            + " WHERE Teacher_id = ?";
                    PreparedStatement pst = conn.prepareStatement(query);
                    for (int id : teacher_id) {
                        pst.setInt(1, id);
                        pst.execute();
                    }
                    for (int id : teacher_id) {
                        query = "DELETE FROM year_" + year + "_teacher_teaches "
                                + " WHERE Teacher_id = ?";
                        pst = conn.prepareStatement(query);
                        pst.setInt(1, id);
                        pst.execute();
                    }
                    refresh();
                } catch (Exception e) {
                    System.out.println("Exception at clickDeleteStudent() "
                            + "at StudentController : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    private void clickEditTeacher(MouseEvent event) {
        if (!table.getSelectionModel().getSelectedItems().isEmpty()) {
            if (table.getSelectionModel().getSelectedItems().size() == 1) {
                int teacher_id = 0;
                TableColumn col = (TableColumn) table.getColumns().get(0);
                String id = (String) col.getCellData(table.getSelectionModel().getSelectedIndex());
                try {
                    teacher_id = Integer.valueOf(id);
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
    }

}

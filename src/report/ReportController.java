/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class ReportController implements Initializable {

    @FXML
    private ComboBox exam_choicebox;

    @FXML
    private ComboBox grade_choicebox;

    @FXML
    private ComboBox section_choicebox;

    @FXML
    private ComboBox roll_combobox;

    @FXML
    private ComboBox save_from_combobox;

    @FXML
    private ComboBox save_to_combobox;

    @FXML
    private ProgressIndicator progress_indicator;

    @FXML
    private Button view_report_btn;

    @FXML
    private AnchorPane anchorpane_label;

    @FXML
    private Label selection_label;

    @FXML
    private Button view_selected_btn;

    @FXML
    private TableView students_table;

    @FXML
    private Label examination_label;

    @FXML
    private Label grade_label;

    @FXML
    private Label section_label;

    @FXML
    private Label male_label;

    @FXML
    private Label female_label;

    @FXML
    private Label passed_label;

    @FXML
    private Label failed_label;

    @FXML
    private Button view_org_btn;

    @FXML
    private TextField exam_name_textfield;

    @FXML
    private Button edit_exam_name_btn;

    @FXML
    private Label prev_label;

    @FXML
    private Label next_label;

    @FXML
    private TextField date_textfield;

    @FXML
    private Button edit_date_btn;

    @FXML
    private Button save_current_btn;

    @FXML
    private Button save_all_btn;

    @FXML
    private StackPane stackpane;

    private String year = LoginController.current_year;
    /*
    This is used to display the marksheet in the stackpane which is a children
    of scrollpane,
    the borderpane contaning marksheet is obtained from the ReportcardController 
    then assigned to this "borderpane" which is assigned as a childern of the
    stackpane"
     */
    private BorderPane borderpane = new BorderPane();
    /*
    ledger_id for storing ledger id, it also listens to changes to ledger id
    similarly for the roll_no
     */
    private StringProperty ledger_id = new SimpleStringProperty();
    private StringProperty roll_no = new SimpleStringProperty();
    /*
    list for storing roll_no and student_id, they have the values stored
    simultaneously so, roll_no_list(index) gives a roll no of a student while
    student_id_list(index) gives the student id of the same student
     */
    private ObservableList<String> roll_no_list = FXCollections.observableArrayList();
    private ObservableList<String> student_id_list = FXCollections.observableArrayList();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //assigning the borderpane as the children of the scrollpane, but first
        //clearing any children present in the scrollpane        
        selectionChoiceBox();
        listenerForLedgerId();
        listenerForRollno();
        listenerForRollComboBox();
        //deactivate both save buttons
        save_current_btn.setDisable(true);
        save_all_btn.setDisable(true);
        //hiding the progress indicator
        progress_indicator.setVisible(false);
    }

    private void listenerForLedgerId() {
        ledger_id.addListener((Observable e) -> {

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    stackpane.getScene().setCursor(Cursor.WAIT);
                    //put the roll no in the roll_no_of_ledger list while
                    //making student table
                    makeStudentTable();
                    reportAbout();
                    examNameInput();
                    resultDate();
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            save_current_btn.setDisable(false);
                            save_all_btn.setDisable(false);
                            String oldval = roll_no.get();
                            roll_no.set(roll_no_list.get(0));
                            if (oldval != null) {
                                if (oldval.equals(roll_no.get())) {
                                    marksReport();
                                    rollChoiceBox();
                                }
                            }
                            rollChoiceBox();
                            //clearing value if existed fot save_from and save_to combobox
                            save_from_combobox.getItems().clear();
                            save_to_combobox.getItems().clear();
                            //adding value to the save_from and save_to combobox
                            save_from_combobox.getItems().addAll(roll_no_list);
                            save_to_combobox.getItems().addAll(roll_no_list);
                            //selecting first and last element resp for from and to
                            save_from_combobox.getSelectionModel().selectFirst();
                            save_to_combobox.getSelectionModel().selectLast();
                            //setting progress indicator not visible
                            progress_indicator.setVisible(false);
                        }
                    });
                    stackpane.getScene().setCursor(Cursor.DEFAULT);
                    return null;
                }

            };
            Thread t = new Thread(task, "Listener of the ledger_id StringProperty");
            t.setPriority(10);
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.out.println("Exception at thread of listenerForLedgerId "
                        + "at ReportController " + ex.getMessage());
            }

            t.start();

        });
    }

    private void listenerForRollno() {
        roll_no.addListener(e -> {
            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    stackpane.getScene().setCursor(Cursor.WAIT);
                    Platform.runLater(() -> {
                        marksReport();
                    });
                    stackpane.getScene().setCursor(Cursor.DEFAULT);
                    return null;
                }
            };
            Thread t = new Thread(task, "Listener of the student_id StringProperty");
            t.setPriority(10);
            try {
                t.join();
            } catch (InterruptedException ex) {
                System.out.println("Exception at thread of listenerForStudentId "
                        + "at ReportController " + ex.getMessage());
            }
            t.start();
        });
    }

    private void selectionChoiceBox() {
        selection_label.setVisible(false);
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Exam_id,Name FROM year_"
                    + year + "_exam";
            ResultSet result = conn.createStatement().executeQuery(query);
            exam_choicebox.getItems().clear();
            while (result.next()) {
                exam_choicebox.getItems().add(result.getString(2));
            }
            grade_choicebox.setDisable(true);
            section_choicebox.setDisable(true);
            /*
             adding listener to the exam_choicebox so only after the
             selection of exam_choicebox, the grade_choicebox will be
             available for selection with valid options for that exam
             */
            exam_choicebox.getSelectionModel().selectedItemProperty().addListener(e -> {
                final String sql = "SELECT DISTINCT Grade FROM year_" + year + "_grade "
                        + "WHERE Grade_id IN (SELECT Grade_id FROM "
                        + "year_" + year + "_ledger WHERE Exam_id IN "
                        + "(SELECT Exam_id FROM"
                        + " year_" + year + "_exam WHERE Name = '"
                        + exam_choicebox.getSelectionModel().getSelectedItem().toString()
                        + "'));";
                grade_choicebox.getItems().clear();
                try {
                    final ResultSet res = conn.createStatement().executeQuery(sql);
                    while (res.next()) {
                        grade_choicebox.getItems().add(res.getString(1));
                    }
                    grade_choicebox.setDisable(false);
                } catch (Exception ex) {
                    System.out.println("Exception while addiing listener"
                            + " to exam_choicebox at selectionChoiceBox() "
                            + "at ReportController : " + ex.getMessage());
                }
            });
            grade_choicebox.getSelectionModel().selectedItemProperty().addListener(e -> {
                final String sql = "SELECT DISTINCT Section FROM year_" + year + "_grade "
                        + "WHERE Grade_id IN (SELECT Grade_id FROM "
                        + "year_" + year + "_ledger WHERE Grade_id IN "
                        + "(SELECT Grade_id FROM"
                        + " year_" + year + "_grade WHERE Grade = '"
                        + grade_choicebox.getSelectionModel().getSelectedItem().toString()
                        + "') AND Exam_id IN "
                        + "(SELECT Exam_id FROM"
                        + " year_" + year + "_exam WHERE Name = '"
                        + exam_choicebox.getSelectionModel().getSelectedItem().toString()
                        + "')"
                        + ");";
                section_choicebox.getItems().clear();
                try {
                    final ResultSet res = conn.createStatement().executeQuery(sql);
                    while (res.next()) {
                        section_choicebox.getItems().add(res.getString(1));
                    }
                    section_choicebox.setDisable(false);
                } catch (Exception ex) {
                    System.out.println("Exception while addiing listener"
                            + "to grade_choicebox at selectionChoiceBox() "
                            + "at ReportController : " + ex.getMessage());
                }
            });

        } catch (Exception e) {
            System.out.println("Exception at selectionChoiceBox() "
                    + "at ReportController : " + e.getMessage());
        }
    }

    private void makeStudentTable() {
        Platform.runLater(() -> {
            students_table.getColumns().clear();
            students_table.getItems().clear();
        });
        String ledger_id = this.ledger_id.get();
        String query = "SELECT Table_name FROM year_" + year + "_ledger WHERE "
                + "Ledger_id = " + ledger_id;
        Connection conn = database.Connection.conn;
        try {
            ResultSet result = conn.createStatement().executeQuery(query);
            String table_name = null;
            while (result.next()) {
                table_name = result.getString(1);
            }
            query = "SELECT Student_id,Roll,Name FROM year_" + year + "_student "
                    + "NATURAL JOIN " + table_name
                    + " WHERE " + table_name + ".Student_id = "
                    + "year_" + year + "_student.Student_id "
                    + "ORDER BY Roll";
            result = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            student_id_list.clear();
            roll_no_list.clear();
            while (result.next()) {
                student_id_list.add(String.valueOf(result.getInt(1)));
                roll_no_list.add(String.valueOf(result.getInt(2)));
                ObservableList list = FXCollections.observableArrayList();
                list.add(String.valueOf(result.getInt(1)));
                list.add(String.valueOf(result.getString(2)));
                list.add(result.getString(3));
                data.add(list);
            }
            /*
            making the columns dynamically
             */
            for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(result.getMetaData().getColumnName(i + 1));
                col.setStyle("-fx-alignment : center;");
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>() {
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        //System.out.println(param.getValue().get(j).toString());
                        return new SimpleStringProperty(param.getValue().get(j).toString());

                    }
                });

                if ("Student_id".equalsIgnoreCase(col.getText())) {
                    col.setVisible(false);
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        students_table.getColumns().add(col);
                    }

                });

            }
            /*
            adding the rows from the data obtained in "data" variable from above
             */
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    students_table.setItems(data);
                    students_table.setTableMenuButtonVisible(true);
                }

            });

        } catch (Exception ex) {
            System.out.println("Exception at makeStudentTable() "
                    + "at ReportController" + ex.getMessage());
        }
    }

    private void reportAbout() {
        try {
            Connection conn = database.Connection.conn;
            String exam = exam_choicebox.getSelectionModel().getSelectedItem().toString();
            String grade = grade_choicebox.getSelectionModel().getSelectedItem().toString();
            String section = section_choicebox.getSelectionModel().getSelectedItem().toString();
            Platform.runLater(() -> {
                examination_label.setText(exam);
                grade_label.setText(grade);
                section_label.setText(section);
            });

            //finding and assigning gender count
            String query = "SELECT Gender,Count(Gender) FROM year_" + year + "_student "
                    + "WHERE Grade_id IN (SELECT Grade_id FROM year_" + year + "_grade "
                    + "WHERE Grade = '" + grade + "' AND Section = '" + section + "') "
                    + "GROUP BY Gender ;";
            ResultSet result = conn.createStatement().executeQuery(query);

            while (result.next()) {
                String gen = result.getString(1);
                int count = result.getInt(2);
                if ("Male".equalsIgnoreCase(gen) || "M".equalsIgnoreCase(gen)) {
                    Platform.runLater(() -> {
                        final int cnt = count;
                        male_label.setText("Male : " + String.valueOf(cnt));

                    });
                } else if ("Female".equalsIgnoreCase(gen) || "F".equalsIgnoreCase(gen)) {
                    Platform.runLater(() -> {
                        final int cnt = count;
                        female_label.setText("Female : " + String.valueOf(cnt));

                    });
                }
            }
            //finding the table_name of recent ledger_id
            query = "SELECT Table_name FROM year_" + year + "_ledger WHERE "
                    + "Ledger_id = " + ledger_id.get() + " ;";
            String table_name = null;
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                table_name = result.getString(1);
            }
            //finding and assigning passed and failed count
            query = "SELECT Result,Count(Result) FROM " + table_name + " "
                    + " GROUP BY Result ;";
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                String gen = result.getString(1);
                int count = result.getInt(2);
                if ("Pass".equalsIgnoreCase(gen) || "passed".equalsIgnoreCase(gen)) {
                    Platform.runLater(() -> {
                        final int cnt = count;
                        passed_label.setText("Passed : " + String.valueOf(cnt));
                    });

                } else if ("Fail".equalsIgnoreCase(gen) || "Failed".equalsIgnoreCase(gen)) {
                    Platform.runLater(() -> {
                        final int cnt = count;
                        failed_label.setText("Failed : " + String.valueOf(cnt));
                    });

                }
            }

        } catch (Exception e) {
            System.out.println("Exception at reportAbout() at ReportController "
                    + " : " + e.getMessage());
        }
    }

    private void examNameInput() {
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Full_name FROM year_" + year + "_exam WHERE "
                    + "Exam_id IN (SELECT Exam_id FROM year_" + year + "_ledger "
                    + "WHERE Ledger_id = " + ledger_id.get() + ")";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                String full_name = result.getString(1);
                Platform.runLater(() -> {
                    final String fn = full_name;
                    exam_name_textfield.clear();
                    exam_name_textfield.setText(fn);
                });
            }
        } catch (Exception e) {
            System.out.println("Exeception at examnameInput() "
                    + "at ReportcardController : " + e.getMessage());
        }
    }

    private void rollChoiceBox() {
        Platform.runLater(() -> {
            roll_combobox.getItems().clear();
            roll_combobox.getItems().addAll(roll_no_list);
            roll_combobox.setVisibleRowCount(5);
            roll_combobox.getSelectionModel().selectFirst();
        });

    }

    private void listenerForRollComboBox() {
        roll_combobox.getSelectionModel().selectedItemProperty().addListener(e -> {
            roll_combobox.getScene().setCursor(Cursor.WAIT);

            Task<Void> task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    roll_combobox.getScene().setCursor(Cursor.WAIT);
                    Platform.runLater(() -> {
                        prev_label.setVisible(true);
                        next_label.setVisible(true);
                        final int index = roll_combobox.getSelectionModel().getSelectedIndex();
                        if (index == 0) {
                            prev_label.setVisible(false);
                        }
                        if (index == (roll_combobox.getItems().size() - 1)) {
                            next_label.setVisible(false);
                        }
                    });
                    try {
                        roll_no.set(roll_combobox.getSelectionModel().getSelectedItem().toString());
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                    roll_combobox.getScene().setCursor(Cursor.DEFAULT);
                    return null;
                }
            };
            Thread t = new Thread(task, "Listener for roll combobox thread");
            t.setPriority(8);
            t.start();
            roll_combobox.getScene().setCursor(Cursor.DEFAULT);

        });

    }

    private void resultDate() {
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Result_date FROM year_" + year + "_exam WHERE "
                    + "Exam_id IN (SELECT Exam_id FROM year_" + year + "_ledger "
                    + "WHERE Ledger_id = " + ledger_id.get() + ")";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                String date = result.getString(1);
                Platform.runLater(() -> {
                    final String res_date = date;
                    date_textfield.clear();
                    date_textfield.setText(date);
                });
                File temp_file = new File("temp_exam_date.txt");
                temp_file.deleteOnExit();
                if (!temp_file.exists()) {
                    temp_file.createNewFile();
                }
                //false in the argument clears the existing data and write to the file
                //if it was true then it will append to the existing data
                FileWriter file_writer = new FileWriter(temp_file, false);
                file_writer.write(date);
                file_writer.close();
            }
        } catch (Exception e) {
            System.out.println("Exception at resultDate() at "
                    + "ReportcardController : " + e.getMessage());
        }
    }

    private void marksReport() {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/report/reportcard.fxml"));
            borderpane = fxml.load();
            ReportcardController controller = (ReportcardController) fxml.getController();
            int index = roll_no_list.indexOf(roll_no.get());
            String student_id = student_id_list.get(index);
            controller.setValues(student_id, ledger_id.get());
            stackpane.getChildren().setAll(borderpane);
        } catch (Exception e) {
            System.out.println("Exception at scrollPane() at ReportController : "
                    + e.getMessage());
            e.printStackTrace();
        }

    }

    @FXML
    private void clickEditDateBtn(MouseEvent event) {
        String btn_name = edit_date_btn.getText();
        if ("Edit".equals(btn_name)) {
            date_textfield.setEditable(true);
            edit_date_btn.setText("Done");
        } else if ("Done".equals(btn_name)) {
            try {
                String temp_date = date_textfield.getText();
                File temp_file = new File("temp_exam_date.txt");
                temp_file.deleteOnExit();
                if (!temp_file.exists()) {
                    temp_file.createNewFile();
                }
                //false in the argument clears the existing data and write to the file
                //if it was true then it will append to the existing data
                FileWriter file_writer = new FileWriter(temp_file, false);
                file_writer.write(temp_date);
                file_writer.close();
                date_textfield.setEditable(false);
                edit_date_btn.setText("Edit");
            } catch (Exception ex) {
                System.out.println("Exception at clickEditDateBtn() "
                        + "at ReportController : " + ex.getMessage());
            }
        }
    }

    @FXML
    private void clickEditExamNameBtn(MouseEvent event) {
        String btn_name = edit_exam_name_btn.getText();
        if ("Edit".equals(btn_name)) {
            exam_name_textfield.setEditable(true);
            edit_exam_name_btn.setText("Done");
        } else if ("Done".equals(btn_name)) {
            String full_name = exam_name_textfield.getText();
            String exam_name = examination_label.getText();;
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "UPDATE year_" + year + "_exam SET Full_name ="
                    + " '" + full_name + "' WHERE Name = '" + exam_name + "' ;";
            try {
                conn.createStatement().executeUpdate(query);
            } catch (SQLException ex) {
                exam_name_textfield.setEditable(true);
                System.out.println("Exception at clickEditExamNameBtn(MouseEvent event) "
                        + "at ReportController : " + ex.getMessage());
            }
            exam_name_textfield.setEditable(false);
            edit_exam_name_btn.setText("Edit");
        }
    }

    @FXML
    private void clickNextLabel(MouseEvent event) {
        roll_combobox.getScene().setCursor(Cursor.DEFAULT);
        roll_combobox.getScene().setCursor(Cursor.WAIT);
        int curr_index = roll_combobox.getSelectionModel().getSelectedIndex();
        roll_combobox.getSelectionModel().select(curr_index + 1);
        roll_combobox.getScene().setCursor(Cursor.DEFAULT);

    }

    @FXML
    private void clickPrevLabel(MouseEvent event) {
        roll_combobox.getScene().setCursor(Cursor.DEFAULT);
        roll_combobox.getScene().setCursor(Cursor.WAIT);
        int curr_index = roll_combobox.getSelectionModel().getSelectedIndex();
        roll_combobox.getSelectionModel().select(curr_index - 1);
        roll_combobox.getScene().setCursor(Cursor.DEFAULT);

    }

    @FXML
    private void clickSaveAllBtn(MouseEvent event) {
        if (save_from_combobox.getSelectionModel().getSelectedItem() == null) {
            save_from_combobox.getSelectionModel().selectFirst();
        } else if (save_to_combobox.getSelectionModel().getSelectedItem() == null) {
            save_from_combobox.getSelectionModel().selectLast();
        } else if (save_from_combobox.getSelectionModel().getSelectedItem() == null
                && save_to_combobox.getSelectionModel().getSelectedItem() == null) {
            save_from_combobox.getSelectionModel().selectFirst();
            save_to_combobox.getSelectionModel().selectLast();
        }
        String save_from = save_from_combobox.getSelectionModel().getSelectedItem().toString();
        String save_to = save_to_combobox.getSelectionModel().getSelectedItem().toString();
        if (Double.valueOf(save_from) > Double.valueOf(save_to)) {
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("'From' Must be Greater than 'To' \n i.e. To>From");
            a.setContentText("Report to save from is always less than report to save "
                    + "to");
            a.show();
        } else {
            try {
                DirectoryChooser file_chooser = new DirectoryChooser();
                file_chooser.setTitle("Save Bulk Report Card");
                file_chooser.setInitialDirectory(
                        new File(System.getProperty("user.home"))
                );
                //storing the current_roll_no in a var so that it can be 
                //reassigned once the task of saving all is completed
                String stored_roll_no = roll_combobox.getSelectionModel().getSelectedItem().toString();

                Stage stage = (Stage) save_all_btn.getScene().getWindow();
                File path_file = file_chooser.showDialog(stage);
                if (path_file != null) {
                    progress_indicator.setVisible(true);
                    Task<Void> task = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            int from = roll_no_list.indexOf(save_from);
                            int to = roll_no_list.indexOf(save_to);
                            //Exam name,grade,section for file naming
                            String exam = examination_label.getText();
                            String grade = grade_label.getText();
                            String section = section_label.getText();
                            //getting the col having of the student's name
                            TableColumn col = (TableColumn) students_table.getColumns().get(2);
                            String file_name = exam + "_" + grade + "_" + section + "_";
                            Platform.runLater(() -> {
                                for (int i = from, j = 1; i <= to; i++, j++) {
                                    String current_file_name = file_name + roll_no_list.get(i);
                                    String student_name = col.getCellData(i).toString();
                                    current_file_name = current_file_name + "_" + student_name + ".png";
                                    String path = path_file.getAbsolutePath();
                                    path = path + "/" + current_file_name;
                                    final File file_to_save = new File(path);
                                    final int index = i;
                                    BorderPane report_card = new BorderPane();
                                    FXMLLoader fxml = new FXMLLoader(getClass().getResource("/report/reportcard.fxml"));
                                    try {
                                        report_card = fxml.load();
                                    } catch (IOException ex) {
                                        Logger.getLogger(ReportController.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    ReportcardController controller = (ReportcardController) fxml.getController();
                                    final String student_id = student_id_list.get(index);
                                    final BorderPane report_borderpane = report_card;
                                    final double progress_made = j;
                                    final double total_task = (to - from + 1);

                                    save_all_btn.getScene().setCursor(Cursor.WAIT);
                                    controller.setValues(student_id, ledger_id.get());
                                    Scene scene = new Scene(report_borderpane);
                                    Stage window = new Stage();
                                    window.setScene(scene);
                                    window.show();
                                    //taking the snapshot of the broderpane object i.e report_card
                                    //created above
                                    //i.e marksheet, so creating
                                    //an object to store that snapshot
                                    SnapshotParameters screenshot = new SnapshotParameters();
                                    //snapshot of borderpane is take and is assigned to WritableImage
                                    WritableImage report = report_borderpane.snapshot(new SnapshotParameters(), null);
                                    //closing window
                                    window.close();
                                    try {
                                        ImageIO.write(SwingFXUtils.fromFXImage(report, null), "png", file_to_save);
                                    } catch (IOException ex) {
                                        System.out.println("Exception while writing image "
                                                + "inside task at saveAllBtn() "
                                                + "at ReportController : "
                                                + ex.getMessage());
                                    }
                                    save_all_btn.getScene().setCursor(Cursor.DEFAULT);
                                    updateProgress(progress_made, total_task);
                                
                                }
                            });

                            roll_combobox.getSelectionModel().select(stored_roll_no);

                            return null;
                        }
                    };
                    //binding the progress indicator to the task
                    progress_indicator.progressProperty().bind(
                            task.progressProperty()
                    );
                    // color the indicator green when the work is complete.
                    progress_indicator.progressProperty().addListener(observable -> {
                        if (progress_indicator.getProgress() >= 1) {
                            progress_indicator.setStyle("-fx-accent: forestgreen;");
                        } else {
                            progress_indicator.setStyle("progress-indicator");
                        }
                    });
                    Thread t = new Thread(task, "Save all button thread");
                    t.setPriority(10);
                    t.start();
                }
                roll_no.set(stored_roll_no);
            } catch (Exception e) {
                System.out.println("Exception at clickSaveAllBtn(MOuseEvent event)"
                        + " at ReportController : "
                        + e.getMessage());
                e.printStackTrace();
            }

        }
    }

    @FXML
    private void clickSaveCurrentBtn(MouseEvent event) {
        try {
            FileChooser file_chooser = new FileChooser();
            file_chooser.setInitialFileName("Filename.png");
            file_chooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All", "*.*"),
                    new FileChooser.ExtensionFilter("PNG (*.Png)", "*.png")
            );
            file_chooser.setTitle("Save Report Card");
            file_chooser.setInitialDirectory(
                    new File(System.getProperty("user.home"))
            );
            Stage stage = (Stage) save_current_btn.getScene().getWindow();
            File report_card = file_chooser.showSaveDialog(stage);
            if (report_card != null) {
                //taking the snapshot of the broderpane i.e marksheet, so creating
                //an object to store that snapshot
                SnapshotParameters screenshot = new SnapshotParameters();
                //snapshot of borderpane is take and is assigned to WritableImage
                WritableImage report = borderpane.snapshot(new SnapshotParameters(), null);
                ImageIO.write(SwingFXUtils.fromFXImage(report, null), "png", report_card);

            }
        } catch (Exception e) {
            System.out.println("Exception at clickSaveCurrentBtn(MouseEvent event) "
                    + "at ReportController : " + e.getMessage());
        }
    }

    @FXML
    private void clickViewOrgBtn(MouseEvent event) {
        try {
            FXMLLoader fxml = new FXMLLoader(getClass().getResource("/organization/organization.fxml"));
            Parent root = fxml.load();
            Scene scene = new Scene(root);
            Stage window = new Stage();
            window.setScene(scene);
            window.initModality(Modality.APPLICATION_MODAL);
            window.show();
            window.setOnCloseRequest(e -> {
                if (!(roll_no.get() == null || ledger_id.get() == null)) {
                    marksReport();

                }

            });
        } catch (Exception e) {
            System.out.println("Exception at clickViewOrgBtn() at ReportController "
                    + e.getMessage());
        }
    }

    @FXML
    private void clickViewSelectedBtn(MouseEvent event) {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int index = students_table.getSelectionModel().getSelectedIndex();
                if (index >= 0) {
                    TableColumn col = (TableColumn) students_table.getColumns().get(1);
                    String roll = String.valueOf(col.getCellData(index));
                    Platform.runLater(() -> {
                        roll_no.set(roll);
                        roll_combobox.getSelectionModel().select(roll);
                    });
                }
                return null;
            }
        };
        Thread t = new Thread(task, "View Selected Btn thread ");
        t.setPriority(10);
        t.start();
    }

    @FXML
    private void clickViewReportBtn(MouseEvent event) {
        try {
            Connection conn = database.Connection.conn;
            String exam = exam_choicebox.getSelectionModel().getSelectedItem().toString();;
            String grade = grade_choicebox.getSelectionModel().getSelectedItem().toString();
            String section = section_choicebox.getSelectionModel().getSelectedItem().toString();
            if (!exam.isEmpty() && !grade.isEmpty() && !section.isEmpty()) {
                /*
                FINDING the ledger_id of the current selection and then
                assining that value to the current ledger_id
                 */
                String query = "SELECT Exam_id FROM year_" + year + "_exam "
                        + "WHERE Name = '" + exam + "' ;";
                ResultSet result = conn.createStatement().executeQuery(query);
                String exam_id = null, grade_id = null;
                while (result.next()) {
                    exam_id = String.valueOf(result.getInt(1));
                }
                query = "SELECT Grade_id FROM year_" + year + "_grade "
                        + "WHERE Grade = '" + grade + "'"
                        + " AND Section = '" + section + "';";
                result = conn.createStatement().executeQuery(query);
                while (result.next()) {
                    grade_id = String.valueOf(result.getInt(1));
                }
                query = "SELECT Ledger_id FROM year_" + year + "_ledger WHERE "
                        + "Exam_id = " + exam_id + " AND Grade_id = " + grade_id;
                result = conn.createStatement().executeQuery(query);
                String led_id = null;
                while (result.next()) {
                    led_id = String.valueOf(result.getInt(1));
                }
                this.ledger_id.set(led_id);
            }
        } catch (Exception e) {
            System.out.println("Exception at clickViewSelectedBtn(MouseEvent event) "
                    + "at ReportController : " + e.getMessage());
        }
    }
}

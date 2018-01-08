/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import calculator.CalculateAverageMarkOfSubject;
import calculator.CalculateGrade;
import calculator.CalculateHighestMarkOfSubject;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class ReportcardController implements Initializable {

    @FXML
    private BorderPane report_BorderPane;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Label label3;

    @FXML
    private Label label4;

    @FXML
    private ImageView school_logo;

    @FXML
    private Label name_label;

    @FXML
    private Label roll_label;

    @FXML
    private Label grade_label;

    @FXML
    private Label section_label;

    @FXML
    private GridPane marks_table;

    @FXML
    private GridPane grading_system_table;

    @FXML
    private Label result_label;

    @FXML
    private Label percentage_label;

    @FXML
    private Label division_label;

    @FXML
    private Label rank_label;

    @FXML
    private Label class_rank_label;

    @FXML
    private Label gpa_label;

    @FXML
    private TextArea remarks_textarea;

    @FXML
    private Label issue_date_label;

    private String student_id;
    private String ledger_id;
    private String year = LoginController.current_year;
    private String table_name;
    private Task<Void> task = null;

    /**
     * get the task and bind the progress bar with it for the task progress view
     * of creating the report card
     *
     * @return
     */
    public Task<Void> getCurrentTask() {
        if (task != null) {
            return task;
        } else {
            return null;
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    public BorderPane getReportCard(String student_id, String ledger_id) {
        setValues(student_id, ledger_id);
        return getReportCard();
    }

    /**
     * find the table_name of the ledger whose ledger_id is given
     *
     */
    private void findTableName() {
        Connection conn = database.Connection.conn;
        String query = "SELECT Table_name FROM year_" + year + "_ledger WHERE "
                + "Ledger_id = " + ledger_id;
        try {
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                table_name = result.getString(1);
            }
        } catch (Exception e) {
            System.out.println("Exception at findTableName() at "
                    + "ReportcardController : " + e.getMessage());
        }
    }

    /**
     * it generates the report card and return it in the form of borderpane
     *
     * @return
     */
    private BorderPane getReportCard() {
        task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                fillLabel1();
                fillLabel2();
                fillLabel3();
                fillLabel4();
                fillLogo();
                fillRemarks();
                updateProgress(1, 10);
                fillFinalDatas();
                updateProgress(2, 10);
                fillInfoTextFields();
                updateProgress(3, 10);
                fillIssueDate();
                updateProgress(4, 10);
                makeGradingTable();
                updateProgress(5, 10);
                makeMarksTable();
                updateProgress(10, 10);
                return null;
            }
        };
        task.run();
        return report_BorderPane;

    }

    /**
     * set the values required for generating a report card, these values should
     * be set before getting the reportcard
     *
     * @param student_id
     * @param ledger_id
     */
    public void setValues(String student_id, String ledger_id) {
        this.student_id = student_id;
        this.ledger_id = ledger_id;
        findTableName();
        getReportCard();

    }

    /**
     * make the table which includes the marks of the student
     *
     */
    private void makeMarksTable() {
        Connection conn = database.Connection.conn;
        String query = null;
        //observablelist for storing the datas for the table
        ObservableList<ObservableList> marks = FXCollections.observableArrayList();
        marks_table.getChildren().clear();
        try {
            Statement st = conn.createStatement();
            query = "DROP TABLE IF EXISTS temp;";
            st.addBatch(query);
            query = "CREATE TEMPORARY TABLE IF NOT EXISTS temp "
                    + "SELECT * FROM year_" + year + "_subject "
                    + "NATURAL JOIN year_" + year + "_marks "
                    + "WHERE "
                    + "year_" + year + "_subject.Subject_id="
                    + "year_" + year + "_marks.Subject_id"
                    + " AND year_" + year + "_marks.Ledger_id=" + ledger_id;
            st.addBatch(query);
            query = "DROP TABLE IF EXISTS temp2";
            st.addBatch(query);
            query = "CREATE TEMPORARY TABLE IF NOT EXISTS temp2("
                    + "SN INT PRIMARY KEY AUTO_INCREMENT "
                    + ",Subject_title varchar(255) DEFAULT 'null'"
                    + ",`OM` varchar(255) DEFAULT 'null' "
                    + ",`AVG` varchar(255) DEFAULT 'null' "
                    + ",`HM` varchar(255) DEFAULT 'null' "
                    + ")";
            st.addBatch(query);
            st.executeBatch();
            query = "SELECT Subject_title FROM temp";
            ResultSet result = conn.createStatement().executeQuery(query);

            while (result.next()) {
                String subject_title = result.getString(1);
                //finding the obtained marks of a subjects
                query = "SELECT `" + subject_title + "` "
                        + " FROM " + table_name
                        + " WHERE Student_id = " + student_id;
                ResultSet res = conn.createStatement().executeQuery(query);
                String ob_marks = null, avg = null, max = null;
                while (res.next()) {
                    ob_marks = String.valueOf(res.getFloat(1));
                }
                //finding average  marks of a subject
                CalculateAverageMarkOfSubject average_marks = new CalculateAverageMarkOfSubject();
                double average = average_marks.getAverageOfGrade(table_name, subject_title);
                avg = String.valueOf(Math.round(average * 100D) / 100D);
                //finding highest/max marks of a subject
                CalculateHighestMarkOfSubject highest_marks = new CalculateHighestMarkOfSubject();
                double highest = highest_marks.getHighestOfGrade(table_name, subject_title);
                max = String.valueOf(Math.round(highest * 100D) / 100D);
                /*
                for the single section of a grade the following code will work fine
                but when it comes to multiple section it won't work
                
                 */
//                System.out.println("Average of "+subject_title + " : " + avg);
//                query = "SELECT AVG(`" + subject_title + "`)"
//                        + ",MAX(`" + subject_title + "`)"
//                        + " FROM " + table_name + " ;";
//                res = conn.createStatement().executeQuery(query);
//                while (res.next()) {
//                    avg = String.valueOf(Math.round(res.getFloat(1) * 100D) / 100D);
//                    max = String.valueOf(res.getFloat(2));
//                }
                query = "INSERT INTO temp2(`Subject_title`"
                        + ",`OM`,`AVG`,`HM`) "
                        + "VALUES ('" + subject_title + "','" + ob_marks + "'"
                        + ",'" + avg + "','" + max + "')";
                st.addBatch(query);
            }
            query = "DROP TABLE IF EXISTS temp3 ;";
            st.addBatch(query);
            query = "CREATE TEMPORARY TABLE IF NOT EXISTS temp3 "
                    + "(SELECT CAST(SN AS CHAR) ,`Subject_name` "
                    + ",`Type`,`FM`,`PM`,`OM`,`AVG`,`HM` FROM "
                    + "temp NATURAL JOIN temp2 WHERE "
                    + "temp.Subject_title = temp2.Subject_title) ;";
            st.addBatch(query);
            st.executeBatch();
            query = "SELECT SUM(CAST(`FM` AS DECIMAL(10,2))),"
                    + "SUM(CAST(`PM` AS DECIMAL(10,2))),"
                    + "SUM(CAST(`OM` AS DECIMAL(10,2))) FROM temp3";
            result = conn.createStatement().executeQuery(query);
            String total_fm = null, total_pm = null, total_om = null;
            while (result.next()) {
                total_fm = String.valueOf(result.getFloat(1));
                total_pm = String.valueOf(result.getFloat(2));
                total_om = String.valueOf(result.getString(3));
            }
            query = "INSERT INTO temp3(`CAST(SN AS CHAR)`,`Subject_name`,`Type`,`FM`,`PM`,`OM`) "
                    + "VALUES('null','Total','null','" + total_fm + "','" + total_pm
                    + "','" + total_om + "')";
            st.addBatch(query);
            st.executeBatch();
            query = "SELECT `CAST(SN AS CHAR)` ,`Subject_name` as Subjects "
                    + ",`Type`,`FM`,`PM`,`OM`,`AVG`,`HM` FROM "
                    + " temp3";
            result = conn.createStatement().executeQuery(query);
            /**
             * *****Making the marks table with the gridpane
             * ******************************* ******************
             */
            //get all the column headers
            for (int i = 0; i < result.getMetaData().getColumnCount(); i++) {
                String header = result.getMetaData().getColumnName(i + 1);
                if ("CAST(SN AS CHAR)".equalsIgnoreCase(header)) {
                    header = "SN";
                }
                Label col_header = new Label(header);
                col_header.setFont(Font.font(12));
                col_header.setAlignment(Pos.CENTER);
                col_header.setPadding(new Insets(5, 16, 5, 16));
                marks_table.add(col_header, i, 0);
            }
            // get all the data related to marks 
            while (result.next()) {
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= result.getMetaData().getColumnCount(); i++) {
                    //Iterate Column
                    try {
                        if (Double.valueOf(result.getString(i)) < 0) {
                            row.add("A");
                        } else {
                            row.add(result.getString(i));
                        }
                    } catch (NumberFormatException e) {
                        if ("null".equalsIgnoreCase(result.getString(i))) {
                            row.add("");
                        } else if ("Theory".equalsIgnoreCase(result.getString(i)) && i == 3) {
                            row.add(result.getString(i));
                        } else if ("Practical".equalsIgnoreCase(result.getString(i)) && i == 3) {
                            row.add(result.getString(i));
                        } else {
                            row.add(result.getString(i));
                        }
                    }
                }
                marks.add(row);
            }
            for (int i = 1; i <= marks.size(); i++) {
                ObservableList list = marks.get(i - 1);
                for (int j = 0; j < list.size(); j++) {
                    Label cell_data = new Label(String.valueOf(list.get(j)));
                    cell_data.setPadding(new Insets(0, 5, 0, 5));
                    cell_data.setFont(Font.font(12));
                    marks_table.add(cell_data, j, i);
                }
            }
            marks_table.setGridLinesVisible(true);
        } catch (Exception e) {
            System.out.println("Exception at makeMarksTable() "
                    + "at ReportcardController : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * fills the image view with the school logo from the database, simply
     * assigns the logo of the school in mark sheet(mark report)
     */
    private void fillLogo() {
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Logo FROM organization ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                InputStream is = result.getBinaryStream(1);
                File logo_file = new File("school_logo.png");
                logo_file.deleteOnExit();
                OutputStream out = new FileOutputStream(logo_file);
                String st = result.getString(1);
                int len = st.length();
                byte[] contents = new byte[len];
                int size = 0;
                while ((size = is.read(contents)) != -1) {
                    out.write(contents, 0, size);
                }
                Image image = new Image("file:school_logo.png");
                school_logo.setImage(image);

            }
        } catch (Exception e) {
            System.out.println("Exception at fillLogo() at ReportcardController : "
                    + e.getMessage());
        }
    }

    /**
     * fills the label at the top of report card and also sets the font value
     * based on the font value inserted for the label1
     */
    private void fillLabel1() {
        label1.setText("");
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Name FROM organization ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                if (!result.getString(1).isEmpty()) {
                    label1.setText(result.getString(1));
                }

            }
            label1.setFont(Font.font("Times New Roman", 17));
        } catch (Exception e) {
            System.out.println("Exception at fillLabel1() at ReportcardController : "
                    + e.getMessage());
        }
    }

    /**
     * fills the label at the second top of report card and also sets the font
     * value based on the font value inserted for the label1
     */
    private void fillLabel2() {
        label2.setText("");
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Established,Year_format FROM organization ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                if (!result.getString(1).isEmpty()) {
                    label2.setText("Estd : " + result.getString(1) + " " + result.getString(2));
                }
            }
            label2.setFont(Font.font("Times New Roman"));
        } catch (Exception e) {
            System.out.println("Exception at fillLabel2() at ReportcardController : "
                    + e.getMessage());
        }
    }

    /**
     * fills the label at the third top of report card and also sets the font
     * value based on the font value inserted for the label1
     */
    private void fillLabel3() {
        label3.setText("");
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Address FROM organization ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                if (!result.getString(1).isEmpty()) {
                    label3.setText(result.getString(1));
                }
            }
            label3.setFont(Font.font("Times New Roman"));
        } catch (Exception e) {
            System.out.println("Exception at fillLabel3() at ReportcardController : "
                    + e.getMessage());
        }
    }

    /**
     * fills the label at the fourth top of report card and also sets the font
     * value based on the font value inserted for the label1
     */
    private void fillLabel4() {
        label4.setText("");
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Full_name FROM year_" + year + "_exam WHERE Exam_id IN "
                    + "(SELECT Exam_id FROM year_" + year + "_ledger WHERE Ledger_id "
                    + " = " + ledger_id + ") ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                if (!result.getString(1).isEmpty()) {
                    label4.setText(result.getString(1));
                }
            }
            label4.setFont(Font.font("Times New Roman", 15));
        } catch (Exception e) {
            System.out.println("Exception at fillLabel4() at ReportcardController : "
                    + e.getMessage());
        }
    }

    /**
     * fill the remarks in the report card
     *
     */
    private void fillRemarks() {
        Connection conn = database.Connection.conn;
        String query = "SELECT Remarks FROM " + table_name
                + " WHERE Student_id = " + student_id;
        try {
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                remarks_textarea.setText(result.getString(1));
            }
        } catch (Exception e) {
            System.out.println("Exception at fillRemarks() at "
                    + "ReportcardConroller : " + e.getMessage());
        }
    }

    /**
     * fill the final datas like result,divison,percentage,etc in report
     *
     */
    private void fillFinalDatas() {
        Connection conn = database.Connection.conn;
        String query = "SELECT Result,Percentage,Division,Rank,ClassRank FROM "
                + table_name + " WHERE Student_id = " + student_id;
        try {
            ResultSet result = conn.createStatement().executeQuery(query);
            if (result.next()) {
                result_label.setText(result.getString(1));
                percentage_label.setText(result.getString(2));
                division_label.setText(result.getString(3));
                rank_label.setText(result.getString(4));
                class_rank_label.setText(result.getString(5));
                CalculateGrade gpa = new CalculateGrade();
                String gpa_value = gpa.calculateGPAOfAStudent(student_id, ledger_id);
                gpa_label.setText(gpa_value);
            } else {

            }
        } catch (Exception e) {
            System.out.println("Exception at fillFinalDatas() at"
                    + " ReportcardController : " + e.getMessage());
        }
    }

    /**
     * fill the info text fields like name,roll,grade,section
     *
     */
    private void fillInfoTextFields() {
        try {
            database.Connection.conn.close();
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Name,Roll,Grade,Section FROM "
                    + "year_" + year + "_student WHERE Student_id = "
                    + student_id + " ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                name_label.setText(result.getString(1));
                roll_label.setText("    " + String.valueOf(result.getInt(2)) + "    ");
                grade_label.setText("  " + result.getString(3) + "  ");
                if ("null".equals(result.getString(4))) {
                    section_label.setText("            ");
                } else {
                    section_label.setText("    " + result.getString(4) + "    ");
                }
            }
        } catch (Exception e) {
            System.out.println("Exception at fillInfoTextFields() "
                    + "at ReportcardController : " + e.getMessage());
        }
    }

    /**
     * fill the issue date, issue date is the result date stored in the database
     *
     */
    private void fillIssueDate() {
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT Result_date FROM year_" + year + "_exam "
                    + "WHERE Exam_id IN (SELECT Exam_id FROM "
                    + "year_" + year + "_ledger WHERE Ledger_id = " + ledger_id + ");";
            ResultSet result = conn.createStatement().executeQuery(query);
            String result_date = null;
            while (result.next()) {
                result_date = result.getString(1);
            }
            try {
                File temp_file = new File("temp_exam_date.txt");
                temp_file.deleteOnExit();
                String exam_result_date = null;
                FileInputStream fis = new FileInputStream(temp_file);
                DataInputStream reader = new DataInputStream(fis);
                Scanner scan = new Scanner(temp_file);
                while (scan.hasNextLine()) {
                    exam_result_date = scan.nextLine();
                }
                issue_date_label.setText(exam_result_date);
            } catch (Exception e) {
                System.out.println("Exception while reading file "
                        + "at fillIssueDate() "
                        + "at ReportcardController : " + e.getMessage());
                issue_date_label.setText(result_date);
            }
        } catch (Exception e) {
            System.out.println("Exception at fillIssueDate() "
                    + "at ReportcardController : " + e.getMessage());
        }
    }

    /**
     * make the table showing the information about the grading
     *
     */
    private void makeGradingTable() {
        try {
            database.Connection.conn.close();
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            grading_system_table.getChildren().clear();
            grading_system_table.setGridLinesVisible(true);
            String titles[] = {"From Per", "To Per", "Grade"};
            for (int i = 0; i < 3; i++) {
                Label headers = new Label(titles[i]);
                headers.setPadding(new Insets(1, 5, 1, 5));
                headers.setFont(Font.font(11));
                grading_system_table.add(headers, i, 0);
            }
            String query = "SELECT Per_from,Per_to,Grade_letter "
                    + "FROM year_" + year + "_grading "
                    + " ORDER BY Per_from DESC,Per_to DESC ;";
            ResultSet result = conn.createStatement().executeQuery(query);
            int i = 1;
            while (result.next()) {
                for (int j = 0; j < 3; j++) {
                    Label values = new Label();
                    values.setPadding(new Insets(0, 3, 0, 5));
                    values.setFont(Font.font(11));
                    try {
                        if (Integer.valueOf(result.getString(j + 1)) > 100) {
                            values.setText("100");
                        } else {
                            values.setText(result.getString(j + 1));
                        }
                    } catch (Exception e) {
                        values.setText(result.getString(j + 1));
                    }
                    grading_system_table.add(values, j, i);
                }
                i++;

            }
            grading_system_table.getColumnConstraints().get(0).setPrefWidth(60);
        } catch (Exception e) {
            System.out.println("Exception at makeGradingTable() at "
                    + "ReportcardController : " + e.getMessage());
            e.printStackTrace();
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings.newSession;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import settings.SettingsController;
import static settings.newSession.Addsession1Controller.session;
import static settings.newSession.Addsession1Controller.session_end;
import static settings.newSession.Addsession1Controller.session_name;
import static settings.newSession.Addsession1Controller.session_start;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class Addsession2Controller extends SettingsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private RadioButton import_existing;

    @FXML
    private ToggleGroup tooglegroup;

    @FXML
    private RadioButton create_new;

    @FXML
    private Button next_btn;

    @FXML
    private ChoiceBox import_year;

    public static String import_year_choice;

    public static Stage window = new Stage();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        import_year.setDisable(true);
        choiceBox();
        listenToogleGroup();
        onNextBtnEnterPressed();
    }

    public void onNextBtnEnterPressed() {
        next_btn.setOnKeyPressed(e -> {
            if (next_btn.isFocused()) {
                if (e.getCode() == KeyCode.ENTER) {
                    next_btn.fire();
                }
            }
        });

    }

    public void choiceBox() {
        for (String session : session_name) {
            if (!session.equals("0")) {
                import_year.getItems().add(session);
            }
        }
        import_year.getSelectionModel().selectedItemProperty().addListener(e -> {
            import_year_choice = import_year.getSelectionModel().getSelectedItem().toString();
        });
    }

    public void listenToogleGroup() {
        tooglegroup.selectedToggleProperty().addListener((o, old_toogle, new_toogle) -> {
            RadioButton chk = (RadioButton) new_toogle.getToggleGroup().getSelectedToggle();
            if (chk.getText().contains("Import Existing")) {
                next_btn.setText("Next");
                import_year.setDisable(false);
            } else {
                next_btn.setText("Finish");
                import_year.setDisable(true);
            }
        });
    }

    @FXML
    public void nextBtn(ActionEvent event) {
        if ("Next".equals(next_btn.getText())) {

        } else if ("Finish".equals(next_btn.getText())) {

            try {
                ResultSet result = null;
                database.Connection.connect();
                Connection conn = database.Connection.conn;
                conn.setAutoCommit(false);
                Statement statement = conn.createStatement();
                //deleting any table of same session if present
                statement.addBatch("DROP TABLE IF EXISTS `Year_" + session + "_student` ;");
                statement.addBatch("DROP TABLE IF EXISTS `Year_" + session + "_teacher`;");
                statement.addBatch("DROP TABLE  IF EXISTS `Year_" + session + "_subject`;");
                statement.addBatch("DROP TABLE IF EXISTS  `Year_" + session + "_grade`;");
                statement.addBatch("DROP TABLE IF EXISTS  `Year_" + session + "_grading`;");
                statement.addBatch("DROP TABLE IF EXISTS  `Year_" + session + "_percentage`;");
                statement.addBatch("DELETE FROM `session` WHERE session.Year='" + session + "';");
                statement.addBatch("DELETE FROM `table_details` WHERE table_details.Year='" + session + "';");
                //now making new session
                statement.addBatch("INSERT INTO `session` (`Year`, `Format`,`From`,`To` ) VALUES ('" + session + "','B.S','" + session_start + "','" + session_end + "');");
                statement.addBatch("CREATE TABLE Year_" + session + "_grade (\n"
                        + "Grade_id int primary key AUTO_INCREMENT,\n"
                        + "Grade varchar(20)  , \n"
                        + "Section varchar(20),\n"
                        + "Male varchar(20),\n"
                        + "Female varchar(20),\n"
                        + "No_of_students int\n"
                        + "\n"
                        + ");\n");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_grade','" + session + "','Grade');\n"
                        + "\n");
                statement.addBatch("CREATE TABLE Year_" + session + "_student (\n"
                        + "Student_id int PRIMARY KEY auto_increment ,\n"
                        + "Roll int NOT NULL,\n"
                        + "Name varchar(100) NOT NULL,\n"
                        + "Grade_id int NULL DEFAULT 0, \n"
                        + "Grade varchar(20) NOT NULL ,\n"
                        + "Section varchar(20) NULL DEFAULT 'null',\n"
                        + "Gender varchar(10) NOT  NULL,\n"
                        + "DOB varchar(30) 	NULL DEFAULT 'null',\n"
                        + "Address varchar(200)  NULL,\n"
                        + "Phone varchar(20)  NULL DEFAULT 'null',\n"
                        + "Father_name varchar(100)  NULL DEFAULT 'null',\n"
                        + "Mother_name varchar(100)  NULL DEFAULT 'null',\n"
                        + "Guardian_name varchar(100)  NULL DEFAULT 'null',\n"
                        + "Previous_school varchar(100) NULL DEFAULT 'null'\n"
                        + "\n"
                        + ");\n");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_student','" + session + "','Student');\n"
                        + "\n");
                statement.addBatch("CREATE TABLE Year_" + session + "_subject (\n"
                        + "Subject_id  int primary key AUTO_INCREMENT,\n"
                        + "Subject_name varchar(30) NOT NULL,\n"
                        + "Grade_id int  NULL DEFAULT 0 UNIQUE,\n"
                        + "Grade varchar(30) NULL DEFAULT 'null',\n"
                        + "Teacher_id varchar(10) NULL DEFAULT '0'\n"
                        + ");\n");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_subject','" + session + "','Subject');\n"
                        + "\n");
                statement.addBatch("CREATE TABLE Year_" + session + "_marks (\n"
                        + "Marks_id  int primary key AUTO_INCREMENT,\n"
                        + "Subject_id int NOT NULL,\n"
                        + "Subject_title VARCHAR(100) NOT NULL,\n"
                        + "Ledger_id int  NOT NULL ,\n"
                        + "FM float  NOT NULL,\n"
                        + "PM float  NOT NULL,\n"
                        + "Type VARCHAR(50) NOT NULL DEFAULT 'Theory'\n"
                        + ");\n");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_marks','" + session + "','Marks');\n"
                        + "\n");

                statement.addBatch("CREATE TABLE Year_" + session + "_teacher (\n"
                        + "Teacher_id int primary key AUTO_INCREMENT,\n"
                        + "Name varchar(100) NOT NULL,\n"
                        + "Gender varchar(30) NOT NULL,"
                        + "Phone varchar(30) NOT NULL,\n"
                        + "Address varchar(200) NOT NULL,\n"
                        + "Qualification varchar(200) NULL\n"
                        + ");\n");

                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_teacher','" + session + "','Teacher');\n"
                        + "\n");
                statement.addBatch("CREATE TABLE year_" + session + "_ledger(Ledger_id INT PRIMARY KEY AUTO_INCREMENT,"
                        + "`Table_name`  Varchar(255) NOT NULL,"
                        + "Grade_id INT NOT  NULL,"
                        + "Exam_id INT NOT NULL     );");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_ledger','" + session + "','Ledger');\n"
                        + "\n");

                statement.addBatch("CREATE TABLE year_" + session + "_exam(Exam_id INT PRIMARY KEY AUTO_INCREMENT,"
                        + "Name  Varchar(255) NOT NULL,"
                        + "Result_date varchar(30) NULL, "
                        + "Full_name varchar(255)  NULL default 'null'"
                        + "     );");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_term','" + session + "','Exam');\n"
                        + "\n");

                statement.addBatch("CREATE TABLE Year_" + session + "_percentage(\n"
                        + "`Division` varchar(30) primary key,\n"
                        + "`From` varchar(30) not null,\n"
                        + "`To` varchar(30) not null,\n"
                        + "`Division_name`  varchar(30) not null UNIQUE,\n"
                        + "CONSTRAINT unique_grade UNIQUE(`From`,`To`)\n"
                        + "\n"
                        + ");\n");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) VALUES ('Year_" + session + "_percentage','" + session + "','Percentage');\n"
                        + "\n");
                statement.addBatch("CREATE TABLE Year_" + session + "_grading(\n"
                        + "`Per_from` varchar(30) not null,\n"
                        + "`Per_to` varchar(30) not null,\n"
                        + "`Grade_letter` varchar(30) not null unique,\n"
                        + "`Grade_point` varchar(30) not null unique,\n"
                        + "CONSTRAINT unique_grade UNIQUE(Per_from,Per_to)\n"
                        + "\n"
                        + ");\n");
                statement.addBatch("INSERT INTO table_details(Table_name,`Year`,`Type`) "
                        + "VALUES ('Year_" + session + "_grading','" + session + "','Grading');\n");
                statement.executeBatch();
                conn.commit();

            } catch (Exception e) {
                System.out.println("exception at adddsession 2 while creating completely new sesssion");
                System.out.println(e.getMessage());
                e.printStackTrace();
                try {
                    database.Connection.conn.rollback();
                } catch (SQLException ex) {
                    System.out.println("Exception while rolling back changes in "
                            + "nextBtn() at Addsession2Controller : "
                            + ex.getMessage());
                }
            }

            Stage window = new Stage();
            window = (Stage) next_btn.getScene().getWindow();
            Stage stage = new Stage();
            try {
                Parent parent = FXMLLoader.load(getClass().getResource("/settings/settings.fxml"));
                Scene sc = new Scene(parent);
                stage.setScene(sc);
                stage.show();
            } catch (IOException ex) {
                System.out.println("Exception at onNextBtnClicked() "
                        + "at Addsession1Controller : " + ex.getMessage());
                ex.printStackTrace();
            }
            window.close();

        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledger;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import login.LoginController;
/**
 * FXML Controller class
 *
 * @author subas
 */
   
public class NewsheetController implements Initializable {

    /**
     * Initializes the controller class.
     */
      @FXML
    private ChoiceBox exam;

    @FXML
    private ChoiceBox grade;

    @FXML
    private ChoiceBox section;

    @FXML
    private Button create_btn;

    @FXML
    private Label label;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        label.setVisible(false);
        LoginController.current_year="2074";
        database.Connection.connect();
        choiceBox();
    }
    private void choiceBox(){
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query = "SELECT DISTINCT Name FROM year_"+LoginController.current_year+"_exam";
        try{
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                this.exam.getItems().add(result.getString(1));
            }
            query = "SELECT DISTINCT Grade FROM year_"+LoginController.current_year+"_grade";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                grade.getItems().add(result.getString(1));
            }
            query = "SELECT DISTINCT Section FROM year_"+LoginController.current_year+"_grade";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                section.getItems().add(result.getString(1));
            }
            this.exam.getSelectionModel().selectFirst();
            grade.getSelectionModel().selectFirst();
            section.getSelectionModel().selectFirst();
            listnerForChoiceBoxes();
            this.exam.getSelectionModel().selectedItemProperty().addListener(e->{
                listnerForChoiceBoxes();
            });
            grade.getSelectionModel().selectedItemProperty().addListener(e->{
                listnerForChoiceBoxes();
            });
            section.getSelectionModel().selectedItemProperty().addListener(e->{
                listnerForChoiceBoxes();
            });
        }
        catch(Exception e){
            System.out.println("Error at getting items for choiceBox in NewsheetController : "+ e.getMessage());
        }
    }
    private void listnerForChoiceBoxes(){
        create_btn.setDisable(false);
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        ObservableList list  = FXCollections.observableArrayList();
        String query = "SELECT Grade_id FROM year_"+LoginController.current_year+"_grade "
                + "WHERE Grade = '"+grade.getSelectionModel().getSelectedItem().toString()+"' "
                + "AND `Section` = '"+section.getSelectionModel().getSelectedItem().toString()+"' ;";
        try{
           
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                String grade_id;
                grade_id = String.valueOf(result.getInt(1));
                list.add(this.exam.getSelectionModel().getSelectedItem().toString());
                list.add(grade_id);
            }            
            query = "SELECT DISTINCT Name,Grade_id FROM year_"+LoginController.current_year+"_exam"
                    + " CROSS JOIN year_"+LoginController.current_year+"_grade ;";
                    
            result = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            while(result.next()){
                ObservableList list2 = FXCollections.observableArrayList();
                list2.add(result.getString(1));
                list2.add(result.getString(2));
                data.add(list2);
            }
            String year = LoginController.current_year;
            query = "SELECT DISTINCT `Name`,Grade_id FROM year_"+LoginController.current_year+"_ledger INNER JOIN "
                    + "year_"+year+"_exam "
                    + "WHERE year_"+year+"_ledger.Exam_id = year_"+year+"_exam.Exam_id";
            result = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> already_present = FXCollections.observableArrayList();
            while(result.next()){                
                ObservableList ob = FXCollections.observableArrayList();
                ob.add(result.getString(1));
                ob.add(result.getString(2));
                already_present.add(ob);
                }            
                       
            System.out.println("All Grades : " + data);
            System.out.println("Selected grade : " + list);
            System.out.println("Does selected grade  exist : " + data.contains(list));           
            System.out.println("Already Present : " + already_present);
            System.out.println("Is list already present : " + already_present.contains(list));
            if(already_present.contains(list)){
                label.setText("Sheet is already present for selection");
                label.setVisible(true);
                create_btn.setDisable(true);
            }
            else{
                if(data.contains(list)){
                    label.setVisible(false);
                    create_btn.setDisable(false);
                }
                else{
                    label.setText("Such Grade and Section combination does not exists");
                    label.setVisible(true);
                    create_btn.setDisable(true);
                }
                
            }
           }
        catch(Exception e){
            create_btn.setDisable(true);
            System.out.println("Exception at listnerForChoiceBoxes :" + e.getMessage());
        }
    }
    @FXML
    private void clickCreateBtn(MouseEvent event) {
        Connection conn = database.Connection.conn;
        String exam = this.exam.getSelectionModel().getSelectedItem().toString();
        String grade = this.grade.getSelectionModel().getSelectedItem().toString();
        String section = this.section.getSelectionModel().getSelectedItem().toString();
        String year = LoginController.current_year;
        String query = "SELECT Exam_id FROM year_"+year+"_exam WHERE Name = '"+exam+"';";
        ResultSet result = null;
        try{
            String exam_id = null;
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
            exam_id = String.valueOf(result.getInt(1));
            }
            String grade_id = null;
            query = "SELECT Grade_id FROM year_"+year+"_grade WHERE Grade = '"+grade+"' "
                    + "AND `Section` = '"+section+"' ;";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                grade_id = String.valueOf(result.getString(1));
            }
            String table_name = "year_"+year+"_"+exam+"_"+grade_id;
            
            query = "INSERT INTO year_"+year+"_ledger (Table_name,Grade_id,Exam_id) VALUES "
                    + "('"+table_name+"',"+grade_id+","+exam_id+")";
            conn.createStatement().execute(query);
            
//            query = "SELECT Subject_name FROM year_"+year+"_subject WHERE Grade_id = "+grade_id+";";
//            result = conn.createStatement().executeQuery(query);
            int i=0;
//            while(result.next()){
//                i++;
//            }
//            String[] subject_col = new String[i];
//            result.beforeFirst();
//            i=0;
//            while(result.next()){
//                subject_col[i] = result.getString(1);
//                i++;
//            }
            Statement  st = conn.createStatement();
            query = "CREATE TABLE "+table_name+" ( Student_id INT NOT NULL ";
//            for(i=0;i<subject_col.length;i++){
//                query = query + " , \n `" +subject_col[i]+"` VARCHAR(30) DEFAULT '0'";
//            }
             query =   query + " , \n `Total` VARCHAR(30) DEFAULT '0'"
                     + " , \n `Percentage` VARCHAR(30) DEFAULT '0'"
                     + " , \n `Result` VARCHAR(30) DEFAULT 'Fail'"
                     + " , \n `Division` VARCHAR(30) DEFAULT 'Failed'"
                     + " , \n `CRank` VARCHAR(30) DEFAULT '0'"
                     + " , \n `Rank` VARCHAR(30) DEFAULT '0'"
                     + " , \n `Remarks` VARCHAR(255) DEFAULT 'None' );";     
             System.out.println(query);
             st.addBatch(query);
             query = "INSERT INTO "+table_name+" (Student_id) SELECT Student_id"
                     + " FROM year_"+year+"_student WHERE Grade_id = "+grade_id+" ;";
             st.addBatch(query);
             st.executeBatch();            
            //alert for successful addition
            Alert a = new Alert(AlertType.INFORMATION);
            a.setHeaderText("New sheet added successfully");
            a.setContentText("A new sheet of Grade : '" +grade+ "' and "
                    + "Section : '"+section+"' was created successfully");
            a.show();
            this.exam.getSelectionModel().selectFirst();
            this.grade.getSelectionModel().selectFirst();
            this.section.getSelectionModel().selectFirst();
            
        }
        catch(Exception e){
            System.out.println("Exception at clickCreateBtn in NewsheetController :" + e.getMessage());
            }
    }
}

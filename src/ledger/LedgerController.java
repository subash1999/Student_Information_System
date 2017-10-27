/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledger;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import login.LoginController;
import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

/**
 * FXML Controller class
 *
 * @author subas 
 */
public class LedgerController implements Initializable {

    /**
     * Initializes the controller class.
     */
      @FXML
    private JFXTextField search_field;

    @FXML
    private Button add_exam;

    @FXML
    private ChoiceBox exam_choicebox;

    @FXML
    private ChoiceBox grade_choicebox;

    @FXML
    private ChoiceBox section_choicebox;

    @FXML
    private AnchorPane anchorpane_label;

    @FXML
    private Label label1;

    @FXML
    private TextField result_date_textfield;

    @FXML
    private Button edit_result_date;

    @FXML
    private RadioButton marks_input_radio_btn;

    @FXML
    private ToggleGroup options;

    @FXML
    private RadioButton marks_to_grading_radio_btn;

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
    private TextField fm_textfield;

    @FXML
    private ChoiceBox subject_choicebox;

    @FXML
    private TextField pm_textfield;

    @FXML
    private RadioButton theory_radiobtn;

    @FXML
    private ToggleGroup subject_type;

    @FXML
    private RadioButton practical_radiobtn;

    @FXML
    private Button add_subject_btn;

    @FXML
    private ChoiceBox subject_column_choicebox;

    @FXML
    private Button delete_subject_btn;

    @FXML
    private SpreadsheetView table_spread;

    @FXML
    private ImageView new_sheet;

    @FXML
    private ImageView delete_ledger_btn;
 
    @FXML
    private Label add_subject_label;
    
    @FXML
    private Button recalculate_btn;
    
    private  StringProperty ledger_id = new SimpleStringProperty() ;
   
    private String table_name;
    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    //only student_id,name,roll is currently set from year_2074_students table 
    //data from the year_2074_ledger and the table is not done
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database.Connection.connect();
        anchorpane_label.setVisible(false);
        table_spread.setShowRowHeader(false);
        GridBase grid = new GridBase(0,0);
        table_spread.setGrid(grid);
        result_date_textfield.setEditable(false);
        LoginController.current_year = "2074";
        choiceBox();
        listnerForLedgerId();
        resultDays();         
        addSubject();
        deleteSubject();
    }   
    private void listnerForLedgerId(){
        ledger_id.addListener(e->{
            gradeInformation();
            addSubject();
            deleteSubject();
        });
    }
    private void resultDays(){
        result_date_textfield.lengthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
           if (newValue.intValue() > oldValue.intValue()) {
               char ch = result_date_textfield.getText().charAt(oldValue.intValue());
               // Check if the new character is the number or other's
               if (!(ch >= '0' && ch <= '9' ||ch == '/' )) {
                   // if it's not number then just setText to previous one
                   result_date_textfield.setText(result_date_textfield.getText().substring(0,result_date_textfield.getText().length()-1));
               }
           }
       });
    }
    private void gradeInformation(){
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String ledger_id = this.ledger_id.get();
        String exam = this.exam_choicebox.getSelectionModel().getSelectedItem().toString();
        String grade = this.grade_choicebox.getSelectionModel().getSelectedItem().toString();
        String section = this.section_choicebox.getSelectionModel().getSelectedItem().toString();
        String year = LoginController.current_year;
        try{
            String query = "SELECT Grade_id FROM year_"+year+"_ledger WHERE Ledger_id = "
                    +ledger_id+" ;";
            result = conn.createStatement().executeQuery(query);
            String grade_id = null;
            while(result.next()){
                grade_id = String.valueOf(result.getInt(1));
            }            
            query = "SELECT Grade,Section FROM year_"+year+"_grade WHERE Grade_id in "
                    + "(SELECT Grade_id FROM year_"+year+"_ledger WHERE Ledger_id = "
                    +ledger_id+") ;";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                System.out.println(result.getString(1));
            grade_label.setText("Grade : " + result.getString(1) );
            section_label.setText("Section : " + result.getString(2));
            }
            query = "SELECT COUNT(Student_id) FROM year_"+year+"_student WHERE Gender = 'Male'"
                    + " AND Grade_id = "+grade_id;
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                male_label.setText("Male : "+ String.valueOf(result.getInt(1)));
            }
            query = "SELECT COUNT(Student_id) FROM year_"+year+"_student WHERE Gender = 'Female' "
                    + " AND Grade_id = "+grade_id;
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                female_label.setText("Female : "+ String.valueOf(result.getInt(1)));
            }
            query = "SELECT Exam_id FROM year_"+year+"_exam WHERE Name = '"+exam+"' ;";
            result = conn.createStatement().executeQuery(query);
            String exam_id = null;
            while(result.next()){
                exam_id = String.valueOf(result.getInt(1));
            }
            query = "SELECT Table_name FROM year_"+year+"_ledger WHERE Grade_id in "
                    + "(SELECT Grade_id FROM year_"+year+"_grade WHERE Grade = '"+grade+"' AND "
                    + "Section = '"+section+"') AND Exam_id = "+exam_id;
            result = conn.createStatement().executeQuery(query);
            String table_name = null;
            while(result.next()){
                table_name = result.getString(1);
            }
            query = "SELECT COUNT(Student_id) FROM "+table_name+" WHERE Result = 'Pass'";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                passed_label.setText("Passed : " + String.valueOf(result.getInt(1)));
            }
            query = "SELECT COUNT(Student_id) FROM "+table_name+" WHERE Result = 'Fail'";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                failed_label.setText("Failed : " + String.valueOf(result.getInt(1)));
            }
        }   
        catch(Exception e){
            System.out.println("Exception at gradeInformation() at LedgerController : " + e.getMessage());
        }
    }
    private void addSubject(){
         subject_choicebox.getItems().clear();
         fm_textfield.clear();
         pm_textfield.clear();
        Connection conn = database.Connection.conn;
        ResultSet result;
        String year = LoginController.current_year;
        try{
            String query = "SELECT Subject_name FROM year_"+year+"_subject WHERE "
                    + " Grade = "+grade_choicebox.getSelectionModel().getSelectedItem().toString()+" ;";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                subject_choicebox.getItems().add(result.getString(1));
            }
            subject_choicebox.getSelectionModel().selectFirst();
            //Making FullMarks textfield only accept integer and dot
           fm_textfield.lengthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
           if (newValue.intValue() > oldValue.intValue()) {
               char ch = fm_textfield.getText().charAt(oldValue.intValue());
               // Check if the new character is the number or other's
               if (!(ch >= '0' && ch <= '9' ||ch == '.' )) {
                   // if it's not number then just setText to previous one
                   fm_textfield.setText(fm_textfield.getText().substring(0,fm_textfield.getText().length()-1));
               }
           }
           });
           //Making PassMarks textfield only accept integer and dot
           pm_textfield.lengthProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
           if (newValue.intValue() > oldValue.intValue()) {
               char ch = pm_textfield.getText().charAt(oldValue.intValue());
               // Check if the new character is the number or other's
               if (!(ch >= '0' && ch <= '9' ||ch == '.' )) {
                   // if it's not number then just setText to previous one
                   pm_textfield.setText(pm_textfield.getText().substring(0,pm_textfield.getText().length()-1));
               }
           }
       });
 
        }
        catch(Exception e){
            System.out.println("Exception at addSubject() at LedgerController : " + e.getMessage());   
        }
        //Setting the theory radio btn selected
        theory_radiobtn.setSelected(true);
        listnerForAddSubject();
        //adding listner to subject_choicebox
        subject_choicebox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
        listnerForAddSubject();
      }
    });    //adding listner to subject_type togglegroup
        subject_type.selectedToggleProperty().addListener(e->{
            listnerForAddSubject();
        });
    }
    private void listnerForAddSubject(){
        String ledger_id = this.ledger_id.get();
        Connection conn = database.Connection.conn;
        ResultSet result ;
        String year = LoginController.current_year;
        String query ;
        try{
            /*getting the already present list from the database and checking if selected list
            is already present in the database */
            query = "SELECT Subject_name,Type FROM year_"+2074+"_marks INNER JOIN "+
                    "year_"+2074+"_subject WHERE year_"+2074+"_marks.Subject_id = "
                    + "year_"+2074+"_subject.Subject_id";
            result = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> already_present = FXCollections.observableArrayList();
            while(result.next()){
                ObservableList temp = FXCollections.observableArrayList();
                temp.add(result.getString(1));
                temp.add(result.getString(2));
                already_present.add(temp);
            }
            ObservableList selected = FXCollections.observableArrayList();
            selected.add(subject_choicebox.getSelectionModel().getSelectedItem().toString());
            String th_pr_type  = null;
            if(theory_radiobtn.isSelected()){
                th_pr_type = "Theory";
            }
            else if(practical_radiobtn.isSelected()){
                th_pr_type = "Practical";
            }
            selected.add(th_pr_type);
            if(already_present.contains(selected)){
                add_subject_label.setVisible(true);
                add_subject_btn.setDisable(true);
            }
            else{
                add_subject_label.setVisible(false);
                add_subject_btn.setDisable(false);
            }
        }
        catch(Exception e){
            System.out.println("Exception at listnerForAddSubject() at LedgerController : "+ e.getMessage());
        }
    }
    private void deleteSubject(){
        subject_column_choicebox.getItems().clear();
        String ledger_id = this.ledger_id.get();
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String year = LoginController.current_year;
        String query = null;
        try{
            query = "SELECT Subject_title FROM year_"+year+"_marks WHERE Ledger_id = "+ledger_id;
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                subject_column_choicebox.getItems().add(result.getString(1));
            }
            subject_column_choicebox.getSelectionModel().selectFirst();
        }
        catch(Exception e){
            System.out.println("Exception at deleteSubject() at LedgerController : " + e.getMessage());
        }
    }
    private void getLedger(String exam,String grade,String section){
        table_spread.getColumns().clear();
        Connection conn = database.Connection.conn;
        ResultSet result = null ;
        String query = "SELECT Grade_id FROM `year_"+LoginController.current_year+"_grade`"
                + " WHERE Grade= '"+grade+"' AND Section = '"+section+"' ;";
        String grade_id = null;
        try{            
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                grade_id = String.valueOf(result.getInt(1));
            }
            
            String exam_id = null;
            String year = LoginController.current_year;
            query = "SELECT Exam_id FROM year_"+year+"_exam WHERE `Name` = '"+exam+"';";
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                exam_id = result.getString(1);
            }
            query = "SELECT Table_name,Ledger_id FROM year_"+LoginController.current_year+"_ledger"
                    + " WHERE Exam_id = "+exam_id+" AND Grade_id = "+grade_id+";";
            result = conn.createStatement().executeQuery(query);
            String table_name = null;
            while(result.next()){
                table_name = result.getString(1);
                this.table_name = table_name;
                ledger_id.setValue(String.valueOf(result.getInt(2)));
               }
           //Inserting values into the result date textfield
           query = "SELECT Result_date FROM year_"+year+"_exam WHERE Exam_id = "+exam_id;
           result = conn.createStatement().executeQuery(query);
           while(result.next()){
               result_date_textfield.setText(result.getString(1));
           }
           Statement st = conn.createStatement();
           query = "CREATE TEMPORARY TABLE temp ("
                   + "Student_id INT NOT NULL,"
                   + "Roll INT Primary Key,"
                   + "Name VARCHAR(200)"
                   + ");";
           st.addBatch(query);
           query = "INSERT temp SELECT Student_id,Roll,Name FROM year_"+LoginController.current_year+"_student"
                    + " WHERE Student_id in (SELECT Student_id FROM "+table_name+")";
           st.addBatch(query);
           st.executeBatch();
           query = "SELECT column_name FROM information_schema.columns"
                   + " WHERE table_schema = 'school' AND table_name='"+table_name+"'"
                   + "AND column_name!='Roll'";
           result = conn.createStatement().executeQuery(query);
           query = "SELECT temp.Roll,temp.Student_id,temp.Name";           
           while(result.next()){
               query = query + ","+table_name+".`"+result.getString(1)+"`";
           }
           query = query + " FROM temp INNER JOIN "+table_name + " WHERE "
                   + " temp.Student_id = "+table_name+".Student_id ;";
            System.out.println(query);
            result = st.executeQuery(query);
            int columnCount = result.getMetaData().getColumnCount();
            int rowCount = 0;
            ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
            while(result.next()){
                rowCount ++;
            }
            result.beforeFirst();
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            while(result.next()){
            ObservableList list = FXCollections.observableArrayList();
            list.add(String.valueOf(result.getInt(1)));
            list.add(String.valueOf(result.getInt(2)));
            list.add(String.valueOf(result.getString(3)));
            for(int i=4;i<=result.getMetaData().getColumnCount();i++){
                list.add(String.valueOf(result.getString(i)));
            }
            data.add(list);
            }
            GridBase grid = new GridBase(rowCount,columnCount) ;
            ObservableList column_headers = FXCollections.observableArrayList();
            for(int i=0;i<result.getMetaData().getColumnCount();i++){
               column_headers.add(result.getMetaData().getColumnName(i+1));
            }
            
            grid.getColumnHeaders().addAll(column_headers);
            
            
            for(int row = 0;row<grid.getRowCount();++row){
                ObservableList values = data.get(row);
               final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
               //creating a list of column which cannot be edited. The editon in these 
               //column is done by the programmer with the help of code only
               ObservableList<String> not_editable_column_list = FXCollections.observableArrayList();
               not_editable_column_list.addAll(Arrays.asList("Division","Total","Percentage",
                       "CRank","Rank","Result","Remarks"));
               not_editable_column_list.addAll(Arrays.asList("Student_id","Roll","Name"));
               for(int column = 0;column<grid.getColumnCount();++column){
                   String column_header  = grid.getColumnHeaders().get(column);
                   SpreadsheetCell s =   SpreadsheetCellType.STRING.createCell(row, column, 1, 1, values.get(column).toString());
                  //adding listner to each cell. 
                  //by adding listner i can update each and every changes to the database
                  //automatically whenever any changes occurs to the spreadsheet
                   s.itemProperty().addListener(e->{
                       try{
                      
                       s.getColumn();
                       s.getRow();
                       }
                       catch(Exception ex){
                           s.setItem("0");
                           System.out.println(ex.getMessage());
                       }
                   });
                   if(not_editable_column_list.contains(column_header)){
                    s.setEditable(false);
                   }
                   else{
                       s.setEditable(true);
                   }
                   list.add(s);
               }
               rows.add(list);               
           }
            grid.setRows(rows);
            table_spread.setGrid(grid);
           for(int i=0;i<table_spread.getGrid().getColumnCount();i++){                
               if("Student_id".equals(table_spread.getGrid().getColumnHeaders().get(i))
                  || "Roll".equals(table_spread.getGrid().getColumnHeaders().get(i))
                  || "Name".equals(table_spread.getGrid().getColumnHeaders().get(i))){
                   table_spread.getColumns().get(i).setFixed(true);
                }
               if("Student_id".equals(table_spread.getGrid().getColumnHeaders().get(i))){
                   table_spread.hideColumn(table_spread.getColumns().get(i));
               }               
            }
            table_spread.getColumns().get(1).fitColumn(); 
            table_spread.getColumns().get(2).fitColumn();
            table_spread.getColumns().get(3).fitColumn();
            table_spread.setShowRowHeader(false);
        }
        catch(Exception e){
            System.out.println("Error at getLedger at LedgerController : " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void choiceBox(){
        exam_choicebox.getItems().clear();
        grade_choicebox.getItems().clear();
        section_choicebox.getItems().clear();
        add_subject_label.setVisible(false);
        try{
        //getting the respective data for ChoiceBoxes for user to select
        Connection conn = database.Connection.conn;
        String query = "SELECT DISTINCT Name FROM year_"+LoginController.current_year
                +"_exam ;";
        ResultSet result = null;
        try{
           result = conn.createStatement().executeQuery(query);
           while(result.next()){
               exam_choicebox.getItems().add(result.getString(1));
           }
           query = "SELECT DISTINCT `Grade` FROM year_"+LoginController.current_year
                +"_grade WHERE Grade_id in (SELECT DISTINCT Grade_id FROM year_"+LoginController.current_year
                   +"_ledger) ;";
           result = conn.createStatement().executeQuery(query);
           while(result.next()){
               grade_choicebox.getItems().add(result.getString(1));
           }
           query = "SELECT DISTINCT `Section` FROM year_"+LoginController.current_year
                +"_grade WHERE Grade_id in (SELECT DISTINCT Grade_id FROM year_"+LoginController.current_year
                   +"_ledger) ;";
           result = conn.createStatement().executeQuery(query);
           while(result.next()){
               section_choicebox.getItems().add(result.getString(1));
           }
        }
        catch(Exception e){
            System.out.println("Exception at setting the choiceBox items in LegderController :" + e.getMessage());
        }
        
        //assigning listner so that they will notify user if there is no sheet available
        exam_choicebox.getSelectionModel().selectedItemProperty().addListener(e->{
            try{
            listnerForChoiceBox();
            }
            catch(Exception f){
                System.out.println("Exception at adding listner of choiceBox() at LedgerController : " + f.getMessage());
            }
           
        });
        grade_choicebox.getSelectionModel().selectedItemProperty().addListener(e->{
            try{
            listnerForChoiceBox();
            }
            catch(Exception f){
                System.out.println("Exception at adding listner of choiceBox() at LedgerController : " + f.getMessage());
              }
        });
       section_choicebox.getSelectionModel().selectedItemProperty().addListener(e->{
            try{
            listnerForChoiceBox();
            }
            catch(Exception f){
                System.out.println("Exception at adding listner of choiceBox() at LedgerController : " + f.getMessage());
              
            }
       });
        }
        catch(Exception e){
            System.out.println("Exception at choiceBox() at LedgerController : " + e.getMessage());
            }
    }
    private void listnerForChoiceBox(){
        database.Connection.connect();
        Connection conn = database.Connection.conn;
        String exam = exam_choicebox.getSelectionModel().getSelectedItem().toString();
        String grade = grade_choicebox.getSelectionModel().getSelectedItem().toString();
        String section = section_choicebox.getSelectionModel().getSelectedItem().toString();
        String query = "SELECT Grade_id FROM year_"+LoginController.current_year+"_grade WHERE"
                + " Grade = '"+grade+"' AND Section = '"+section+"';";
        String grade_id = null;
        ResultSet result = null;
        ObservableList<ObservableList<String>> total_list = FXCollections.observableArrayList();
        try{
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                grade_id = String.valueOf(result.getInt(1));
            }
            String year = LoginController.current_year;
            query = "SELECT Name,Grade_id FROM year_"+year+"_ledger INNER JOIN year_"+year
                    +"_exam WHERE \n" +
                    "year_"+year+"_ledger.Exam_id=year_"+year+"_exam.Exam_id ;";
            result = conn.createStatement().executeQuery(query);
           
            while(result.next()){
                ObservableList<String> temp = FXCollections.observableArrayList();
                temp.add(result.getString(1));
                temp.add(String.valueOf(result.getInt(2)));
                total_list.addAll(temp);
            }
            ObservableList<String> selected_list = FXCollections.observableArrayList();
            selected_list.addAll(exam,grade_id);
            if(total_list.contains(selected_list)){
                anchorpane_label.setVisible(false);
                getLedger(exam,grade,section);
            }
            else{
                anchorpane_label.setVisible(true);
            
            }
        }
        catch(Exception e){
            System.out.println("Listner for choiceBox Exception : " + e.getMessage());
        }
    }
    @FXML
    private void clickAddSubject(MouseEvent event) {
        String fm = this.fm_textfield.getText();
        String pm = this.pm_textfield.getText();
        String subject = subject_choicebox.getSelectionModel().getSelectedItem().toString();
        System.out.println(subject);
        System.out.println(fm);
        System.out.println(pm);
        if(!"".equals(subject) && !"".equals(fm) && !"".equals(pm)){
        Alert a= new Alert(AlertType.CONFIRMATION);
        a.setHeaderText("Are you sure you want to add subject");
        Optional<ButtonType> button = a.showAndWait();
        if(button.get()==ButtonType.OK){
        Connection conn = database.Connection.conn;
        ResultSet result ;
        String query ;
        String year = LoginController.current_year;
        String after_column = "";
        try{
            String type = null;
            String subject_title = null;
            String temp = "";
            if(theory_radiobtn.isSelected()){
                type = "Theory";
                subject_title = subject + "_th";
                temp = subject + "_pr";
            }
            else if(practical_radiobtn.isSelected()){
                type = "Practical";
                subject_title = subject + "_pr";
                temp = subject + "_th";
            }
            //know where the new column is to be inserted on the ledger's table
            //get the table name
            query = "SELECT Table_name FROM year_"+year+"_ledger WHERE Ledger_id = "+ledger_id.get();
            result = conn.createStatement().executeQuery(query);
            String table_name = "";
            while(result.next()){
                table_name = result.getString(1);
            }
            //check if subject_pr column is present
            query = "SELECT * FROM information_schema.columns where TABLE_SCHEMA = 'school' "
                        + "AND TABLE_NAME = '"+table_name+"'";
            result = conn.createStatement().executeQuery(query);
            
            while(result.next()){       
                System.out.println(result.getString("COLUMN_NAME"));
                if(result.getString("COLUMN_NAME").contains(temp)){
                    if(theory_radiobtn.isSelected()){
                        result.previous();
                        after_column = result.getString("COLUMN_NAME");
                    }
                    else{
                        after_column = result.getString("COLUMN_NAME");
                    }
                    
                    break;
                }
                else if("Total".equals(result.getString("COLUMN_NAME"))){
                    result.previous();
                    after_column = result.getString("COLUMN_NAME");
                    break;                
                }
                
            }
            //getting the subject_id
            query = "SELECT Subject_id FROM year_"+year+"_subject WHERE Subject_name = '"
                    +subject+"' AND Grade_id in (SELECT Grade_id FROM year_"+year+"_ledger "
                    + "WHERE Ledger_id = "+ledger_id.get()+")";
            result = conn.createStatement().executeQuery(query);
            String subject_id = null;
            while(result.next()){
                subject_id = String.valueOf(result.getInt(1));
            }
            
            query = "SELECT Table_name FROM year_"+year+"_ledger WHERE Ledger_id = "+ledger_id.get();
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                table_name = result.getString(1);
            }
            subject_title=subject_title+" "+pm+"/"+fm;            
            query = "INSERT INTO year_"+year+"_marks(Subject_id,Subject_title,Ledger_id,FM,PM,Type) "
                    + "VALUES("+subject_id+",'"+subject_title+"',"+ledger_id.get()
                    +","+fm+","+pm+",'"+type+"');";
            Statement st = conn.createStatement();
            st.addBatch(query);  
            query = "ALTER TABLE "+table_name + " ADD `"+subject_title+"` "
                    + " FLOAT DEFAULT '0' AFTER `"+after_column+"`;";
            st.addBatch(query);            
            st.executeBatch();
            refresh();
        }
        catch(Exception e){
            System.out.println("Exception at clickAddSubject() at LedgerController : "+ e.getMessage());
            e.printStackTrace();
        }
        }
        }
        else{
         Alert a = new Alert(AlertType.ERROR);
         a.setHeaderText("Fill all the spaces");
         a.setContentText("Please fill all the spaces then click the button");
         a.show();
        }
    }
    @FXML
    private void clickDeleteSubject(MouseEvent event){
        Alert a = new Alert(AlertType.CONFIRMATION);
        a.setHeaderText("Are you sure you want to delete "+ subject_column_choicebox.getSelectionModel().getSelectedItem().toString());
        a.setContentText("Deleting a subject column means deleting all the "
                + "marks entered in it");
        Optional<ButtonType> button = a.showAndWait();
        if(button.get()==ButtonType.OK){
            String subject_column = subject_column_choicebox.getSelectionModel().getSelectedItem().toString();
            String year = LoginController.current_year;
            Connection conn = database.Connection.conn;
            ResultSet result ;
            String query;
            try{
                 Statement st = conn.createStatement();
                 query = "SELECT Table_name FROM year_"+2074+"_ledger WHERE Ledger_id = "+ledger_id.get()+" ;";
                 result = conn.createStatement().executeQuery(query);
                 String table_name = null;
                 while(result.next()){
                     table_name = result.getString(1);
                 }
                 query = "DELETE FROM year_"+year+"_marks WHERE Ledger_id = "+ledger_id.get()
                        +" AND Subject_title = '"+subject_column+"' ; ";
                 st.addBatch(query);
                 query = "ALTER TABLE "+table_name+" DROP COLUMN `"+subject_column+"` ;";
                 st.addBatch(query);
                 st.executeBatch();
                 a.setAlertType(AlertType.INFORMATION);
                 a.setHeaderText("Deletion of Column successful");
                 a.setContentText("The column "+subject_column+" was deleted"
                         + " successfully along \n with all the data entered in it");
                 a.show();
                 refresh();
            }
            catch(Exception e){
                System.out.println("Exception at clickDeleteSubject() at LedgerController : "+ e.getMessage());
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void clickAddExam(MouseEvent event) {
        try{
        Parent root = FXMLLoader.load(getClass().getResource("/ledger/newexam.fxml"));
        Scene scene = new Scene(root);
        Stage window = new Stage();
        window.setScene(scene);
        window.setTitle("New Exam");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(e->{
            try{
            e.consume();
            Parent roo = FXMLLoader.load(getClass().getResource("/ledger/ledger.fxml"));
            VBox v = new VBox();
            Scene sce = new Scene(roo);
            Stage st = (Stage) table_spread.getScene().getWindow();
            st.setScene(sce);      
            st.show();
            window.close();
            }
            catch(Exception ex){
                System.out.println("Exception in clickAddExam() at LedgerController : " + ex.getMessage());
            }
            });
        window.show();
        }
        catch(Exception e){
            System.out.println("Error at clickAddExam at LedgerController : " + e.getMessage());
        }
    }
    @FXML
    private void clickCreateNewSheet(MouseEvent event) {
        try{
        Parent root = FXMLLoader.load(getClass().getResource("/ledger/newsheet.fxml"));
        Scene scene = new Scene(root);
        Stage window = new Stage();
        window.setScene(scene);
        window.setTitle("New Sheet");
        window.initModality(Modality.APPLICATION_MODAL);
        window.setOnCloseRequest(e->{
            try{
            e.consume();
            Parent roo = FXMLLoader.load(getClass().getResource("/ledger/ledger.fxml"));
            VBox v = new VBox();
            Scene sce = new Scene(roo);
            Stage st = (Stage) table_spread.getScene().getWindow();
            st.setScene(sce);      
            st.show();
            window.close();
            }
            catch(Exception ex){
                System.out.println("Exception in clickCreateNewSheet() at LedgerController : " + ex.getMessage());
            }
            });
        window.show();
        }
        catch(Exception e){
            System.out.println("Error at clickAddExam at LedgerController : " + e.getMessage());
        }
    }

    @FXML
    private void clickEditResultDate(MouseEvent event) {
       if("Edit".equals(edit_result_date.getText())){
           result_date_textfield.setEditable(true);
           edit_result_date.setText("Done");
       }
       else {
           Connection conn  = database.Connection.conn;
           ResultSet result ;
           String year = LoginController.current_year;
           String date = result_date_textfield.getText();
           try{
               String query = "UPDATE year_"+year+"_exam SET Result_date = '"+date+"' WHERE "
                       + " Exam_id in (SELECT Exam_id FROM year_"+year+"_ledger "
                       + "WHERE Ledger_id = "+this.ledger_id.get()+") ;";
               conn.createStatement().executeUpdate(query);
               edit_result_date.setText("Edit");
               result_date_textfield.setEditable(false);
           }
           catch(Exception e){
               System.out.println("Exception at clickEditResultDate() at LedgerController : " + e.getMessage());
           }
       }
    }

    @FXML
    private void clickDeleteLedgerBtn(MouseEvent event) {
        if(!"".equals(table_name)  || ! "".equals(ledger_id.get())){
        String ledger_id = this.ledger_id.get();
            Alert al  = new Alert(AlertType.CONFIRMATION);
        al.setHeaderText("Do you want to confirm the deletion of the ledger \n of exam : "+exam_choicebox.getSelectionModel().getSelectedItem().toString()
                        +"\n of grade : "+grade_choicebox.getSelectionModel().getSelectedItem().toString()+
                        "\n of section : "+ section_choicebox.getSelectionModel().getSelectedItem().toString() );
        al.setContentText("Deleting a ledger will only affect the single ledger and none other factors are "
                + "effected");
        Optional<ButtonType> button = al.showAndWait();
        if(button.get()==ButtonType.OK){
            Connection conn = database.Connection.conn;
            ResultSet result = null;            
            String sql = null;
            String year = LoginController.current_year;
            try{
                Statement st = conn.createStatement() ;
                sql = "DROP TABLE "+table_name+" ;";
                st.addBatch(sql);
                sql = "DELETE FROM year_"+year+"_ledger WHERE Ledger_id =  "+ledger_id+" ;";
                st.addBatch(sql);
                sql = "DELETE FROM year_"+year+"_marks WHERE Ledger_id =  "+ledger_id+" ;";
                st.addBatch(sql);
                st.executeBatch();
                GridBase grid = new GridBase(0,0);
                table_spread.setGrid(grid);                
                al  = new Alert(AlertType.INFORMATION);
                al.setHeaderText("Deletion of the ledger of exam : "+exam_choicebox.getSelectionModel().getSelectedItem().toString()
                        +", of grade : "+grade_choicebox.getSelectionModel().getSelectedItem().toString()+
                        " of section : "+ section_choicebox.getSelectionModel().getSelectedItem().toString() +
                        " was successful");
                al.setContentText("Operation was successful ");
                al.show();
                choiceBox();
                
            }
            catch(Exception e){
                System.out.println("Exception at clickDeleteLedgerBtn() at LedgerController : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    }
    @FXML
    private void clickReCalculateBtn(MouseEvent event) {
    }
    public void refresh(){
        String exam = exam_choicebox.getSelectionModel().getSelectedItem().toString();
        String grade = grade_choicebox.getSelectionModel().getSelectedItem().toString();
        String section = section_choicebox.getSelectionModel().getSelectedItem().toString();
        choiceBox();
        exam_choicebox.getSelectionModel().select(exam);
        grade_choicebox.getSelectionModel().select(grade);
        section_choicebox.getSelectionModel().select(section);
        listnerForChoiceBox();
        addSubject();
        deleteSubject();
    }
    
}

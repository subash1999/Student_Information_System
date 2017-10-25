/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class AddteacherController implements Initializable {

    /**
     * Initializes the controller class.
     */
      @FXML
    private TextField name;

    @FXML
    private TextField address;

    @FXML
    private TextField phone;

    @FXML
    private RadioButton male;

    @FXML
    private ToggleGroup gender_tooglegroup;

    @FXML
    private RadioButton female;

    @FXML
    private TextField qualification;

    @FXML
    private TableView<TableOfAddTeacherController> table;

    @FXML
    private ChoiceBox subject;

    @FXML
    private ChoiceBox grade;

    @FXML
    private ChoiceBox section;

    @FXML
    private Button add;

    @FXML
    private Button delete;
    
    @FXML 
    private Label label;
    
    @FXML
    private Label label1;

    private String[] subjects,grades,sections;
    private String gender;
    private ObservableList<ObservableList> list = FXCollections.observableArrayList();
    private ObservableList<TableOfAddTeacherController> ob = FXCollections.observableArrayList();
        
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        database.Connection.connect();
        LoginController.current_year= "2074";
        label.setVisible(false);
        label1.setVisible(false);
        add.setDisable(true);
        choiceBox();
        listnerForChoiceBoxes();
        columnsForTable();
        listnerForToogleGroup();
    }   
    public void listnerForToogleGroup(){
     
            gender_tooglegroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if(newToggle.selectedProperty().get()){
            RadioButton chk = (RadioButton)newToggle.getToggleGroup().getSelectedToggle(); // Cast object to radio button
            gender=chk.getText();
            }
        });
    }
    
    @FXML
    private void addBtnOnAction(ActionEvent event) {
        
        ob.add(new TableOfAddTeacherController(this.subject.getSelectionModel().getSelectedItem().toString()
        ,this.grade.getSelectionModel().getSelectedItem().toString()
        , this.section.getSelectionModel().getSelectedItem().toString())
        );  
        table.refresh();
        
        
    }

    @FXML
    private void deleteBtnOnAction(ActionEvent event) {        
      try{table.getItems().remove(table.getSelectionModel().getSelectedIndex());
      }
      catch(Exception e){
          
      }
    }

    @FXML
    private void registerBtnOnAction(ActionEvent event) {
        
        if(name.getText().equals("")
                || "".equals(address.getText()) 
                || "".equals(phone.getText()) 
                || "".equals(gender)){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setHeaderText("Fill all the required fields");
            alert.setContentText("The fields with the star(*) sign are the required field."
                    + "Please fill up all the required fields with the correct information to register "
                    + "a new teacher");
            alert.show();
        }
        else{
            Connection conn = database.Connection.conn;
            ResultSet result = null;
            String query = null ;
            try{
                query  = "INSERT INTO year_"+LoginController.current_year+"_teacher(Name,Phone,Address,Qualification,Gender) VALUES ('"+
                        name.getText()+"','"+phone.getText()+"','"+address.getText()+"','"+qualification.getText()+
                        "','"+gender+"')";
                conn.createStatement().execute(query);
                query  = "SELECT Teacher_id FROM year_"+LoginController.current_year+"_teacher WHERE Name='"+
                        name.getText()+"' AND Phone='"+phone.getText()+"' AND Address='"+address.getText()+
                        "' AND Qualification='"+qualification.getText()+
                        "' AND Gender ='"+gender+"'";
                result = conn.createStatement().executeQuery(query);
                String teacher_id = null;
                while(result.next()){
                    teacher_id = String.valueOf(result.getInt(1));
                }
                
                String subject = this.subject.getSelectionModel().getSelectedItem().toString();
                String grade = this.grade.getSelectionModel().getSelectedItem().toString();
                String section = this.section.getSelectionModel().getSelectedItem().toString();
                query = "SELECT Grade_id FROM year_"+LoginController.current_year+"_grade WHERE Grade = '"+grade+"' AND Section = '"+section+"'";
                String grade_id = null;
                result = conn.createStatement().executeQuery(query);
                while(result.next()){
                    grade_id = String.valueOf(result.getInt(1));
                }
                for(int i=0;i<table.getItems().size();i++){
                    query = "UPDATE year_"+LoginController.current_year+"_subject "
                            + "SET Teacher_id = '"+teacher_id+"' WHERE Subject_name =  '" +subject+
                            "' AND Grade_id = " +grade_id+";";
                    conn.createStatement().execute(query);
                }
                name.clear();
                phone.clear();
                qualification.clear();
                address.clear();
                gender_tooglegroup.getSelectedToggle().setSelected(false);
                table.getItems().clear();
                table.getColumns().clear();
                Alert a = new Alert (AlertType.INFORMATION);
                a.setHeaderText("Registration Successful !!");
                a.setContentText("The task of registering a new teacher was successful");
                a.showAndWait();
            }            
            catch(Exception e){
                System.out.println("Error at AddteacherController Register button : " + e.getMessage());
            }
            }
    }
   private void choiceBox(){
       Connection conn = database.Connection.conn;
       String query = "SELECT DISTINCT Subject_name FROM Year_"+LoginController.current_year+"_subject";
       ResultSet result  ;
       String[] subject = null, grade=null,section=null;
       try{
           result = conn.createStatement().executeQuery(query);
           int i=0;
           while(result.next()){
               i++;
           }
           subject = new String[i];
           result.beforeFirst();
           i=0;
           while(result.next()){
               subject[i] = result.getString(1);
               i++;
           }
           
           query = "SELECT DISTINCT Grade FROM Year_"+LoginController.current_year+"_subject";
           result = conn.createStatement().executeQuery(query);
           i=0;
           while(result.next()){
               i++;
           }
           grade = new String[i];
           result.beforeFirst();
           i=0;
           while(result.next()){
               grade[i] = result.getString(1);
               i++;
           }
           query = "SELECT DISTINCT year_"+LoginController.current_year+"_grade.Section FROM year_"+LoginController.current_year+"_grade WHERE Grade_id in (SELECT DISTINCT Grade_id FROM year_2074_subject)";
           result = conn.createStatement().executeQuery(query);
           i=0;
           while(result.next()){
               i++;
           }
           section = new String[i];
           result.beforeFirst();
           i=0;
           while(result.next()){
               section[i] = result.getString(1);
               i++;
           }
                   
           subjects = subject;
           grades = grade;
           sections = section;
           query = "select Subject_name,year_"+LoginController.current_year+"_subject.Grade,Section from year_2074_subject"
                   + " INNER JOIN year_"+LoginController.current_year+"_grade WHERE year_"+LoginController.current_year+"_subject.Grade_id="
                   + "year_"+LoginController.current_year+"_grade.Grade_id ";
           result = conn.createStatement().executeQuery(query);
           while (result.next()){
               ObservableList temp = FXCollections.observableArrayList();
               temp.add(result.getString(1));
               temp.add(result.getString(2));
               temp.add(result.getString(3));
               this.list.add(temp);
           }
       
       }catch(Exception e){
           System.out.println("Exception at getting values to choice box from database : " + e.getMessage());
           e.printStackTrace();
       }          
       
       //assigning the strings to the choicebox
       this.section.getItems().addAll(Arrays.asList(section));
       this.subject.getItems().addAll(Arrays.asList(subject));
       this.grade.getItems().addAll(Arrays.asList(grade));
       this.section.getSelectionModel().select(0);
       this.grade.getSelectionModel().select(0);
       this.subject.getSelectionModel().select(0);
       //adding the listner for choice box
       this.section.getSelectionModel().selectedItemProperty().addListener(e->{
          listnerForChoiceBoxes();
      });
       this.subject.getSelectionModel().selectedItemProperty().addListener(e->{
          listnerForChoiceBoxes();
      });
       this.grade.getSelectionModel().selectedItemProperty().addListener(e->{
          listnerForChoiceBoxes();
      });
   }
   
   private void listnerForChoiceBoxes(){
       String section = this.section.getSelectionModel().getSelectedItem().toString();
       String subject = this.subject.getSelectionModel().getSelectedItem().toString();
       String grade = this.grade.getSelectionModel().getSelectedItem().toString();
       String query = "select distinct Grade_id,Subject_name from year_"+LoginController.current_year+"_subject where Teacher_id !=0 ";
              
       ResultSet result;
       Connection conn  = database.Connection.conn;
       String[] subject_pre,grade_id_pre;
       try{
           result = conn.createStatement().executeQuery(query);
           int i=0;
           while(result.next()){
               i++;
           }
           subject_pre = new String[i];
           grade_id_pre = new String[i];
           i=0;
           result.beforeFirst();
           while(result.next()){
               grade_id_pre[i] = String.valueOf(result.getInt(1));
               subject_pre[i]= result.getString(2);
               i++;
           }
           query = "SELECT Grade_id FROM year_2074_grade WHERE Grade="+grade+
                   " AND Section = '"+section+"'";
           result = conn.createStatement().executeQuery(query);
           String grade_id = null;
           while(result.next()){
               grade_id = String.valueOf(result.getInt(1));
           }
           ObservableList temp_list  = FXCollections.observableArrayList();
           temp_list.add(subject);
           temp_list.add(grade);
           temp_list.add(section);
           boolean hasSubject = true;
           if(!list.contains(temp_list)){
               hasSubject=false;
           }
           boolean bool = true;           
           for(int j=0;j<grade_id_pre.length;j++){
               if(grade_id.equals(grade_id_pre[j]) && subject.equals(subject_pre[j])){
                   bool = false;
               }
           }
           if(hasSubject){
           if(bool){
               add.setDisable(false);
               label.setVisible(false);
               label1.setVisible(false);
           }
           else{
               label.setVisible(true);
               label1.setVisible(false);
               add.setDisable(true);
           }
           }
           else{
               add.setDisable(true);
               label.setVisible(false);
               label1.setVisible(true);
           }
       }catch(Exception e){
           System.out.println("listnerForChoiceBoxes() Getting the already present subject : " + e.getMessage());
           label.setDisable(true);
       }
       
   }   
    private void columnsForTable(){
        TableColumn<TableOfAddTeacherController,String> subject_col = new TableColumn<>("Subject");       
        subject_col.setCellValueFactory(new PropertyValueFactory("subject"));
        table.getColumns().add(subject_col);
        TableColumn<TableOfAddTeacherController,String> grade_col = new TableColumn<>("Grade");
        grade_col.setCellValueFactory(new PropertyValueFactory("grade"));
        table.getColumns().add(grade_col);
        TableColumn<TableOfAddTeacherController,String> section_col = new TableColumn<>("Section");
        section_col.setCellValueFactory(new PropertyValueFactory("section"));
        table.getColumns().add(section_col);
        table.setItems(ob);
    //making table columns resizeable
        table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        //making the table menu button visible which will help to show or hide columns
        table.setTableMenuButtonVisible(true);
    }
    public static class TableOfAddTeacherController {
    public  StringProperty subject;
    public  StringProperty grade;
    public  StringProperty section;
    TableOfAddTeacherController(String subject,String grade,String section){
        this.subject = new SimpleStringProperty (subject);
        this.grade = new SimpleStringProperty(grade);
        this.section = new SimpleStringProperty(section);
    }

        public String getSubject() {
            return subject.get();
        }

        public String getGrade() {
            return grade.get();
        }

        public String getSection() {
            return section.get();
        }

        public void setSubject(String subject) {
            this.subject.set(subject);
        }

        public void setGrade(String grade) {
            this.grade.set(grade);
        }

        public void setSection(String section) {
            this.section.set(section);
        }

    
}

}


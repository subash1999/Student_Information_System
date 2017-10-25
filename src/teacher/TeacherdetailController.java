/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package teacher;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class TeacherdetailController implements Initializable {

    /**
     * Initializes the controller class.
     */
      @FXML
    private MenuBar menubar;

    @FXML
    private JFXTextField search_field;

    @FXML
    private TableView teacher_table;

    @FXML
    private Label teacher_id;

    @FXML
    private Label name;

    @FXML
    private Label gender;

    @FXML
    private Label address;

    @FXML
    private Label phone;

    @FXML
    private TableView subject_table;
    
    TeacherIdStore store_teacher_id = new TeacherIdStore();
    ObservableList<ObservableList> subject_data = FXCollections.observableArrayList();
    ObservableList<ObservableList> teacher_data = FXCollections.observableArrayList();
            
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        display_given_teacher();
        display_subject_table();
        display_teacher_table();
        searchTable();
    } 
    public void setTeacherId(String s){
        store_teacher_id.set(s);
    }
    public void searchTable(){
        search_field.textProperty().addListener(new InvalidationListener() {           
             @Override
             public void invalidated(javafx.beans.Observable observable) {
                
                if(search_field.textProperty().get().isEmpty()) {

                    teacher_table.setItems(teacher_data);

                    return;

                }
                ObservableList tableItems = FXCollections.observableArrayList();
                ObservableList<TableColumn> cols = teacher_table.getColumns();     
                for(int i=0;i<teacher_data.size();i++){
                    TableColumn col = new TableColumn();                
                for(int j=0; j<cols.size(); j++) {                    
                             col = cols.get(j);
                              if(col.isVisible()){
                                String cellValue = col.getCellData(teacher_data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(teacher_data.get(i));

                                 break;

                             }}            

            }
                }
                teacher_table.setItems(tableItems);
             }
        });        
             
    }
    public void display_given_teacher(){
         teacher_id.setText("Teacher ID : " + store_teacher_id.get());
         Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        try{
            query = "select Name,Gender,Phone,Address from Year_"+LoginController.current_year+"_teacher where Teacher_id=" + store_teacher_id.get();
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
            name.setText("Name : "+result.getString("Name"));
            gender.setText("Gender : " + result.getString("Gender"));
            phone.setText("Phone : " + result.getString("Phone"));
            address.setText("Address : " + result.getString("Address"));
            }
            
        }
        catch(Exception e){
            System.out.println("Exception in assigning given teacher : "+ e.getMessage());
        }
    }
    public void display_teacher_table(){
        teacher_table.getItems().clear();
        teacher_table.getColumns().clear();
        Connection conn = database.Connection.conn;
        String query = "SELECT Teacher_id,Name,Gender,Phone,Address FROM Year_"+LoginController.current_year+"_teacher \n" +
                       ";";
        ResultSet rs = null;
    try{
    rs = conn.createStatement().executeQuery(query);
    
    }
    catch(Exception e){
        System.out.println("Displaying teacher table  error while executing query :" + e.getMessage());
    }
    /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/           
    try{        
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setStyle("-fx-alignment : center;");
                
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return  new SimpleStringProperty(param.getValue().get(j).toString());
                        
                    }                    
                });

                if("Gender".equals(col.getText()) || "Address".equals(col.getText())
                        || "Phone".equals(col.getText())){
                    col.setVisible(false);
                }
                teacher_table.getColumns().addAll(col);
                
            }
            
        } catch (SQLException ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }                
                teacher_data.add(row);

            }
        } catch (SQLException ex) {
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
    }
    public void display_subject_table(){
        subject_table.getItems().clear();
        subject_table.getColumns().clear();
        Connection conn = database.Connection.conn;
        String query = "SELECT Subject_id,Subject_name,Grade FROM Year_"+LoginController.current_year+"_subject \n" +
                      "WHERE Teacher_id=" +store_teacher_id.get()+ ";";
        ResultSet rs = null;
    try{
    rs = conn.createStatement().executeQuery(query);
    
    }
    catch(Exception e){
        System.out.println("Displaying subject of a teacher error :" + e.getMessage());
    }
    /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/           
    try{        
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setStyle("-fx-alignment : center;");
                
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    @Override
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return  new SimpleStringProperty(param.getValue().get(j).toString());
                        
                    }                    
                });

                
                
                subject_table.getColumns().addAll(col);                 
            }
        } catch (SQLException ex) {
            System.out.println("Getting metadata error");
        }

        try {
            /********************************
             * Data added to ObservableList *
             ********************************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }                
                subject_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
          //FINALLY ADDED TO TableView
            subject_table.setItems(subject_data);
         //getting rid of the extra column in the javafx tableview
         // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
         //but in this case we need the extra column for proper resizing of the columns so
         // the table is being made UNCONSTRAINED_RESIZE_POLICY
         subject_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
         //setting the table menu button visible which lets user to select the column to view or hide
         subject_table.setTableMenuButtonVisible(true);
   
    }
    private  class TeacherIdStore {
        private String s="0";
        public void set(String s){
            this.s = s;
            display_subject_table();
            display_given_teacher();
        }
        public String get(){
         return s;   
        }        
    }
}

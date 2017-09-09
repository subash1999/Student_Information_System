/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User.Teacher;

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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.util.Callback;


/**
 * FXML Controller class
 *
 * @author subas
 */


public class Teachers_of_studentController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private MenuBar menubar;

    @FXML
    private JFXTextField search_field;

    @FXML
    private TableView search_table;

    @FXML
    private Label student_id;

    @FXML
    private Label name;

    @FXML
    private Label grade;

    @FXML
    private Label section;

    @FXML
    private Label roll;

    @FXML
    private TableView teacher_table;
    StudentIdStore store_student_id = new StudentIdStore();
    ObservableList<ObservableList> data = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }
    public void setStudentId(String s){
        store_student_id.set(s);
    }
    private  class StudentIdStore {
        private String s="0";
        public void set(String s){
            this.s = s;
            //displayDetails();
        }
        public String get(){
         return s;   
        }        
    }
   public void searchTable(){
         search_field.textProperty().addListener(new InvalidationListener() {           

             @Override
             public void invalidated(javafx.beans.Observable observable) {
                
                if(search_field.textProperty().get().isEmpty()) {

                    search_table.setItems(data);

                    return;

                }
                 ObservableList filteredItems = FXCollections.observableArrayList();

                ObservableList<TableColumn> cols = search_table.getColumns();                
                    
                for(int i=0; i<data.size(); i++) {                   
                    
                    TableColumn col = new TableColumn();
                    for(int j=0; j<cols.size(); j++) {
                             col = cols.get(j);
                             
                                String cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 filteredItems.add(data.get(i));

                                 break;

                             } 
                            
                            }
                }
                search_table.setItems(filteredItems);
            }
             

        });
    }
}

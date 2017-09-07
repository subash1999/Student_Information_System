/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package two_search_for_single_table;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
/**
 * FXML Controller class
 *
 * @author subas
 */
public class StudentController implements Initializable {

    /**
     * Initializes the controller class.
     */
      @FXML
    private MenuItem menuitem_close;

    @FXML
    private MenuItem menuitem_detailview;

    @FXML
    private MenuItem menuitem_about;

    @FXML
    private JFXTextField search_field;

    @FXML
    private ChoiceBox search_choice;

    @FXML
    private JFXTextField search_field2;

    @FXML
    private ChoiceBox search_choice2;

    @FXML
    private ChoiceBox join_search_choice;
    
    @FXML
    private TableView table;
    
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private String[] column_name;
    private int column_count;
    private ObservableList tableItems = FXCollections.observableArrayList();
    private ObservableList tabelItems2= FXCollections.observableArrayList();
    
    @Override   
    public void initialize(URL url, ResourceBundle rb) {
       //populating the table with the data and column from the database       
       makeTable();
       choiceBoxes();
       search();
      
       
           
  } 
    public void choiceBoxes(){
        ObservableList<String> choice_list= FXCollections.observableArrayList();
        choice_list.add("All");
        for(String col : column_name){
            choice_list.add(col);
        }
        ObservableList<String> conjunction_list= FXCollections.observableArrayList("AND","OR");
        //for the choice box which includes the conjunction AND , OR
        join_search_choice.setItems(conjunction_list);
        join_search_choice.getSelectionModel().select("AND");
        //adding listner to the choice box so that when ever the value changes
        //the searchbox will search accordingly the conjunction choice of the user
        join_search_choice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
        search();
      }
    });
        //for choice box 1
        search_choice.getSelectionModel().select("All");
        search_choice.setItems(choice_list);
        
        //adding listner to the choice box so that when ever the value changes
        //the searchbox will search accordingly the search_choice of the user
         search_choice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
        search();
      }
    });
        //for search choice box 2
        search_choice2.getSelectionModel().select("All");
        search_choice2.setItems(choice_list);
        //adding listner to the choice box so that when ever the value changes
        //the searchbox will search accordingly the search_choice of the user
         search_choice2.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
        search();
      }
    });
    }
    public void search(){
        ObservableList<ObservableList> filtered_data1 = FXCollections.observableArrayList();
        ObservableList<ObservableList> filtered_data2 = FXCollections.observableArrayList();
        ObservableList<ObservableList> result = FXCollections.observableArrayList();;
        String search_by1 = search_choice.getSelectionModel().getSelectedItem().toString();
        String search_by2 = search_choice2.getSelectionModel().getSelectedItem().toString();
        //if first search field is null then only search the data by second search 
        if(search_field.getText()==null){
            
            filtered_data2 =  searchby2(search_by2);
            for(ObservableList o : filtered_data2){
                result.add(o);
            }
            table.setItems(filtered_data2);
        }
        // else if second search field is null then only search the data by first search 
        else if(search_field2.getText()==null){
            filtered_data1 = searchby1(search_by1);
            //Doing below is same as the above for each loop in case of if condition
            filtered_data1.forEach((o)->{
                result.add(o);
            });
            table.setItems(filtered_data1);
        }
        //if both of them search field are null and search is to be called put the same data in result 
        //without searching
        else if(search_field.getText()==null && search_field.getText()==null){
            data.forEach((o) -> {
                result.add(o);
            });
            table.setItems(data);
        }
        else{
        if("AND".equalsIgnoreCase(join_search_choice.getSelectionModel().getSelectedItem().toString())){
          for(ObservableList o :filtered_data1){
              for(ObservableList ob : filtered_data2){
                  if(o==ob){
                      result.add(o);
                      System.out.println(ob);
                  }
              }
          }          
        }
        else if("OR".equalsIgnoreCase(join_search_choice.getSelectionModel().getSelectedItem().toString())){
            for(ObservableList o :filtered_data1){
                   result.add(o);
                   System.out.println(o);
          }
            for(ObservableList ob : filtered_data2){                  
                    result.add(ob);
                    System.out.println(ob);
              }
        }
        }
          
    }
    public ObservableList searchby1(String search_by){
         search_field.textProperty().addListener(new InvalidationListener() {           

             @Override
             public void invalidated(javafx.beans.Observable observable) {
                
                if(search_field.textProperty().get().isEmpty()) {

                    table.setItems(data);

                    return;

                }

                

                ObservableList<TableColumn> cols = table.getColumns();                
                    
                for(int i=0; i<data.size(); i++) {                   
                    
                    TableColumn col = new TableColumn();
                      switch(search_by.toLowerCase()){
                          
                        case "all":
                            for(int j=0; j<cols.size(); j++) {
                             col = cols.get(j);
                              if(col.isVisible()){
                                String cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                 break;

                             } 
                            }
                            }
                            break;
                        case "student_id":
                              for(TableColumn c : cols){
                                 if("student_id".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }                                     
                              
                                String cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                                

                              
                            }                    
                            
                            break; 
                        case "roll":for(TableColumn c : cols){
                                 if("roll".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                      
                            break;
                        case "name":
                            for(TableColumn c : cols){
                                 if("name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                            break;
                        case "grade":
                            for(TableColumn c : cols){
                                 if("grade".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                             
                            }    
                            break;
                        case "section":
                            for(TableColumn c : cols){
                                 if("section".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                            break;
                        case "gender":
                           for(TableColumn c : cols){
                                 if("gender".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));
                                

                             
                            }    
                            break;
                        case "address":
                            for(TableColumn c : cols){
                                 if("address".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                             
                            }    
                            break;
                        case "phone":
                            for(TableColumn c : cols){
                                 if("phone".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                             
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                             
                            }    
                            break; 
                        case "father_name":
                           for(TableColumn c : cols){
                                 if("father_name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                            break;
                        case "mother_name":
                            for(TableColumn c : cols){
                                 if("mother_name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                               

                             } 
                                break;
                                
                        case "guardian_name":
                            for(TableColumn c : cols){
                                 if("guardian_name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }         
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                               

                             }        
                            break;
                        case "previous_school":
                            for(TableColumn c : cols){
                                 if("previous_school".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }         
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                               

                             }        
                            break;    
                        default : 
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setHeaderText("Error search by Type");
                            alert.setContentText("The search type didnot matched."
                                    + "Please check the code for the proper search type");
                            alert.show();
                            break;
                    }
                              
                }   

                

               
             }

            

        });
         return tableItems;
    }
     public ObservableList searchby2(String search_by){
         search_field2.textProperty().addListener(new InvalidationListener() {           

             @Override
             public void invalidated(javafx.beans.Observable observable) {
                
                if(search_field2.textProperty().get().isEmpty()) {

                    table.setItems(data);

                    return;

                }

                

                ObservableList<TableColumn> cols = table.getColumns();                
                    
                for(int i=0; i<data.size(); i++) {                   
                    
                    TableColumn col = new TableColumn();
                      switch(search_by.toLowerCase()){
                          
                        case "all":
                            for(int j=0; j<cols.size(); j++) {
                             col = cols.get(j);
                              if(col.isVisible()){
                                String cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())){                                    

                                 tableItems.add(data.get(i));

                                 break;

                             } 
                            }
                            }
                            break;
                        case "student_id":
                              for(TableColumn c : cols){
                                 if("student_id".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }                                     
                              
                                String cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                                

                              
                            }                    
                            
                            break; 
                        case "roll":for(TableColumn c : cols){
                                 if("roll".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                      
                            break;
                        case "name":
                            for(TableColumn c : cols){
                                 if("name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                            break;
                        case "grade":
                            for(TableColumn c : cols){
                                 if("grade".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                             
                            }    
                            break;
                        case "section":
                            for(TableColumn c : cols){
                                 if("section".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                            break;
                        case "gender":
                           for(TableColumn c : cols){
                                 if("gender".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));
                                

                             
                            }    
                            break;
                        case "address":
                            for(TableColumn c : cols){
                                 if("address".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                             
                            }    
                            break;
                        case "phone":
                            for(TableColumn c : cols){
                                 if("phone".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                             
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                             
                            }    
                            break; 
                        case "father_name":
                           for(TableColumn c : cols){
                                 if("father_name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));

                                

                              
                            }    
                            break;
                        case "mother_name":
                            for(TableColumn c : cols){
                                 if("mother_name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }          
                              
                                 cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                               

                             } 
                                break;
                                
                        case "guardian_name":
                            for(TableColumn c : cols){
                                 if("guardian_name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }         
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                               

                             }        
                            break;
                        case "previous_school":
                            for(TableColumn c : cols){
                                 if("previous_school".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }         
                              
                                cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field2.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                               

                             }        
                            break;    
                        default : 
                            Alert alert = new Alert(AlertType.ERROR);
                            alert.setHeaderText("Error search by Type");
                            alert.setContentText("The search type didnot matched."
                                    + "Please check the code for the proper search type");
                            alert.show();
                            break;
                    }
                              
                }   

                

               
             }

            

        });
         return tableItems;
    }
    public void makeTable(){
         database.Connection.connect();         
          Connection conn=database.Connection.conn;    
        
            //SQL FOR SELECTING ALL OF STUDENT
            String SQL;
            ResultSet rs = null;
            try{
                 SQL = "SELECT * FROM  student";
                  
                  rs = conn.createStatement().executeQuery(SQL);
            } catch(Exception e){
              e.printStackTrace();
              System.out.println("Query Error");             
          }
            //ResultSet
        try {
            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
            column_count=rs.getMetaData().getColumnCount();
            column_name = new String[column_count];
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;                
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setStyle("-fx-alignment : center;");
                
                col.setCellValueFactory(new Callback<CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){                    
                    @Override
                    public ObservableValue<String> call(CellDataFeatures<ObservableList, String> param) {
                        return  new SimpleStringProperty(param.getValue().get(j).toString());
                        
                    }                    
                });
                column_name[i]=col.getText();
                if("previous_school".equalsIgnoreCase(col.getText()) || "mother_name".equalsIgnoreCase(col.getText())){
                    col.setVisible(false);
                }
                
                table.getColumns().addAll(col);                 
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
    }    
    

}

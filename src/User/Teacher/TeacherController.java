/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User.Teacher;

import com.jfoenix.controls.JFXTextField;
import com.sun.javafx.collections.ObservableListWrapper;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
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
    
    public static String teacher_id="0";
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private String[] column_name;
    private int column_count;
    
    
    @Override   
    public void initialize(URL url, ResourceBundle rb) {
       //populating the table with the data and column from the database
       makeTable();
       searchTable();
       choiceBox();     
                  
  } 
   
    private void choiceBox(){
        ObservableList<String> choice_list= FXCollections.observableArrayList();
        choice_list.add("All");
        for(String col : column_name){
            choice_list.add(col);
        }
        search_choice.getSelectionModel().select("All");
        search_choice.setItems(choice_list);
        //adding listner to the choice box so that when ever the value changes
        //the searchbox will search accordingly the search_choice of the user
         search_choice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
      @Override
      public void changed(ObservableValue<? extends String> observableValue, String number, String number2) {
        updateFilterTable(search_choice.getSelectionModel().getSelectedItem().toString());
        
      }
    });
    }
    private void searchTable(){
         search_field.textProperty().addListener(new InvalidationListener() {           

             @Override
             public void invalidated(javafx.beans.Observable observable) {
                
                if(search_field.textProperty().get().isEmpty()) {

                    table.setItems(data);

                    return;

                }
                updateFilterTable(search_choice.getSelectionModel().getSelectedItem().toString());
            }            

        });
    }
    private void updateFilterTable(String choice){
         ObservableList tableItems = FXCollections.observableArrayList();

                ObservableList<TableColumn> cols = table.getColumns();                
                    
                for(int i=0; i<data.size(); i++) {                   
                    
                    TableColumn col = new TableColumn();
                      switch(choice.toLowerCase()){
                          
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
                        case "name":
                              for(TableColumn c : cols){
                                 if("name".equals(c.getText().toLowerCase())){
                                    col = c;
                                }
                            }                                     
                              
                                String cellValue = col.getCellData(data.get(i)).toString();

                                 cellValue = cellValue.toLowerCase();

                                 if(cellValue.contains(search_field.textProperty().get().toLowerCase())) {

                                 tableItems.add(data.get(i));                                

                              
                            }                    
                            
                            break; 
                        case "phone":for(TableColumn c : cols){
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
                        case "address":for(TableColumn c : cols){
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
                        default : 
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
    private void makeTable(){
         database.Connection.connect();
         
          Connection conn=database.Connection.conn;     
        
            //SQL FOR SELECTING ALL OF STUDENT
            String SQL;
            ResultSet rs = null;
            try{
                 SQL = "SELECT Teacher_id,Name,Phone,Address FROM  teacher";                  
                  rs = conn.createStatement().executeQuery(SQL);
            } catch(Exception e){
              e.printStackTrace();
              System.out.println("Query Error");             
          }
            
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
                
                table.getColumns().addAll(col);                 
            }

           } catch (Exception ex) {
            System.out.println("Getting metadata error  " + ex.getMessage());
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
    


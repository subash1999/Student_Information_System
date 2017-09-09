/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package User.Student;

import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
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
    protected MenuItem menuitem_close;

    @FXML
    protected MenuItem menuitem_detailview;

    @FXML
    protected MenuItem menuitem_about;

    @FXML
    protected JFXTextField search_field;

    @FXML
    protected ChoiceBox search_choice;

    @FXML
    protected TableView table;
    
    public static String student_id="0";
    private ObservableList<ObservableList> data = FXCollections.observableArrayList();
    private String[] column_name;
    private int column_count;
    
    
    @Override   
    public void initialize(URL url, ResourceBundle rb) {
       //populating the table with the data and column from the database
       makeTable();
       searchTable();
       choiceBox();
       onRowClick();
       
           
  } 
    private void onRowClick(){
        table.setRowFactory( tv -> {
    TableRow row = new TableRow<>();
    row.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
        ObservableList<TableColumn> cols = table.getColumns();
        TableColumn col = cols.get(0);
        ObservableList<ObservableList> table_data = FXCollections.observableArrayList() ;
        table_data=table.getItems();
        student_id = col.getCellData(row.getIndex()).toString();
        //the two code below do the same job as the code above
        //String student_id = col.getCellData(table.getSelectionModel().getSelectedIndex()).toString();
        //String student_id = col.getCellData(table_data.get(table.getSelectionModel().getSelectedIndex())).toString();
       try{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("studentdetail.fxml"));
        
            Parent root1 = null;            
            root1 =  fxmlLoader.load();
            StudentdetailController controller = (StudentdetailController)fxmlLoader.getController();
            //passing the id of the selected student to the studentdetail.fxml file
            controller.setStudentId(student_id);            
            Stage stage = new Stage();           
            stage.setTitle("Student Detail" );
            stage.setScene(new Scene(root1));
            stage.show();
       }
            catch (IOException ex) {
                System.out.println("FXML loader error");
            }
   }
    });
    return row ;
});
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
                        case "dob":for(TableColumn c : cols){
                                 if("dob".equals(c.getText().toLowerCase())){
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

                

                table.setItems(tableItems);
             }
    private void makeTable(){
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
                if("previous_school".equalsIgnoreCase(col.getText())){
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

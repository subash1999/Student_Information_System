/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package settings;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import login.LoginController;

/**
 * FXML Controller class
 *
 * @author subas
 */
public class SettingsController implements Initializable {

    /**
     * Initializes the controller class.
     */
     @FXML
    private AnchorPane session_anchorpane;

    @FXML
    protected  TableView session_table;

    @FXML
    private Button session_delete;

    @FXML
    private Button session_add;
    private  ObservableList<ObservableList> data = FXCollections.observableArrayList();
    
    
    @FXML
    public static AnchorPane port_anchorpane;
    //Grade Management
   @FXML
    private Button grade_add;

    @FXML
    private TextField grade_grade;

    @FXML
    private TextField grade_section;

    @FXML
    private Button grade_delete;
    @FXML
    private TableView grade_table;
    private  ObservableList<ObservableList> grade_data = FXCollections.observableArrayList();
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
       
       makeSessionTable();
       makeGradeTable();
    }

    
//Session Management
     @FXML
    private void addSessionBtnClicked(MouseEvent event) {
        try{
            Stage window = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/settings/newSession/addsession1.fxml"));
           //Parent root = FXMLLoader.load(getClass().getResource("/login/login.fxml"));
            Scene scene = new Scene(root);
            window.setScene(scene);
            window.setResizable(false);
            window.toFront();
            window.initModality(Modality.APPLICATION_MODAL);
            window.setOnCloseRequest(f->{
                session_table.refresh();
            });
            window.show();
            window = (Stage) session_add.getScene().getWindow();
            window.close();
        } catch (Exception ex) {
             ex.printStackTrace();
            System.out.println("File not found" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
       

    }
    
    @FXML
    private void deleteSessionBtnClicked(MouseEvent e) {
        ObservableList<TableColumn> cols = session_table.getColumns();
        TableColumn col = cols.get(0);
        String id =(String) col.getCellData(session_table.getSelectionModel().getSelectedIndex());
        String year =(String) cols.get(1).getCellData(session_table.getSelectionModel().getSelectedIndex());
        Alert al = new Alert(Alert.AlertType.CONFIRMATION);
        al.setHeaderText("Are you sure you want to delete session with id : " + id);
        al.setContentText("The all the information related to this session will be permanently deleted");
        Optional<ButtonType> res = al.showAndWait();
        ResultSet result = null;
        if(res.get()==ButtonType.OK){
            Connection conn = database.Connection.conn;
          try{
           
            Statement statement = conn.createStatement();
            statement.addBatch("DROP TABLE `Year_"+year+"_student`;");
            statement.addBatch("DROP TABLE `Year_"+year+"_teacher`;");
            statement.addBatch("DROP TABLE `Year_"+year+"_subject`;");
            statement.addBatch("DROP TABLE `Year_"+year+"_grade`;");
            statement.addBatch("DROP TABLE `Year_"+year+"_grading`;");
            statement.addBatch("DROP TABLE `Year_"+year+"_percentage`;");
            Statement st = conn.createStatement();
            String query = "SELECT Table_name FROM `year_"+year+"_ledger";
            result = st.executeQuery(query);
            while(result.next()){
                String table = result.getString(1);
                statement.addBatch("DROP TABLE `"+table+"`;" );
            }
            statement.addBatch("DROP TABLE `Year_"+year+"_term`;");
            statement.addBatch("DROP TABLE `Year_"+year+"_ledger`;");
            statement.addBatch("DELETE FROM `session` WHERE session.Year='"+year+"';");
            statement.addBatch("DELETE FROM `table_details` WHERE table_details.Year='"+year+"';");
            statement.executeBatch();
            ObservableList temp = FXCollections.observableArrayList();
            temp=session_table.getItems();
            temp.removeAll(session_table.getSelectionModel().getSelectedItems());
            session_table.setItems(temp);
          }
          catch(Exception ex){
              System.out.println("Exception at onDeleteBtnClicked :" + ex.getMessage());
          }
        }
        
    }
    private  void makeSessionTable(){
          database.Connection.connect();
         
          Connection conn=database.Connection.conn;     
        
            //SQL FOR SELECTING ALL OF STUDENT
            String SQL;
            ResultSet rs = null;
            try{
                 SQL = "SELECT * FROM `session`";
                  
                  rs = conn.createStatement().executeQuery(SQL);
            } catch(Exception e){
              e.printStackTrace();
              System.out.println("Query Error");             
          }
            
        try {
            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
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
               
                session_table.getColumns().addAll(col);                 
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
                  
                    row.add(rs.getString(i));
                    
                }
                if(!"0".equals(row.get(1))){
                data.add(row);
                }

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
          //FINALLY ADDED TO TableView
            session_table.setItems(data);
         //getting rid of the extra column in the javafx tableview
         // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
         //but in this case we need the extra column for proper resizing of the columns so
         // the table is being made UNCONSTRAINED_RESIZE_POLICY
         session_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
         //setting the table menu button visible which lets user to select the column to view or hide
         session_table.setTableMenuButtonVisible(true);
    }
    
//Grade Management
    @FXML
    private void addGradeBtnClicked(MouseEvent event) {
        String grade = grade_grade.getText();                
        String section = grade_section.getText();
        String query = "INSERT INTO year_"+LoginController.current_year+"_grade(Grade,Section)"
                + " VALUES "
                + "('"+grade+"' , '"+section+"')";        
        Connection conn = database.Connection.conn;                
       try{ 
           conn.createStatement().execute(query);
        }catch(Exception e){
           System.out.println("add Grade Btn clicked in settings :" + e.getMessage());
       }
       grade_grade.clear();
       grade_section.clear();
       grade_table.refresh();
       grade_table.getColumns().clear();
       grade_table.getItems().clear();
       makeGradeTable();
    }
     @FXML
    private void deleteGradeBtnClicked(MouseEvent event) {
        ObservableList<TableColumn> cols = grade_table.getColumns();
        TableColumn col = cols.get(0);
        String id =(String) col.getCellData(grade_table.getSelectionModel().getSelectedIndex());
        Alert al = new Alert(Alert.AlertType.CONFIRMATION);
        al.setHeaderText("Are you sure you want to delete grade with id : " + id);
        al.setContentText("All the information related to this grade will be permanently deleted."
                + "All students, grade,Mark Records,subjects will be deleted and the teachers will be unassigned to the grade");
        Optional<ButtonType> res = al.showAndWait();
        if(res.get()==ButtonType.OK){
            Connection conn = database.Connection.conn;
            try{
                Statement statement = conn.createStatement() ;
                statement.addBatch("DELETE FROM year_"+LoginController.current_year+"_student WHERE Grade_id = " +id+" ;");
                statement.addBatch("DELETE FROM year_"+LoginController.current_year+"_grade WHERE Grade_id = " +id+" ;");
                statement.addBatch("DELETE FROM year_"+LoginController.current_year+"_subject WHERE Grade_id = " +id+" ;");
                Statement st = conn.createStatement();
                ResultSet result ;
                String query = "SELECT Table_name FROM `year_"+LoginController.current_year+"_ledger WHERE Grade_id = " +id+";";
                result = st.executeQuery(query);
                while(result.next()){
                String table = result.getString(1);
                statement.addBatch("DROP TABLE `"+table+"`;" );
                }
                statement.executeBatch();
                grade_table.refresh();
                grade_table.getColumns().clear();
                grade_table.getItems().clear();
                makeGradeTable();
            }   
            catch(Exception e){
                System.out.println("Exception at delete Grade button :" + e.getMessage());
            }
        }
    }
    
    
    public  void makeGradeTable(){
          database.Connection.connect();
         
          Connection conn=database.Connection.conn;     
        
            //SQL FOR SELECTING ALL OF STUDENT
            String SQL;
            ResultSet rs = null;
            try{
                 SQL = "SELECT * FROM year_"+LoginController.current_year+"_grade";
                  
                  rs = conn.createStatement().executeQuery(SQL);
            } catch(Exception e){
              e.printStackTrace();
              System.out.println("Query Error");             
          }
            
        try {
            /**********************************
             * TABLE COLUMN ADDED DYNAMICALLY *
             **********************************/
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
               if("Male".equals(col.getText())
                       || "Female".equals(col.getText()) 
                       || "No_of_students".equals(col.getText())){
                   col.setVisible(false);
               }
               grade_table.getColumns().addAll(col);
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
                  
                    row.add(rs.getString(i));
                    
                }
                grade_data.add(row);

            }
        } catch (SQLException ex) {
            System.out.println("No next data error");
        }
          //FINALLY ADDED TO TableView
          grade_table.setItems(grade_data);
         //getting rid of the extra column in the javafx tableview
         // is done by the code table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
         //but in this case we need the extra column for proper resizing of the columns so
         // the table is being made UNCONSTRAINED_RESIZE_POLICY
         grade_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
         //setting the table menu button visible which lets user to select the column to view or hide
         grade_table.setTableMenuButtonVisible(true);
    }


}
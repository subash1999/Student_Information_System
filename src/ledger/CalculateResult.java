/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import login.LoginController;

/**
 *
 * @author subas
 */
public class CalculateResult {
    private StringProperty ledger_id = new SimpleStringProperty();
    Connection conn = database.Connection.conn;
    ResultSet result;
    Statement st;
    String year = LoginController.current_year;
    String table_name;
    CalculateResult(){
        try{
           st = conn.createStatement();
        }
        catch(SQLException e){
            System.out.println("Exception at constructor CalculateResult() "
                    + "at CalcualteResult : " + e.getMessage());
            e.printStackTrace();
        }
        listnerForLedgerId();
    }
    
    
    CalculateResult(String ledger_id ){
        this.ledger_id.set(ledger_id);
           try{
           st = conn.createStatement();
        }
        catch(SQLException e){
            System.out.println("Exception at constructor CalculateResult() "
                    + "at CalcualteResult : " + e.getMessage());
            e.printStackTrace();
        }
        listnerForLedgerId();
    }
    
    public void listnerForLedgerId(){
        this.ledger_id.addListener(e->{
            try{
                System.out.println("listner");
                String query = "SELECT Table_name FROM year_"+year+"_ledger "
                        + "WHERE Ledger_id = "+ this.ledger_id.get();
                result = conn.createStatement().executeQuery(query);
                while(result.next()){
                    table_name = result.getString(1);
                }
            }
            catch(Exception ex){
                 System.out.println("Exception at listnerForLedgerId() "
                    + "at CalcualteResult : " + ex.getMessage());
                 ex.printStackTrace();
            }
        });
    }
    public void setLedgerId(String ledger_id){
        this.ledger_id.set(ledger_id);
        
    }
    public void calculateAll(String ledger_id){
        this.ledger_id.set(ledger_id);
        calculateAll();
                
    }
    public void calculateAll(){
        if(!ledger_id.get().isEmpty()){
        calculateTotal();
        calculatePercentage();
        calculateResult();
        calculateDivision();
        calculateClassRank();
        calculateRank();
        calculateRemarks();
        }
        else{
            Alert a = new Alert(AlertType.ERROR);
            a.setHeaderText("Ledger_id is not set");
            a.setContentText("Ledger Id must be set before calculating");
            a.show();
        }
    }
    public void calculateTotal(){
        String query;
        query = "SELECT Subject_title FROM year_"+year+"_marks WHERE Ledger_id = "+
                ledger_id.get();
        try{
            result = conn.createStatement().executeQuery(query);
            int i=0;
            while(result.next()){
                i++;
            }
            String[] column_name = new String[i];
            query = "SELECT Student_id,";
            for(int j=0;j<column_name.length;i++){                
                query = query + column_name[j]; 
                if(j!=column_name.length){
                    query = query + ",";
                }                               
            }
            query = query + " " + " FROM "+table_name;
            result = conn.createStatement().executeQuery(query);
            float total = 0;
            while(result.next()){
                for(int j=2;j<result.getMetaData().getColumnCount();j++){
                    total = total + result.getFloat(j);
                }
                query = "UPDATE "+ table_name + " SET Total = "
                        +String.valueOf(total) +" WHERE Student_id = "+ 
                        String.valueOf(result.getInt(1));
                conn.createStatement().executeUpdate(query);                
            }
        }
        catch(Exception e){
            System.out.println("Exception at calculateTotal() at CalculateResult: "+
                    e.getMessage());
            e.printStackTrace();
            
        }
        
    }
    public void calculatePercentage(){
        String query;
        query = "SELECT FM FROM year_"+year+"_marks WHERE Ledger_id = "+
                ledger_id.get();
        try{
            result = conn.createStatement().executeQuery(query);
            float total_fm = 0;
            while(result.next()){
                total_fm = total_fm + result.getFloat(1);
            }
            
            query ="SELECT Student_id,Total " + " FROM "+table_name;
            result = conn.createStatement().executeQuery(query);
            float total = 0;
            while(result.next()){
                float per = result.getFloat(2)/total_fm;
                query = "UPDATE "+ table_name + " SET Percentage = "
                        +String.valueOf(total) +" WHERE Student_id = "+ 
                        String.valueOf(result.getInt(1));
                conn.createStatement().executeUpdate(query);                
            }
        }
        catch(Exception e){
            System.out.println("Exception at calculatePercentage() at CalculateResult: "+
                    e.getMessage());
            e.printStackTrace();
            
        }
        
    }
    public void calculateResult(){
        String query;
        query = "SELECT Subject_title,PM FROM year_"+year+"_marks WHERE Ledger_id = "+
                ledger_id.get();
        try{
            result = conn.createStatement().executeQuery(query);
            ObservableList<ObservableList> pass_marks = FXCollections.observableArrayList();
            while(result.next()){
                ObservableList temp = FXCollections.observableArrayList();
                temp.add(result.getString(1));
                temp.add(result.getFloat(2));
                pass_marks.add(temp);
            }
            result.beforeFirst();
            int i=0;
            while(result.next()){
                i++;
            }
            String[] column_name = new String[i];
            query = "SELECT Student_id,";
            for(int j=0;j<column_name.length;i++){                
                query = query + column_name[j]; 
                if(j!=column_name.length){
                    query = query + ",";
                }                               
            }
            query = query + " " + " FROM "+table_name;
            result = conn.createStatement().executeQuery(query);
            while(result.next()){
                String res = "Pass";
                for(int j=2;j<result.getMetaData().getColumnCount();j++){
                   for(int k=0;k<pass_marks.size();k++){
                       if(pass_marks.get(k).get(0).equals(result.getMetaData().getColumnName(j))){
                           if(result.getFloat(j)<Float.valueOf(pass_marks.get(k).get(1).toString())){
                               res = "Fail";
                               break;
                           }
                       }                       
                   }
                   if("Fail".equals(res)){
                           break;
                       }
                }
                query = "UPDATE "+ table_name + " SET Result = '"
                        +res +"' WHERE Student_id = "+ 
                        String.valueOf(result.getInt(1));
                conn.createStatement().executeUpdate(query);                
            }
        }
        catch(Exception e){
            System.out.println("Exception at calculateResult() at CalculateResult: "+
                    e.getMessage());
            e.printStackTrace();
            
        }
        
    }
    public void calculateDivision(){
        String query = "SELECT Student_id,Percentage FROM "+table_name 
                +" WHERE Result = 'Pass'" ;
            try{
            result = conn.createStatement().executeQuery(query);
            query = "SELECT `From`,`To`,`Division_name` FROM year_"+year+"_percentage ; ";
            ResultSet res = conn.createStatement().executeQuery(query);
            while(result.next()){
                Float per = Float.valueOf(result.getString("Percentage"));
                String div = "No division found";                    
                res.beforeFirst();
                while(res.next()){
                    Float from = Float.valueOf(res.getString("From"));
                    Float to = Float.valueOf(res.getString("To"));
                    if(from<=per && per<to){
                        div = result.getString("Division_name");
                        break;
                    }
                }
                query = "UPDATE " + table_name + " SET Division = '"+div +"' "
                        + " WHERE Student_id = "
                        + String.valueOf(result.getInt("Student_id"));
                st.addBatch(query);
            }
            query = "UPDATE " + table_name + " SET Division = 'Failed' "
                        + " WHERE Result = 'Fail'";
            st.addBatch(query);
            st.executeBatch();
        }
        catch(Exception e){
            System.out.println("Exception at calculateDivision() at "
                    + "CalculateResult : "+ e.getMessage());
        }
    }
    public void calculateClassRank(){
        System.out.println(table_name);
        String query;
        try{
        query = "CREATE TEMPORARY TABLE temp ("
                + "Student_id INT, "
                + "Total Decimal,"
                +"Rank Int);";
       st.addBatch(query);
            System.out.println(query);
       query = "SET @prev_value = NULL;";
       st.addBatch(query);
       System.out.println(query);
       query = "SET @rank_count = 0;";
       System.out.println(query);
       st.addBatch(query);
        query = "INSERT INTO temp (Student_id,Total,Rank) "
               +"SELECT Student_id, Total, (CASE\n" +
                "    WHEN @prev_value = Total THEN @rank_count\n" +
                "    WHEN @prev_value := Total "
                + "THEN @rank_count := @rank_count + 1\n" +
                "END) AS Rank\n" +
                "FROM "+table_name+"\n" +
                " WHERE Result = 'Pass' "+
                "ORDER BY cast(Total as decimal(10,3)) DESC";                
        st.addBatch(query);
        System.out.println(query);
        query = "UPDATE "+ table_name +" INNER JOIN temp "
                + " ON "+table_name + ".Student_id = temp.Student_id "
                + " SET "+table_name+".ClassRank="+"temp.Rank;";
        st.addBatch(query);
        System.out.println(query);
        query = "UPDATE "+ table_name +" SET ClassRank = '0' "
                + "WHERE Result = 'Fail';";
        st.addBatch(query);       
        System.out.println(query);
        st.executeBatch();
        }        
        catch(Exception e){
            System.out.println("Exception at calculateResult() at CalculateResult: "+
                    e.getMessage());
            e.printStackTrace();
            
        }
    }
    public void calculateRank(){
        String query;
        try{
        query = "CREATE TEMPORARY TABLE temp ("
                + "Student_id INT, "
                + "Total Decimal);";       
        st.addBatch(query);
            System.out.println(query);
        query = "CREATE TEMPORARY TABLE temp2 ("
                    + "Student_id int,"
                    + "Rank int );";
       st.addBatch(query);
            System.out.println(query);       
       query = "SET @prev_value = NULL;";
       st.addBatch(query);
       System.out.println(query);
       query = "SET @rank_count = 0;";
       st.addBatch(query);
       System.out.println(query);
       query = "INSERT INTO temp (Student_id,Total) "
               + "SELECT Student_id,cast(Total as decimal(10,2)) FROM "
               + table_name+" WHERE "+table_name+".Result = 'Pass';";
       st.addBatch(query);
       System.out.println(query);
        //check
        query = "SELECT Exam_id FROM year_"+2074+"_ledger "
                + "WHERE Ledger_id = "+ ledger_id.get();
        
        result = conn.createStatement().executeQuery(query);
        String exam_id = null;
        while(result.next()){
            exam_id = String.valueOf(result.getInt(1));
        }
        query = "SELECT Grade_id FROM year_"+year+"_ledger "
                + "WHERE Ledger_id = "+ledger_id.get();
        result = conn.createStatement().executeQuery(query);
        String grade_id = null;
        while(result.next()){
            grade_id = String.valueOf(result.getInt(1));
        }
        query = "SELECT Grade_id FROM year_"+year+"_grade WHERE Grade in"
                + "(SELECT Grade FROM year_"+year+"_grade WHERE Grade_id in"
                + "(SELECT Grade_id FROM year_"+year+"_ledger WHERE "
                + "Ledger_id = "+ ledger_id.get()+")) "
                + "AND Grade_id !="+grade_id+" ;";
        System.out.println(query);
        result = conn.createStatement().executeQuery(query);
        String[] table_names = new String[0]; 
        while(result.next()){
            query = "SELECT Table_name FROM year_"+year+"_ledger WHERE "
                    + " Grade_id = "+String.valueOf(result.getInt(1))
                    + " AND Exam_id = "+ exam_id+" ;";
            ResultSet res = conn.createStatement().executeQuery(query);
            int i=0;
            while(res.next()){
                i++;
            }
            res.beforeFirst();
            table_names = new String[i];
            i=0;            
            while(res.next()){
                table_names[i]=res.getString(1);
                i++;
            }
            for(i=0;i<table_names.length;i++){
                query = "INSERT INTO temp (Student_id,Total) "
                        + " SELECT Student_id, cast(Total as decimal(10,2)) FROM "
                        + table_names[i]
                        +" WHERE "+table_names[i]+".Result = 'Pass';";;
                st.addBatch(query);
                System.out.println(query);
            }
         }
           query = "INSERT INTO temp2 (Student_id,Rank) "
                    + "SELECT Student_id,(CASE\n" +
                "    WHEN @prev_value = Total THEN @rank_count\n" +
                "    WHEN @prev_value := Total "
                    + "THEN @rank_count := @rank_count + 1\n" +
                "END) AS Rank\n" +
                "FROM temp \n" +
                "ORDER BY cast(Total as decimal(10,2)) DESC";                
        
        st.addBatch(query);
        System.out.println(query);
        query = "UPDATE "+ table_name +" INNER JOIN temp2 "
                + " ON "+table_name + ".Student_id = temp2.Student_id "
                + " SET "+table_name+".Rank="+"temp2.Rank;";
        st.addBatch(query);
        System.out.println(query);
        query = "UPDATE "+ table_name +" SET Rank = '0' WHERE Result = 'Fail';";
        st.addBatch(query);
        System.out.println(query);
        for(int i=0;i<table_names.length;i++){
            query = "UPDATE "+ table_names[i] +" INNER JOIN temp2 "
                + " ON "+table_names[i] + ".Student_id = temp2.Student_id "
                + " SET "+table_names[i]+".Rank="+"temp2.Rank;";
            st.addBatch(query);
            System.out.println(query);
            query = "UPDATE "+table_names[i]+" SET Rank = '0' WHERE Result = 'Fail';";
            st.addBatch(query);
            System.out.println(query);
        
        }
            st.executeBatch();
            
        }     
        catch(Exception e){
            System.out.println("Exception at calculateResult() "
                    + "at CalculateResult: "+
                    e.getMessage());
            e.printStackTrace();
            
        }
    }
    
    public void calculateRemarks(){
        String query = "SELECT Student_id,Percentage,Result FROM "+table_name;
        try{
            result  = conn.createStatement().executeQuery(query);
            ResultSet res ;
            query  = "SELECT SUM(PM) FROM year_"+year+"_marks "
                    + "WHERE Ledger_id = "+ ledger_id.get();
            res = conn.createStatement().executeQuery(query);
            Float sum_pm = null;
            while(res.next()){
                sum_pm = res.getFloat(1);
            }
            query  = "SELECT SUM(FM) FROM year_"+year+"_marks "
                    + "WHERE Ledger_id = "+ ledger_id.get();
            res = conn.createStatement().executeQuery(query);
            Float sum_fm = null;
            while(res.next()){
                sum_fm = res.getFloat(1);
            }
            Float pass_per = ((sum_pm/sum_fm)*100);
            while(result.next()){
              Float percentage = Float.valueOf(result.getString(2));
              String remarks = "No evaluation done";
              String pass_fail = result.getString(3);
              if("Pass".equalsIgnoreCase(pass_fail)){
              if(percentage >= 90) {
                  remarks = "Excellent Keep it Up";
              }
              else if(percentage>=80 && percentage<90){
                  remarks = "Very Good. You can do more than this";
              }
              else if(percentage>=70 && percentage<80){
                  remarks = "Good. Focous on weak subjects to achieve distinction";
              }
              else if(percentage>=60 && percentage<70){
                  remarks ="Average. Aim higher to score more ";
              }
              else if(percentage>=pass_per && percentage<60){
                  remarks = "Poor performance. Work harder";
              }
              else{
                  remarks = "FAILED. Work hard on failed Subjects";
              }
              }
              else{
                  remarks = "Failed. Work hard on failed Subjects";
              }
              String id = String.valueOf(result.getInt(1));
              query = "UPDATE "+table_name+" SET Remarks  = '"
                      +remarks +"' WHERE Student_id = "+id+" ;";
              conn.createStatement().executeUpdate(query);
            }
        }
        catch(Exception e){
            System.out.println("Exception at calculateRemarks() at "
                    + "CalculateResult : " + e.getMessage());
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database_test;

import java.sql.Connection;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author subas
 */
public class get_teachers_of_student {
    public static void main(String[] args){
        getteacher();
    }
    public static void getteacher(){
        database.Connection.connect();
        Connection conn=database.Connection.conn;
        String query = "select Grade FROM student where Student_id=33;";
        ResultSet res;
        String grade="0";
        String table_name = "0";
        String[] subject_name=new String[1];
        String[] sub_id = null;
        String[] teacher_id=null;
        int l=0;
        
        try{
            res=conn.createStatement().executeQuery(query);
            while(res.next()){
                grade = res.getString(1);
            }
            System.out.println(grade);
        }
        catch(Exception e){
            System.out.println("1. Error while getting grade ");
        }
        query= "select Table_name From ledger_details where Grade="+ grade +" AND  Year =2073 ";
        try{
            res=conn.createStatement().executeQuery(query);
            while(res.next()){
                table_name = res.getString(1);
                System.out.println(res.getString(1));
            }
        }
        catch(Exception e){
            System.out.println("2. Error while getting table_name " + e.getMessage());
        }
        System.out.println(table_name);
        query= "select * From "+ table_name ;
        try{
            res=conn.createStatement().executeQuery(query);
            String sql = "select Subject_name from subject where Grade =" + grade;
            ResultSet result;
            result=conn.createStatement().executeQuery(sql);
            String[] subject = new String[200];
            int i=0;
            while(result.next()){
                i++;
            }
            subject=new String[i];
            i=0;
            while(result.next()){
            subject[i]=result.getString(1);
            i++;
            }
            //table_name
            String[] col_name = new String [res.getMetaData().getColumnCount()];
            subject_name = new String[res.getMetaData().getColumnCount()];
            for(int j=0;j<res.getMetaData().getColumnCount();j++){
                col_name[j]=res.getMetaData().getColumnName(j);
            }
            
            for(int j=0;j<res.getMetaData().getColumnCount();j++){
                for(int k=0;k<i;k++){
                    if(col_name[j].equals(subject[k])){
                        subject[l]=col_name[j];
                        l++;
                        break;
                    }
                }
        }
        }
        catch(Exception e){
            System.out.println("3. Error while getting column from table " + e.getMessage());
            System.out.println("local :" + e.getLocalizedMessage());
        }
        
        try{
            sub_id = new String[l];
            int j=0;
            for(int i=0;i<l;i++){
                query="select Subject_id from subject where Subject_name="+subject_name[i]+" AND Grade ="+grade;
                res = conn.createStatement().executeQuery(query);
                
                while(res.next()){
                    sub_id[j]= res.getString(0);
                    j++;
                }                
            }
            l=j;
        }
        catch(Exception e){
            System.out.println("4. Error while getting sub id"+ e.getMessage());
        }
        int no_of_teacher=0;
        try{
            
            int j=0;
            for(int i=0;i<l;i++){
            
            query="select Teacher_id from teacher where Subject_id = "+ sub_id[i];
            res=conn.createStatement().executeQuery(query);
            
            while(res.next()){
                no_of_teacher++;
            }
            
            while(res.next()){
                teacher_id[j]=res.getString(0);
                j++;
            }
            }
           
            
        }
        catch(Exception e){
            System.out.println("5. Error while finding teacher id" + e.getMessage());
        }
        try{
            ObservableList<ObservableList> data = FXCollections.observableArrayList();
            for(int i=0;i<no_of_teacher;i++){
            query="select * from teacher where Teacher_id="+ teacher_id[i];
            res=conn.createStatement().executeQuery(query);
            while(res.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int j=1 ; j<=res.getMetaData().getColumnCount(); j++){
                    //Iterate Column
                    row.add(res.getString(j));
                }                
                data.add(row);
                System.out.println(row);
        }
            }
            
        }
        catch(Exception e){
            System.out.println("6. Error while getting teacher's data" + e.getMessage());
        }
            
    }
}

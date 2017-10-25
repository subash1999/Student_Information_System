
import java.sql.Connection;
import java.sql.ResultSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author subas
 */
public class Assign_no_of_students_male_female_numbe_to_students_table {
    public static void main(String args[]){   
        database.Connection.connect();
        Connection conn =database.Connection.conn;
        ResultSet result = null;
        String query1= "SELECT `Grade_id` FROM `year_2074_grade`;";
        String[] grade_id;
        try{
             result = conn.createStatement().executeQuery(query1);
                              
            int i=0;
            while(result.next()){
                i++;
            }
            int grade_row = i;
           grade_id =new String [i];
           i=0;
           result.beforeFirst();
           while(result.next()){
               grade_id[i] = String.valueOf(result.getInt("Grade_id"));
               
               i++;
           }
           for(String id : grade_id){
               query1= "SELECT COUNT(Student_id) FROM year_2074_student where Grade_id = "+id;
               result = conn.createStatement().executeQuery(query1);
               int j=0;
               String res = null;
               result.beforeFirst();
               while(result.next()){
                   res = String.valueOf(result.getString(1));
               }
               System.out.println(id);
               System.out.println(res);
               query1 = "UPDATE `year_2074_grade`"
                       + "SET `No_of_students` ="+ res + " WHERE `Grade_id` ="+ id;
               result = null;
               conn.createStatement().execute(query1);
//               conn.prepareStatement(query1).execute();
               query1= "SELECT COUNT(Student_id) FROM year_2074_student where Gender = 'Female' AND  Grade_id = "+id;
               result = conn.createStatement().executeQuery(query1);
               res = null;
               while(result.next()){
                   res = String.valueOf(result.getString(1));
                }
               System.out.println(res);
               query1 = "UPDATE `year_2074_grade`"
                       + "SET `Female` ="+ res + " WHERE `Grade_id` ="+ id;
               conn.createStatement().execute(query1);
               query1= "SELECT COUNT(Student_id) FROM year_2074_student where Gender = 'Male' AND  Grade_id = "+id;
               result = conn.createStatement().executeQuery(query1);
               res = null;
               while(result.next()){
                   res = String.valueOf(result.getString(1));
                }
               query1 = "UPDATE `year_2074_grade`"
                       + "SET `Male` ="+ res + " WHERE `Grade_id` ="+ id;
               conn.createStatement().execute(query1);
           }
        }
        catch(Exception e){
         e.printStackTrace();
            System.out.println(e.getMessage());
        }
            
    }
}

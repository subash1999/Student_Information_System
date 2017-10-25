
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
// The program assigns the grade id to the students by checking the combine value of grade and 
//section to the combine value of grade and section in the grade table
//the retrives the grade_id of matching value and put it to the students
//
public class Assign_grade_id_to_students_from_grade_table {
    public static void main(String args[]){
        database.Connection.connect();
        Connection conn =database.Connection.conn;
        ResultSet result = null;
        String query1= "SELECT `Grade`,`Section` FROM `year_2074_student`;";
        String[] grade;
        String [] section ;
        try{           
            result = conn.createStatement().executeQuery(query1);
                             
            int i=0;
            while(result.next()){
                i++;
            }
            int student_row = i;
            grade =new String [i];
           section = new String [i];
           i=0;
           result.beforeFirst();
           while(result.next()){
               grade[i] = result.getString("Grade");
               section[i] = result.getString("Section");
               i++;
           }
           query1 = "select Grade_id,Grade,Section FROM year_2074_grade";
           result = conn.createStatement().executeQuery(query1);
           i=0;
           while(result.next()){
               i++;
              
           }
           int grade_row = i;
           String [] grade_id = new String [i];
           String[] grade1 = new String[i];
           String[] section1 = new String[i];
            i=0;
            result.beforeFirst();
           while(result.next()){
               grade1[i] = result.getString("Grade");
               section1[i] = result.getString("Section");
               grade_id[i] = String.valueOf(result.getInt("Grade_id"));
               i++;
           }
           
           int l=0;
            System.out.println(student_row);
            System.out.println(grade_row);
           for(int j=0;j<student_row;j++){
            for(int k=0;k<grade_row;k++){
                System.out.println("");
                   if((grade[j] == null ? grade1[k] == null : grade[j].equals(grade1[k])) && (section[j] == null ? section1[k] == null : section[j].equals(section1[k]))){
                       query1= "UPDATE year_2074_student "
                               + "SET Grade_id = "+grade_id[k] +" WHERE Student_id = "+String.valueOf(j+1) +"  ";
                        conn.prepareStatement(query1).execute();
                       System.out.println(grade_id[k]);
                       System.out.println(j+1);
                   }
               }
               l++;
           }
        }
        catch(Exception e){
            System.out.println("Exception at first : " + e.getMessage());
            e.printStackTrace();
        }
    }
}

package calculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import login.LoginController;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author subas
 */
public class CalculateHighestMarkOfSubject {

    private String exam_name = null;
    private String grade = null;
    private String section = null;
    private String subject_title = null;
    private String ledger_table_name = null;
    private String year = LoginController.current_year;
    private String exam_id = null;
    private String ledger_id=null,grade_id=null;

    public CalculateHighestMarkOfSubject() {

    }

    public CalculateHighestMarkOfSubject(String exam_name, String grade, String section, String subject_title) {

    }

    private void setValues(String exam_name, String grade, String section, String subject_title) {
        this.exam_name = exam_name;
        this.grade = grade;
        this.section = section;
        this.subject_title = subject_title;
        try {
            Connection conn = database.Connection.conn;
            //finding the exam_id
            String query = "SELECT Exam_id FROM year_" + year + "_exam WHERE Name = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, exam_name);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                this.exam_id = String.valueOf(result.getInt(1));
            }
            //finding the grade_id
            query = "SELECT Grade_id FROM year_" + year + "_grade WHERE "
                    + " Grade = ? AND Section = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, this.grade);
            pst.setString(2, this.section);
            result = pst.executeQuery();
            while (result.next()) {
                this.grade_id = String.valueOf(result.getInt(1));
            }
            //finding the ledger_id
            query = "SELECT Ledger_id FROM year_" + year + "_ledger WHERE "
                    + " Grade_id = ? AND Exam_id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, this.grade_id);
            pst.setString(2, this.exam_id);
            result = pst.executeQuery();
            while (result.next()) {
                this.ledger_id = String.valueOf(result.getInt(1));
            }
            //finding the ledger name of the table
            query = "SELECT Table_name FROM year_" + year + "_ledger WHERE "
                    + "Ledger_id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, this.ledger_id);
            result = pst.executeQuery();
            while (result.next()) {
                this.ledger_table_name = result.getString(1);
            }

        } catch (Exception e) {
            System.out.println("Exception at setValues(args) at CalculateHighestMarkOfSubject "
                    + " : " + e.getMessage());
        }
    }

    public double getHighestOfSection(String exam_name, String grade, String section, String subject_column_name) {
        setValues(exam_name, grade, section, subject_column_name);
        return getHighestOfSection(this.ledger_table_name);
    }

    private double getHighestOfSection(String table_name) {
        double high = 0.0d;
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            String query = "SELECT MAX(`" + subject_title + "`) FROM " + ledger_table_name + " ; ";
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                high = Math.round(result.getDouble(1) * 100D) / 100D;
            }
        } catch (Exception e) {
            System.out.println("Exception at getHighestOfSection() at "
                    + "CalculateHighestMarkOfSubject : " + e.getMessage());
        }
        return high;
    }

    public double getHighestOfGrade(String exam_name, String grade, String section, String subject_column_name) {
        setValues(exam_name, grade, section, subject_column_name);
        return getHighestOfGrade(this.ledger_table_name);
    }

    private double getHighestOfGrade(String ledger_table_name) {
        double high = 0.0d;
        try {
            String query = null;
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            PreparedStatement pst = null;
            ResultSet result = null;
            /*checking if there is any other ledger of the same grade in the same
            examination if present then checking if the subject whose average 
            is being calculatd is present in the ledger of the another section.
            if and only if another section ledger is present and 
            the subject is also present in another ledger then the average is calculated
             */
            query = "SELECT Grade_id FROM year_" + year + "_grade WHERE Grade = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, this.grade);
            result = pst.executeQuery();
            while (result.next()) {
                //getting table name of all the sections of the same grade 
                //who has ledger in the exam
                String grade_ids = String.valueOf(result.getInt(1));
                query = "SELECT Table_name FROM year_" + year + "_ledger "
                        + " WHERE Grade_id = ? AND Exam_id = ?";
                pst = conn.prepareStatement(query);
                pst.setString(1, grade_ids);
                pst.setString(2, this.exam_id);
                ResultSet resul = pst.executeQuery();
                while (resul.next()) {
                    //checking if the same subject having same FM and PM  exists
                    //in the another table_name
                    query = "SELECT * FROM information_schema.COLUMNS WHERE"
                            + " TABLE_SCHEMA = ?"
                            + " AND TABLE_NAME = ?"
                            + " AND COLUMN_NAME = ?";
                    String table_name = resul.getString(1);
                    pst = conn.prepareStatement(query);
                    pst.setString(1, "school");
                    pst.setString(2, resul.getString(1));
                    pst.setString(3, this.subject_title);
                    ResultSet resu = pst.executeQuery();
                    while (resu.next()) {
                        if(high<getHighestOfSection(table_name)){
                            high = getHighestOfSection(table_name);
                        }
                        
                    }
                }
            }
         } catch (Exception e) {
            System.out.println("Exception at getAverageOfGrade() "
                    + "at CalculateAverageMarkOfSubject : " + e.getMessage());
        }
        return high;
    }

    public double getHighestOfGrade(String table_name, String subject_title) {
        double high = 0.0d;
        String exam_name = null, grade = null, section = null, subject_column_name = subject_title;
        try {
            String query = "SELECT Name FROM year_" + year + "_exam WHERE Exam_id IN"
                    + " (SELECT Exam_id FROM year_" + year + "_ledger WHERE "
                    + "Table_name = ? )";
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, table_name);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                exam_name = result.getString(1);
            }
            query = "SELECT Grade,Section FROM year_" + year + "_grade WHERE Grade_id IN "
                    + "(SELECT Grade_id FROM year_" + year + "_ledger WHERE Table_name = ?)";
            pst = conn.prepareStatement(query);
            pst.setString(1, table_name);
            result = pst.executeQuery();
            while (result.next()) {
                grade = result.getString(1);
                section = result.getString(2);
            }
            high = getHighestOfGrade(exam_name, grade, section, subject_column_name);

        } catch (Exception e) {
            System.out.println("Exception at getHighestOfGrade() at "
                    + "CalculateHighestMarkOfSubject : " + e.getMessage());
        }
        return high;
    }
    public double getHighestOfSection(String table_name,String subject_title){
        double high = 0.0d;
        String exam_name = null, grade = null, section = null, subject_column_name = subject_title;
        try {
            String query = "SELECT Name FROM year_" + year + "_exam WHERE Exam_id IN"
                    + " (SELECT Exam_id FROM year_" + year + "_ledger WHERE "
                    + "Table_name = ? )";
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, table_name);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                exam_name = result.getString(1);
            }
            query = "SELECT Grade,Section FROM year_" + year + "_grade WHERE Grade_id IN "
                    + "(SELECT Grade_id FROM year_" + year + "_ledger WHERE Table_name = ?)";
            pst = conn.prepareStatement(query);
            pst.setString(1, table_name);
            result = pst.executeQuery();
            while (result.next()) {
                grade = result.getString(1);
                section = result.getString(2);
            }
            high = getHighestOfSection(exam_name, grade, section, subject_column_name);

        } catch (Exception e) {
            System.out.println("Exception at getHighestOfSection() at "
                    + "CalculateHighestMarkOfSubject : " + e.getMessage());
        }
        return high;
    }
}

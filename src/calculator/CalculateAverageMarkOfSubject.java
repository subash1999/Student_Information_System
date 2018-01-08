/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import login.LoginController;

/**
 *
 * @author subas
 */
public class CalculateAverageMarkOfSubject {

    private String exam_name;
    private String grade;
    private String section;
    private String subject_column_name;
    private String ledger_id;
    private String exam_id;
    private String grade_id;
    private String ledger_table_name;
    String year = LoginController.current_year;

    public CalculateAverageMarkOfSubject() {

    }

    /**
     * initiallize CalculateAverageMarkOfSubject while assigning values of
     * parameters
     *
     * @param exam_name
     * @param grade
     * @param section
     * @param subject_column_name
     */
    public CalculateAverageMarkOfSubject(String exam_name, String grade, String section, String subject_column_name) {
        setValues(exam_name, grade, section, subject_column_name);
    }

    private void setValues(String exam_name, String grade, String section, String subject_column_name) {
        this.exam_name = exam_name;
        this.grade = grade;
        this.section = section;
        this.subject_column_name = subject_column_name;
        database.Connection.connect();
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
            System.out.println("Exception at setValues(args) at CalculateAverageMarksOfSubject : "
                    + e.getMessage());
        }
    }
/**
 * it returns the average marks of a subject obtained by the students of a section
 * in a exam when below parameters are given
 * @param exam_name
 * @param grade
 * @param section
 * @param subject_column_name
 * @return 
 */
    public double getAverageOfSection(String exam_name, String grade, String section, String subject_column_name) {
        setValues(exam_name, grade, section, subject_column_name);
        return getAverageOfSection(this.ledger_table_name);
    }

    private double getAverageOfSection(String table_name) {
        String query = "SELECT AVG(`" + this.subject_column_name + "`) "
                + " FROM " + table_name + " ;";
        double average = 0.0D;
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                average = result.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("Exception at getAverageOfSection() at "
                    + "CalculateAverageMarkOfSubject : " + e.getMessage());
        }
        return average;
    }

    private double getStudentCountOfLedger(String table_name) {
        String query = "SELECT COUNT(*)"
                + " FROM " + table_name + " ;";
        double count = 0.0D;
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            PreparedStatement pst = conn.prepareStatement(query);
            ResultSet result = pst.executeQuery();
            while (result.next()) {
                count = result.getDouble(1);
            }
        } catch (Exception e) {
            System.out.println("Exception at getAverageOfSection() at "
                    + "CalculateAverageMarkOfSubject : " + e.getMessage());
        }
        return count;
    }
/**
 * it returns average of a subject obtained by a grade
 * of a exam while following 
 * parameters are passed, the section is argument is for calculation 
 * output will include all the sections of that grade
 * @param exam_name
 * @param grade
 * @param section
 * @param subject_column_name
 * @return 
 */
    public double getAverageOfGrade(String exam_name, String grade, String section, String subject_column_name) {
        setValues(exam_name, grade, section, subject_column_name);
        return getAverageOfGrade();
    }

    private double getAverageOfGrade() {
        double average = 0.0d;
        double total_sum = 0.0d;
        double total_count = 0.0d;
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
                    pst.setString(3, this.subject_column_name);
                    ResultSet resu = pst.executeQuery();
                    while (resu.next()) {
                        double avg = getAverageOfSection(table_name);
                        double count = getStudentCountOfLedger(table_name);
                        total_sum = total_sum + avg * count;
                        total_count = total_count + count;
                    }
                }
            }
            average = total_sum / total_count;
        } catch (Exception e) {
            System.out.println("Exception at getAverageOfGrade() "
                    + "at CalculateAverageMarkOfSubject : " + e.getMessage());
        }
        return average;
    }
/**
 * it return average marks of a subject obtained by a grade (no section discrimination)
 * when table_name of ledger and subject_tile is given
 * @param table_name
 * @param subject_title
 * @return 
 */
    public double getAverageOfGrade(String table_name, String subject_title) {
        double avg = 0.0d;
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
            avg = getAverageOfGrade(exam_name, grade, section, subject_column_name);
        } catch (Exception e) {
            System.out.println("Exception at getAverageofGrade(table_name,subject_title)"
                    + " at CalculateAverageMarkOfSubject : " + e.getMessage());

        }
        return avg;
    }
    /**
     * it returns average marks of a subject of obtained by a section
     * @param table_name
     * @param subject_title
     * @return 
     */
     public double getAverageOfSection(String table_name, String subject_title) {
        double avg = 0.0d;
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
            avg = getAverageOfSection(exam_name, grade, section, subject_column_name);
        } catch (Exception e) {
            System.out.println("Exception at getAverageofGrade(table_name,subject_title)"
                    + " at CalculateAverageMarkOfSubject : " + e.getMessage());

        }
        return avg;
    }
}

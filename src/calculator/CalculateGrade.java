/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import login.LoginController;

/**
 *
 * @author subas
 */
public class CalculateGrade {

    private String ledger_id;
    private String table_name;
    private float gpa_obtained;
    private Connection connection = database.Connection.conn;
    private Statement statement;
    private ResultSet result_set;
    private String sql = null;

    //making the calculateGrade constructor for any input
    public CalculateGrade() {

    }

    public CalculateGrade(String ledger_id) {
        setLedgerId(ledger_id);
    }

    public CalculateGrade(int ledger_id) {
        setLedgerId(String.valueOf(ledger_id));
    }

    public CalculateGrade(float ledger_id) {
        setLedgerId(String.valueOf(ledger_id));
    }

    public CalculateGrade(double ledger_id) {
        setLedgerId(String.valueOf(ledger_id));
    }

    //setting the ledger_id, this must be always used to set the value
    private void setLedgerId(String ledger_id) {
        this.ledger_id = ledger_id;
        Statement st;
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        String year = LoginController.current_year;
        try {
            query = "SELECT Table_name FROM year_" + year + "_ledger WHERE "
                    + "Ledger_id = " + ledger_id;
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                table_name = result.getString(1);
            }
        } catch (Exception e) {
            System.out.println("Exception at setLedgerId() at CalcualteGrade : "
                    + e.getMessage());
        }
    }

    //giving the output for any type of ledger_id given so ........
    public ResultSet calculateAllGrade(double ledger_id) {
        setLedgerId(String.valueOf(ledger_id));
        return calculateAllGrade();
    }

    public ResultSet calculateAllGrade(float ledger_id) {
        setLedgerId(String.valueOf(ledger_id));
        return calculateAllGrade();
    }

    public ResultSet calculateAllGrade(int ledger_id) {
        setLedgerId(String.valueOf(ledger_id));
        return calculateAllGrade();
    }

    public ResultSet calculateAllGrade(String ledger_id) {
        setLedgerId(ledger_id);
        return calculateAllGrade();
    }

    public ResultSet calculateAllGrade() {
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        String year = LoginController.current_year;

        //getting the list of the subject titles from the ledger via table contaning 
        //marks
        try {
            //initializing the Statement object 'statement'
            statement = connection.createStatement();
            query = "SELECT Subject_title FROM year_" + year + "_marks WHERE Ledger_id  = "
                    + ledger_id;
            result = conn.createStatement().executeQuery(query);
            int i = 0;
            while (result.next()) {
                i++;
            }
            result.beforeFirst();
            String[] subject_title = new String[i];
            i = 0;
            while (result.next()) {
                subject_title[i] = result.getString(1);
                i++;
            }
            //creating a temporary table to get the table of grade calculated
            sql = "CREATE TEMPORARY TABLE IF NOT EXISTS temp ("
                    + " `Student_id` int primary key  "
                    + ", `Roll` int unique  "
                    + ", `Name` varchar(255)  ";
            for (String subject_title1 : subject_title) {
                sql = sql + ", `" + subject_title1 + "` varchar(20) ";
            }
            sql = sql + " ) ;";
            statement.addBatch(sql);
            sql = "TRUNCATE TABLE temp;";
            statement.addBatch(sql);
            sql = "INSERT INTO temp SELECT `Student_id`,`Roll`,`Name`";
            for (String subject_title1 : subject_title) {
                sql = sql + ", `" + subject_title1 + "` ";
            }
            sql = sql + " FROM " + table_name + " NATURAL JOIN year_" + year + "_student ;";
            statement.addBatch(sql);
            sql = "IF NOT EXISTS( SELECT NULL FROM INFORMATION_SCHEMA.COLUMNS"
                    + " WHERE table_name = 'temp'"
                    + " AND table_schema = 'school'"
                    + " AND column_name = 'GPA')  THEN"
                    + " ALTER TABLE `temp` ADD `GPA` FLOAT "
                    + "NOT NULL default '0';"
                    + " END IF;";
            statement.addBatch(sql);
            boolean gpa_zero = false;
            //getting the students id
            query = "SELECT Student_id FROM " + table_name + " ;";
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                gpa_obtained = 0f;
                for (i = 0; i < subject_title.length; i++) {
                    String g;
                    g = calculateGradeLetterOfASubject(subject_title[i], result.getString(1));
                    gpa_obtained = gpa_obtained + getGradePoint(g);
                    if ("NA".equals(g) || "F".equals(g)) {
                        gpa_zero = true;
                    }
                }
                if (gpa_zero) {
                    gpa_obtained = 0f;
                }
                double grade_point = Math.round((gpa_obtained / subject_title.length) * 100D) / 100D;
                sql = "UPDATE temp SET `GPA`= " + String.valueOf(grade_point) + " "
                        + "WHERE Student_id = " + String.valueOf(result.getInt(1)) + ";";
                statement.addBatch(sql);
            }
            statement.executeBatch();
            sql = "SELECT * FROM temp ORDER BY Roll;";
            statement.addBatch(sql);
            result_set = statement.executeQuery(sql);
        } catch (Exception e) {
            System.out.println("Exception at calculateAllGrade() at CalcualteGrade : "
                    + e.getMessage());
            e.printStackTrace();
        }

        return result_set;
    }

    /**
     * returns the gpa of a student when ledger_id is given
     *
     * @param student_id
     * @param ledger_id
     * @return
     */
    public String calculateGPAOfAStudent(String student_id, String ledger_id) {
        setLedgerId(ledger_id);
        Connection conn = database.Connection.conn;
        String gpa = null;
        String table_name = this.table_name;
        String query;
        ResultSet result;
        String year = LoginController.current_year;

        //getting the list of the subject titles from the ledger via table contaning 
        //marks
        try {
            //initializing the Statement object 'statement'
            statement = connection.createStatement();
            query = "SELECT Subject_title FROM year_" + year + "_marks WHERE Ledger_id  = "
                    + ledger_id + ";";
            result = conn.createStatement().executeQuery(query);
            int i = 0;
            while (result.next()) {
                i++;
            }
            result.beforeFirst();
            String[] subject_title = new String[i];
            i = 0;
            while (result.next()) {
                subject_title[i] = result.getString(1);
                i++;
            }
            sql="DROP TABLE IF EXISTS temp";
            statement.addBatch(sql);
            //creating a temporary table to get the table of grade calculated
            sql = "CREATE TEMPORARY TABLE IF NOT EXISTS temp ("
                    + " `Student_id` int primary key  "
                    + ", `Roll` int unique  "
                    + ", `Name` varchar(255)  ";
            for (String subject_title1 : subject_title) {
                sql = sql + ", `" + subject_title1 + "` varchar(20) ";
            }
            sql = sql + " ) ;";
            statement.addBatch(sql);
            sql = "TRUNCATE TABLE temp;";
            statement.addBatch(sql);
            sql = "INSERT INTO temp SELECT `Student_id`,`Roll`,`Name`";
            for (String subject_title1 : subject_title) {
                sql = sql + ", `" + subject_title1 + "` ";
            }
            sql = sql + " FROM " + table_name + " NATURAL JOIN year_" + year + "_student "
                    + " WHERE Student_id = " + student_id + ";";
            statement.addBatch(sql);
            sql = "IF NOT EXISTS( SELECT * FROM INFORMATION_SCHEMA.COLUMNS"
                    + " WHERE table_name = 'temp'"
                    + " AND table_schema = 'school'"
                    + " AND column_name = 'GPA')  "
                    + "THEN"
                    + " ALTER TABLE `temp` ADD `GPA` FLOAT "
                    + "NOT NULL default '0'; "
                    + " END IF ;";
            statement.addBatch(sql);
            boolean gpa_zero = false;
            //getting the students id
            query = "SELECT Student_id FROM " + table_name
                    + " WHERE Student_id = " + student_id + " ;";
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                gpa_obtained = 0f;
                for (i = 0; i < subject_title.length; i++) {
                    String g;
                    g = calculateGradeLetterOfASubject(subject_title[i], result.getString(1));
                    gpa_obtained = gpa_obtained + getGradePoint(g);
                    if ("NA".equals(g) || "F".equals(g)) {
                        gpa_zero = true;
                    }
                }
                if (gpa_zero) {
                    gpa_obtained = 0f;
                }
                double grade_point = Math.round((gpa_obtained / subject_title.length) * 100D) / 100D;
                sql = "UPDATE temp SET `GPA`= " + String.valueOf(grade_point) + " "
                        + "WHERE Student_id = " + String.valueOf(result.getInt(1)) + ";";
                statement.addBatch(sql);
            }
            statement.executeBatch();
            sql = "SELECT * FROM temp ORDER BY Roll;";
            statement.addBatch(sql);
            result_set = statement.executeQuery(sql);
            while (result_set.next()) {
                gpa = String.valueOf(result_set.getFloat("GPA"));
            }
            sql = "DROP TEMPORARY TABLE temp";
            conn.createStatement().execute(sql);
        } catch (Exception e) {
            System.out.println("Exception at calculateGPAOfAStudent() "
                    + "at CalcualteGPAOfAStudent : "
                    + e.getMessage());
            e.printStackTrace();
        }

        return gpa;
    }

    private float getMaximumGradePoint() {
        float max = 0;
        ResultSet result = null;
        String query;
        Connection conn = database.Connection.conn;
        String year = LoginController.current_year;
        try {
            query = "SELECT MAX(CAST(`Grade_point` AS DOUBLE)) FROM year_" + year + "_grading";
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                max = Float.valueOf(String.valueOf(result.getDouble(1)));
            }
        } catch (Exception e) {
            System.out.println("Exception at getMaximumGradePoint() at "
                    + "CalculateGrade() : " + e.getMessage());
        }
        return max;
    }

    private float getGradePoint(String grade_letter) {
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        String year = LoginController.current_year;
        String grade_point = null;
        try {
            query = "SELECT Grade_point FROM year_" + year + "_grading WHERE "
                    + "Grade_letter = '" + grade_letter + "';";
            result = conn.createStatement().executeQuery(query);
            while (result.next()) {
                grade_point = result.getString(1);
            }
        } catch (Exception e) {
            System.out.println("Exception at getGradePoint() at CalculateGrade : "
                    + e.getMessage());

        }
        if (grade_point == null) {
            return 0f;
        } else {
            return Float.valueOf(grade_point);
        }
    }

    /**
     * it calculate the grade letter of a subject as well as updates the marks
     * with the grade in the temporary ledger created
     *
     *
     * @param subject_title
     * @param student_id
     * @return
     */
    private String calculateGradeLetterOfASubject(String subject_title, String student_id) {
        String grade_letter = null;
        Connection conn = database.Connection.conn;
        ResultSet result = null;
        String query;
        String year = LoginController.current_year;
        try {
            //getting the marks obtained in that subject
            query = "SELECT `" + subject_title + "` FROM " + table_name + " WHERE "
                    + "Student_id = " + student_id;
            result = conn.createStatement().executeQuery(query);
            float marks = 0.00F;
            while (result.next()) {
                marks = result.getFloat(1);
            }
            //gettting the full marks and pass marks of the subject
            query = "SELECT FM,PM " + "FROM year_" + year + "_marks WHERE "
                    + "Subject_title = '" + subject_title + "' AND Ledger_id = '"
                    + ledger_id + "'";
            result = conn.createStatement().executeQuery(query);
            float fm = 0.00f, pm = 0.00f;
            while (result.next()) {
                fm = result.getFloat("FM");
                pm = result.getFloat("PM");
            }
            if (marks < pm) {
                grade_letter = "F";
            } else {
                float percentage = (marks / fm) * 100;
                //getting the grade criteria and assigning the grade letter
                // query for getting the grade point
                query = "SELECT Per_from,Per_to,Grade_letter FROM year_" + year + "_grading ";
                result = conn.createStatement().executeQuery(query);
                boolean available = false;
                while (result.next()) {
                    float from = Float.valueOf(result.getString(1));
                    float to = Float.valueOf(result.getString(2));
                    if (percentage >= from && percentage <= to) {
                        available = true;
                        grade_letter = result.getString(3);
                    }
                }
                if (!available) {
                    grade_letter = "NA";
                }

            }
            //changing the value in the subject in database
            sql = "UPDATE temp SET `" + subject_title + "`= '" + grade_letter + "' "
                    + "WHERE Student_id = " + student_id + ";";
            statement.addBatch(sql);
        } catch (Exception e) {
            System.out.println("Exception at calculateGradeOfASubject() at "
                    + "CalcualateGrade : " + e.getMessage());
        }

        return grade_letter;
    }
}

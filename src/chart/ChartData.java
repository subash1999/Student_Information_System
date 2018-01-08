/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chart;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import login.LoginController;

/**
 *
 * @author subas
 */
public class ChartData {

    public List<String> grade_id = new ArrayList<String>();
    public String exam_id = null;
    public String year = LoginController.current_year;
    public String student_id;

    public ChartData() {

    }

    public ChartData(String exam_id, List<String> grade_id) {
        setValues(exam_id, grade_id);

    }

    private void setValues(String exam_id, List<String> grade_id) {
        this.grade_id.addAll(grade_id);
        this.exam_id = exam_id;
    }

    private void setValues(String exam_id) {
        this.exam_id = exam_id;
    }

    private void setValues(String exam_id, List<String> grade_id, String student_id) {
        this.grade_id.addAll(grade_id);
        this.exam_id = exam_id;
        this.student_id = student_id;
    }

    /**
     * returns List<List> which contains the pass and fail data of a exam, which
     * is used to create the chart
     *
     * @param exam_id
     * @return
     */
    public List<List> getExamPassFailData(String exam_id) {
        setValues(exam_id);
        List<List> data = new ArrayList<List>();
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            List<String> table_name = new ArrayList<String>();
            String query = null;
            PreparedStatement pst = null;
            ResultSet result = null;
            query = "SELECT Table_name FROM year_" + year + "_ledger "
                    + "WHERE Exam_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, Integer.valueOf(exam_id));
            result = pst.executeQuery();
            while (result.next()) {
                table_name.add(result.getString(1));
            }
            int total_pass = 0;
            int total_fail = 0;
            for (String table_name_1 : table_name) {
                query = "SELECT Result,COUNT(Result) FROM " + table_name_1 + " "
                        + " GROUP BY Result";
                pst = conn.prepareStatement(query);
                result = pst.executeQuery();
                while (result.next()) {
                    if ("pass".equalsIgnoreCase(result.getString(1))) {
                        total_pass = total_pass + result.getInt(2);

                    } else {
                        total_fail = total_fail + result.getInt(2);
                    }
                }
            }
            List list = new ArrayList();
            //adding the pass in the list
            list.add("Pass(" + total_pass + ")");
            list.add(total_pass);
            data.add(list);
            //adding the fail in the list but first clearing the already present data
            List list2 = new ArrayList();
            list2.add("Fail(" + total_fail + ")");
            list2.add(total_fail);
            data.add(list2);

        } catch (Exception e) {
            System.out.println("Exception at public List<List> examPassFailData() "
                    + "at ChartData  : " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    /**
     * returns List<List> containing data about pass and fail numbers of a
     * grade/section if size of grade_id list > 1 then it gives the combined
     * pass fail data of grade id passed if size of grade_id list>0 then it
     * gives the pass fail data of a single section or grade given empty if size
     * of grade_id = 0 used to create a chart
     *
     * @param exam_id
     * @param grade_id
     * @return
     */
    public List<List> getGradePassFailData(String exam_id, List<String> grade_id) {
        setValues(exam_id, grade_id);
        List<List> data = new ArrayList<List>();
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            List<String> table_name = new ArrayList<String>();
            String query = null;
            PreparedStatement pst = null;
            ResultSet result = null;
            for (String grade_id_1 : grade_id) {
                query = "SELECT Table_name FROM year_" + year + "_ledger "
                        + "WHERE Grade_id = ? AND Exam_id = ?";
                pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.valueOf(grade_id_1));
                pst.setInt(2, Integer.valueOf(exam_id));
                result = pst.executeQuery();
                while (result.next()) {
                    table_name.add(result.getString(1));
                }
            }
            int total_pass = 0;
            int total_fail = 0;
            for (String table_name_1 : table_name) {
                query = "SELECT Result,COUNT(Result) FROM " + table_name_1 + " "
                        + " GROUP BY Result";
                pst = conn.prepareStatement(query);
                result = pst.executeQuery();
                while (result.next()) {
                    if ("Pass".equalsIgnoreCase(result.getString(1))) {
                        total_pass = total_pass + result.getInt(2);
                    } else if ("Fail".equalsIgnoreCase(result.getString(1))) {
                        total_fail = total_fail + result.getInt(2);
                    }
                }
            }
            List list = new ArrayList();
            list.add("Pass(" + total_pass + ")");
            list.add(total_pass);
            data.add(list);
            List list2 = new ArrayList();
            list2.add("Fail(" + total_fail + ")");
            list2.add(total_fail);
            data.add(list2);
        } catch (Exception e) {
            System.out.println("Exception at public List<List> getGradePassFailData() "
                    + "at ChartData  : " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }

    /**
     * returns List<List> containing data about no of students obtaining
     * different divisions of a grade/section if size of grade_id list > 1 then
     * it gives the combined pass fail data of grade id passed if size of
     * grade_id list>0 then it gives the pass fail data of a single section or
     * grade given empty if size of grade_id = 0 used to create a chart
     *
     * @param exam_id
     * @param grade_id
     * @return
     */
    public List<List> getDivisionData(String exam_id, List<String> grade_id) {
        setValues(exam_id, grade_id);
        List<List> data = new ArrayList<List>();
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            List<String> table_name = new ArrayList<String>();
            String query = null;
            PreparedStatement pst = null;
            ResultSet result = null;
            for (String grade_id_1 : grade_id) {
                query = "SELECT Table_name FROM year_" + year + "_ledger "
                        + "WHERE Grade_id = ? AND Exam_id = ? ;";
                pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.valueOf(grade_id_1));
                pst.setInt(2, Integer.valueOf(exam_id));
                result = pst.executeQuery();
                while (result.next()) {
                    table_name.add(result.getString(1));
                }
            }
            //getting the list of division
            query = "SELECT DISTINCT Division_name FROM year_" + year + "_percentage ;";
            pst = conn.prepareStatement(query);
            result = pst.executeQuery();
            List<String> div = new ArrayList();
            while (result.next()) {
                div.add(result.getString(1));
            }
            String[] division = new String[div.size()];
            int i = 0;
            for (String d : div) {
                division[i] = d;
                i++;
            }
            //array to store no of  student acheving a division in the same
            //index as the division name in "division" string
            int[] division_count = new int[div.size()];
            Arrays.fill(division_count, 0);
            int division_not_found_count = 0;
            for (String table_name_1 : table_name) {
                query = "SELECT Division,COUNT(Result) FROM " + table_name_1 + " "
                        + " GROUP BY Division";
                pst = conn.prepareStatement(query);
                result = pst.executeQuery();
                while (result.next()) {
                    boolean division_not_found = true;
                    for (i = 0; i < division.length; i++) {
                        String d = division[i];
                        if (d.equalsIgnoreCase(result.getString(1))) {
                            division_count[i] = division_count[i] + result.getInt(2);
                            division_not_found = false;
                        }
                    }
                    if (division_not_found) {
                        division_not_found_count = division_not_found_count + result.getInt(2);
                    }
                }
            }
            for (i = 0; i < division.length; i++) {
                List list = new ArrayList();
                list.add(division[i]+"("+division_count[i]+")");
                list.add(division_count[i]);
                data.add(list);
            }
            if (division_not_found_count != 0) {
                List list = new ArrayList();
                list.add("No Division Found("+division_not_found_count+")");
                list.add(division_not_found_count);
                data.add(list);
            }
        } catch (Exception e) {
            System.out.println("Exception at public List<List> getDivisionData() "
                    + "at ChartData  : " + e.getMessage());
        }
        return data;
    }
     /**
     * returns List<List> containing data about pass and fail numbers of male student from
     * grade/section if size of grade_id list > 1 then it gives the combined
     * pass fail data of grade id passed if size of grade_id list>0 then it
     * gives the pass fail data of a single section or grade given empty if size
     * of grade_id = 0 used to create a chart
     *
     * @param exam_id
     * @param grade_id
     * @return
     */
    public List<List> getMalePassFailData(String exam_id, List<String> grade_id) {
        setValues(exam_id, grade_id);
        List<List> data = new ArrayList<List>();
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            List<String> table_name = new ArrayList<String>();
            String query = null;
            PreparedStatement pst = null;
            ResultSet result = null;
            for (String grade_id_1 : grade_id) {
                query = "SELECT Table_name FROM year_" + year + "_ledger "
                        + "WHERE Grade_id = ? AND Exam_id = ?";
                pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.valueOf(grade_id_1));
                pst.setInt(2, Integer.valueOf(exam_id));
                result = pst.executeQuery();
                while (result.next()) {
                    table_name.add(result.getString(1));
                }
            }
            int total_pass = 0;
            int total_fail = 0;
            for (String table_name_1 : table_name) {
                query = "SELECT Result,COUNT(Result) FROM " + table_name_1 + " "
                        + "INNER JOIN year_"+year+"_student "
                        + "WHERE "+table_name_1+".Student_id = "
                        + "year_"+year+"_student.Student_id AND (Gender = 'Male' "
                        + " OR Gender = 'M')"
                        + " GROUP BY Result  ";
                pst = conn.prepareStatement(query);
                result = pst.executeQuery();
                while (result.next()) {
                    if ("Pass".equalsIgnoreCase(result.getString(1))) {
                        total_pass = total_pass + result.getInt(2);
                    } else if ("Fail".equalsIgnoreCase(result.getString(1))) {
                        total_fail = total_fail + result.getInt(2);
                    }
                }
            }
            List list = new ArrayList();
            list.add("Pass");
            list.add(total_pass);
            data.add(list);
            List list2 = new ArrayList();
            list2.add("Fail");
            list2.add(total_fail);
            data.add(list2);
        } catch (Exception e) {
            System.out.println("Exception at public List<List> getMalePassFailData() "
                    + "at ChartData  : " + e.getMessage());
        }
        return data;
    }
    /**
     * returns List<List> containing data about pass and fail numbers of male student from
     * grade/section if size of grade_id list > 1 then it gives the combined
     * pass fail data of grade id passed if size of grade_id list>0 then it
     * gives the pass fail data of a single section or grade given empty if size
     * of grade_id = 0 used to create a chart
     *
     * @param exam_id
     * @param grade_id
     * @return
     */
    public List<List> getFemalePassFailData(String exam_id, List<String> grade_id) {
        setValues(exam_id, grade_id);
        List<List> data = new ArrayList<List>();
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            List<String> table_name = new ArrayList<String>();
            String query = null;
            PreparedStatement pst = null;
            ResultSet result = null;
            for (String grade_id_1 : grade_id) {
                query = "SELECT Table_name FROM year_" + year + "_ledger "
                        + "WHERE Grade_id = ? AND Exam_id = ?";
                pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.valueOf(grade_id_1));
                pst.setInt(2, Integer.valueOf(exam_id));
                result = pst.executeQuery();
                while (result.next()) {
                    table_name.add(result.getString(1));
                }
            }
            int total_pass = 0;
            int total_fail = 0;
            for (String table_name_1 : table_name) {
                query = "SELECT Result,COUNT(Result) FROM " + table_name_1 + " "
                        + "INNER JOIN year_"+year+"_student "
                        + "WHERE "+table_name_1+".Student_id = "
                        + "year_"+year+"_student.Student_id AND (Gender = 'Female' "
                        + " OR Gender = 'f')"
                        + " GROUP BY Result  ";
                pst = conn.prepareStatement(query);
                result = pst.executeQuery();
                while (result.next()) {
                    if ("Pass".equalsIgnoreCase(result.getString(1))) {
                        total_pass = total_pass + result.getInt(2);
                    } else if ("Fail".equalsIgnoreCase(result.getString(1))) {
                        total_fail = total_fail + result.getInt(2);
                    }
                }
            }
            List list = new ArrayList();
            list.add("Pass");
            list.add(total_pass);
            data.add(list);
            List list2 = new ArrayList();
            list2.add("Fail");
            list2.add(total_fail);
            data.add(list2);
        } catch (Exception e) {
            System.out.println("Exception at public List<List> getFemalePassFailData() "
                    + "at ChartData  : " + e.getMessage());
            e.printStackTrace();
        }
        return data;
    }
    /**
     * returns the subject wise marks obtained by a student in a exam, of a
     * given grade in the grade_id list there can be many grade_id but only the
     * data from the subject whose exam is given by the student and the grade
     * student belongs to will be obtained the grade_list must contain the
     * grade_id which the student belongs to
     *
     * @param exam_id
     * @param grade_id
     * @param student_id
     * @return
     */
    public List<List> getStudentSubjectWiseMarkData(String exam_id, List<String> grade_id, String student_id) {
        setValues(exam_id, grade_id, student_id);
        List<List> data = new ArrayList<List>();
        try {
            database.Connection.connect();
            Connection conn = database.Connection.conn;
            List<String> table_name = new ArrayList<String>();
            int ledger_id = 0;
            String query = null;
            PreparedStatement pst = null;
            ResultSet result = null;
            for (String grade_id_1 : grade_id) {
                query = "SELECT Table_name,Ledger_id FROM year_" + year + "_ledger "
                        + "WHERE Grade_id = ? AND Exam_id = ? ;";
                pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.valueOf(grade_id_1));
                pst.setInt(2, Integer.valueOf(exam_id));
                result = pst.executeQuery();
                while (result.next()) {
                    table_name.add(result.getString(1));
                }
            }
            String final_table_name = null;
            for (String table_name_1 : table_name) {
                query = "SELECT * FROM " + table_name_1 + " WHERE "
                        + "Student_id = ?";
                pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.valueOf(student_id));
                result = pst.executeQuery();
                while (result.next()) {
                    final_table_name = table_name_1;
                }
            }
            query = "SELECT Ledger_id FROM year_" + year + "_ledger WHERE "
                    + " Table_name = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, final_table_name);
            result = pst.executeQuery();
            while (result.next()) {
                ledger_id = result.getInt(1);
            }
            List<String> subject_list = new ArrayList();
            query = "SELECT Subject_title FROM year_" + year + "_marks WHERE "
                    + "Ledger_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, ledger_id);
            result = pst.executeQuery();
            while (result.next()) {
                subject_list.add(result.getString(1));
            }

            for (String subject_list_1 : subject_list) {
                query = "SELECT " + subject_list_1 + " FROM " + final_table_name + " "
                        + "WHERE Student_id = ?";
                pst = conn.prepareStatement(query);
                pst.setInt(1, Integer.valueOf(student_id));
                result = pst.executeQuery();
                while (result.next()) {
                    List list = new ArrayList();
                    list.add(subject_list_1);
                    if (result.getFloat(1) < 0) {
                        list.add(0);
                    } else {
                        list.add(result.getFloat(1));
                    }
                    data.add(list);
                }
            }

        } catch (Exception e) {
            System.out.println("Exception at public List<List> getStudentSubjectWiseMarkData() "
                    + "at ChartData  : " + e.getMessage());
        }
        return data;
    }

    /**
     * returns the List<List> which contains the data of the average marks of
     * the subjects of a grade/section. if a single grade_id is given then the
     * only section is evaluated but if all the grade_id of all sections is
     * given then the average is calculated from the overall data
     *
     * @param exam_id
     * @param grade_id
     * @param student_id
     * @return
     */
    private List<List> getAverageSubjectWiseMarkData(String exam_id, List<String> grade_id, String student_id) {
        List<List> data = new ArrayList();
        List<Integer> ledger_id = new ArrayList<Integer>();

        if (grade_id.size() == 1) {
            for (String grade_id_1 : grade_id) {
                String query = "SELECT Ledger_id FROM year_" + year + "_ledger";
            }
        } else if (grade_id.size() > 1) {

        }
        return data;
    }
}

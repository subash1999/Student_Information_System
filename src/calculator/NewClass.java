/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculator;

import login.LoginController;

/**
 *
 * @author subas
 */
public class NewClass {
    public static void main(String[] args){
        database.Connection.connect();
        String year = LoginController.current_year;
        CalculateHighestMarkOfSubject ca = new CalculateHighestMarkOfSubject();        
        double a = ca.getHighestOfSection("first_term", "8", "null","English_th 40/100" );
        double b = ca.getHighestOfGrade("first_term", "8", "null","English_th 40/100" );
        double c = ca.getHighestOfSection("year_2074_first_term_1", "English_th 40/100");
        double d = ca.getHighestOfGrade("year_2074_first_term_1", "English_th 40/100");
        System.out.println("Section 1 : "+a);
        System.out.println("Section 2 : "+c);
        System.out.println("Grade1 : "+b);
        System.out.println("Grade 2 : "+ d);
    }
}

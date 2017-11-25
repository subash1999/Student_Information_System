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
        LoginController.current_year="2074";
        CalculateGrade c = new CalculateGrade("34");        
        c.calculateAllGrade();
    }
}

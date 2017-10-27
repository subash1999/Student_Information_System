/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ledger;

import login.LoginController;

/**
 *
 * @author subas
 */
public class main {
    public static void main(String[] args){
        database.Connection.connect();
         LoginController.current_year = "2074";
        CalculateResult c = new CalculateResult();      
        c.setLedgerId("36");
        c.calculateDivision();
    }
}

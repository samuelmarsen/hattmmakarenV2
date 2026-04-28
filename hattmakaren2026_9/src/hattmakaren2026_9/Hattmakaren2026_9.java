/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package hattmakaren2026_9;

import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.SQLException;
import oru.inf.InfException;
import oru.inf.InfDB;


/**
 *
 * @author Linus
 */
public class Hattmakaren2026_9 {

    /**
     * @param args the command line arguments
     */
    
    private static InfDB idb;
    
    
    public static void main(String[] args) {
        // TODO code application logic here
     
 
        try {
            idb = new InfDB("hattmakaren", "3306", "hattAdmin26", "hattAdmin26PW");
            System.out.println("Anslutningen lyckades!");

            new LoginMedarbetare(idb).setVisible(true);
        } catch (InfException ex) {
            System.out.println("Ett fel uppstod vid uppstart: " + ex.getMessage());
        }
    }
}
   
    
    
    


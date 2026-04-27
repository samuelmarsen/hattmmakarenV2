/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hattmakaren2026_9;

import javax.swing.JOptionPane;
import javax.swing.JTextField;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
/**
 *
 * @author franz
 */
public class Validering {
    // Kontrollera om fältet är tomt
    public static boolean arTom(JTextField falt, String felmeddelande) {
        if (falt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, felmeddelande);
            falt.requestFocus();
            return true;
        }
        return false;
    }

    // Kontrollera om det är ett giltigt heltal 
    public static boolean arHeltal(JTextField falt) {
        try {
            Integer.parseInt(falt.getText());
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Var god ange ett heltal.");
            return false;
        }
    }

    // Kontrollera pris/decimal
    public static boolean arDecimal(JTextField falt) {
        try {
            String text = falt.getText().replace(',', '.'); 
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ogiltigt pris. Använd siffror.");
            return false;
        }
    }

    // kontrollera datumformat
    public static boolean arGiltigtDatum(JTextField falt) {
        try {
            LocalDate.parse(falt.getText());
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Ange datum i formatet ÅÅÅÅ-MM-DD ");
            return false;
        }
    }
    // Kontrollera om e-posten har rätt format
public static boolean isEpostGiltig(JTextField falt) {
    String epost = falt.getText().trim();
    
    String epostMönster = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    
    if (!epost.matches(epostMönster)) {
        JOptionPane.showMessageDialog(null, "E-postadressen är inte giltig. "
                + "\nSe till att den innehåller @ och en domän ( .se eller .com).", 
                "Felaktigt format", JOptionPane.ERROR_MESSAGE);
        falt.requestFocus();
        return false;
    }
    return true;
}



public static boolean arPositivtTal(JTextField falt) {
    try {
        double värde = Double.parseDouble(falt.getText().replace(',', '.'));
        if (värde < 0) {
            JOptionPane.showMessageDialog(null, "Värdet kan inte vara negativt.");
            return false;
        }
        return true;
    } catch (NumberFormatException e) {
        return false; 
    }
}

public static boolean arGiltigtTelefonnummer(JTextField falt) {
    String tel = falt.getText().trim();
    if (!tel.matches("^[0-9+\\s-]+$")) {
        JOptionPane.showMessageDialog(null, "Telefonnumret får bara innehålla siffror, mellanslag eller bindestreck.");
        falt.requestFocus();
        return false;
    }
    return true;
}
public static boolean arGiltigBestallning(JTextField falt, String artikelNamn) {
        String text = falt.getText().trim();

        if (text.contains(",")) {
            JOptionPane.showMessageDialog(null, 
                "Fel i artikel: " + artikelNamn + "\nAnvänd punkt (.) istället för komma (,).", 
                "Formatfel", JOptionPane.WARNING_MESSAGE);
            falt.requestFocus();
            return false;
        }

        // Kontrollera om det är siffror
        try {
            double varde = Double.parseDouble(text);
            
            // 3. Kontrollera negativt tal
            if (varde < 0) {
                JOptionPane.showMessageDialog(null, 
                    "Fel i artikel: " + artikelNamn + "\nAntalet kan inte vara negativt.", 
                    "Inmatningsfel", JOptionPane.ERROR_MESSAGE);
                falt.requestFocus();
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, 
                "Fel i artikel: " + artikelNamn + "\nAnge enbart siffror (bokstäver är inte tillåtna).", 
                "Inmatningsfel", JOptionPane.ERROR_MESSAGE);
            falt.requestFocus();
            return false;
        }
    }



public static boolean harForOchEfternamn(JTextField falt) {
    String text = falt.getText().trim();

    String namnMonster = "^[a-zA-ZåäöÅÄÖ]+[\\s-]+[a-zA-ZåäöÅÄÖ\\s-]+$";

    if (!text.matches(namnMonster)) {
        JOptionPane.showMessageDialog(null, 
            "Ange både för- och efternamn (endast bokstäver och mellanslag).", 
            "Inmatningsfel", JOptionPane.ERROR_MESSAGE);
        falt.requestFocus();
        return false;
    }
    return true;
}
}

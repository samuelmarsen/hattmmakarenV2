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
    // 1. Kontrollera om fältet är tomt
    public static boolean arTom(JTextField falt, String felmeddelande) {
        if (falt.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, felmeddelande);
            falt.requestFocus();
            return true;
        }
        return false;
    }

    // 2. Kontrollera om det är ett giltigt heltal (t.ex. för Antal eller ModellID)
    public static boolean arHeltal(JTextField falt) {
        try {
            Integer.parseInt(falt.getText());
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Var god ange ett heltal.");
            return false;
        }
    }

    // 3. Kontrollera pris/decimal
    public static boolean arDecimal(JTextField falt) {
        try {
            String text = falt.getText().replace(',', '.'); // Hanterar om användaren skriver komma
            Double.parseDouble(text);
            return true;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Ogiltigt pris. Använd siffror.");
            return false;
        }
    }

    // 4. Kontrollera datumformat (Viktigt för OrderDatum)
    // Förväntar sig formatet ÅÅÅÅ-MM-DD
    public static boolean arGiltigtDatum(JTextField falt) {
        try {
            LocalDate.parse(falt.getText());
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(null, "Ange datum i formatet ÅÅÅÅ-MM-DD ");
            return false;
        }
    }
    // Kontrollera om e-posten har rätt format (innehåller @ och punkt på rätt ställen)
public static boolean isEpostGiltig(JTextField falt) {
    String epost = falt.getText().trim();
    
    // Denna "regex" kollar: text + @ + text + punkt + text
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
        return false; // Hanteras oftast av arDecimal redan
    }
}

// Kontrollerar att telefonnumret bara innehåller siffror, mellanslag eller bindestreck
public static boolean arGiltigtTelefonnummer(JTextField falt) {
    String tel = falt.getText().trim();
    // Tillåter siffror, mellanslag, plus och bindestreck
    if (!tel.matches("^[0-9+\\s-]+$")) {
        JOptionPane.showMessageDialog(null, "Telefonnumret får bara innehålla siffror, mellanslag eller bindestreck.");
        falt.requestFocus();
        return false;
    }
    return true;
}

// Kontrollerar att fältet innehåller minst två namn (för- och efternamn) separerade med mellanslag
public static boolean harForOchEfternamn(JTextField falt) {
    String text = falt.getText().trim();

    // Regex förklaring:
    // ^[a-zA-ZåäöÅÄÖ]+  -> Börja med minst en bokstav
    // \s+               -> Kräv minst ett mellanslag
    // [a-zA-ZåäöÅÄÖ]+$  -> Avsluta med minst en bokstav
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

public static boolean arGiltigDecimal(JTextField falt) {
String text = falt.getText().trim();
    if (text.contains(",")) {
        JOptionPane.showMessageDialog(null, "Använd punkt (.) istället för komma (,).");
        return false; // VIKTIGT: Denna returnerar false till knappen
    }
    try {
        Double.parseDouble(text);
        return true; // OK!
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(null, "Ange siffror (t.ex. 10.5).");
        return false; // Fel format!
    }
}}

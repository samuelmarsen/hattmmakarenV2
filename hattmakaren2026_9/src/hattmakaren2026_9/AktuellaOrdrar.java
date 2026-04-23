/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hattmakaren2026_9;
import oru.inf.InfException;
import oru.inf.InfDB;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.JTable;

/**
 *
 * @author lucasbrautigam
 */
public class AktuellaOrdrar extends javax.swing.JFrame {
    private InfDB idb;
    
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(AktuellaOrdrar.class.getName());

    /**
     * Creates new form AktuellaOrdrar
     */
    public AktuellaOrdrar(InfDB idb) {
        this.idb = idb;
        initComponents();
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        fyllOrderTabell();
        
        jtAktuellaOrdrar.setDefaultEditor(Object.class, null);
        
        jtAktuellaOrdrar.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                visaOrderInnehall();
            }
        }
    });
    }
    
    private void fyllOrderTabell(){
        try {
            String sql = "SELECT * FROM Ordrar WHERE Status != 'Klar'";
            
            ArrayList<HashMap<String, String>> allaOrdrar = idb.fetchRows(sql);
            
            DefaultTableModel model = (DefaultTableModel)jtAktuellaOrdrar.getModel();
            model.setRowCount(0);
            
            if(allaOrdrar != null) {
                // Loopen måste börja HÄR, så att vi går igenom en rad i taget
                for(HashMap<String, String> rad : allaOrdrar) {
                    
                    // 1. Hämta värdet för den aktuella raden
                    String snabbVarde = rad.get("ArSnabborder");
            
                    // 2. Skapa en textvariabel som översätter värdet
                    String snabbText = "Nej"; 
                    if (snabbVarde != null && (snabbVarde.equals("1") || snabbVarde.equalsIgnoreCase("true"))) {
                        snabbText = "Ja";
                    }
                    
                    // 3. Lägg till raden i tabellen
                    model.addRow(new Object[]{
                        rad.get("OrderID"),
                        rad.get("KundID"),
                        rad.get("OrderDatum"),
                        rad.get("Status"),
                        snabbText, // <-- Här skickar vi in "Ja" eller "Nej"
                        rad.get("FraktAdress"),
                        rad.get("TotalPrisInclMoms")
                    });
                }
            }
        } catch(InfException ex) {
            JOptionPane.showMessageDialog(null, "Kunde inte hämta ordrar: "+ ex.getMessage());
        }
        
        // --- Denna del ska bara finnas EN gång i slutet av metoden ---
        jtAktuellaOrdrar.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jtAktuellaOrdrar.getColumnModel().getColumn(0).setPreferredWidth(100);  
        jtAktuellaOrdrar.getColumnModel().getColumn(1).setPreferredWidth(100);  
        jtAktuellaOrdrar.getColumnModel().getColumn(2).setPreferredWidth(250); 
        jtAktuellaOrdrar.getColumnModel().getColumn(3).setPreferredWidth(200);
        jtAktuellaOrdrar.getColumnModel().getColumn(4).setPreferredWidth(150);  
        jtAktuellaOrdrar.getColumnModel().getColumn(5).setPreferredWidth(300); 
        jtAktuellaOrdrar.getColumnModel().getColumn(6).setPreferredWidth(150);
    }
    private void visaOrderInnehall() {
    int valdRad = jtAktuellaOrdrar.getSelectedRow();

        if (valdRad == -1) {
            return;
        }

        try {
            String orderID = jtAktuellaOrdrar.getValueAt(valdRad, 0).toString();

            String sqlRader =  "SELECT H.ModellNamn, H.Farg, H.Tyg, H.Storlek, O.Antal " + 
                               "FROM Orderrader O " + "JOIN Hattmodeller H ON O.ModellID = H.ModellID " +                      
                               "WHERE O.OrderID = " + orderID;
            ArrayList<HashMap<String, String>> rader = idb.fetchRows(sqlRader);

            String sqlMedarbetare = "SELECT DISTINCT a.Namn " +
                                    "FROM Arbetspass ap " +
                                    "JOIN Anstallda a ON ap.AnstalldID = a.AnstalldID " +
                                    "WHERE ap.OrderID = " + orderID;
            ArrayList<HashMap<String, String>> medarbetare = idb.fetchRows(sqlMedarbetare);

            StringBuilder sb = new StringBuilder();
            sb.append("ORDERDETALJER FÖR ORDER ").append(orderID).append("\n");
            sb.append("=====================================\n\n");

            if (rader != null && !rader.isEmpty()) {
                for (HashMap<String, String> rad : rader) {
                    sb.append("Modell: ").append(rad.get("ModellNamn")).append("\n");
                    sb.append("Färg: ").append(rad.get("Farg")).append("\n");
                    sb.append("Tyg: ").append(rad.get("Tyg")).append("\n");
                    sb.append("Storlek: ").append(rad.get("Storlek")).append("\n");
                    sb.append("Antal: ").append(rad.get("Antal")).append(" st\n");
                    sb.append("-------------------------------------\n");
                }
            } else {
                sb.append("Inga orderrader finns registrerade.\n\n");
            }

            sb.append("\nMEDARBETARE SOM ARBETAT PÅ ORDERN:\n");
            if (medarbetare != null && !medarbetare.isEmpty()) {
                for (HashMap<String, String> person : medarbetare) {
                    sb.append("- ").append(person.get("Namn")).append("\n");
                                sb.append("=====================================\n");

                }
            } else {
                sb.append("Ingen medarbetare har loggat arbetspass på denna order ännu.\n");
            }

            txtOrderDetaljer.setText(sb.toString());
            txtOrderDetaljer.setCaretPosition(0);

        } catch (InfException ex) {
            JOptionPane.showMessageDialog(this, "Fel vid hämtning av orderinnehåll: " + ex.getMessage());
        }
}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        btnTillbaka = new javax.swing.JButton();
        btnRedigeraOrder = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtAktuellaOrdrar = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOrderDetaljer = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        btnTillbaka.setText("Tillbaka");
        btnTillbaka.addActionListener(this::btnTillbakaActionPerformed);
        getContentPane().add(btnTillbaka, new java.awt.GridBagConstraints());

        btnRedigeraOrder.setText("Redigera Order");
        btnRedigeraOrder.addActionListener(this::btnRedigeraOrderActionPerformed);
        getContentPane().add(btnRedigeraOrder, new java.awt.GridBagConstraints());

        jtAktuellaOrdrar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "OrderID", "KundID", "OrderDatum", "Status", "ArSnabborder", "FraktAdress", "TotalPrisExklMoms"
            }
        ));
        jScrollPane2.setViewportView(jtAktuellaOrdrar);

        txtOrderDetaljer.setEditable(false);
        txtOrderDetaljer.setColumns(20);
        txtOrderDetaljer.setRows(5);
        jScrollPane1.setViewportView(txtOrderDetaljer);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 819, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTillbakaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTillbakaActionPerformed
    this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_btnTillbakaActionPerformed

    private void btnRedigeraOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRedigeraOrderActionPerformed
int radIndex = jtAktuellaOrdrar.getSelectedRow();
    
    if (radIndex == -1) {
        JOptionPane.showMessageDialog(this, "Vänligen välj en order i tabellen först!");
        return;
    }

    try {
        // Hämta nuvarande värden
        String orderID = String.valueOf(jtAktuellaOrdrar.getValueAt(radIndex, 0));
        
        //Skapa textfält för alla värden och fyll dem med nuvarande data
        javax.swing.JTextField txtDatum = new javax.swing.JTextField(String.valueOf(jtAktuellaOrdrar.getValueAt(radIndex, 2)));
        javax.swing.JTextField txtStatus = new javax.swing.JTextField(String.valueOf(jtAktuellaOrdrar.getValueAt(radIndex, 3)));
        javax.swing.JTextField txtSnabb = new javax.swing.JTextField(String.valueOf(jtAktuellaOrdrar.getValueAt(radIndex, 4)));
        javax.swing.JTextField txtAdress = new javax.swing.JTextField(String.valueOf(jtAktuellaOrdrar.getValueAt(radIndex, 5)));
        javax.swing.JTextField txtPris = new javax.swing.JTextField(String.valueOf(jtAktuellaOrdrar.getValueAt(radIndex, 6)));

        // Designa panelen som ska visas i rutan
        Object[] message = {
            "OrderDatum (YYYY-MM-DD HH:MM:SS):", txtDatum,
            "Status:", txtStatus,
            "Snabborder (Ja/Nej):", txtSnabb,
            "FraktAdress:", txtAdress,
            "TotalPris inkl moms:", txtPris
        };

       
        int option = JOptionPane.showConfirmDialog(this, message, "Redigera Order " + orderID, JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
        
        // --- BÖRJAN PÅ STEG 2 (Läggs in precis i början av if-satsen) ---
        String inmatadSnabb = txtSnabb.getText().trim();
        String snabbDBVarde = "0"; 
        if (inmatadSnabb.equalsIgnoreCase("Ja") || inmatadSnabb.equals("1")) {
            snabbDBVarde = "1";
        }
        // --- SLUT PÅ NYA KODEN ---

        // Bygg SQL-frågan med de nya värdena från fälten
        String sql = "UPDATE Ordrar SET "
                   + "FraktAdress = '" + txtAdress.getText() + "', "
                   + "Status = '" + txtStatus.getText() + "', "
                   + "OrderDatum = '" + txtDatum.getText() + "', "
                   + "ArSnabborder = " + snabbDBVarde + ", " // <-- Här används variabeln vi nyss skapade
                   + "TotalPrisInclMoms = " + txtPris.getText() + " "
                   + "WHERE OrderID = " + orderID;
        
        idb.update(sql);
            
            // Uppdatera och bekräfta
            fyllOrderTabell(); 
            JOptionPane.showMessageDialog(this, "Order " + orderID + " har uppdaterats!");
        }
        
    } catch (InfException ex) {
        JOptionPane.showMessageDialog(this, "Databastillgång misslyckades: " + ex.getMessage());
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Ett fel uppstod: " + ex.getMessage());
    }
    }//GEN-LAST:event_btnRedigeraOrderActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        //java.awt.EventQueue.invokeLater(() -> new AktuellaOrdrar().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnRedigeraOrder;
    private javax.swing.JButton btnTillbaka;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jtAktuellaOrdrar;
    private javax.swing.JTextArea txtOrderDetaljer;
    // End of variables declaration//GEN-END:variables
}

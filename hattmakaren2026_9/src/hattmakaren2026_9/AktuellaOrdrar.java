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
        btnMakuleraOrder.setEnabled(false);
        
        
        cmbStatus.removeAllItems(); 
        cmbStatus.addItem("Registrerad");
        cmbStatus.addItem("Tillverkning");
        cmbStatus.addItem("Under tillverkning");
        cmbStatus.addItem("Klar");
        cmbStatus.addItem("Skickad");

        cmbSnabborder.removeAllItems();
        cmbSnabborder.addItem("Nej");
        cmbSnabborder.addItem("Ja");
        
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        fyllOrderTabell();
        
        jtAktuellaOrdrar.setDefaultEditor(Object.class, null);
        
        jtAktuellaOrdrar.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            if (evt.getClickCount() == 2) {
                visaOrderInnehall();
            }
             if (jtAktuellaOrdrar.getSelectedRow() != -1) {
                    btnMakuleraOrder.setEnabled(true);
                }

        }
    });
    }
    
    private void fyllOrderTabell(){
        try {
            String sql = "SELECT * FROM Ordrar WHERE Status != 'Klar' and Status != 'Makulerad'";

            
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
            // Hämta OrderID
            String orderID = jtAktuellaOrdrar.getValueAt(valdRad, 0).toString();

            // --- FYLL DE FASTA FÄLTEN FÖR REDIGERING ---
            txtAdress.setText(String.valueOf(jtAktuellaOrdrar.getValueAt(valdRad, 5)));
            txtTotalPris.setText(String.valueOf(jtAktuellaOrdrar.getValueAt(valdRad, 6)));

            // Fyll rullgardinslistorna (ComboBox)
            cmbStatus.setSelectedItem(String.valueOf(jtAktuellaOrdrar.getValueAt(valdRad, 3)));
            cmbSnabborder.setSelectedItem(String.valueOf(jtAktuellaOrdrar.getValueAt(valdRad, 4)));

            // Fyll Datum-väljaren
            try {
                String datumStr = String.valueOf(jtAktuellaOrdrar.getValueAt(valdRad, 2));
                // JDateChooser vill ha ett riktigt Date-objekt, inte en sträng
                java.util.Date date = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(datumStr);
                jDOrderdatum.setDate(date); 
            } catch (Exception e) {
                System.out.println("Kunde inte tolka datumet.");
            }
            // -----------------------------------------------

            // --- Koden nedan (för att fylla textrutan till höger) är oförändrad ---
            String sqlRader = "SELECT H.ModellNamn, O.Farg, O.Tyg, O.Storlek, O.Antal "
                    + "FROM Orderrader O " + "JOIN Hattmodeller H ON O.ModellID = H.ModellID "
                    + "WHERE O.OrderID = " + orderID;
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

        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtAktuellaOrdrar = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOrderDetaljer = new javax.swing.JTextArea();
        txtTotalPris = new javax.swing.JTextField();
        jDOrderdatum = new com.toedter.calendar.JDateChooser();
        cmbStatus = new javax.swing.JComboBox<>();
        cmbSnabborder = new javax.swing.JComboBox<>();
        txtAdress = new javax.swing.JTextField();
        btnSpara = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        btnTillbaka = new javax.swing.JButton();
        btnMakuleraOrder = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

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

        cmbStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbSnabborder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        txtAdress.addActionListener(this::txtAdressActionPerformed);

        btnSpara.setText("Spara");
        btnSpara.addActionListener(this::btnSparaActionPerformed);

        jLabel1.setText("Datum:");

        jLabel2.setText("Status:");

        jLabel3.setText("Är snabborder:");

        jLabel4.setText("Fraktadress:");

        jLabel5.setText("Total pris ink moms:");

        btnTillbaka.setText("Tillbaka");
        btnTillbaka.addActionListener(this::btnTillbakaActionPerformed);

        btnMakuleraOrder.setText("Makulera order");
        btnMakuleraOrder.addActionListener(this::btnMakuleraOrderActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 819, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addGap(71, 71, 71)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDOrderdatum, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbSnabborder, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtAdress, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTotalPris, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 436, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnSpara)
                        .addGap(47, 47, 47)
                        .addComponent(btnMakuleraOrder))
                    .addComponent(btnTillbaka))
                .addGap(0, 1656, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDOrderdatum, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cmbStatus, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addGap(18, 18, 18)
                                        .addComponent(cmbSnabborder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(18, 18, 18)
                                .addComponent(txtAdress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTotalPris, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 467, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSpara)
                    .addComponent(btnMakuleraOrder))
                .addGap(152, 152, 152)
                .addComponent(btnTillbaka)
                .addGap(341, 341, 341))
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

    private void btnSparaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSparaActionPerformed
                                      
        int radIndex = jtAktuellaOrdrar.getSelectedRow();
    
        if (radIndex == -1) {
            JOptionPane.showMessageDialog(this, "Vänligen välj en order i tabellen att redigera!");
            return;
        }

        try {
            // Hämta OrderID från den valda raden
            String orderID = String.valueOf(jtAktuellaOrdrar.getValueAt(radIndex, 0));
            
            // 1. Läs av värden från dina fasta komponenter
            String inmatadAdress = txtAdress.getText();
            String inmatatPris = txtTotalPris.getText();
            String inmatadStatus = cmbStatus.getSelectedItem().toString();
            String inmatadSnabb = cmbSnabborder.getSelectedItem().toString();

            // Översätt texten "Ja"/"Nej" tillbaka till databasens "1" eller "0"
            String snabbDBVarde = "0"; 
            if (inmatadSnabb.equalsIgnoreCase("Ja")) {
                snabbDBVarde = "1";
            }
            
            // Läs av och formatera datumet från JDateChooser
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String inmatatDatum = sdf.format(jDOrderdatum.getDate());

            // 2. Skicka värdena till databasen
            String sql = "UPDATE Ordrar SET "
                       + "FraktAdress = '" + inmatadAdress + "', "
                       + "Status = '" + inmatadStatus + "', "
                       + "OrderDatum = '" + inmatatDatum + "', "
                       + "ArSnabborder = " + snabbDBVarde + ", " 
                       + "TotalPrisInclMoms = " + inmatatPris + " "
                       + "WHERE OrderID = " + orderID;
            
            idb.update(sql);
            
            // 3. Uppdatera tabellen i bakgrunden och ge feedback
            fyllOrderTabell(); 
            JOptionPane.showMessageDialog(this, "Order " + orderID + " har uppdaterats!");
            
        } catch (InfException ex) {
            JOptionPane.showMessageDialog(this, "Databastillgång misslyckades: " + ex.getMessage());
        } catch (NullPointerException npe) {
            JOptionPane.showMessageDialog(this, "Vänligen fyll i ett korrekt datum.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ett fel uppstod: " + ex.getMessage());
        }      // TODO add your handling code here:
    }//GEN-LAST:event_btnSparaActionPerformed

    private void txtAdressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAdressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtAdressActionPerformed

    private void btnMakuleraOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMakuleraOrderActionPerformed
         int valdRad = jtAktuellaOrdrar.getSelectedRow();

        String orderID = jtAktuellaOrdrar.getValueAt(valdRad, 0).toString();
        
        int svar = JOptionPane.showConfirmDialog(this,
                "Är du säker på att du vill makulera order #" + orderID + "?",
                "Bekräfta makulering",
                JOptionPane.YES_NO_OPTION);

        if (svar == JOptionPane.YES_OPTION) {
            try {
                String sql = "UPDATE Ordrar SET Status = 'Makulerad' WHERE OrderID = " + orderID;
                idb.update(sql);

                fyllOrderTabell();

            } catch (InfException ex) {
                JOptionPane.showMessageDialog(this, "Ett fel uppstod vid kontakt med databasen: " + ex.getMessage());
            }
        }

    }//GEN-LAST:event_btnMakuleraOrderActionPerformed

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
    private javax.swing.JButton btnMakuleraOrder;
    private javax.swing.JButton btnSpara;
    private javax.swing.JButton btnTillbaka;
    private javax.swing.JComboBox<String> cmbSnabborder;
    private javax.swing.JComboBox<String> cmbStatus;
    private com.toedter.calendar.JDateChooser jDOrderdatum;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jtAktuellaOrdrar;
    private javax.swing.JTextField txtAdress;
    private javax.swing.JTextArea txtOrderDetaljer;
    private javax.swing.JTextField txtTotalPris;
    // End of variables declaration//GEN-END:variables
}

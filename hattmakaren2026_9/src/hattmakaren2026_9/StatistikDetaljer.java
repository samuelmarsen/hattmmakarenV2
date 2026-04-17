/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hattmakaren2026_9;

/**
 *
 * @author ziggy
 */

/** ta in datum från orderar och antal och pris från orderrader   **/
import oru.inf.InfDB;
import oru.inf.InfException;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class StatistikDetaljer extends javax.swing.JFrame {
    private InfDB idb;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(StatistikDetaljer.class.getName());

    /**
     * Creates new form StatistikDetaljer
     */
    public StatistikDetaljer(InfDB idb) {
        initComponents();
        this.idb = idb;
        hamtaIntaktPerAr();
        hamtaIntaktPerKvartal();
        hamtaIntaktPerManad();
        statistikKund();
        modellStatistik();
        
    }
    
    public void hamtaIntaktPerAr() {

        DefaultTableModel model = (DefaultTableModel) TBar.getModel();
        model.setRowCount(0);

        try {

            String sql =
                "SELECT YEAR(o.OrderDatum) AS Ar, " +
                "SUM(orad.RadPrisExklMoms) AS Intakt " +
                "FROM Ordrar o " +
                "JOIN Orderrader orad ON o.OrderID = orad.OrderID " +
                "GROUP BY YEAR(o.OrderDatum) " +
                "ORDER BY Ar";

            ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);

            for (HashMap<String, String> rad : resultat) {

                Object[] row = {
                    rad.get("Ar"),
                    rad.get("Intakt")
                };

                model.addRow(row);
            }

        } catch (InfException e) {

            JOptionPane.showMessageDialog(this,
                "Fel vid hämtning av årsintäkt:\n" + e.getMessage());

            e.printStackTrace();
        }
    }
    
        public void hamtaIntaktPerKvartal() {

        DefaultTableModel model = (DefaultTableModel) TBkvartal.getModel();
        model.setRowCount(0);

        try {

            String sql =
                "SELECT YEAR(o.OrderDatum) AS Ar, " +
                "QUARTER(o.OrderDatum) AS Kvartal, " +
                "SUM(orad.RadPrisExklMoms) AS Intakt " +
                "FROM Ordrar o " +
                "JOIN Orderrader orad ON o.OrderID = orad.OrderID " +
                "GROUP BY YEAR(o.OrderDatum), QUARTER(o.OrderDatum) " +
                "ORDER BY Ar, Kvartal";

            ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);

            for (HashMap<String, String> rad : resultat) {

                String period = rad.get("Ar") + "-Q" + rad.get("Kvartal");

                Object[] row = {
                    period,
                    rad.get("Intakt")
                };

                model.addRow(row);
            }

        } catch (InfException e) {

            JOptionPane.showMessageDialog(this,
                "Fel vid hämtning av kvartalsintäkt:\n" + e.getMessage());

            e.printStackTrace();
        }
    }
        
    public void hamtaIntaktPerManad() {

        DefaultTableModel model = (DefaultTableModel) TBmanad.getModel();
        model.setRowCount(0);

        try {

            String sql =
                "SELECT YEAR(o.OrderDatum) AS Ar, " +
                "MONTH(o.OrderDatum) AS Manad, " +
                "SUM(orad.RadPrisExklMoms) AS Intakt " +
                "FROM Ordrar o " +
                "JOIN Orderrader orad ON o.OrderID = orad.OrderID " +
                "GROUP BY YEAR(o.OrderDatum), MONTH(o.OrderDatum) " +
                "ORDER BY Ar, Manad";

            ArrayList<HashMap<String, String>> resultat = idb.fetchRows(sql);

            for (HashMap<String, String> rad : resultat) {

                String period = rad.get("Ar") + "-" + rad.get("Manad");

                Object[] row = {
                    period,
                    rad.get("Intakt")
                };

                model.addRow(row);
            }

        } catch (InfException e) {

            JOptionPane.showMessageDialog(this,
                "Fel vid hämtning av månadsintäkt:\n" + e.getMessage());

            e.printStackTrace();
        }
    }
    
    private void statistikKund() {
        try {
            String sql = "SELECT Kunder.Epost, " + 
                         "SUM(IFNULL(Orderrader.Antal, 0)) AS AntalHattar, " +
                         "SUM(IFNULL(Orderrader.Antal * Orderrader.RadPrisExklMoms, 0)) AS SummaSpenderat " + 
                         "FROM Kunder " +
                         "LEFT JOIN Ordrar ON Kunder.KundID = Ordrar.KundID " + 
                         "LEFT JOIN Orderrader ON Ordrar.OrderID = Orderrader.OrderID " + 
                         "GROUP BY Kunder.Epost"; 

            ArrayList<HashMap<String, String>> rader = idb.fetchRows(sql);
            
            DefaultTableModel model = (DefaultTableModel) TBkund.getModel();
            model.setRowCount(0);
            
            if (rader != null) {
                for (HashMap<String, String> rad : rader) {
                    String epost = rad.get("Epost");
                    String antal = rad.get("AntalHattar");
                    String pengar = rad.get("SummaSpenderat");
                    
                    if (antal == null) antal = "0";
                    if (pengar == null) pengar = "0";
                    
                    model.addRow(new Object[]{
                        epost,
                        antal,
                        pengar + " kr"
                    });
                }
            }

        } catch (InfException ex) {
            JOptionPane.showMessageDialog(null, "Fel vid hämntning av kundstatistik: " + ex.getMessage());
        }
    }
    
    private void modellStatistik() {
    
    try { 
        
        String sql = "SELECT Hattmodeller.ModellNamn, " +
             "SUM(IFNULL(Orderrader.Antal, 0)) AS TotaltAntal, " +
             "SUM(IFNULL(Orderrader.Antal * Orderrader.RadPrisExklMoms, 0)) AS TotalSumma " +
             "FROM Hattmodeller " +
             "LEFT JOIN Orderrader ON Hattmodeller.ModellID = Orderrader.ModellID " +
             "GROUP BY Hattmodeller.ModellID, Hattmodeller.ModellNamn";

        
        ArrayList<HashMap<String, String>> rader = idb.fetchRows(sql);
        
        DefaultTableModel model = (DefaultTableModel) TBmodell.getModel();
        model.setRowCount(0);
        
        if (rader != null) {
    for (HashMap<String, String> rad : rader) {
        String namn = rad.get("ModellNamn");
        String antal = rad.get("TotaltAntal");
        String summa = rad.get("TotalSumma"); // 

        if (summa == null) {
            summa = "0";
        }
        if (antal == null) {
            antal = "0";
        }

        model.addRow(new Object[]{
            namn,
            antal,
            summa + " kr"
        });
    }
}
    } catch (InfException ex) {
        JOptionPane.showMessageDialog(null, "Fel vid hämtning av statistik: " + ex.getMessage());
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

        jButton1 = new javax.swing.JButton();
        Main = new javax.swing.JTabbedPane();
        JPar = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TBar = new javax.swing.JTable();
        JPkvartal = new javax.swing.JPanel();
        Scrollpane = new javax.swing.JScrollPane();
        TBkvartal = new javax.swing.JTable();
        JPmanad = new javax.swing.JPanel();
        Scrollpane2 = new javax.swing.JScrollPane();
        TBmanad = new javax.swing.JTable();
        JPmodell = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TBmodell = new javax.swing.JTable();
        JPkund = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TBkund = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("Tillbaka");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        TBar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "År", "Intäkt"
            }
        ));
        jScrollPane1.setViewportView(TBar);

        javax.swing.GroupLayout JParLayout = new javax.swing.GroupLayout(JPar);
        JPar.setLayout(JParLayout);
        JParLayout.setHorizontalGroup(
            JParLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JParLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addContainerGap())
        );
        JParLayout.setVerticalGroup(
            JParLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JParLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                .addContainerGap())
        );

        Main.addTab("  År  ", JPar);

        TBkvartal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Period", "Intäkt"
            }
        ));
        Scrollpane.setViewportView(TBkvartal);

        javax.swing.GroupLayout JPkvartalLayout = new javax.swing.GroupLayout(JPkvartal);
        JPkvartal.setLayout(JPkvartalLayout);
        JPkvartalLayout.setHorizontalGroup(
            JPkvartalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPkvartalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addContainerGap())
        );
        JPkvartalLayout.setVerticalGroup(
            JPkvartalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPkvartalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Scrollpane, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                .addContainerGap())
        );

        Main.addTab("Kvartal", JPkvartal);

        TBmanad.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Datum", "Intäkt"
            }
        ));
        Scrollpane2.setViewportView(TBmanad);

        javax.swing.GroupLayout JPmanadLayout = new javax.swing.GroupLayout(JPmanad);
        JPmanad.setLayout(JPmanadLayout);
        JPmanadLayout.setHorizontalGroup(
            JPmanadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPmanadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Scrollpane2, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addContainerGap())
        );
        JPmanadLayout.setVerticalGroup(
            JPmanadLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPmanadLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(Scrollpane2, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                .addContainerGap())
        );

        Main.addTab("Månad", JPmanad);

        TBmodell.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Hattmodell", "Antal sålda", "Intäkt"
            }
        ));
        jScrollPane2.setViewportView(TBmodell);

        javax.swing.GroupLayout JPmodellLayout = new javax.swing.GroupLayout(JPmodell);
        JPmodell.setLayout(JPmodellLayout);
        JPmodellLayout.setHorizontalGroup(
            JPmodellLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPmodellLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addContainerGap())
        );
        JPmodellLayout.setVerticalGroup(
            JPmodellLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPmodellLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                .addContainerGap())
        );

        Main.addTab("Modell", JPmodell);

        TBkund.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Epost", "Antal hattar", "Spendering "
            }
        ));
        jScrollPane3.setViewportView(TBkund);

        javax.swing.GroupLayout JPkundLayout = new javax.swing.GroupLayout(JPkund);
        JPkund.setLayout(JPkundLayout);
        JPkundLayout.setHorizontalGroup(
            JPkundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPkundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 773, Short.MAX_VALUE)
                .addContainerGap())
        );
        JPkundLayout.setVerticalGroup(
            JPkundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JPkundLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 494, Short.MAX_VALUE)
                .addContainerGap())
        );

        Main.addTab("Kund", JPkund);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Main)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Main)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        this.dispose();        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JPar;
    private javax.swing.JPanel JPkund;
    private javax.swing.JPanel JPkvartal;
    private javax.swing.JPanel JPmanad;
    private javax.swing.JPanel JPmodell;
    private javax.swing.JTabbedPane Main;
    private javax.swing.JScrollPane Scrollpane;
    private javax.swing.JScrollPane Scrollpane2;
    private javax.swing.JTable TBar;
    private javax.swing.JTable TBkund;
    private javax.swing.JTable TBkvartal;
    private javax.swing.JTable TBmanad;
    private javax.swing.JTable TBmodell;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    // End of variables declaration//GEN-END:variables
}

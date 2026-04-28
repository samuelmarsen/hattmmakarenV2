
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hattmakaren2026_9;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import oru.inf.InfDB;
import oru.inf.InfException;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Creates new form SkapaKundorder
 */
public class SkapaKundorder extends javax.swing.JFrame {

    private InfDB idb;
    private String InloggadEmail;

    private double totaltPris = 0.0;
    private double styckPrisHatt = 0.0;
    private double extraKostnadMaterial = 0.0;
    private String valdBildSokvag = "";

    public SkapaKundorder(InfDB idb, String InloggadEmail) throws InfException {
        initComponents();
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        this.idb = idb;
        this.InloggadEmail = InloggadEmail;

        LocalDate dagensDatum = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        txtInloggadEmail.setText(InloggadEmail);
        txtInloggadEmail.setEditable(false);
        txtDatum.setText(dagensDatum.format(formatter));
        txtDatum.setEditable(false);

        fyllRulllistaMedKunder();
        fyllRullistaMedHattar();
        fyllAlternativ();
        fyllRullistaMedMaterial();

        jTable1.getColumnModel().getColumn(1).setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                java.awt.Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (value != null && value.toString().startsWith("#")) {
                    try {
                        java.awt.Color farg = java.awt.Color.decode(value.toString());
                        c.setBackground(farg);

                        setText("");

                        setToolTipText(value.toString());
                    } catch (Exception e) {

                        c.setBackground(table.getBackground());
                    }
                } else {

                    c.setBackground(table.getBackground());
                }

                if (isSelected) {
                    setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.WHITE, 2));
                } else {
                    setBorder(null);
                }

                return c;
            }
        });

    }

    private void fyllRulllistaMedKunder() {
        try {
            cmbValjKund.removeAllItems();
            cmbValjKund.addItem("Välj kund");
            ArrayList<String> namnLista = idb.fetchColumn("select namn from kunder");

            if (namnLista != null) {
                for (String namn : namnLista) {
                    cmbValjKund.addItem(namn);
                }
            }
        } catch (InfException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void fyllRullistaMedHattar() {
        try {
            cmbHatt.removeAllItems();
            ArrayList<String> namnLista = idb.fetchColumn("select ModellNamn from Hattmodeller");

            if (namnLista != null) {
                for (String namn : namnLista) {
                    cmbHatt.addItem(namn);
                }
            }
        } catch (InfException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }

    private void fyllRullistaMedMaterial() {
        try {
            cmbDekoration.removeAllItems();

            String fraga = "SELECT Namn FROM Material WHERE Kategori = 'Dekoration'";

            ArrayList<String> material = idb.fetchColumn(fraga);

            if (material != null) {
                for (String namn : material) {
                    cmbDekoration.addItem(namn);
                }
            }
        } catch (InfException e) {
            System.out.println("Fel vid hämtning av dekorationer: " + e.getMessage());
        }
    }

    private void fyllAlternativ() {
        //cmbFarg.addItem("Svart");
        //cmbFarg.addItem("Vit");
        //cmbFarg.addItem("Blå");
        //cmbFarg.addItem("Röd");
        //cmbFarg.addItem("Grön");
        //cmbFarg.addItem("Gul");
        //cmbFarg.addItem("Brun");
        //cmbFarg.addItem("Natur");

        cmbTyg.addItem("Ullfilt");
        cmbTyg.addItem("Kaninfilt");
        cmbTyg.addItem("Läder");
        cmbTyg.addItem("Linne");
        cmbTyg.addItem("Tweed");
        cmbTyg.addItem("Toquillastrå");

        cmbStorlek.addItem("S");
        cmbStorlek.addItem("M");
        cmbStorlek.addItem("L");
        cmbStorlek.addItem("XL");

    }

    private void uppdateraTotalPris() {
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        totaltPris = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            try {
                String hattModell = model.getValueAt(i, 0).toString();
                int antal = Integer.parseInt(model.getValueAt(i, 4).toString());
                String snabborder = model.getValueAt(i, 5).toString();
                String dekorationer = model.getValueAt(i, 6).toString();

                String prisStr = idb.fetchSingle("SELECT PrisExklMoms FROM Hattmodeller WHERE ModellNamn = '" + hattModell + "'");
                double basPris = Double.parseDouble(prisStr);

                double radPris = basPris;

                if (dekorationer.contains("Egen text:")) {
                    radPris += 150.0;
                }

                if (dekorationer.contains("Arbetstid:")) {

                    String tidDel = dekorationer.substring(dekorationer.indexOf("Arbetstid:") + 11);
                    double timmar = Double.parseDouble(tidDel.substring(0, tidDel.indexOf("h")));
                    radPris += (timmar * 50.0);
                }

                radPris *= antal;

                if (snabborder.equals("Ja")) {
                    radPris *= 1.2;
                }

                totaltPris += radPris;

            } catch (Exception e) {
                System.out.println("Kunde inte räkna om rad " + i + ": " + e.getMessage());
            }
        }

        txtPrisExklMoms.setText(String.format("%.2f", totaltPris));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        cmbValjKund = new javax.swing.JComboBox();
        btnPaborjaOrder = new javax.swing.JButton();
        cmbHatt = new javax.swing.JComboBox<>();
        lblHattmodell = new javax.swing.JLabel();
        txtKundId = new javax.swing.JTextField();
        lblKundIdForOrder = new javax.swing.JLabel();
        txtAntalHattar = new javax.swing.JTextField();
        btnLaggTillIOrder = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        cmbTyg = new javax.swing.JComboBox<>();
        cmbStorlek = new javax.swing.JComboBox<>();
        lblVäljAntal = new javax.swing.JLabel();
        txtPrisExklMoms = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        chkSnabborder = new javax.swing.JCheckBox();
        btnTillbaka = new javax.swing.JButton();
        lblBildStatus = new javax.swing.JLabel();
        btnBifogaReferensBild = new javax.swing.JButton();
        txtInloggadEmail = new javax.swing.JTextField();
        lblInloggadAnstalld = new javax.swing.JLabel();
        btnValjFarg = new javax.swing.JButton();
        JpVisaFarg = new javax.swing.JPanel();
        btnTaBortOrderrad = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        lblSpecialorder = new javax.swing.JLabel();
        lblEgenText = new javax.swing.JLabel();
        txtEgenHattText = new javax.swing.JTextField();
        lblUppskattadTid = new javax.swing.JLabel();
        txtUppskattadTid = new javax.swing.JTextField();
        lblDekoration = new javax.swing.JLabel();
        cmbDekoration = new javax.swing.JComboBox<>();
        lblAntalDekoration = new javax.swing.JLabel();
        txtDekorationAntal = new javax.swing.JTextField();
        btnAdderaDekoration = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtAreaSpecial = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        lblDatum = new javax.swing.JLabel();
        txtDatum = new javax.swing.JTextField();
        lblFraktadress = new javax.swing.JLabel();
        txtFraktadress = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Skapa en ny kundorder");

        cmbValjKund.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Kurt Lupton", "Mikael Maskerad", "Lotta Larsson", "Sofia Sömmerska", "Erik Export" }));
        cmbValjKund.addActionListener(this::cmbValjKundActionPerformed);

        btnPaborjaOrder.setText("Registrera Order");
        btnPaborjaOrder.addActionListener(this::btnPaborjaOrderActionPerformed);

        cmbHatt.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbHatt.addActionListener(this::cmbHattActionPerformed);

        lblHattmodell.setText("Lagerförda hattmodeller");

        txtKundId.setEditable(false);
        txtKundId.addActionListener(this::txtKundIdActionPerformed);

        lblKundIdForOrder.setText("KundId för ordern");

        txtAntalHattar.addActionListener(this::txtAntalHattarActionPerformed);

        btnLaggTillIOrder.setText("Lägg till i order");
        btnLaggTillIOrder.addActionListener(this::btnLaggTillIOrderActionPerformed);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hattmodell", "Färg", "Tyg", "Storlek", "Antal", "Snabborder", "Dekoration"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel5.setText("Hattar i order");

        cmbTyg.addActionListener(this::cmbTygActionPerformed);

        lblVäljAntal.setText("Välj antal");

        txtPrisExklMoms.setEditable(false);
        txtPrisExklMoms.addActionListener(this::txtPrisExklMomsActionPerformed);

        jLabel2.setText("Pris exkl moms:");

        chkSnabborder.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        chkSnabborder.setText("Snabborder");
        chkSnabborder.addActionListener(this::chkSnabborderActionPerformed);

        btnTillbaka.setText("Tillbaka");
        btnTillbaka.addActionListener(this::btnTillbakaActionPerformed);

        btnBifogaReferensBild.setText("Bifoga Referensbild ");
        btnBifogaReferensBild.addActionListener(this::btnBifogaReferensBildActionPerformed);

        txtInloggadEmail.setEditable(false);
        txtInloggadEmail.addActionListener(this::txtInloggadEmailActionPerformed);

        lblInloggadAnstalld.setText("Inloggad anställd");

        btnValjFarg.setText("Välj färg");
        btnValjFarg.addActionListener(this::btnValjFargActionPerformed);

        JpVisaFarg.setBackground(new java.awt.Color(255, 255, 255));
        JpVisaFarg.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout JpVisaFargLayout = new javax.swing.GroupLayout(JpVisaFarg);
        JpVisaFarg.setLayout(JpVisaFargLayout);
        JpVisaFargLayout.setHorizontalGroup(
            JpVisaFargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );
        JpVisaFargLayout.setVerticalGroup(
            JpVisaFargLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btnTaBortOrderrad.setText("Ta bort orderrad");
        btnTaBortOrderrad.addActionListener(this::btnTaBortOrderradActionPerformed);

        jPanel1.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));

        lblSpecialorder.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblSpecialorder.setText("Specialorder");

        lblEgenText.setText("Egen text på hatt");

        lblUppskattadTid.setText("Uppskattad tid(h)");

        txtUppskattadTid.setColumns(10);
        txtUppskattadTid.addActionListener(this::txtUppskattadTidActionPerformed);

        lblDekoration.setText("Dekoration");

        cmbDekoration.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblAntalDekoration.setText("Antal");

        btnAdderaDekoration.setText("Lägg till dekoration");
        btnAdderaDekoration.addActionListener(this::btnAdderaDekorationActionPerformed);

        txtAreaSpecial.setEditable(false);
        txtAreaSpecial.setColumns(20);
        txtAreaSpecial.setRows(5);
        jScrollPane2.setViewportView(txtAreaSpecial);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(lblSpecialorder, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblEgenText)
                        .addGap(18, 18, 18)
                        .addComponent(txtEgenHattText, javax.swing.GroupLayout.PREFERRED_SIZE, 341, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblDekoration)
                                .addGap(18, 18, 18)
                                .addComponent(cmbDekoration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblAntalDekoration, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                .addComponent(lblUppskattadTid)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtUppskattadTid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtDekorationAntal, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnAdderaDekoration)
                        .addGap(27, 27, 27)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblSpecialorder)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblEgenText)
                            .addComponent(txtEgenHattText, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblUppskattadTid)
                            .addComponent(txtUppskattadTid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblDekoration)
                            .addComponent(cmbDekoration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblAntalDekoration)
                            .addComponent(txtDekorationAntal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnAdderaDekoration))))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        lblDatum.setText("Datum");

        txtDatum.setText("Datum");

        lblFraktadress.setText("Fraktadress");

        txtFraktadress.setText("Fraktadress");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFraktadress, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lblDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFraktadress, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDatum)
                    .addComponent(txtDatum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 21, Short.MAX_VALUE)
                        .addComponent(lblFraktadress)
                        .addGap(29, 29, 29))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtFraktadress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(chkSnabborder, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnPaborjaOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(txtInloggadEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(lblInloggadAnstalld, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                                .addGap(205, 205, 205)
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(434, 434, 434))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cmbValjKund, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(lblHattmodell, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(cmbHatt, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(btnValjFarg)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(JpVisaFarg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(cmbTyg, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(26, 26, 26)
                                        .addComponent(cmbStorlek, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(63, 63, 63)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(lblVäljAntal, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(txtAntalHattar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(91, 91, 91)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(btnTaBortOrderrad, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                                            .addComponent(btnLaggTillIOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtKundId, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblKundIdForOrder, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(25, 25, 25))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblBildStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnBifogaReferensBild, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtPrisExklMoms, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 1633, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnTillbaka, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(8, 8, 8)
                                        .addComponent(lblInloggadAnstalld)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtInloggadEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(cmbValjKund, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addComponent(lblKundIdForOrder)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtKundId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addGap(47, 47, 47)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(JpVisaFarg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cmbHatt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblHattmodell)
                                    .addComponent(cmbTyg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cmbStorlek, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(btnValjFarg)))
                            .addComponent(btnLaggTillIOrder)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lblVäljAntal)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtAntalHattar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTaBortOrderrad)
                .addGap(9, 9, 9)
                .addComponent(jLabel5)
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addComponent(btnBifogaReferensBild)
                        .addGap(18, 18, 18)
                        .addComponent(lblBildStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 241, Short.MAX_VALUE)))
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtPrisExklMoms, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnPaborjaOrder, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chkSnabborder))
                .addGap(3, 3, 3)
                .addComponent(btnTillbaka, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void btnPaborjaOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPaborjaOrderActionPerformed
        try {

            String kundID = txtKundId.getText();
            String fraktAdress = txtFraktadress.getText();
            String datum = txtDatum.getText();
            // Kontrollera att en kund är vald
            if (kundID.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Välj en kund först!");
                return;
            }

            String OttoID = null;
            try {
                OttoID = idb.fetchSingle("SELECT AnstalldID FROM Anstallda WHERE Email = '" + InloggadEmail.trim() + "'");
            } catch (InfException ex) {
                System.out.println("Kunde inte hitta Otto: " + ex.getMessage());
            }

            int arSnabborder = 0;
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Ordern är tom. Lägg till hattar först!");
                return;
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 5).toString().equals("Ja")) {
                    arSnabborder = 1;
                    break;
                }
            }

            String totalPrisStr = txtPrisExklMoms.getText().replace(",", ".");
            double totalPrisInklMoms = Double.parseDouble(totalPrisStr) * 1.25;

            String orderSql = "INSERT INTO Ordrar (KundID, OrderDatum, Status, ArSnabborder, FraktAdress, TotalPrisInclMoms, BildSokvag) "
                    + "VALUES (" + kundID + ", '" + datum + "', 'Registrerad', " + arSnabborder + ", '" + fraktAdress + "', " + totalPrisInklMoms + ", '" + valdBildSokvag + "')";
            idb.insert(orderSql);

            String nyttOrderID = idb.fetchSingle("SELECT MAX(OrderID) FROM Ordrar");

            // 5. REGISTRERA OTTO I ARBETSPASS (Viktigt: vi fyller i alla kolumner nu!)
            if (OttoID != null && nyttOrderID != null) {
                try {
                    // Vi lägger till Datum, 0 timmar och en aktivitetstext för att tabellen ska acceptera raden
                    String arbetspassSql = "INSERT INTO Arbetspass (AnstalldID, OrderID, Datum, Timmar, Aktivitet) "
                            + "VALUES (" + OttoID + ", " + nyttOrderID + ", '" + datum + "', 0, 'Order registrerad')";
                    idb.insert(arbetspassSql);
                    System.out.println("Otto kopplad till order #" + nyttOrderID);
                } catch (InfException ex) {
                    System.out.println("Kunde inte spara Arbetspass: " + ex.getMessage());
                }
            }

            for (int i = 0; i < model.getRowCount(); i++) {
                String hattNamn = model.getValueAt(i, 0).toString();
                String farg = model.getValueAt(i, 1).toString();
                String tyg = model.getValueAt(i, 2).toString();
                String storlek = model.getValueAt(i, 3).toString();
                int antalHattar = Integer.parseInt(model.getValueAt(i, 4).toString());

                String dekoration = model.getValueAt(i, 6).toString();

                String komplettAnpassning = "Färg: " + farg + ", Tyg: " + tyg + ", Storlek: " + storlek;
                if (!dekoration.isEmpty()) {
                    komplettAnpassning += " | EXTRA: " + dekoration;
                }

                String modellID = idb.fetchSingle("SELECT ModellID FROM Hattmodeller WHERE ModellNamn = '" + hattNamn + "'");

                String radSql = "INSERT INTO Orderrader (OrderID, ModellID, Antal, Anpassningstext, Farg, Tyg, Storlek) "
                        + "VALUES (" + nyttOrderID + ", " + modellID + ", " + antalHattar + ", '" + komplettAnpassning + "', '" + farg + "', '" + tyg + "', '" + storlek + "')";
                idb.insert(radSql);

                idb.update("UPDATE Material SET LagerSaldo = LagerSaldo - " + antalHattar + " WHERE Namn = '" + tyg + "'");
                
                if (dekoration != null && !dekoration.isEmpty() && !dekoration.equalsIgnoreCase("Ingen")) {
                    String rentDekorNamn = dekoration;
                    int antalDekorPerHatt = 1; 

                    if (dekoration.contains("x ")) {
                        try {
                            
                            String antalStr = dekoration.substring(0, dekoration.indexOf("x")).trim();
                            antalDekorPerHatt = Integer.parseInt(antalStr);
                            
                            rentDekorNamn = dekoration.substring(dekoration.indexOf(" ") + 1).trim();
                        } catch (Exception e) {
                            antalDekorPerHatt = 1;
                        }
                    }
                    
                    
                    int totalMinskningDekor = antalHattar * antalDekorPerHatt;
                    
                    idb.update("UPDATE Material SET LagerSaldo = LagerSaldo - " + totalMinskningDekor + " WHERE Namn = '" + rentDekorNamn + "'");
                }

            }

            JOptionPane.showMessageDialog(this, "Order #" + nyttOrderID + " har registrerats!");

            model.setRowCount(0);
            totaltPris = 0;
            txtPrisExklMoms.setText("0.00");

            valdBildSokvag = "";
            lblBildStatus.setIcon(null);
            lblBildStatus.setText("");
            lblBildStatus.repaint();

        } catch (InfException ex) {
            JOptionPane.showMessageDialog(this, "Databastillgång misslyckades: " + ex.getMessage());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Ett fel uppstod: " + ex.getMessage());
        }
    }//GEN-LAST:event_btnPaborjaOrderActionPerformed

    private void txtKundIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtKundIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtKundIdActionPerformed

    private void cmbHattActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbHattActionPerformed
        String valtNamn = (String) cmbHatt.getSelectedItem();

        if (valtNamn == null || valtNamn.isEmpty()) {
            return;
        }

        try {
            String fraga = "SELECT Farg, Tyg, Storlek, PrisExklMoms FROM Hattmodeller WHERE ModellNamn = '" + valtNamn + "'";
            java.util.HashMap<String, String> rad = idb.fetchRow(fraga);

            if (rad != null) {

                String f = rad.get("Farg");
                String t = rad.get("Tyg");
                String s = rad.get("Storlek");

                if ("Svart".equals(f)) {
                    JpVisaFarg.setBackground(Color.BLACK);
                }

                if ("Vit".equals(f)) {
                    JpVisaFarg.setBackground(Color.WHITE);
                }

                if ("Natur".equals(f)) {
                    JpVisaFarg.setBackground(Color.GREEN);
                }

                if ("Brun".equals(f)) {
                    java.awt.Color Brun = new java.awt.Color(139, 69, 19);

                    JpVisaFarg.setBackground(Brun);
                }

                if ("Blå".equals(f)) {
                    JpVisaFarg.setBackground(Color.BLUE);
                }

                if ("Grå".equals(f)) {
                    JpVisaFarg.setBackground(Color.GRAY);
                }

                String p = rad.get("PrisExklMoms");
                if (p != null) {
                    styckPrisHatt = Double.parseDouble(p);
                }

                javax.swing.SwingUtilities.invokeLater(() -> {

                    //7cmbFarg.setSelectedItem(f != null ? f : "Saknas");
                    cmbTyg.setSelectedItem(t != null ? t : "Saknas");

                    cmbStorlek.setSelectedItem(s != null ? s : "Saknas");

                    //cmbFarg.revalidate();
                    //cmbFarg.repaint();
                    cmbTyg.revalidate();
                    cmbTyg.repaint();
                    cmbStorlek.revalidate();
                    cmbStorlek.repaint();
                });

                System.out.println("Data insatt i boxarna: " + f + ", " + t + ", " + s);
            }
        } catch (InfException e) {
            System.out.println("Fel: " + e.getMessage());
        }

    }//GEN-LAST:event_cmbHattActionPerformed

    private void cmbValjKundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbValjKundActionPerformed
        String valtNamn = (String) cmbValjKund.getSelectedItem();
        if (valtNamn == null || valtNamn.isEmpty()) {
            return;
        }
        try {
            String fraga = "select KundID, Adress FROM Kunder WHERE Namn = '" + valtNamn + "'";
            java.util.HashMap<String, String> rad = idb.fetchRow(fraga);

            if (rad != null) {
                txtKundId.setText(rad.get("KundID"));
                txtFraktadress.setText(rad.get("Adress"));
            }

        } catch (InfException e) {
            System.out.println("Fel: " + e.getMessage());
        }

    }//GEN-LAST:event_cmbValjKundActionPerformed

    private void txtAntalHattarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtAntalHattarActionPerformed

    }//GEN-LAST:event_txtAntalHattarActionPerformed

    private void btnLaggTillIOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLaggTillIOrderActionPerformed
        String snabborderText = "Nej";
        if (chkSnabborder.isSelected()) {
            snabborderText = "Ja";
        }
        String antalStr = txtAntalHattar.getText().trim();
        int antal = antalStr.isEmpty() ? 1 : Integer.parseInt(antalStr);
        String hattModell = (String) cmbHatt.getSelectedItem();
        String tyg = (String) cmbTyg.getSelectedItem();
        //String farg = (String) cmbFarg.getSelectedItem();
        String storlek = (String) cmbStorlek.getSelectedItem();
        java.awt.Color aktuellFarg = JpVisaFarg.getBackground();
        String hexFarg = String.format("#%02x%02x%02x", aktuellFarg.getRed(), aktuellFarg.getGreen(), aktuellFarg.getBlue());
        System.out.println("Sparad färg: " + hexFarg);

        double timpris = 50.0;
        String tidStr = txtUppskattadTid.getText().trim().replace(",", ".");
        double antalTimmar = tidStr.isEmpty() ? 0.0 : Double.parseDouble(tidStr);
        double arbetskostnad = antalTimmar * timpris;

        double extraTextPris = 0.0;
        String egenText = txtEgenHattText.getText().trim();
        if (!egenText.isEmpty()) {
            extraTextPris = 150.0;
        }

        double radPris = (styckPrisHatt + extraKostnadMaterial + extraTextPris + arbetskostnad) * antal;

        if (chkSnabborder.isSelected()) {
            radPris *= 1.2;
        }
        totaltPris += radPris;

        txtPrisExklMoms.setText(String.format("%.2f", totaltPris));

        String dekorationer = txtAreaSpecial.getText().replace("\n", ", ").trim();

        if (!egenText.isEmpty()) {
            if (!dekorationer.isEmpty()) {
                dekorationer += ", ";
            }
            dekorationer += "Egen text: " + egenText;
        }

        if (antalTimmar > 0) {
            if (!dekorationer.isEmpty()) {
                dekorationer += ", ";
            }
            dekorationer += "Arbetstid: " + antalTimmar + "h";
        }

        if (dekorationer.endsWith(",")) {
            dekorationer = dekorationer.substring(0, dekorationer.length() - 1);
        }

        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) jTable1.getModel();

        model.addRow(new Object[]{hattModell, hexFarg, tyg, storlek, antal, snabborderText, dekorationer});
        //Farg efter tyg

        extraKostnadMaterial = 0.0;
        txtAreaSpecial.setText("");
        txtEgenHattText.setText("");
        txtUppskattadTid.setText("");


    }//GEN-LAST:event_btnLaggTillIOrderActionPerformed

    private void txtPrisExklMomsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrisExklMomsActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPrisExklMomsActionPerformed

    private void chkSnabborderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkSnabborderActionPerformed

    }//GEN-LAST:event_chkSnabborderActionPerformed

    private void btnTillbakaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTillbakaActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnTillbakaActionPerformed

    private void btnAdderaDekorationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAdderaDekorationActionPerformed
        try {
            String valtMat = cmbDekoration.getSelectedItem().toString();
            int antal = Integer.parseInt(txtDekorationAntal.getText().trim());

            // Hämta priset för materialet från databasen
            String prisStr = idb.fetchSingle("SELECT EnhetsPris FROM Material WHERE Namn = '" + valtMat + "'");
            double styckPris = Double.parseDouble(prisStr);

            // Addera till den temporära potten
            extraKostnadMaterial += (styckPris * antal);

            // Visa i loggen för Otto
            txtAreaSpecial.append(antal + "x " + valtMat + "\n");
            txtDekorationAntal.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Vänligen ange antal i siffror.");
        }        // TODO add your handling code here:
    }//GEN-LAST:event_btnAdderaDekorationActionPerformed

    private void txtUppskattadTidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUppskattadTidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtUppskattadTidActionPerformed

    private void btnBifogaReferensBildActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBifogaReferensBildActionPerformed
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        int resultat = fc.showOpenDialog(this);

        if (resultat == javax.swing.JFileChooser.APPROVE_OPTION) {
            java.io.File fil = fc.getSelectedFile();
            valdBildSokvag = fil.getAbsolutePath();

            // 1. Skapa en ImageIcon från filen
            javax.swing.ImageIcon originalIkon = new javax.swing.ImageIcon(valdBildSokvag);

            // 2. Skala om bilden så den passar i din label (t.ex. 150x150 pixlar)
            java.awt.Image bild = originalIkon.getImage();
            java.awt.Image skaladBild = bild.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);

            // 3. Skapa en ny ikon av den skalade bilden och sätt på labeln
            javax.swing.ImageIcon färdigIkon = new javax.swing.ImageIcon(skaladBild);
            lblBildStatus.setIcon(färdigIkon);

            // Valfritt: Ta bort texten om du bara vill se bilden
            lblBildStatus.setText("");
        }      // TODO add your handling code here:
    }//GEN-LAST:event_btnBifogaReferensBildActionPerformed

    private void txtInloggadEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtInloggadEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtInloggadEmailActionPerformed

    private void btnValjFargActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnValjFargActionPerformed
        Color valdFarg = javax.swing.JColorChooser.showDialog(this, "Välj färg på hatten", Color.WHITE);
        if (valdFarg != null) {
            JpVisaFarg.setBackground(valdFarg);
            String hex = String.format("#%02x%02x%02x", valdFarg.getRed(), valdFarg.getGreen(), valdFarg.getBlue());
            System.out.println("Sparad färg: " + hex);
        }
    }//GEN-LAST:event_btnValjFargActionPerformed

    private void cmbTygActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTygActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbTygActionPerformed

    private void btnTaBortOrderradActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaBortOrderradActionPerformed
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();

        // Kontrollera om en rad är markerad
        int markeradRad = jTable1.getSelectedRow();

        if (markeradRad != -1) {
            // Valfritt: Bekräftelsefråga
            int svar = JOptionPane.showConfirmDialog(this, "Vill du ta bort den valda hatten från ordern?", "Ta bort", JOptionPane.YES_NO_OPTION);

            if (svar == JOptionPane.YES_OPTION) {
                // Ta bort raden från modellen
                model.removeRow(markeradRad);

                // HÄR MÅSTE DU UPPDATERA PRISET
                uppdateraTotalPris();

                JOptionPane.showMessageDialog(this, "Hatten har tagits bort.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Markera först den hatt i listan som du vill ta bort.");
        }

    }//GEN-LAST:event_btnTaBortOrderradActionPerformed

    //public static void main(String args[]) {
    // java.awt.EventQueue.invokeLater(new Runnable() {S
    //  public void run() {
    // new SkapaKundorder().setVisible(true);
    //}
    //});
    //}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel JpVisaFarg;
    private javax.swing.JButton btnAdderaDekoration;
    private javax.swing.JButton btnBifogaReferensBild;
    private javax.swing.JButton btnLaggTillIOrder;
    private javax.swing.JButton btnPaborjaOrder;
    private javax.swing.JButton btnTaBortOrderrad;
    private javax.swing.JButton btnTillbaka;
    private javax.swing.JButton btnValjFarg;
    private javax.swing.JCheckBox chkSnabborder;
    private javax.swing.JComboBox<String> cmbDekoration;
    private javax.swing.JComboBox<String> cmbHatt;
    private javax.swing.JComboBox<String> cmbStorlek;
    private javax.swing.JComboBox<String> cmbTyg;
    private javax.swing.JComboBox cmbValjKund;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel lblAntalDekoration;
    private javax.swing.JLabel lblBildStatus;
    private javax.swing.JLabel lblDatum;
    private javax.swing.JLabel lblDekoration;
    private javax.swing.JLabel lblEgenText;
    private javax.swing.JLabel lblFraktadress;
    private javax.swing.JLabel lblHattmodell;
    private javax.swing.JLabel lblInloggadAnstalld;
    private javax.swing.JLabel lblKundIdForOrder;
    private javax.swing.JLabel lblSpecialorder;
    private javax.swing.JLabel lblUppskattadTid;
    private javax.swing.JLabel lblVäljAntal;
    private javax.swing.JTextField txtAntalHattar;
    private javax.swing.JTextArea txtAreaSpecial;
    private javax.swing.JTextField txtDatum;
    private javax.swing.JTextField txtDekorationAntal;
    private javax.swing.JTextField txtEgenHattText;
    private javax.swing.JTextField txtFraktadress;
    private javax.swing.JTextField txtInloggadEmail;
    private javax.swing.JTextField txtKundId;
    private javax.swing.JTextField txtPrisExklMoms;
    private javax.swing.JTextField txtUppskattadTid;
    // End of variables declaration//GEN-END:variables
}

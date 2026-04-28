/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package hattmakaren2026_9;

import oru.inf.InfDB;
import oru.inf.InfException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JTextField;
import com.toedter.calendar.JDateChooser;

/**
 *
 * @author rodaf
 */
public class SchemaVy extends javax.swing.JFrame {

    private InfDB idb;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(SchemaVy.class.getName());

    /**
     * Creates new form SchemaVy
     */
    public SchemaVy(InfDB idb) {
        initComponents();
        this.idb = idb;
        visaStartVecka();
        fyllPlaneraComboboxar();
        jDateChooser1.setDate(new java.util.Date());
        this.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        jtSchema.setDefaultRenderer(Object.class, new SchemaFargsattare());
    }

    private void visaStartVecka() {
        cbVecka.setSelectedIndex(0);
    }
    private ArrayList<String> passIDLista = new ArrayList<>();

    private void fyllPlaneraComboboxar() {
        try {
            cmbValjAnstalld.removeAllItems();
            cmbValjOrder.removeAllItems();

            ArrayList<String> anstallda = idb.fetchColumn("SELECT Namn FROM Anstallda");
            ArrayList<String> ordrar = idb.fetchColumn("SELECT OrderID FROM Ordrar");

            if (anstallda != null) {
                for (String namn : anstallda) {
                    cmbValjAnstalld.addItem(namn);
                }
            }

            cmbValjOrder.addItem("Inget (Allmänt arbete)");

            if (ordrar != null) {
                for (String orderID : ordrar) {
                    cmbValjOrder.addItem(orderID);
                }
            }

        } catch (InfException e) {
            JOptionPane.showMessageDialog(this, "Kunde inte ladda listor: " + e.getMessage());
        }
    }

    private void sparaPlaneratPass() {
        try {
            if (cmbValjAnstalld.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Välj en anställd!");
                return;
            }

            if (cmbValjOrder.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Välj en order!");
                return;
            }

            if (jDateChooser1.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Välj ett datum!");
                return;
            }

            if (Validering.arTom(txtAntalTimmar, "Timmar saknas!")
                    || Validering.arTom(txtBeskrivning, "Beskrivning saknas!")) {
                return;
            }

            if (!Validering.arDecimal(txtAntalTimmar)
                    || !Validering.arPositivtTal(txtAntalTimmar)) {
                return;
            }

            String valtNamn = cmbValjAnstalld.getSelectedItem().toString();

            String anstID = idb.fetchSingle(
                    "SELECT AnstalldID FROM Anstallda WHERE Namn = '" + valtNamn + "'"
            );

            String valtOrderID = cmbValjOrder.getSelectedItem().toString();
            String orderValue = valtOrderID.contains("Inget") ? "NULL" : valtOrderID;

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String datum = sdf.format(jDateChooser1.getDate());

            String aktivitet = txtBeskrivning.getText().replace("'", "''");

            String sql = "INSERT INTO Arbetspass (AnstalldID, OrderID, Datum, Timmar, Aktivitet) VALUES ("
                    + anstID + ", "
                    + orderValue + ", '"
                    + datum + "', "
                    + txtAntalTimmar.getText().replace(',', '.') + ", '"
                    + aktivitet + "')";

            idb.insert(sql);

            visaValdVecka();

            JOptionPane.showMessageDialog(this, "Passet har sparats!");

            txtAntalTimmar.setText("");
            txtBeskrivning.setText("");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte spara passet: " + e.getMessage());
        }
    }

    private void visaValdVecka() {
        String valdVecka = cbVecka.getSelectedItem().toString();

        String startDatum = "";
        String slutDatum = "";

        if (valdVecka.contains("Vecka 16")) {
            startDatum = "2026-04-13";
            slutDatum = "2026-04-17";
        } else if (valdVecka.contains("Vecka 17")) {
            startDatum = "2026-04-20";
            slutDatum = "2026-04-24";
        }

        fyllSchemaTabell(startDatum, slutDatum);
    }

    private void fyllPassTabellForValdPerson() {
        int valdRad = jtSchema.getSelectedRow();

        if (valdRad == -1) {
            return;
        }

        try {
            String anstalldNamn = jtSchema.getValueAt(valdRad, 0).toString();

            String anstalldID = idb.fetchSingle(
                    "SELECT AnstalldID FROM Anstallda WHERE Namn = '" + anstalldNamn + "'"
            );

            String sql = "SELECT PassID, Datum, Aktivitet, Timmar FROM Arbetspass "
                    + "WHERE AnstalldID = " + anstalldID + " "
                    + "ORDER BY Datum";

            ArrayList<HashMap<String, String>> passLista = idb.fetchRows(sql);

            DefaultTableModel model = (DefaultTableModel) jtPassLista.getModel();
            model.setRowCount(0);
            passIDLista.clear();

            if (passLista != null) {
                for (HashMap<String, String> pass : passLista) {
                    passIDLista.add(pass.get("PassID"));

                    model.addRow(new Object[]{
                        pass.get("Datum"),
                        pass.get("Aktivitet"),
                        pass.get("Timmar")
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte hämta pass: " + e.getMessage());
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jtSchema = new javax.swing.JTable();
        btnTillbakaKnapp = new javax.swing.JButton();
        cbVecka = new javax.swing.JComboBox<>();
        lblVisaSchema = new javax.swing.JLabel();
        txtAntalTimmar = new javax.swing.JTextField();
        txtBeskrivning = new javax.swing.JTextField();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        cmbValjOrder = new javax.swing.JComboBox<>();
        cmbValjAnstalld = new javax.swing.JComboBox<>();
        lblPlaneraPass = new javax.swing.JLabel();
        lblDatum = new javax.swing.JLabel();
        lblAntalTimmar = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lblVäljOrder = new javax.swing.JLabel();
        lblAnstalldForPass = new javax.swing.JLabel();
        btnSparaPass = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jtPassLista = new javax.swing.JTable();
        txtBeskrivningAktivitet = new javax.swing.JTextField();
        txtTimmar = new javax.swing.JTextField();
        jdDatumRedigera = new com.toedter.calendar.JDateChooser();
        lblBeskrivningRedigera = new javax.swing.JLabel();
        lblDatumRedigera = new javax.swing.JLabel();
        lblTimmarRedigera = new javax.swing.JLabel();
        btnTaBort = new javax.swing.JButton();
        btnSparaRedigera = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        lblRedigeraPass = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jtSchema.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Anställda", "Måndag", "Tisdag", "Onsdag", "Torsdag", "Fredag"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtSchema.setGridColor(new java.awt.Color(204, 204, 204));
        jtSchema.setIntercellSpacing(new java.awt.Dimension(1, 1));
        jtSchema.setRowHeight(60);
        jtSchema.setRowSelectionAllowed(false);
        jtSchema.setShowGrid(true);
        jtSchema.setSurrendersFocusOnKeystroke(true);
        jtSchema.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtSchemaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jtSchema);

        btnTillbakaKnapp.setText("Tillbaka");
        btnTillbakaKnapp.addActionListener(this::btnTillbakaKnappActionPerformed);

        cbVecka.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Vecka 16 (2026-04-13 - 2026-04-17) ", "Vecka 17 (2026-04-20 - 2026-04-24)" }));
        cbVecka.addActionListener(this::cbVeckaActionPerformed);

        lblVisaSchema.setText("Visa schema");

        txtBeskrivning.setText("Beskrivning...");

        cmbValjOrder.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmbValjAnstalld.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        lblPlaneraPass.setFont(new java.awt.Font("Helvetica Neue", 1, 18)); // NOI18N
        lblPlaneraPass.setText("Planera pass");

        lblDatum.setText("Datum");

        lblAntalTimmar.setText("Antal timmar");

        jLabel1.setText("Vad ska göras?");

        lblVäljOrder.setText("Välj order");

        lblAnstalldForPass.setText("Anställd");

        btnSparaPass.setText("Spara planerat pass");
        btnSparaPass.addActionListener(this::btnSparaPassActionPerformed);

        jtPassLista.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Datum", "Beskrivning", "Timmar"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jtPassLista.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jtPassListaMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jtPassLista);

        txtBeskrivningAktivitet.addActionListener(this::txtBeskrivningAktivitetActionPerformed);

        txtTimmar.addActionListener(this::txtTimmarActionPerformed);

        lblBeskrivningRedigera.setText("Beskrivning");

        lblDatumRedigera.setText("Datum");

        lblTimmarRedigera.setText("Timmar");

        btnTaBort.setText("Ta bort");
        btnTaBort.addActionListener(this::btnTaBortActionPerformed);

        btnSparaRedigera.setText("Spara");
        btnSparaRedigera.addActionListener(this::btnSparaRedigeraActionPerformed);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel2.setText("Dubbelklicka på en anställd för att se planerade pass");

        lblRedigeraPass.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblRedigeraPass.setText("Redigera pass");

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel3.setText("Planerade pass");

        jLabel4.setFont(new java.awt.Font("Segoe UI", 2, 12)); // NOI18N
        jLabel4.setText("Välj ett pass för att redigera ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblVäljOrder, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(lblAntalTimmar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(29, 29, 29))
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblDatum, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(63, 63, 63)))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(txtAntalTimmar)
                                        .addComponent(txtBeskrivning, javax.swing.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cmbValjOrder, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGap(665, 665, 665))))
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnSparaPass)
                            .addGap(459, 459, 459))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGap(122, 122, 122)
                            .addComponent(cmbValjAnstalld, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGap(665, 665, 665))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                    .addComponent(cbVecka, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(462, 462, 462)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(0, 0, Short.MAX_VALUE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(0, 0, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1051, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblAnstalldForPass, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnTillbakaKnapp, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblPlaneraPass, javax.swing.GroupLayout.PREFERRED_SIZE, 337, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblVisaSchema, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(732, 732, 732)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 387, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(btnTaBort)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSparaRedigera))
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblBeskrivningRedigera)
                                .addComponent(lblDatumRedigera)
                                .addComponent(lblTimmarRedigera, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(31, 31, 31)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(txtBeskrivningAktivitet)
                                .addComponent(jdDatumRedigera, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtTimmar, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(lblRedigeraPass, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(857, 857, 857))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblVisaSchema)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(cbVecka, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2))
                                .addGap(19, 19, 19)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 404, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addGap(10, 10, 10)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblPlaneraPass)
                            .addComponent(lblRedigeraPass))
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblAnstalldForPass)
                            .addComponent(cmbValjAnstalld, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cmbValjOrder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblVäljOrder))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDatum))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtAntalTimmar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblAntalTimmar)))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtBeskrivningAktivitet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblBeskrivningRedigera))
                                .addGap(30, 30, 30)
                                .addComponent(lblDatumRedigera))
                            .addComponent(jdDatumRedigera, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTimmar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTimmarRedigera))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnTaBort)
                            .addComponent(btnSparaRedigera))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBeskrivning, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addComponent(btnSparaPass)
                .addGap(38, 38, 38)
                .addComponent(btnTillbakaKnapp)
                .addContainerGap(273, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnTillbakaKnappActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTillbakaKnappActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnTillbakaKnappActionPerformed

    private void cbVeckaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbVeckaActionPerformed
        visaValdVecka();     // TODO add your handling code here:
    }//GEN-LAST:event_cbVeckaActionPerformed

    private void btnSparaPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSparaPassActionPerformed
        sparaPlaneratPass();        // TODO add your handling code here:
    }//GEN-LAST:event_btnSparaPassActionPerformed

    private void jtSchemaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtSchemaMouseClicked
        fyllPassTabellForValdPerson();  // TODO add your handling code here:
    }//GEN-LAST:event_jtSchemaMouseClicked

    private void txtBeskrivningAktivitetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBeskrivningAktivitetActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtBeskrivningAktivitetActionPerformed

    private void txtTimmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimmarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimmarActionPerformed

    private void btnSparaRedigeraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSparaRedigeraActionPerformed

        try {
            int rad = jtPassLista.getSelectedRow();

            if (rad == -1) {
                JOptionPane.showMessageDialog(this, "Välj ett pass i tabellen först!");
                return;
            }

            if (jdDatumRedigera.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Välj ett datum!");
                return;
            }

            if (Validering.arTom(txtBeskrivningAktivitet, "Beskrivning saknas!")
                    || Validering.arTom(txtTimmar, "Timmar saknas!")) {
                return;
            }

            if (!Validering.arDecimal(txtTimmar)
                    || !Validering.arPositivtTal(txtTimmar)) {
                return;
            }

            String passID = passIDLista.get(rad);

            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            String datum = sdf.format(jdDatumRedigera.getDate());

            String aktivitet = txtBeskrivningAktivitet.getText().replace("'", "''");
            String timmar = txtTimmar.getText().replace(',', '.');

            String sql = "UPDATE Arbetspass SET "
                    + "Aktivitet='" + aktivitet + "', "
                    + "Datum='" + datum + "', "
                    + "Timmar=" + timmar + " "
                    + "WHERE PassID=" + passID;

            idb.update(sql);

            visaValdVecka();
            fyllPassTabellForValdPerson();

            JOptionPane.showMessageDialog(this, "Passet har uppdaterats!");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Kunde inte uppdatera passet: " + e.getMessage());
        }    // TODO add your handling code here:
    }//GEN-LAST:event_btnSparaRedigeraActionPerformed

    private void jtPassListaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jtPassListaMouseClicked
        int rad = jtPassLista.getSelectedRow();

        if (rad == -1) {
            return;
        }

        txtBeskrivningAktivitet.setText(jtPassLista.getValueAt(rad, 1).toString());
        txtTimmar.setText(jtPassLista.getValueAt(rad, 2).toString());

        try {
            String datumStr = jtPassLista.getValueAt(rad, 0).toString();

            java.util.Date datum = new java.text.SimpleDateFormat("yyyy-MM-dd")
                    .parse(datumStr);

            jdDatumRedigera.setDate(datum);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Fel vid datum");
        }
        
    }//GEN-LAST:event_jtPassListaMouseClicked

    private void btnTaBortActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaBortActionPerformed
        try {
            
            int valdRad = jtPassLista.getSelectedRow();

            if (valdRad == -1) {
                JOptionPane.showMessageDialog(this, "Vänligen välj ett pass i listan som du vill ta bort.");
                return;
            }

            
            int svar = JOptionPane.showConfirmDialog(this,
                    "Är du säker på att du vill ta bort det markerade arbetspasset?",
                    "Bekräfta borttagning",
                    JOptionPane.YES_NO_OPTION);

            if (svar == JOptionPane.YES_OPTION) {
                
                String passID = passIDLista.get(valdRad);

                
                String sql = "DELETE FROM Arbetspass WHERE PassID = " + passID;
                idb.delete(sql);

   
                visaValdVecka(); 
                fyllPassTabellForValdPerson(); 

                
                txtBeskrivningAktivitet.setText("");
                txtTimmar.setText("");
                jdDatumRedigera.setDate(null);

            
            }
        } catch (InfException ex) {
            JOptionPane.showMessageDialog(this, "Ett fel uppstod i databasen: " + ex.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ett fel uppstod: " + e.getMessage());
        }
    }//GEN-LAST:event_btnTaBortActionPerformed

    private void fyllSchemaTabell(String startDatum, String slutDatum) {
        try {
            DefaultTableModel model = (DefaultTableModel) jtSchema.getModel();
            model.setRowCount(0);

            String sqlAnstallda = "SELECT AnstalldID, Namn FROM Anstallda";
            ArrayList<HashMap<String, String>> listaAnstallda = idb.fetchRows(sqlAnstallda);

            if (listaAnstallda != null) {
                for (HashMap<String, String> anstalld : listaAnstallda) {
                    String id = anstalld.get("AnstalldID");
                    Object[] radData = new Object[6];
                    radData[0] = anstalld.get("Namn");

                    String sqlPass = "SELECT Datum, Aktivitet, OrderID, Timmar FROM Arbetspass "
                            + "WHERE AnstalldID = " + id + " "
                            + "AND Datum BETWEEN '" + startDatum + "' AND '" + slutDatum + "'";

                    ArrayList<HashMap<String, String>> passData = idb.fetchRows(sqlPass);

                    if (passData != null) {
                        for (HashMap<String, String> pass : passData) {
                            String datum = pass.get("Datum");
                            String aktivitet = pass.get("Aktivitet");
                            String order = (pass.get("OrderID") != null) ? " (#" + pass.get("OrderID") + ")" : "";
                            String timmar = pass.get("Timmar") != null ? pass.get("Timmar") : "0";

                            int kolumn = beraknaKolumnIndex(startDatum, datum);

                            if (kolumn >= 1 && kolumn <= 5) {
                                String nyttPassInfo = aktivitet + order + " [" + timmar + "h]";

                                if (radData[kolumn] == null) {

                                    radData[kolumn] = nyttPassInfo;
                                } else {

                                    String befintligText = radData[kolumn].toString();

                                    if (!befintligText.startsWith("<html>")) {
                                        radData[kolumn] = "<html>" + befintligText + "<br>" + nyttPassInfo + "</html>";
                                    } else {
                                        String städadText = befintligText.replace("</html>", "");
                                        radData[kolumn] = städadText + "<br>" + nyttPassInfo + "</html>";
                                    }
                                }
                            }
                        }
                    }
                    model.addRow(radData);
                }
            }
        } catch (InfException ex) {
            JOptionPane.showMessageDialog(this, "Fel vid hämtning: " + ex.getMessage());
        }
    }

    private int beraknaKolumnIndex(String startStr, String passStr) {
        try {
            
            java.time.LocalDate start = java.time.LocalDate.parse(startStr);
            java.time.LocalDate pass = java.time.LocalDate.parse(passStr);

            long skillnad = java.time.temporal.ChronoUnit.DAYS.between(start, pass);

            return (int) skillnad + 1;
        } catch (Exception e) {
            return -1; 
        }
    }

    public class SchemaFargsattare extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setBackground(Color.WHITE);
            c.setForeground(Color.BLACK);

            if (column > 0 && value != null) {
                String cellText = value.toString();
                int totalaTimmar = extraheraOchSummeraTimmar(cellText);

                if (totalaTimmar >= 7) {
                    c.setBackground(new Color(255, 153, 153)); // Röd
                } else if (totalaTimmar >= 4) {
                    c.setBackground(new Color(255, 255, 153)); // Gul
                } else if (totalaTimmar > 0) {
                    c.setBackground(new Color(153, 255, 153)); // Grön
                }
            }
            if (isSelected) {
                c.setBackground(table.getSelectionBackground());
                c.setForeground(table.getSelectionForeground());
            }
            return c;
        }

        private int extraheraOchSummeraTimmar(String text) {
            int summa = 0;
            try {
                String[] delar = text.split("\\[");
                for (int i = 1; i < delar.length; i++) {
                    StringBuilder siffra = new StringBuilder();
                    for (char tecken : delar[i].toCharArray()) {
                        if (Character.isDigit(tecken)) {
                            siffra.append(tecken);
                        } else {
                            break;
                        }
                    }
                    if (siffra.length() > 0) {
                        summa += Integer.parseInt(siffra.toString());
                    }
                }
            } catch (Exception e) {
                System.out.println("Fel vid summering: " + e.getMessage());
            }
            return summa;
        }
    }

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
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSparaPass;
    private javax.swing.JButton btnSparaRedigera;
    private javax.swing.JButton btnTaBort;
    private javax.swing.JButton btnTillbakaKnapp;
    private javax.swing.JComboBox<String> cbVecka;
    private javax.swing.JComboBox<String> cmbValjAnstalld;
    private javax.swing.JComboBox<String> cmbValjOrder;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private com.toedter.calendar.JDateChooser jdDatumRedigera;
    private javax.swing.JTable jtPassLista;
    private javax.swing.JTable jtSchema;
    private javax.swing.JLabel lblAnstalldForPass;
    private javax.swing.JLabel lblAntalTimmar;
    private javax.swing.JLabel lblBeskrivningRedigera;
    private javax.swing.JLabel lblDatum;
    private javax.swing.JLabel lblDatumRedigera;
    private javax.swing.JLabel lblPlaneraPass;
    private javax.swing.JLabel lblRedigeraPass;
    private javax.swing.JLabel lblTimmarRedigera;
    private javax.swing.JLabel lblVisaSchema;
    private javax.swing.JLabel lblVäljOrder;
    private javax.swing.JTextField txtAntalTimmar;
    private javax.swing.JTextField txtBeskrivning;
    private javax.swing.JTextField txtBeskrivningAktivitet;
    private javax.swing.JTextField txtTimmar;
    // End of variables declaration//GEN-END:variables
}

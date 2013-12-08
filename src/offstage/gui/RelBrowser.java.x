/*
 * RelBrowser.java
 *
 * Created on March 25, 2009, 8:44 PM
 */

package offstage.gui;

import citibob.jschema.SchemaBuf;
import citibob.sql.DbKeyedModel;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
import citibob.swing.WidgetTree;
import citibob.util.ObjectUtil;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class RelBrowser extends javax.swing.JPanel {

RelDbModel relDb;
DbKeyedModel relidsKm;
RelEditDialog edit;
Integer defaultTemporalID;

/** Creates new form RelBrowser */
public RelBrowser() {
	initComponents();
	rels.setHighlightMouseover(true);
	edit = new RelEditDialog(WidgetTree.getJFrame(this));
}

public void initRuntime(SqlRun str, final FrontApp app, String relIdSql,
String temporalIdSql, Integer defaultTemporalID)
{
//	edit.initRuntime(app);
//
//	// Set up list of relationships we can access
//	String sql =
//		" select relid,name,0\n" +
//		" from (" + relIdSql + ") xx, relids\n" +
//		" where xx.id = relids.relid";
//	relidsKm = new DbKeyedModel(str, null, null, sql, "<No Relationship>");
//	edit.relids.setKeyedModel(relidsKm);
//	//edit.lRel.setJType(relidsKm, (String)relidsKm.getNullValue());
//	// TODO: Change KeyedSFormat to get the null value out of the KeyedModel
//	// Rationalize null value handling between KeyedModel, DbKeyedModel, etc.
//
//	relDb = new RelDbModel(str, app) {
//	public void setKey(Object... keys) {
//	}};
//
//	relDb.setRelIdSql(relIdSql);
//	relDb.setTemporalIdSql(temporalIdSql);
//
//	str.execUpdate(new UpdTasklet2() {		// Set up table AFTER enrolledDb has been initialized
//	public void run(SqlRun str) {
//		// RSSchema schema = (RSSchema)relDb.getSchemaBuf().getSchema();
//		rels.setModelU(app.swingerMap(), relDb.getTableModel(),
//			new String[] {"Description"},
//			new String[] {"description"});
//		rels.setEditable(false);
//
//		rels.getSelectionModel().addListSelectionListener(
//		new ListSelectionListener() {
//		public void valueChanged(ListSelectionEvent e) {
//			editRow(e.getFirstIndex());
//		}});
//	}});
}


void editRow(SqlRun str, int row)
{
//	SchemaBuf sbuf = relDb.getSchemaBuf();
//	Integer thisID = relDb.getEntityID();
//
//	Integer eid0 = (Integer)sbuf.getValueAt(row, "entityid0");
//	Integer eid1 = (Integer)sbuf.getValueAt(row, "entityid1");
//	Integer relid = (Integer)sbuf.getValueAt(row, "relid");
//	edit.setEditMode(edit.MODE_EDIT, thisID,
//		eid0, relid, eid1);
//	edit.setVisible(true);
//
//	switch(edit.action) {
//		case RelEditDialog.ACTION_OK : {
//
//		} break;
//		case RelEditDialog.ACTION_DELETE : {
//			String sql =
//				" delete from rels" +
//				" where entityid0 = "
//		} break;
//	}
//	if (edit.action == edit.)
//
//	edit.setThisID(thisID)
//
//	edit.entityid0.setValue(eid0);
//	if (ObjectUtil.eq(eid0, thisID)) edit.entityid0.setEnabled(false);
//
//	edit.entityid1.setValue(eid1);
//	if (ObjectUtil.eq(eid1, thisID)) edit.entityid1.setEnabled(false);
}


public void setRel_o2m(SqlRun str, String srelid, String stemporalid,
int entityid0, int entityid1)
{
//	str.execSql(
//		" select w_rels_o2m_set(" +
//		srelid + ", " + stemporalid + ", " + entityid0 + ", " + entityid1 + ");");
//	doSelect(str);
}


	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel1 = new javax.swing.JPanel();
        GroupScrollPanel = new javax.swing.JScrollPane();
        rels = new citibob.swing.StyledTable();
        jPanel2 = new javax.swing.JPanel();
        relids = new citibob.swing.typed.JKeyedComboBox();
        bAddRel = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setLayout(new java.awt.BorderLayout());

        rels.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        GroupScrollPanel.setViewportView(rels);

        jPanel1.add(GroupScrollPanel, java.awt.BorderLayout.CENTER);

        add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        relids.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        relids.setColName("rbplan"); // NOI18N
        relids.setPreferredSize(new java.awt.Dimension(120, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(relids, gridBagConstraints);

        bAddRel.setText("+");
        bAddRel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bAddRelActionPerformed(evt);
            }
        });
        jPanel2.add(bAddRel, new java.awt.GridBagConstraints());

        add(jPanel2, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

	private void bAddRelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bAddRelActionPerformed
		// TODO add your handling code here:
}//GEN-LAST:event_bAddRelActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JButton bAddRel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private citibob.swing.typed.JKeyedComboBox relids;
    private citibob.swing.StyledTable rels;
    // End of variables declaration//GEN-END:variables

	
// =================================================================
	
}

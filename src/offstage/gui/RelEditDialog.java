/*
 * RelEditorDialog.java
 *
 * Created on March 25, 2009, 9:30 PM
 */

package offstage.gui;

import citibob.app.App;
import citibob.sql.SqlRun;
import citibob.util.ObjectUtil;
import javax.swing.JOptionPane;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class RelEditDialog extends javax.swing.JDialog {

App app;
	
public static final int ACTION_CANCEL = 0;
public static final int ACTION_OK = 1;
public static final int ACTION_DELETE = 2;
protected int action = ACTION_CANCEL;	// How this dialog was exited

Integer thisID;			// EntityID of the currently-editing record.

public static final int MODE_EDIT = 0;	// Edit an existing relationship
public static final int MODE_NEW = 1;	// Add new relationship

int editMode;
//int editMode;

/** Creates new form RelEditorDialog */
public RelEditDialog(java.awt.Frame parent) {
	super(parent, true);
	initComponents();
}

public void initRuntime(FrontApp app)
{
	this.app = app;
	entityid0.initRuntime(app);
	entityid1.initRuntime(app);
}

//public void setThisID(Integer thisID)
//	{ this.thisID = thisID; }

public Integer getEntityID0() { return (Integer)entityid0.getValue(); }
public Integer getEntityID1() { return (Integer)entityid1.getValue(); }
public Integer getRelID() { return (Integer)relids.getValue(); }

public void setEditMode(int editMode, Integer thisID,
Integer eid0, Integer relid, Integer eid1)
{
	this.editMode = editMode;
	this.thisID = thisID;

	entityid0.setValue(eid0);
	relids.setValue(relid);
	entityid1.setValue(eid1);

	if (editMode == MODE_EDIT) {
		entityid0.setEnabled(!ObjectUtil.eq(eid0, thisID));
		relids.setEnabled(false);
		entityid1.setEnabled(!ObjectUtil.eq(eid1, thisID));
	} else {
		// New item: let them edit everything
		entityid0.setEnabled(true);
		relids.setEnabled(true);
		entityid1.setEnabled(true);
	}

	app.sqlRun().flush();
}

/** Store the just-edited relation in the database */
public void storeRel(SqlRun str)
{
//	DB.
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
        entityid0 = new offstage.swing.typed.EntityIDDropdown();
        entityid1 = new offstage.swing.typed.EntityIDDropdown();
        relids = new citibob.swing.typed.JKeyedComboBox();
        jPanel2 = new javax.swing.JPanel();
        bOK = new javax.swing.JButton();
        bDelete1 = new javax.swing.JButton();
        bDelete = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        entityid0.setColName("entityid0"); // NOI18N
        entityid0.setPreferredSize(new java.awt.Dimension(200, 19));

        entityid1.setColName("entityid0"); // NOI18N
        entityid1.setPreferredSize(new java.awt.Dimension(200, 19));

        relids.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        relids.setColName("rbplan"); // NOI18N
        relids.setPreferredSize(new java.awt.Dimension(120, 19));

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(entityid1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .add(entityid0, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(12, 12, 12)
                        .add(relids, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(entityid0, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(relids, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(11, 11, 11)
                .add(entityid1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(44, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        bOK.setText("OK");
        bOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bOKActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        jPanel2.add(bOK, gridBagConstraints);

        bDelete1.setText("Cancel");
        bDelete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDelete1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        jPanel2.add(bDelete1, gridBagConstraints);

        bDelete.setText("Delete");
        bDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bDeleteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 19, 0, 0);
        jPanel2.add(bDelete, gridBagConstraints);

        getContentPane().add(jPanel2, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

	private void bDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDeleteActionPerformed
		// TODO add your handling code here:
		action = ACTION_DELETE;
		setVisible(false);
}//GEN-LAST:event_bDeleteActionPerformed

	private void bOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bOKActionPerformed
		if (getRelID() == null) {
			JOptionPane.showMessageDialog(this, "You must select a relationship!");
			return;
		}

		if (!ObjectUtil.eq(entityid0.getValue(), thisID) &&
			!ObjectUtil.eq(entityid1.getValue(), thisID))
		{
			JOptionPane.showMessageDialog(this, "At least one of the people must\nbe the person being edited.");
			return;
		}
		action = ACTION_OK;
		setVisible(false);
		// TODO add your handling code here:
	}//GEN-LAST:event_bOKActionPerformed

	private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		action = ACTION_CANCEL;
		// TODO add your handling code here:
	}//GEN-LAST:event_formWindowClosing

	private void bDelete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bDelete1ActionPerformed
		// TODO add your handling code here:
		action = ACTION_CANCEL;
		setVisible(false);
	}//GEN-LAST:event_bDelete1ActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bDelete;
    private javax.swing.JButton bDelete1;
    private javax.swing.JButton bOK;
    protected offstage.swing.typed.EntityIDDropdown entityid0;
    protected offstage.swing.typed.EntityIDDropdown entityid1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    protected citibob.swing.typed.JKeyedComboBox relids;
    // End of variables declaration//GEN-END:variables
	
}

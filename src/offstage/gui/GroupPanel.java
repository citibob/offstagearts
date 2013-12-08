/*
OffstageArts: Enterprise Database for Arts Organizations
This file Copyright (c) 2005-2008 by Robert Fischer

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
/*
 * GroupPanel.java
 *
 * Created on June 5, 2005, 2:31 PM
 */

package offstage.gui;

import java.sql.*;
import citibob.jschema.*;
import citibob.jschema.swing.*;
//import citibob.jschema.swing.JSchemaWidgetTree;
import citibob.swing.table.*;
import citibob.sql.*;
import citibob.swing.typed.*;
import citibob.types.*;
import offstage.devel.gui.*;

/**
 *
 * @author  citibob
 */
public class GroupPanel extends javax.swing.JPanel {
	
SchemaBuf groupSb;
String sGroupCol;
boolean enabled;		// Are we in enabled state?
//DataCols<Boolean> editableEnabled;	// Editable status when we're enabled
DataCols<Boolean> editableDisabled;	// Editable status when we're disabled

	/** Creates new form GroupPanel */
	public GroupPanel() {
		initComponents();
	}

	public GroupsTable getTable() { return groupTable; }
	
	// st good
	public void initRuntime(SqlRun str, SchemaBuf groupSb,
		String sGroupCol,
		String[] colNames, String[] sColMap,
		citibob.swing.typed.SwingerMap swingers)
	//throws java.sql.SQLException
	{
		this.enabled = true;
		this.sGroupCol = sGroupCol;
		
		// Set up model for column editing when we're enabled.
		// Use defaults...
		JEnum addJType = (JEnum)groupSb.getSchema().getCol(sGroupCol).getType();
		groupTable.setModelU(groupSb, colNames, sColMap, null, swingers);
		//editableEnabled = (DataCols<Boolean>)groupTable.getDelegateStyledTM().getEditableModel();

		// Set up model for column eding when we're disabled.
		// Copy the enabled model, set all to false.
		int ncol = sColMap.length + 1;
		editableDisabled = new DataCols<Boolean>(Boolean.class, ncol);
		for (int i=0; i<ncol; ++i) editableDisabled.setColumn(i, Boolean.FALSE);

		// groupTable.getDelegateStyledTM().setEditable(xeditable);

		initRuntime(str, groupSb, addJType, groupTable, swingers);
	}

	public void setEditable(boolean enabled)
	{
		addBtn.setEnabled(enabled);
		delBtn.setEnabled(enabled);
		addType.setEnabled(enabled);

		if (enabled == this.enabled) return;
		this.enabled = enabled;
		if (enabled) {
			//add(controller);
			groupTable.getDelegateStyledTM().setEditableModel(null);
			//validate();
		} else {
			//remove(controller);
			groupTable.getDelegateStyledTM().setEditableModel(editableDisabled);
			//validate();
		}
	}

	/** @param addJType Type of column that should appear in the drop-down next
	to the "add row" (+) button */
	public void initRuntime(SqlRun str, SchemaBuf groupSb, JEnum addJType,
	GroupsTable groupTable, SwingerMap swingers)
	//throws java.sql.SQLException
	{
		this.groupSb = groupSb;
		this.groupTable = groupTable;
		swingers.newSwinger(addJType).configureWidget(addType);
//		this.addType.setJType(swingers.newSwinger(addJType));
//		this.addType.setKeyedModel(new DbKeyedModel(st, null,
//				idTableName, "groupid", "name", "name"));
	}
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        GroupScrollPanel = new javax.swing.JScrollPane();
        groupTable = new offstage.devel.gui.GroupsTable();
        controller = new javax.swing.JPanel();
        addType = new citibob.swing.typed.JKeyedComboBox();
        jPanel1 = new javax.swing.JPanel();
        addBtn = new javax.swing.JButton();
        delBtn = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        groupTable.setModel(new javax.swing.table.DefaultTableModel(
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
        GroupScrollPanel.setViewportView(groupTable);

        add(GroupScrollPanel, java.awt.BorderLayout.CENTER);

        controller.setLayout(new java.awt.BorderLayout());

        addType.setMinimumSize(new java.awt.Dimension(31, 19));
        addType.setPreferredSize(new java.awt.Dimension(31, 19));
        controller.add(addType, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        addBtn.setFont(new java.awt.Font("Dialog", 1, 12));
        addBtn.setText("+");
        addBtn.setMargin(new java.awt.Insets(0, 2, 0, 2));
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel1.add(addBtn, gridBagConstraints);

        delBtn.setFont(new java.awt.Font("Dialog", 1, 12));
        delBtn.setText("-");
        delBtn.setMargin(new java.awt.Insets(0, 2, 0, 2));
        delBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
        jPanel1.add(delBtn, gridBagConstraints);

        controller.add(jPanel1, java.awt.BorderLayout.EAST);

        add(controller, java.awt.BorderLayout.SOUTH);
    }// </editor-fold>//GEN-END:initComponents

	private void delBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delBtnActionPerformed
		int selected = groupTable.getSelectedRow();
		if (selected != -1) {
System.out.println("Deleting row: " + selected);
			groupSb.deleteRow(selected);
		}
	}//GEN-LAST:event_delBtnActionPerformed

	private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
		try {
			groupSb.insertRow(-1, sGroupCol, addType.getValue());
		} catch(KeyViolationException e) {
			System.out.println(e);
			// We should put up a JOptionPane here.
		}
	}//GEN-LAST:event_addBtnActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane GroupScrollPanel;
    private javax.swing.JButton addBtn;
    private citibob.swing.typed.JKeyedComboBox addType;
    private javax.swing.JPanel controller;
    private javax.swing.JButton delBtn;
    private offstage.devel.gui.GroupsTable groupTable;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables
	
}

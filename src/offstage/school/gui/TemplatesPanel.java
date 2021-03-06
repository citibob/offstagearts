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
 * ResourcePanel.java
 *
 * Created on February 24, 2008, 10:27 AM
 */

package offstage.school.gui;

import citibob.resource.ResModels;
import citibob.app.App;
import citibob.resource.ResData;
import citibob.resource.RtResKey;
import citibob.resource.ResSet;
import citibob.resource.ResUtil;
import citibob.resource.Resource;
import citibob.resource.RtRes;
import citibob.resource.RtVers;
import citibob.resource.UpgradePlan;
import citibob.sql.SqlRun;
import citibob.task.SqlTask;

/**
 
 @author  citibob
 */
public class TemplatesPanel extends javax.swing.JPanel
{

App app;
SchoolModel smod;

Resource curResource;
RtResKey curResKey;
//Integer curVersion;
UpgradePlan curUPlan;

ResData rdata;
ResModels rmods;
boolean inUpdate;

	/** Creates new form ResourcePanel */
	public TemplatesPanel()
	{
		initComponents();
	}
	
	public void initRuntime(SqlRun str, App xapp, SchoolModel xsmod, String resourceGroup)
	{
		this.app = xapp;
		this.smod = xsmod;
		
		ResSet rset = app.resSet();
		rdata = app.resData(); //new ResData(str, rset, app.sqlTypeSet());
		rmods = new ResModels(rdata, app, app.sysVersion(), resourceGroup);
		//rdata.readData(str);
		
		// Set up tables
		tResources.setModelU(rmods.resMod,
			new String[] {"Resource"},
			new String[] {"name"},
			null, app.swingerMap());
		
//		// Select a Resource
//		tResources.addPropertyChangeListener("value", new PropertyChangeListener() {
//		public void propertyChange(PropertyChangeEvent evt) {
//			RtRes res = (RtRes)evt.getNewValue();
//			editResource(getResKey(res));
//		}});
		
	};
	

	/** Returns the specific uversion'd resource, given a general
	 resource and the global termID */
	RtResKey getResKey(RtRes res)
	{
		int termID = smod.getTermID();
		for (RtResKey rk : res.relevant) {
System.out.println("looking at termID = " + termID + ", rk = " + rk);
			if (rk.uversionid == termID) return rk;
		}
		return null;
	}
	void editResource(RtResKey rk) throws Exception
	{
		// See if we have the required version
		int reqVersion = rk.res.getRequiredVersion(app.sysVersion());
		RtVers rv = rk.getRtVers(reqVersion);
			
		// Create the required version if it does not exist.
		if (rv == null) {
			// See if we can make required version from this version
			UpgradePlan uplan = rk.getUpgradePlanFromLatest(reqVersion);
			if (uplan != null) {
				// Create the new version
				SqlRun str = app.sqlRun();
				uplan.applyPlan(str, app.pool());
				str.flush();
				rdata.readData(str);
				str.flush();
				rv = rk.getRtVers(reqVersion);
			}
		}
		
		// Edit it
		ResUtil.editResource(app.sqlRun(), app.pool(),
			TemplatesPanel.this, rk, rv);
	}

	/** This method is called from within the constructor to
	 initialize the form.
	 WARNING: Do NOT modify this code. The content of this method is
	 always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        bEdit = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tResources = new citibob.swing.typed.JTypedSelectTable();

        setLayout(new java.awt.BorderLayout());

        jPanel4.setLayout(new java.awt.BorderLayout());

        bEdit.setText("Edit Template");
        bEdit.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bEditActionPerformed(evt);
            }
        });
        jPanel1.add(bEdit);

        jPanel4.add(jPanel1, java.awt.BorderLayout.SOUTH);

        tResources.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][]
            {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String []
            {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(tResources);

        jPanel4.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        add(jPanel4, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void bEditActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bEditActionPerformed
	{//GEN-HEADEREND:event_bEditActionPerformed
		app.guiRun().run(TemplatesPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			RtRes res = (RtRes)tResources.getValue();
			editResource(getResKey(res));
		}});
	}//GEN-LAST:event_bEditActionPerformed

	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bEdit;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane3;
    private citibob.swing.typed.JTypedSelectTable tResources;
    // End of variables declaration//GEN-END:variables
	
}

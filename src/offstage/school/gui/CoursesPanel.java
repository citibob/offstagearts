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
 * CoursesPanel.java
 *
 * Created on December 15, 2007, 4:39 PM
 */

package offstage.school.gui;

import citibob.jschema.DayOfWeekKeyedModel;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.SchemaBuf;
import citibob.jschema.SqlSchemaInfo;
import citibob.sql.ConsSqlQuery;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet2;
import citibob.sql.pgsql.SqlInteger;
import citibob.sql.pgsql.SqlTimestamp;
import citibob.swing.WidgetTree;
import citibob.swing.typed.Swinger;
import citibob.swingers.JDateSwinger;
import citibob.task.ETask;
import citibob.task.SqlTask;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JOptionPane;
import offstage.FrontApp;

/**
 *
 * @author  citibob
 */
public class CoursesPanel extends javax.swing.JPanel
{
	
FrontApp fapp;
SchoolModel smod;

IntKeyedDbModel coursesDb;
IntKeyedDbModel meetingsDb;
int courseid;		// Currently select course
int courseRow;		// Currently selected row of courses table

/** Creates new form CoursesPanel */
public CoursesPanel()
{
	initComponents();
}

	

/** Creates new form CompleteStatusPanel */
public void initRuntime(FrontApp xfapp, SchoolModel smod, SqlRun str)
//throws SQLException
{
	this.fapp = xfapp;
	this.smod = smod;

//	// Set up terms selector
//	terms.setKeyedModel(new DbKeyedModel(str, fapp.getDbChange(), "termids",
//		"select groupid, name from termids where iscurrent order by firstdate"));
////	str0.runBatches(fapp);

	smod.addListener(new SchoolModel.Adapter() {
    public void termIDChanged(int oldTermID, int termID) {
			termChanged(fapp.sqlRun());
//			fapp.guiRun().run(new BatchRunnable() {
//			public void run(SqlRun str) throws Exception {
//				termChanged(str);
//			}});
	}});

	// Set up courses editor
	coursesDb = new IntKeyedDbModel(fapp.getSchema("courseids"),
		"termid", fapp.dbChange()) {
	/** Override stuff to delete from meetings table when we delete from courseids table. */
	protected ConsSqlQuery doSimpleDeleteNoRemoveRow(int row, SqlRun str, SqlSchemaInfo qs) {
		ConsSqlQuery q = super.doSimpleDeleteNoRemoveRow(row, str, qs);
		int courseid = (Integer)getSchemaBuf().getValueAt(row, "courseid");
		String sql =
			" delete from meetings where courseid = " + SqlInteger.sql(courseid) + ";\n" +
			" delete from enrollments where courseid = " + SqlInteger.sql(courseid) + ";\n";
		str.execSql(sql);
		return q;
	}};
	coursesDb.setDoInsertKeys(false);
	coursesDb.setOrderClause("dayofweek, tstart, name");

	courses.setModelU(coursesDb.getSchemaBuf(),
		new String[] {"Status", "#", "Name", "Day", "Start", "End", "Tuition", "Reg Fee", "Enroll Limit"},
		new String[] {"__status__", "__rowno__", "name", "dayofweek", "tstart", "tnext", "price", "regfee", "enrolllimit"},
		null, fapp.swingerMap());
	courses.setFormatU("dayofweek", new DayOfWeekKeyedModel());
courses.setHighlightMouseover(true);
	
	courses.addPropertyChangeListener("value", new PropertyChangeListener() {
	public void propertyChange(PropertyChangeEvent evt) {
//	courses.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//	public void valueChanged(ListSelectionEvent e) {
		int oldid = courseid;
		Integer Courseid = (Integer)courses.getValue("courseid");
		// courseRow = courses.getSelectedRow();
		courseid = Courseid == null ? -1 : Courseid.intValue();
		//courseRow < 0 ? -1 : (Integer)coursesDb.getSchemaBuf().getValueAt(courseRow, "courseid");
		if (courseid != oldid) {
//			fapp.guiRun().run(new SqlTask() {
//			public void run(SqlRun str) throws Exception {
			fapp.sqlRun().pushFlush();
				courseChanged(fapp.sqlRun());
			fapp.sqlRun().popFlush();
//			}});
		}
	}});
	
	// Set up meetings editor
	meetingsDb = new IntKeyedDbModel(fapp.getSchema("meetings"),
		"courseid", fapp.dbChange());
	meetingsDb.setDoInsertKeys(false);
	meetingsDb.setOrderClause("dtstart");
	meetings.setModelU(meetingsDb.getSchemaBuf(),
		new String[] {"Status", "#", "Start", "End"},
		new String[] {"__status__", "__rowno__", "dtstart", "dtnext"},
		null, fapp.swingerMap());

	// Dates are internally represented (in the database) in GMT, but displayed in local timezone.
	Swinger swing = new JDateSwinger(new SqlTimestamp("GMT"),
		new String[] {"EEE MMM dd hh:mm a yy"}, "", fapp.timeZone(),
		citibob.swing.calendar.JCalendarDateHHMM.class);
	meetings.setFormatU("dtstart", swing);
	meetings.setFormatU("dtnext", swing);

}

void termChanged(SqlRun str) 
{
	meetingsDb.doUpdate(str);
	coursesDb.setKey((Integer)smod.getTermID());
	coursesDb.doUpdate(str);
	coursesDb.doSelect(str);
}

void courseChanged(SqlRun str)
{
	meetingsDb.setKey(courseid);
	meetingsDb.doUpdate(str);
	meetingsDb.doSelect(str);
}
	
void all_doSelect(SqlRun str)
{
	final int id = courseid;
	meetingsDb.doSelect(str);
	coursesDb.doSelect(str);

	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		courses.setSelectedRowU(id, "courseid");
	}});
}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jToolBar1 = new javax.swing.JToolBar();
        bSave = new javax.swing.JButton();
        bUndo = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        coursesButtons = new javax.swing.JPanel();
        bAddCourse = new javax.swing.JButton();
        bDelCourse = new javax.swing.JButton();
        bUndelCourse = new javax.swing.JButton();
        bDelAllCourse = new javax.swing.JButton();
        bUndelAllCourse = new javax.swing.JButton();
        bAutoFillAll = new javax.swing.JButton();
        coursesPane = new javax.swing.JScrollPane();
        courses = new citibob.swing.typed.JTypedSelectTable();
        jPanel4 = new javax.swing.JPanel();
        meetingsPane = new javax.swing.JScrollPane();
        meetings = new citibob.jschema.swing.SchemaBufTable();
        lMeetings = new javax.swing.JLabel();
        meetingsButtons = new javax.swing.JPanel();
        bAddMeeting = new javax.swing.JButton();
        bDelMeeting = new javax.swing.JButton();
        bUndoDelMeeting = new javax.swing.JButton();
        bDelAllMeeting = new javax.swing.JButton();
        bUndelAllMeeting = new javax.swing.JButton();
        bAutoFillMeetings = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        bSave.setText("Save");
        bSave.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bSaveActionPerformed(evt);
            }
        });
        jToolBar1.add(bSave);

        bUndo.setText("Undo");
        bUndo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoActionPerformed(evt);
            }
        });
        jToolBar1.add(bUndo);

        add(jToolBar1, java.awt.BorderLayout.SOUTH);

        jPanel3.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("Courses");
        jPanel3.add(jLabel1, java.awt.BorderLayout.NORTH);

        bAddCourse.setText("Add");
        bAddCourse.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bAddCourse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAddCourseActionPerformed(evt);
            }
        });
        coursesButtons.add(bAddCourse);

        bDelCourse.setText("Del");
        bDelCourse.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bDelCourse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelCourseActionPerformed(evt);
            }
        });
        coursesButtons.add(bDelCourse);

        bUndelCourse.setText("(Undo)");
        bUndelCourse.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bUndelCourse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndelCourseActionPerformed(evt);
            }
        });
        coursesButtons.add(bUndelCourse);

        bDelAllCourse.setText("Del All");
        bDelAllCourse.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bDelAllCourse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelAllCourseActionPerformed(evt);
            }
        });
        coursesButtons.add(bDelAllCourse);

        bUndelAllCourse.setText("(Undo)");
        bUndelAllCourse.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bUndelAllCourse.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndelAllCourseActionPerformed(evt);
            }
        });
        coursesButtons.add(bUndelAllCourse);

        bAutoFillAll.setText("Auto Fill All");
        bAutoFillAll.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAutoFillAllActionPerformed(evt);
            }
        });
        coursesButtons.add(bAutoFillAll);

        jPanel3.add(coursesButtons, java.awt.BorderLayout.SOUTH);

        courses.setModel(new javax.swing.table.DefaultTableModel(
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
        coursesPane.setViewportView(courses);

        jPanel3.add(coursesPane, java.awt.BorderLayout.CENTER);

        jSplitPane1.setLeftComponent(jPanel3);

        jPanel4.setLayout(new java.awt.BorderLayout());

        meetingsPane.setPreferredSize(new java.awt.Dimension(300, 275));

        meetings.setModel(new javax.swing.table.DefaultTableModel(
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
        meetingsPane.setViewportView(meetings);

        jPanel4.add(meetingsPane, java.awt.BorderLayout.CENTER);

        lMeetings.setText("Meetings");
        jPanel4.add(lMeetings, java.awt.BorderLayout.NORTH);

        bAddMeeting.setText("Add");
        bAddMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bAddMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAddMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bAddMeeting);

        bDelMeeting.setText("Del");
        bDelMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bDelMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bDelMeeting);

        bUndoDelMeeting.setText("(Undo)");
        bUndoDelMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bUndoDelMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndoDelMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bUndoDelMeeting);

        bDelAllMeeting.setText("Del All");
        bDelAllMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bDelAllMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bDelAllMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bDelAllMeeting);

        bUndelAllMeeting.setText("(Undo)");
        bUndelAllMeeting.setMargin(new java.awt.Insets(2, 2, 2, 2));
        bUndelAllMeeting.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bUndelAllMeetingActionPerformed(evt);
            }
        });
        meetingsButtons.add(bUndelAllMeeting);

        bAutoFillMeetings.setText("Auto Fill");
        bAutoFillMeetings.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                bAutoFillMeetingsActionPerformed(evt);
            }
        });
        meetingsButtons.add(bAutoFillMeetings);

        jPanel4.add(meetingsButtons, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setRightComponent(jPanel4);

        add(jSplitPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void bUndelAllCourseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndelAllCourseActionPerformed
	{//GEN-HEADEREND:event_bUndelAllCourseActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			coursesDb.getSchemaBuf().undeleteAllRows();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndelAllCourseActionPerformed

	private void bUndelCourseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndelCourseActionPerformed
	{//GEN-HEADEREND:event_bUndelCourseActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			coursesDb.getSchemaBuf().undeleteRow(courses.getSelectedRow());
			courses.requestFocus();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndelCourseActionPerformed

	private void bUndelAllMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndelAllMeetingActionPerformed
	{//GEN-HEADEREND:event_bUndelAllMeetingActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			meetingsDb.getSchemaBuf().undeleteAllRows();
		}});

// TODO add your handling code here:
	}//GEN-LAST:event_bUndelAllMeetingActionPerformed

	private void bDelAllCourseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelAllCourseActionPerformed
	{//GEN-HEADEREND:event_bDelAllCourseActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			coursesDb.getSchemaBuf().deleteAllRows();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bDelAllCourseActionPerformed

	private void bUndoDelMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoDelMeetingActionPerformed
	{//GEN-HEADEREND:event_bUndoDelMeetingActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			meetingsDb.getSchemaBuf().undeleteRow(meetings.getSelectedRow());
			meetings.requestFocus();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndoDelMeetingActionPerformed

	private void bDelAllMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelAllMeetingActionPerformed
	{//GEN-HEADEREND:event_bDelAllMeetingActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			meetingsDb.getSchemaBuf().deleteAllRows();
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bDelAllMeetingActionPerformed

	private void bUndoActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bUndoActionPerformed
	{//GEN-HEADEREND:event_bUndoActionPerformed
		fapp.guiRun().run(new SqlTask() {
		public void run(SqlRun str) throws Exception {
			all_doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bUndoActionPerformed

	private void bSaveActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bSaveActionPerformed
	{//GEN-HEADEREND:event_bSaveActionPerformed
		fapp.guiRun().run(new SqlTask() {
		public void run(SqlRun str) throws Exception {
			meetingsDb.doUpdate(str);
			coursesDb.doUpdate(str);
			str.flush();
			all_doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bSaveActionPerformed

	private void bAutoFillAllActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAutoFillAllActionPerformed
	{//GEN-HEADEREND:event_bAutoFillAllActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			if (JOptionPane.showConfirmDialog(CoursesPanel.this,
				"Are you sure you wish to fill all course meetings?\n" +
				"This will overwrite all previous settings.",
				"Fill All Meetings",
				JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

			SchoolDB.w_meetings_autofill(str, smod.getTermID(), -1, fapp.timeZone());
			str.flush();
			meetingsDb.doSelect(str);
		}});
// TODO add your handling code here:
}//GEN-LAST:event_bAutoFillAllActionPerformed

	private void bAutoFillMeetingsActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAutoFillMeetingsActionPerformed
	{//GEN-HEADEREND:event_bAutoFillMeetingsActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			SchoolDB.w_meetings_autofill(str, smod.getTermID(), courseid, fapp.timeZone());
			str.flush();
			meetingsDb.doSelect(str);
		}});
// TODO add your handling code here:
	}//GEN-LAST:event_bAutoFillMeetingsActionPerformed

	private void bDelMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelMeetingActionPerformed
	{//GEN-HEADEREND:event_bDelMeetingActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			meetingsDb.getSchemaBuf().deleteRow(meetings.getSelectedRow());
			meetings.requestFocus();
		}});
	}//GEN-LAST:event_bDelMeetingActionPerformed

	private void bAddMeetingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAddMeetingActionPerformed
	{//GEN-HEADEREND:event_bAddMeetingActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new SqlTask() {
		public void run(SqlRun str) throws Exception {
			AddMeetingWizard wizard = new AddMeetingWizard(fapp, WidgetTree.getJFrame(CoursesPanel.this));
			if (wizard.runWizard()) {
				SchemaBuf csb = coursesDb.getSchemaBuf();
				SchemaBuf msb = meetingsDb.getSchemaBuf();
				long tstartMS = ((java.util.Date)csb.getValueAt(courseRow, "tstart")).getTime();
				long tnextMS = ((java.util.Date)csb.getValueAt(courseRow, "tnext")).getTime();
				long ms = ((java.util.Date)wizard.getVal("date")).getTime();
				
				msb.insertRow(-1, new String[] {"courseid", "dtstart", "dtnext"},
					new Object[] {new Integer(courseid),
						new java.util.Date(ms + tstartMS),
						new java.util.Date(ms + tnextMS)});
				
			}
			
		}});

//		fapp.guiRun().run(CoursesPanel.this, new ERunnable()
//		{ public void run() throws Exception
//		  {
//			  meetingsSb.getSchemaBuf().insertRow(-1);
//		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bAddMeetingActionPerformed

	private void bDelCourseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bDelCourseActionPerformed
	{//GEN-HEADEREND:event_bDelCourseActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask() {
		public void run() throws Exception {
			coursesDb.getSchemaBuf().deleteRow(courses.getSelectedRow());
			courses.requestFocus();
		}});
	}//GEN-LAST:event_bDelCourseActionPerformed

	private void bAddCourseActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_bAddCourseActionPerformed
	{//GEN-HEADEREND:event_bAddCourseActionPerformed
		fapp.guiRun().run(CoursesPanel.this, new ETask()
		{ public void run() throws Exception
		  {
			  coursesDb.getSchemaBuf().insertRow(-1,
				  new String[] {"termid", "dayofweek"},
				  new Object[] {(Integer)smod.getTermID(), new Integer(-1)});
		  }});
// TODO add your handling code here:
	}//GEN-LAST:event_bAddCourseActionPerformed
	
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bAddCourse;
    private javax.swing.JButton bAddMeeting;
    private javax.swing.JButton bAutoFillAll;
    private javax.swing.JButton bAutoFillMeetings;
    private javax.swing.JButton bDelAllCourse;
    private javax.swing.JButton bDelAllMeeting;
    private javax.swing.JButton bDelCourse;
    private javax.swing.JButton bDelMeeting;
    private javax.swing.JButton bSave;
    private javax.swing.JButton bUndelAllCourse;
    private javax.swing.JButton bUndelAllMeeting;
    private javax.swing.JButton bUndelCourse;
    private javax.swing.JButton bUndo;
    private javax.swing.JButton bUndoDelMeeting;
    private citibob.swing.typed.JTypedSelectTable courses;
    private javax.swing.JPanel coursesButtons;
    private javax.swing.JScrollPane coursesPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lMeetings;
    private citibob.jschema.swing.SchemaBufTable meetings;
    private javax.swing.JPanel meetingsButtons;
    private javax.swing.JScrollPane meetingsPane;
    // End of variables declaration//GEN-END:variables
	
}

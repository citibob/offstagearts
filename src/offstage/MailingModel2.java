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
 * MailingDbModel.java
 *
 * Created on July 11, 2005, 10:31 PM
 */

package offstage;
import citibob.jschema.*;
import citibob.reports.Reports;
import citibob.reports.ReportsApp;
import java.sql.*;
import java.sql.*;
import java.io.*;
import net.sf.jasperreports.engine.*;
import java.util.*;
import javax.swing.event.*;
import offstage.db.DB;
import citibob.task.*;
import offstage.schema.*;
import net.sf.jasperreports.engine.export.*;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.*;
import citibob.sql.*;

/**
 *
 * @author citibob
 */
public class MailingModel2 extends MultiDbModel
{

SchemaBufDbModel mailingids;		// Set to just one record (for 1 mailing)
IntKeyedDbModel mailings;		// Set to entire mailing info
//IntKeyedDbModel oneMailingid;		// Set to entire mailing info
//int mailingID;					// Current mailing ID
//SqlRun str;
JobRun runner;
ReportsApp app;

public IntKeyedDbModel getMailingsDb()
	{ return mailings; }
public SchemaBufDbModel getMailingidsDb()
	{ return mailingids; }
//public SchemaBufDbModel getOneMailingidsDb()
//	{ return oneMailingid; }

public void setKey(int mailingID)
{
	mailings.setKey(mailingID);
//	oneMailingid.setKey(mailingID);
}

/** Sets up the SchemaBufs for a new person,
which will be inserted into the DB upon doUpdate(). */
public void newAddress() throws KeyViolationException
{
	mailings.getSchemaBuf().insertRow(-1, new String[] {"groupid"}, new Object[] {new Integer(mailings.getIntKey())});
//	mailings.getSchemaBuf().insertRow(-1, (String[])null, null);
	//new String[] {"groupid"}, new Object[] {mailings.getKey()});
}


/** Creates a new instance of MailingDbModel */
public MailingModel2(final SqlRun str, ReportsApp app) //OffstageSchemaSet sset)
//throws SQLException
{
	this.app = app;
	SchemaSet sset = app.schemaSet();
	this.runner = runner;
	mailings = new IntKeyedDbModel(sset.get("mailings"), "groupid", null); //, new IntKeyedDbModel.Params(false));
	mailings.setDoInsertKeys(false);
	add(mailings);
//	oneMailingid = new IntKeyedDbModel(sset.mailingids, "groupid", false, null);
//	add(oneMailingid);
	
//	add(mailings);
//	mailings.setInstantUpdate(runner, true);
//	add(mailingids = new SchemaBufDbModel(sset.mailingids, "groupid", false, null));
	mailingids = new SchemaBufDbModel(new SchemaBuf(sset.get("mailingids")), null);
	mailingids.setWhereClause("created >= now() - interval '30 days'");
	mailingids.setOrderClause("created desc");
	add(mailingids);
	mailingids.doSelect(str);
	
//	mailingids.setInstantUpdate(runner, true);

//	// Refresh mailingids table when this changes.
//	mailingids.getSchemaBuf().addTableModelListener(new TableModelListener() {
//	public void tableChanged(TableModelEvent e) {
//		try {
//			mailingids.doSelect(st);
//		} catch(SQLException ee) {
//			ee.printStackTrace(System.out);
//		}
//	}});
}

//public void makeReport(SqlRun str) throws SQLException, JRException
//{
//	DB.w_mailings_makereport(str, mailings.getIntKey());
//
//	String sql =
//		"select * from mailings" +
//		" where groupid=" + mailings.getIntKey() +
//		" and isgood" +
//		" order by country, zip";
//	str.execSql(sql, new RsTasklet2() {
//	public void run(SqlRun str, ResultSet rs) throws Exception {
//		Reports rr = app.reports();
//		rr.viewJasper(rr.toJasper(rs), null, "AddressLabels.jrxml", 0);
//	}});
//}

//public void makeReport() throws SQLException, JRException
//{
////	runner.doRun(new StRunnable() {
////	public void run(SqlRun str) throws Exception {
//		ResultSet rs = null;
//		InputStream in = null;
//		try {
//			DB.w_mailings_makereport(st, mailings.getKey());
//
//			in = Object.class.getResourceAsStream("/offstage/reports/AddressLabels.jrxml");
//			rs = st.executeQuery("select * from mailings" +
//					" where groupid=" + mailings.getKey() +
//					" and isgood = 't'" +
//					" order by country, zip");
//			HashMap params = new HashMap();
//			JRResultSetDataSource jrdata = new JRResultSetDataSource(rs);
//			JasperPrint jprint = net.sf.jasperreports.engine.JasperFillManager.fillReport(in, params, jrdata);
//			net.sf.jasperreports.view.JasperViewer.viewReport(jprint, false);
//		} finally {
//			// try { rs.close(); } catch(Exception e) {}
//			try { in.close(); } catch(Exception e) {}		
//		}
//
////	}});
//
//	
//}

}

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
 * JMBT.java
 *
 * Created on July 5, 2005, 7:38 PM
 */

package offstage.school.tuition;

import citibob.sql.*;
import java.util.*;
import citibob.sql.pgsql.*;
import citibob.types.KeyedModel;
import citibob.util.DayConv;
import citibob.wizard.TypedHashMap;
import offstage.FrontApp;
import offstage.accounts.gui.AccountsDB;
import offstage.schema.Actrans2Schema;

/**
 * A bunch of "stored procedures" for the JMBT database.  This is because
 * PostgreSQL stored procedures are nearly useless.
 * @author citibob
 */
public class TuitionCalc {

// ==========================================================

int termid;
SqlDate date;
String payerIdSql;
TuitionData tdata;
MyTuitionCon tcon;
RBPlanSet rbPlanSet;
FrontApp app;
//DayConv dconv;
//Calendar cal;

/** @param payerIdSql IdSql that selects the payers for which we want to recalc tuition. */
public TuitionCalc(FrontApp app, int termid)
{
	this.termid = termid;
	this.app = app;
	date = new SqlDate(app.timeZone(), true);
//	dconv = new DayConv(app.timeZone());
}

public void setPayerIDs(String payerIdSql)
	{ this.payerIdSql = payerIdSql; }
public void setPayerIDs(int payerID)
	{ setPayerIDs("select " + SqlInteger.sql(payerID) + " as id"); }
public void setPayerIDs(Set<Integer> payerIDs)
	{ setPayerIDs(" select entityid from entities where entityid in " + SQL.intList(payerIDs)); }
public void setAllPayerIDs()
{
	setPayerIDs(
//		" select distinct payerid from termregs tr, entities es" +
//		" select distinct payerid from termregs tr, rels_o2m r, entities es" +
		" select distinct r.entityid0 as payerid\n" +
		" from termregs tr, rels_o2m r, entities es" +
		" where tr.groupid = " + SqlInteger.sql(termid) +
		" and r.temporalid = tr.groupid and r.entityid1 = tr.entityid\n" +
		" and r.relid = (select relid from relids where name = 'payerof')\n" +
		" and tr.entityid = es.entityid");
}


public void recalcTuition(SqlRun str)
{
	tdata = new TuitionData(str, termid, payerIdSql, date.getTimeZone());
	str.execUpdate(new UpdTasklet2() {
	public void run(SqlRun str) throws Exception {
		if (tdata.calcTuition()) {
			recalcTuition();
			String sql = writeTuitionSql();
			str.execSql(sql);
		}
	}});
}

// ==========================================================================

void recalcTuition()
throws InstantiationException, IllegalAccessException, ClassNotFoundException
{
	rbPlanSet = (RBPlanSet)app.siteCode().loadClass(tdata.rbPlanSetClass).newInstance();
//	rbPlanSet = (RBPlanSet)Class.forName(tdata.rbPlanSetClass).newInstance();
	tcon = new MyTuitionCon();

	// Go through family by family
	for (Payer payer : tdata.payers.values()) {
		RBPlan rbplan = rbPlanSet.getPlan(payer.rbplan);
System.out.println("Getting rbplan " + payer.rbplan + " = " + rbplan);
		rbplan.getRatePlan().setTuition(tcon, payer);
	}
}

/** @return SQL to update tuition records */
String writeTuitionSql()
{
	StringBuffer sql = new StringBuffer();
	tcon.sql = sql;
	
	// Produce the SQL to store this tuition calculation
	for (Payer payer : tdata.payers.values()) {
		RBPlan rbplan = rbPlanSet.getPlan(payer.rbplan);
		for (Student ss : payer.students) {
			double tuition = ss.getTuition();
			double regfee = ss.getRegFee();
				
			// Main tuition in student record
			sql.append(
				" update termregs" +
				" set defaulttuition=" + TuitionData.money.sql(ss.defaulttuition) + "," +
				" tuition=" + TuitionData.money.sql(tuition) + "," +
				" defaultregfee=" + TuitionData.money.sql(ss.defaultregfee) + "," +
				" regfee=" + TuitionData.money.sql(regfee) +
				" where groupid = " + SqlInteger.sql(tdata.termid) +
				" and entityid = " + SqlInteger.sql(ss.entityid) +
				";\n");

			// Don't mess with accounts if there's no tuition to be charged
			if (tuition + regfee == 0) continue;

			rbplan.getBillingPlan().billAccount(tcon, ss);
		}
	}
	
	return sql.toString();
}

//public static void main(String[] args) throws Exception
//{
//	citibob.sql.ConnPool pool = offstage.db.DB.newConnPool();
//	offstage.FrontApp fapp = new offstage.FrontApp(pool,null);
//	TuitionCalc.w_recalc(fapp.getBatchSet(), fapp.getTimeZone(), 346,
//		"select 24822 as id");
//	fapp.getBatchSet().runBatches();
//}
// ===============================================================
class MyTuitionCon implements TuitionCon
{
	
StringBuffer sql;	// Left null; needs to be set from outside = new StringBuffer();

/** Sets the calculated tuition for a particular student */
public void setCalcTuition(Student student, double tuition, double regfee, String desc)
{
	student.defaulttuition = tuition;
	student.defaultregfee = regfee;
	student.tuitionDesc = desc;
}
	
/** Adds a tuition record to be written to the database. */
public void addTransaction(Student student,
int duedateDN, double amount, String description)
{
	// Don't add nuisance transactions (like $0 scholarships)
	if (amount == 0.0D) return;

	java.util.Date duedate = new Date(DayConv.toMS(duedateDN, date.getCalendar()));
//		dconv.toDate(duedateDN);
	int payerid = student.payerid;
	int studentid = student.entityid;
	
	KeyedModel transtypes = app.schemaSet().getKeyedModel("actrans2", "actranstypeid");
	int schoolid = ((Actrans2Schema)app.getSchema("actrans2")).actypeKmodel.getIntKey("school");
	TypedHashMap optional = new TypedHashMap();
		optional.put("description", description);
		optional.put("studentid", studentid);
		optional.put("termid", termid);
	sql.append(AccountsDB.w_actrans2_insert_sql(app, payerid, "billed",
		schoolid, "tuition", duedate, optional,
		new int[] {0}, new double[] {-amount}));
}

/** Gives us the data set, in case we need it. */
public TuitionData getData() { return tdata; }

}
}

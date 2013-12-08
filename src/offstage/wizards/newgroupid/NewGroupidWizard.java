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
package offstage.wizards.newgroupid;
/*
 * NewRecordWizard.java
 *
 * Created on October 8, 2006, 11:27 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

import citibob.sql.ansi.SqlDay;
import citibob.swing.html.*;
import citibob.wizard.*;
import offstage.wizards.*;
import citibob.sql.pgsql.*;
import citibob.util.Day;
import java.awt.Component;

/**
 *
 * @author citibob
 */
public class NewGroupidWizard extends OffstageWizard {

	/*
addState(new State("", "", "") {
	public HtmlWiz newWiz(WizState.Context con)
		{ return new }
	public void process(citibob.sql.SqlRun str)
	{
		
	}
});
*/
	
public NewGroupidWizard(offstage.FrontApp xfapp, Component component)
{
	super("New Category", xfapp, component);
// ---------------------------------------------
//addState(new State("init", "init", "init") {
//	public HtmlWiz newWiz(WizState.Context con) throws Exception
//		{ return new InitWiz(frame); }
//	public void process(citibob.sql.SqlRun str) throws Exception
//	{
//		String s = v.getString("type");
//		if (s != null) state = s;
//	}
//});
//// ---------------------------------------------
//addState(new State("person", "init", null) {
addStartState(new AbstractWizState("grouplist", null, "catname") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new GroupListWiz(frame); }
	public void process(Wizard.Context con) throws Exception
	{
		String table = v.getString("submit");
		v.put("table", table);
		if ("donationids".equals(table)) stateName = "donationname";
		else if ("eventids".equals(table)) stateName = "eventname";
		else if ("ticketeventids".equals(table)) stateName = "ticketname";
		else stateName = "catname";
	}
});
// ---------------------------------------------
// Query for name of new category
addState(new AbstractWizState("catname", "grouplist", "finished") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new CatNameWiz(frame, v.getString("table")); }
	public void process(Wizard.Context con) throws Exception
	{
		String catname = v.getString("catname");
		if (catname == null || "".equals(catname)) return;
		String table = v.getString("table");
		String sql =
			" insert into " + table +
			" (name) values (" + SqlString.sql(catname) + ")";
System.out.println(sql);
		con.str.execSql(sql);
		fapp.dbChange().fireTableWillChange(con.str, table);
	}
});
// ---------------------------------------------
// Query for name of new donation category
addState(new AbstractWizState("donationname", "grouplist", "finished") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new DonationNameWiz(frame); }
	public void process(Wizard.Context con) throws Exception
	{
		String catname = v.getString("catname");
		if (catname == null || "".equals(catname)) return;
		int fiscalyear = (int)v.getLong("fiscalyear");
		String sql =
			" insert into donationids" +
			" (name, fiscalyear) values (" +
			SqlString.sql(catname) + ", " + SqlInteger.sql(fiscalyear) + ")";
System.out.println(sql);
		con.str.execSql(sql);
		fapp.dbChange().fireTableWillChange(con.str, "donationids");
	}
});
// ---------------------------------------------
// Query for name of new event category
addState(new AbstractWizState("eventname", "grouplist", "finished") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new EventNameWiz(frame, app); }
	public void process(Wizard.Context con) throws Exception
	{
		String catname = v.getString("catname");
		if (catname == null || "".equals(catname)) return;
		Day date = (Day)v.get("date");
		SqlDay sqlday = new SqlDay();
		String sql =
			" insert into eventids" +
			" (name, date) values (" +
			SqlString.sql(catname) + ", " + sqlday.toSql(date) + ")";
System.out.println(sql);
		con.str.execSql(sql);
		fapp.dbChange().fireTableWillChange(con.str, "eventids");
	}
});
// ---------------------------------------------
// Query for name of new event category
addState(new AbstractWizState("ticketname", "grouplist", "finished") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new EventNameWiz(frame, app); }
	public void process(Wizard.Context con) throws Exception
	{
		String catname = v.getString("catname");
		if (catname == null || "".equals(catname)) return;
		Day startdate = (Day)v.get("startdate");
		SqlDay sqlday = new SqlDay();
		String sql =
			" insert into eventids" +
			" (name, startdate) values (" +
			SqlString.sql(catname) + ", " + sqlday.toSql(startdate) + ")";
System.out.println(sql);
		con.str.execSql(sql);
		fapp.dbChange().fireTableWillChange(con.str, "ticketeventids");
	}
});
// ---------------------------------------------
// Query for name of new donation category
addState(new AbstractWizState("finished", null, null) {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new FinishedWiz(frame); }
	public void process(Wizard.Context con) throws Exception
	{
	}
});
// ---------------------------------------------
}
// =========================================================================






}

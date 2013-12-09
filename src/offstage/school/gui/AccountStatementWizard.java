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

package offstage.school.gui;

import java.sql.*;
import citibob.types.*;
import citibob.swing.html.*;
import citibob.swing.typed.TypedWidget;
import citibob.wizard.*;
import java.util.Calendar;
import offstage.wizards.*;

/**
 *
 * @author citibob
 */
class AccountStatementWiz extends HtmlWiz {

public AccountStatementWiz(java.awt.Frame owner, citibob.app.App app)
throws org.xml.sax.SAXException, java.io.IOException, SQLException
{
	super(owner, app.swingerMap());
	setSize(600,460);

	Calendar cal = Calendar.getInstance(app.timeZone());
	java.util.Date now = new java.util.Date();

	JDate jdate = new JDate(cal.getTimeZone(), false);
	TypedWidget soft_start_dt = app.swingerMap().newWidget(jdate);
	addWidget("soft_start_dt", soft_start_dt);
	TypedWidget hard_start_dt = app.swingerMap().newWidget(jdate);
	addWidget("hard_start_dt", hard_start_dt);

	cal.setTime(now);
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	now = cal.getTime();
	cal.set(Calendar.MONTH, Calendar.JULY);
	cal.set(Calendar.DAY_OF_MONTH, 1);
	java.util.Date then = cal.getTime();
	if (then.getTime() >= now.getTime())
		cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
	hard_start_dt.setValue(cal.getTime());
	cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) - 1);
	soft_start_dt.setValue(cal.getTime());


	TypedWidget asof_dt = app.swingerMap().newWidget(jdate);
	asof_dt.setValue(now);
	addWidget("asof_dt", asof_dt);

	cal.set(Calendar.YEAR, cal.get(Calendar.YEAR)+4);
	TypedWidget end_dt = app.swingerMap().newWidget(new JDate(cal.getTimeZone(), false));
	end_dt.setValue(cal.getTime());
	addWidget("end_dt", end_dt);
	loadHtml();
}
}

public class AccountStatementWizard extends OffstageWizard {


public AccountStatementWizard(offstage.FrontApp xfapp, java.awt.Component comp)
{
	super("Run Account Statements", xfapp, comp);
// ---------------------------------------------
addStartState(new AbstractWizState("AccountStatement ", null, "<end>") {
	public HtmlWiz newWiz(Wizard.Context con) throws Exception
		{ return new AccountStatementWiz(frame, fapp); }
	public void process(Wizard.Context con) throws Exception
		{}
});

}

}
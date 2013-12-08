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
 * StudentSchedule.java
 *
 * Created on August 9, 2007, 12:51 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.reports;

import citibob.reports.Reports;
import java.io.*;
import java.util.*;
import com.pdfhacks.*;
import citibob.sql.pgsql.*;
import java.sql.*;
import offstage.*;
import citibob.sql.*;
import citibob.swing.table.*;
import citibob.text.*;
import citibob.reports.*;

/**
 *
 * @author citibob
 */
public class StudentConfirmationLetter
{

public static void viewReport(SqlRun str, final ReportsApp app,
final int termid, int entityid)
throws Exception
{
	
	String idSql;
	if (entityid < 0) {
		idSql =
			" select xx.entityid\n" +
			" from (\n" +
			"   select distinct r.entityid0 as entityid\n" +
			"   from termregs tr, rels_o2m r\n" +
			" 	where tr.groupid = " + termid + "\n" +
			" 	and tr.entityid = r.entityid1\n" +
//			" 	select distinct s.parent1id as entityid\n" +
//			" 	from termregs tr, entities s\n" +
//			" 	where tr.groupid = " + termid + "\n" +
//			" 	and tr.entityid = s.entityid\n" +
			" ) xx, persons p\n" +
			" where xx.entityid = p.entityid\n" +
			" order by p.lastname, p.firstname";
	} else {
		idSql =
			" select entityid0\n" +
			" from rels_o2m r\n" +
			" where r.entityid1 = " + entityid +
			" and r.relid = (select relid from relids where name = 'parent1of')";
//		idSql = "select parent1id from entities e where e.entityid = " + entityid;
	}
	String sql = LabelReport.getSql(idSql, null);
	
	str.execSql(sql, new RsTasklet2() {
	public void run(SqlRun str, ResultSet rs) throws Exception {
		Reports rr = app.reports();
		rr.viewJodPdfs(rr.toJodList(rs,
			new String[][] {{"line1", "line2", "line3", "city", "state", "zip", "firstname"}}),
			null, "StudentConfirmationLetter.odt", termid);
	}});
}

}

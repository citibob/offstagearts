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
 * JDate.java
 *
 * Created on May 14, 2003, 8:52 PM
 */

package offstage.swing.typed;

import citibob.sql.RsTasklet2;
import citibob.sql.SqlRun;
import citibob.sql.pgsql.*;
import citibob.swing.typed.JTypedLabelDB;
import citibob.swing.typed.TextTypedWidget;
import citibob.text.DBFormat;
import citibob.text.SFormat;

public class EntityIDLabel extends JTypedLabelDB
{

// ---------------------------------------------------------------
// Must override stuff in TextTypedWidget
public void setJType(SqlRun str)
{
	super.setJType(str, new EntityIDDBFormat(this));
}

// =========================EntityIDFormatter=============
protected static class EntityIDDBFormat implements DBFormat
{

TextTypedWidget tw;
	
public EntityIDDBFormat(TextTypedWidget tw)
{
	this.tw = tw;
}
	
public void setDisplayValue(SqlRun str, final Object value)
{
	tw.setHorizontalAlignment(SFormat.LEFT);
	if (value == null) {
		tw.setDisplayValue(null, "");
		return;
	}
	String sql =
		" select " +
			" (case when firstname is null then '' else firstname || ' ' end ||" +
			" case when middlename is null then '' else middlename || ' ' end ||" +
			" case when lastname is null then '' else lastname end ||" +
//			" case when orgname is null then '' else ' (' || orgname || ')' end" +
			" case when obsolete then ' **(DELETED)**' else '' end" +
			" ) as name" +
		" from entities" +
		" where entityid = " + SqlInteger.sql((Integer)value);
	str.execSql(sql, new RsTasklet2() {
	public void run(citibob.sql.SqlRun str, java.sql.ResultSet rs) throws Exception {
		if (rs.next()) {
			tw.setDisplayValue(value, rs.getString("name"));
		} else {
			tw.setDisplayValue(value, "");
		}
	}});
}

}

}


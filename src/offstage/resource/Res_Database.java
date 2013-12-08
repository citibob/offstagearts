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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package offstage.resource;

import citibob.resource.DbbCreator;
import citibob.resource.DbbResource;
import citibob.resource.DbbUpgrader;
import citibob.resource.ResSet;
import citibob.sql.ConnPool;
import citibob.sql.SqlRun;
import java.sql.Connection;

/**
 *
 * @author citibob
 */
public class Res_Database extends DbbResource
{

public Res_Database(ResSet rset)
{
	super(rset, "general", "database.sql");

	add(new DbbCreator(this, 1));
	add(new DbbUpgrader(this, 1, 45, true));
	add(new DbbUpgrader(this, 45, 50, true));
	
	add(new DbbUpgrader(this, 50, 59, true) {
	public void upgrade(SqlRun str, final ConnPool pool, int uversionid0, final int uversionid1)
	throws Exception {
		super.upgrade(str, pool, uversionid0, uversionid1);
		final String[] tables = new String[] {"actrans_old", "cashpayments_old",
			"ccpayments_old", "checkpayments_old", "termids_old", "termregs_old",
			"tuitiontrans_old", "invoices"};
		for (String table : tables) dropTable(pool, table, true);
	}});
	
	add(new DbbUpgrader(this, 59, 60, true) {
	public void upgrade(SqlRun str, final ConnPool pool, int uversionid0, final int uversionid1)
	throws Exception {
		super.upgrade(str, pool, uversionid0, uversionid1);
		dropTable(pool, "entities_school", false);
	}});
	
	add(new DbbUpgrader(this, 60, 78, true));
	add(new DbbUpgrader(this, 78, 86, true));
	add(new DbbUpgrader(this, 86, 88, true));
	add(new DbbUpgrader(this, 88, 96, true));
	add(new DbbUpgrader(this, 96, 97, true));
	add(new DbbUpgrader(this, 97, 100, true));
	add(new DbbUpgrader(this, 100, 104, true));
	add(new DbbUpgrader(this, 104, 106, true));
	add(new DbbUpgrader(this, 106, 111, true));
	add(new DbbUpgrader(this, 111, 123, true));
	add(new DbbUpgrader(this, 123, 129, true));
	add(new DbbUpgrader(this, 129, 132, true));
	add(new DbbUpgrader(this, 132, 138, true));
	add(new DbbUpgrader(this, 138, 158, true));
	add(new DbbUpgrader(this, 158, 201, true));
	add(new DbbUpgrader(this, 201, 202, true));
	add(new DbbUpgrader(this, 202, 203, true));
	add(new DbbUpgrader(this, 203, 212, true));
	add(new DbbUpgrader(this, 212, 231, true));
	add(new DbbUpgrader(this, 231, 241, true));
	add(new DbbUpgrader(this, 241, 242, true));
	add(new DbbUpgrader(this, 242, 243, true));
//	add(new DbbUpgrader(this, 231, 234, true));
	

//	add(new DbbUpgrader(this, 2, 3, true));
}
	
}

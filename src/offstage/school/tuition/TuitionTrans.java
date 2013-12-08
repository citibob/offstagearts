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

package offstage.school.tuition;

/**
 *
 * @author citibob
 */
public class TuitionTrans {

public int entityid;
public int studentid;
public java.util.Date duedate;
public double tuitionamount;
public double regfeeamount;
public String description;

public TuitionTrans(int entityid, int studentid,
java.util.Date duedate, double tuitionamount, double regfeeamount, String description)
{
	this.entityid = entityid;
	this.studentid = studentid;
	this.duedate = duedate;
	this.tuitionamount = tuitionamount;
	this.regfeeamount = regfeeamount;
	this.description = description;
}
}

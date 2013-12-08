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
 * SchoolModel.java
 *
 * Created on December 9, 2007, 5:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package offstage.school.gui;

import citibob.app.App;
import citibob.jschema.IntKeyedDbModel;
import citibob.jschema.IntsKeyedDbModel;
import citibob.jschema.SchemaBufRowModel;
import citibob.sql.SqlRun;
import citibob.sql.UpdTasklet;
import offstage.db.RelO2mDbModel;

/**
 *
 * @author citibob
 */
public class SchoolModel extends SchoolModelMVC
{

int termID;

public StudentDbModel studentDm;
	public SchemaBufRowModel studentRm;
RelO2mDbModel parent1ofDm;
	public SchemaBufRowModel parent1ofRm;
RelO2mDbModel parent2ofDm;
	public SchemaBufRowModel parent2ofRm;
RelO2mDbModel payerofDm;
	public SchemaBufRowModel payerofRm;
RelO2mDbModel headofDm;
	public SchemaBufRowModel headofRm;

//	public SchemaBufRowModel schoolRm;
IntKeyedDbModel oneTermDm;
	public SchemaBufRowModel oneTermRm;
//	public RBPlanSet rbPlanSet;
public IntsKeyedDbModel termregsDm;
	public SchemaBufRowModel termregsRm;
public IntsKeyedDbModel payertermregsDm;
	public SchemaBufRowModel payertermregsRm;	
public PayerDbModel payerDm;
	public SchemaBufRowModel payerRm;
//public HouseholdDbModel householdDm;
public ParentDbModel parent1Dm;
	public SchemaBufRowModel parent1Rm;
public ParentDbModel parent2Dm;
	public SchemaBufRowModel parent2Rm;

// -------------------------------------------------------------
App app;
public SchoolModel(SqlRun str, App fapp)
{
	this.app = fapp;
	studentDm = new StudentDbModel(fapp);
		studentRm = new SchemaBufRowModel(studentDm.personDb.getSchemaBuf());

	parent1ofDm = new RelO2mDbModel(str, app, RelO2mDbModel.COL_ENTITYID0) {
		public void setKey(Object key) {
			super.entityid1 = (Integer)key;
		}};
	parent1ofDm.setKeys("parent1of", -1, null);

	parent2ofDm = new RelO2mDbModel(str, app, RelO2mDbModel.COL_ENTITYID0) {
		public void setKey(Object key) {
			super.entityid1 = (Integer)key;
		}};
	parent2ofDm.setKeys("parent2of", -1, null);

	payerofDm = new RelO2mDbModel(str, app, RelO2mDbModel.COL_ENTITYID0) {
		public void setKey(Object key) {
			super.entityid1 = (Integer)key;
		}};
	payerofDm.setKeys("payerof", -1, null);

	headofDm = new RelO2mDbModel(str, app, RelO2mDbModel.COL_ENTITYID0_NOTNULL) {
		public void setKey(Object key) {
			super.entityid1 = (Integer)key;
		}};
	headofDm.setKeys("headof", -1, null);

	
	str.execUpdate(new UpdTasklet() {
	public void run() {
		parent1ofRm = new SchemaBufRowModel(parent1ofDm.getSchemaBuf());
		parent2ofRm = new SchemaBufRowModel(parent2ofDm.getSchemaBuf());
		payerofRm = new SchemaBufRowModel(payerofDm.getSchemaBuf());
		headofRm = new SchemaBufRowModel(headofDm.getSchemaBuf());

	}});

	payerDm = new PayerDbModel(fapp);
		payerRm = new SchemaBufRowModel(payerDm.personDb.getSchemaBuf());
	parent1Dm = new ParentDbModel(fapp);
		parent1Rm = new SchemaBufRowModel(parent1Dm.personDb.getSchemaBuf());
	parent2Dm = new ParentDbModel(fapp);
		parent2Rm = new SchemaBufRowModel(parent2Dm.personDb.getSchemaBuf());
	oneTermDm = new IntKeyedDbModel(fapp.getSchema("termids"),
		"groupid", fapp.dbChange());
	oneTermDm.setDoInsertKeys(false);
		oneTermRm = new SchemaBufRowModel(oneTermDm.getSchemaBuf());
	termregsDm = new IntsKeyedDbModel(fapp.getSchema("termregs"),
		new String[] {"groupid", "entityid"}, true);
		termregsRm = new SchemaBufRowModel(termregsDm.getSchemaBuf());

	payertermregsDm = new IntsKeyedDbModel(fapp.getSchema("payertermregs"),
		new String[] {"termid", "entityid"}, true);
		payertermregsRm = new SchemaBufRowModel(payertermregsDm.getSchemaBuf());

}

public void setTermID(Integer TermID)
	{ setTermID(TermID == null ? -1 : TermID.intValue()); }
public void setTermID(int newTermID)
{
	int oldTermID = termID;
	this.termID = newTermID;
	
	SqlRun str = app.sqlRun();
	oneTermDm.doUpdate(str);
	payerofDm.doUpdate(str);
	oneTermDm.setKey(termID);
	payerofDm.setTemporalID(termID);
	oneTermDm.doSelect(str);
	payerofDm.doSelect(str);
			
	if (oldTermID != termID) fireTermIDChanged(oldTermID, termID);
}
public int getTermID()
	{ return termID; }

public Integer getStudentID()
	{ return (Integer)studentRm.get("entityid"); }
public Integer getPayerID()
	{ return (Integer)payerofRm.get("entityid0"); }
//	{ return (Integer)termregsRm.get("payerid"); }
public Integer getParent1ID()
	{ return (Integer)parent1ofRm.get("entityid0"); }
//	{ return (Integer)studentRm.get("parent1id"); }
public Integer getParent2ID()
	{ return (Integer)parent2ofRm.get("entityid0"); }
//	{ return (Integer)studentRm.get("parent2id"); }

}

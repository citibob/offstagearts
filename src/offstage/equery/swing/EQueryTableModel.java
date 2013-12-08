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
 * EClauseTableModel.java
 *
 * Created on June 23, 2005, 10:34 PM
 */

package offstage.equery.swing;

import static offstage.equery.QuerySchema.*;
import citibob.util.*;
import citibob.swing.*;
import citibob.swing.table.*;
import citibob.swing.typed.*;
import java.sql.*;
import javax.swing.table.*;
import javax.swing.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import citibob.jschema.*;
import offstage.equery.*;
import java.io.*;
import citibob.types.*;
import offstage.equery.compare.Comp;

/**
 *
 * @author citibob
 */
public class EQueryTableModel extends AbstractJTypeTableModel
implements EClauseTableConst
{

public static final int C_ADDSUB = 0;
public static final int C_NAME = 1;

public static final String S_ADDSUB = "Add/Sub";
public static final String S_NAME = "Name";


ArrayList rows;		// Description of what goes in each row
QuerySchema schema;			// Info on valid columns in the query
EQuery query;					// The query we're editing
static JType[] jtypesQuery;
static {
	jtypesQuery = new JType[] {
		new JEnum(new KeyedModel(
			new Object[] {new Integer(EClause.ADD), new Integer(EClause.SUBTRACT), new Integer(EClause.ZERO)},
			new Object[] {"+", "-", "0"})),
		JavaJType.jtString,
		new JavaJType(IntRange.class)};
}
public EQueryTableModel(QuerySchema schema)
{
	rows = new ArrayList();
	this.schema = schema;
}
// ------------------------------------------------------
static class RowSpec {
//	public Clause clause;
//	public Element element;
	public int cix;
	public int eix;
	public RowSpec(int cix, int eix) {
		this.cix = cix;
		this.eix = eix;
	}
	public boolean isDummy() { return (cix < 0); }
	public boolean isClause() { return (cix >= 0 && eix < 0); }
	public boolean isElement() { return (cix >= 0 && eix >= 0); }
}
public Element getElement(RowSpec rs)
	{ return query.getClause(rs.cix).getElement(rs.eix); }
public EClause getClause(RowSpec rs)
	{ return query.getClause(rs.cix); }
// -------------------------------------------------------
RowSpec getRow(int row)
	{ return (RowSpec)rows.get(row); }
void makeRowSpecs()
{
	rows.clear();
	for (int ci=0; ci<query.getClauses().size(); ++ci) {
		EClause c = (EClause)query.getClauses().get(ci);
		rows.add(new RowSpec(ci, -1));
		for (int ei=0; ei<c.getElements().size(); ++ei) {
			Element e = (Element)c.getElements().get(ei);
			rows.add(new RowSpec(ci, ei));
		}
	}
	rows.add(new RowSpec(-1, -1));		// Dummy row...
}
// ------------------------------------------------------
int baseRow(int row)
{
	return row - (getRow(row).eix + 1);
}
// ------------------------------------------------------
/** Sets the value.  Same as method in JFormattedTextField.  Fires a
 * propertyChangeEvent("value") when calling setValue() changes the value. */
public EQuery setSQuery(String squery)
{
	if (squery == null) {	// Set to a blank query
		setQuery(new EQuery());
		return getQuery();
	}
	EQuery eqy = (EQuery)Query.fromXML(schema, squery);
	if (eqy == null) eqy = new EQuery();
	setQuery(eqy);
	return getQuery();
	
//	Object obj = null;
//	try {
//		StringReader fin = new StringReader(squery);
//		EQueryXStream xs = new EQueryXStream();
//		ObjectInputStream ois = xs.createObjectInputStream(fin);
//		obj = ois.readObject();
//	} catch(ClassNotFoundException e) {
//		return null;
////		throw new IOException("Class Not Found in Serialized File");
//	} catch(com.thoughtworks.xstream.io.StreamException se) {
//		return null;
////		throw new IOException("Error reading serialized file");
//	} catch(IOException e) {}	// won't happen
//	
//	if (obj == null) {
//		setQuery(new EQuery());
//	} else if (!(obj instanceof EQuery)) {
//		return null;
////		throw new IOException("Wrong object of class " + obj.getClass() + " found in EQuery file");
//	} else {
//		setQuery((EQuery)obj);
//	}
//	return getQuery();
}

public String getSQuery()
{
	EQuery q = getQuery();
	if (q == null) return null;
	return getQuery().toXML(schema);
//	// Serialize using XML
//	StringWriter fout = new StringWriter();
//	EQueryXStream xs = new EQueryXStream();
//	try {
//		ObjectOutputStream oos = xs.createObjectOutputStream(fout);
//		oos.writeObject(getQuery());
//		oos.close();
//	} catch(IOException e) {}	// won't happen
//	return fout.getBuffer().toString();
}
// ------------------------------------------------------
/** Inserts clause before the row'th row of the overall table.  row = rows.size() if we wish to append to end... */
public void insertClause(int row, EClause clause)
{
	if (row < 0) return;
	RowSpec rs = getRow(row);
	int cix = (rs.isDummy() ? query.getClauses().size() : rs.cix);
	row = baseRow(row);

	// Add to underlying query
//	int cix = (row < rows.size() ? getRow(row).cix : query.getClauses().size());
//	if (cix < 0) cix = query.getClauses().size();		// Append row
	query.insertClause(cix, clause);

	// Add new rows to table model and shift old rows...
	int nele = clause.getElements().size();
	ArrayListUtil.setSize(rows, rows.size() + nele + 1);
	ArrayListUtil.shift(rows, row, nele+1);

	// Insert new RowSpecs
	rows.set(row, new RowSpec(cix, -1));
	for (int i=0; i<nele; ++i) rows.set(row + i + 1, new RowSpec(cix, i));

	// Modify indices in all others
	for (int i=row + nele + 1; i<rows.size()-1; ++i) ++getRow(i).cix;

	this.fireTableRowsInserted(row, row+nele+1-1);
}
//public void appendClause(Clause clause)
//	{ insertClause(rows.size(), clause); }
// ------------------------------------------------------
public void removeClause(int row)
{
	if (row < 0) return;
	RowSpec rs = getRow(row);
	if (!rs.isClause()) return;
	
	row = baseRow(row);
	int cix = getRow(row).cix;
	EClause clause = query.getClause(cix);

	query.removeClause(cix);

	// Remove new rows from table model and shift old rows...
	int nele = clause.getElements().size();
	ArrayListUtil.shift(rows, row + nele+1, -(nele+1));
	ArrayListUtil.setSize(rows, rows.size() - nele - 1);

	// Modify indices in all others
	for (int i=row; i<rows.size(); ++i) --getRow(i).cix;

	this.fireTableRowsDeleted(row, row+nele+1-1);
}
// ------------------------------------------------------
public void removeElement(int row)
{
	if (row < 0) return;
	RowSpec rs = getRow(row);
	if (!rs.isElement()) return;
	
//	row = baseRow(EClause	
	EClause clause = query.getClause(rs.cix);
	clause.removeElement(rs.eix);

	// Remove new rows from table model and shift old rows...
	rows.remove(row);
//	ArrayListUtil.shift(rows, row, -1);
//	ArrayListUtil.setSize(rows, rows.size() - 1);

	// Modify indices in all others
	for (int i=rs.eix; i<clause.getElements().size(); ++i) {
		int irow = row - rs.eix + i;
		--getRow(irow).eix;
	}

	this.fireTableRowsDeleted(row, row);
}
// ------------------------------------------------------
public void insertElement(int row, Element ele)
{
	if (row < 0) return;
	if (row == 0) return;		// Cannot insert an element here....
	
	// Get clause and element to insert before
	RowSpec prs = getRow(row-1);
	int cix = prs.cix;			// Clause to insert into
	int eix = prs.eix+1;	// Element index to insert before
	
	//if (eix < 0) return;		// Cannot insert here...
	
	insertElement(row, cix,eix,ele);
}
public void insertElement(int row, int cix, int eix, Element ele)
{
	if (eix < 0) return;		// Cannot insert here...
	
	// Insert it in the EClause
	EClause clause = query.getClause(cix);
	clause.insertElement(eix, ele);		// Header rows have eix == -1, this is OK

	// Insert it in the table model
	rows.add(row, new RowSpec(cix, eix));
	for (int i=eix+1; i<clause.getElements().size(); ++i) ++getRow(row-eix+i).eix;

	this.fireTableRowsInserted(row, row);
}
// ------------------------------------------------------
public void removeRow(int row)
{
	RowSpec rs = getRow(row);
	if (rs.isElement()) removeElement(row);
	if (rs.isClause()) removeClause(row);
}
// ------------------------------------------------------

public EQuery getQuery()
	{ return query; }
public void setQuery(EQuery query)
{
	this.query = query;
	makeRowSpecs();
	this.fireTableDataChanged();
}

// ===============================================================
// Implementation of TableModel

// --------------------------------------------------
public String getColumnName(int column) 
{
	  switch(column) {
		  case C_COLUMN : return S_COLUMN;
		  case C_COMPARE : return S_COMPARE;
		  case C_VALUE : return S_VALUE;
	  }
	  return null;	
}
// --------------------------------------------------
/** Allow editing of all non-key fields. */
public boolean isCellEditable(int rowIndex, int columnIndex)
{
	if (query == null) return false;
	
	RowSpec rs = getRow(rowIndex);
	if (rs.isDummy()) return false;
	if (rs.isClause()) {
		return true;
//		return (columnIndex < 2);
	} else {
		return (query != null);
	}
}
// --------------------------------------------------
/** Set entire row.  Normally, setValueAt() will be called with a modified
version of the object retrieved from getValueAt(). */
public void setValueAt(Object val, int row, int col)
{
	RowSpec rs = getRow(row);
	if (rs.isDummy()) return;
	if (rs.isClause()) {
		if (query == null) return;
		EClause clause = query.getClause(rs.cix);
		switch(col) {
			case C_ADDSUB : clause.type = ((Integer)val).intValue(); break;
			case C_NAME : clause.name = (String)val; break;
			case C_VALUE : {
				if (val == null) {
					clause.minDups = null;
					clause.maxDups = null;
				} else {
					IntRange range = (IntRange)val;
					clause.minDups = range.min;
					clause.maxDups = range.max;
				}
			} break;
		}
		// Redisplay the entire row!
		this.fireTableCellUpdated(row, col);		
	} else {	// Body (ElemeEClausew
		EClause c = query.getClause(rs.cix);
		Element el = c.getElement(rs.eix);
		//EQuery.Element el = getElement(row);
		if (el == null) return;
		switch(col) {
			case C_COLUMN :
				JType oldCompareType = getJType(row, C_COMPARE);
				JType oldValueType = getJType(row, C_VALUE);

				ColName cn = (ColName)val;
				el.setColName(cn);
				this.fireTableCellUpdated(row, C_COLUMN);

				// Update other cols if needed...
				if (oldCompareType == null || !oldCompareType.equals(getJType(row, C_COMPARE))) {
					el.setComparator(QuerySchema.eqCP);
					this.fireTableCellUpdated(row, C_COMPARE);
					this.fireTableCellUpdated(row, C_VALUE);
				}
				if (oldValueType == null || !oldValueType.equals(getJType(row, C_VALUE))) {
					QuerySchema.Col scol = schema.getCol(el.getColName());
					if (scol == null || scol.col == null) el.value = null;
					else el.value = scol.col.getDefault();
					this.fireTableCellUpdated(row, C_VALUE);
				}

			break;
			case C_COMPARE :
				el.setComparator((Comp)val);
//				this.fireTableRowsUpdated(row, row);
				this.fireTableCellUpdated(row, C_COMPARE);
				this.fireTableCellUpdated(row, C_VALUE);
			break;
			case C_VALUE :
				el.value = val;
				this.fireTableCellUpdated(row, col);
			break;
		}
	}	
}
// --------------------------------------------------
	public int getRowCount()
	  { return rows.size(); }
	public int getColumnCount()
	  { return 3; }
public Object getValueAt(int row, int column)
{
	RowSpec rs = getRow(row);
	if (rs.isDummy()) {
		switch(column) {
			case C_COLUMN : return "Append";
		}
		return null;
	} else if (rs.isClause()) {
		EClause c = query.getClause(rs.cix);
		switch(column) {
			case C_ADDSUB : return new Integer(c.type);
			case C_NAME : return c.name;
			case C_VALUE : {
				IntRange range = new IntRange();
					range.min = c.minDups;
					range.max = c.maxDups;
				return range;
			}
		}
		return null;
	} else {
//System.err.println(row + ", " + column);
		Element el = getElement(rs);
		if (el == null) return null;
		switch(column) {
			case C_COLUMN : return el.getColName();
			case C_COMPARE : return el.getComparator();
			case C_VALUE : return el.value;
		}
		return null;
		
	}
}
//public Class getColumnClass(int column) 
//{
//	switch(column) {
//		case C_ADDSUB : return Integer.class;
//		case C_NAME : return String.class;
//	}
//	return String.class;
//}
// ===============================================================
// Implementation of CitibobTableModel (prototype stuff)
// ===============================================================
// Implementation of JTypeTableModel (prototype stuff)
public JType getJType(int row, int column)
{
	RowSpec rs = getRow(row);
	if (rs.isDummy()) {
		return null;
	} if (rs.isClause()) {
//if (column == C_VALUE) {
//	System.out.println("hoi");
//}
		return jtypesQuery[column];	
	} else {
		if (column == C_COLUMN) return schema.getColsJType();
	
		Element el = getElement(rs);
		if (el == null) return null;
		QuerySchema.Col col = schema.getCol(el.getColName());
		if (col == null) return null;
		if (column == C_COMPARE) return col.comparators;
		if (col.col == null) return null;
		if (column == C_VALUE) {
//			Comp comp = el.getComparator();
//			if (comp == inFileCP || comp == ninFileCP) {
////				return JavaJType.jtString;
//				return jFile;
//			} else if (comp == inCP_JEnum || comp == ninCP_JEnum) {
//				return new JEnumMulti(col)
//			} else {
				return schema.getType(getClause(rs), el, col);
//				return col.col.getType();
//					Clause clause, Element el, SqlCol col
//			}
		}
		return null;
	}
}

///** Return JType for a cell --- used to set up renderers and editors */
//public JType getJType(int row, int colIndex)
//{ return null; }
// ===============================================================
}

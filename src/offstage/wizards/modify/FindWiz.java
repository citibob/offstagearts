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
 * NewRecordWiz2.java
 *
 * Created on October 8, 2006, 6:08 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package offstage.wizards.modify;

import citibob.swing.html.HtmlWiz;
import citibob.swing.html.HtmlWiz;
import java.util.*;
import citibob.swing.typed.*;
import citibob.swing.html.*;
import offstage.types.*;
import javax.swing.*;
import offstage.wizards.*;
import offstage.*;
import offstage.gui.*;
import citibob.wizard.*;
import citibob.sql.*;

/**
 *
 * @author citibob
 */
public class FindWiz extends HtmlWiz {

///** Should this Wiz screen be cached when "Back" is pressed? */
//public boolean getCacheWiz() { return false; }
SearchViewer listView;

/**
 * Creates a new instance of NewRecordWiz2 
 */
public FindWiz(java.awt.Frame owner, SqlRun str, FrontApp fapp)
throws org.xml.sax.SAXException, java.io.IOException, java.sql.SQLException
{
	super(owner);

	listView = new SearchViewer();
	listView.initRuntime(str, fapp);
//	html.getMap().put("idlistviewer", listView);
	html.addWidget("searchview", listView);
	this.setSize(new java.awt.Dimension(750, 550));
	loadHtml();
}

public int getEntityID()
{ return listView.getDisplayedEntityID(); }

}

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

package offstage;

import com.Ostermiller.util.CSVParser;
import com.jangomail.api.JangoMail;
import com.jangomail.api.JangoMailLocator;
import com.jangomail.api.JangoMailSoap;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import javax.xml.rpc.ServiceException;

/**
 *
 * @author citibob
 */
public class Jango {

	public JangoMailSoap soap;
	public String usr;
	public String pwd;
	
	public static final DateFormat groupNameFmt = new SimpleDateFormat("yyMMdd-HHmmss");
	static {
		groupNameFmt.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public Jango(Properties props)
	throws ServiceException, MalformedURLException
	{
		// Get JangoMail Handle
		JangoMail service = new JangoMailLocator();
		soap = service.getJangoMailSoap(
			new URL("https://api.jangomail.com/api.asmx"));

		usr = props.getProperty("custmail.jango.user");
		pwd = props.getProperty("custmail.jango.password");
	}
	
	public void deleteOldGroups()
	throws RemoteException, IOException, ParseException
	{
		String groups = soap.groups_GetList_String(usr, pwd, "n", ",", "");
		CSVParser csv = new CSVParser(new StringReader(groups));
		long nowMS = System.currentTimeMillis();
		String[] ll;
		while ((ll = csv.getLine()) != null) {
			// Parse the date from the group name
			String name = ll[1];
			int dash = name.indexOf('-');
			Date dtime = groupNameFmt.parse(name.substring(dash+1));
			
			// Remove old groups
			if (nowMS - dtime.getTime() > 2*7*86400*1000L) {
				int id = Integer.parseInt(ll[0]);
System.out.println("Deleting JangoMail group id " + id + " (" + name + ")");
				soap.deleteGroupByID(usr, pwd, id);
			}
		}
	}

}

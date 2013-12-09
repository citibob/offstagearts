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
package offstage.launch;

import citibob.config.ConfigMaker;
import citibob.config.MultiConfigMaker;
import java.io.File;
import java.lang.Exception;
import java.sql.*;
import offstage.FrontApp;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
//import com.jgoodies.looks.plastic.theme.*;

/**
 *
 * @author citibob
 *
 * The main class used to run (without arguments) in the "new" deployment system
 * (aka Dec 7, 2013)
 * java -cp target/executable-netbeans.dir/offstagearts-1.10.23.jar offstage.launch.DeployLaunch
 *
 */
public class TestProgram {

	public static boolean exitAfterMain = false;
	public static void main(String[] args) throws Exception
    {
		ConfigMaker configMaker;
		File launcherFile;

		// Get current directory
		// See: http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
		File curjar = new File(DeployLaunch.class.getProtectionDomain()
			.getCodeSource().getLocation().toURI().getPath());
		File curdir = curjar.getParentFile();
		launcherFile = new File("/export/home/citibob/db.offstagearts.org/launchers/offstagearts-ballettheatre.jar");

		configMaker = new MultiConfigMaker(new Object[]{launcherFile});

		// Null = load from database
		// <file> = use a file
		// "<none>" = Don't use any site code
		// Use the file "sitecode.jar" if it exists.
		File sitecodeFile = new File("/export/home/citibob/ant/oa_jmbt/dist/oa_jmbt.jar");
		String siteCodeFileName = (sitecodeFile.exists() ? sitecodeFile.toString() : null);

		System.out.println("********************** DeployLaunch, args.length = " + args.length);
		System.out.println("Launcher = " + launcherFile + ", siteCode = " + siteCodeFileName);

//		FrontApp.launch(configMaker, siteCodeFileName);
		final FrontApp app = new FrontApp(configMaker); //new File("/export/home/citibob/svn/offstage/config"));


		{
			Connection dbb = app.pool().checkout();
			PySystemState state = new PySystemState();
//			state.path.append(new PyString("/export/home/citibob/opt/jython2.5.2/Lib"));
			System.out.println(state.path);
			PythonInterpreter interp = new PythonInterpreter(null, state);
//			interp.set("db", new com.ziclix.python.sql.PyConnection(dbb));
			interp.set("conn", dbb);
			interp.set("statements_txt", "statements1.txt");
			interp.set("labels_txt", "labels1.txt");
			interp.exec("import account_statements");
			interp.exec("account_statements.run_report_java(conn, 597, statements_txt, labels_txt)");
//			interp.exec("import offstage.reports.account_statements");
//			interp.exec("offstage.reports.account_statements.run_report_java()");
			interp.cleanup();
		}
		
		// Try some JDBC
		java.sql.Connection conn = null;
		try {
			conn = app.pool().checkout();
			String sql = "select name from termids";
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.getString("name"));
			}

			// Try some Jython JDBC
			PythonInterpreter interp = new PythonInterpreter();
			interp.set("db", new com.ziclix.python.sql.PyConnection(conn));
			interp.exec("c = db.cursor()");
			interp.exec("c.execute('select name from termids')");
			interp.exec("for a in c.fetchall() :\n" +
				"    print '****', a");
			interp.exec("import sys");
			interp.exec("print sys.path");
			interp.exec("import testpython");
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (conn != null) app.pool().checkin(conn);
		}
		System.exit(0);
    }

}

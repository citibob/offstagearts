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
import citibob.config.DialogConfigMaker;
import citibob.config.MultiConfigMaker;
import java.io.File;
import offstage.FrontApp;
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
public class DeployLaunch {

	public static boolean exitAfterMain = false;
	public static void main(String[] args) throws Exception
    {
		ConfigMaker cmaker;
		File launcherFile;

		// Get current directory
		// See: http://stackoverflow.com/questions/320542/how-to-get-the-path-of-a-running-jar-file
		File curjar = new File(DeployLaunch.class.getProtectionDomain()
			.getCodeSource().getLocation().toURI().getPath());
		File curdir = curjar.getParentFile();
		launcherFile = new File(curdir, "launcher.jar");

		cmaker = new MultiConfigMaker(new Object[]{launcherFile});

		// Null = load from database
		// <file> = use a file
		// "<none>" = Don't use any site code
		// Use the file "sitecode.jar" if it exists.
		File sitecodeFile = new File(curdir, "sitecode.jar");
		String siteCodeFileName = (sitecodeFile.exists() ? sitecodeFile.toString() : null);

		System.out.println("********************** DeployLaunch, args.length = " + args.length);
		System.out.println("Launcher = " + launcherFile + ", siteCode = " + siteCodeFileName);

		FrontApp.launch(cmaker, siteCodeFileName);
    }

}

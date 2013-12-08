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

import com.Ostermiller.util.CSVParser;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import offstage.school.tuition.HourlyRatePlan;

/**
 * Looks up rates in an embedded .CSV file.
 * @author citibob
 */
public class TableRatePlan extends HourlyRatePlan
{

int[] timeS;
double[] rateY;

public TableRatePlan(double siblingDiscount, double termRegFee, URL tableURL) throws IOException
{
	this.siblingDiscount = siblingDiscount;
	this.termRegFee = termRegFee;
	
	// Read the CSV file
	CSVParser csv = new CSVParser(tableURL.openStream());
	
	// Get the headers and the cols we're interested in
	String[] headers = csv.getLine();	
	int hoursCol = -1;
	int tuitionCol = -1;
	for (int i=0; i<headers.length; ++i)
		if ("hours".equals(headers[i])) { hoursCol = i; break; }
	for (int i=0; i<headers.length; ++i)
		if ("tuition".equals(headers[i])) { tuitionCol = i; break; }
	
	
	// Read the rest of the file
	List<String[]> lines = new ArrayList();
	for (;;) {
		String[] ll = csv.getLine();
		if (ll == null) break;
		if (ll.length == 0) continue;
		lines.add(ll);
	}
	
	// close it
	csv.close();
	
	// Make arrays out of it
	int nrow = lines.size();
	timeS = new int[nrow+2];
	rateY = new double[nrow+2];
	int i=0;
	timeS[i] = 0;		// Sentinel
	rateY[i] = 0;
	++i;
	for (String[] ll : lines) {
		timeS[i] = (int)Math.round(Double.parseDouble(ll[hoursCol]) * 3600.0D);
		rateY[i] = Double.parseDouble(ll[tuitionCol]);
		++i;
	}
	timeS[i] = timeS[i-1] * 2;		// Sentinel
	rateY[i] = rateY[i-1] * 2;
}

public double getPrice(int weeklyTimeS)
{
	int ix = java.util.Arrays.binarySearch(timeS, weeklyTimeS);
	if (ix >= 0) return rateY[ix];

	ix = -ix-1;
//System.out.println("weeklyTimeS = " + weeklyTimeS + "(" + ((double)weeklyTimeS / 3600.0D) + ") ix = " + ix);
	return ((double)(weeklyTimeS - timeS[ix-1]) / (double)(timeS[ix] - timeS[ix-1]))
		* (rateY[ix-1] + rateY[ix]);
}


}

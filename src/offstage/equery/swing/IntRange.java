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

package offstage.equery.swing;

import java.util.List;

/**
 * Used on the right hand side of the "in" and "not in" comparators
 * for JEnumMulti.  Specifies that between min and max (inclusive) of the specified
 * elements should match.  Special cases: (min=null ==> at most max).  (max=null ==> at least min).
 * (min=null,max=null => at least 1).
 * @author citibob
 */
public class IntRange
{
	public Integer min;
	public Integer max;
}

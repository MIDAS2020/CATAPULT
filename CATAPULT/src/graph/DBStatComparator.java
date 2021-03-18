/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Comparator;

public class DBStatComparator implements Comparator<DBStat> {

	public DBStatComparator() {

	}

	@Override
	public int compare(DBStat x, DBStat y) {
		int x_freq = x.getFreq();
		int y_freq = y.getFreq();
		if (x_freq > y_freq)
			return -1;
		else if (x_freq < y_freq)
			return 1;
		else
			return 0;
	}
}

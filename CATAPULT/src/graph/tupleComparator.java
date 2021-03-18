/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Comparator;

public class tupleComparator implements Comparator<priorityQueueTuple> {

	public tupleComparator() {

	}

	@Override
	public int compare(priorityQueueTuple x, priorityQueueTuple y) {
		if (x.getValue() < y.getValue())
			return 1;
		else
			return -1;
	}
}

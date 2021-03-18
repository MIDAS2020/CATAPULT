/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Comparator;

public class closureEdgeComparator implements Comparator<closureEdge> {

	public closureEdgeComparator() {

	}

	@Override
	public int compare(closureEdge x, closureEdge y) {
		int x_cv = x.getCostValue();
		int y_cv = y.getCostValue();
		if (x_cv == y_cv)
			return 0;
		else if (x_cv > y_cv)
			return 1;
		else
			return -1;
	}
}

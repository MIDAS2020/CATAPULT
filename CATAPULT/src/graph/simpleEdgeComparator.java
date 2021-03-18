/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Comparator;

public class simpleEdgeComparator implements Comparator<simpleEdge> {

	public simpleEdgeComparator() {

	}

	@Override
	public int compare(simpleEdge x, simpleEdge y) {
		String x_source_label = x.getSource().getLabel();
		String x_target_label = x.getTarget().getLabel();
		String y_source_label = y.getSource().getLabel();
		String y_target_label = y.getTarget().getLabel();
		if ((x_source_label.compareTo(y_source_label) == 0 && x_target_label.compareTo(y_target_label) == 0)
				|| (x_source_label.compareTo(y_target_label) == 0 && x_target_label.compareTo(y_source_label) == 0))
			return 0;
		else
			return -1;
	}
}

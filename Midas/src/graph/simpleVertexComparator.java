/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.Comparator;

public class simpleVertexComparator implements Comparator<simpleVertex> {

	public simpleVertexComparator() {

	}

	@Override
	public int compare(simpleVertex x, simpleVertex y) {
		return x.getLabel().compareTo(y.getLabel());
	}
}

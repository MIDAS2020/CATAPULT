/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
* Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
*
* File name: PairCompare.java
*
* Abstract:  used in SimVerify
*
* Current Version:      0.1
* Auther:               Jin Changjiu
* Modified Date:        Oct.16,2009
*
*/
package similarity;

import adjlistgraph.Graph;
import frequentindex.Vertex;
import java.util.Comparator;

/**
 *
 * @author cjjin
 */

public class PairCompare implements Comparator {

	private Graph sg = null;

	public PairCompare(Graph g) {
		sg = g;
	}

	public int compare(Object o1, Object o2) {
		Pair p1 = (Pair) o1;
		Pair p2 = (Pair) o2;

		Vertex e1 = sg.getNode(p1.getM());
		Vertex e2 = sg.getNode(p2.getM());

		if (e1.getM().size() > e2.getM().size()) {
			return -1;
		} else if (e1.getM().size() == e2.getM().size()) {
			return 0;
		} else {
			return 1;
		}
	}

}

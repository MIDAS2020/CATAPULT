/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: Cluster.java
 *
 * Abstract:  The cluster structure in DF-index
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Mar.5, 2010
 *
 */
package frequentindex;

import java.util.Vector;

/**
 *
 * @author cjjin
 */
public class Cluster {

	private Vector<Integer> vIdSet = new Vector<Integer>(); // set of ids belong to this cluster;
	private Vector<String> edgeSet = new Vector<String>();

	public void addNode(int id) {
		vIdSet.addElement(id);

	}

	public void addEdge(int src, int trg) {

		edgeSet.addElement(src + " " + trg);
	}

	public Vector<Integer> getNodeSet() {
		return vIdSet;

	}

	public Vector<String> getEdgeSet() {
		return edgeSet;

	}
}

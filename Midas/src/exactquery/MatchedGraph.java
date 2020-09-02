/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
* Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
*
* File name: MatchedGraph.java
*
* Abstract: Keep the matched subgraph part of the result graph
*
* Current Version:      0.1
* Auther:               Jin Changjiu
* Modified Date:        Sep.22,2009
*
*/

package exactquery;

import java.util.Vector;

/**
 *
 * @author cjjin
 */
public class MatchedGraph {

	private Vector<Integer> nodeset = new Vector<Integer>();
	private int id;

	public void addNodes(int nodeid) {
		nodeset.addElement(nodeid);
	}

	public Vector<Integer> getNodeset() {
		return nodeset;
	}

	public void setID(int graphid) {
		id = graphid;
	}

	public int getID() {
		return id;
	}

}

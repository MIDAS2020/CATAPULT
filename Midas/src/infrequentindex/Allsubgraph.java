
/*
* Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
*
* File name: Allsubgraph.java
*
* Abstract: Get all the subgraphs of a given graph
*
* Current Version:      0.1
* Auther:               Jin Changjiu
* Modified Date:        Jun.16,2009
*
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package infrequentindex;

import adjlistgraph.Graph;
import java.util.Collections;
import java.util.HashSet;
import java.util.Vector;
import frequentindex.Vertex;

/**
 *
 * @author cjjin
 */
public class Allsubgraph {
	private static CamGenerator bc = new CamGenerator();

	public Vector<String> getSubgraphCam(Graph graph) {
		HashSet<String> subcams = new HashSet<String>();
		getAllSubgraph(graph, subcams);
		Vector<String> list = new Vector<String>();

		for (String value : subcams) {
			list.addElement(value);
		}

		StringSort ss = new StringSort();
		Collections.sort(list, ss);
		list.addElement(bc.buildCam(graph));// add the graph itself

		return list;

	}

	public HashSet<String> getAllSubgraph(Graph graph, HashSet<String> subcam) {
		int gsize = graph.getVertexNum();

		for (int i = 0; i < gsize - 1; i++)// delete one node,not the last one
		{
			Graph subgraph = new Graph();
			subgraph.setVertexNum(gsize - 1);
			for (int j = 0; j < gsize; j++) {
				if (j != i) {
					Vertex node = new Vertex();
					node.setLabel(graph.getNode(j).getLabel());
					subgraph.addNode(node);

					for (int k = 0; k < gsize; k++) {
						if (k != i && graph.getEdgeLabel(j, k) == 1) {
							int m = 0, n = 0;
							if (j > i) {
								m = j - 1;
							} else {
								m = j;
							}
							if (k > i) {
								n = k - 1;
							} else {
								n = k;
							}

							subgraph.addEdge(m, n);
							subgraph.getNode(m).incDegree();
							subgraph.getNode(m).setID(m);
						}

					}
				}

			}

			int k = 0;
			for (k = 0; k < gsize - 1; k++) {
				if (subgraph.getNode(k).getDegree() == 0) {
					break;
				}
			}
			if (k == gsize - 1) {
				getAllSubgraph(subgraph, subcam);
				String subcamstr = bc.buildCam(subgraph);
				subcam.add(subcamstr);
			}
		}

		return subcam;
	}

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package infrequentindex;

import adjlistgraph.Graph;
import frequentindex.Vertex;
import java.util.Vector;

/**
 *
 * @author cjjin
 */

/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological
 * University
 *
 * File name: constructLsubgraph.java
 *
 * Abstract: construct the largest subgraphs of graph g
 *
 * Current Version: 0.1 Auther: Jin Changjiu Modified Date: Jun.26,2010
 *
 */

public class ConLsubgraph {

	private CamGenerator bc = new CamGenerator();

	public Vector<Graph> getLargeSubgraph(Graph graph) {
		Vector<String> subcams = new Vector<String>();
		Vector<Graph> largestSubgraphs = new Vector<Graph>();

		int gsize = graph.getVertexNum();

		for (int i = 0; i < gsize; i++)// choose one node to delete
		{
			Graph subgraph = new Graph();
			subgraph.setVertexNum(gsize - 1);
			int edgenum = 0;
			for (int j = 0; j < gsize; j++) {

				if (j != i) {
					int m = j;
					Vertex node = new Vertex();
					node.setLabel(graph.getNode(j).getLabel());
					subgraph.addNode(node);
					for (int k = 0; k < gsize; k++) {
						if (k != i && graph.getEdgeLabel(j, k) > 0) {
							int n = k;
							if (j > i) {
								m = j - 1; // m is the id of new j
							}

							if (k > i) {
								n = k - 1;
							}

							subgraph.addEdge(m, n);
							edgenum++;
							subgraph.getNode(m).incDegree();
							subgraph.getNode(m).setID(m);

						}
					}
				}
			}

			int t = 0;
			for (t = 0; t < gsize - 1; t++) {
				if (subgraph.getNode(t).getDegree() == 0) {
					break;
				}
			}

			if (t == gsize - 1 && edgenum / 2 == (graph.getEdgeNum() - 1)) {
				String subcamstr = bc.buildCam(subgraph);

				if (!largestSubgraphs.contains(subgraph)) {
					subcams.addElement(subcamstr);

					subgraph.setCam(subcamstr);
					subgraph.setEdgeNum(edgenum / 2);

					largestSubgraphs.addElement(subgraph);
				}
			}
		}

		return largestSubgraphs;
	}

}

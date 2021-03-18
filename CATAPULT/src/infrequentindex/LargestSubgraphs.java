package infrequentindex;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: LargestSubgraphs.java
 *
 * Abstract: Found out all the largest subgraphs of a fragment
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Jan.21,2010
 *
 */
import adjlistgraph.Graph;
import frequentindex.Vertex;
import java.util.ArrayList;

/**
 *
 * @author cjjin
 */
public class LargestSubgraphs {

	private CamGenerator bc = new CamGenerator();
	ArrayList<Graph> largestSubgraphs = null;

	public ArrayList<String> getLargeSubgraphSet(Graph graph) {
		ArrayList<String> subcams = new ArrayList<String>();
		largestSubgraphs = new ArrayList<Graph>();
		int vNum = graph.getVertexNum();

		for (int i = 0; i < vNum; i++)// choose one node to delete
		{
			Graph subgraph = new Graph();
			subgraph.setVertexNum(vNum - 1);
			for (int j = 0; j < vNum; j++) {
				if (j != i) {
					int m = j;
					Vertex node = new Vertex();
					node.setLabel(graph.getNode(j).getLabel());
					subgraph.addNode(node);
					for (int k = 0; k < vNum; k++) {
						if (k != i && graph.getEdgeLabel(j, k) > 0) {
							int n = k;
							if (j > i) {
								m = j - 1; // m is the id of new j
							}

							if (k > i) {
								n = k - 1;
							}

							subgraph.addEdge(m, n);
							subgraph.getNode(m).incDegree();
							subgraph.getNode(m).setID(m);

						}
					}
				}
			}

			int t = 0;
			for (t = 0; t < vNum - 1; t++) {
				if (subgraph.getNode(t).getDegree() == 0) {
					break;
				}
			}
			if (t == vNum - 1) {
				String subcamstr = bc.buildCam(subgraph);
				if (!subcams.contains(subcamstr))// the unique largest subgraph
				{
					subcams.add(subcamstr);
					subgraph.setCam(subcamstr);
					largestSubgraphs.add(subgraph);
				}
			}
		}

		return subcams;
	}

	public ArrayList<Graph> getSubgraphSet() {
		return largestSubgraphs;
	}
}

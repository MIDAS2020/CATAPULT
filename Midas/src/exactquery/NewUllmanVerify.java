package exactquery;

/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: UllmanVerify.java
 *
 * Abstract: Ullman subgraph isomorphism verification
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
import adjlistgraph.Graph;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.Collections;
import java.util.Vector;
import frequentindex.Vertex;
import infrequentindex.Nodecompare;

/**
 *
 * @author cjjin
 */
public class NewUllmanVerify {

	// matchingResult[i] = j => i-th of query ~ j-th of graph;
	private Vector<Integer> matchingResult = new Vector<Integer>();

	// check if q is subgraph of g
	public boolean verify(Graph qGraph, Graph gGraph) {
		matchingResult.clear();

		int nGraph = gGraph.getVertexNum();
		Vector<Integer> F = new Vector<Integer>(); // F[i]=1 => ith vertex of g has been used;
		for (int i = 0; i < nGraph; i++) {
			F.addElement(0);
		}

		int nQuery = qGraph.getVertexNum();
		Vector<Integer> H = new Vector<Integer>(); // H[i]=j => ith vertex of q ~ jth vertex of g;
		for (int i = 0; i < nQuery; i++) {
			H.addElement(0);
		}

		int d = 0;// depth

		int[][] M = new int[nQuery][nGraph]; // Possible pair for matching;k
		for (int i = 0; i < nQuery; i++) {
			for (int j = 0; j < nGraph; j++) {
				if (qGraph.getNode(i).getDegree() <= gGraph.getNode(j).getDegree() // degree is less
						&& qGraph.getNode(i).getLabel().equals(gGraph.getNode(j).getLabel())) { // labels are same
					M[i][j] = 1;
				} else {
					M[i][j] = 0;
				}
			}

		}

		return UllmanAlgo(F, H, qGraph, gGraph, d, M);

	}

	// the ullmanalgorim verification
	public boolean UllmanAlgo(Vector<Integer> F, Vector<Integer> H, Graph qGraph, Graph graph, int curDepth,
			int[][] M) {

		int nGraph = graph.getVertexNum();
		int nQuery = qGraph.getVertexNum();

		if (curDepth == nQuery) {
			return true;
		} else {
			// Consider all vertices of g;
			for (int k = 0; k < nGraph; k++) {
				if (F.elementAt(k) == 0 && refine(qGraph, graph, curDepth, k, M)) {
					H.setElementAt(k, curDepth); // curDepth-th of query ~ k-th of graph;
					F.setElementAt(1, k); // k-th of graph has been used;

					// Save M[][];
					int[][] M1 = new int[nQuery][nGraph];
					for (int n = 0; n < nQuery; n++) {
						for (int m = 0; m < nGraph; m++) {
							M1[n][m] = M[n][m];
						}
					}

					// Modify M to make vertex d of graph q not available for matching anymore;
					for (int m = 0; m < nGraph; m++) {
						if (m != k) {
							M[curDepth][m] = 0;
						}
					}

					// Recursive call
					if (UllmanAlgo(F, H, qGraph, graph, curDepth + 1, M)) {
						matchingResult.addElement(k);
						return true;
					} else {
						M = M1; // revert M[][];
						F.setElementAt(0, k); // revert F[];
					}
				}
			} // end for
		} // end else
		return false;
	}

	// refine process
	public boolean refine(Graph qGraph, Graph graph, int qIdx, int gIdx, int[][] M) {
		Vector<Integer> visited = new Vector<Integer>();

		if (M[qIdx][gIdx] == 1) {
			Vector<Vertex> qIdxNeighborSet = getNeighbors(qIdx, qGraph);

			// For each qIdxNeighbor;
			for (int i = 0; i < qIdxNeighborSet.size(); i++) {
				int qIdxNeighbor = qIdxNeighborSet.elementAt(i).getId();

				// Find match for qIdxNeighbor (ERROR!)
				int gIdxNeighbor = 0;
				for (gIdxNeighbor = 0; gIdxNeighbor < graph.getVertexNum(); gIdxNeighbor++) {
					if (!visited.contains(gIdxNeighbor) && graph.getEdgeLabel(gIdxNeighbor, gIdx) == 1
							&& M[qIdxNeighbor][gIdxNeighbor] == 1) {
						visited.addElement(gIdxNeighbor);
						break;
					}
				}

				// Not found gIdxNeighbor
				if (gIdxNeighbor == graph.getVertexNum()) {
					M[qIdx][gIdx] = 0;
					return false;
				}
			}
		} else {
			return false;
		}
		return true;

	}

	// get neighbor of nid in graph q;
	// Result is sorted;
	public Vector<Vertex> getNeighbors(int nid, Graph q) {

		Vector<Vertex> neighbors = new Vector<Vertex>();
		for (int i = 0; i < q.getVertexNum(); i++) {
			if (q.getEdgeLabel(i, nid) > 0) {
				neighbors.addElement(q.getNode(i));
			}
		}

		Nodecompare nc = new Nodecompare();
		Collections.sort(neighbors, nc);

		return neighbors;
	}

	public Vector<Integer> getNodeset() {
		return matchingResult;
	}

	/**********************************************************************/
	public static void main(String[] args) throws FileNotFoundException {
		NewUllmanVerify u = new NewUllmanVerify();
		u.testVerifyFunction("test1.txt");
	}

	public void testVerifyFunction(String filename) {

		Vector<Graph> graphlist = new Vector<Graph>();
		Graph tmpgraph = null;

		String strLine = null;
		FileInputStream inStream;
		DataInputStream in;
		BufferedReader br;

		File fin = new File(filename);

		if (fin.exists()) {
			try {
				inStream = new FileInputStream(fin);
				in = new DataInputStream(inStream);
				br = new BufferedReader(new InputStreamReader(in));

				// Read file line by line
				try {
					int graphnum = -1;
					while ((strLine = br.readLine()) != null) {
						// Print the content on the console
						if (strLine.contains("t #")) {
							String[] line = strLine.split("\\s");

							tmpgraph = new Graph();
							tmpgraph.setVertexNum(Integer.parseInt(line[3]));

							graphnum++;
						} else if (strLine.contains("v")) {
							String[] line = strLine.split("\\s");

							Vertex node = new Vertex();
							node.setID(Integer.parseInt(line[1]));
							node.setLabel(line[2]);

							tmpgraph.addNode(node);
						} else if (strLine.contains("e")) {
							String[] line = strLine.split("\\s");

							int s = Integer.parseInt(line[1]);
							int t = Integer.parseInt(line[2]);
							tmpgraph.addEdge(s, t);
							tmpgraph.getNode(s).incDegree();
							tmpgraph.getNode(t).incDegree();

						} else {
							graphlist.addElement(tmpgraph);
						}
					}

					if (verify(graphlist.elementAt(0), graphlist.elementAt(1))) {
						System.out.println(graphlist.elementAt(1).getVertexNum() + " Correct!");
					} else {
						System.out.println(graphlist.elementAt(1).getVertexNum() + "Fail!");
					}

					in.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}

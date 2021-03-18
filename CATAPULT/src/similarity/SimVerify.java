
/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: SimVerify.java
 *
 * Abstract: Verify the similarity between two graphs by the MCS
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Aug.17,2010
 *
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity;

/**
 *
 * @author cjjin
 */
import adjlistgraph.Graph;
import frequentindex.Vertex;
import infrequentindex.Nodecompare;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

public class SimVerify {

	private ArrayList<Integer> matchednodeset = null;
	private ArrayList<Vertex> qVertexSet = new ArrayList<Vertex>();

	public SimVerify(Graph q) {
		for (int i = 0; i < q.getVertexNum(); i++) {
			q.getNode(i).setID(i);
			qVertexSet.add(q.getNode(i));
		}
		Nodecompare nc = new Nodecompare();// sort the nodes by label and degree
		Collections.sort(qVertexSet, nc);
	}

	// Main method;
	public boolean verify(Graph qGraph, Graph graph, int nMissed) {
		matchednodeset = new ArrayList<Integer>();

		// Import vertices from g;
		ArrayList<Vertex> gVertexSet = new ArrayList<Vertex>();
		for (int i = 0; i < graph.getVertexNum(); i++) {
			graph.getNode(i).setID(i);
			gVertexSet.add(graph.getNode(i));
		}
		Nodecompare nc = new Nodecompare();// sort the nodes by label and degree
		Collections.sort(gVertexSet, nc);

		//
		for (int i = 0; i < qVertexSet.size(); i++) {
			Vertex qVertex = qVertexSet.get(i);

			for (int j = 0; j < gVertexSet.size(); j++) {
				Vertex gVertex = gVertexSet.get(j);

				ArrayList<Integer> qVertexNeighborIdSet = qVertex.getIn();
				ArrayList<Integer> gVertexNeighborIdSet = gVertex.getIn();

				if (qVertex.getDegree() - nMissed <= gVertex.getDegree() // Vertex degree check;
						&& qVertex.getLabel().equals(gVertex.getLabel()) // Vertex label check;
						&& isNeighborCampatible(qGraph, graph, qVertexNeighborIdSet, gVertexNeighborIdSet, nMissed)) {

					State state = new State();
					state.addToM1(qVertex.getId());
					state.addToM2(gVertex.getId());

					if (match(qGraph, graph, state, nMissed)) {// match is a recursion function
						return true;
					}
				} // end if
			}
		}

		return false;

	}

	// Parent node of i, j, which are already in M and campatible
	public boolean isNeighborCampatible(Graph qGraph, Graph graph, ArrayList<Integer> qVertexNeighborSet,
			ArrayList<Integer> gVertexNeighborSet, int missed) {

		HashSet<Integer> matchedSet = new HashSet<Integer>(); // list of candidates in graph;
		for (int i = 0; i < qVertexNeighborSet.size(); i++) {

			int qNeighborId = qVertexNeighborSet.get(i);
			HashSet<Integer> matched = new HashSet<Integer>(); // candidate list for mapping with i-th of query;

			for (int j = 0; j < gVertexNeighborSet.size(); j++) {
				int gNeighborId = gVertexNeighborSet.get(j);

				// check matching between (qNeighborId, gNeighborId) by vertex label, degree;
				if (qGraph.getNode(qNeighborId).getLabel().equals(graph.getNode(gNeighborId).getLabel())
						&& qGraph.getNode(qNeighborId).getDegree() - missed <= graph.getNode(gNeighborId).getDegree()) {

					matched.add(gNeighborId);
				}
			}
			if (matched.isEmpty()) {
				return false;
			} else {
				matchedSet.addAll(matched);
			}
		}
		// all the neighbors of node1 in M can find the matched neighbors of node2 in M
		if (matchedSet.size() >= qVertexNeighborSet.size()) {
			return true;
		} else {
			return false;
		}
	}

	// The initialize of P and M
	public boolean match(Graph q, Graph g, State state, int missed) {
		// calculate the number of edge in matched subgraph of g
		ArrayList<Integer> IdsetM2 = state.getM2();
		ArrayList<Integer> IdsetM1 = state.getM1();

		// Consider pair(t1, t2) belong to M1;
		for (int t1 = 0; t1 < IdsetM1.size(); t1++) {
			for (int t2 = t1 + 1; t2 < IdsetM1.size(); t2++) {

				if (q.getEdgeLabel(IdsetM1.get(t1), IdsetM1.get(t2)) > 0) {
					if (g.getEdgeLabel(IdsetM2.get(t1), IdsetM2.get(t2)) > 0) {
						state.edgeNumberInc();// add one edge
					} else {
						break;
					}
				}
			}
		}

		int stateEdgenum = state.getEdgeNumber();

		if (stateEdgenum == (q.getEdgeNum() - missed)) {
			// get the matched node set
			for (int i = 0; i < IdsetM2.size(); i++) {
				matchednodeset.add(IdsetM2.get(i)); // the position id
			}

			return true;
		} else {
			// construct the new candidate Pair and get the information of current state
			ArrayList<Pair> p = constructP(q, g, state, missed);

			for (Iterator itr = p.iterator(); itr.hasNext();) {
				State newstate = new State();
				// store the previous state, by copying the previous states M1 and M2
				for (int j = 0; j < state.getM1().size(); j++) {
					newstate.addToM1(state.getM1().get(j));
					newstate.addToM2(state.getM2().get(j));
				}
				Pair tmpp = (Pair) itr.next();

				if (compatible(state, tmpp, q, g, missed))// if the new two nodes compatible
				{
					newstate.addToM1(tmpp.getM());// add this new pair
					newstate.addToM2(tmpp.getN());

					if (match(q, g, newstate, missed))// call match again
					{
						return true;
					}
				}
			}
		}
		return false;
	}

	public void getNeighbors(ArrayList<Integer> M, Vertex node) {
		node.getM().clear();

		ArrayList<Integer> indegree = node.getIn();// the neighbors of nodei in q
		for (int i = 0; i < indegree.size(); i++) {
			if (M.contains(indegree.get(i))) {
				node.setM(indegree.get(i));// neighbors in M1
			}
		}
	}

	// the parent node of i, j, which are already in M and campatible
	public boolean neighborCampatible(Graph q, Graph g, State s, Vertex node1, Vertex node2, int missed) {
		getNeighbors(s.getM1(), node1);
		getNeighbors(s.getM2(), node2);

		int m = 0;
		for (int i = 0; i < node1.getM().size(); i++) {
			for (int j = 0; j < node2.getM().size(); j++) {
				// if there are campatible pair among the neighbors of nodei and nodej
				if (q.getNode(node1.getM().get(i)).getLabel().equals(g.getNode(node2.getM().get(j)).getLabel())
						&& q.getNode(node1.getM().get(i)).getDegree() - missed <= g.getNode(node2.getM().get(j))
								.getDegree()) {
					m++;
				}
			}
		}
		// all the neighbors of node1 in M can find the matched neighbors of node2 in M
		if (m >= node1.getM().size()) {
			return true;
		} else {
			return false;
		}

	}

	// nodes compatible
	public boolean compatible(State state, Pair pair, Graph q, Graph g, int missed) {
		int id1 = pair.getM();
		int id2 = pair.getN();

		boolean f1 = !state.getM1().contains(id1) && !state.getM2().contains(id2)
				&& q.getNode(id1).getLabel().equals(g.getNode(id2).getLabel())
				&& q.getNode(id1).getDegree() - missed <= g.getNode(id2).getDegree();

		boolean f2 = neighborCampatible(q, g, state, q.getNode(id1), g.getNode(id2), missed);
		if (f1 && f2) {
			return true;
		} else {
			return false;
		}

	}

	public void getRule(Graph g, ArrayList<Integer> in, ArrayList<Integer> M) {
		// all the input nodes which not in M
		for (int i = 0; i < M.size(); i++) {
			// get Tin
			ArrayList<Integer> allin = g.getNode(M.get(i)).getIn();
			for (int j = 0; j < allin.size(); j++) {
				if (!M.contains(allin.get(j))) {
					in.add(allin.get(j));
				}
			}
		}
	}

	// construct the Pair
	public ArrayList<Pair> constructP(Graph q, Graph g, State s, int missed) {
		ArrayList<Pair> p = new ArrayList<Pair>();
		getRule(q, s.getTin1(), s.getM1());
		getRule(g, s.getTin2(), s.getM2());

		if (!s.getTin1().isEmpty() && !s.getTin2().isEmpty()) {
			for (int i = 0; i < s.getTin1().size(); i++) {
				int id1 = s.getTin1().get(i);
				for (int j = 0; j < s.getTin2().size(); j++) {
					int id2 = s.getTin2().get(j);
					if (q.getNode(id1).getDegree() - missed <= g.getNode(id2).getDegree()
							&& q.getNode(id1).getLabel().equals(g.getNode(id2).getLabel())) {
						Pair tmp = new Pair();
						tmp.setM(id1);
						tmp.setN(id2);
						if (!pairContain(p, tmp)) {
							p.add(tmp);
						}
					}
				}
			}
		}
		// order the q's nodes in pair vector, choose the one has more neighbors in M
		return p;
	}

	public boolean pairContain(ArrayList<Pair> pairset, Pair pair) {
		for (int i = 0; i < pairset.size(); i++) {
			if (pairset.get(i).getM() == pair.getM() && pairset.get(i).getN() == pair.getN()) {
				return true;
			}
		}

		return false;
	}

	public ArrayList<Integer> getNodeset() {
		return matchednodeset;
	}
}

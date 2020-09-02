/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedMultigraph;

public class JGraphtGraph {
	private UndirectedGraph<simpleVertex, simpleEdge> g;
	private int graphID;
	private ArrayList<simpleVertex> simpleVertexList = new ArrayList<simpleVertex>();
	private ArrayList<Integer> nodeIDList = new ArrayList<Integer>();

	public JGraphtGraph() {
		g = new SimpleGraph<simpleVertex, simpleEdge>(simpleEdge.class);
	}

	public JGraphtGraph(int id) {
		g = new SimpleGraph<simpleVertex, simpleEdge>(simpleEdge.class);
		graphID = id;
	}

	private ArrayList<simpleVertex> getVertexList() {
		return simpleVertexList;
	}

	public UndirectedGraph<simpleVertex, simpleEdge> getGraph() {
		return g;
	}

	public int size() {
		return simpleVertexList.size();
	}

	public simpleVertex getMaxDegreeVertex() {
		ArrayList<Integer> v_deg = new ArrayList<Integer>();
		for (int i = 0; i < simpleVertexList.size(); i++) {
			simpleVertex v = simpleVertexList.get(i);
			v_deg.add(g.degreeOf(v));
		}
		int max_deg = Collections.max(v_deg);
		int max_deg_index = v_deg.indexOf(max_deg);
		if (max_deg_index == -1) {
			System.out.println("ERROR (though it shouldn't be happening...): max_deg_index is not found!!!");
			return null;
		}
		return simpleVertexList.get(max_deg_index);
	}

	public void removeVertex(simpleVertex v) {
		boolean REMOVEVERTEX_PRINT = false;
		int index = simpleVertexList.indexOf(v);
		if (index == -1)// v is not found
			return;
		else {
			ArrayList<simpleEdge> edgeSet = getEdgeSet(v);
			for (int i = 0; i < edgeSet.size(); i++)
				g.removeEdge(edgeSet.get(i));
			g.removeVertex(v);
			int id = v.getID();
			int id_index = nodeIDList.indexOf(id);
			nodeIDList.remove(id_index);
			simpleVertexList.remove(index);
			if (REMOVEVERTEX_PRINT) {
				System.out.println("REMOVE SIMPLEVERTEX: ");
				v.print();
				System.out.println("GRAPH AFTER REMOVAL: ");
				print();
				System.out.println("nodeIDList: " + nodeIDList.toString());
				for (int i = 0; i < simpleVertexList.size(); i++)
					simpleVertexList.get(i).print();
				System.out.println("___________________________________________________");
			}
		}
	}

	public int getNumNodes() {
		return g.vertexSet().size();
	}

	public simpleVertex getVertexAt(int pos) {
		if (pos >= 0 && pos < getNumNodes())
			return simpleVertexList.get(pos);
		else
			return null;
	}

	public int getGraphID() {
		return graphID;
	}

	public void addNode(simpleVertex v) {
		if (simpleVertexList.contains(v) == false) {
			simpleVertexList.add(v);
			nodeIDList.add(v.getID());
		}
		g.addVertex(v);
	}

	public void addNodeIfDoesNotExist(simpleVertex v) {
		if (simpleVertexList.contains(v) == false) {
			simpleVertexList.add(v);
			nodeIDList.add(v.getID());
		}
		if (g.containsVertex(v) == false) {
			g.addVertex(v);
		}
	}

	public void addEdge(simpleVertex v1, simpleVertex v2) {
		simpleEdge e = new simpleEdge(v1, v2);
		g.addEdge(v1, v2, e);
	}

	public int getIndexOfVertex(simpleVertex v) {
		return simpleVertexList.indexOf(v);
	}

	public ArrayList<simpleVertex> getNeighbourOf(simpleVertex v) {
		ArrayList<simpleVertex> neighbourList = new ArrayList<simpleVertex>();
		Set eSet = g.edgesOf(v);
		Iterator i = eSet.iterator();
		while (i.hasNext()) {
			simpleEdge e = (simpleEdge) i.next();
			simpleVertex sV = e.getSource();
			simpleVertex tV = e.getTarget();
			if (sV.getID() != v.getID())
				neighbourList.add(sV);
			if (tV.getID() != v.getID())
				neighbourList.add(tV);
		}
		return neighbourList;
	}

	public int getDegreeOf(simpleVertex v) {
		// System.out.println("value of v:"+v.getID() + "---"+v.getLabel());
		return g.degreeOf(v);
	}

	public String addEdge(int v1_id, int v2_id) {
		int v1_index = nodeIDList.indexOf(v1_id);
		int v2_index = nodeIDList.indexOf(v2_id);
		if (v1_index == -1 || v2_index == -1)
			return null;
		simpleVertex v1 = simpleVertexList.get(v1_index);
		simpleVertex v2 = simpleVertexList.get(v2_index);
		simpleEdge e = new simpleEdge(v1, v2);
		g.addEdge(v1, v2, e);
		return e.getEdgeLabelString();
	}

	public ArrayList<simpleVertex> getNodeSet() {
		Set vSet = g.vertexSet();
		ArrayList<simpleVertex> list = new ArrayList<simpleVertex>();
		Iterator i = vSet.iterator();
		while (i.hasNext())
			list.add((simpleVertex) i.next());
		return list;
	}

	public ArrayList<simpleEdge> getEdgeSet() {
		Set eSet = g.edgeSet();
		ArrayList<simpleEdge> list = new ArrayList<simpleEdge>();
		Iterator i = eSet.iterator();
		while (i.hasNext())
			list.add((simpleEdge) i.next());
		return list;
	}

	public ArrayList<simpleEdge> getEdgeSet(simpleVertex v) {
		Set eSet = g.edgesOf(v);
		ArrayList<simpleEdge> list = new ArrayList<simpleEdge>();
		Iterator i = eSet.iterator();
		while (i.hasNext())
			list.add((simpleEdge) i.next());
		return list;
	}

	public JGraphtClosureGraph convertToClosureGraph() {
		JGraphtClosureGraph g = new JGraphtClosureGraph();
		ArrayList<closureVertex> cVertexList = new ArrayList<closureVertex>();

		for (int i = 0; i < simpleVertexList.size(); i++) {
			ArrayList<String> l = new ArrayList<String>();
			l.add(simpleVertexList.get(i).getLabel());
			closureVertex v = new closureVertex(simpleVertexList.get(i).getID(), l);
			cVertexList.add(v);
			g.addNode(v);
		}
		ArrayList<simpleEdge> eSet = getEdgeSet();
		for (int i = 0; i < eSet.size(); i++) {
			simpleEdge e = eSet.get(i);
			simpleVertex source = e.getSource();
			simpleVertex target = e.getTarget();
			int sourceIndex = simpleVertexList.indexOf(source);
			int targetIndex = simpleVertexList.indexOf(target);
			closureVertex cSource = cVertexList.get(sourceIndex);
			closureVertex cTarget = cVertexList.get(targetIndex);
			closureEdge cEdge = new closureEdge(cSource, cTarget, new ArrayList<Integer>());
			g.addEdge(cSource, cTarget, cEdge);
		}

		return g;
	}

	public void print() {
		System.out.println("graphID:" + graphID);
		ArrayList<simpleVertex> vArr = getNodeSet();
		for (int i = 0; i < vArr.size(); i++)
			vArr.get(i).print();
		ArrayList<simpleEdge> eArr = getEdgeSet();
		for (int i = 0; i < eArr.size(); i++)
			eArr.get(i).print();
	}
}

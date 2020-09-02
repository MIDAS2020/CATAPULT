
/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: FGraph.java
 *
 * Abstract:     The structure of MF-getIndex
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        June.22,2010
 *
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package frequentindex;

import adjlistgraph.Graph;
import adjlistgraph.Link;
import adjlistgraph.Node;
import java.util.Hashtable;
import java.util.Stack;
import java.util.Vector;

/**
 *
 * @author cjjin
 */
public class FreqIndexGraph {

	private Graph[] vertexs;
	private Link[] adjTab = null;

	// head of clusters in df-getIndex;
	private Hashtable<Integer, String> clusterId_CamSet = new Hashtable<Integer, String>();

	private int pos = -1;
	private static Vector<Integer> rootlist = new Vector<Integer>();

	public FreqIndexGraph(int size) {
		vertexs = new Graph[size];
		pos = -1;
		adjTab = new Link[size];
		for (int i = 0; i < size; i++) {
			adjTab[i] = new Link();
		}
	}

	public void setroot(int graphid) {
		rootlist.addElement(graphid);
	}

	public Vector<Integer> getroot() {
		return rootlist;
	}

	public void addnode(Graph value) {
		vertexs[++pos] = value;
	}

	public int getSize() {
		return pos + 1;
	}

	public Graph getNode(int index) {
		return vertexs[index];
	}

	public int hasEdge(int from, int to) {

		Node p = adjTab[from].getFirst();
		while (p != null) {
			if (p.getIndex() == to) {
				return 1;
			}
			p = p.getNext();
		}

		return 0;
	}

	public void addEdge(int from, int to) {
		adjTab[from].add(to);
	}

	public int getUnvisitedChild(int index) {
		for (int i = 0; i <= pos; i++) {
			if (hasEdge(index, i) == 1 && !vertexs[i].isVisited()) {
				return i;
			}
		}
		return -1;
	}

	// Find successors;
	public void findSuccessors(int index) {
		int inital = index;
		if (vertexs[index] == null) {
			return;
		}

		Stack s = new Stack();
		vertexs[index].setVisited();
		s.push(index);

		while (!s.isEmpty()) {
			index = getUnvisitedChild((Integer) s.peek());
			if (index != -1) {
				vertexs[index].setVisited();
				s.push(index);
				vertexs[inital].setSucc(index);
			} else {
				s.pop();
			}
		}
		clean();
	}

	public void clean() {
		for (Graph v : vertexs) {
			if (v != null) {
				v.clean();
			}
		}
	}

	public Hashtable<Integer, String> getClusterHeads() {
		return clusterId_CamSet;
	}

	public void setClusterHeads(int id, String cam) {
		clusterId_CamSet.put(id, cam);
	}
}

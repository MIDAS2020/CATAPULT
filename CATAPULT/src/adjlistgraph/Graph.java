/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package adjlistgraph;

import frequentindex.Vertex;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: Graph.java
 *
 * Abstract: The graph structure
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Feb 28,2010
 *
 */
public class Graph {
	private int graphid; // 0-based;

	private int nodeNum = 0;
	private ArrayList<Vertex> vertexes;

	private int edgeNum = 0;
	private ArrayList<Link> adjTab; // Link is LinkedList;

	private boolean isVisited;
	private String cam = null;

	private HashSet<Integer> idlist = new HashSet<Integer>(); // id of graphs containing this graph;

	// graph in hierarchical order;
	private ArrayList<Integer> Succ = new ArrayList<Integer>(); // children in all level, used for compute fsgIds
	private ArrayList<Integer> children = new ArrayList<Integer>();

	// support storing clusters in mf-getIndex;
	private ArrayList<Integer> clusterlist = new ArrayList<Integer>();

	private int pos = -1; // just to control the position to add vertex into "vertexes[]";

	public Graph() {
		vertexes = new ArrayList<Vertex>();
		adjTab = new ArrayList<Link>();
	}

	public void setVertexList(ArrayList<Vertex> list) {
		vertexes = list;
	}

	public void setAdjTab(ArrayList<Link> list) {
		adjTab = list;
	}

	public ArrayList<Vertex> getVertexList() {
		return vertexes;
	}

	public ArrayList<Link> getAdjTab() {
		return adjTab;
	}

	/*----------------------------Get & Set methods------------------------------*/
	public int getEdgeLabel(int from, int to) {
		Node p = adjTab.get(from).getFirst();
		while (p != null) {
			if (p.getIndex() == to) {
				return 1;
			}
			p = p.getNext();
		}

		return 0;
	}

	public int getVertexNum() {
		return nodeNum;
	}

	public void setVertexNum(int num) {
		nodeNum = num;
	}

	public void setEdgeNum(int num) {
		edgeNum = num;
	}

	public int getEdgeNum() {
		return edgeNum;
	}

	public Vertex getNode(int index) {
		return vertexes.get(index);
	}

	public Link getLink(int index) {
		return adjTab.get(index);
	}

	public void setGraphid(int gid) {
		graphid = gid;
	}

	public int getGraphID() {
		return graphid;
	}

	public void setCam(String line) {
		cam = line;
	}

	public String getCam() {
		return cam;
	}

	public void setSucc(int node) {
		Succ.add(node);
	}

	public ArrayList<Integer> getSucc() {
		return Succ;
	}

	public void addToChildren(int nodeid) {
		children.add(nodeid);
	}

	public ArrayList<Integer> getChildren() {
		return children;
	}

	public void addToClusters(int cid) {
		clusterlist.add(cid);
	}

	public ArrayList<Integer> getClusters() {
		return clusterlist;
	}
	/*--------------------------------------------------------------------------*/

	public void addNode(Vertex node) {
		vertexes.add(node);
	}

	public void addEdge(int from, int to) {
		int adjTabSize = adjTab.size();
		int largerIndex;
		if (from >= to)
			largerIndex = from;
		else
			largerIndex = to;
		if (largerIndex > adjTabSize - 1) // from index is beyond the adjTab size, add more entries till we have enough
		{
			int difference = largerIndex - adjTabSize + 1;
			for (int i = 0; i < difference; i++)
				adjTab.add(new Link());
		}
		adjTab.get(from).add(to);
		adjTab.get(to).add(from);
	}

	public void delEdge(int from, int to) {
		adjTab.get(from).remove(to);
		adjTab.get(to).remove(from);
	}

	// display the Adjlist
	public void dispAdjList() {
		Node p = null;
		for (int i = 0; i < nodeNum; i++) {
			System.out.println(i + ":");
			p = adjTab.get(i).getFirst();
			while (p != null) {
				System.out.print("[" + i + "," + p.getIndex() + "]" + " -> ");
				p = p.getNext();
			}
			System.out.println();
		}
	}

	public void addToIdlist(int gid) {
		idlist.add(gid);
	}

	public HashSet<Integer> getIdList() {
		return idlist;
	}

	public boolean isVisited() {
		return isVisited;
	}

	public void clean() {
		isVisited = false;
	}

	public void setVisited() {
		isVisited = true;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class SEQTuple {
	private int Ti_p;
	private String Ti_l;
	private closureVertex Ti_v;
	private int deg;
	private ArrayList<Integer> nonSpanningTreeEdgeList;

	public SEQTuple() {

	}

	public SEQTuple(int p, String l, closureVertex v) {
		Ti_p = p;
		Ti_l = l;
		Ti_v = v;
		deg = -1;
		nonSpanningTreeEdgeList = null;
	}

	public SEQTuple(int p, String l, closureVertex v, int num_deg, ArrayList<Integer> edgeList) {
		Ti_p = p;
		Ti_l = l;
		Ti_v = v;
		deg = num_deg;
		nonSpanningTreeEdgeList = edgeList;
	}

	public void setDeg(int num_deg) {
		deg = num_deg;
	}

	public void addEdge(int nonSpanningTreeParent) {
		nonSpanningTreeEdgeList.add(nonSpanningTreeParent);
	}

	public int getParentTupleIndex() {
		return Ti_p;
	}

	public String getLabel() {
		return Ti_l;
	}

	public closureVertex getVertex() {
		return Ti_v;
	}

	public int getDeg() {
		return deg;
	}

	public ArrayList<Integer> getNonSpanningTreeEdgeList() {
		return nonSpanningTreeEdgeList;
	}

	public void print() {
		System.out.println("Ti_p=" + Ti_p + " Ti_l=" + Ti_l + " Ti_v=" + Ti_v.getID() + "[" + Ti_v.getLabel() + "],"
				+ Ti_v.getWt() + " deg=" + deg + " edge=" + nonSpanningTreeEdgeList.toString());
	}
}

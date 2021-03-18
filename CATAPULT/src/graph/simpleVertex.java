/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

public class simpleVertex {
	private int node_id;
	private String node_label;
	private int node_wt;

	public simpleVertex(int id, String label) {
		node_id = id;
		node_label = label;
		node_wt = 0;
	}

	public simpleVertex(int id, String label, int wt) {
		node_id = id;
		node_label = label;
		node_wt = wt;
	}

	public int getID() {
		return node_id;
	}

	public String getLabel() {
		return node_label;
	}

	public int getWt() {
		return node_wt;
	}

	public void print() {
		System.out.println("node_id:" + node_id + " node_label:" + node_label + " node_wt:" + node_wt);
	}
}

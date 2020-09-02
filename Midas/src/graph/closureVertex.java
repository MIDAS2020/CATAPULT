/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class closureVertex {
	private ArrayList<String> node_label;
	private int node_id;
	private Float node_wt;
	private int node_freq;

	public closureVertex(int id, ArrayList<String> label) {
		node_id = id;
		node_label = label;
		node_wt = 0F;
		node_freq = 1;
	}

	public closureVertex(closureVertex v) {
		node_id = v.getID();
		node_label = v.getLabel();
		node_wt = 0F;
		node_freq = 1;
	}

	public Float getWt() {
		return node_wt;
	}

	public void setWt(Float w) {
		node_wt = w;
	}

	public int getFreq() {
		return node_freq;
	}

	public void setFreq(int f) {
		node_freq = f;
	}

	public void addToNodeLabel(String label) {
		if (node_label.contains(label) == false)
			node_label.add(label);
	}

	public int getID() {
		return node_id;
	}

	public ArrayList<String> getLabel() {
		return node_label;
	}

	public void setLabel(ArrayList<String> l) {
		node_label = l;
	}

	public void print() {
		/*
		if (this == null)
			System.out.println("NULL");
		else
			System.out.println("node_id:" + node_id + " node_label:" + node_label.toString() + " wt:" + node_wt
					+ " freq:" + node_freq);
					*/
	}

	public void print_noNewLine() {
		if (this == null)
			System.out.print("NULL");
		else
			System.out.print("node_id:" + node_id + " node_label:" + node_label.toString() + " wt:" + node_wt + " freq:"
					+ node_freq);
	}
}

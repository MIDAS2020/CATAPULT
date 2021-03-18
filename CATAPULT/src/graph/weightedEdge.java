/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class weightedEdge {
	private closureVertex source;
	private closureVertex target;
	private ArrayList<Integer> graphIDList = new ArrayList<Integer>();

	public weightedEdge(closureVertex s, closureVertex t, ArrayList<Integer> list) {
		source = s;
		target = t;
		for (int i = 0; i < list.size(); i++) {
			if (graphIDList.contains(list.get(i)) == false)
				graphIDList.add(list.get(i));
		}
	}

	public void addToGraphIDList(int id) {
		if (graphIDList.contains(id) == false)
			graphIDList.add(id);
	}

	public ArrayList<Integer> getGraphIDList() {
		return graphIDList;
	}

	public closureVertex getSource() {
		return source;
	}

	public closureVertex getTarget() {
		return target;
	}

	public void print() {
		System.out.println(graphIDList.toString() + ": " + source.getID() + source.getLabel().toString() + "--"
				+ target.getID() + target.getLabel().toString());
	}
}

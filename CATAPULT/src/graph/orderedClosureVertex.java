/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class orderedClosureVertex {
	private ArrayList<closureVertex> orderedVertices = new ArrayList<closureVertex>();
	private ArrayList<closureVertex> parentOfOrderedVertices = new ArrayList<closureVertex>();
	private ArrayList<ArrayList<String>> neighbourLabelOfOrderedVertices = new ArrayList<ArrayList<String>>();

	public orderedClosureVertex(ArrayList<closureVertex> vList, ArrayList<closureVertex> parent_vList,
			ArrayList<ArrayList<String>> neighbourLabel_vList) {
		for (int i = 0; i < vList.size(); i++) {
			orderedVertices.add(vList.get(i));
			parentOfOrderedVertices.add(parent_vList.get(i));
			neighbourLabelOfOrderedVertices.add(neighbourLabel_vList.get(i));
		}
	}

	public int indexOfOrderedVertx(closureVertex v) {
		return orderedVertices.indexOf(v);
	}

	public ArrayList<closureVertex> getOrderedVertices() {
		return orderedVertices;
	}

	public ArrayList<closureVertex> getParentOfOrderedVertices() {
		return parentOfOrderedVertices;
	}

	public ArrayList<ArrayList<String>> getNeighbourLabelOfOrderedVertices() {
		return neighbourLabelOfOrderedVertices;
	}

	public int size() {
		return orderedVertices.size();
	}

	public closureVertex getOrderedVertexAt(int pos) {
		if (pos >= 0 && pos < orderedVertices.size())
			return orderedVertices.get(pos);
		else
			return null;
	}

	public closureVertex getParentOfOrderedVertexAt(int pos) {
		if (pos >= 0 && pos < parentOfOrderedVertices.size())
			return parentOfOrderedVertices.get(pos);
		else
			return null;
	}

	public ArrayList<String> getNeighbourLabelOfOrderedVertexAt(int pos) {
		if (pos >= 0 && pos < neighbourLabelOfOrderedVertices.size())
			return neighbourLabelOfOrderedVertices.get(pos);
		else
			return null;
	}

	public void print() {
		System.out.println("ordered vertex\t parent ");
		for (int i = 0; i < orderedVertices.size(); i++) {
			closureVertex v = orderedVertices.get(i);
			closureVertex pv = parentOfOrderedVertices.get(i);
			ArrayList<String> nv = neighbourLabelOfOrderedVertices.get(i);

			System.out.print(i + ": ");
			if (v == null)
				System.out.print("NULL");
			else
				v.print_noNewLine();
			System.out.print("\t" + nv.toString() + "\t");
			if (pv == null)
				System.out.println("NULL");
			else
				pv.print();
		}
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class nodeAssignment {
	// nodeList_from and nodeList_to is used to store the mappings
	// nodeList_from[i]->nodeList_to[i]
	private ArrayList<closureVertex> nodeList_from;
	private ArrayList<Integer> nodeList_from_id;
	private ArrayList<closureVertex> nodeList_to;
	private ArrayList<Integer> nodeList_to_id;

	public nodeAssignment() {
		nodeList_from = new ArrayList<closureVertex>();
		nodeList_to = new ArrayList<closureVertex>();
		nodeList_from_id = new ArrayList<Integer>();
		nodeList_to_id = new ArrayList<Integer>();
	}

	// if (fromVertex -> toVertex) do not exist, then add it in.
	public void addAssignment(closureVertex fromVertex, closureVertex toVertex) {
		// first check if fromVertex is found in nodeList_from, if found, retrieve all
		// existing mappings of nodeList_to
		// that are mapped to fromVertex and ensure that toVertex is not found there.
		// (if found means the mapping entry
		// is already present there, no action required)

		// System.out.println("addAssignment");
		// System.out.println("fromVertex: ");
		// if(fromVertex==null)
		// System.out.println("null");
		// else
		// fromVertex.print();
		// System.out.println("toVertex: ");
		// if(toVertex==null)
		// System.out.println("null");
		// else
		// toVertex.print();

		// System.out.println("print of nodeAssignment");
		// print();

		ArrayList<Integer> toRestoreList = new ArrayList<Integer>();
		int fromVertexID;
		if (fromVertex == null)
			fromVertexID = -1;
		else
			fromVertexID = fromVertex.getID();
		int index = nodeList_from_id.indexOf(fromVertexID);
		// System.out.println("index:"+index);
		boolean CONTINUE = true;
		if (index == -1) {
			nodeList_from.add(fromVertex);
			nodeList_to.add(toVertex);
			if (fromVertex == null)
				nodeList_from_id.add(-1);
			else
				nodeList_from_id.add(fromVertex.getID());
			if (toVertex == null)
				nodeList_to_id.add(-1);
			else
				nodeList_to_id.add(toVertex.getID());
		} else {
			while (index != -1 && CONTINUE) {
				closureVertex toNode = nodeList_to.get(index);
				if (toNode.getID() == toVertex.getID()
						&& toNode.getLabel().get(0).compareTo(toVertex.getLabel().get(0)) == 0)// the to-node is
																								// equivalent
					CONTINUE = false; // found match, do nothing and return
				else {
					// match not found, proceed to check next potential
					nodeList_from_id.set(index, -2);
					toRestoreList.add(index);
					index = nodeList_from_id.indexOf(fromVertexID);
				}
			}
			// restore nodeList_from first
			for (int i = 0; i < toRestoreList.size(); i++)
				nodeList_from_id.set(toRestoreList.get(i), fromVertexID);
			if (CONTINUE == false) {
				nodeList_from.add(fromVertex);
				nodeList_to.add(toVertex);
				if (fromVertex == null)
					nodeList_from_id.add(-1);
				else
					nodeList_from_id.add(fromVertex.getID());
				if (toVertex == null)
					nodeList_to_id.add(-1);
				else
					nodeList_to_id.add(toVertex.getID());
			}
		}
		// System.out.println("done- addAssignment");
	}

	public int numAssignments() {
		return nodeList_from.size();
	}

	public void print() {
		System.out.println("nodeList_from -> nodeList_to");
		for (int i = 0; i < nodeList_from.size(); i++) {
			System.out.println(i + ": ______________________________");
			if (nodeList_from.get(i) == null)
				System.out.println("null");
			else
				nodeList_from.get(i).print();
			System.out.println("<<<<<<<<<<<< >>>>>>>>>>>>>>>>>");
			if (nodeList_to.get(i) == null)
				System.out.println("null");
			else
				nodeList_to.get(i).print();
		}
	}
}

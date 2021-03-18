/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package adjlistgraph;

import java.util.ArrayList;

/**
 *
 * @author ibmuser
 */
public class Link {

	int size = 0;
	private Node first = null;
	private Node current = null;
	private ArrayList<Integer> graphIdList = new ArrayList<Integer>();

	public void addToGraphIDList(int id) {
		graphIdList.add(id);
	}

	public void addToGraphIDList(ArrayList<Integer> idList) {
		for (int i = 0; i < idList.size(); i++)
			graphIdList.add(idList.get(i));
	}

	public ArrayList<Integer> getGraphIdList() {
		return graphIdList;
	}

	public void add(int index) {
		++size;
		Node node = new Node(index);
		node.setNext(first);
		first = node;
		current = first;
	}

	public boolean hasNext() {
		return (current != null && current.getNext() != null);
	}

	public Node next() {
		current = current.getNext();
		return current;
	}

	public void remove(int index) {
		Node previous = null;
		Node current = first;
		while (current != null) {
			if (current.getIndex() == index) {
				if (previous == null) {
					first = current;
				} else {
					previous.setNext(current.getNext());
				}

				--size;
				return;
			}

			// Advance to the 2 pointers;
			previous = current;
			current = current.getNext();
		}
	}

	public Node getFirst() {
		return first;
	}
}

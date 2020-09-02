/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exactquery;

import adjlistgraph.Link;
import adjlistgraph.Node;
import java.util.Stack;

/**
 *
 * @author c4jin
 */

// SpigVertex isnot isomorphic to all others;
public class SpindleGraph {

	private int gid; // hashcode of the last edge;
	private SpigVertex[] vertices;
	private Link[] adjTab = null;
	private int pos = -1;

	public SpindleGraph(int size) {
		vertices = new SpigVertex[size];
		pos = -1;
		adjTab = new Link[size];
		for (int i = 0; i < size; i++) {
			adjTab[i] = new Link();
		}
	}

	public void addNode(SpigVertex node) {
		vertices[++pos] = node;
	}

	public int getVNum() {
		return pos + 1;
	}

	public int getId() {
		return gid;
	}

	public void setID(int id) {
		gid = id;
	}

	public SpigVertex getNode(int index) {
		return vertices[index];
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

	public int getUnvisitedParent(int index) {
		for (int i = 0; i <= pos; i++) { // here is a reverse search
			if (hasEdge(i, index) == 1 && !vertices[i].isVisited()) {
				return i;
			}
		}
		return -1;
	}

	public void findAncesstors(int index) {
		int inital = index;
		if (vertices[index] == null) {
			return;
		}

		Stack s = new Stack();
		vertices[index].setVisited();
		s.push(index);

		while (!s.isEmpty()) {
			index = getUnvisitedParent((Integer) s.peek());
			if (index != -1) {
				vertices[index].setVisited();
				s.push(index);
				vertices[inital].setSucc(index);
			} else {
				s.pop();
			}
		}
		clean();
	}

	public void clean() {
		for (SpigVertex v : vertices) {
			if (v != null) {
				v.clean();
			}
		}
	}

	public void print() {
		for (int from = 0; from <= pos; from++) {
			System.out.print(from + ":" + vertices[from].getEdgeIdSet().toString() + " -> ");
			Node p = adjTab[from].getFirst();
			while (p != null) {
				System.out.print(p.getIndex() + ":" + vertices[p.getIndex()].getEdgeIdSet().toString() + " ");
				p = p.getNext();
			}
			System.out.println();
		}
	}
}

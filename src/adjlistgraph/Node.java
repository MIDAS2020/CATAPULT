/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package adjlistgraph;

/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: Node.java
 *
 * Abstract: Node in the ALGraph adjlist
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Feb 28,2010
 *
 */
public class Node {

	private int index;
	private Node next;

	public Node(int index) {
		this.index = index;
	}

	public void setNext(Node next) {
		this.next = next;
	}

	public Node getNext() {
		return next;
	}

	public int getIndex() {
		return index;
	}
}

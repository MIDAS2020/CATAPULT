/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

public class priorityQueueTuple {
	private Float simValue;
	private closureVertex u;
	private closureVertex vm;

	public priorityQueueTuple() {

	}

	public priorityQueueTuple(Float val, closureVertex a, closureVertex b) {
		simValue = val;
		u = a;
		vm = b;
	}

	public closureVertex getU() {
		return u;
	}

	public closureVertex getV() {
		return vm;
	}

	public Float getValue() {
		return simValue;
	}
}

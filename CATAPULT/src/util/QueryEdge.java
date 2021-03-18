/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 *
 * @author ibmuser
 */
public class QueryEdge {

	private int edgeID; // 0-based index
	private int srcID;
	private int trgID;

	public QueryEdge() {
		super();
	}

	public QueryEdge(int srcID, int trgID, int edgeID) {
		this.edgeID = edgeID;
		this.srcID = srcID;
		this.trgID = trgID;
	}

	public void setSourceID(int src) {
		srcID = src;
	}

	public void setTargetID(int trg) {
		trgID = trg;
	}

	public void setEdgeID(int id) {
		edgeID = id;
	}

	public int getSourceID() {
		return srcID;
	}

	public int getTargetID() {
		return trgID;
	}

	public int getID() {
		return edgeID;
	}
}

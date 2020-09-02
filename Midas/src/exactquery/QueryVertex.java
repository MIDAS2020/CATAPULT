
/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: QueryNodeID.java
 *
 * Abstract:
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Jun.16,2009
 *
 */
package exactquery;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Vector;

/**
 *
 * @author cjjin
 */
public class QueryVertex {
	public boolean isPattern = false;
	public int patternId = -1;
	public int coeff = -1;

	private int hashCode; // Hashcode;
	private String nodeLabelId; // id corresponding to the label;
	private int id = -1; // 0-based

	private Vector<Integer> neighbors = new Vector<Integer>();

	private int degree = 0;
	private int oldid = 0;
	private String nc = null;

	public QueryVertex() {
		super();
	}

	public QueryVertex(int hashcode, String labelID, int queryVertexNumber) {
		this.hashCode = hashcode;
		this.nodeLabelId = labelID;
		this.id = queryVertexNumber;
	}

	public void setId(int tmpid) {
		id = tmpid;
	}

	public int getID() {
		return id;
	}

	public void setHashCode(int code) {
		hashCode = code;
	}

	public int getHashCode() {
		return hashCode;
	}

	public void setNodeLabelId(String label) {
		nodeLabelId = label;
	}

	public String getNodelabel() {
		return nodeLabelId;
	}

	public void setDegree() {
		degree++;
	}

	public int getDegree() {
		return degree;
	}

	public void addNeighbor(int code) {
		neighbors.addElement(code);

	}

	public Vector<Integer> getNeighbors() {
		return neighbors;
	}

	public void setoldid(int old) {
		oldid = old;
	}

	public int getoldid() {
		return oldid;
	}

	public void setNCode(String code) {
		nc = code;
	}

	public String getNCode() {
		return nc;
	}
}


/*
* Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
*
* File name: Vertex.java
*
* Abstract: The vertex structure in query graph
*
* Current Version:      0.1
* Auther:               Jin Changjiu
* Modified Date:        Jun.16,2009
*
*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package frequentindex;

import java.util.ArrayList;

/**
 *
 * @author cjjin
 */
public class Vertex {
	public boolean isPattern = false;
	public int patternId = -1;

	private String label = null; // label;
	private int degree = 0; // degree;

	// Neighboors list;
	private ArrayList<Integer> Out = new ArrayList<Integer>();
	private ArrayList<Integer> In = new ArrayList<Integer>();

	private int num = 0; // ???

	private int id = 0; // id inside current graph;
	private int queryId = 0; // id taken from reference in another graph;
	private String strNeighbor = null;

	private ArrayList<Integer> Succ = new ArrayList<Integer>();
	private ArrayList<Integer> Pred = new ArrayList<Integer>();

	private boolean isVisited;

	public void setLabel(String lab) {
		label = lab;
	}

	public String getLabel() {
		return label;
	}

	public void setNumber(int number) {
		num = number;
	}

	public int getNumber() {
		return num;
	}

	public void setID(int nodeID) {
		id = nodeID;
	}

	public int getId() {
		return id;
	}

	public void setQueryVertexID(int nodeId) {
		queryId = nodeId;
	}

	public int getQueryVertexId() {
		return queryId;
	}

	public void incDegree() {
		degree++;
	}

	public void setDegree(int d) {
		degree = d;
	}

	public int getDegree() {
		return degree;
	}

	public void setNeighbor(String neighbor) {
		strNeighbor = neighbor;
	}

	public String getNeighbor() {
		return strNeighbor;
	}

	public void setSucc(int node) {
		Succ.add(node);
	}

	public ArrayList<Integer> getSucc() {
		return Succ;
	}

	public void setPred(int node) {
		Pred.add(node);
	}

	public ArrayList<Integer> getPred() {
		return Pred;
	}

	public void setM(int node) {
		Out.add(node);
	}

	public ArrayList<Integer> getM() {
		return Out;
	}

	public void setIn(int node) {
		In.add(node);
	}

	public ArrayList<Integer> getIn() {
		return In;
	}

	public void visit() {

		isVisited = true;

	}

	public void clean() {
		isVisited = false;
	}

	public boolean isVisited() {
		return isVisited;
	}
}
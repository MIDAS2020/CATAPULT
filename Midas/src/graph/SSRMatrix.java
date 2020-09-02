/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class SSRMatrix {
	private ArrayList<closureVertex> patternVertexList;
	private ArrayList<closureVertex> dataGraphVertexList;

	public SSRMatrix() {
		patternVertexList = new ArrayList<closureVertex>();
		dataGraphVertexList = new ArrayList<closureVertex>();
	}

	public SSRMatrix(ArrayList<closureVertex> p_list, ArrayList<closureVertex> d_list) {
		patternVertexList = new ArrayList<closureVertex>();
		dataGraphVertexList = new ArrayList<closureVertex>();
		for (int i = 0; i < p_list.size(); i++)
			patternVertexList.add(p_list.get(i));
		for (int i = 0; i < d_list.size(); i++)
			dataGraphVertexList.add(d_list.get(i));
	}

	// save a match to the SSRMatrix only if both the pattern and dataGraph vertices
	// have not been matched before
	public void addAMatch(closureVertex patternV, closureVertex dataGraphV) {
		int patternVIndex = patternVertexList.indexOf(patternV);
		int dataGraphVIndex = dataGraphVertexList.indexOf(dataGraphV);
		// check that both patternV and dataGraphV have not been matched before
		if (patternVIndex == -1 && dataGraphVIndex == -1) {
			patternVertexList.add(patternV);
			dataGraphVertexList.add(dataGraphV);
		}
	}

	// return the vertices in the pattern that has been matched
	public ArrayList<closureVertex> getMatchedPatternVertexList() {
		return patternVertexList;
	}

	// return the vertices in the data graph that has been matched
	public ArrayList<closureVertex> getMatchedDataGraphVertexList() {
		return dataGraphVertexList;
	}

	// return number of matched pairs
	public int size() {
		return patternVertexList.size();
	}

	public void print() {
		System.out.println("patternVertexList=================================");
		for (int i = 0; i < patternVertexList.size(); i++)
			patternVertexList.get(i).print();
		System.out.println("dataGraphVertexList=================================");
		for (int i = 0; i < dataGraphVertexList.size(); i++)
			dataGraphVertexList.get(i).print();
		System.out.println("===================================================");
	}
}

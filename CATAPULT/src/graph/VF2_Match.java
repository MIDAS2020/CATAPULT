/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class VF2_Match {
	
	private ArrayList<SSRMatrix> saved_pattern_dataGraph_match;
	public ArrayList<SSRMatrix> getSaved_pattern_dataGraph_match() {
		return saved_pattern_dataGraph_match;
	}

	public void setSaved_pattern_dataGraph_match(ArrayList<SSRMatrix> saved_pattern_dataGraph_match) {
		this.saved_pattern_dataGraph_match = saved_pattern_dataGraph_match;
	}

	private ArrayList<closureVertex> curr_patternVertexList;
	public ArrayList<closureVertex> getCurr_patternVertexList() {
		return curr_patternVertexList;
	}

	public void setCurr_patternVertexList(ArrayList<closureVertex> curr_patternVertexList) {
		this.curr_patternVertexList = curr_patternVertexList;
	}

	private ArrayList<closureVertex> curr_dataGraphVertexList;
	public ArrayList<closureVertex> getCurr_dataGraphVertexList() {
		return curr_dataGraphVertexList;
	}

	public void setCurr_dataGraphVertexList(ArrayList<closureVertex> curr_dataGraphVertexList) {
		this.curr_dataGraphVertexList = curr_dataGraphVertexList;
	}

	private ArrayList<closureVertex> visited_dataGraphVertexList;
	public ArrayList<closureVertex> getVisited_dataGraphVertexList() {
		return visited_dataGraphVertexList;
	}

	public void setVisited_dataGraphVertexList(ArrayList<closureVertex> visited_dataGraphVertexList) {
		this.visited_dataGraphVertexList = visited_dataGraphVertexList;
	}

	private boolean FAIL_CURRENT_BRANCH = false;
	public boolean isFAIL_CURRENT_BRANCH() {
		return FAIL_CURRENT_BRANCH;
	}

	public void setFAIL_CURRENT_BRANCH(boolean fAIL_CURRENT_BRANCH) {
		FAIL_CURRENT_BRANCH = fAIL_CURRENT_BRANCH;
	}

	private int bestNumMatch;

	public void setBestNumMatch(int bestNumMatch) {
		this.bestNumMatch = bestNumMatch;
	}
	public VF2_Match() {
		saved_pattern_dataGraph_match = new ArrayList<SSRMatrix>();
		curr_patternVertexList = new ArrayList<closureVertex>();
		curr_dataGraphVertexList = new ArrayList<closureVertex>();
		visited_dataGraphVertexList = new ArrayList<closureVertex>();
		bestNumMatch = 0;
	}

	/// added by Kai
	 public void assign(VF2_Match match) {
		 this.setBestNumMatch(match.getBestNumMatch());
		 this.setCurr_dataGraphVertexList(match.getCurr_dataGraphVertexList());
		 this.setCurr_patternVertexList(match.getCurr_patternVertexList());
		 this.setFAIL_CURRENT_BRANCH(match.getFailCurrentBranch());
		 this.setSaved_pattern_dataGraph_match(match.getSaved_pattern_dataGraph_match());
		 this.setVisited_dataGraphVertexList(match.getVisited_dataGraphVertexList());
		 System.out.println("after assign: "+match.getBestNumMatch());
	 }
	
	public closureVertex getMatchedDataGraphVertex(closureVertex patternVertex) {
		int index = curr_patternVertexList.indexOf(patternVertex);
		if (index == -1)
			return null;
		else
			return curr_dataGraphVertexList.get(index);
	}

	public void setFailCurrentBranch(boolean flag) {
		FAIL_CURRENT_BRANCH = flag;
	}

	public boolean getFailCurrentBranch() {
		return FAIL_CURRENT_BRANCH;
	}

	// get best number of matched vertices between pattern and dataGraph
	public int getBestNumMatch() {
		// return bestNumMatch;
		return curr_patternVertexList.size();
	}

	// save a match to the SSRMatrix only if both the pattern and dataGraph vertices
	// have not been matched before
	public void addAMatch(closureVertex patternV, closureVertex dataGraphV) {
		int patternVIndex = curr_patternVertexList.indexOf(patternV);
		int dataGraphVIndex = curr_dataGraphVertexList.indexOf(dataGraphV);
		// check that both patternV and dataGraphV have not been matched before
		if (patternVIndex == -1 && dataGraphVIndex == -1) {
			curr_patternVertexList.add(patternV);
			curr_dataGraphVertexList.add(dataGraphV);
		}
		// update bestNumMatch
		if (curr_patternVertexList.size() > bestNumMatch)
			bestNumMatch = curr_patternVertexList.size();
	}

	// rollback, remove last match
	public void removeLastMatch() {
		if (curr_patternVertexList.size() == 0)
			return;

		curr_patternVertexList.remove(curr_patternVertexList.size() - 1);
		curr_dataGraphVertexList.remove(curr_dataGraphVertexList.size() - 1);
	}

	public ArrayList<closureVertex> getVisitedDataGraphVertex() {
		return visited_dataGraphVertexList;
	}

	public void pushCurrToSavedMatch() {
		SSRMatrix currMatch = new SSRMatrix(curr_patternVertexList, curr_dataGraphVertexList);
		saved_pattern_dataGraph_match.add(currMatch);
		for (int i = 0; i < curr_dataGraphVertexList.size(); i++) {
			if (visited_dataGraphVertexList.contains(curr_dataGraphVertexList.get(i)) == false)
				visited_dataGraphVertexList.add(curr_dataGraphVertexList.get(i));
		}
		curr_patternVertexList = new ArrayList<closureVertex>();
		curr_dataGraphVertexList = new ArrayList<closureVertex>();
	}

	// return the vertices in the pattern that has been matched
	public ArrayList<closureVertex> getMatchedPatternVertexList() {
		return curr_patternVertexList;
	}

	// return the vertices in the data graph that has been matched
	public ArrayList<closureVertex> getMatchedDataGraphVertexList() {
		return curr_dataGraphVertexList;
	}

	// return number of matched pairs
	public int size() {
		return curr_patternVertexList.size();
	}

	public void print() {
		if (curr_patternVertexList == null || curr_dataGraphVertexList == null)
			return;

		System.out.println("curr_patternVertexList=================================");
		for (int i = 0; i < curr_patternVertexList.size(); i++)
			curr_patternVertexList.get(i).print();
		System.out.println("curr_dataGraphVertexList=================================");
		for (int i = 0; i < curr_dataGraphVertexList.size(); i++)
			curr_dataGraphVertexList.get(i).print();
		System.out.println("===================================================");
	}
}

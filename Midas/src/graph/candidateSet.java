/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class candidateSet {
	private ArrayList<Integer> clusterID;
	private ArrayList<ArrayList<closureEdge>> patternList_edge;
	private ArrayList<ArrayList<String>> patternList_string;
	private ArrayList<Integer> potentialOrphanGraphID;

	public candidateSet() {
		clusterID = new ArrayList<Integer>();
		patternList_edge = new ArrayList<ArrayList<closureEdge>>();
		patternList_string = new ArrayList<ArrayList<String>>();
		potentialOrphanGraphID = new ArrayList<Integer>();
	}

	public ArrayList<Integer> getClusterIDList() {
		return clusterID;
	}

	public int getClusterID(int i) {
		if (i >= 0 && i < clusterID.size())
			return clusterID.get(i);
		else
			return -1;
	}

	public void addToPotentialOrphanGraphID(ArrayList<Integer> idList) {
		for (int i = 0; i < idList.size(); i++) {
			if (potentialOrphanGraphID.contains(idList.get(i)) == false)
				potentialOrphanGraphID.add(idList.get(i));
		}
	}

	public ArrayList<Integer> getPotentialOrphanGraphID() {
		return potentialOrphanGraphID;
	}

	public int size() {
		return patternList_edge.size();
	}

	public void addToCandidateSet(int cluster_id, ArrayList<closureEdge> pattern) {
		// if a pattern is added to candidate set, it must have achieved the
		// requirements and its graph IDs should not be orphaned.
		// reset the potentialOrphanGraphID to get ready for next candidate check
		potentialOrphanGraphID = new ArrayList<Integer>();

		ArrayList<String> patternString = new ArrayList<String>();
		boolean PATTERN_ALREADY_EXIST = false;
		// get the pattern string
		for (int i = 0; i < pattern.size(); i++) {
			closureEdge e = pattern.get(i);
			patternString.add(e.getEdgeString());
		}
		// System.out.println("patternString: "+patternString.toString());
		// check if patternString is found in patternList_string. If found=pattern
		// exists in patternList_edge - do nothing.
		// If not found, add pattern to patternList_edge and add patternString to
		// patternList_string
		for (int i = 0; i < patternList_string.size(); i++) {
			ArrayList<String> patternString_workingCopy = new ArrayList<String>();
			ArrayList<String> patternList_string_currElement = patternList_string.get(i);
			for (int j = 0; j < patternString.size(); j++)
				patternString_workingCopy.add(patternString.get(j));
			// System.out.println(i+"||
			// patternList_string_currElement:"+patternList_string_currElement.toString());
			System.out.println("patternString size: " + patternString.size() + " patternList_string_currElement size: "
					+ patternList_string_currElement.size());
			boolean STOP = false;
			for (int j = 0; j < patternList_string_currElement.size() && !STOP; j++) {
				String s = patternList_string_currElement.get(j);
				int index = patternString_workingCopy.indexOf(s);
				if (index != -1)
					patternString_workingCopy.remove(index);
				if (patternString.size() == 0)
					STOP = true;
			}
			// patternString_workingCopy.removeAll(patternList_string_currElement);
			if (patternString_workingCopy.size() == 0)// patternString is an exact replica of
														// patternList_string_currElement. So, pattern can be found in
														// patternList_string
			{
				PATTERN_ALREADY_EXIST = true;
				i = patternList_string.size();
			}
		}
		if (PATTERN_ALREADY_EXIST == false)// pattern not found in patternList_edge
		{
			System.out.println("add a pattern: " + patternString.toString());
			patternList_edge.add(pattern);
			patternList_string.add(patternString);
			clusterID.add(cluster_id);
		}
	}

	// return the ith candidate pattern in patternList_edge
	public ArrayList<closureEdge> getCandidateAt(int i) {
		if (patternList_edge == null || patternList_edge.size() == 0)
			return null;
		if (i >= 0 && i < patternList_edge.size())
			return patternList_edge.get(i);
		else
			return null;
	}

	// return all candidate patterns in patternList_edge
	public ArrayList<ArrayList<closureEdge>> getCandidateList() {
		return patternList_edge;
	}

	public void print() {
		System.out.println("candidateSet:");
		for (int i = 0; i < patternList_edge.size(); i++) {
			System.out.println("candidate pattern " + i + ": --------------------------------");
			ArrayList<closureEdge> edgeList = patternList_edge.get(i);
			for (int j = 0; j < edgeList.size(); j++)
				edgeList.get(j).print();
			System.out.println("pattern string: " + patternList_string.get(i).toString());
		}
		System.out.println("potentialOrphanGraphID: " + potentialOrphanGraphID.toString());
	}
}

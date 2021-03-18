/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class closureEdge {
	private closureVertex source;
	private closureVertex target;
	private ArrayList<Integer> graphIDList = new ArrayList<Integer>();
	private int wt;
	private int freq;
	private int costValue;
	private boolean matched;

	public closureEdge(closureVertex s, closureVertex t) {
		source = s;
		target = t;
		wt = 0;
		freq = 1;
		costValue = 0;
		matched = false;
	}

	public closureEdge(closureVertex s, closureVertex t, ArrayList<Integer> list) {
		source = s;
		target = t;
		for (int i = 0; i < list.size(); i++) {
			if (graphIDList.contains(list.get(i)) == false)
				graphIDList.add(list.get(i));
		}
		wt = 0;
		freq = 1;
	}

	public closureEdge(closureVertex s, closureVertex t, ArrayList<Integer> list, int w) {
		source = s;
		target = t;
		for (int i = 0; i < list.size(); i++) {
			if (graphIDList.contains(list.get(i)) == false)
				graphIDList.add(list.get(i));
		}
		wt = w;
		freq = 1;
	}

	public closureEdge(closureEdge e) {
		source = e.getSource();
		target = e.getTarget();
		wt = e.getWeight();
		costValue = e.getCostValue();
		matched = false;
		freq = e.getFreq();
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int f) {
		freq = f;
	}

	public void addToGraphIDList(int id) {
		if (graphIDList.contains(id) == false)
			graphIDList.add(id);
	}

	public ArrayList<Integer> getGraphIDList() {
		return graphIDList;
	}

	public closureVertex getSource() {
		return source;
	}

	public closureVertex getTarget() {
		return target;
	}

	public int getWeight() {
		return wt;
	}

	public void setWeight(int w) {
		wt = w;
	}

	public void setCostValue(int c) {
		costValue = c;
	}

	public int getCostValue() {
		return costValue;
	}

	public void markMatched() {
		matched = true;
	}

	public boolean getMatched() {
		return matched;
	}

	public void markUnmatched() {
		matched = false;
	}

	public String getEdgeLabelString() {
		String sourceLabel = getSource().getLabel().get(0);
		String targetLabel = getTarget().getLabel().get(0);
		if (sourceLabel.compareTo(targetLabel) <= 0)
			return sourceLabel + " " + targetLabel;
		else
			return targetLabel + " " + sourceLabel;
	}

	public String getEdgeString() {
		int sourceID = getSource().getID();
		int targetID = getTarget().getID();
		if (sourceID <= targetID)
			return sourceID + " " + targetID;
		else
			return targetID + " " + sourceID;
	}
	


	public String getEdgeUnorderedString() {
		int sourceID = getSource().getID();
		int targetID = getTarget().getID();
		return sourceID + " " + targetID;
	}

	public void print() {
		/*
		if (source == null && target == null)
			System.out.println("NULL");
		else
			System.out.println(graphIDList.toString() + ": " + source.getID() + source.getLabel().toString() + ","
					+ source.getWt() + "--" + target.getID() + target.getLabel().toString() + "," + target.getWt()
					+ " edge wt:" + wt + " costValue:" + costValue + " matched:" + matched + " freq=" + freq);
					*/
	}
}

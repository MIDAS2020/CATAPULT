/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;

public class pattern {
	private ArrayList<closureEdge> patternEdgeList;// this contains the pattern edge list
	private int currChildIndex;// keep track of which child we are currently at
	private boolean STOP_MINING;
	private ArrayList<pattern> childEdgeList;// for some edges, they have the same jaccard values and we branch further
	private pattern parent;
	private ArrayList<Integer> potentialOrphanGraphID;

	public pattern() {
		this.patternEdgeList = new ArrayList<closureEdge>();// init empty edge list
		this.childEdgeList = new ArrayList<pattern>();// init empty child list
		this.currChildIndex = -1;
		this.STOP_MINING = false;
		this.parent = null;
		this.potentialOrphanGraphID = new ArrayList<Integer>();
	}

	public void setPotentialOrphanGraphID(ArrayList<Integer> idList) {
		for (int i = 0; i < idList.size(); i++) {
			if (potentialOrphanGraphID.contains(idList.get(i)) == false)
				potentialOrphanGraphID.add(idList.get(i));
		}
	}

	public ArrayList<Integer> getPotentialOrphanGraphID() {
		return potentialOrphanGraphID;
	}

	public pattern getParent() {
		return parent;
	}

	public void setParent(pattern p) {
		this.parent = p;
	}

	public void setStopMiningFlag(boolean flag) {
		STOP_MINING = flag;
	}

	public boolean getStopMiningFlag() {
		return STOP_MINING;
	}

	public int getCurrChildIndex() {
		return this.currChildIndex;
	}

	public pattern getCurrChildPattern() {
		if (this.currChildIndex >= 0 && this.currChildIndex < this.childEdgeList.size())
			return this.childEdgeList.get(this.currChildIndex);
		else
			return null;
	}

	public ArrayList<closureEdge> getCurrBranchEdgeList() {
		return this.patternEdgeList;
	}

	public void addEdgeToPattern(closureEdge e) {
		System.out.println("[addEdgeToPattern] currChildIndex=" + this.currChildIndex);
		if (this.currChildIndex == -1) {
			if (this.patternEdgeList.contains(e) == false)
				this.patternEdgeList.add(e);
		} else {
			if (this.currChildIndex < this.childEdgeList.size()) {
				pattern currChildPattern = this.childEdgeList.get(this.currChildIndex);
				currChildPattern.addEdgeToPattern(e);
			}
		}
	}

	public void createChildBranches(ArrayList<closureEdge> e_commonJaccard_list) {
		this.currChildIndex = 0;
		for (int i = 0; i < e_commonJaccard_list.size(); i++) {
			closureEdge e = e_commonJaccard_list.get(i);
			pattern child_pattern = new pattern();
			child_pattern.addEdgeToPattern(e);
			child_pattern.setParent(this);
			this.childEdgeList.add(child_pattern);
		}
	}

	public void setToNextCurrChildIndex() {
		if (this.currChildIndex + 1 < this.childEdgeList.size())
			this.currChildIndex++;
	}

	public void setToLastCurrChildIndex() {
		this.currChildIndex = this.childEdgeList.size() - 1;
	}

	public int getNumChildren() {
		return childEdgeList.size();
	}

	public int size() {
		if (this.currChildIndex == -1)
			return this.patternEdgeList.size();
		else {
			// return the size for the curr pattern branch
			pattern childPattern = getCurrChildPattern();
			return patternEdgeList.size() + childPattern.size();

			// int minChildSize=1000;//some arbitrary large number
			// for(int i=0; i<this.childEdgeList.size(); i++)
			// {
			// //check each child size and return smallest size
			// int currChildSize;
			// pattern childPattern=this.childEdgeList.get(i);
			// currChildSize=this.patternEdgeList.size()+childPattern.size();
			// if(currChildSize<minChildSize)
			// minChildSize=currChildSize;
			// }
			// return minChildSize;
		}
	}

	public void resetAllChildIndexPos() {
		this.STOP_MINING = false;
		if (this.currChildIndex == -1)
			return;
		else {
			if (this.currChildIndex > 0) {
				this.currChildIndex = 0;
				for (int i = 0; i < getNumChildren(); i++)
					childEdgeList.get(i).resetAllChildIndexPos();
			}
		}
	}

	public void print() {
		System.out.println("pattern:+++++++++++++++++++++++++++++++++");
		for (int i = 0; i < this.patternEdgeList.size(); i++)
			this.patternEdgeList.get(i).print();
		System.out.println("currChildIndex:" + this.currChildIndex + " STOP_MINING:" + STOP_MINING);
		for (int i = 0; i < this.childEdgeList.size(); i++) {
			pattern childPattern = this.childEdgeList.get(i);
			System.out.print("child " + i + " :");
			childPattern.print();
		}
	}
}

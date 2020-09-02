/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;

//larger numCommonGraphID are desired. If there are multiple numCommonGraphID that are maximum, then, choose the one with larger jaccard.
//If there are multiple numCommonGraphID that are maximum and their jaccard are all the same, randomly return one index
public class patternEdgeScore {
	ArrayList<Integer> numCommonGraphID;
	ArrayList<Float> jaccard;

	public patternEdgeScore() {
		numCommonGraphID = new ArrayList<Integer>();
		jaccard = new ArrayList<Float>();
	}

	public void add(int num, float j) {
		numCommonGraphID.add(num);
		jaccard.add(j);
	}

	private boolean multipleMaxNumCommonGraphID() {
		int max = Collections.max(numCommonGraphID);
		int index = numCommonGraphID.indexOf(max);
		numCommonGraphID.set(index, -1);
		int nextMax = Collections.max(numCommonGraphID);
		if (max == nextMax) {
			numCommonGraphID.set(index, max);
			return true;
		} else
			return false;
	}

	private int getIndexOfMaxNumCommonGraphID() {
		int max = Collections.max(numCommonGraphID);
		int index = numCommonGraphID.indexOf(max);
		return index;
	}

	public ArrayList<Integer> getIndexOfBestScore() {
		ArrayList<Integer> bestIndex = new ArrayList<Integer>();

		if (multipleMaxNumCommonGraphID() == true) {
			ArrayList<Integer> indicesOfMaxNumCommonGraphID = new ArrayList<Integer>();
			ArrayList<Float> correspondingJaccard = new ArrayList<Float>();
			ArrayList<Integer> workingCopy_commonGraphID = new ArrayList<Integer>();
			// identify all the multiple instances of maximum numCommonGraphID (set A)
			for (int i = 0; i < numCommonGraphID.size(); i++)
				workingCopy_commonGraphID.add(numCommonGraphID.get(i));
			int max = Collections.max(workingCopy_commonGraphID);
			int curr_max;
			int index = workingCopy_commonGraphID.indexOf(max);
			boolean CONTINUE = true;
			indicesOfMaxNumCommonGraphID.add(index);
			correspondingJaccard.add(jaccard.get(index));
			while (CONTINUE) {
				workingCopy_commonGraphID.set(index, -1);
				curr_max = Collections.max(workingCopy_commonGraphID);
				if (curr_max == max) {
					index = workingCopy_commonGraphID.indexOf(curr_max);
					indicesOfMaxNumCommonGraphID.add(index);
					correspondingJaccard.add(jaccard.get(index));
				} else
					CONTINUE = false;
			}
			// identify all the multiple instances of maximum jaccard that correspond to set
			// A
			Float maxJaccard = Collections.max(correspondingJaccard);
			int maxJaccardIndex = correspondingJaccard.indexOf(maxJaccard);
			ArrayList<Integer> indicesOfMaxJaccardInCorrespondingJaccard = new ArrayList<Integer>();
			indicesOfMaxJaccardInCorrespondingJaccard.add(maxJaccardIndex);
			Float curr_maxJaccard, epsilon = 0.000001f;
			CONTINUE = true;
			while (CONTINUE) {
				correspondingJaccard.set(maxJaccardIndex, -1f);
				curr_maxJaccard = Collections.max(correspondingJaccard);
				if (Math.abs(curr_maxJaccard - maxJaccard) < epsilon) {
					maxJaccardIndex = correspondingJaccard.indexOf(curr_maxJaccard);
					indicesOfMaxJaccardInCorrespondingJaccard.add(maxJaccardIndex);
				} else
					CONTINUE = false;
			}
			for (int i = 0; i < indicesOfMaxJaccardInCorrespondingJaccard.size(); i++) {
				int indexJaccard = indicesOfMaxJaccardInCorrespondingJaccard.get(i);
				bestIndex.add(indicesOfMaxNumCommonGraphID.get(indexJaccard));
			}
		} else
			bestIndex.add(getIndexOfMaxNumCommonGraphID());

		return bestIndex;
	}

	public void print() {
		System.out.println("numCommonGraphID=" + numCommonGraphID.toString());
		System.out.println("jaccard=" + jaccard.toString());
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
* Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
*
* File name: FeatureRecord.java
*
* Abstract: Record the feature in the Node of SPIG
*
* Current Version:      0.1
* Auther:               Jin Changjiu
* Modified Date:        Feb 28,2010
*
*/
package exactquery;

import java.util.HashSet;

/**
 *
 * @author cjjin
 */
public class FeatureRecord {

	private HashSet<Integer> difset = new HashSet<Integer>();
	private HashSet<Integer> freqset = new HashSet<Integer>();

	public void addFreqFeatures(HashSet<Integer> idset) {
		freqset.addAll(idset);
	}

	public void addDifFeatures(HashSet<Integer> idset) {
		difset.addAll(idset);
	}

	public HashSet<Integer> getFreqFeatures() {
		return freqset;
	}

	public HashSet<Integer> getDifFeatures() {
		return difset;
	}

}

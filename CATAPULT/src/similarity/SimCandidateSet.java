/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity;

import java.util.HashSet;
/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: Candidates.java
 *
 * Abstract: The candidate structure contains verification-free part and verification part.
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Mar,3 2010
 *
 */

/**
 *
 * @author cjjin
 */
public class SimCandidateSet {

	private HashSet<Integer> verifyPart = new HashSet<Integer>();// the candidates need verification
	private HashSet<Integer> noVerifyPart = new HashSet<Integer>();// the candidates free of verification

	public void setVerifyPart(HashSet<Integer> set) {
		verifyPart.addAll(set);
	}

	public HashSet<Integer> getVerifyPart() {
		return verifyPart;
	}

	public void addFreeVerifyPart(HashSet<Integer> set) {
		noVerifyPart.addAll(set);
	}

	public HashSet<Integer> getNoVerifyPart() {
		return noVerifyPart;
	}
}

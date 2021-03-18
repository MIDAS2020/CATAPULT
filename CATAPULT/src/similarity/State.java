
/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: Pair.java
 *
 * Abstract: used in SimVerify
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Oct.16,2009
 *
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity;

import java.util.ArrayList;

/**
 *
 * @author cjjin
 */
public class State {

	private ArrayList<Integer> M1 = new ArrayList<Integer>();
	private ArrayList<Integer> M2 = new ArrayList<Integer>();
	private ArrayList<Integer> Tin1 = new ArrayList<Integer>();
	private ArrayList<Integer> Tin2 = new ArrayList<Integer>();
	private ArrayList<Integer> Tout1 = new ArrayList<Integer>();
	private ArrayList<Integer> Tout2 = new ArrayList<Integer>();
	private ArrayList<Integer> N1 = new ArrayList<Integer>();
	private ArrayList<Integer> N2 = new ArrayList<Integer>();

	private int edgenum = 0;

	public void addToM1(int node) {
		M1.add(node);
	}

	public void addToM2(int node) {
		M2.add(node);
	}

	public void addToTin1(int node) {
		Tin1.add(node);
	}

	public void addToTin2(int node) {
		Tin2.add(node);
	}

	public void addToTout1(int node) {
		Tout1.add(node);
	}

	public void addToTout2(int node) {
		Tout2.add(node);
	}

	public void addToN1(int node) {
		N1.add(node);
	}

	public void addToN2(int node) {
		N2.add(node);
	}

	public void edgeNumberInc() {
		++edgenum;
	}

	public int getEdgeNumber() {
		return edgenum;
	}

	public void clearEdgeNumber() {
		edgenum = 0;
	}

	public ArrayList<Integer> getM1() {
		return M1;
	}

	public ArrayList<Integer> getM2() {
		return M2;
	}

	public ArrayList<Integer> getTin1() {
		return Tin1;
	}

	public ArrayList<Integer> getTin2() {
		return Tin2;
	}

	public ArrayList<Integer> getTout1() {
		return Tout1;
	}

	public ArrayList<Integer> getTout2() {
		return Tout2;
	}

	public ArrayList<Integer> getN1() {
		return N1;
	}

	public ArrayList<Integer> getN2() {
		return N2;
	}
}

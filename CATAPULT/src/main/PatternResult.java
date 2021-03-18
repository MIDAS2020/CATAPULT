package main;

import java.util.ArrayList;

public class PatternResult {
	   private  ArrayList<Double>  subgraphcoverage;
       private  ArrayList<Double>  patternscore;
       private  ArrayList<Double>  GED;
       private  ArrayList<Double>  COG;
       private  ArrayList<Double>  LCOV;
       private  ArrayList<ArrayList<Integer>>   coveredgraphIDs;
       private  ArrayList<ArrayList<Integer>>    coveredgraphids_byEdges;
	public ArrayList<Double> getSubgraphcoverage() {
		return subgraphcoverage;
	}
	public void setSubgraphcoverage(ArrayList<Double> subgraphcoverage) {
		this.subgraphcoverage = subgraphcoverage;
	}
	public ArrayList<Double> getPatternscore() {
		return patternscore;
	}
	public void setPatternscore(ArrayList<Double> patternscore) {
		this.patternscore = patternscore;
	}
	public ArrayList<ArrayList<Integer>> getCoveredgraphIDs() {
		return coveredgraphIDs;
	}
	public void setCoveredgraphIDs(ArrayList<ArrayList<Integer>> coveredgraphIDs) {
		this.coveredgraphIDs = coveredgraphIDs;
	}
	public ArrayList<Double> getGED() {
		return GED;
	}
	public void setGED(ArrayList<Double> gED) {
		GED = gED;
	}
	public ArrayList<Double> getCOG() {
		return COG;
	}
	public void setCOG(ArrayList<Double> cOG) {
		COG = cOG;
	}
	public ArrayList<Double> getLCOV() {
		return LCOV;
	}
	public void setLCOV(ArrayList<Double> lCOV) {
		LCOV = lCOV;
	}
	public ArrayList<ArrayList<Integer>> getCoveredgraphids_byEdges() {
		return coveredgraphids_byEdges;
	}
	public void setCoveredgraphids_byEdges(ArrayList<ArrayList<Integer>> coveredgraphids_byEdges) {
		this.coveredgraphids_byEdges = coveredgraphids_byEdges;
	}
}

package main;

import java.util.ArrayList;

import graph.JGraphtClosureGraph;
import graph.VF2;
import graph.VF2_Match;
import graph.closureEdge;

public class CollapsePattern {
	private ArrayList<ArrayList<closureEdge>> distinctCandidatePatterns = new ArrayList<ArrayList<closureEdge>>();
	private ArrayList<ArrayList<closureEdge>> candidatePatterns = new ArrayList<ArrayList<closureEdge>>();
	private VF2 VF2_operator = new VF2();

	public CollapsePattern(ArrayList<ArrayList<closureEdge>> cPatterns) {
		for (int i = 0; i < cPatterns.size(); i++)
			candidatePatterns.add(cPatterns.get(i));
	}

	public ArrayList<ArrayList<closureEdge>> getDistinctPattern() {
		return distinctCandidatePatterns;
	}
	private void collapsePattern() {
		boolean PRINT = false;
		// System.out.println("Starting to run collapse pattern thread "+threadName);
		distinctCandidatePatterns = new ArrayList<ArrayList<closureEdge>>();
		if(candidatePatterns.size() <= 0)  {
			System.out.println("candidatePatterns.size() <= 0");
			return ;
		}
		distinctCandidatePatterns.add(candidatePatterns.get(0));
		candidatePatterns.remove(0);
		while (candidatePatterns.size() > 0) {
			boolean FOUND = false;
			ArrayList<closureEdge> pattern = candidatePatterns.get(0);
			if (pattern.size() > 0) {
				JGraphtClosureGraph patternCGraph = new JGraphtClosureGraph(pattern);
				for (int i = 0; i < distinctCandidatePatterns.size() && !FOUND; i++) {
					ArrayList<closureEdge> dPattern = distinctCandidatePatterns.get(i);
					JGraphtClosureGraph distinctCGraph = new JGraphtClosureGraph(dPattern);
					// skip if size is different
					if (patternCGraph.getClosureEdgeList().size() == distinctCGraph.getClosureEdgeList().size()
							&& patternCGraph.getClosureVertexList().size() == distinctCGraph.getClosureVertexList()
									.size()) {
						VF2_Match matchedPairs = new VF2_Match();
						matchedPairs = VF2_operator.doVF2(matchedPairs, patternCGraph, distinctCGraph, false);
						if (matchedPairs == null) {
							// not found, continue;
						} else {
							if (matchedPairs.getBestNumMatch() == patternCGraph.getClosureVertexList().size()) {
								// FOUND!
								if (PRINT) {
									for (int p = 0; p < pattern.size(); p++)
										pattern.get(p).print();
								}
								FOUND = true;
							} else {
								// not found, continue;
							}
						}
					}
				}
				if (!FOUND)// this pattern is distinct!!
				{
					if(pattern == null) continue;
					distinctCandidatePatterns.add(pattern);
				}
			}
			candidatePatterns.remove(0);
		}
		// System.out.println("Finishing collapse pattern thread "+threadName);
	}
}

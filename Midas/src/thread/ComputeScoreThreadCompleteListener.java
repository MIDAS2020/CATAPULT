/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread;

import graph.closureEdge;
import java.util.ArrayList;

public class ComputeScoreThreadCompleteListener implements TaskListener {
	ArrayList<ArrayList<closureEdge>> distinctCandidatePatterns = new ArrayList<ArrayList<closureEdge>>();
	ArrayList<ArrayList<Integer>> distinctCandidatePatterns_closureIndex = new ArrayList<ArrayList<Integer>>();
	ArrayList<Double> distinctCandidatePatterns_score = new ArrayList<Double>();

	public ComputeScoreThreadCompleteListener() {
		System.out.println("initializing ComputeScoreThreadCompleteListener ... ");
	}

	public ArrayList<ArrayList<closureEdge>> getCandidatePatterns() {
		return distinctCandidatePatterns;
	}

	public ArrayList<Double> getCandidatePatternScore() {
		return distinctCandidatePatterns_score;
	}

	public ArrayList<ArrayList<Integer>> getCandidatePatternClosureIndex() {
		return distinctCandidatePatterns_closureIndex;
	}

	@Override
	public void threadComplete(Runnable runner) {
		ComputeScoreThread t = (ComputeScoreThread) runner;
		// System.out.println("thread name notify completion: "+t.getName());
		ArrayList<ArrayList<closureEdge>> t_patterns = t.getCandidatePatterns();
		ArrayList<ArrayList<Integer>> t_patterns_closureIndex = t.getCandidatePatternsClosureIndex();
		ArrayList<Double> t_pattern_score = t.getCandidatePatternsScore();

		if (t_patterns.size() > 0) {
			for (int i = 0; i < t_patterns.size(); i++) {
				if (t_pattern_score.get(i) != null) {
					ArrayList<closureEdge> t_patterns_ele = t_patterns.get(i);
					distinctCandidatePatterns.add(t_patterns_ele);
					// System.out.println(t.getName()+" adding candidatePattern "+i);
					ArrayList<Integer> t_patterns_closureIndex_ele = t_patterns_closureIndex.get(i);
					distinctCandidatePatterns_closureIndex.add(t_patterns_closureIndex_ele);
					distinctCandidatePatterns_score.add(t_pattern_score.get(i));
					// System.out.println(t.getName()+" adding candidatePatterns_score "+i);
				}
			}
		}
	}
}

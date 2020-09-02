/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread;

import graph.closureEdge;
import java.util.ArrayList;

public class GeneratePatternThreadCompleteListenerUpdated implements TaskListener {
	ArrayList<ArrayList<closureEdge>> candidatePatterns = new ArrayList<ArrayList<closureEdge>>();
	ArrayList<Integer> candidatePatterns_closureIndex = new ArrayList<Integer>();

	public GeneratePatternThreadCompleteListenerUpdated(ArrayList<ArrayList<closureEdge>> patterns,
			ArrayList<Integer> pattern_closureIndex) {
		candidatePatterns = patterns;
		candidatePatterns_closureIndex = pattern_closureIndex;
		// System.out.println("initializing GeneratePatternThreadCompleteListener ...
		// candidatePatterns="+candidatePatterns.size());
	}

	public ArrayList<ArrayList<closureEdge>> getCandidatePatterns() {
		return candidatePatterns;
	}

	public ArrayList<Integer> getCandidatePatternClosureIndex() {
		return candidatePatterns_closureIndex;
	}

	@Override
	public void threadComplete(Runnable runner) {
		GeneratePatternThreadUpdated t = (GeneratePatternThreadUpdated) runner;
		// System.out.println("thread name notify completion: "+t.getName());
		ArrayList<ArrayList<closureEdge>> t_patterns = t.getCandidatePatterns();
		ArrayList<Integer> t_pattern_closureIndex = t.getCandidatePatternsClosureIndex();
		for (int i = 0; i < t_patterns.size(); i++) {
			ArrayList<closureEdge> t_patterns_ele = t_patterns.get(i);
			candidatePatterns.add(t_patterns_ele);
			// System.out.println(t.getName()+" adding candidatePattern "+i);
			candidatePatterns_closureIndex.add(t_pattern_closureIndex.get(i));
			// System.out.println(t.getName()+" adding candidatePattern_closureIndex "+i);
		}
	}
}

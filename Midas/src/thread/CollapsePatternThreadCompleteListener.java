/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread;

import graph.closureEdge;
import java.util.ArrayList;

public class CollapsePatternThreadCompleteListener implements TaskListener {
	private ArrayList<ArrayList<closureEdge>> distinctCandidatePatterns = new ArrayList<ArrayList<closureEdge>>();
	private ArrayList<ArrayList<Integer>> distinctCandidatePatterns_closureIndex = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<closureEdge>> candidatePatterns;
	private ArrayList<Integer> candidateClosureIndex;
	private ArrayList<ArrayList<Integer>> thread_patternIndex;
	private ArrayList<Thread> threadList;
	private ArrayList<String> threadNameList;

	public CollapsePatternThreadCompleteListener(ArrayList<ArrayList<Integer>> t_patternIndex,
			ArrayList<ArrayList<closureEdge>> cPatterns, ArrayList<Integer> cClosureIndex, ArrayList<Thread> t_list,
			ArrayList<String> tName_list) {
		distinctCandidatePatterns = new ArrayList<ArrayList<closureEdge>>();
		distinctCandidatePatterns_closureIndex = new ArrayList<ArrayList<Integer>>();
		candidatePatterns = cPatterns;
		candidateClosureIndex = cClosureIndex;
		thread_patternIndex = t_patternIndex;
		threadList = t_list;
		threadNameList = tName_list;
	}

	public ArrayList<ArrayList<closureEdge>> getDistinctPatterns() {
		return distinctCandidatePatterns;
	}

	public ArrayList<ArrayList<Integer>> getDistinctPatternsClosureIndex() {
		return distinctCandidatePatterns_closureIndex;
	}

	@Override
	public void threadComplete(Runnable runner) {
		//System.out.println("**************HEREHERE1111************");
		CollapsePatternThread t = (CollapsePatternThread) runner;
		// System.out.println("thread name notify completion: "+t.getName());
		ArrayList<ArrayList<closureEdge>> t_distinctPattern = t.getDistinctPattern();
		ArrayList<ArrayList<Integer>> t_distinctPatternClosureIndex = t.getDistinctPatternClosureIndex();
	//	for (int i = 0; i < t_distinctPattern.size(); i++)
	//	{
	//		if(t_distinctPattern.get(i) == null) continue;
	//		if(t_distinctPatternClosureIndex.get(i) == null) continue;
			
	//		distinctCandidatePatterns.add(t_distinctPattern.get(i));
	//		distinctCandidatePatterns_closureIndex.add(t_distinctPatternClosureIndex.get(i));
	//	}
		
		System.out.println("Kai1:"+ t_distinctPattern.size());
		System.out.println("Kai2:"+ t_distinctPatternClosureIndex.size());
		
		for (int i = 0; i < t_distinctPattern.size(); i++) {
			if(t_distinctPattern.get(i) == null) continue;
			if(i >=  t_distinctPatternClosureIndex.size() )  continue;
			distinctCandidatePatterns.add(t_distinctPattern.get(i));
			 distinctCandidatePatterns_closureIndex.add(t_distinctPatternClosureIndex.get(i));
		}
		 
		// for (int i = 0; i < t_distinctPatternClosureIndex.size(); i++) {
		//	 if(t_distinctPatternClosureIndex.get(i) == null) continue;
		//	 distinctCandidatePatterns_closureIndex.add(t_distinctPatternClosureIndex.get(i));
		// }
		int index = threadNameList.indexOf(t.getName());
		// remove the entry in threadNameList and threadList with the index
		// threadNameList.remove(index);
		// threadList.remove(index);

		// check if more to go
		if (thread_patternIndex.size() > 0) {
			System.out.println("still have more sizes to process!! sizes left: " + thread_patternIndex.size());
			ArrayList<ArrayList<closureEdge>> thisThread_candidatePattern = new ArrayList<ArrayList<closureEdge>>();
			ArrayList<Integer> thisThread_candidateClosureIndex = new ArrayList<Integer>();
			//System.out.println(thread_patternIndex.size());
			ArrayList<Integer> thisThread_patternIndex  = new ArrayList<Integer>();
			if(thread_patternIndex.size() > 0)   thisThread_patternIndex = thread_patternIndex.get(0);
			for (int j = 0; j < thisThread_patternIndex.size(); j++) {
				thisThread_candidatePattern.add(candidatePatterns.get(thisThread_patternIndex.get(j)));
				thisThread_candidateClosureIndex.add(candidateClosureIndex.get(thisThread_patternIndex.get(j)));
			}
			CollapsePatternThread th = new CollapsePatternThread(thisThread_candidatePattern,
					thisThread_candidateClosureIndex, index, this);
			Thread gpThread = new Thread(th);
			gpThread.start();
			threadNameList.add(th.getName());
			threadList.add(gpThread);
			//System.out.println(thread_patternIndex.size());
			if(thread_patternIndex.size() > 0) 
			     thread_patternIndex.remove(0);
			System.out.println("spun new thread " + th.getName() + " sizes left:" + thread_patternIndex.size());
		} else {
			System.out.println("distinctCandidatePatternsCollapsePatternThreadCompleteListener: " + distinctCandidatePatterns.size()
					+ " distinctCandidatePatterns_closureIndexCollapsePatternThreadCompleteListener:" + distinctCandidatePatterns_closureIndex.size());
		}
		//System.out.println("**************HEREHERE2222************");
	}
}

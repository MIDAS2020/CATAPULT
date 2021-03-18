package thread;

import graph.JGraphtClosureGraph;
import graph.VF2_Match;
import graph.VF2;
import thread.NotificationThread;
import graph.closureEdge;
import java.util.ArrayList;

/**
 *
 * @author Chua Huey Eng
 */
public class CollapsePatternThread extends NotificationThread {
	private String threadName;
	private ArrayList<ArrayList<closureEdge>> distinctCandidatePatterns = new ArrayList<ArrayList<closureEdge>>();
	private ArrayList<ArrayList<Integer>> distinctCandidatePatterns_closureIndex = new ArrayList<ArrayList<Integer>>();
	private ArrayList<ArrayList<closureEdge>> candidatePatterns = new ArrayList<ArrayList<closureEdge>>();
	private ArrayList<Integer> candidateClosureIndex = new ArrayList<Integer>();
	private VF2 VF2_operator = new VF2();

	public CollapsePatternThread(ArrayList<ArrayList<closureEdge>> cPatterns, ArrayList<Integer> cClosureIndex,
			int threadNum, CollapsePatternThreadCompleteListener listener) {
		super.addListener(listener);
		threadName = "COLLAPSE_PATTERN_THREAD_#_" + threadNum;

		for (int i = 0; i < cPatterns.size(); i++)
			candidatePatterns.add(cPatterns.get(i));
		for (int i = 0; i < cClosureIndex.size(); i++)
			candidateClosureIndex.add(cClosureIndex.get(i));
	}

	public void setCandidatePatternsAndIndex(ArrayList<ArrayList<closureEdge>> cPatterns,
			ArrayList<Integer> cClosureIndex) {
		candidatePatterns = new ArrayList<ArrayList<closureEdge>>();
		candidateClosureIndex = new ArrayList<Integer>();
		for (int i = 0; i < cPatterns.size(); i++)
			candidatePatterns.add(cPatterns.get(i));
		for (int i = 0; i < cClosureIndex.size(); i++)
			candidateClosureIndex.add(cClosureIndex.get(i));
	}

	public ArrayList<ArrayList<closureEdge>> getDistinctPattern() {
		return distinctCandidatePatterns;
	}

	public ArrayList<ArrayList<Integer>> getDistinctPatternClosureIndex() {
		return distinctCandidatePatterns_closureIndex;
	}

	private void collapsePattern() {
		boolean PRINT = false;
		// System.out.println("Starting to run collapse pattern thread "+threadName);
		distinctCandidatePatterns = new ArrayList<ArrayList<closureEdge>>();
		distinctCandidatePatterns_closureIndex = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> closureIndex = new ArrayList<Integer>();
		if(candidatePatterns.size() <= 0)  {
			System.out.println("candidatePatterns.size() <= 0");
			return ;
		}
		
		distinctCandidatePatterns.add(candidatePatterns.get(0));
		closureIndex.add(candidateClosureIndex.get(0));
		distinctCandidatePatterns_closureIndex.add(closureIndex);
		candidatePatterns.remove(0);
		candidateClosureIndex.remove(0);
		while (candidatePatterns.size() > 0) {
			boolean FOUND = false;
			ArrayList<closureEdge> pattern = candidatePatterns.get(0);
			int index = candidateClosureIndex.get(0);

			if (pattern.size() > 0) {
				JGraphtClosureGraph patternCGraph = new JGraphtClosureGraph(pattern);
				// System.out.println("curr pattern: ");
				// for(int i=0; i<pattern.size(); i++)
				// pattern.get(i).print();
				// System.out.println("distinctCandidatePatterns
				// size:"+distinctCandidatePatterns.size());
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
									System.out.println("############ found a pattern for index " + index
											+ " in distinct pattern " + i);
									for (int p = 0; p < pattern.size(); p++)
										pattern.get(p).print();
								}
								ArrayList<Integer> indexList = distinctCandidatePatterns_closureIndex.get(i);
								if (indexList.contains(index) == false)
									distinctCandidatePatterns_closureIndex.get(i).add(index);
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
					closureIndex = new ArrayList<Integer>();
					closureIndex.add(index);
					distinctCandidatePatterns_closureIndex.add(closureIndex);
				}
			}
			candidatePatterns.remove(0);
			candidateClosureIndex.remove(0);
		}
		// System.out.println("Finishing collapse pattern thread "+threadName);
	}

	public String getName() {
		return threadName;
	}

	@Override
	public void doWork() {
		//System.out.println("HERE:: "+threadName);
		collapsePattern();
	//	System.out.println("HERE2:: "+threadName);
	}
}

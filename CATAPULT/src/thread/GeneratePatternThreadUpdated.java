package thread;

import db.DatabaseInfo;
import thread.GeneratePatternThreadCompleteListener;
import thread.NotificationThread;
import graph.JGraphtClosureGraph;
import graph.closureEdge;
import main.Trie;

import java.util.ArrayList;
import java.util.Set;

/**
 *
 * @author Chua Huey Eng
 */
public class GeneratePatternThreadUpdated extends NotificationThread {
	private String threadName;
	private ArrayList<JGraphtClosureGraph> closureGraphList = new ArrayList<JGraphtClosureGraph>();
	private ArrayList<Integer> candidateClosureIndex = new ArrayList<Integer>();
	private ArrayList<String> DBEdgeLabel = new ArrayList<String>();
	private ArrayList<Float> DBEdge_wt = new ArrayList<Float>();
	private int patternMinSize;
	private int patternMaxSize;
	private ArrayList<Integer> patternSizeList;
	private ArrayList<Integer> patternSizeCounter;
	private int GUISize;
	private ArrayList<ArrayList<closureEdge>> candidatePatterns;
	private ArrayList<Integer> candidatePatterns_closureIndex;
	private ArrayList<String> DBVertexLabel = new ArrayList<String>();
	private ArrayList<Integer> DBVertexLabelValency = new ArrayList<Integer>();
	private Trie  TrieTree;

	public GeneratePatternThreadUpdated(ArrayList<JGraphtClosureGraph> cGraphList, ArrayList<Integer> closureIndex,
			ArrayList<String> edgeLabel, ArrayList<Float> edgeLabelWt, int minSize, int maxSize, int threadNum,
			GeneratePatternThreadCompleteListenerUpdated listener, ArrayList<Integer> GUIPatternSize,
			ArrayList<Integer> GUIPatternSizeCounter, int totalPatterns, ArrayList<String> vLabelList,
			ArrayList<Integer> vLabelList_valency) {
		candidatePatterns = new ArrayList<ArrayList<closureEdge>>();
		candidatePatterns_closureIndex = new ArrayList<Integer>();
		super.addListener(listener);
		threadName = "GENERATE_PATTERN_THREAD_#_" + threadNum;
		for (int i = 0; i < cGraphList.size(); i++)
			closureGraphList.add(cGraphList.get(i));
		for (int i = 0; i < closureIndex.size(); i++)
			candidateClosureIndex.add(closureIndex.get(i));
		for (int i = 0; i < edgeLabel.size(); i++)
			DBEdgeLabel.add(edgeLabel.get(i));
		for (int i = 0; i < edgeLabelWt.size(); i++)
			DBEdge_wt.add(edgeLabelWt.get(i));
		patternMinSize = minSize;
		patternMaxSize = maxSize;
		patternSizeList = GUIPatternSize;
		patternSizeCounter = GUIPatternSizeCounter;
		GUISize = totalPatterns;
		DBVertexLabel = vLabelList;
		DBVertexLabelValency = vLabelList_valency;
		// maxValency=dbinfo.getMaxValency();

		// System.out.println("Initializing thread "+threadName+"
		// patternMinSize:"+patternMinSize+" patternMaxSize:"+patternMaxSize);
	}
	
	public GeneratePatternThreadUpdated(ArrayList<JGraphtClosureGraph> cGraphList, ArrayList<Integer> closureIndex,
			ArrayList<String> edgeLabel, ArrayList<Float> edgeLabelWt, int minSize, int maxSize, int threadNum,
			GeneratePatternThreadCompleteListenerUpdated listener, ArrayList<Integer> GUIPatternSize,
			ArrayList<Integer> GUIPatternSizeCounter, int totalPatterns, ArrayList<String> vLabelList,
			ArrayList<Integer> vLabelList_valency, Trie tree) {
		candidatePatterns = new ArrayList<ArrayList<closureEdge>>();
		candidatePatterns_closureIndex = new ArrayList<Integer>();
		super.addListener(listener);
		threadName = "GENERATE_PATTERN_THREAD_#_" + threadNum;
		for (int i = 0; i < cGraphList.size(); i++)
			closureGraphList.add(cGraphList.get(i));
		for (int i = 0; i < closureIndex.size(); i++)
			candidateClosureIndex.add(closureIndex.get(i));
		for (int i = 0; i < edgeLabel.size(); i++)
			DBEdgeLabel.add(edgeLabel.get(i));
		for (int i = 0; i < edgeLabelWt.size(); i++)
			DBEdge_wt.add(edgeLabelWt.get(i));
		patternMinSize = minSize;
		patternMaxSize = maxSize;
		patternSizeList = GUIPatternSize;
		patternSizeCounter = GUIPatternSizeCounter;
		GUISize = totalPatterns;
		DBVertexLabel = vLabelList;
		DBVertexLabelValency = vLabelList_valency;
		// maxValency=dbinfo.getMaxValency();

		// System.out.println("Initializing thread "+threadName+"
		// patternMinSize:"+patternMinSize+" patternMaxSize:"+patternMaxSize);
		
		TrieTree = tree;
	}

	private void findPatterns() {
		// System.out.println("Starting to run generate pattern thread "+threadName+"
		// closureGraphList: "+closureGraphList.size()+" candidateClosureIndex:
		// "+candidateClosureIndex.size());
		int maxPatternForEachSize = Math.max(GUISize / patternSizeCounter.size(), 1);
		int maxSize;
		if (GUISize < patternSizeCounter.size())
			maxSize = patternMinSize + GUISize - 1;
		else
			maxSize = patternMaxSize;
		for (int i = 0; i < candidateClosureIndex.size(); i++) {
			// System.out.println(threadName+" running loop "+i+" out of "+
			// candidateClosureIndex.size());
			JGraphtClosureGraph cGraph = closureGraphList.get(candidateClosureIndex.get(i));
			cGraph.updateEdgeWeights(DBEdgeLabel, DBEdge_wt, threadName);

			for (int s = patternMinSize; s <= maxSize; s++) {
				int patternIndex = patternSizeList.indexOf(s);
				if (patternSizeCounter.get(patternIndex) < maxPatternForEachSize) {
					ArrayList<closureEdge> p = cGraph.getBestPattern(s, false, DBVertexLabel, DBVertexLabelValency, TrieTree);
					if (p != null && p.size() == s) {
						candidatePatterns.add(p);
					    candidatePatterns_closureIndex.add(candidateClosureIndex.get(i));
					  }
					//if(p==null) {
					//	   return ;
					//}
				}
			}
		}
		// System.out.println("Finishing generate pattern thread "+threadName+"
		// candidatePatterns: "+candidatePatterns.size()+"
		// candidatePatterns_closureIndex:"+candidatePatterns_closureIndex.toString());
	}

	public ArrayList<ArrayList<closureEdge>> getCandidatePatterns() {
		return candidatePatterns;
	}

	public ArrayList<Integer> getCandidatePatternsClosureIndex() {
		return candidatePatterns_closureIndex;
	}

	public String getName() {
		return threadName;
	}

	@Override
	public void doWork() {
		// System.out.println("HERE:: "+threadName);
		findPatterns();
	}
}

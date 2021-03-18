package thread;

import extendedindex.ExIndex;
import thread.GenerateClosureThreadCompleteListener;
import thread.NotificationThread;
import graph.JGraphtClosureGraph;
import java.util.ArrayList;

/**
 *
 * @author Chua Huey Eng
 */
public class GenerateClosureThread extends NotificationThread {
	private String threadName;
	private String fileName;
	private ArrayList<ArrayList<Integer>> closure_graphIndex = new ArrayList<ArrayList<Integer>>();
	private ArrayList<Integer> closureIndex = new ArrayList<Integer>();

	private ArrayList<JGraphtClosureGraph> closureGraphList = new ArrayList<JGraphtClosureGraph>();
	private ExIndex exIndex = new ExIndex();
	private int maxClosureSize = 0;
	private int totalGraphs = 0;
	private ArrayList<String> dbEdgeLabels = new ArrayList<String>();
	private ArrayList<Integer> dbEdgeLabelsFreq = new ArrayList<Integer>();
	private ArrayList<String> dbVertexLabels = new ArrayList<String>();
	private ArrayList<Integer> dbVertexLabelsValency = new ArrayList<Integer>();

	public GenerateClosureThread(String fName, ArrayList<ArrayList<Integer>> closureGraphIndex,
			ArrayList<Integer> cIndex, int threadNum, GenerateClosureThreadCompleteListener listener) {
		super.addListener(listener);
		threadName = "GENERATE_CLOSURE_THREAD_#_" + threadNum;
		fileName = fName;
		for (int i = 0; i < closureGraphIndex.size(); i++)
			closure_graphIndex.add(closureGraphIndex.get(i));
		for (int i = 0; i < cIndex.size(); i++)
			closureIndex.add(cIndex.get(i));
	}

	public ArrayList<String> getDBEdgeLabel() {
		return dbEdgeLabels;
	}

	public ArrayList<Integer> getDBEdgeLabelFreq() {
		return dbEdgeLabelsFreq;
	}

	public ArrayList<String> getDBVertexLabel() {
		return dbVertexLabels;
	}

	public ArrayList<Integer> getDBVertexLabelValency() {
		return dbVertexLabelsValency;
	}

	public int getMaxClosureSize() {
		return maxClosureSize;
	}

	public int getTotalGraphs() {
		return totalGraphs;
	}

	public ArrayList<JGraphtClosureGraph> getClosureGraphs() {
		return closureGraphList;
	}

	public ArrayList<Integer> getClosureGraphIndex() {
		return closureIndex;
	}

	private void findClosures() {
		System.out.println("Starting to run generate closure thread " + threadName);
		dbEdgeLabels = new ArrayList<String>();// the unique edge labels found in the graphs in the DB
		dbEdgeLabelsFreq = new ArrayList<Integer>();// count the number of graphs having that edge label in the DB

		for (int i = 0; i < closure_graphIndex.size(); i++) {
			boolean PRINT = false;
			// 2A. read the graphs of clusters
			// exIndex.readGraphSetForSpecificGraphIds("data/pubchem2000",
			// closure_graphIndex.get(i), false);
			// System.out.println(fileName);
			// System.out.println(closure_graphIndex.get(i));
			// All graphs in a cluster
			exIndex.readGraphSetForSpecificGraphIds(fileName, closure_graphIndex.get(i), false);
			// if(true) return;
			// 2B. perform closure
			exIndex.performClosure(PRINT);
			closureGraphList.add(exIndex.getClosureGraph());
			// 2C. update DB vertex label and edge label statistics
			ArrayList<String> edgeLabels = exIndex.getUniqueEdgeLabelsInCluster();
			ArrayList<Integer> edgeLabels_freq = exIndex.getUniqueEdgeLabelFrequencyInCluster();
			ArrayList<String> vertexLabels = exIndex.getUniqueVertexLabelsInCluster();
			ArrayList<Integer> vertexLabels_valency = exIndex.getUniqueVertexLabelValencyInCluster();

			for (int l = 0; l < edgeLabels.size(); l++) {
				String eLabel = edgeLabels.get(l);
				int index = dbEdgeLabels.indexOf(eLabel);
				if (index == -1) {
					dbEdgeLabels.add(eLabel);
					dbEdgeLabelsFreq.add(edgeLabels_freq.get(l));
				} else {
					int freq = dbEdgeLabelsFreq.get(index) + edgeLabels_freq.get(l);
					dbEdgeLabelsFreq.set(index, freq);
				}
			}

			for (int l = 0; l < vertexLabels.size(); l++) {
				String vLabel = vertexLabels.get(l);
				int index = dbVertexLabels.indexOf(vLabel);
				if (index == -1) {
					dbVertexLabels.add(vLabel);
					dbVertexLabelsValency.add(vertexLabels_valency.get(l));
				} else {
					if (dbVertexLabelsValency.get(index) < vertexLabels_valency.get(l))
						dbVertexLabelsValency.set(index, vertexLabels_valency.get(l));
				}
			}

			if (maxClosureSize < closure_graphIndex.get(i).size())
				maxClosureSize = closure_graphIndex.get(i).size();
			totalGraphs = totalGraphs + closure_graphIndex.get(i).size();
		}
		// System.out.println("dbEdgeLabels:"+dbEdgeLabels.toString());
		// System.out.println("dbEdgeLabelsFreq:"+dbEdgeLabelsFreq.toString());
		System.out.println(
				"Finishing generate closure thread " + threadName + " closureGraphList: " + closureGraphList.size());
	}

	public String getName() {
		return threadName;
	}

	@Override
	public void doWork() {
		// System.out.println("HERE:: "+threadName);
		findClosures();
	}
}

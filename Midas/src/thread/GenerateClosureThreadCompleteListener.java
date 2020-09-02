/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thread;

import graph.JGraphtClosureGraph;
import java.util.ArrayList;

public class GenerateClosureThreadCompleteListener implements TaskListener {
	private ArrayList<String> DBEdgeLabel;
	private ArrayList<Integer> DBEdgeLabelFreq;
	private ArrayList<String> DBVertexLabel;
	private ArrayList<Integer> DBVertexLabelValency;
	private int maxClosureSize;
	private int totalGraphs;
	private ArrayList<JGraphtClosureGraph> closureGraphList;
	private ArrayList<Integer> closureIndex;

	public GenerateClosureThreadCompleteListener() {
		DBEdgeLabel = new ArrayList<String>();
		DBEdgeLabelFreq = new ArrayList<Integer>();
		DBVertexLabel = new ArrayList<String>();
		DBVertexLabelValency = new ArrayList<Integer>();
		maxClosureSize = 0;
		totalGraphs = 0;
		closureGraphList = new ArrayList<JGraphtClosureGraph>();
		closureIndex = new ArrayList<Integer>();
	}

	public ArrayList<String> getDBEdgeLabel() {
		return DBEdgeLabel;
	}

	public ArrayList<Integer> getDBEdgeLabelFreq() {
		return DBEdgeLabelFreq;
	}

	public ArrayList<String> getDBVertexLabel() {
		return DBVertexLabel;
	}

	public ArrayList<Integer> getDBVertexLabelValency() {
		return DBVertexLabelValency;
	}

	public int getMaxClosureSize() {
		return maxClosureSize;
	}

	public int getTotalGraphs() {
		return totalGraphs;
	}

	public ArrayList<JGraphtClosureGraph> getClosureGraph() {
		return closureGraphList;
	}

	public ArrayList<Integer> getClosureGraphIndex() {
		return closureIndex;
	}

	@Override
	public void threadComplete(Runnable runner) {
		GenerateClosureThread t = (GenerateClosureThread) runner;
		// System.out.println("thread name notify completion: "+t.getName());
		ArrayList<String> t_edgeLabel = t.getDBEdgeLabel();
		ArrayList<Integer> t_edgeLabelFreq = t.getDBEdgeLabelFreq();
		ArrayList<String> t_vertexLabel = t.getDBVertexLabel();
		ArrayList<Integer> t_vertexLabelValency = t.getDBVertexLabelValency();
		ArrayList<JGraphtClosureGraph> t_closureGraph = t.getClosureGraphs();
		ArrayList<Integer> t_closureGraphIndex = t.getClosureGraphIndex();
		int temp_maxClosureSize = t.getMaxClosureSize();
		System.out.println("t_edgeLabel:" + t_edgeLabel.toString());
		System.out.println("t_edgeLabelFreq:" + t_edgeLabelFreq.toString());
		for (int i = 0; i < t_edgeLabel.size(); i++) {
			int index = DBEdgeLabel.indexOf(t_edgeLabel.get(i));
			if (index == -1) {
				DBEdgeLabel.add(t_edgeLabel.get(i));
				DBEdgeLabelFreq.add(t_edgeLabelFreq.get(i));
			} else {
				int newFreq = DBEdgeLabelFreq.get(index) + t_edgeLabelFreq.get(i);
				DBEdgeLabelFreq.set(index, newFreq);
			}
		}

		for (int i = 0; i < t_vertexLabel.size(); i++) {
			int index = DBVertexLabel.indexOf(t_vertexLabel.get(i));
			if (index == -1) {
				DBVertexLabel.add(t_vertexLabel.get(i));
				DBVertexLabelValency.add(t_vertexLabelValency.get(i));
			} else {
				if (DBVertexLabelValency.get(index) < t_vertexLabelValency.get(i))
					DBVertexLabelValency.set(index, t_vertexLabelValency.get(i));
			}
		}

		for (int i = 0; i < t_closureGraph.size(); i++)
			closureGraphList.add(t_closureGraph.get(i));
		for (int i = 0; i < t_closureGraphIndex.size(); i++)
			closureIndex.add(t_closureGraphIndex.get(i));

		if (maxClosureSize < temp_maxClosureSize)
			maxClosureSize = temp_maxClosureSize;
		totalGraphs = totalGraphs + t.getTotalGraphs();
		// System.out.println("DBEdgeLabel:"+DBEdgeLabel.toString());
		// System.out.println("DBEdgeLabelFreq:"+DBEdgeLabelFreq.toString());
		// System.out.println("maxClosureSize:"+maxClosureSize);
		// System.out.println("totalGraphs:"+totalGraphs);
	}
}

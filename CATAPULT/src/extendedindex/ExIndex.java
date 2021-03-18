/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package extendedindex;

import adjlistgraph.Graph;
import frequentindex.Vertex;
import result.DotGenerator;
import graph.JGraphtClosureGraph;
import graph.JGraphtGraph;
import graph.closureEdge;
import graph.closureVertex;
import graph.priorityQueueTuple;
import graph.simpleEdge;
import graph.simpleEdgeComparator;
import graph.simpleVertex;
import graph.simpleVertexComparator;
import graph.tupleComparator;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Stack;
//import org.jgrapht.GraphMapping;
//import org.openide.util.Exceptions;

/**
 *
 * @author nguyenhhien
 */
public class ExIndex {

	private ArrayList<Graph> patternSet;
	private ArrayList<Graph> graphSet;
	private ArrayList<JGraphtGraph> jGraphTGraphSet;
	public ArrayList<JGraphtGraph> getjGraphTGraphSet() {
		return jGraphTGraphSet;
	}

	private ArrayList<Integer> graphIdList;
	private JGraphtClosureGraph closureGraph;
	private DotGenerator dotGenerator;
	ArrayList<String> allVertexInGraphDatabase_label = new ArrayList<String>();
	ArrayList<Float> allVertexInGraphDatabase_aveInnerSupport = new ArrayList<Float>();
	ArrayList<ArrayList<String>> allEdgeInGraphDatabase_label = new ArrayList<ArrayList<String>>();
	ArrayList<Float> allEdgeInGraphDatabase_aveInnerSupport = new ArrayList<Float>();

	// for getting some statistics of the DB
	private ArrayList<String> uniqueVertexLabelsInCluster = new ArrayList<String>();// unique vertex labels in graphs in
																					// this cluster
	private ArrayList<Integer> uniqueVertexLabelsInCluster_freq = new ArrayList<Integer>();// # graphs in this cluster
																							// having the vertex label
	private ArrayList<Integer> uniqueVertexLabelsInCluster_valency = new ArrayList<Integer>();// max degree of vertex
																								// having this label
	private ArrayList<String> uniqueEdgeLabelsInCluster = new ArrayList<String>();// unique edge labels in graphs in
																					// this cluster
	private ArrayList<Integer> uniqueEdgeLabelsInCluster_freq = new ArrayList<Integer>();// # graphs in this cluster
																							// having the edge label

	// for doing graph closure
	private ArrayList<closureVertex> matched_u;
	private ArrayList<closureVertex> matched_v;

	// for check missed and fully contained graph id;
	private ArrayList<Integer> missedGraphID = new ArrayList<Integer>();
	private ArrayList<Integer> fullyContainedGraphID = new ArrayList<Integer>();

	public ExIndex() {
		patternSet = new ArrayList<Graph>();
		graphSet = new ArrayList<Graph>();
		graphIdList = new ArrayList<Integer>();
		dotGenerator = new DotGenerator();
		closureGraph = new JGraphtClosureGraph();
		jGraphTGraphSet = new ArrayList<JGraphtGraph>();
	}

	public ArrayList<String> getUniqueVertexLabelsInCluster() {
		return uniqueVertexLabelsInCluster;
	}

	public ArrayList<Integer> getUniqueVertexLabelFrequencyInCluster() {
		return uniqueVertexLabelsInCluster_freq;
	}

	public ArrayList<Integer> getUniqueVertexLabelValencyInCluster() {
		return uniqueVertexLabelsInCluster_valency;
	}

	public ArrayList<String> getUniqueEdgeLabelsInCluster() {
		return uniqueEdgeLabelsInCluster;
	}

	public ArrayList<Integer> getUniqueEdgeLabelFrequencyInCluster() {
		return uniqueEdgeLabelsInCluster_freq;
	}

	public void updateGraphIDList(ArrayList<Integer> mGraphID, ArrayList<Integer> fcGraphID) {
		missedGraphID = mGraphID;
		fullyContainedGraphID = fcGraphID;
	}

	public ArrayList<Integer> getMissedGraphID() {
		return missedGraphID;
	}

	public ArrayList<Integer> getFullyContainedGraphID() {
		return fullyContainedGraphID;
	}

	public JGraphtClosureGraph getClosureGraph() {
		return closureGraph;
	}

	public int getJGraphtGraphSetSize() {
		return jGraphTGraphSet.size();
	}

	public JGraphtGraph getClusterGraphElementAt(int pos) {
		if (pos >= 0 && pos < jGraphTGraphSet.size())
			return jGraphTGraphSet.get(pos);
		return null;
	}

	public ArrayList<Graph> getPatternSet() {
		return patternSet;
	}

	public ArrayList<Graph> getGraphSet() {
		return graphSet;
	}
	

	public ArrayList<String> getVertexLabelList() {
		return allVertexInGraphDatabase_label;
	}

	public ArrayList<Float> getVertexInnerSupportList() {
		return allVertexInGraphDatabase_aveInnerSupport;
	}

	public ArrayList<ArrayList<String>> getEdgeLabelList() {
		return allEdgeInGraphDatabase_label;
	}

	public ArrayList<Float> getEdgeInnerSupportList() {
		return allEdgeInGraphDatabase_aveInnerSupport;
	}

	public void build(ExFreqIndex exFreqIndex) {
		readPatternSet("images/Patterns");
		readGraphSet("data/AIDS/40k/AIDS40k");

		exFreqIndex.readFreqFragSet("data/AIDS/40k/0.1AIDS40k");
		exFreqIndex.build(this);

		// ExInfIndex.build();
	}

	public void readPatternSet(String fileName) {
		String strLine = null;
		File fin = new File(fileName);

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new BufferedInputStream(new FileInputStream(fin))));

			int gId = -1;
			int vId = -1;
			int eNum = 0;

			Graph tmpGraph = null;

			while ((strLine = br.readLine()) != null) {

				// beginning line of a graph data;
				if (strLine.contains("t #")) {
					vId = -1;
					gId++;

					eNum = 0;
					String[] str = strLine.split("\\s");
					int vNum = Integer.parseInt(str[3]);

					tmpGraph = new Graph();
					tmpGraph.setGraphid(gId);
					tmpGraph.setVertexNum(vNum);

				} else if (strLine.contains("v")) {
					Vertex node = new Vertex();
					String[] nodeline = strLine.split("\\s");
					node.setLabel(nodeline[2]);
					node.setID(++vId);
					tmpGraph.addNode(node);
				} else if (strLine.contains("e")) {
					eNum++;
					String[] strnode = strLine.split("\\s");
					int v1 = Integer.parseInt(strnode[1]);
					int v2 = Integer.parseInt(strnode[2]);

					// Connect v1 with v2; Increase degree of each;
					tmpGraph.addEdge(v1, v2);
					tmpGraph.getNode(v1).incDegree();
					tmpGraph.getNode(v2).incDegree();

					// set v1, v2 to be in In-list of each other;
					tmpGraph.getNode(v1).setIn(v2);
					tmpGraph.getNode(v2).setIn(v1);
				} else {
					tmpGraph.setEdgeNum(eNum);
					patternSet.add(tmpGraph);
				}
			}
			br.close();
		} catch (Exception ex) {
			System.out.println("Exception: ");
			ex.printStackTrace();
		}

		System.out.println(patternSet.size() + " patterns loaded!");
	}

	public void readGraphSet(String filename) {
		String strLine = null;

		File fin = new File(filename);
		BufferedReader br;
		ArrayList<Integer> allVertexInGraphDatabase_count = new ArrayList<Integer>();
		ArrayList<Integer> allVertexInGraphDatabase_numGraphs = new ArrayList<Integer>();
		ArrayList<Boolean> allVertexInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();
		ArrayList<Integer> allEdgeInGraphDatabase_count = new ArrayList<Integer>();
		ArrayList<Integer> allEdgeInGraphDatabase_numGraphs = new ArrayList<Integer>();
		ArrayList<Boolean> allEdgeInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();

		try {
			br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fin))));

			int graphid = -1;
			int eNum = 0;

			Graph tmpgraph = null;

			while ((strLine = br.readLine()) != null) {

				if (strLine.contains("t # ")) {
					allVertexInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();
					for (int i = 0; i < allVertexInGraphDatabase_label.size(); i++)
						allVertexInGraphDatabase_firstOccurrenceInThisGraph.add(true);

					allEdgeInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();
					for (int i = 0; i < allEdgeInGraphDatabase_label.size(); i++)
						allEdgeInGraphDatabase_firstOccurrenceInThisGraph.add(true);

					int spaceIndex = strLine.indexOf(" ", 4);
					if (spaceIndex != -1) {
						graphid = Integer.parseInt(strLine.substring(4, spaceIndex));
						graphIdList.add(graphid);
					} else {
						System.out.println("ERROR: ExIndex.java readGraphSet. Can't find graph id! so auto increment!");
						graphid++;
					}
					// System.out.println("new graph-------------------"+ graphid);

					eNum = 0;

					String[] str = strLine.split("\\s");
					int nodenum = Integer.parseInt(str[3]);

					tmpgraph = new Graph();
					tmpgraph.setGraphid(graphid);
					tmpgraph.setVertexNum(nodenum);
				} else if (strLine.contains("v")) {
					Vertex node = new Vertex();
					String[] nodeline = strLine.split("\\s");
					node.setLabel(nodeline[2]);
					node.setID(Integer.parseInt(nodeline[1]));
					tmpgraph.addNode(node);
					String label = nodeline[2];
					if (allVertexInGraphDatabase_label.contains(label) == false)// new label not seen before
					{
						allVertexInGraphDatabase_label.add(label);
						allVertexInGraphDatabase_count.add(1);
						allVertexInGraphDatabase_numGraphs.add(1);
						allVertexInGraphDatabase_firstOccurrenceInThisGraph.add(false);

						// System.out.println("add label first time: "+label);
						// System.out.println("allVertexLabel:
						// "+allVertexInGraphDatabase_label.toString());
						// System.out.println("allVertexCount:
						// "+allVertexInGraphDatabase_count.toString());
						// System.out.println("allVertexNumGraph:
						// "+allVertexInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allVertexInGraphDatabase_firstOccurrenceInThisGraph.toString());
					} else// has seen this label before
					{
						int index = allVertexInGraphDatabase_label.indexOf(label);
						int currCount = allVertexInGraphDatabase_count.get(index);
						allVertexInGraphDatabase_count.set(index, currCount + 1);
						if (allVertexInGraphDatabase_firstOccurrenceInThisGraph.get(index) == true)// first time seeing
																									// in this graph
						{
							int currNumGraphs = allVertexInGraphDatabase_numGraphs.get(index);
							allVertexInGraphDatabase_numGraphs.set(index, currNumGraphs + 1);
							allVertexInGraphDatabase_firstOccurrenceInThisGraph.set(index, false);
							// System.out.println("first time see label in this graph!");
						}

						// System.out.println("seen label before: "+label);
						// System.out.println("allVertexLabel:
						// "+allVertexInGraphDatabase_label.toString());
						// System.out.println("allVertexCount:
						// "+allVertexInGraphDatabase_count.toString());
						// System.out.println("allVertexNumGraph:
						// "+allVertexInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allVertexInGraphDatabase_firstOccurrenceInThisGraph.toString());
					}
				} else if (strLine.contains("e")) {
					eNum++;
					String[] strnode = strLine.split("\\s");
					int v1 = Integer.parseInt(strnode[1]);
					int v2 = Integer.parseInt(strnode[2]);

					// Connect v1 with v2; Increase degree of each;
					tmpgraph.addEdge(v1, v2);
					tmpgraph.getNode(v1).incDegree();
					tmpgraph.getNode(v2).incDegree();

					// set v1, v2 to be in In-list of each other;
					tmpgraph.getNode(v1).setIn(v2);
					tmpgraph.getNode(v2).setIn(v1);

					// retrieve the labels of the vertices
					String label1 = tmpgraph.getNode(v1).getLabel();
					String label2 = tmpgraph.getNode(v2).getLabel();
					ArrayList<String> edgeLabel1 = new ArrayList<String>();
					ArrayList<String> edgeLabel2 = new ArrayList<String>();
					edgeLabel1.add(label1);
					edgeLabel1.add(label2);
					edgeLabel2.add(label2);
					edgeLabel2.add(label1);

					if (allEdgeInGraphDatabase_label.contains(edgeLabel1) == false
							&& allEdgeInGraphDatabase_label.contains(edgeLabel2) == false)// new label not seen before
					{
						allEdgeInGraphDatabase_label.add(edgeLabel1);
						allEdgeInGraphDatabase_count.add(1);
						allEdgeInGraphDatabase_numGraphs.add(1);
						allEdgeInGraphDatabase_firstOccurrenceInThisGraph.add(false);

						// System.out.println("add label first time: "+edgeLabel1);
						// System.out.println("allEdgeLabel: "+allEdgeInGraphDatabase_label.toString());
						// System.out.println("allEdgeCount: "+allEdgeInGraphDatabase_count.toString());
						// System.out.println("allEdgeNumGraph:
						// "+allEdgeInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allEdgeInGraphDatabase_firstOccurrenceInThisGraph.toString());
					} else// has seen this label before
					{
						int index1 = allEdgeInGraphDatabase_label.indexOf(edgeLabel1);
						int index2 = allEdgeInGraphDatabase_label.indexOf(edgeLabel2);
						int index;
						ArrayList<String> label;
						if (index1 != -1) {
							index = index1;
							label = edgeLabel1;
						} else {
							index = index2;
							label = edgeLabel2;
						}
						int currCount = allEdgeInGraphDatabase_count.get(index);
						allEdgeInGraphDatabase_count.set(index, currCount + 1);
						if (allEdgeInGraphDatabase_firstOccurrenceInThisGraph.get(index) == true)// first time seeing in
																									// this graph
						{
							int currNumGraphs = allEdgeInGraphDatabase_numGraphs.get(index);
							allEdgeInGraphDatabase_numGraphs.set(index, currNumGraphs + 1);
							allEdgeInGraphDatabase_firstOccurrenceInThisGraph.set(index, false);
							// System.out.println("first time see label in this graph!");
						}

						// System.out.println("seen label before: "+label.toString());
						// System.out.println("allEdgeLabel: "+allEdgeInGraphDatabase_label.toString());
						// System.out.println("allEdgeCount: "+allEdgeInGraphDatabase_count.toString());
						// System.out.println("allEdgeNumGraph:
						// "+allEdgeInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allEdgeInGraphDatabase_firstOccurrenceInThisGraph.toString());
					}
				} else {
					tmpgraph.setEdgeNum(eNum);
					graphSet.add(tmpgraph);
					exportGraphToSVG(tmpgraph, graphid);
				}
			}
			br.close();
			// System.out.println(graphSet.size() + " data graphs loaded");
			// System.out.println("graphIdList size:" + graphIdList.size());
			// finish all the parsing....now to compute inner support
			for (int l = 0; l < allVertexInGraphDatabase_label.size(); l++) {
				Float count = Float.valueOf(allVertexInGraphDatabase_count.get(l));
				Float numGraph = Float.valueOf(allVertexInGraphDatabase_numGraphs.get(l));
				Float innerSupport = count / numGraph;
				allVertexInGraphDatabase_aveInnerSupport.add(innerSupport);
			}
			for (int l = 0; l < allEdgeInGraphDatabase_label.size(); l++) {
				Float count = Float.valueOf(allEdgeInGraphDatabase_count.get(l));
				Float numGraph = Float.valueOf(allEdgeInGraphDatabase_numGraphs.get(l));
				Float innerSupport = count / numGraph;
				allEdgeInGraphDatabase_aveInnerSupport.add(innerSupport);
			}
			//System.out.println("allVertexInGraphDatabase_label:" + allVertexInGraphDatabase_label.toString());
			//System.out.println(
			//		"allVertexInGraphDatabase_aveInnerSupport:" + allVertexInGraphDatabase_aveInnerSupport.toString());
			//System.out.println("allEdgeLabel: " + allEdgeInGraphDatabase_label.toString());
			//System.out.println(
			//		"allEdgeInGraphDatabase_aveInnerSupport:" + allVertexInGraphDatabase_aveInnerSupport.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void readGraphSetOf(String dbName, String filename) {
		String strLine = null;

		File fin = new File(filename);
		BufferedReader br;
		ArrayList<Integer> allVertexInGraphDatabase_count = new ArrayList<Integer>();
		ArrayList<Integer> allVertexInGraphDatabase_numGraphs = new ArrayList<Integer>();
		ArrayList<Boolean> allVertexInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();
		ArrayList<Integer> allEdgeInGraphDatabase_count = new ArrayList<Integer>();
		ArrayList<Integer> allEdgeInGraphDatabase_numGraphs = new ArrayList<Integer>();
		ArrayList<Boolean> allEdgeInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();

		try {
			br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fin))));

			int graphid = -1;
			int eNum = 0;

			Graph tmpgraph = null;

			while ((strLine = br.readLine()) != null) {

				if (strLine.contains("t # ")) {
					allVertexInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();
					for (int i = 0; i < allVertexInGraphDatabase_label.size(); i++)
						allVertexInGraphDatabase_firstOccurrenceInThisGraph.add(true);

					allEdgeInGraphDatabase_firstOccurrenceInThisGraph = new ArrayList<Boolean>();
					for (int i = 0; i < allEdgeInGraphDatabase_label.size(); i++)
						allEdgeInGraphDatabase_firstOccurrenceInThisGraph.add(true);

					int spaceIndex = strLine.indexOf(" ", 4);
					if (spaceIndex != -1) {
						graphid = Integer.parseInt(strLine.substring(4, spaceIndex));
						graphIdList.add(graphid);
					} else {
						System.out.println("ERROR: ExIndex.java readGraphSet. Can't find graph id! so auto increment!");
						graphid++;
					}
					// System.out.println("new graph-------------------"+ graphid);

					eNum = 0;

					String[] str = strLine.split("\\s");
					int nodenum = Integer.parseInt(str[3]);

					tmpgraph = new Graph();
					tmpgraph.setGraphid(graphid);
					tmpgraph.setVertexNum(nodenum);
				} else if (strLine.contains("v")) {
					Vertex node = new Vertex();
					String[] nodeline = strLine.split("\\s");
					node.setLabel(nodeline[2]);
					node.setID(Integer.parseInt(nodeline[1]));
					tmpgraph.addNode(node);
					String label = nodeline[2];
					if (allVertexInGraphDatabase_label.contains(label) == false)// new label not seen before
					{
						allVertexInGraphDatabase_label.add(label);
						allVertexInGraphDatabase_count.add(1);
						allVertexInGraphDatabase_numGraphs.add(1);
						allVertexInGraphDatabase_firstOccurrenceInThisGraph.add(false);

						// System.out.println("add label first time: "+label);
						// System.out.println("allVertexLabel:
						// "+allVertexInGraphDatabase_label.toString());
						// System.out.println("allVertexCount:
						// "+allVertexInGraphDatabase_count.toString());
						// System.out.println("allVertexNumGraph:
						// "+allVertexInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allVertexInGraphDatabase_firstOccurrenceInThisGraph.toString());
					} else// has seen this label before
					{
						int index = allVertexInGraphDatabase_label.indexOf(label);
						int currCount = allVertexInGraphDatabase_count.get(index);
						allVertexInGraphDatabase_count.set(index, currCount + 1);
						if (allVertexInGraphDatabase_firstOccurrenceInThisGraph.get(index) == true)// first time seeing
																									// in this graph
						{
							int currNumGraphs = allVertexInGraphDatabase_numGraphs.get(index);
							allVertexInGraphDatabase_numGraphs.set(index, currNumGraphs + 1);
							allVertexInGraphDatabase_firstOccurrenceInThisGraph.set(index, false);
							// System.out.println("first time see label in this graph!");
						}

						// System.out.println("seen label before: "+label);
						// System.out.println("allVertexLabel:
						// "+allVertexInGraphDatabase_label.toString());
						// System.out.println("allVertexCount:
						// "+allVertexInGraphDatabase_count.toString());
						// System.out.println("allVertexNumGraph:
						// "+allVertexInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allVertexInGraphDatabase_firstOccurrenceInThisGraph.toString());
					}
				} else if (strLine.contains("e")) {
					eNum++;
					String[] strnode = strLine.split("\\s");
					int v1 = Integer.parseInt(strnode[1]);
					int v2 = Integer.parseInt(strnode[2]);

					// Connect v1 with v2; Increase degree of each;
					tmpgraph.addEdge(v1, v2);
					tmpgraph.getNode(v1).incDegree();
					tmpgraph.getNode(v2).incDegree();

					// set v1, v2 to be in In-list of each other;
					tmpgraph.getNode(v1).setIn(v2);
					tmpgraph.getNode(v2).setIn(v1);

					// retrieve the labels of the vertices
					String label1 = tmpgraph.getNode(v1).getLabel();
					String label2 = tmpgraph.getNode(v2).getLabel();
					ArrayList<String> edgeLabel1 = new ArrayList<String>();
					ArrayList<String> edgeLabel2 = new ArrayList<String>();
					edgeLabel1.add(label1);
					edgeLabel1.add(label2);
					edgeLabel2.add(label2);
					edgeLabel2.add(label1);

					if (allEdgeInGraphDatabase_label.contains(edgeLabel1) == false
							&& allEdgeInGraphDatabase_label.contains(edgeLabel2) == false)// new label not seen before
					{
						allEdgeInGraphDatabase_label.add(edgeLabel1);
						allEdgeInGraphDatabase_count.add(1);
						allEdgeInGraphDatabase_numGraphs.add(1);
						allEdgeInGraphDatabase_firstOccurrenceInThisGraph.add(false);

						// System.out.println("add label first time: "+edgeLabel1);
						// System.out.println("allEdgeLabel: "+allEdgeInGraphDatabase_label.toString());
						// System.out.println("allEdgeCount: "+allEdgeInGraphDatabase_count.toString());
						// System.out.println("allEdgeNumGraph:
						// "+allEdgeInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allEdgeInGraphDatabase_firstOccurrenceInThisGraph.toString());
					} else// has seen this label before
					{
						int index1 = allEdgeInGraphDatabase_label.indexOf(edgeLabel1);
						int index2 = allEdgeInGraphDatabase_label.indexOf(edgeLabel2);
						int index;
						ArrayList<String> label;
						if (index1 != -1) {
							index = index1;
							label = edgeLabel1;
						} else {
							index = index2;
							label = edgeLabel2;
						}
						int currCount = allEdgeInGraphDatabase_count.get(index);
						allEdgeInGraphDatabase_count.set(index, currCount + 1);
						if (allEdgeInGraphDatabase_firstOccurrenceInThisGraph.get(index) == true)// first time seeing in
																									// this graph
						{
							int currNumGraphs = allEdgeInGraphDatabase_numGraphs.get(index);
							allEdgeInGraphDatabase_numGraphs.set(index, currNumGraphs + 1);
							allEdgeInGraphDatabase_firstOccurrenceInThisGraph.set(index, false);
							// System.out.println("first time see label in this graph!");
						}

						// System.out.println("seen label before: "+label.toString());
						// System.out.println("allEdgeLabel: "+allEdgeInGraphDatabase_label.toString());
						// System.out.println("allEdgeCount: "+allEdgeInGraphDatabase_count.toString());
						// System.out.println("allEdgeNumGraph:
						// "+allEdgeInGraphDatabase_numGraphs.toString());
						// System.out.println("firstOccurence:
						// "+allEdgeInGraphDatabase_firstOccurrenceInThisGraph.toString());
					}
				} else {
					tmpgraph.setEdgeNum(eNum);
					graphSet.add(tmpgraph);
					exportGraphToSVG_dataset(dbName, tmpgraph, graphid);
				}
			}
			br.close();
			// System.out.println(graphSet.size() + " data graphs loaded");
			// System.out.println("graphIdList size:" + graphIdList.size());
			// finish all the parsing....now to compute inner support
			for (int l = 0; l < allVertexInGraphDatabase_label.size(); l++) {
				Float count = Float.valueOf(allVertexInGraphDatabase_count.get(l));
				Float numGraph = Float.valueOf(allVertexInGraphDatabase_numGraphs.get(l));
				Float innerSupport = count / numGraph;
				allVertexInGraphDatabase_aveInnerSupport.add(innerSupport);
			}
			for (int l = 0; l < allEdgeInGraphDatabase_label.size(); l++) {
				Float count = Float.valueOf(allEdgeInGraphDatabase_count.get(l));
				Float numGraph = Float.valueOf(allEdgeInGraphDatabase_numGraphs.get(l));
				Float innerSupport = count / numGraph;
				allEdgeInGraphDatabase_aveInnerSupport.add(innerSupport);
			}
			//System.out.println("allVertexInGraphDatabase_label:" + allVertexInGraphDatabase_label.toString());
			//System.out.println(
			//		"allVertexInGraphDatabase_aveInnerSupport:" + allVertexInGraphDatabase_aveInnerSupport.toString());
			//System.out.println("allEdgeLabel: " + allEdgeInGraphDatabase_label.toString());
			//System.out.println(
			//		"allEdgeInGraphDatabase_aveInnerSupport:" + allVertexInGraphDatabase_aveInnerSupport.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ArrayList<ArrayList<Float>> populateMatrix(ArrayList<ArrayList<Float>> matrix, JGraphtClosureGraph closureG,
			JGraphtClosureGraph nextG, boolean PRINT) {
		int row = closureG.getNumNodes();
		int col = nextG.getNumNodes();

		for (int closureGNode = 0; closureGNode < row; closureGNode++) {
			for (int nextGNode = 0; nextGNode < col; nextGNode++) {
				closureVertex closureGraphNode = closureG.getVertexAt(closureGNode);
				closureVertex nextGraphNode = nextG.getVertexAt(nextGNode);
				ArrayList<String> closureGNodeLabel = closureGraphNode.getLabel();
				ArrayList<String> nextGNodeLabel = nextGraphNode.getLabel();
				Float jaccard_numerator, jaccard_denominator, jaccard;

				// situation may arise that G1 has a node with label D but G2 has no nodes with
				// label D
				// we would assign highest similarity to nodes having same label, and neighbours
				// with same labels
				// followed by those having same label but neighbours with different labels
				// NOTE: node that nextG is always a graph and not a closure graph even though
				// it is stored in this format
				// Hence, nodes in nextG only has a single label
				// if(PRINT)
				// System.out.println("closureGNode:"+closureGNodeLabel.get(0)+"
				// ["+closureGraphNode.getID()+"] nextGNode:"+nextGNodeLabel.get(0)+"
				// ["+nextGraphNode.getID()+"]");

				if (closureGNodeLabel.contains(nextGNodeLabel.get(0)) == false) {
					jaccard = -1f;
					// if(PRINT)
					// System.out.println("[different labels] jaccard:"+jaccard);
				} else {
					// get neighbour labels of currGNode
					ArrayList<String> closureGNodeNeighbour = closureG.getNeighbourLabelsOf(closureGraphNode);
					ArrayList<String> nextGNodeNeighbour = nextG.getNeighbourLabelsOf(nextGraphNode);
					int denominator = closureGNodeNeighbour.size() + nextGNodeNeighbour.size();

					// if(PRINT)
					// {
					// System.out.println("closureGNodeNeighbour:"+closureGNodeNeighbour.toString());
					// System.out.println("nextGNodeNeighbour:"+nextGNodeNeighbour.toString());
					// }

					// find common neighbours (jaccard index numerator)
					int currPos = 0;
					while (currPos < closureGNodeNeighbour.size()) {
						String currLabel = closureGNodeNeighbour.get(currPos);
						int commonNodeIndex = nextGNodeNeighbour.indexOf(currLabel);
						if (commonNodeIndex == -1)// not found
						{
							closureGNodeNeighbour.remove(currPos);
						} else {
							currPos++;
							nextGNodeNeighbour.remove(commonNodeIndex);
						}
					}
					jaccard_numerator = Float.valueOf(closureGNodeNeighbour.size());

					jaccard_denominator = Float.valueOf(denominator) - jaccard_numerator;
					jaccard = jaccard_numerator / jaccard_denominator;

					// if(PRINT)
					// {
					// System.out.println("[same labels] jaccard_numerator:"+jaccard_numerator);
					// System.out.println("[same labels] jaccard_denominator:"+jaccard_denominator);
					// System.out.println("[same labels] jaccard:"+jaccard);
					// }
				}
				matrix.get(closureGNode).set(nextGNode, jaccard);
			}
		}
		return matrix;
	}

	/*
	 * private void getGraphMapping(JGraphtClosureGraph smallerG,
	 * JGraphtClosureGraph biggerG, boolean PRINT) { Mcgregor mcGregor = new
	 * Mcgregor(smallerG, biggerG, true); org.jgrapht.Graph<StringLabeledObject,
	 * StringLabeledObject> resultM1 = Mcgregor.maxCommonSubgraph(smallerG, biggerG,
	 * true); }
	 */
	private void getGraphMapping_2(JGraphtClosureGraph smallerG, JGraphtClosureGraph biggerG, boolean PRINT) {
		float BIAS = 2f;
		boolean U_WT_HAS_CHANGED = false;

		// compute similarity matrix W[u,v] for currG and nextG where u=nodes in currG
		// and v=nodes in nextG
		// init simMatrix array
		ArrayList<ArrayList<Float>> simMatrix = initMatrix(smallerG.getNumNodes(), biggerG.getNumNodes());
		simMatrix = populateMatrix(simMatrix, smallerG, biggerG, false);
		// for(int i=0; i<simMatrix.size(); i++)
		// System.out.println(simMatrix.get(i).toString());
		Comparator<priorityQueueTuple> pqTupleComparator = new tupleComparator();
		PriorityQueue<priorityQueueTuple> pq = new PriorityQueue<priorityQueueTuple>(pqTupleComparator);
		ArrayList<closureVertex> u_mate = new ArrayList<closureVertex>();
		ArrayList<Float> u_wt = new ArrayList<Float>();
		matched_u = new ArrayList<closureVertex>();
		matched_v = new ArrayList<closureVertex>();

		for (int n = 0; n < smallerG.getNumNodes(); n++) {
			// find v_m such that sim_{n,v_m}=max{sim_{n,v}|v\in nextG}
			ArrayList<Float> thisNodeSimVector = simMatrix.get(n);
			Float maxSim = Collections.max(thisNodeSimVector);
			int indexMaxSim = thisNodeSimVector.indexOf(maxSim);
			u_mate.add(biggerG.getVertexAt(indexMaxSim));
			u_wt.add(maxSim);
			if (maxSim.compareTo(0f) >= 0)// allow matching of only nodes with labels that has corresponding labels in
											// the mapping graph
			{
				// System.out.println("add to pq: " + maxSim + " " +
				// smallerG.getVertexAt(n).getID() + "[" + smallerG.getVertexAt(n).getLabel() +
				// "] " + biggerG.getVertexAt(indexMaxSim).getID() + "[" +
				// biggerG.getVertexAt(indexMaxSim).getLabel() + "]");
				pq.add(new priorityQueueTuple(maxSim, smallerG.getVertexAt(n), biggerG.getVertexAt(indexMaxSim)));
			}

		}
		// Iterator pqI=pq.iterator();
		// while(pqI.hasNext())
		// {
		// priorityQueueTuple t=(priorityQueueTuple)pqI.next();
		// System.out.println(t.getValue()+"
		// "+t.getU().getID()+"["+t.getU().getLabel()+"]
		// "+t.getV().getID()+"["+t.getV().getLabel()+"]");
		// }
		// System.out.println("currGraph:******************************");
		// currG.print();
		// System.out.println("nextGraph:==============================");
		// nextG.print();
		// System.out.println("simMatrix:++++++++++++++++++++++++++++++");
		// for(int m=0; m<simMatrix.size(); m++)
		// System.out.println(simMatrix.get(m).toString());
		// System.out.println("u_mate:---------------------------------");
		// for(int m=0; m<u_mate.size(); m++)
		// {
		// System.out.print(m+" ");
		// u_mate.get(m).print();
		// }
		// System.out.println("u_wt:"+u_wt.toString());
		while (pq.isEmpty() == false) {
			// dequeue pq
			priorityQueueTuple pqTuple = pq.remove();
			closureVertex u = pqTuple.getU();
			closureVertex v = pqTuple.getV();
			// System.out.println("dequeued");
			// System.out.println("value="+pqTuple.getValue()+"
			// u="+u.getID()+"["+u.getLabel()+"] v="+v.getID()+"["+v.getLabel()+"]");

			if (matched_u.contains(u) == true) {
				// do nothing
				// System.out.println("u is matched. Do nothing!");
			}
			if (matched_v.contains(v) == true) {
				// System.out.println("v is matched. Find next v with highest similarity that is
				// unmatched!");
				// find v_m such that sim_{u,v_m}=max{sim_{u,v}|v\in nextG, v is unmatched}
				// retrieve simVector for node u
				int u_index = smallerG.getIndexOfVertex(u);
				if (u_index != -1) {
					ArrayList<Float> thisNodeSimVector = simMatrix.get(u_index);
					int nextBestSimIndex = findNextBestMatchInSimMatrix(thisNodeSimVector, matched_v, biggerG);
					if (nextBestSimIndex != -1) {
						Float maxSim = thisNodeSimVector.get(nextBestSimIndex);
						pq.add(new priorityQueueTuple(maxSim, smallerG.getVertexAt(u_index),
								biggerG.getVertexAt(nextBestSimIndex)));
						u_mate.set(u_index, biggerG.getVertexAt(nextBestSimIndex));
						u_wt.set(u_index, maxSim);
						// System.out.println("u_mate:---------------------------------");
						// for (int m = 0; m < u_mate.size(); m++) {
						// System.out.print(m + " ");
						// u_mate.get(m).print();
						// }
						// System.out.println("u_wt:" + u_wt.toString());
					}
				}
			}
			// mark u and v as matched
			// System.out.println("mark u and v as matched");
			if (matched_u.contains(u) == false && matched_v.contains(v) == false) {
				matched_u.add(u);
				matched_v.add(v);
			}
			ArrayList<closureVertex> neighbour_u = smallerG.getNeighbourOf(u);
			ArrayList<closureVertex> neighbour_v = biggerG.getNeighbourOf(v);
			// remove matched neighbours
			neighbour_u.removeAll(matched_u);
			neighbour_v.removeAll(matched_v);
			// update unmatched neighbours to higher weights
			for (int ne_u = 0; ne_u < neighbour_u.size(); ne_u++) {
				U_WT_HAS_CHANGED = false;
				int ne_u_index = smallerG.getIndexOfVertex(neighbour_u.get(ne_u));
				for (int ne_v = 0; ne_v < neighbour_v.size(); ne_v++) {
					// retrieve simVector for node u
					int ne_v_index = biggerG.getIndexOfVertex(neighbour_v.get(ne_v));
					if (ne_u_index != -1 && ne_v_index != -1) {
						Float simValue = simMatrix.get(ne_u_index).get(ne_v_index);
						simValue = simValue * BIAS;
						if (Float.compare(simValue, u_wt.get(ne_u_index)) > 0) {
							// System.out.println("update unmatched neighbours to higher weight...simValue="
							// + simValue);
							// System.out.println("currG neighbour=" +
							// smallerG.getVertexAt(ne_u_index).getID() + "[" +
							// smallerG.getVertexAt(ne_u_index).getLabel() + "] nextG neighbour="
							// + biggerG.getVertexAt(ne_v_index).getID() + "[" +
							// biggerG.getVertexAt(ne_v_index).getLabel() + "]");
							u_mate.set(ne_u_index, biggerG.getVertexAt(ne_v_index));
							if (Float.compare(u_wt.get(ne_u_index), simValue) != 0) {
								u_wt.set(ne_u_index, simValue);
								U_WT_HAS_CHANGED = true;
							}
						}
					}
				}
				if (U_WT_HAS_CHANGED == true) {
					pq.add(new priorityQueueTuple(u_wt.get(ne_u_index), smallerG.getVertexAt(ne_u_index),
							u_mate.get(ne_u_index)));
				}
			}
		}
		if (PRINT) {
			System.out.println("----------------------------------------");
			for (int m = 0; m < matched_u.size(); m++) {
				System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel() + "]"
						+ "<->v=" + matched_v.get(m).getID() + "[" + matched_v.get(m).getLabel() + "]");
			}
			System.out.println("----------------------------------------");
		}
	}

	private void getGraphMapping(JGraphtClosureGraph smallerG, JGraphtClosureGraph biggerG, boolean PRINT) {
		float BIAS = 2f;
		boolean U_WT_HAS_CHANGED = false;

		// compute similarity matrix W[u,v] for currG and nextG where u=nodes in currG
		// and v=nodes in nextG
		// init simMatrix array
		ArrayList<ArrayList<Float>> simMatrix = initMatrix(smallerG.getNumNodes(), biggerG.getNumNodes());
		simMatrix = populateMatrix(simMatrix, smallerG, biggerG, PRINT);
		// for(int i=0; i<simMatrix.size(); i++)
		// System.out.println(simMatrix.get(i).toString());
		Comparator<priorityQueueTuple> pqTupleComparator = new tupleComparator();
		PriorityQueue<priorityQueueTuple> pq = new PriorityQueue<priorityQueueTuple>(pqTupleComparator);
		ArrayList<closureVertex> u_mate = new ArrayList<closureVertex>();
		ArrayList<Float> u_wt = new ArrayList<Float>();
		matched_u = new ArrayList<closureVertex>();
		matched_v = new ArrayList<closureVertex>();

		for (int n = 0; n < smallerG.getNumNodes(); n++) {
			// find v_m such that sim_{n,v_m}=max{sim_{n,v}|v\in nextG}
			ArrayList<Float> thisNodeSimVector = simMatrix.get(n);
			Float maxSim = Collections.max(thisNodeSimVector);
			int indexMaxSim = thisNodeSimVector.indexOf(maxSim);
			u_mate.add(biggerG.getVertexAt(indexMaxSim));
			u_wt.add(maxSim);
			if (maxSim.compareTo(0f) >= 0)// allow matching of only nodes with labels that has corresponding labels in
											// the mapping graph
			{
				// System.out.println("add to pq: " + maxSim + " " +
				// smallerG.getVertexAt(n).getID() + "[" + smallerG.getVertexAt(n).getLabel() +
				// "] " + biggerG.getVertexAt(indexMaxSim).getID() + "[" +
				// biggerG.getVertexAt(indexMaxSim).getLabel() + "]");
				pq.add(new priorityQueueTuple(maxSim, smallerG.getVertexAt(n), biggerG.getVertexAt(indexMaxSim)));
			}
		}
		// Iterator pqI=pq.iterator();
		// while(pqI.hasNext())
		// {
		// priorityQueueTuple t=(priorityQueueTuple)pqI.next();
		// System.out.println(t.getValue()+"
		// "+t.getU().getID()+"["+t.getU().getLabel()+"]
		// "+t.getV().getID()+"["+t.getV().getLabel()+"]");
		// }
		// System.out.println("currGraph:******************************");
		// currG.print();
		// System.out.println("nextGraph:==============================");
		// nextG.print();
		// System.out.println("simMatrix:++++++++++++++++++++++++++++++");
		// for(int m=0; m<simMatrix.size(); m++)
		// System.out.println(simMatrix.get(m).toString());
		// System.out.println("u_mate:---------------------------------");
		// for(int m=0; m<u_mate.size(); m++)
		// {
		// System.out.print(m+" ");
		// u_mate.get(m).print();
		// }
		// System.out.println("u_wt:"+u_wt.toString());
		while (pq.isEmpty() == false) {
			// dequeue pq
			priorityQueueTuple pqTuple = pq.remove();
			closureVertex u = pqTuple.getU();
			closureVertex v = pqTuple.getV();
			// System.out.println("dequeued");
			// System.out.println("value="+pqTuple.getValue()+"
			// u="+u.getID()+"["+u.getLabel()+"] v="+v.getID()+"["+v.getLabel()+"]");

			if (matched_u.contains(u) == true) {
				// do nothing
				// System.out.println("u is matched. Do nothing!");
			}
			if (matched_v.contains(v) == true) {
				// System.out.println("v is matched. Find next v with highest similarity that is
				// unmatched!");
				// find v_m such that sim_{u,v_m}=max{sim_{u,v}|v\in nextG, v is unmatched}
				// retrieve simVector for node u
				int u_index = smallerG.getIndexOfVertex(u);
				if (u_index != -1) {
					ArrayList<Float> thisNodeSimVector = simMatrix.get(u_index);
					int nextBestSimIndex = findNextBestMatchInSimMatrix(thisNodeSimVector, matched_v, biggerG);
					if (nextBestSimIndex != -1) {
						Float maxSim = thisNodeSimVector.get(nextBestSimIndex);
						pq.add(new priorityQueueTuple(maxSim, smallerG.getVertexAt(u_index),
								biggerG.getVertexAt(nextBestSimIndex)));
						u_mate.set(u_index, biggerG.getVertexAt(nextBestSimIndex));
						u_wt.set(u_index, maxSim);
						// System.out.println("u_mate:---------------------------------");
						// for (int m = 0; m < u_mate.size(); m++) {
						// System.out.print(m + " ");
						// u_mate.get(m).print();
						// }
						// System.out.println("u_wt:" + u_wt.toString());
					}
				}
			}
			// mark u and v as matched
			// System.out.println("mark u and v as matched");
			if (matched_u.contains(u) == false && matched_v.contains(v) == false) {
				matched_u.add(u);
				matched_v.add(v);
			}
			ArrayList<closureVertex> neighbour_u = smallerG.getNeighbourOf(u);
			ArrayList<closureVertex> neighbour_v = biggerG.getNeighbourOf(v);
			// remove matched neighbours
			neighbour_u.removeAll(matched_u);
			neighbour_v.removeAll(matched_v);
			// update unmatched neighbours to higher weights
			for (int ne_u = 0; ne_u < neighbour_u.size(); ne_u++) {
				U_WT_HAS_CHANGED = false;
				int ne_u_index = smallerG.getIndexOfVertex(neighbour_u.get(ne_u));
				for (int ne_v = 0; ne_v < neighbour_v.size(); ne_v++) {
					// retrieve simVector for node u
					int ne_v_index = biggerG.getIndexOfVertex(neighbour_v.get(ne_v));
					if (ne_u_index != -1 && ne_v_index != -1) {
						Float simValue = simMatrix.get(ne_u_index).get(ne_v_index);
						simValue = simValue * BIAS;
						if (Float.compare(simValue, u_wt.get(ne_u_index)) > 0) {
							// System.out.println("update unmatched neighbours to higher weight...simValue="
							// + simValue);
							// System.out.println("currG neighbour=" +
							// smallerG.getVertexAt(ne_u_index).getID() + "[" +
							// smallerG.getVertexAt(ne_u_index).getLabel() + "] nextG neighbour="
							// + biggerG.getVertexAt(ne_v_index).getID() + "[" +
							// biggerG.getVertexAt(ne_v_index).getLabel() + "]");
							u_mate.set(ne_u_index, biggerG.getVertexAt(ne_v_index));
							if (Float.compare(u_wt.get(ne_u_index), simValue) != 0) {
								u_wt.set(ne_u_index, simValue);
								U_WT_HAS_CHANGED = true;
							}
						}
					}
				}
				if (U_WT_HAS_CHANGED == true) {
					pq.add(new priorityQueueTuple(u_wt.get(ne_u_index), smallerG.getVertexAt(ne_u_index),
							u_mate.get(ne_u_index)));
				}
			}
		}

		if (PRINT) {
			System.out.println("----------------------------------------");
			for (int m = 0; m < matched_u.size(); m++) {
				if (matched_v.get(m) != null)
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=" + matched_v.get(m).getID() + "[" + matched_v.get(m).getLabel() + "]");
				else
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=NULL");
			}
			System.out.println("----------------------------------------");
		}
	}

	private JGraphtClosureGraph updateClosure(JGraphtClosureGraph smallerG, JGraphtClosureGraph biggerG,
			ArrayList<Integer> clusterGraphList, int newGraphID) {
		JGraphtClosureGraph closureG = new JGraphtClosureGraph();
		int nodeID = 0;
		ArrayList<closureVertex> smallerG_cV = new ArrayList<closureVertex>();
		ArrayList<closureVertex> biggerG_cV = new ArrayList<closureVertex>();
		ArrayList<closureVertex> smallerG_corrNewG_cV = new ArrayList<closureVertex>();
		ArrayList<closureVertex> biggerG_corrNewG_cV = new ArrayList<closureVertex>();

		// create closureVertex for mapped nodes
		if (matched_u.size() > 0) {
			for (int i = 0; i < matched_u.size(); i++) {
				// get label
				ArrayList<String> mapped_node_label = matched_u.get(i).getLabel();
				closureVertex v = new closureVertex(nodeID++, mapped_node_label);
				closureG.addNode(v);
				smallerG_cV.add(matched_u.get(i));
				biggerG_cV.add(matched_v.get(i));
				smallerG_corrNewG_cV.add(v);
				biggerG_corrNewG_cV.add(v);
			}
		}
		// get unmatched nodes from smallerG and put them into closure
		if (smallerG.getNumNodes() > matched_u.size()) {
			ArrayList<closureVertex> unmatchedNodes = getUnmatchedVertex(matched_u, smallerG);
			// System.out.println("smallerG unmatched nodes: "+unmatchedNodes.size());
			if (unmatchedNodes.size() > 0) {
				for (int i = 0; i < unmatchedNodes.size(); i++) {
					// get label
					// unmatchedNodes.get(i).print();
					ArrayList<String> label = unmatchedNodes.get(i).getLabel();
					closureVertex v = new closureVertex(nodeID++, label);
					closureG.addNode(v);
					smallerG_cV.add(unmatchedNodes.get(i));
					smallerG_corrNewG_cV.add(v);
				}
			}
		}
		// get unmatched nodes from biggerG and put them into closure
		if (biggerG.getNumNodes() > matched_v.size()) {
			ArrayList<closureVertex> unmatchedNodes = getUnmatchedVertex(matched_v, biggerG);
			// System.out.println("biggerG unmatched nodes: "+unmatchedNodes.size());
			if (unmatchedNodes.size() > 0) {
				for (int i = 0; i < unmatchedNodes.size(); i++) {
					// get label
					// unmatchedNodes.get(i).print();
					ArrayList<String> label = unmatchedNodes.get(i).getLabel();
					closureVertex v = new closureVertex(nodeID++, label);
					closureG.addNode(v);
					biggerG_cV.add(unmatchedNodes.get(i));
					biggerG_corrNewG_cV.add(v);
				}
			}
		}
		// update the clusterGraphList
		if (clusterGraphList.contains(newGraphID) == false)
			clusterGraphList.add(newGraphID);
		closureG.addToClusterGraphList(clusterGraphList);
		// add the closureEdges from the smallerG
		ArrayList<closureEdge> smallerG_cE = smallerG.getClosureEdgeList();
		for (int i = 0; i < smallerG_cE.size(); i++) {
			closureEdge newEdge;
			closureEdge edge = smallerG_cE.get(i);
			closureVertex sV = edge.getSource();
			closureVertex tV = edge.getTarget();
			closureVertex mapped_sV, mapped_tV;
			ArrayList<Integer> graphList = edge.getGraphIDList();
			int sV_indexToSmallerGVertexList = smallerG_cV.indexOf(sV);
			int tV_indexToSmallerGVertexList = smallerG_cV.indexOf(tV);
			int sV_matchedIndex = matched_u.indexOf(sV);
			int tV_matchedIndex = matched_u.indexOf(tV);
			mapped_sV = smallerG_corrNewG_cV.get(sV_indexToSmallerGVertexList);
			mapped_tV = smallerG_corrNewG_cV.get(tV_indexToSmallerGVertexList);
			if (sV_matchedIndex != -1 && tV_matchedIndex != -1)// this is a mapped edge
			{
				// System.out.println("mapped edge :) :) - update graphList");
				// System.out.println("sV_matchedIndex:"+sV_matchedIndex+"
				// tV_matchedIndex:"+tV_matchedIndex);
				// locate the corresponding mapped edge in the biggerG and combine the
				// graphIDList
				int mapped_sV_index = biggerG_corrNewG_cV.indexOf(mapped_sV);
				int mapped_tV_index = biggerG_corrNewG_cV.indexOf(mapped_tV);
				closureVertex biggerG_sV = biggerG_cV.get(mapped_sV_index);
				closureVertex biggerG_tV = biggerG_cV.get(mapped_tV_index);
				// biggerG_sV.print();
				// biggerG_tV.print();
				closureEdge e = biggerG.getClosureEdge(biggerG_sV, biggerG_tV);
				if (e != null) {
					// e.print();
					ArrayList<Integer> biggerGEdgeGraphList = e.getGraphIDList();
					for (int b = 0; b < biggerGEdgeGraphList.size(); b++) {
						if (graphList.contains(biggerGEdgeGraphList.get(b)) == false)
							graphList.add(biggerGEdgeGraphList.get(b));
					}
				}
			}
			// System.out.println("add edge with graphList: "+graphList.toString());
			newEdge = new closureEdge(mapped_sV, mapped_tV, graphList);
			closureG.addEdge(mapped_sV, mapped_tV, newEdge);
		}
		// add the closureEdges from the biggerG
		ArrayList<closureEdge> biggerG_cE = biggerG.getClosureEdgeList();
		for (int i = 0; i < biggerG_cE.size(); i++) {
			closureEdge newEdge;
			closureEdge edge = biggerG_cE.get(i);
			closureVertex sV = edge.getSource();
			closureVertex tV = edge.getTarget();
			closureVertex mapped_sV, mapped_tV;
			ArrayList<Integer> graphList = edge.getGraphIDList();
			int sV_indexToBiggerGVertexList = biggerG_cV.indexOf(sV);
			int tV_indexToBiggerGVertexList = biggerG_cV.indexOf(tV);
			int sV_matchedIndex = matched_v.indexOf(sV);
			int tV_matchedIndex = matched_v.indexOf(tV);
			mapped_sV = biggerG_corrNewG_cV.get(sV_indexToBiggerGVertexList);
			mapped_tV = biggerG_corrNewG_cV.get(tV_indexToBiggerGVertexList);
			if (sV_matchedIndex != -1 && tV_matchedIndex != -1)// the vertices are mapped...but need to check if the
																// edge is also present in smallerG
			{
				// locate the corresponding mapped edge in the biggerG and combine the
				// graphIDList
				int mapped_sV_index = smallerG_corrNewG_cV.indexOf(mapped_sV);
				int mapped_tV_index = smallerG_corrNewG_cV.indexOf(mapped_tV);
				closureVertex smallerG_sV = biggerG_cV.get(mapped_sV_index);
				closureVertex smallerG_tV = biggerG_cV.get(mapped_tV_index);
				// biggerG_sV.print();
				// biggerG_tV.print();
				closureEdge e = smallerG.getClosureEdge(smallerG_sV, smallerG_tV);
				if (e != null) {
					// do nothing since already added. Don't want to double count
					// System.out.println("has been matched and added, skip !!!");
				} else {
					// this edge though having both vertices that are mapped, is not present in the
					// smallerG
					// Thus, it is a unique edge of biggerG and should be added.
					newEdge = new closureEdge(mapped_sV, mapped_tV, graphList);
					// System.out.println("add edge with graphList: "+graphList.toString());
					closureG.addEdge(mapped_sV, mapped_tV, newEdge);
				}
			} else {
				newEdge = new closureEdge(mapped_sV, mapped_tV, graphList);
				// System.out.println("add edge with graphList: "+graphList.toString());
				closureG.addEdge(mapped_sV, mapped_tV, newEdge);
			}
		}
		// print out all nodes to check
		// closureG.print();

		return closureG;
	}

	private ArrayList<closureVertex> getUnmatchedVertex(ArrayList<closureVertex> matchedNodes, JGraphtClosureGraph g) {
		ArrayList<closureVertex> list = new ArrayList<closureVertex>();
		ArrayList<closureVertex> tmp = new ArrayList<closureVertex>();

		list = g.getClosureVertexList();
		for (int i = 0; i < list.size(); i++)
			tmp.add(list.get(i));
		tmp.removeAll(matchedNodes);
		return tmp;
	}

	public JGraphtClosureGraph performClosure(boolean PRINT) {
		// System.out.println("[performClosure] graphIdList:"+graphIdList.toString());
		if (graphIdList.size() == 0) {
			return null;
		} else if (graphIdList.size() == 1) {
			int graphId = graphIdList.get(0);
			JGraphtGraph thisGraph = jGraphTGraphSet.get(0);
			closureGraph = new JGraphtClosureGraph(thisGraph, graphId);
			// closureGraph.print();
		} else {
			closureGraph = new JGraphtClosureGraph(jGraphTGraphSet.get(0), graphIdList.get(0));
			if (PRINT)
				System.out.println("graphIdList size: " + graphIdList.size() + " closureGraph=" + graphIdList.get(0)
						+ " size:" + closureGraph.getNumNodes());
			for (int i = 1; i < graphIdList.size(); i++) {
				JGraphtClosureGraph nextG = new JGraphtClosureGraph(jGraphTGraphSet.get(i), graphIdList.get(i));

				JGraphtClosureGraph smallerG, biggerG;
				if (closureGraph.getNumNodes() > nextG.getNumNodes()) {
					smallerG = nextG;
					biggerG = closureGraph;
					if (PRINT)
						System.out.println("smallerG=" + graphIdList.get(i) + " size: " + nextG.getNumNodes()
								+ " and biggerG=closureGraph");
				} else {
					smallerG = closureGraph;
					biggerG = nextG;
					if (PRINT)
						System.out.println("smallerG=closureGraph and biggerG=" + graphIdList.get(i) + " size: "
								+ nextG.getNumNodes());
				}
				getGraphMapping(smallerG, biggerG, PRINT);
				if (PRINT) {
					padMatchedList(smallerG, biggerG, PRINT);
					removeMismatch(smallerG, biggerG, PRINT);
					boolean REFINEMENT = true;

					while (REFINEMENT)
						REFINEMENT = refineMapping(smallerG, biggerG, PRINT);
				}
				// System.out.println("smallerG++++++++++++++++++++++++++");
				// smallerG.print();
				// System.out.println("biggerG++++++++++++++++++++++++++");
				// biggerG.print();
				// System.out.println("++++++++++++++++++++++++++++++++");
				removeNullFromMap(PRINT);
				closureGraph = updateClosure(smallerG, biggerG, closureGraph.getClusterGraphList(), graphIdList.get(i));
				if (PRINT) {
					System.out
							.println(">>>>>>>>>>>>>> closure of " + graphIdList.get(0) + " and " + graphIdList.get(i));
					closureGraph.print();
				}
			}
			if (PRINT == true) {
				System.out.println(">>>>>>>>>>>>>> closure of " + graphIdList.toString());
				System.out.println("done a cluster! closure graph as below:_______________________");
				closureGraph.print();
			}
		}
		// System.out.println("performing closure....");
		return closureGraph;
	}

	private void removeNullFromMap(boolean PRINT) {
		int counter = 0;
		while (counter < matched_u.size()) {
			if (matched_v.get(counter) == null) {
				matched_u.remove(counter);
				matched_v.remove(counter);
			} else
				counter++;
		}
		if (PRINT) {
			System.out.println("---------------REMOVE NULL FROM MAP-------------------");
			for (int m = 0; m < matched_u.size(); m++) {
				if (matched_v.get(m) != null)
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=" + matched_v.get(m).getID() + "[" + matched_v.get(m).getLabel() + "]");
				else
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=NULL");
			}
			System.out.println("----------------------------------------");
		}
	}

	private boolean refineMapping(JGraphtClosureGraph smallerG, JGraphtClosureGraph biggerG, boolean PRINT) {
		ArrayList<closureVertex> correctMatch_u = new ArrayList<closureVertex>();
		ArrayList<closureVertex> correctMatch_v = new ArrayList<closureVertex>();
		ArrayList<closureVertex> unmatch_u = new ArrayList<closureVertex>();
		boolean REFINEMENT = false;

		for (int i = 0; i < matched_u.size(); i++) {
			if (matched_v.get(i) == null)
				unmatch_u.add(matched_u.get(i));
			else {
				correctMatch_u.add(matched_u.get(i));
				correctMatch_v.add(matched_v.get(i));
			}
		}

		for (int i = 0; i < correctMatch_u.size(); i++) {
			closureVertex u = correctMatch_u.get(i);
			closureVertex v = correctMatch_v.get(i);
			ArrayList<closureVertex> uNeighbour = smallerG.getNeighbourOf(u);
			ArrayList<closureVertex> vNeighbour = biggerG.getNeighbourOf(v);
			// get unmatched neighbours. check for unmatched neighbours if there are
			// corresponding nodes with same labels in smallerG and biggerG.
			// for each such corresponding node, get the similarity score which is the
			// jaccard similarity of the labels of the neighbours of that node.
			uNeighbour.removeAll(correctMatch_u);
			vNeighbour.removeAll(correctMatch_v);
			if (uNeighbour.size() != 0 && vNeighbour.size() != 0) {
				for (int uN = 0; uN < uNeighbour.size(); uN++) {
					closureVertex uNei = uNeighbour.get(uN);
					String uLabel = uNei.getLabel().get(0);
					ArrayList<Float> vNeighbourSimScore = new ArrayList<Float>();
					// get neighbours of uNei
					ArrayList<closureVertex> uNeiNeighbour = smallerG.getNeighbourOf(uNei);
					ArrayList<String> uNeiNeighbour_label = new ArrayList<String>();
					for (int k = 0; k < uNeiNeighbour.size(); k++)
						uNeiNeighbour_label.add(uNeiNeighbour.get(k).getLabel().get(0));

					for (int vN = 0; vN < vNeighbour.size(); vN++) {
						closureVertex vNei = vNeighbour.get(vN);
						String vLabel = vNei.getLabel().get(0);
						if (uLabel.compareTo(vLabel) == 0) {
							// get neighbours of vNei
							ArrayList<closureVertex> vNeiNeighbour = biggerG.getNeighbourOf(vNei);
							ArrayList<String> vNeiNeighbour_label = new ArrayList<String>();
							for (int k = 0; k < vNeiNeighbour.size(); k++)
								vNeiNeighbour_label.add(vNeiNeighbour.get(k).getLabel().get(0));
							float denominator = uNeiNeighbour_label.size() + vNeiNeighbour_label.size();
							float distinct, jaccard, common;
							vNeiNeighbour_label.retainAll(uNeiNeighbour_label);
							common = vNeiNeighbour_label.size();
							distinct = denominator - common;
							jaccard = common / distinct;
							vNeighbourSimScore.add(jaccard);
						} else
							vNeighbourSimScore.add(-1f);
					}
					float maxSimScore = Collections.max(vNeighbourSimScore);
					if (maxSimScore > -1f)// found a potential match to expand
					{
						// update matched_u and matched_v
						int matchedU_index = matched_u.indexOf(uNei);
						int vNeighbourSimScore_index = vNeighbourSimScore.indexOf(maxSimScore);
						matched_v.set(matchedU_index, vNeighbour.get(vNeighbourSimScore_index));
						REFINEMENT = true;
					}
				}
			}
		}

		if (PRINT) {
			System.out.println("---------------REFINED-------------------");
			for (int m = 0; m < matched_u.size(); m++) {
				if (matched_v.get(m) != null)
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=" + matched_v.get(m).getID() + "[" + matched_v.get(m).getLabel() + "]");
				else
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=NULL");
			}
			System.out.println("----------------------------------------");
		}
		return REFINEMENT;
	}

	private void padMatchedList(JGraphtClosureGraph smallerG, JGraphtClosureGraph biggerG, boolean PRINT) {
		// sometimes the getGraphMapping do not return all matches since some candidated
		// have mismatched labels
		if (matched_u.size() < smallerG.getNumNodes())// some nodes are not matched!!
		{
			// fill it up
			System.out.println("we try to find some matched_u that has not been matched and missing from list!");
			ArrayList<closureVertex> smallerGVList = smallerG.getClosureVertexList();
			for (int i = 0; i < smallerGVList.size(); i++) {
				if (matched_u.contains(smallerGVList.get(i)) == false) {
					matched_u.add(smallerGVList.get(i));
					matched_v.add(null);
				}
			}
		}

		if (PRINT) {
			System.out.println("---------------PAD MATCH-------------------");
			for (int m = 0; m < matched_u.size(); m++) {
				if (matched_v.get(m) != null)
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=" + matched_v.get(m).getID() + "[" + matched_v.get(m).getLabel() + "]");
				else
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=NULL");
			}
			System.out.println("----------------------------------------");
		}
	}

	private void removeMismatch(JGraphtClosureGraph smallerG, JGraphtClosureGraph biggerG, boolean PRINT) {
		// ArrayList<closureVertex> matched_u;
		// ArrayList<closureVertex> matched_v;
		ArrayList<closureVertex> matched_u_copy = new ArrayList<closureVertex>();
		ArrayList<closureVertex> matched_v_copy = new ArrayList<closureVertex>();
		ArrayList<ArrayList<closureVertex>> connectedComponent = new ArrayList<ArrayList<closureVertex>>();
		ArrayList<Integer> matched_u_cc = new ArrayList<Integer>();
		ArrayList<closureVertex> matched_u_seed = new ArrayList<closureVertex>();

		for (int i = 0; i < matched_u.size(); i++) {
			matched_u_copy.add(matched_u.get(i));
			matched_u_cc.add(-1);
			matched_u_seed.add(matched_u.get(i));
		}
		for (int i = 0; i < matched_v.size(); i++)
			matched_v_copy.add(matched_v.get(i));

		System.out.println("matched_u_cc: " + matched_u_cc.toString());
		int cc_count = 0;

		while (matched_u_seed.size() > 0) {
			closureVertex seed = matched_u_seed.get(0);
			int index = matched_u_copy.indexOf(seed);
			matched_u_cc.set(index, cc_count);

			// DFS on seed. check for each visited vertex that matched_v is the same as
			// expected in biggerG.
			// Initially mark all vertices as not visited
			ArrayList<Boolean> visited = new ArrayList<Boolean>();
			for (int i = 0; i < matched_u_copy.size(); i++)
				visited.add(false);
			// Create a stack for DFS
			Stack<closureVertex> stack = new Stack<>();
			// Push the current source node
			stack.push(seed);
			while (stack.empty() == false) {
				// Pop a vertex from stack and print it
				seed = stack.peek();
				stack.pop();
				// Stack may contain same vertex twice. So we need to print the popped item only
				// if it is not visited.
				if (visited.get(matched_u_copy.indexOf(seed)) == false) {
					matched_u_seed.remove(seed);
					int seedIndex = matched_u_copy.indexOf(seed);
					matched_u_cc.set(seedIndex, cc_count);
					visited.set(matched_u_copy.indexOf(seed), true);
				}
				// Get all adjacent vertices of the popped vertex seed. If a adjacent has not
				// been visited, then puah it
				// to the stack.
				ArrayList<closureVertex> seedNeighbour = smallerG.getNeighbourOf(seed);
				// get matched vertex in biggerG
				int seed_index = matched_u_copy.indexOf(seed);
				closureVertex vBig = matched_v_copy.get(seed_index);
				ArrayList<closureVertex> vBigNeighbour = biggerG.getNeighbourOf(vBig);
				System.out.println("vBigNeighbour " + vBigNeighbour.size());
				ArrayList<closureVertex> vMatchNeighbour = new ArrayList<closureVertex>();
				ArrayList<closureVertex> uMatchNeighbour = new ArrayList<closureVertex>();
				for (int i = 0; i < seedNeighbour.size(); i++) {
					closureVertex seedNei = seedNeighbour.get(i);
					int seedNei_index = matched_u_copy.indexOf(seedNei);
					vMatchNeighbour.add(matched_v_copy.get(seedNei_index));
				}

				vMatchNeighbour.retainAll(vBigNeighbour);
				if (vMatchNeighbour.size() > 0) {
					for (int i = 0; i < vMatchNeighbour.size(); i++) {
						closureVertex seedNei = vMatchNeighbour.get(i);
						int seedNei_index = matched_v_copy.indexOf(seedNei);
						uMatchNeighbour.add(matched_u_copy.get(seedNei_index));
					}

					for (int i = 0; i < uMatchNeighbour.size(); i++) {
						if (!visited.get(matched_u_copy.indexOf(uMatchNeighbour.get(i))))
							stack.push(uMatchNeighbour.get(i));
					}
				}
			}
			cc_count++;
			System.out.println("matched_u_cc: " + matched_u_cc.toString());
		}

		// find most largest cc
		ArrayList<Integer> cc_size = new ArrayList<Integer>();
		System.out.println("cc_count:" + cc_count);
		for (int i = 0; i < cc_count; i++)
			cc_size.add(0);
		for (int i = 0; i < matched_u_cc.size(); i++) {
			int originalCount = cc_size.get(matched_u_cc.get(i));
			cc_size.set(matched_u_cc.get(i), originalCount + 1);
		}
		int maxSize = Collections.max(cc_size);
		int max_cc = cc_size.indexOf(maxSize);
		System.out.println("cc_size: " + cc_size.toString() + " max_cc:" + max_cc);

		// reset matched_v for all cc (except max_cc)
		for (int i = 0; i < matched_v.size(); i++) {
			if (matched_u_cc.get(i) != max_cc)
				matched_v.set(i, null);
		}

		if (PRINT) {
			System.out.println("---------------REMOVE MISMATCH-------------------");
			for (int m = 0; m < matched_u.size(); m++) {
				if (matched_v.get(m) != null)
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=" + matched_v.get(m).getID() + "[" + matched_v.get(m).getLabel() + "]");
				else
					System.out.println("m:" + m + " u=" + matched_u.get(m).getID() + "[" + matched_u.get(m).getLabel()
							+ "]" + "<->v=NULL");
			}
			System.out.println("----------------------------------------");
		}
	}

	private int findNextBestMatchInSimMatrix(ArrayList<Float> simVector, ArrayList<closureVertex> matchedVertex,
			JGraphtClosureGraph graph) {
		int vectorIndex = -1;
		boolean FOUND = false;
		ArrayList<Integer> matchedIndices = new ArrayList<Integer>();
		ArrayList<Integer> tempVectorIndex = new ArrayList<Integer>();
		for (int i = 0; i < matchedVertex.size(); i++) {
			int matchedIndex = graph.getIndexOfVertex(matchedVertex.get(i));
			if (matchedIndex != -1) {
				matchedIndices.add(matchedIndex);
			}
		}
		ArrayList<Float> tempVector = new ArrayList<Float>();
		for (int i = 0; i < simVector.size(); i++) {
			tempVector.add(simVector.get(i));
			tempVectorIndex.add(i);
		}
		while (!FOUND && tempVector.size() > 0) {
			Float maxSim = Collections.max(tempVector);
			if (maxSim.compareTo(0f) >= 0)// allow matching of only nodes with labels that has corresponding labels in
											// the mapping graph
			{
				int index = tempVector.indexOf(maxSim);
				vectorIndex = tempVectorIndex.get(index);
				if (matchedIndices.contains(vectorIndex) == false) {
					FOUND = true;
				} else {
					tempVector.remove(index);
					tempVectorIndex.remove(index);
				}
			} else {
				FOUND = true;
				vectorIndex = -1; // best matched is of different label
			}
		}

		return vectorIndex;
	}

	private ArrayList<ArrayList<Float>> initMatrix(int row, int col) {
		ArrayList<ArrayList<Float>> matrix = new ArrayList<ArrayList<Float>>();
		for (int j = 0; j < row; j++) {
			ArrayList rowList = new ArrayList<Double>();
			for (int k = 0; k < col; k++) {
				rowList.add(0f);
			}
			matrix.add(rowList);
		}
		return matrix;
	}

	public int getClusterGraphElementGraphIDAt(int pos) {
		if (pos >= 0 && pos < jGraphTGraphSet.size())
			return graphIdList.get(pos);
		return -1;
	}

	public synchronized ArrayList<Integer> readGraphSetForSpecificGraphIds(String filename, ArrayList<Integer> idList,
			boolean CONTAINMENT_CHECK) {
		// graphIdList = idList;
		graphIdList = new ArrayList<Integer>();
		uniqueVertexLabelsInCluster = new ArrayList<String>();// unique vertex labels in graphs in this cluster
		uniqueVertexLabelsInCluster_freq = new ArrayList<Integer>();// # graphs in this cluster having the vertex label
		uniqueVertexLabelsInCluster_valency = new ArrayList<Integer>();
		uniqueEdgeLabelsInCluster = new ArrayList<String>();// unique edge labels in graphs in this cluster
		uniqueEdgeLabelsInCluster_freq = new ArrayList<Integer>();// # graphs in this cluster having the edge label
		ArrayList<String> uniqueVertexLabels = new ArrayList<String>();
		ArrayList<String> uniqueEdgeLabels = new ArrayList<String>();
		ArrayList<simpleVertex> vertexList = new ArrayList<simpleVertex>();

		// System.out.println("readGraphSetForSpecificGraphIds filename: "+filename);

		ArrayList<Integer> workingList = new ArrayList<Integer>();
		String strLine = null;
		File fin = new File(filename);
		BufferedReader br;

		for (int i = 0; i < idList.size(); i++) {
			if (CONTAINMENT_CHECK) {
				if (fullyContainedGraphID.contains(idList.get(i)) == false)
					workingList.add(idList.get(i));
			} else
				workingList.add(idList.get(i));
		}
		workingList.add(-1);

		// for (int i = 0; i < workingList.size(); i++) {
		// System.out.println("workingList:"+i+" is :"+workingList.get(i));
		// }

		try {
			br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fin))));

			int graphid = -1;
			int eNum = 0;

			Graph tmpgraph = null;
			JGraphtGraph tmpJGraphTGraph = new JGraphtGraph();
			boolean currentGraphInWantedList = false;
			graphSet = new ArrayList<Graph>();
			jGraphTGraphSet = new ArrayList<JGraphtGraph>();
			// System.out.println("start while....");
			while ((strLine = br.readLine()) != null && workingList.size() > 0) {
				if (strLine.contains("t # ")) {
					int spaceIndex = strLine.indexOf(" ", 4);
					if (spaceIndex != -1) {
						graphid = Integer.parseInt(strLine.substring(4, spaceIndex));
						if (workingList.contains(graphid) == true) {
							// System.out.println("graphid:"+graphid);
							currentGraphInWantedList = true;
							int index = workingList.indexOf(graphid);
							workingList.remove(index);
							eNum = 0;
							String[] str = strLine.split("\\s");
							int nodenum = Integer.parseInt(str[3]);
							//if(nodenum <= 1) System.out.println(graphid + "has no more than one node.");
							//if (nodenum > 1) {
						   //// Modified by Kai
							if (nodenum >= 1) {
								//if(nodenum == 1) System.out.println(graphid + "has only one node.");
								tmpgraph = new Graph();
								tmpgraph.setGraphid(graphid);
								tmpgraph.setVertexNum(nodenum);
								// store graph data into graph structure for generating closure graphs later
								tmpJGraphTGraph = new JGraphtGraph(graphid);
								// reset uniqueVertexLabel, uniqueVertexLabels_freq, uniqueEdgeLabels and
								// uniqueEdgeLabels_fre
								uniqueVertexLabels = new ArrayList<String>();
								uniqueEdgeLabels = new ArrayList<String>();
								//// Modified by Kai
								vertexList = new ArrayList<simpleVertex>();
							} else
								currentGraphInWantedList = false;
						} else {
							currentGraphInWantedList = false;
							if (workingList.size() == 1 && workingList.get(0) == -1) {
								workingList.remove(workingList.indexOf(-1));// terminate the search
							}
						}
					}
				} else if (currentGraphInWantedList == true) {
					// System.out.println(strLine);
					if (strLine.contains("v")) {
						Vertex node = new Vertex();
						String[] nodeline = strLine.split("\\s");
						int id = Integer.parseInt(nodeline[1]);
						String label = nodeline[2];
						node.setLabel(label);
						node.setID(id);
						tmpgraph.addNode(node);
						// store graph data into graph structure for generating closure graphs later
						simpleVertex n = new simpleVertex(id, label);
						if (uniqueVertexLabels.contains(label) == false)
							uniqueVertexLabels.add(label);
						tmpJGraphTGraph.addNode(n);
						vertexList.add(n);
					} else if (strLine.contains("e")) {
						eNum++;
						String[] strnode = strLine.split("\\s");
						int v1 = Integer.parseInt(strnode[1]);
						int v2 = Integer.parseInt(strnode[2]);
						// Connect v1 with v2; Increase degree of each;
						tmpgraph.addEdge(v1, v2);
						tmpgraph.getNode(v1).incDegree();
						tmpgraph.getNode(v2).incDegree();
						// set v1, v2 to be in In-list of each other;
						tmpgraph.getNode(v1).setIn(v2);
						tmpgraph.getNode(v2).setIn(v1);
						// store graph data into graph structure for generating closure graphs later
						String edgeLabel = tmpJGraphTGraph.addEdge(v1, v2);
						if (uniqueEdgeLabels.contains(edgeLabel) == false)
							uniqueEdgeLabels.add(edgeLabel);
					} else {
						// System.out.println("----"+strLine.length()+"-----");
						// System.out.println("----"+eNum+"-----");
						tmpgraph.setEdgeNum(eNum);
						graphSet.add(tmpgraph);
						// store graph data into graph structure for generating closure graphs later
						// tmpJGraphTGraph.print(); //debug purpose
						jGraphTGraphSet.add(tmpJGraphTGraph);
						graphIdList.add(graphid);

						// System.out.println(graphSet.size());
						// System.out.println(jGraphTGraphSet.size());
						// System.out.println(graphIdList.size());

						// exportGraphToSVG(tmpgraph, graphid);
						// update uniqueVertexLabelsInCluster and uniqueEdgeLabelsInCluster
						for (int l = 0; l < uniqueVertexLabels.size(); l++) {
							String vlabel = uniqueVertexLabels.get(l);
							int index = uniqueVertexLabelsInCluster.indexOf(vlabel);
							if (index == -1) {
								uniqueVertexLabelsInCluster.add(vlabel);
								uniqueVertexLabelsInCluster_freq.add(1);
								uniqueVertexLabelsInCluster_valency.add(0);
							} else {
								int freq = uniqueVertexLabelsInCluster_freq.get(index) + 1;
								uniqueVertexLabelsInCluster_freq.set(index, freq);
							}
						}
						// System.out.println("vertexList.size():"+vertexList.size());
						for (int n = 0; n < vertexList.size(); n++) {
							// System.out.println(tmpJGraphTGraph.getGraphID()+" n:"+n);
							simpleVertex v = vertexList.get(n);
							String vLabel = v.getLabel();
							int vLabel_index = uniqueVertexLabelsInCluster.indexOf(vLabel);
							int vDegree = tmpJGraphTGraph.getDegreeOf(v);
							// System.out.println( "vDegree:"+vDegree);
							int currMaxValency = uniqueVertexLabelsInCluster_valency.get(vLabel_index);
							if (currMaxValency < vDegree)// this label valency information has not been added yet
								uniqueVertexLabelsInCluster_valency.set(vLabel_index, vDegree);
						}
						for (int l = 0; l < uniqueEdgeLabels.size(); l++) {
							String elabel = uniqueEdgeLabels.get(l);
							int index = uniqueEdgeLabelsInCluster.indexOf(elabel);
							if (index == -1) {
								uniqueEdgeLabelsInCluster.add(elabel);
								uniqueEdgeLabelsInCluster_freq.add(1);
							} else {
								int freq = uniqueEdgeLabelsInCluster_freq.get(index) + 1;
								uniqueEdgeLabelsInCluster_freq.set(index, freq);
							}
						}
					}
				}
			}
			// System.out.println("end while....");
			br.close();
			// System.out.println(jGraphTGraphSet.size() + " data graphs loaded");
			// System.out.println("graphIdList size:" + graphIdList.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return graphIdList;
	}
	
	
	public void readGraphSetForGraphIdsInConstraintSet(String filename, ArrayList<Integer> idList,
			ArrayList<Integer> constraintList) {
		// graphIdList = idList;
		graphIdList = new ArrayList<Integer>();
		ArrayList<Integer> workingList = new ArrayList<Integer>();
		String strLine = null;
		File fin = new File(filename);
		BufferedReader br;

		if (constraintList == null || constraintList.size() == 0)
			return;

		for (int i = 0; i < idList.size(); i++) {
			int graphID = idList.get(i);
			if (constraintList.contains(graphID) == true)
				workingList.add(graphID);
		}
		workingList.add(-1);

		try {
			br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fin))));

			int graphid = -1;
			int eNum = 0;

			Graph tmpgraph = null;
			JGraphtGraph tmpJGraphTGraph = new JGraphtGraph();
			boolean currentGraphInWantedList = false;
			graphSet = new ArrayList<Graph>();
			jGraphTGraphSet = new ArrayList<JGraphtGraph>();

			while ((strLine = br.readLine()) != null && workingList.size() > 0) {
				if (strLine.contains("t # ")) {
					int spaceIndex = strLine.indexOf(" ", 4);
					if (spaceIndex != -1) {
						graphid = Integer.parseInt(strLine.substring(4, spaceIndex));
						if (workingList.contains(graphid) == true) {
							currentGraphInWantedList = true;
							int index = workingList.indexOf(graphid);
							workingList.remove(index);
							eNum = 0;
							String[] str = strLine.split("\\s");
							int nodenum = Integer.parseInt(str[3]);
							tmpgraph = new Graph();
							tmpgraph.setGraphid(graphid);
							tmpgraph.setVertexNum(nodenum);
							// store graph data into graph structure for generating closure graphs later
							tmpJGraphTGraph = new JGraphtGraph(graphid);
						} else {
							currentGraphInWantedList = false;
							if (workingList.size() == 1 && workingList.get(0) == -1) {
								workingList.remove(workingList.indexOf(-1));// terminate the search
							}
						}
					}
				} else if (currentGraphInWantedList == true) {
					if (strLine.contains("v")) {
						Vertex node = new Vertex();
						String[] nodeline = strLine.split("\\s");
						int id = Integer.parseInt(nodeline[1]);
						String label = nodeline[2];
						node.setLabel(label);
						node.setID(id);
						tmpgraph.addNode(node);
						// store graph data into graph structure for generating closure graphs later
						simpleVertex n = new simpleVertex(id, label);
						tmpJGraphTGraph.addNode(n);
					} else if (strLine.contains("e")) {
						eNum++;
						String[] strnode = strLine.split("\\s");
						int v1 = Integer.parseInt(strnode[1]);
						int v2 = Integer.parseInt(strnode[2]);
						// Connect v1 with v2; Increase degree of each;
						tmpgraph.addEdge(v1, v2);
						tmpgraph.getNode(v1).incDegree();
						tmpgraph.getNode(v2).incDegree();
						// set v1, v2 to be in In-list of each other;
						tmpgraph.getNode(v1).setIn(v2);
						tmpgraph.getNode(v2).setIn(v1);
						// store graph data into graph structure for generating closure graphs later
						tmpJGraphTGraph.addEdge(v1, v2);
					} else {
						tmpgraph.setEdgeNum(eNum);
						graphSet.add(tmpgraph);
						// store graph data into graph structure for generating closure graphs later
						// tmpJGraphTGraph.print(); //debug purpose
						jGraphTGraphSet.add(tmpJGraphTGraph);
						graphIdList.add(graphid);
						// exportGraphToSVG(tmpgraph, graphid);
					}
				}
			}
			br.close();
			// System.out.println(jGraphTGraphSet.size() + " data graphs loaded");
			// System.out.println("graphIdList size:" + graphIdList.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void exportGraphToSVG(Graph tmpgraph, int graphid) {
		try {
			dotGenerator.createGraph(tmpgraph);
			dotGenerator.formatDotFile(graphid);

			// String cmdGenerateSVG = "cmd /c dot -Tsvg -Kneato -Gepsilon=0.0001
			// patterns/temp_pattern.dot -o " + "patterns/" + Integer.toString(graphid) +
			// ".svg";
			String cmdGenerateSVG = "cmd /c dot -Tjpg  -Kneato -Gepsilon=0.0001 patterns/temp_pattern.dot -o "
					+ "patterns/" + Integer.toString(graphid) + ".jpg";

			try {
				Process procSVG = Runtime.getRuntime().exec(cmdGenerateSVG);
				procSVG.waitFor();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				// Exceptions.printStackTrace(ex);
			}
		} catch (ClassNotFoundException ex) {
			// Exceptions.printStackTrace(ex);
		}
	}

	private void exportGraphToSVG_dataset(String dbName, Graph tmpgraph, int graphid) {
		try {
			dotGenerator.createGraph(tmpgraph, dbName);
			dotGenerator.formatDotFile(graphid);

			// String cmdGenerateSVG = "cmd /c dot -Tsvg -Kneato -Gepsilon=0.0001
			// patterns/temp_pattern.dot -o " + "patterns/" + Integer.toString(graphid) +
			// ".svg";
			String cmdGenerateSVG = "cmd /c dot -Tjpg  -Kneato -Gepsilon=0.0001 patterns/temp_pattern.dot -o "
					+ "patterns/" + Integer.toString(graphid) + ".jpg";

			try {
				Process procSVG = Runtime.getRuntime().exec(cmdGenerateSVG);
				procSVG.waitFor();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			} catch (IOException ex) {
				// Exceptions.printStackTrace(ex);
			}
		} catch (ClassNotFoundException ex) {
			// Exceptions.printStackTrace(ex);
		}
	}
}

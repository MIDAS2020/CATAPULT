/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package infrequentindex;
/**
 *
 * @author cjjin
 */

/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: BuildInfreqIndex.java
 *
 * Abstract: Build the infrequent descriminative fragments into A2I-index
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        June.20,2010
 *
 */
import adjlistgraph.Graph;
import db.DatabaseInfo;
import db.Parameters;
import exactquery.NewUllmanVerify;
import java.io.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;

import frequentindex.Vertex;
import util.RunTime;
import java.sql.*;
import java.util.ArrayList;

public class InfreqIndex {

	private static DatabaseInfo dbinfo = null;

	// Used to compute infIndex
	private static ArrayList<Graph> graphSet = new ArrayList<Graph>();
	private static ArrayList<Graph> edgeSet = new ArrayList<Graph>();// set of all edges in database;

	private static ArrayList<Graph> freqFragSet = new ArrayList<Graph>();
	private static ArrayList<String> freqCamSet = new ArrayList<String>();

	private static ArrayList<Graph> infIndex = new ArrayList<Graph>();

	private static LargestSubgraphs lsg = new LargestSubgraphs();
	private static CamGenerator bc = new CamGenerator();
	private static NewUllmanVerify exactVerifier = new NewUllmanVerify();

	private static RunTime time = new RunTime();
	private static String dir = null;

	// Read or Build
	public static void load(Parameters para)
			throws FileNotFoundException, IOException, SQLException, ClassNotFoundException {
		dir = "data/" + para.getDBName() + "/" + para.getdatasize() + "k/";

		// read the graphs from disk to memory
		readGraphSet(para);

		// The set of all discriminative infrequent fragments;
		String strLine = null;
		File fin = new File(dir + para.getSupportThreshold() + "Iindex" + para.getdatasize() + "k"); // example:
																										// 0.1Iindex40k

		// If A2Iindex exist
		if (fin.exists()) {
			try {
				BufferedReader infReader = new BufferedReader(
						new InputStreamReader(new BufferedInputStream(new FileInputStream(fin))));

				try {
					while ((strLine = infReader.readLine()) != null) {
						// create a new graph
						Graph node = new Graph();

						// set cam code to the graph;
						String[] list = strLine.split("\\s");
						node.setCam(list[0]);

						// read the list of graphs containing this edge;
						for (int j = 2; j < list.length; j++) {
							node.addToIdlist(Integer.parseInt(list[j]));
						}

						infIndex.add(node);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// If A2Iindex doesn't exist
		else {
			readFreqFragSet(para);
			buildInfIndex(para);
		}
		System.out.println("The number of infrequent fragments: " + infIndex.size());

	}

	// read all graphs and edges into "dataset" & "edgeSet";
	public static void readGraphSet(Parameters para) {
		String filename = dir + para.getDBName() + para.getdatasize() + "k"; // example: AIDS40k
		String strLine = null;

		File fin = new File(filename);
		FileInputStream inStream;
		DataInputStream in;
		BufferedReader br;

		if (fin.exists()) {
			try {
				inStream = new FileInputStream(fin);
				in = new DataInputStream(inStream);
				br = new BufferedReader(new InputStreamReader(in));

				// Read file line by line
				try {

					int graphid = -1;
					int edgenum = 0;

					Graph tmpgraph = null;

					while ((strLine = br.readLine()) != null) {

						// beginning line of a graph data;
						if (strLine.contains("t #")) {
							graphid++;

							edgenum = 0;

							String[] str = strLine.split("\\s");
							int nodenum = Integer.parseInt(str[3]);

							tmpgraph = new Graph();
							tmpgraph.setGraphid(graphid);
							tmpgraph.setVertexNum(nodenum);

							// read in a vertex
						} else if (strLine.contains("v")) {
							Vertex node = new Vertex();
							String[] nodeline = strLine.split("\\s");
							node.setLabel(nodeline[2]);
							node.setID(Integer.parseInt(nodeline[1]));
							tmpgraph.addNode(node);

							// read in an edge
						} else if (strLine.contains("e")) {
							edgenum++;
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

							// CAM code processing
							String slabel = tmpgraph.getNode(v1).getLabel();
							String tlabel = tmpgraph.getNode(v2).getLabel();

							String cam = null;
							if (Integer.parseInt(slabel) > Integer.parseInt(tlabel)) {
								cam = "(" + slabel + ")1(" + tlabel + ")";
							} else {
								cam = "(" + tlabel + ")1(" + slabel + ")";
							}

							int flag = isIncluded(cam, edgeSet);

							// If the edge hasn't been seen before;
							if (flag == -1) {
								// Create a new graph conatain only the edge;
								Graph tmpedge = new Graph();
								Vertex node1 = new Vertex();
								node1.incDegree();
								node1.setLabel(slabel);

								Vertex node2 = new Vertex();
								node2.incDegree();
								node2.setLabel(tlabel);

								tmpedge.addNode(node1);
								tmpedge.addNode(node2);
								tmpedge.setVertexNum(2);
								tmpedge.setEdgeNum(1);
								tmpedge.addEdge(0, 1);
								tmpedge.setCam(cam);
								tmpedge.addToIdlist(graphid);

								// add the edge to edgeSet set;
								edgeSet.add(tmpedge);
							} else {
								// add the graph to the list of graph containing the edge;
								edgeSet.get(flag).addToIdlist(graphid);
							}
						} // Empty line => end of a graph;
						else {
							tmpgraph.setEdgeNum(edgenum);
							graphSet.add(tmpgraph);
						}
					}
					// Close the input stream
					in.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	// -->freqFragSet, freqCamSet;
	public static void readFreqFragSet(Parameters para) throws FileNotFoundException, IOException {
		long time1 = System.currentTimeMillis();

		String dir2 = para.getSupportThreshold() + para.getDBName() + para.getdatasize() + "k";// Example: 0.1AIDS40k
		String strLine = null;
		File fin = new File(dir + dir2);

		if (fin.exists()) {
			try {

				BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fin));
				BufferedReader br = new BufferedReader(new InputStreamReader(bin));

				try {
					int graphid = -1; // 0-based
					int vn = 0;
					int en = 0;

					// Read the list of frequent graphs;
					while ((strLine = br.readLine()).contains("idlist") == false) {
						if (strLine.contains("#")) {
							graphid++;

							// read the number of vertex;
							strLine = br.readLine();
							vn = Integer.parseInt(strLine);
							Graph tmpgraph = new Graph();
							tmpgraph.setGraphid(graphid);

							// read vertex labels;
							for (int i = 0; i < vn; i++) {
								strLine = br.readLine();
								Vertex node = new Vertex();
								node.setID(i);
								node.setLabel(strLine);
								tmpgraph.addNode(node);
							}

							// read the number of edges;
							strLine = br.readLine();
							en = Integer.parseInt(strLine);

							// read in the pairs of edge Ids;
							for (int i = 0; i < en; i++) {
								strLine = br.readLine();
								String[] strnode = strLine.split("\\s");
								int e1 = Integer.parseInt(strnode[0]);
								int e2 = Integer.parseInt(strnode[1]);
								tmpgraph.addEdge(e1, e2);
								tmpgraph.getNode(e1).incDegree();
								tmpgraph.getNode(e2).incDegree();
							}

							tmpgraph.setVertexNum(vn);
							tmpgraph.setEdgeNum(en);

							String cam = bc.buildCam(tmpgraph);
							tmpgraph.setCam(cam);
							freqFragSet.add(tmpgraph);
							freqCamSet.add(cam);
						}
					}

					// Read the list of graphs containing frequent graphs;
					if (strLine.contains("idlist")) {
						int nodenum = 0;
						while ((strLine = br.readLine()) != null) {
							String[] idset = strLine.split("\\s");

							for (int i = 0; i < idset.length; i++) {
								freqFragSet.get(nodenum).addToIdlist(Integer.parseInt(idset[i]));
							}
							nodenum++;
						}
					}

					br.close();

				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		long time2 = System.currentTimeMillis();
		System.out.println("FGSs reading time(s): " + (time2 - time1) / 1000);

	}

	public static void buildInfIndex(Parameters para)
			throws SQLException, ClassNotFoundException, FileNotFoundException, IOException {

		float a = para.getSupportThreshold();
		String output = dir + a + "Iindex" + para.getdatasize() + "k"; // example: 0.1Iindex40k
		PrintStream infWriter;
		File fout = new File(output);

		ArrayList<Graph> freqEdges = new ArrayList<Graph>(); // the frequent edge set
		HashSet<String> freqVertexLabelSet = new HashSet<String>();

		try {
			infWriter = new PrintStream(new BufferedOutputStream(new FileOutputStream(fout)));

			// I. Infrequent edges
			for (int i = 0; i < edgeSet.size(); i++) {

				// Get the list of graphs containing this edge
				HashSet<Integer> containingIdList = edgeSet.get(i).getIdList();

				// Frequent edge
				if (containingIdList.size() >= a * graphSet.size()) {
					freqEdges.add(edgeSet.get(i));

					freqVertexLabelSet.add(edgeSet.get(i).getNode(0).getLabel());
					freqVertexLabelSet.add(edgeSet.get(i).getNode(1).getLabel());
				}
				// Infrequent edge
				else {
					infIndex.add(edgeSet.get(i));
				}
			}

			// Print infrequent edges to "0.1Iindex40k"
			for (int i = 0; i < infIndex.size(); i++) {
				Graph infedge = infIndex.get(i);
				infWriter.print(infedge.getCam() + " :");

				Iterator itr = infedge.getIdList().iterator();
				while (itr.hasNext()) {
					Integer id = (Integer) itr.next();
					infWriter.print(" " + id);
				}
				infWriter.println();
			}

			// II. Find infrequent fragments with size >= 2
			// Classify frequent fragments by graph size
			ArrayList<ArrayList<Integer>> fragmentSet = new ArrayList<ArrayList<Integer>>(12);
			for (int i = 0; i < 12; i++) {
				ArrayList<Integer> level = new ArrayList<Integer>();
				fragmentSet.add(level);
			}
			classifyFreqIndex(fragmentSet);

			time.time[RunTime.RUN_START] = System.currentTimeMillis();

			// Generate discriminative infrequent fragments from frequent fragments
			for (int i = 0; i < freqFragSet.size(); i++) {
				// Only examine the MF-Index
				if (freqFragSet.get(i).getVertexNum() > 8) {
					break;
				}

				Graph frequentGraph = freqFragSet.get(i);
				HashSet<Integer> containingIdSet = freqFragSet.get(i).getIdList();

				for (int j = 0; j < frequentGraph.getVertexNum(); j++) { // Each vertex of freqentGraph
					String oldLabel = frequentGraph.getNode(j).getLabel();

					for (Iterator itr = freqVertexLabelSet.iterator(); itr.hasNext();) { // Each vertex label
						String newLabel = (String) itr.next(); // The new added label

						// Compute CAM
						String newEdgeCam = null;
						if (Integer.parseInt(oldLabel) > Integer.parseInt(newLabel)) {
							newEdgeCam = "(" + oldLabel + ")1(" + newLabel + ")";
						} else {
							newEdgeCam = "(" + newLabel + ")1(" + oldLabel + ")";
						}

						// Check if new edge is frequent one
						int z = isIncluded(newEdgeCam, freqEdges);
						if (z != -1) {

							int oldVertexNum = frequentGraph.getVertexNum();
							Graph newGraph = new Graph();

							// Copy all the nodes from frequentGraph;
							for (int nodeid = 0; nodeid < frequentGraph.getVertexNum(); nodeid++) {
								Vertex tmpVertex = new Vertex();
								tmpVertex.setDegree(frequentGraph.getNode(nodeid).getDegree());
								tmpVertex.setLabel(frequentGraph.getNode(nodeid).getLabel());
								tmpVertex.setID(nodeid);

								newGraph.addNode(tmpVertex);
							}

							// Add new node
							Vertex newnode = new Vertex();
							newnode.setLabel(newLabel);
							newnode.setID(frequentGraph.getVertexNum());

							newGraph.addNode(newnode);

							// Copy all the edges from the frequentGraph;
							for (int fgi = 0; fgi < frequentGraph.getVertexNum(); fgi++) {
								for (int fgj = 0; fgj < frequentGraph.getVertexNum(); fgj++) {
									if (frequentGraph.getEdgeLabel(fgi, fgj) == 1) {
										newGraph.addEdge(fgi, fgj);
										newGraph.getNode(fgi).setIn(fgj);
										newGraph.getNode(fgj).setIn(fgi);
									}
								}
							}

							// Add new edges;
							newGraph.addEdge(frequentGraph.getVertexNum(), j);
							newGraph.getNode(frequentGraph.getVertexNum()).setIn(j);
							newGraph.getNode(j).setIn(frequentGraph.getVertexNum());
							newGraph.getNode(j).incDegree();
							newGraph.getNode(frequentGraph.getVertexNum()).incDegree();

							// Set the number of vertex in new graph;
							newGraph.setVertexNum(oldVertexNum + 1);

							// Set cam code
							String newcam = bc.buildCam(newGraph);
							newGraph.setCam(newcam);

							if (freqCamSet.contains(newcam) == false && // not be frequent
									isIncluded(newcam, infIndex) == -1) { // not exist in infrequent

								ArrayList<String> subCamSet = lsg.getLargeSubgraphSet(newGraph);

								// If "new graph" is discriminative infrequent fragment
								if (freqCamSet.containsAll(subCamSet)) {
									ArrayList<Integer> containingIdList = new ArrayList<Integer>();

									for (Iterator itr1 = containingIdSet.iterator(); itr1.hasNext();) {
										int id = (Integer) itr1.next();

										// Check if newgraph is children of id-th graph in dataset;
										if (exactVerifier.verify(newGraph, graphSet.get(id))) {
											containingIdList.add(id);
										}
									}

									if (containingIdList.size() > 0 && containingIdList.size() < a * graphSet.size()) {
										infWriter.print(newGraph.getCam() + " :");

										for (int t = 0; t < containingIdList.size(); t++) {
											infWriter.print(" " + containingIdList.get(t));
											newGraph.addToIdlist(containingIdList.get(t));
										}

										infWriter.println();
										infIndex.add(newGraph);
									}

								} // end sc
							}
						} // end z
					} // end label
				} // end j

			}

			time.time[RunTime.RUN_END] = System.currentTimeMillis();
			time.time[RunTime.USED] = time.time[RunTime.RUN_END] - time.time[RunTime.RUN_START];

			infWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Classify the frequent fragments by their sizes
	public static void classifyFreqIndex(ArrayList<ArrayList<Integer>> fragments) {
		int size = 2;
		for (int i = 0; i < freqFragSet.size(); i++) {
			if (freqFragSet.get(i).getVertexNum() > size) {
				size++;
			}

			fragments.get(size).add(freqFragSet.get(i).getGraphID());
		}
	}

	public static int isIncluded(String strCAM, ArrayList<Graph> CAMSet) {
		for (int i = 0; i < CAMSet.size(); i++) {
			String cam = CAMSet.get(i).getCam();
			if (strCAM.equals(cam)) {
				return i;
			}
		}
		return -1; // not found
	}

	/*---------------------------Get & Set-------------------------------------*/
	public ArrayList<Graph> getInfIndex() {
		return infIndex;
	}

	public ArrayList<Graph> getFreqGraphs() {
		return freqFragSet;
	}

	public ArrayList<Graph> getDataGraphs() {
		return graphSet;
	}

	public void setDBcon(DatabaseInfo db) {
		dbinfo = db;
	}
}

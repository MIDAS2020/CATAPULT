/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package frequentindex;

/**
 *
 * @author cjjin
 */

/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: BuildFreqIndex.java
 *
 * Abstract: Build the frequent fragments into the A2F-index
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Mar.5, 2010
 *
 */
import adjlistgraph.Graph;
import db.Parameters;
import exactquery.NewUllmanVerify;
import infrequentindex.InfreqIndex;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import util.RunTime;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class FreqIndex {

	private static FreqIndexGraph mfIndex = null; // graph of mf-frequent graphs;
	private static ArrayList<Graph> dfIndex = new ArrayList<Graph>(); // df-frequent graphs;

	private static ArrayList<Graph> freFragSet = new ArrayList<Graph>();
	private static int mfEndIdx; // fsize graph of freFragSet will belong to MF-index;

	private static NewUllmanVerify uv = new NewUllmanVerify();

	private static RunTime time = new RunTime();

	// verify if m-th in freFragSet is subgraph of n-th in freFragSet;
	public static boolean isSubGraph(int m, int n) {
		return uv.verify(freFragSet.get(m), freFragSet.get(n));
	}

	public static long build(InfreqIndex infIndex, Parameters para) throws SQLException, ClassNotFoundException {

		int mfSize = para.getMFThreshold();
		String dir = "data/" + para.getDBName() + "/" + para.getdatasize() + "k/" + para.getSupportThreshold()
				+ para.getDBName() + para.getdatasize() + "k" + mfSize;

		String output1 = dir + "DFIndex"; // Example: 0.1AIDS40k8DFIndex
		String output2 = dir + "MFIndex"; // Example: 0.1AIDS40k8MFIndex

		PrintStream dfIndexPrinter = null, mfIndexPrinter = null;
		File dfFile = new File(output1);
		File mfFile = new File(output2);

		try {
			time.time[RunTime.RUN_START] = System.currentTimeMillis();

			// Read or Construct the A2I-index
			InfreqIndex.load(para);

			// If 0.1AIDS40k8MFIndex exists, read the index from the file
			if (mfFile.exists()) {
				readMFIndex(mfFile);
			} else {
				mfIndexPrinter = new PrintStream(new BufferedOutputStream(new FileOutputStream(mfFile)));
				dfIndexPrinter = new PrintStream(new BufferedOutputStream(new FileOutputStream(dfFile)));

				// Read in the list of the frequent graphs;
				if (infIndex.getFreqGraphs().isEmpty()) {
					InfreqIndex.readFreqFragSet(para);
				}
				freFragSet = infIndex.getFreqGraphs();

				// Size of the MF-Index
				for (mfEndIdx = 0; mfEndIdx < freFragSet.size(); mfEndIdx++) {
					if (freFragSet.get(mfEndIdx).getEdgeNum() > mfSize) {
						break;
					}
				}
				mfIndex = new FreqIndexGraph(mfEndIdx);
				mfIndexPrinter.println(mfEndIdx);

				HashSet<Integer> clusterHeadIdSet = new HashSet<Integer>();

				// For each frequent fragment
				for (int i = 0; i < freFragSet.size(); i++) {
					Graph freqFragi = freFragSet.get(i);

					// I. If node i is not the leave of the MF-Index;
					if (freqFragi.getEdgeNum() != mfSize) {
						// Find children of freqFragi;
						for (int j = i + 1; j < freFragSet.size(); j++) {
							Graph freqFragj = freFragSet.get(j);

							// If freqFragj satisfy the conditions to be child of freqFragi;
							if ((freqFragi.getEdgeNum() == freqFragj.getEdgeNum() - 1) && isSubGraph(i, j)) {

								// Set the hierarchical relationship
								if (j < mfEndIdx) {
									mfIndex.addEdge(i, j);
								}
								freqFragi.addToChildren(j);

								// Only store delId in freqFragi;
								freqFragi.getIdList().removeAll(freqFragj.getIdList());
							}
						}

						if (freqFragi.getEdgeNum() < mfSize) {
							// Print cam code;
							mfIndexPrinter.print(freqFragi.getCam() + " :");

							// Print the list of graphs containing this fragment;
							for (Iterator itr = freqFragi.getIdList().iterator(); itr.hasNext();) {
								int id = (Integer) itr.next();
								mfIndexPrinter.print(" " + id);
							}
							mfIndexPrinter.println();

							// Add the node to mfIndex;
							mfIndex.addnode(freqFragi);
						} else if (freqFragi.getEdgeNum() > mfSize && dfIndexPrinter != null) {

							dfIndexPrinter.print(" " + i + ":");
							for (Iterator itr = freqFragi.getIdList().iterator(); itr.hasNext();) {
								int id = (Integer) itr.next();
								dfIndexPrinter.print(" " + id);
							}
							dfIndexPrinter.println();

							// Add the node to dfIndex;
							dfIndex.add(freqFragi);
						}
						// II. freqFragi is a leave of the MF-Index;
					} else {
						// Print freqFragi's cam code & freqFragi's containing list;
						mfIndexPrinter.print(freqFragi.getCam() + " :");
						for (Iterator itr = freqFragi.getIdList().iterator(); itr.hasNext();) {
							int id = (Integer) itr.next();
							mfIndexPrinter.print(" " + id);
						}
						mfIndexPrinter.println();

						// Find children of freqFragi;
						for (int j = i + 1; j < freFragSet.size(); j++) {
							Graph fnodej = freFragSet.get(j);
							if ((freqFragi.getEdgeNum() == fnodej.getEdgeNum() - 1) && isSubGraph(i, j)) {

								freqFragi.addToClusters(j);
								clusterHeadIdSet.add(j);
								mfIndex.setClusterHeads(j, fnodej.getCam());
							}
						}
						mfIndex.addnode(freqFragi);
					}
				}

				// Print the MF-Index
				for (int m = 0; m < mfEndIdx; m++) {
					// Print edges of mf-index;
					for (int n = m; n < mfEndIdx; n++) {
						if (mfIndex.hasEdge(m, n) == 1) {
							mfIndexPrinter.println(m + " " + n);
						}
					}

					// Print cluster information
					if (mfIndex.getNode(m).getEdgeNum() == mfSize && !mfIndex.getNode(m).getClusters().isEmpty()) {

						mfIndexPrinter.print(m + " ;");
						for (int t = 0; t < mfIndex.getNode(m).getClusters().size(); t++) {
							mfIndexPrinter.print(" " + mfIndex.getNode(m).getClusters().get(t));
						}
						mfIndexPrinter.println();
					}
				}

				// Output the cluster set
				Hashtable<Integer, String> clusterId_CamSet = new Hashtable<Integer, String>();
				clusterId_CamSet = mfIndex.getClusterHeads();

				for (Iterator itr = clusterId_CamSet.keySet().iterator(); itr.hasNext();) {
					int cid = (Integer) itr.next();
					mfIndexPrinter.println(cid + " | " + clusterId_CamSet.get(cid));
				}

				if (dfIndexPrinter != null) {

					// List of the heads of clusters in order;
					List<Integer> clusterHeadIdSet2 = new ArrayList<Integer>();
					clusterHeadIdSet2.addAll(clusterHeadIdSet);
					Collections.sort(clusterHeadIdSet2);

					for (Iterator itr = clusterHeadIdSet2.iterator(); itr.hasNext();) { // each cluster heads
						int id = (Integer) itr.next();
						Cluster cluster = new Cluster();
						cluster.addNode(id);

						buildCluster(cluster, id);

						// Output
						dfIndexPrinter.println("Cluster " + id);
						dfIndexPrinter.println(cluster.getNodeSet().size());

						for (int i = 0; i < cluster.getNodeSet().size(); i++) {
							int gid = cluster.getNodeSet().elementAt(i);
							dfIndexPrinter.println(freFragSet.get(gid).getCam() + ":" + gid);
						}

						for (int i = 0; i < cluster.getEdgeSet().size(); i++) {
							dfIndexPrinter.println(cluster.getEdgeSet().elementAt(i));

						}

						dfIndexPrinter.println("End");

					}

					clusterHeadIdSet.clear();
					clusterHeadIdSet2.clear();
					dfIndexPrinter.close();
				}

				// clear data before exit;
				freFragSet.clear();

				// Close the input stream
				mfIndexPrinter.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		time.time[RunTime.RUN_END] = System.currentTimeMillis();
		time.time[RunTime.USED] = time.time[RunTime.RUN_END] - time.time[RunTime.RUN_START];
		System.out.println("Indices Building Time(s): " + time.time[RunTime.USED] / 1000);
		return time.time[RunTime.USED];
	}

	public static FreqIndexGraph readMFIndex(File fin) throws FileNotFoundException, IOException {
		String strLine = null;
		BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fin));
		BufferedReader mfIndexReader = new BufferedReader(new InputStreamReader(bin));

		int nodenum = -1;
		String firstLine = mfIndexReader.readLine();
		mfIndex = new FreqIndexGraph(Integer.parseInt(firstLine));

		while ((strLine = mfIndexReader.readLine()) != null) {
			// Line for graph's cam & its containing list;
			if (strLine.contains(":")) {
				nodenum++;

				Graph node = new Graph();// set default size 0

				String[] list = strLine.split("\\s");

				node.setCam(list[0]);
				node.setGraphid(nodenum);

				for (int j = 2; j < list.length; j++) {
					node.addToIdlist(Integer.parseInt(list[j]));
				}
				mfIndex.addnode(node);
			}

			// Line for edge in MF-Index;
			else if (!strLine.contains(";") && !strLine.contains("|")) {
				String[] pair = strLine.split("\\s");
				int src = Integer.parseInt(pair[0]);
				int trg = Integer.parseInt(pair[1]);
				mfIndex.addEdge(src, trg);
				mfIndex.getNode(src).addToChildren(trg);// set children
			}

			// Lines that contain links from leave nodes of MF-Index to the heads in
			// DF-Index;
			else if (strLine.contains(";")) {
				String[] list = strLine.split("\\s");
				for (int j = 2; j < list.length; j++) {
					mfIndex.getNode(Integer.parseInt(list[0])).addToClusters(Integer.parseInt(list[j]));
				}
			}

			// Lines that store cluster head's ids & their camcode;
			else if (strLine.contains("|")) {
				String[] list = strLine.split("\\s");
				for (int j = 2; j < list.length; j++) {
					mfIndex.setClusterHeads(Integer.parseInt(list[0]), list[2]);
				}
			}
		}
		// Close the input stream
		mfIndexReader.close();

		System.out.println("MFIndex size = " + mfIndex.getSize());
		return mfIndex;
	}

	public static void buildCluster(Cluster cluster, int id) {

		Graph dfFrag = dfIndex.get(id - mfEndIdx);

		for (int i = 0; i < dfFrag.getChildren().size(); i++) {
			int childId = dfFrag.getChildren().get(i);

			// Add vertex;
			if (!cluster.getNodeSet().contains(childId)) {
				cluster.addNode(childId);
			}

			int jid = cluster.getNodeSet().indexOf(childId);
			int iid = cluster.getNodeSet().indexOf(id);

			// Add edge;
			if (!cluster.getEdgeSet().contains(iid + " " + jid)) {
				cluster.addEdge(iid, jid);
			}

			buildCluster(cluster, childId);
		}
	}

	public FreqIndexGraph getMFGraph() {
		return mfIndex;
	}

	public ArrayList<Graph> getDFCluster() {
		return dfIndex;
	}
}

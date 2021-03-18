/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: ReadCluster.java
 *
 * Abstract:
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Jul.25,2009
 *
 */
package exactquery;

/**
 *
 * @author cjjin
 */
import adjlistgraph.Graph;
import db.Parameters;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import frequentindex.FreqIndexGraph;
import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class DFClusterReader {

	// only sotre graph hierarchy, not the containingIdList;
	private static FreqIndexGraph cluster = null;
	private static String filename = "SIndex.txt";

	public DFClusterReader(Parameters para) {
		filename = "data/" + para.getDBName() + "/" + para.getdatasize() + "k/" + para.getSupportThreshold()
				+ para.getDBName() + para.getdatasize() + "k" + para.getMFThreshold() + "DFIndex";

	}

	// Only load the graph hierarchy, not the containingIdList;
	public static FreqIndexGraph readDFCluster(int pid) {

		String strLine = null;

		File fin = new File(filename);

		if (fin.exists()) {
			try {

				BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fin));
				BufferedReader br = new BufferedReader(new InputStreamReader(bin));
				// Read file line by line
				try {

					int vn = 0;
					int graphnum = -1;

					while ((strLine = br.readLine()) != null) {
						// Print the content on the console
						if (strLine.equals("Cluster " + pid)) {
							graphnum++;
							strLine = br.readLine();
							vn = Integer.parseInt(strLine);
							cluster = new FreqIndexGraph(vn);

							for (int i = 0; i < vn; i++) {
								strLine = br.readLine();
								int index = strLine.indexOf(":");

								Graph node = new Graph();
								node.setCam(strLine.substring(0, index));
								int oldid = Integer.parseInt(strLine.substring(index + 1));
								node.setGraphid(oldid);
								cluster.addnode(node);
							}

							while (!(strLine = br.readLine()).equals("End")) {
								String[] pair = strLine.split("\\s");
								int src = Integer.parseInt(pair[0]);
								int trg = Integer.parseInt(pair[1]);
								cluster.addEdge(src, trg);
								cluster.getNode(src).addToChildren(trg);
							}
						}
					}
					// Close the input stream
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return cluster;
	}

	public static void getContainingIds(ArrayList<Integer> oldlist, HashSet<Integer> fcandlist) {
		String strLine = null;
		FileInputStream inStream;
		DataInputStream in;
		BufferedReader br;
		File fin = new File(filename);

		if (fin.exists()) {
			try {

				inStream = new FileInputStream(fin);
				in = new DataInputStream(inStream);
				br = new BufferedReader(new InputStreamReader(in));
				// Read file line by line
				try {
					for (int i = 0; i < oldlist.size(); i++) {
						while ((strLine = br.readLine()) != null) {
							if (strLine.contains(" " + oldlist.get(i) + ": ")) {
								int index = strLine.indexOf(":");
								String[] list = strLine.substring(index + 2).split("\\s");
								for (int j = 0; j < list.length; j++) {
									fcandlist.add(Integer.parseInt(list[j]));
								}
								break;
							}
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
}

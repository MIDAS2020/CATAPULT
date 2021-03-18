/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package result;
/*
 * Copyright 2010, Center for Advanced Information Systems, Nanyang Technological University
 *
 * File name: DotGenerator.java
 *
 * Abstract: Create the DOT format file for the selected result graph
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Dec.08,2009
 *
 */

/**
 *
 * @author cjjin
 */
import adjlistgraph.Graph;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.sql.*;
import db.DatabaseInfo;
import frequentindex.Vertex;
import exactquery.MatchedGraph;

public class DotGenerator {

	private static Graph g_display;
	private String dbName;

	public void createGraph(DatabaseInfo dbinfo, Graph graph) throws ClassNotFoundException, SQLException {
		int nodenum = graph.getVertexNum();
		g_display = new Graph();
		g_display.setVertexNum(nodenum);
		dbName = dbinfo.getDbName();

		// Get back the real label for each nodes;
		String reallabels[] = dbinfo.getLabels();
		for (int i = 0; i < nodenum; i++) {
			String l = graph.getNode(i).getLabel();
			String label = reallabels[Integer.parseInt(l)] + "_" + i;
			Vertex node = new Vertex();
			node.setLabel(label);
			g_display.addNode(node);
		}

		// Set edges;
		for (int i = 0; i < nodenum; i++) {
			for (int j = 0; j < nodenum; j++) {
				if (graph.getEdgeLabel(i, j) == 1) {
					g_display.addEdge(i, j);
				}
			}
		}

		g_display.setEdgeNum(graph.getEdgeNum());
		g_display.setGraphid(graph.getGraphID());

	}

	// formulate the dot file
	public File formatDotFile(MatchedGraph mg) {
		String output = "graph.dot";
		FileOutputStream outStream;
		PrintStream p;
		File fout = new File(output);

		try {
			outStream = new FileOutputStream(fout, false);
			// Connect print stream to the output stream
			p = new PrintStream(outStream);

			p.println("graph \"result\" {");
			p.println("graph [fontname = \"Helvetica-Oblique\", fontsize = 20,label = \"\\n\\n\\nGraph "
					+ g_display.getGraphID() + "\",size = \"4,4\" ];");
			p.println("node [label = \"\\N\",shape = doublecircle, sides = 4, color = cadetblue1,"
					+ "style = filled,fontname = \"Helvetica-Outline\" ];");

			for (int i = 0; i < g_display.getVertexNum(); i++) {
				if (mg.getNodeset().contains(i)) {
					int testIndex = g_display.getNode(i).getLabel().indexOf('_');
					String todisplay = g_display.getNode(i).getLabel().substring(0, testIndex);
					p.println(g_display.getNode(i).getLabel() + " [color= orange label = " + todisplay + "];");
				} else {
					int testIndex = g_display.getNode(i).getLabel().indexOf('_');
					String todisplay = g_display.getNode(i).getLabel().substring(0, testIndex);
					p.println(g_display.getNode(i).getLabel() + " [label = " + todisplay + "];");
				}
			}

			for (int i = 0; i < g_display.getVertexNum(); i++) {
				for (int j = i + 1; j < g_display.getVertexNum(); j++) {
					if (g_display.getEdgeLabel(i, j) == 1) {

						p.println(g_display.getNode(i).getLabel() + " -- " + g_display.getNode(j).getLabel() + ";");
					}
				}
			}

			p.println("}");
			p.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fout;
	}

	// Ngoc Anh - formulate pattern dot file
	public File formatDotFile(int patternId) {
		String output = "patterns/temp_pattern.dot";
		FileOutputStream outStream;
		PrintStream p;
		File fout = new File(output);

		try {
			outStream = new FileOutputStream(fout, false);
			// Connect print stream to the output stream
			p = new PrintStream(outStream);

			p.println("graph \"result\" {");
			// p.println("graph [fontsize = 30, ratio = 0.835, dpi = 100, size = \"2,2\" ];"
			// );
			// p.println("node [label = \"\\N\",shape = doublecircle, sides = 4, color =
			// skyblue,"
			// + "style = filled ];");
			p.println("graph [dpi = 100, ratio=0.835, size = \"2,2\" ];");
			p.println("node [fontsize=30, shape = plaintext, sides = 4, style = bold];");
			for (int i = 0; i < g_display.getVertexNum(); i++) {
				String labelname = g_display.getNode(i).getLabel();
				String[] parts = labelname.split("_");
				String part1 = parts[0];
				// String Vertexlabels1 = " [shape=doublecircle,label=\" " + parts[0] + "\"]";
				String Vertexlabels1 = " [shape=circle,label=\"" + parts[0] + "\"]";
				p.println(g_display.getNode(i).getLabel() + Vertexlabels1 + ";");
			}

			for (int i = 0; i < g_display.getVertexNum(); i++) {
				for (int j = i + 1; j < g_display.getVertexNum(); j++) {
					if (g_display.getEdgeLabel(i, j) == 1) {
						p.println(g_display.getNode(i).getLabel() + " -- " + g_display.getNode(j).getLabel() + ";");
					}
				}
			}

			p.println("}");
			p.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fout;
	}

	// Huey Eng - formulate pattern dot file with specific output file name
	public File formatDotFileWithFilename(int patternId, String opFileName) {
		String output = "patterns/" + opFileName + "_" + patternId + ".dot";
		FileOutputStream outStream;
		PrintStream p;
		File fout = new File(output);

		try {
			outStream = new FileOutputStream(fout, false);
			// Connect print stream to the output stream
			p = new PrintStream(outStream);

			p.println("graph \"result\" {");
			// p.println("graph [fontsize = 30, ratio = 0.835, dpi = 100, size = \"3,3\" ];"
			// );
			// p.println("node [label = \"\\N\",shape = doublecircle, sides = 4, color =
			// skyblue,"
			// + "style = filled ];");
			p.println("graph [fontsize = 30, ratio = 0.835, dpi = 100, size = \"2,2\" ];");
			p.println("node [label = \"\\N\",shape = circle, sides = 2, color = skyblue," + "style = filled ];");
			for (int i = 0; i < g_display.getVertexNum(); i++) {
				String labelname = g_display.getNode(i).getLabel();
				String[] parts = labelname.split("_");
				String part1 = parts[0];
				// String Vertexlabels1 = " [shape=doublecircle,label=\" " + parts[0] + "\"]";
				String Vertexlabels1 = " [shape=circle,label=\" " + parts[0] + "\"]";
				p.println(g_display.getNode(i).getLabel() + Vertexlabels1 + ";");
			}

			for (int i = 0; i < g_display.getVertexNum(); i++) {
				for (int j = i + 1; j < g_display.getVertexNum(); j++) {
					if (g_display.getEdgeLabel(i, j) == 1) {
						p.println(g_display.getNode(i).getLabel() + " -- " + g_display.getNode(j).getLabel() + ";");
					}
				}
			}

			p.println("}");
			p.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fout;
	}

	// Andy - simpler version of createGraph
	public void createGraph(Graph graph) throws ClassNotFoundException {
		int nodenum = graph.getVertexNum();
		g_display = new Graph();
		g_display.setVertexNum(nodenum);

		// Get back the real label for each nodes;
		for (int i = 0; i < nodenum; i++) {
			String l = graph.getNode(i).getLabel();
			//System.out.println("[DotGenerator] l=" + l);
			String label;
			label = DatabaseInfo.dblabels[Integer.parseInt(l)] + "_" + i;
			Vertex node = new Vertex();
			node.setLabel(label);
			g_display.addNode(node);
		}

		// Set edges;
		for (int i = 0; i < nodenum; i++) {
			for (int j = 0; j < nodenum; j++) {
				if (graph.getEdgeLabel(i, j) == 1) {
					g_display.addEdge(i, j);
				}
			}
		}

		g_display.setEdgeNum(graph.getEdgeNum());
		g_display.setGraphid(graph.getGraphID());

	}

	// Huey Eng - simpler version of createGraph based on database
	public void createGraph(Graph graph, String database) throws ClassNotFoundException {
		int nodenum = graph.getVertexNum();
		g_display = new Graph();
		g_display.setVertexNum(nodenum);

		// Get back the real label for each nodes;
		for (int i = 0; i < nodenum; i++) {
			String l = graph.getNode(i).getLabel();
			System.out.println("[DotGenerator] l="+l);
			String label;
			if (database.compareTo("emolecul") == 0)
				label = DatabaseInfo.dbLabels_emolecul[Integer.parseInt(l)] + "_" + i;
			else if (database.compareTo("pubchem") == 0)
				label = DatabaseInfo.dbLabels_pubchem[Integer.parseInt(l)] + "_" + i;
			else
				label = DatabaseInfo.dblabels[Integer.parseInt(l)] + "_" + i;
			Vertex node = new Vertex();
			node.setLabel(label);
			g_display.addNode(node);
		}

		// Set edges;
		for (int i = 0; i < nodenum; i++) {
			for (int j = 0; j < nodenum; j++) {
				//System.out.println("i=" + i + " j=" + j);
				//System.out.println("graph.getEdgeLabel(i, j)=" + graph.getEdgeLabel(i, j));
				if (graph.getEdgeLabel(i, j) == 1) {
					g_display.addEdge(i, j);
				}
			}
		}

		g_display.setEdgeNum(graph.getEdgeNum());
		g_display.setGraphid(graph.getGraphID());

	}
}

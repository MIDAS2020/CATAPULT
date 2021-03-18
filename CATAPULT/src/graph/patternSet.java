/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import adjlistgraph.Graph;
import frequentindex.Vertex;
import result.DotGenerator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
//import org.openide.util.Exceptions;

public class patternSet {
	private ArrayList<ArrayList<closureEdge>> patternList_edge;
	private ArrayList<ArrayList<closureVertex>> patternList_vertex;
	private ArrayList<ArrayList<String>> patternList_string;
	private ArrayList<Integer> fullyCoveredGraphIDList; // list of graph id that are fully covered by at least one
														// pattern in the set
	private ArrayList<Integer> fullyUncoveredGraphIDList; // list of graph id that are not fully covered by any pattern
															// in the set
	private ArrayList<Integer> orphanGraphIDList; // list of graph id that will be ignored as they for some reason
													// cannot be used to generate pattern
	private ArrayList<Integer> patternSize;
	private ArrayList<Integer> patternSize_freq;
	private ArrayList<Integer> skipClusterIndex;
	private String thumbnailFolderPath = "patterns\\thumbnails\\";

	public patternSet() {
		patternList_edge = new ArrayList<ArrayList<closureEdge>>();
		patternList_vertex = new ArrayList<ArrayList<closureVertex>>();
		patternList_string = new ArrayList<ArrayList<String>>();
		fullyCoveredGraphIDList = new ArrayList<Integer>();
		fullyUncoveredGraphIDList = new ArrayList<Integer>();
		orphanGraphIDList = new ArrayList<Integer>();
		patternSize = new ArrayList<Integer>();
		patternSize_freq = new ArrayList<Integer>();
		skipClusterIndex = new ArrayList<Integer>();
	}

	public void addToSkipClusterIndex(int clusterI) {
		if (skipClusterIndex.contains(clusterI) == false)
			skipClusterIndex.add(clusterI);
	}

	public ArrayList<Integer> getSkipClusterIndex() {
		return skipClusterIndex;
	}

	public boolean isInSkipClusterIndex(int index) {
		return skipClusterIndex.contains(index);
	}

	public void promoteToOrphanList(ArrayList<Integer> graphID) {
		fullyUncoveredGraphIDList.removeAll(graphID);
		for (int i = 0; i < graphID.size(); i++) {
			if (orphanGraphIDList.contains(graphID.get(i)) == false)
				orphanGraphIDList.add(graphID.get(i));
		}
	}

	public int getNumOfPatternOfThisSize(int pSize) {
		int pSize_index = patternSize.indexOf(pSize);
		if (pSize_index == -1)
			return 0;
		else
			return patternSize_freq.get(pSize_index);
	}

	public ArrayList<Integer> getFullyCoveredGraphIDList() {
		return fullyCoveredGraphIDList;
	}

	public ArrayList<Integer> getFullyUncoveredGraphIDList() {
		return fullyUncoveredGraphIDList;
	}

	public void initFullyUncoveredGraphIDList(ArrayList<Integer> graphIDList) {
		fullyUncoveredGraphIDList = new ArrayList<Integer>();
		for (int i = 0; i < graphIDList.size(); i++)
			fullyUncoveredGraphIDList.add(graphIDList.get(i));
	}

	public int numPattern() {
		return patternList_edge.size();
	}

	public ArrayList<closureEdge> getPattern_edge(int i) {
		if (i >= 0 && i < numPattern())
			return patternList_edge.get(i);
		return null;
	}

	public ArrayList<closureVertex> getPattern_vertex(int i) {
		if (i >= 0 && i < numPattern())
			return patternList_vertex.get(i);
		return null;
	}

	private boolean patternSetHasNewPattern(ArrayList<String> newPattern_string) {
		for (int i = 0; i < patternList_string.size(); i++) {
			ArrayList<String> currPattern_string = patternList_string.get(i);
			ArrayList<String> tmpString = new ArrayList<String>();
			int tmpString_size;
			boolean CONTINUE_INNERLOOP = true;
			for (int j = 0; j < currPattern_string.size(); j++)
				tmpString.add(currPattern_string.get(j));
			tmpString_size = tmpString.size();
			for (int j = 0; j < newPattern_string.size() && CONTINUE_INNERLOOP; j++) {
				int index = tmpString.indexOf(newPattern_string.get(j));
				if (index == -1)
					CONTINUE_INNERLOOP = false;
				else
					tmpString.remove(index);
			}
			if (tmpString_size == newPattern_string.size() && tmpString.size() == 0)// this is an exact match
			{
				System.out.println(
						"<<<<< patternSetHasNewPattern >>>>> newPattern exists!! " + newPattern_string.toString());
				return true;
			}
		}
		return false;
	}

	public boolean addToPattern(ArrayList<closureEdge> newPattern, ArrayList<closureVertex> vertexList,
			ArrayList<Integer> graphIDFullyCovered_new, ArrayList<Integer> graphIDFullyUncovered_toRemove) {
		// System.out.println(">>>>>>>>>>>>>>> addToPattern: BEFORE");
		// print();
		ArrayList<Integer> vertexList_ID = new ArrayList<Integer>();
		ArrayList<String> newPattern_string = new ArrayList<String>();
		// System.out.println(">>>>>>>>>>>>>>> addToPattern: AFTER");
		// print();
		// System.out.println(">>>>>>>>>>>>>>> addToPattern:
		// newPattern="+newPattern.size());
		for (int i = 0; i < newPattern.size(); i++) {
			closureEdge e = newPattern.get(i);
			closureVertex source = e.getSource();
			closureVertex target = e.getTarget();
			// System.out.print("source: ");
			// source.print();
			// System.out.print("target: ");
			// target.print();
			if (vertexList_ID.contains(source.getID()) == false) {
				vertexList.add(source);
				vertexList_ID.add(source.getID());
			}
			if (vertexList_ID.contains(target.getID()) == false) {
				vertexList.add(target);
				vertexList_ID.add(target.getID());
			}
			newPattern_string.add(e.getEdgeLabelString());
			// System.out.println("vertexList_ID: "+vertexList_ID.toString());
		}
		// check existence of this pattern
		if (patternSetHasNewPattern(newPattern_string) == false) {
			patternList_vertex.add(vertexList);
			patternList_edge.add(newPattern);
			patternList_string.add(newPattern_string);
			int pSize = newPattern.size();
			int pSize_index = patternSize.indexOf(pSize);
			if (pSize_index == -1) {
				patternSize.add(pSize);
				patternSize_freq.add(1);
			} else {
				int currNumPatternForThisSize = patternSize_freq.get(pSize_index);
				patternSize_freq.set(pSize_index, currNumPatternForThisSize + 1);
			}
			// update fullyCoveredGraphIDList to contain all id in graphIDFullyCovered_new
			for (int i = 0; i < graphIDFullyCovered_new.size(); i++) {
				int newGraphID = graphIDFullyCovered_new.get(i);
				if (fullyCoveredGraphIDList.contains(newGraphID) == false)
					fullyCoveredGraphIDList.add(newGraphID);
			}
			// update fullyUncoveredGraphIDList to remove all id in
			// graphIDFullyUncovered_toRemove
			for (int i = 0; i < graphIDFullyUncovered_toRemove.size(); i++) {
				int index = fullyUncoveredGraphIDList.indexOf(graphIDFullyUncovered_toRemove.get(i));
				if (index != -1)
					fullyUncoveredGraphIDList.remove(index);
			}
			print();
			return true;// return true if added successfully
		}
		return false;// return false is newPattern is not added successfully
	}

	private void createPatternFolders() throws IOException {
		int minimumSize = Collections.min(patternSize);
		int maximumSize = Collections.max(patternSize);

		for (int i = minimumSize; i <= maximumSize; i++) {
			String cmdEmptyDifFolder = "cmd /c mkdir " + thumbnailFolderPath + "S" + i;
			try {
				Process procEmptyDif = Runtime.getRuntime().exec(cmdEmptyDifFolder);
				procEmptyDif.waitFor();
			} catch (InterruptedException ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	private void generatePatternImage(Graph tmpPattern, int patternId, String imagePatternPath)
			throws ClassNotFoundException, IOException {
		DotGenerator dotGenerator = new DotGenerator();

		dotGenerator.createGraph(tmpPattern);
		dotGenerator.formatDotFile(patternId);

		String cmdGeneratePNG = "cmd /c dot -Tpng  -Kneato -Gepsilon=0.0001 patterns/temp_pattern.dot -o "
				+ imagePatternPath + Integer.toString(patternId) + ".png";
		String cmdGenerateSVG = "cmd /c dot -Tsvg  -Kneato -Gepsilon=0.0001 patterns/temp_pattern.dot -o patterns/tmp_pattern.svg";

		try {
			Process procPNG = Runtime.getRuntime().exec(cmdGeneratePNG);
			/*
			 * BufferedReader stdInput = new BufferedReader(new
			 * InputStreamReader(procPNG.getInputStream()));
			 * 
			 * Checking output of cmd running from java BufferedReader stdError = new
			 * BufferedReader(new InputStreamReader(procPNG.getErrorStream())); String s =
			 * ""; // read the output from the command
			 * System.out.println("Here is the standard output of the command:\n"); while
			 * ((s = stdInput.readLine()) != null) { System.out.println(s); }
			 * 
			 * // read any errors from the attempted command
			 * System.out.println("Here is the standard error of the command (if any):\n");
			 * while ((s = stdError.readLine()) != null) { System.out.println(s); }
			 */

			procPNG.waitFor();
			Process procSVG = Runtime.getRuntime().exec(cmdGenerateSVG);
			procSVG.waitFor();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}
	}

	// convert the mined patterns to the graph format in davinci.adjlistgraph so as
	// to make use of
	// generatePatternImage() function in PatternGenerator.java to generate the .dot
	// files for display
	public void convertPatternsToGraphForDot() throws IOException {
		// Create fresh ./patterns/image folder
		String cmdDeleteImageFolder = "cmd /c rmdir /Q /S " + thumbnailFolderPath;
		String cmdcmdMakeImageFolder = "cmd /c mkdir " + thumbnailFolderPath;
		try {
			Process procDeleteImageFolder = Runtime.getRuntime().exec(cmdDeleteImageFolder);
			procDeleteImageFolder.waitFor();

			Process procmdMakeImageFolder = Runtime.getRuntime().exec(cmdcmdMakeImageFolder);
			procmdMakeImageFolder.waitFor();
		} catch (InterruptedException ex) {
			throw new RuntimeException(ex);
		}

		// Create folders to contain images of generated patterns which are categorized
		// based on number of edges
		createPatternFolders();

		print();

		// System.out.println("num patterns: "+patternList_vertex.size());

		for (int i = 0; i < patternList_vertex.size(); i++) {
			try {
				ArrayList<closureVertex> thisPattern_vertex = getPattern_vertex(i);
				ArrayList<closureEdge> thisPattern_edge = getPattern_edge(i);
				Graph dotGraph = new Graph();
				String pathToSaveImage = "";

				ArrayList<Integer> thisPattern_vertexID = new ArrayList<Integer>();
				ArrayList<Integer> dotGraph_vertexID = new ArrayList<Integer>();

				// System.out.println("pattern "+i+" : num of
				// vertex="+thisPattern_vertex.size());
				for (int j = 0; j < thisPattern_vertex.size(); j++) {
					closureVertex thisVertex = thisPattern_vertex.get(j);
					thisVertex.print();
					String label = thisVertex.getLabel().get(0);
					Vertex node = new Vertex();
					node.setLabel(label);
					node.setID(j);
					dotGraph.addNode(node);
					thisPattern_vertexID.add(thisVertex.getID());
					dotGraph_vertexID.add(j);
				}

				// System.out.println("thisPattern_vertexID: "+thisPattern_vertexID.toString());
				// System.out.println("dotGraph_vertexID: "+dotGraph_vertexID.toString());

				for (int j = 0; j < thisPattern_edge.size(); j++) {
					closureEdge e = thisPattern_edge.get(j);
					closureVertex sV = e.getSource();
					closureVertex tV = e.getTarget();
					int patternSourceVertexID_index = thisPattern_vertexID.indexOf(sV.getID());
					int patternTargetVertexID_index = thisPattern_vertexID.indexOf(tV.getID());
					if (patternSourceVertexID_index == -1)
						System.out.println(
								"patternSet.java [ERROR]: please check your patterns again. patternSourceVertexID not found! sV:"
										+ sV.getID() + " [" + sV.getLabel().get(0) + "]");
					if (patternTargetVertexID_index == -1)
						System.out.println(
								"patternSet.java [ERROR]: please check your patterns again. patternTargetVertexID_index not found! tV:"
										+ tV.getID() + " [" + tV.getLabel().get(0) + "]");
					int dotGraph_sourceVertexID = dotGraph_vertexID.get(patternSourceVertexID_index);
					int dotGraph_targetVertexID = dotGraph_vertexID.get(patternTargetVertexID_index);
					dotGraph.addEdge(dotGraph_sourceVertexID, dotGraph_targetVertexID);
					dotGraph.getNode(dotGraph_sourceVertexID).setIn(dotGraph_targetVertexID);
					dotGraph.getNode(dotGraph_targetVertexID).setIn(dotGraph_sourceVertexID);
					dotGraph.getNode(dotGraph_sourceVertexID).incDegree();
					dotGraph.getNode(dotGraph_targetVertexID).incDegree();
				}

				// Set number of vertices and number of edges of the temp pattern
				dotGraph.setVertexNum(thisPattern_vertex.size());
				dotGraph.setEdgeNum(thisPattern_edge.size());
				pathToSaveImage = thumbnailFolderPath + "S" + thisPattern_edge.size() + "/";
				generatePatternImage(dotGraph, i, pathToSaveImage);
			} catch (ClassNotFoundException ex) {
				// Exceptions.printStackTrace(ex);
			}
		}
	}

	public void print() {
		System.out.println("pattern List:");
		for (int i = 0; i < patternList_edge.size(); i++) {
			ArrayList<closureEdge> edgeList = patternList_edge.get(i);
			System.out.println("pattern " + i + ": --------------------------------");
			System.out.println("its edges: " + edgeList.size());
			for (int j = 0; j < edgeList.size(); j++)
				edgeList.get(j).print();
			ArrayList<closureVertex> vList = patternList_vertex.get(i);
			System.out.println("its vertices: " + vList.size());
			for (int j = 0; j < vList.size(); j++)
				vList.get(j).print();

		}
		System.out.println("fullyCoveredGraphIDList: [" + fullyCoveredGraphIDList.size() + "] "
				+ fullyCoveredGraphIDList.toString());
		System.out.println("fullyUncoveredGraphIDList: [" + fullyUncoveredGraphIDList.size() + "] "
				+ fullyUncoveredGraphIDList.toString());
		System.out.println("orphanGraphIDList: [" + orphanGraphIDList.size() + "] " + orphanGraphIDList.toString());
	}
}

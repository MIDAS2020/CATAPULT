/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: CreateExtendedGraph.java
 *
 * Abstract: Construct the Spindle Shaped Graph
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        June 25,2010
 *
 */
package exactquery;

import frequentindex.Vertex;
import infrequentindex.CamGenerator;
import similarity.IdComparator;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 *
 * @author cjjin
 */

// Just create the parent-child relationship;
public class SpigGraphHierarchyCreator {

	private SpindleGraph spigGraph = new SpindleGraph(100);
	private Vector<String> camSet = new Vector<String>(); // cams of all SpigVertex in this SpigGraph

	private int lastSpigVertexId;
	private Queue<SpigVertex> queue = new LinkedList<SpigVertex>();

	private CamGenerator camGenerator = new CamGenerator();

	// Construct the SPIG graph
	public SpindleGraph constructSPIG(SpigVertex targetSpigVertex, SpigVertex startSpigVertex) {

		spigGraph.addNode(startSpigVertex);
		camSet.addElement(startSpigVertex.getCam());

		queue.offer(startSpigVertex);
		lastSpigVertexId = 0;

		SpigVertex curSpigVertex = null;

		// BFS
		while ((curSpigVertex = queue.poll()) != null) {

			// Cannot extend more;
			if (curSpigVertex.getEdgeNum() == targetSpigVertex.getEdgeNum()) {
				break;
			}

			for (int i = 0; i < curSpigVertex.getVertexNum(); i++) { // Each vertex in curSpigVertex;
				for (int j = 0; j < targetSpigVertex.getVertexNum(); j++) { // Each vertex in targetSpigVertex;

					// 1-based
					int edgeId = targetSpigVertex.getEdgeLabel(curSpigVertex.getNode(i).getQueryVertexId(), j);
					if (isNewEdge(curSpigVertex, edgeId)) {

						// Construct a new subgraph based on the seed
						constructNewSpigVertex(curSpigVertex, targetSpigVertex, i, j, edgeId);
					}
				}
			}
		}

		return spigGraph;
	}

	// check if the edgeId idComparator new in curSpigVertex;
	public boolean isNewEdge(SpigVertex curSpigVertex, int edgeLabel) {
		if (edgeLabel > 0 && !curSpigVertex.getEdgeIdSet().contains(edgeLabel)) {
			return true;
		} else {
			return false;
		}
	}

	public SpigVertex constructNewSpigVertex(SpigVertex curSpigVertex, SpigVertex targetSpigVertex, int curVertexId,
			int targetVertexId, int edgeLabel) {
		int curSpigVertexVNum = curSpigVertex.getVertexNum();
		int curSpigVertexENum = curSpigVertex.getEdgeNum();
		SpigVertex newSpigVertex = null;

		// Find VNum of newSpigVertex
		int idx = curSpigVertex.getVertexIDsInQuery().indexOf(targetVertexId);
		if (idx == -1) { // New edge connect 1 vertex in curSpigVertex to 1 vertex in targetSpigVertex;
			newSpigVertex = new SpigVertex(curSpigVertexVNum + 1);
		} else { // New edge connect 2 vertices in curSpigVertex;
			newSpigVertex = new SpigVertex(curSpigVertexVNum);
		}

		// Copy vertices from curSpigVertex to newSpigVertex;
		for (int i = 0; i < curSpigVertexVNum; i++) {
			Vertex node = new Vertex();
			node.setLabel(curSpigVertex.getNode(i).getLabel());
			node.setID(i);
			node.setQueryVertexID(curSpigVertex.getNode(i).getQueryVertexId());
			newSpigVertex.addNode(node);
		}
		newSpigVertex.addVertexIDsInQuery(curSpigVertex.getVertexIDsInQuery());

		// Copy edges from curSpigVertex to newSpigVertex;
		for (int i = 0; i < curSpigVertexVNum; i++) {
			for (int j = 0; j < i; j++) {
				int label = curSpigVertex.getEdgeLabel(i, j);
				if (label > 0) {
					newSpigVertex.addEdge(i, j, label);
					newSpigVertex.getNode(i).incDegree();
					newSpigVertex.getNode(j).incDegree();
					newSpigVertex.addToEdgeIdSet(label);
				}
			}
		}

		// Add 1 new vertex and 1 new edge;
		if (idx == -1) {
			// Create new vertex;
			Vertex node = new Vertex();
			node.setLabel(targetSpigVertex.getNode(targetVertexId).getLabel());
			node.setQueryVertexID(targetVertexId);
			node.setID(curSpigVertexVNum);

			// Add new vertex;
			newSpigVertex.addNode(node);
			newSpigVertex.addToVertexIdsInQuery(targetVertexId);

			// Add new edge;
			newSpigVertex.addEdge(curVertexId, curSpigVertexVNum, edgeLabel);
			newSpigVertex.getNode(curVertexId).incDegree();
			newSpigVertex.getNode(curSpigVertexVNum).incDegree();
			newSpigVertex.setVertexNum(curSpigVertexVNum + 1);
		}
		// Add 1 new edge;
		else {
			newSpigVertex.addEdge(curVertexId, idx, edgeLabel);
			newSpigVertex.getNode(curVertexId).incDegree();
			newSpigVertex.getNode(idx).incDegree();
			newSpigVertex.setVertexNum(curSpigVertexVNum);
		}

		// The number of edge always increases
		newSpigVertex.addToEdgeIdSet(edgeLabel);
		newSpigVertex.setEdgeNum(curSpigVertexENum + 1);

		// Sort the edge label in label set
		IdComparator idComparator = new IdComparator();
		Collections.sort(newSpigVertex.getEdgeIdSet(), idComparator);

		// Add newSpigVertex to SpigGraph;
		int index = searchByEdgeLabelSet(newSpigVertex);
		if (index == -1) {
			String tmpcam = CamGenerator.buildCam(newSpigVertex);
			newSpigVertex.setCam(tmpcam);

			newSpigVertex.setGraphid(++lastSpigVertexId);
			spigGraph.addNode(newSpigVertex);
			camSet.addElement(tmpcam);

			if (spigGraph.hasEdge(curSpigVertex.getGraphID(), newSpigVertex.getGraphID()) == 0) {
				spigGraph.addEdge(curSpigVertex.getGraphID(), newSpigVertex.getGraphID());
			}

			// Add to queue to be extended in the future;
			queue.offer(newSpigVertex);
		} else {
			if (spigGraph.hasEdge(curSpigVertex.getGraphID(), index) == 0) {
				spigGraph.addEdge(curSpigVertex.getGraphID(), index);
			}
		}

		return newSpigVertex;
	}

	// Check spigVertex idComparator a node in SPIG by its edge labels
	public int searchByEdgeLabelSet(SpigVertex spigVertex) {
		for (int i = 0; i < spigGraph.getVNum(); i++) {
			if (isEqual(spigGraph.getNode(i).getEdgeIdSet(), spigVertex.getEdgeIdSet())) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isEqual(Vector<Integer> list1, Vector<Integer> list2) {
		if (list1.size() != list2.size())
			return false;

		for (int i = 0; i < list1.size(); i++) {
			if (list1.get(i) != list2.get(i))
				return false;
		}

		return true;
	}
}

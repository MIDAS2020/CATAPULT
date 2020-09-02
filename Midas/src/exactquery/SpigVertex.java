/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package exactquery;

import andyfyp.Pattern;
import andyfyp.PatternManager;
import adjlistgraph.Graph;
import frequentindex.Vertex;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Vector;

/*
 * Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: SNode.java
 *
 * Abstract: The SPIG node structure
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Feb.22,2010
 *
 */
/**
 *
 * @author cjjin
 */

// undirected graph
public class SpigVertex extends Graph {

	private int[][] adjMat; // Value is 1-based;

	private Vector<Integer> qVertexIdSet = new Vector<Integer>(); // Sub-set of vertexId taken from query;
	private Vector<Integer> qEdgeIdSet = new Vector<Integer>(); // Value is 1-based;

	private int clusterId = -1;

	// 4 elements of SpigVertex's Fragment list;
	private int freqId = -1;
	private int difId = -1;
	private HashSet<Integer> difIdlist = new HashSet<Integer>();
	private HashSet<Integer> freqIdlist = new HashSet<Integer>();

	public SpigVertex(int nodeNum) {
		super();

		adjMat = new int[nodeNum][nodeNum];
		for (int i = 0; i < nodeNum; i++) {
			for (int j = 0; j < nodeNum; j++) {
				adjMat[i][j] = 0;
			}
		}
	}

	// Add formulation sequence as the label of the edge
	public void addEdge(int from, int to, int label) {
		adjMat[from][to] = label;
		adjMat[to][from] = label;
	}

	@Override
	public int getEdgeLabel(int from, int to) {
		return adjMat[from][to];
	}

	public void addToDifIds(int gid) {
		difIdlist.add(gid);
	}

	public HashSet<Integer> getDifIDs() {
		return difIdlist;
	}

	public void addToFreqIDs(int gid) {
		freqIdlist.add(gid);
	}

	public HashSet<Integer> getFreqIDs() {
		return freqIdlist;
	}

	public void setItsFreqID(int id) {
		freqId = id;
	}

	public int getItsFreqID() {
		return freqId;
	}

	public void setItsDifId(int id) {
		difId = id;
	}

	public int getItsDifID() {
		return difId;
	}

	// if the node is frequent, which cluster does it belong to
	public void setClusterID(int id) {
		clusterId = id;
	}

	public int getClusterID() {
		return clusterId;
	}

	// for exnode generatioin
	public void addToVertexIdsInQuery(int id) {
		qVertexIdSet.add(id);
	}

	public void addVertexIDsInQuery(Vector<Integer> idset) {
		qVertexIdSet.addAll(idset);
	}

	public Vector<Integer> getVertexIDsInQuery() {
		return qVertexIdSet;
	}

	public void addToEdgeIdSet(int label) {
		qEdgeIdSet.add(label);
	}

	public Vector<Integer> getEdgeIdSet() {
		return qEdgeIdSet;
	}

	/*----------------------------HongHien------------------------------------*/
	public static SpigVertex convertViewToReal(SpigVertex spigVertex) {
		boolean isView = false;
		for (int i = 0; i < spigVertex.getVertexNum(); i++) {
			int tmp = spigVertex.getNode(i).patternId;
			if (tmp != -1) {
				isView = true;
				break;
			}
		}
		if (!isView)
			return spigVertex;

		SpigVertex newSpigVertex = null;
		int[] ma = new int[spigVertex.getVertexNum()];
		Arrays.fill(ma, -1);

		// calculate vNum;
		int vNum = 0;
		for (int i = 0; i < spigVertex.getVertexNum(); i++) {
			Vertex tmpV = spigVertex.getNode(i);
			if (tmpV.patternId == -1) {
				++vNum;
			} else {
				if (tmpV.isPattern) {
					int pId = util.Utilities.getPatternId1(-tmpV.patternId);
					vNum += PatternManager.patternSet.get(pId).vertexNum;
				}
			}
		}
		newSpigVertex = new SpigVertex(vNum);
		newSpigVertex.setVertexNum(vNum);

		// Copy patterns;
		vNum = 0;
		for (int i = 0; i < spigVertex.getVertexNum(); i++) {
			Vertex tmpV = spigVertex.getNode(i);
			if (tmpV.isPattern) {
				int realPId = util.Utilities.getPatternId1(-tmpV.patternId);
				int coeff = util.Utilities.getPatternCoeff(-tmpV.patternId);
				Pattern pat = PatternManager.patternSet.get(realPId);

				for (int j = 0; j < pat.vertexNum; j++) {
					Vertex node = new Vertex();
					node.setLabel(pat.vertexSet[j].labelId);
					node.setID(vNum + j);
					newSpigVertex.addNode(node);

					int tmpPId = -util.Utilities.getPatternVertexId(realPId, j, coeff);
					for (int k = 0; k < spigVertex.getVertexNum(); k++) {
						if (spigVertex.getNode(k).patternId == tmpPId) {
							ma[k] = vNum + j;
							break;
						}
					}
				}

				for (int j = 0; j < pat.vertexNum; j++) {
					for (int k = j + 1; k < pat.vertexNum; k++) {
						if (pat.hasEdge(j, k)) {
							newSpigVertex.addEdge(vNum + j, vNum + k, 1);
							newSpigVertex.getNode(vNum + j).incDegree();
							newSpigVertex.getNode(vNum + k).incDegree();
						}
					}
				}

				vNum = vNum + pat.vertexNum;
			}
		}

		// Copy the rest;
		for (int i = 0; i < spigVertex.getVertexNum(); i++)
			if (ma[i] == -1) {
				Vertex tmpV = spigVertex.getNode(i);
				if (tmpV.patternId != -1)
					continue;

				Vertex node = new Vertex();
				node.setLabel(tmpV.getLabel());
				node.setID(vNum);
				ma[i] = vNum;
				++vNum;

				newSpigVertex.addNode(node);
			}
		for (int i = 0; i < spigVertex.getVertexNum(); i++) {
			for (int j = i + 1; j < spigVertex.getVertexNum(); j++) {
				if (spigVertex.getNode(i).isPattern || spigVertex.getNode(j).isPattern)
					continue;

				if (spigVertex.getNode(i).patternId != -1 && spigVertex.getNode(j).patternId != -1) {
					int pId1 = util.Utilities.getPatternId1(-spigVertex.getNode(i).patternId);
					int pId2 = util.Utilities.getPatternId1(-spigVertex.getNode(j).patternId);
					int vId1 = util.Utilities.getPatternVertexId1(-spigVertex.getNode(i).patternId);
					int vId2 = util.Utilities.getPatternVertexId1(-spigVertex.getNode(j).patternId);

					if (pId1 == pId2) {
						Pattern pat = andyfyp.PatternManager.patternSet.get(pId1);
						if (pat.hasEdge(vId1, vId2))
							continue;
					}
				}

				int label = spigVertex.getEdgeLabel(i, j);
				if (label > 0) {
					int ii = ma[i];
					int jj = ma[j];
					newSpigVertex.addEdge(ii, jj, label);
					newSpigVertex.getNode(ii).incDegree();
					newSpigVertex.getNode(jj).incDegree();
				}
			}
		}

		return newSpigVertex;
	}
}

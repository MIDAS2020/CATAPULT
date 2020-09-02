/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
/**
 *
 * @author Chua Huey Eng
 */
public class VF2 {
	
	//added by Kai 
	//private ArrayList<VF2_Match> allmatched;
	//public ArrayList<VF2_Match> getAllmatched() {
	//	return allmatched;
//	}


	//public void setAllmatched(ArrayList<VF2_Match> allmatched) {
	//	this.allmatched = allmatched;
//	}

	private ArrayList<Integer> allBestMatchings;
	public ArrayList<Integer> getAllBestMatchings() {
		return allBestMatchings;
	}


	public void setAllBestMatchings(ArrayList<Integer> allBestMatchings) {
		this.allBestMatchings = allBestMatchings;
	}

	private  ArrayList<ArrayList<closureVertex>>   matchedDataNodes;
	public ArrayList<ArrayList<closureVertex>> getMatchedDataNodes() {
		return matchedDataNodes;
	}


	public void setMatchedDataNodes(ArrayList<ArrayList<closureVertex>> matchedDataNodes) {
		this.matchedDataNodes = matchedDataNodes;
	}
	private  ArrayList<ArrayList<closureVertex>>   matchedQueryNodes;

	public ArrayList<ArrayList<closureVertex>> getMatchedQueryNodes() {
		return matchedQueryNodes;
	}


	public void setMatchedQueryNodes(ArrayList<ArrayList<closureVertex>> matchedQueryNodes) {
		this.matchedQueryNodes = matchedQueryNodes;
	}


	public VF2() {
		super();
		
	}

	
	// Add By Kai , find all matches
	public void doVF2FindAllmatchings(VF2_Match intState, JGraphtClosureGraph pattern, JGraphtClosureGraph dataGraph,
			boolean PRINT) {
		JGraphtClosureGraph smallG, bigG;
		// 1. find out which graph is small and which is big
		if (pattern.getNumNodes() > dataGraph.getNumNodes()) {
			smallG = dataGraph;
			bigG = pattern;
		} else {
			smallG = pattern;
			bigG = dataGraph;
		}
		// 2. filter candidates
		ArrayList<ArrayList<closureVertex>> candidates = VF2_filterCandidates(smallG, bigG, PRINT);
		if (candidates == null)// at least one node label in smallG is not found in bigG
			return ;
		if (candidates.size() == 0) {
			System.out.println("%%% Oh dear! NO candidates returned for VF2_filterCandidates!");
		}

		// 3. Optimize the order of the query vertex according to the number of
		// candidates in the data graph
		ArrayList<Integer> reorderedQueryVertexIndex = VF2_reorderQueryVertex(candidates, PRINT);
		if (PRINT) {
			System.out.println("reorderedQueryVertexIndex: " + reorderedQueryVertexIndex.toString());
			ArrayList<closureVertex> smallGVList = smallG.getClosureVertexList();
			for (int i = 0; i < reorderedQueryVertexIndex.size(); i++)
				smallGVList.get(reorderedQueryVertexIndex.get(i)).print();
		}
		//allmatched =  new ArrayList<VF2_Match>();
		allBestMatchings =  new  ArrayList<Integer>();
		matchedDataNodes  =   new  ArrayList<ArrayList<closureVertex>>()  ;
		matchedQueryNodes   =   new  ArrayList<ArrayList<closureVertex>>()  ;
		
		
		// 4. subgraph search for all matchings
	    VF2_subgraphSearchForAllMatchings(smallG, bigG, intState, reorderedQueryVertexIndex, candidates, PRINT);
	    
	       
		 //System.out.println("before return");
	   // System.out.println("allBestMatchings: "+allBestMatchings.toString());
		// System.out.println("matchedDataNodes: "+matchedDataNodes.size());
	//	 for(int i=0;i<matchedDataNodes.size();i++) 
		//	 System.out.println("matchedDataNodes.get(i).size(): "+matchedDataNodes.get(i).size());
	////	 System.out.println("matchedQueryNodes: "+matchedQueryNodes.size());
	//	 for(int i=0;i<matchedQueryNodes.size();i++) 
	//		 System.out.println("matchedQueryNodes.get(i).size(): "+matchedQueryNodes.get(i).size());
		 
		 /*
		  System.out.println("before return");
			for(int k=0;k<allBestMatchings.size();k++) {
				VF2_Match  match =  new VF2_Match();
				match.setBestNumMatch(allBestMatchings.get(k));;
				ArrayList<closureVertex> copiednodes  =  new ArrayList<closureVertex> ();
				for(int i = 0; i< matchedDataNodes.get(k).size();i++){
					closureVertex  newv  = new  closureVertex(matchedDataNodes.get(k).get(i));
					copiednodes.add(newv);
				}
				match.setCurr_dataGraphVertexList(copiednodes);
				allmatched.add(match);
			}*/
	    
	}

	
	private void VF2_subgraphSearchForAllMatchings(JGraphtClosureGraph smallG, JGraphtClosureGraph bigG, VF2_Match intState,
			ArrayList<Integer> queryVertexIndex, ArrayList<ArrayList<closureVertex>> candidates, boolean PRINT) {
		// recursion stops when algo finds the complete solution
		if (PRINT) {
			System.out.println("intState.getBestNumMatch():" + intState.getBestNumMatch());
			System.out.println("smallG.getNumNodes():" + smallG.getNumNodes());
		}
		if (intState.getBestNumMatch() == smallG.getNumNodes()) {
			//System.out.println("lucky intState.getBestNumMatch():" +intState.getBestNumMatch());
			//System.out.println("lucky smallG.getNumNodes():" +smallG.getNumNodes());
			ArrayList<closureVertex> nodes = intState.getCurr_dataGraphVertexList();
			ArrayList<closureVertex> copiednodes  =  new ArrayList<closureVertex> ();
			for(int i = 0; i< nodes.size();i++){
				closureVertex  newv  = new  closureVertex(nodes.get(i));
				copiednodes.add(newv);
			}
			matchedDataNodes.add(copiednodes);
			
			
			ArrayList<closureVertex> nodes2 = intState.getCurr_patternVertexList();
			ArrayList<closureVertex> copiednodes2  =  new ArrayList<closureVertex> ();
			for(int i = 0; i< nodes2.size();i++){
				closureVertex  newv2  = new  closureVertex(nodes2.get(i));
				copiednodes2.add(newv2);
			}
			matchedQueryNodes.add(copiednodes2);
			
			
			allBestMatchings.add(intState.getBestNumMatch());
			return ;
		}
			
		else {
			closureVertex u = VF2_nextQueryVertex(smallG, intState, queryVertexIndex, PRINT);
			if (u == null)
				return ;
			ArrayList<closureVertex> CR = VF2_refineCandidates(intState, u, candidates, smallG, bigG, PRINT);
			ArrayList<closureVertex> Mg = intState.getMatchedDataGraphVertexList();
			boolean CONTINUE = true;
			if (PRINT) {
				System.out.println("refined candidates");
				for (int i = 0; i < CR.size(); i++)
					CR.get(i).print();
			}
			for (int i = 0; i < CR.size() && CONTINUE; i++) {
				closureVertex v = CR.get(i);
				if (PRINT) {
					System.out.println("candidate closureVertex v: ");
					v.print();
				}
				if (Mg.contains(v) == false) {
					if (PRINT)
						System.out.println("closureVertex v is not in matched set!");
					if (VF2_isJoinable(smallG, bigG, intState, u, v)) {
						if (PRINT)
							System.out.println("closureVertex v is joinable");
						intState.addAMatch(u, v);// VF2_updateState
						VF2_subgraphSearchForAllMatchings(smallG, bigG, intState, queryVertexIndex, candidates, PRINT);
						
					    /// modified by Kai
						intState.removeLastMatch();// VF2_restoreState
						
						//if (intState.getBestNumMatch() == smallG.getNumNodes())
						//	CONTINUE = false;
						//else {
						//	intState.removeLastMatch();// VF2_restoreState
						if (PRINT)
							System.out.println("remove last match");
						}
					} else {
						if (PRINT)
							System.out.println("closureVertex v is NOT joinable");
					}
				}
			}
		return ;
	}

	
	
	// implementation based on VLDB 2012 Jinsoo Lee et al. An In-depth Comparison of
	// Subgraph Isomorphism Algorithms in Graph Databases
	public VF2_Match doVF2(VF2_Match intState, JGraphtClosureGraph pattern, JGraphtClosureGraph dataGraph,
			boolean PRINT) {
		JGraphtClosureGraph smallG, bigG;
		// 1. find out which graph is small and which is big
		if (pattern.getNumNodes() > dataGraph.getNumNodes()) {
			smallG = dataGraph;
			bigG = pattern;
		} else {
			smallG = pattern;
			bigG = dataGraph;
		}
		// 2. filter candidates
		ArrayList<ArrayList<closureVertex>> candidates = VF2_filterCandidates(smallG, bigG, PRINT);
		if (candidates == null)// at least one node label in smallG is not found in bigG
			return null;
		if (candidates.size() == 0) {
			System.out.println("%%% Oh dear! NO candidates returned for VF2_filterCandidates!");
		}

		// 3. Optimize the order of the query vertex according to the number of
		// candidates in the data graph
		ArrayList<Integer> reorderedQueryVertexIndex = VF2_reorderQueryVertex(candidates, PRINT);
		if (PRINT) {
			System.out.println("reorderedQueryVertexIndex: " + reorderedQueryVertexIndex.toString());
			ArrayList<closureVertex> smallGVList = smallG.getClosureVertexList();
			for (int i = 0; i < reorderedQueryVertexIndex.size(); i++)
				smallGVList.get(reorderedQueryVertexIndex.get(i)).print();
		}

		// 4. subgraph search
		return VF2_subgraphSearch(smallG, bigG, intState, reorderedQueryVertexIndex, candidates, PRINT);
	}

	private ArrayList<ArrayList<closureVertex>> VF2_filterCandidates(JGraphtClosureGraph smallG,
			JGraphtClosureGraph bigG, boolean PRINT) {
		ArrayList<ArrayList<closureVertex>> candList = new ArrayList<ArrayList<closureVertex>>();
		ArrayList<closureVertex> smallG_nodeList = smallG.getClosureVertexList();
		ArrayList<closureVertex> bigG_nodeList = bigG.getClosureVertexList();
		ArrayList<String> bigG_labelList = new ArrayList<String>();
		ArrayList<ArrayList<closureVertex>> bigG_labelList_node = new ArrayList<ArrayList<closureVertex>>();

		if (PRINT)
			bigG.print();

		for (int i = 0; i < bigG_nodeList.size(); i++) {
			closureVertex v = bigG_nodeList.get(i);
			String v_label = v.getLabel().get(0);
			int labelList_index = bigG_labelList.indexOf(v_label);

			if (labelList_index == -1)// label not in list yet
			{
				bigG_labelList.add(v_label);
				ArrayList<closureVertex> vList = new ArrayList<closureVertex>();
				vList.add(v);
				bigG_labelList_node.add(vList);
			} else// label already in list
				bigG_labelList_node.get(labelList_index).add(v);
		}

		if (PRINT) {
			System.out.println("bigG_labelList: " + bigG_labelList.toString());
			for (int i = 0; i < bigG_labelList_node.size(); i++)
				System.out.println(i + "size:" + bigG_labelList_node.get(i).size());
		}

		for (int i = 0; i < smallG_nodeList.size(); i++) {
			String v_label = smallG_nodeList.get(i).getLabel().get(0);
			int v_label_index = bigG_labelList.indexOf(v_label);
			if (v_label_index == -1)// a node in smallG has a label not found in bigG
				return null;
			else {
				ArrayList<closureVertex> v_candList = bigG_labelList_node.get(v_label_index);
				candList.add(v_candList);
			}
		}
		if (PRINT) {
			for (int i = 0; i < candList.size(); i++) {
				System.out.println(i + "candList size:" + candList.get(i).size());
				for (int j = 0; j < candList.get(i).size(); j++)
					candList.get(i).get(j).print();
			}
		}

		return candList;
	}

	private closureVertex VF2_nextQueryVertex(JGraphtClosureGraph smallG, VF2_Match currMatch,
			ArrayList<Integer> queryVertexIndex, boolean PRINT) {
		ArrayList<closureVertex> candVertexList = smallG.getClosureVertexList();
		if (PRINT) {
			System.out.println("smallG unordered cand vertex:");
			for (int i = 0; i < candVertexList.size(); i++)
				candVertexList.get(i).print();
		}
		closureVertex nextVertex = null;
		if (currMatch.getBestNumMatch() == 0)
			nextVertex = candVertexList.get(queryVertexIndex.get(0));
		else {
			ArrayList<closureVertex> matchedVertexList = currMatch.getMatchedPatternVertexList();
			ArrayList<closureVertex> candNextVertex = new ArrayList<closureVertex>();
			for (int i = 0; i < matchedVertexList.size(); i++) {
				closureVertex currMatchedVertex = matchedVertexList.get(i);
				ArrayList<closureVertex> currMatchedVertex_neighbours = smallG.getNeighbourOf(currMatchedVertex);
				currMatchedVertex_neighbours.removeAll(matchedVertexList);
				if (currMatchedVertex_neighbours.size() > 0) {
					for (int n = 0; n < currMatchedVertex_neighbours.size(); n++) {
						closureVertex neighbour = currMatchedVertex_neighbours.get(n);
						if (candNextVertex.contains(neighbour) == false)
							candNextVertex.add(neighbour);
					}
				}
			}
			ArrayList<Integer> candNextVertex_newIndex = new ArrayList<Integer>();
			if (PRINT)
				System.out.println("connected vertices:");
			for (int i = 0; i < candNextVertex.size(); i++) {
				int candVertexList_index = candVertexList.indexOf(candNextVertex.get(i));
				int queryVertexIndex_index = queryVertexIndex.indexOf(candVertexList_index);
				candNextVertex_newIndex.add(queryVertexIndex_index);
				if (PRINT) {
					System.out.print("candNextVertx:");
					candNextVertex.get(i).print();
					System.out.println("candVertexList_index: " + candVertexList_index + " queryVertexIndex_index:"
							+ queryVertexIndex_index);
				}
			}
			if (PRINT)
				System.out.println("candNextVertex_newIndex: " + candNextVertex_newIndex.toString());
			if (candNextVertex_newIndex.size() > 0) {
				int nextVertex_index = Collections.min(candNextVertex_newIndex);
				int index = candNextVertex_newIndex.indexOf(nextVertex_index);
				nextVertex = candNextVertex.get(index);
			}
		}
		if (PRINT) {
			System.out.println("nextVertex: ");
			nextVertex.print();
		}
		return nextVertex;
	}

	private ArrayList<closureVertex> VF2_refineCandidates(VF2_Match intState, closureVertex u,
			ArrayList<ArrayList<closureVertex>> C, JGraphtClosureGraph smallG, JGraphtClosureGraph bigG,
			boolean PRINT) {
		// 1. Prune out any vertex v in C(u) such that v is not connected from already
		// matched data vertices.
		// 2. Let Mq and Mg be a set of matched query vertices and a set of matched data
		// vertices, respectively.
		// Let Cq and Cg be a set of adjacent and not-yet-matched query vertices
		// connected from Mq and a set of
		// adjacent and not-yet-matched data vertices connected from Mg, respectively.
		// Let adj(u) be a set of adjacent
		// vertices to a vertex u.
		// Then, prune out any vertex v in C(u) such that |Cq intersect adj(u)|>|Cg
		// intersect adj(v)|
		// 3. Prune out any vertex v in C(u) such that |adj(u)\Cq\Mq|>|adj(v)\Cg\Mg|

		// find C(u)
		ArrayList<closureVertex> smallG_vertexList = smallG.getClosureVertexList();
		int index = smallG_vertexList.indexOf(u);
		ArrayList<closureVertex> u_cand = C.get(index);
		ArrayList<closureVertex> Cu = new ArrayList<closureVertex>();
		for (int i = 0; i < u_cand.size(); i++)
			Cu.add(u_cand.get(i));
		if (PRINT) {
			System.out.println("candidates of u:");
			for (int i = 0; i < Cu.size(); i++)
				Cu.get(i).print();
		}
		if (intState.getBestNumMatch() > 0) {
			ArrayList<closureVertex> Mg = intState.getMatchedDataGraphVertexList();
			ArrayList<closureVertex> Mg_adjVertex = new ArrayList<closureVertex>();
			ArrayList<closureVertex> Cg = new ArrayList<closureVertex>();
			for (int i = 0; i < Mg.size(); i++) {
				closureVertex v = Mg.get(i);
				ArrayList<closureVertex> v_neighbour = bigG.getNeighbourOf(v);
				for (int n = 0; n < v_neighbour.size(); n++) {
					closureVertex neighbour = v_neighbour.get(n);
					// [1] get all adjacent vertices of already matched data vertices Mg
					if (Mg_adjVertex.contains(neighbour) == false)
						Mg_adjVertex.add(neighbour);
					// [2] get all adjacent and not-yet-matched data vertices
					if (Mg.contains(neighbour) == false && Cg.contains(neighbour) == false)
						Cg.add(neighbour);
				}
			}
			// [1] Prune out any vertex v in C(u) such that v is not connected from already
			// matched data vertices.
			if (intState.getBestNumMatch() > 0)
				Cu.retainAll(Mg_adjVertex);

			ArrayList<closureVertex> Mq = intState.getMatchedPatternVertexList();
			ArrayList<closureVertex> Cq = new ArrayList<closureVertex>();
			for (int i = 0; i < Mq.size(); i++) {
				closureVertex q = Mq.get(i);
				ArrayList<closureVertex> q_neighbour = smallG.getNeighbourOf(q);
				for (int n = 0; n < q_neighbour.size(); n++) {
					closureVertex neighbour = q_neighbour.get(n);
					// [2] get all adjacent and not-yet-matched query vertices
					if (Mq.contains(neighbour) == false && Cq.contains(neighbour) == false)
						Cq.add(neighbour);
				}
			}

			// [2] Prune out any vertex v in C(u) such that |Cq intersect adj(u)|>|Cg
			// intersect adj(v)|
			int Cu_item_count = 0;
			while (Cu_item_count < Cu.size()) {
				closureVertex v = Cu.get(Cu_item_count);
				ArrayList<closureVertex> v_adj = bigG.getNeighbourOf(v);
				v_adj.retainAll(Cg);
				ArrayList<closureVertex> u_adj = smallG.getNeighbourOf(u);
				u_adj.retainAll(Cq);
				if (u_adj.size() > v_adj.size())
					Cu.remove(Cu_item_count);
				else
					Cu_item_count++;
			}

			// [3] Prune out any vertex v in C(u) such that |adj(u)\Cq\Mq|>|adj(v)\Cg\Mg|
			Cu_item_count = 0;
			while (Cu_item_count < Cu.size()) {
				closureVertex v = Cu.get(Cu_item_count);
				ArrayList<closureVertex> v_adj = bigG.getNeighbourOf(v);
				v_adj.removeAll(Cg);
				v_adj.removeAll(Mg);
				ArrayList<closureVertex> u_adj = smallG.getNeighbourOf(u);
				u_adj.removeAll(Cq);
				u_adj.removeAll(Mq);
				if (u_adj.size() > v_adj.size())
					Cu.remove(Cu_item_count);
				else
					Cu_item_count++;
			}
		}
		return Cu;
	}

	private boolean VF2_isJoinable(JGraphtClosureGraph smallG, JGraphtClosureGraph bigG, VF2_Match intState,
			closureVertex u, closureVertex v) {
		ArrayList<closureVertex> Mq = intState.getMatchedPatternVertexList();
		ArrayList<closureVertex> u_adj = smallG.getNeighbourOf(u);
		u_adj.retainAll(Mq);// adjacent vertices of u that are already matched

		ArrayList<closureVertex> Mg = intState.getMatchedDataGraphVertexList();
		ArrayList<closureVertex> v_adj = bigG.getNeighbourOf(v);
		v_adj.retainAll(Mg);// adjacent vertices of v that are already matched

		// if there's an edge between u_adj(i) and u, there should be a corresponding
		// edge between intState.getMatchedDataGraphVertex(u_adj(i)) and v
		for (int i = 0; i < u_adj.size(); i++) {
			closureVertex u_adj_i = u_adj.get(i);
			closureVertex matchedV = intState.getMatchedDataGraphVertex(u_adj_i);
			if (v_adj.contains(matchedV) == false)
				return false;
		}
		return true;
	}

	private VF2_Match VF2_subgraphSearch(JGraphtClosureGraph smallG, JGraphtClosureGraph bigG, VF2_Match intState,
			ArrayList<Integer> queryVertexIndex, ArrayList<ArrayList<closureVertex>> candidates, boolean PRINT) {
		// recursion stops when algo finds the complete solution
		if (PRINT) {
			System.out.println("intState.getBestNumMatch():" + intState.getBestNumMatch());
			System.out.println("smallG.getNumNodes():" + smallG.getNumNodes());
		}
		if (intState.getBestNumMatch() == smallG.getNumNodes())
			return intState;
		else {
			closureVertex u = VF2_nextQueryVertex(smallG, intState, queryVertexIndex, PRINT);
			if (u == null)
				return intState;
			ArrayList<closureVertex> CR = VF2_refineCandidates(intState, u, candidates, smallG, bigG, PRINT);
			ArrayList<closureVertex> Mg = intState.getMatchedDataGraphVertexList();
			boolean CONTINUE = true;
			if (PRINT) {
				System.out.println("refined candidates");
				for (int i = 0; i < CR.size(); i++)
					CR.get(i).print();
			}
			for (int i = 0; i < CR.size() && CONTINUE; i++) {
				closureVertex v = CR.get(i);
				if (PRINT) {
					System.out.println("candidate closureVertex v: ");
					v.print();
				}
				if (Mg.contains(v) == false) {
					if (PRINT)
						System.out.println("closureVertex v is not in matched set!");
					if (VF2_isJoinable(smallG, bigG, intState, u, v)) {
						if (PRINT)
							System.out.println("closureVertex v is joinable");
						intState.addAMatch(u, v);// VF2_updateState
						VF2_subgraphSearch(smallG, bigG, intState, queryVertexIndex, candidates, PRINT);
						if (intState.getBestNumMatch() == smallG.getNumNodes())
							CONTINUE = false;
						else {
							intState.removeLastMatch();// VF2_restoreState
							if (PRINT)
								System.out.println("remove last match");
						}
					} else {
						if (PRINT)
							System.out.println("closureVertex v is NOT joinable");
					}
				}
			}
		}

		return intState;
	}

	private ArrayList<Integer> VF2_reorderQueryVertex(ArrayList<ArrayList<closureVertex>> candidate, boolean PRINT) {
		ArrayList<Integer> reorderedIndex = new ArrayList<Integer>();
		ArrayList<Integer> candSize = new ArrayList<Integer>();
		for (int i = 0; i < candidate.size(); i++)
			candSize.add(candidate.get(i).size());
		int maxSizeDefault;
		if (candSize.size() == 0)
			maxSizeDefault = 1;
		else
			maxSizeDefault = Collections.max(candSize) + 1;
		for (int i = 0; i < candidate.size(); i++) {
			int minSize = Collections.min(candSize);
			int index = candSize.indexOf(minSize);
			reorderedIndex.add(index);
			candSize.set(index, maxSizeDefault);
		}
		return reorderedIndex;
	}
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
//import mcGregor.StringLabeledObject;
import org.jgrapht.Graph;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.SimpleGraph;

import main.Trie;

public class JGraphtClosureGraph {

	private UndirectedGraph<closureVertex, closureEdge> g;
	public UndirectedGraph<closureVertex, closureEdge> getG() {
		return g;
	}
	private Integer   ENDTAG   = 3001;
	private Integer   ENDOFTREETAG   = 3002;
	private Integer   EDGELABEL   = 3000;
    private ArrayList<Integer> CanonicalString = new ArrayList<Integer>();
	public ArrayList<Integer> getCanonicalString() {
		return CanonicalString;
	}

	public void setCanonicalString(ArrayList<Integer> canonicalString) {
		CanonicalString = canonicalString;
	}
	private ArrayList<Integer> clusterGraphList = new ArrayList<Integer>();
	// private Graph<StringLabeledObject, StringLabeledObject> mcGregorGraph;
	private ArrayList<closureVertex> closureVertexList = new ArrayList<closureVertex>();
	private ArrayList<closureEdge> closureEdgeList = new ArrayList<closureEdge>();
	private ArrayList<Integer> closureEdgeWtList = new ArrayList<Integer>();
	// private ArrayList<StringLabeledObject> stringLabeledObjList=new
	// ArrayList<StringLabeledObject>();
	private ArrayList<Integer> nodeIDList = new ArrayList<Integer>();
	private ArrayList<String> vertexLabelList = new ArrayList<String>();
	// the vertices having the label corresponding  to that  in vertexLabelList
	private ArrayList<ArrayList<closureVertex>> vertexLabelList_listOfVertex = new ArrayList<ArrayList<closureVertex>>();
	private ArrayList<String> edgeLabelList = new ArrayList<String>();
	private int numOrphanEdges = 0;
	private int lowerEdgeWtBound, upperEdgeWtBound;
	private String threadName;
	private  int root1 ; 
	private  int root2; 
	private ArrayList<Integer>    level   =  new  ArrayList<Integer>(); //the levels of vertices
	private  ArrayList<Integer>   parent  =  new  ArrayList<Integer>() ; //the parents of vertices
	private ArrayList<Integer>   edgeLabelToParent =  new  ArrayList<Integer>(); //the edge label of edge from parent
	private   ArrayList<Integer>   automorphism =  new  ArrayList<Integer>(); //the list of automorphisms
	// for mining
	ArrayList<closureEdge> candidateNextEdges = new ArrayList<closureEdge>();
	ArrayList<Float> jaccardSim = new ArrayList<Float>();
	float bestJaccardSim = 0f;
	ArrayList<closureEdge> bestCandidateNextEdges = new ArrayList<closureEdge>();
	ArrayList<closureEdge> patternEdgeList = new ArrayList<closureEdge>();
	ArrayList<ArrayList<Integer>> closureEdgeMissedGraphList = new ArrayList<ArrayList<Integer>>();
	ArrayList<Integer> closureEdgeMissedGraphSize = new ArrayList<Integer>();
	ArrayList<Integer> distinctMissedGraphCoveredByPattern = new ArrayList<Integer>();
	ArrayList<closureVertex> patternVertexList = new ArrayList<closureVertex>();
	int maxSize;
	int closureEdgeIndex;
	closureEdge nextEdge;
	ArrayList<Integer> currEdgeMissedGraphList = new ArrayList<Integer>();

	public JGraphtClosureGraph() {
		g = new SimpleGraph<closureVertex, closureEdge>(closureEdge.class);
		// mcGregorGraph=new SimpleGraph<StringLabeledObject,
		// StringLabeledObject>(StringLabeledObject.class);
	}

	public JGraphtClosureGraph(ArrayList<closureEdge> patternEdges) {
		g = new SimpleGraph<closureVertex, closureEdge>(closureEdge.class);
		for (int i = 0; i < patternEdges.size(); i++) {
			closureEdge e = patternEdges.get(i);
			closureVertex source = e.getSource();
			closureVertex target = e.getTarget();
			addNode(source);
			addNode(target);
			addEdge(source, target, e);
		}
	}

	public JGraphtClosureGraph(JGraphtClosureGraph h) {
		g = new SimpleGraph<closureVertex, closureEdge>(closureEdge.class);
		ArrayList<closureEdge> eList = h.getClosureEdgeList();
		for (int i = 0; i < eList.size(); i++) {
			closureEdge e = new closureEdge(eList.get(i));
			closureVertex source = e.getSource();
			closureVertex target = e.getTarget();
			addNode(source);
			addNode(target);
			addEdge(source, target, e);
		}
	}

	public JGraphtClosureGraph(JGraphtClosureGraph h, String forcedLabelOnAllNode) {
		g = new SimpleGraph<closureVertex, closureEdge>(closureEdge.class);
		ArrayList<closureEdge> eList = h.getClosureEdgeList();
		for (int i = 0; i < eList.size(); i++) {
			closureEdge e = new closureEdge(eList.get(i));
			closureVertex source = e.getSource();
			closureVertex target = e.getTarget();
			addNode(source);
			addNode(target);
			addEdge(source, target, e);
		}
		for (int i = 0; i < closureVertexList.size(); i++) {
			ArrayList<String> label = new ArrayList<String>();
			label.add(forcedLabelOnAllNode);
			closureVertex v = closureVertexList.get(i);
			v.setLabel(label);
		}
		vertexLabelList = new ArrayList<String>();
		vertexLabelList.add(forcedLabelOnAllNode);
		vertexLabelList_listOfVertex = new ArrayList<ArrayList<closureVertex>>();
		ArrayList<closureVertex> vList = new ArrayList<closureVertex>();
		for (int i = 0; i < closureVertexList.size(); i++)
			vList.add(closureVertexList.get(i));
		vertexLabelList_listOfVertex.add(vList);
	}

	public JGraphtClosureGraph getGraph() {
		return this;
	}

	public void incrementOrphanEdges() {
		numOrphanEdges = numOrphanEdges + 1;
	}

	public int getOrphanEdges() {
		return numOrphanEdges;
	}

	public ArrayList<String> getEdgeLabelList() {
		return edgeLabelList;
	}
  
	public void findRoot() {
	     int totalDegree = 0;
		ArrayList<Integer> degreeList = new ArrayList<Integer>();
		for(int i=0;i<nodeIDList.size();i++) {
			int id = nodeIDList.get(i);
			int degree = getNeighbourOf(id).size();
			degreeList.add(degree);
			totalDegree +=degree;
		}
		if (totalDegree == 0) //trivial case, single vertex 
		{
			root1 = 0;  root2 = -1;
		}
		else if (totalDegree == 2) //trivial case, a pair of vertices
		{
			root1 = 0;  root2 = 1;
		}else {
			Queue<Integer> tempQ = new LinkedList<Integer>();

			for (int  i = 0; i <nodeIDList.size();i++) {
				if (degreeList.get(i) == 1)
					tempQ.offer(i); //find all leaves
			}
			tempQ.offer(-1); //a round
		//	System.out.println("1 : " + tempQ.toString());
		//	System.out.println("1 : " + degreeList.toString());
			while (true)
			{
				int i = tempQ.poll();
		//		System.out.println("2 : " + tempQ.toString());
		//		System.out.println("2: " + degreeList.toString());
				if (i == -1) //finished one round
				{
					if (totalDegree <= 2) break;
					else
					{
						tempQ.offer(-1);
						continue;
					}
				}
				degreeList.set(i,0);
		//		System.out.println("3 : " + tempQ.toString());
		//		System.out.println("3 : " + degreeList.toString());
				totalDegree -= 2;
				int v_id = nodeIDList.get(i);
				ArrayList<closureVertex> t   = getNeighbourOf(v_id);
				int j = 0;
			    for(int k=0;k<t.size();k++) {
			    	closureVertex v= t.get(k);
			    	int id = nodeIDList.indexOf(v.getID());
			    	int degree = degreeList.get(id);
			    	if(degree != 0) {
			    		j  = id;
			    		break;
			    	}
			    }
			    degreeList.set(j, degreeList.get(j) -1);
		//		System.out.println("7 : " + tempQ.toString());
		//		System.out.println("7 : " + degreeList.toString());
				if ((degreeList.get(j)) == 1) {
					tempQ.offer(j);
			//		System.out.println("4 : " + tempQ.toString());
		//			System.out.println("4 : " + degreeList.toString());
				}
			}
	//		System.out.println("5 : " + tempQ.toString());
	//		System.out.println("5 : " + degreeList.toString());
			
			root1 = tempQ.poll() ;

	//		System.out.println("6 : " + tempQ.toString());
	//		System.out.println("6 : " + degreeList.toString());
			if (totalDegree == 2)   root2 = tempQ.element();
			else root2 = -1;
		}
	}
	public void  ConvertJGraphtClosureGraphToCanonicalString() {
		 //ArrayList<closureVertex> getNeighbourOf(int v_id)
		//ArrayList<String> getNeighbourLabelsOf(closureVertex v)
		//ArrayList<closureEdge> getEdgesOf(closureVertex v) 
	 ///closureVertex getVertexWithID(int v_id) 
		///public ArrayList<closureVertex> getNeighbourOf(int v_id) 
	//	private ArrayList<closureVertex> closureVertexList = new ArrayList<closureVertex>();
	//	private ArrayList<closureEdge> closureEdgeList = new ArrayList<closureEdge>();
	//	private ArrayList<Integer> closureEdgeWtList = new ArrayList<Integer>();
	//	private ArrayList<Integer> nodeIDList = new ArrayList<Integer>();
	    

		//// 1. find root 
		findRoot();
		System.out.println("root1: " + root1 + " , root2:" +root2);

	    ArrayList<closureVertex> t = new  ArrayList<closureVertex> ();
	    Queue<Integer> tempQ = new LinkedList<Integer>();
		ArrayList<Boolean> tempFlag= new ArrayList<Boolean>(); //assuming inintal values are false
		for(int i=0;i<nodeIDList.size();i++)  tempFlag.add(false);
		ArrayList<Integer> vertexByLevel = new ArrayList<Integer>(); //partition vertices by levels
		for (int  i = 0; i <= nodeIDList.size(); i++)  vertexByLevel.add(0);
		ArrayList<Integer> vertexOrder= new ArrayList<Integer>(); //the order of each vertex, in a level
		 for(int i=0;i<nodeIDList.size();i++)  vertexOrder.add(0);
		ArrayList<Integer> vertexPerLevel= new ArrayList<Integer>(); //how many vertices in each level
        for(int i=0;i<nodeIDList.size();i++)  vertexPerLevel.add(0);
        
		Integer currentLevel = 0; //current level for counting
		
		//Integer currentVertex = 0; //current vertex for counting
		Integer currentVertex = -1; //current vertex for counting
		
		//first step: filling in levels for all vertices

		for(int i=0;i<nodeIDList.size();i++)   parent.add(-1);
		for(int i=0;i<nodeIDList.size();i++)   edgeLabelToParent.add(-1);
		for(int i=0;i<nodeIDList.size();i++)   level.add(-1);
		for(int i=0;i<nodeIDList.size();i++)   automorphism.add(-1);
        System.out.println("first step: filling in levels for all vertices");
		tempQ.offer(root1);
		tempFlag.set(root1, true);
		parent.set(root1, 0);
		edgeLabelToParent.set(root1, 0); //no parent edge
		if (root2 != -1)
		{
			tempQ.offer(root2);
			tempFlag.set(root2, true);
			parent.set(root2, 0);
			//int v_id = nodeIDList.get(root1);
			//t   = getNeighbourOf(v_id);
			//edgeLabelToParent.set(root1, 1);  // set all edge label to 1
		///	edgeLabelToParent.set(root2, 1);  // set all edge label to 1
			edgeLabelToParent.set(root1, EDGELABEL);  // set all edge label to 1
			edgeLabelToParent.set(root2, EDGELABEL);  // set all edge label to 1
		}
		tempQ.offer(-1); //-1 is used to indicate the end of a level

		vertexPerLevel.set(0,0); //initialize the number of vertices at level 0
		currentLevel = 1;
		vertexPerLevel.set(currentLevel,0); //initialize the number of vertices at first level

		while (!tempQ.isEmpty())
		{
			int i = tempQ.poll();
			if (i == -1) //a new level
			{
				if (tempQ.isEmpty()) continue; //exit while loop
				currentLevel++;
				vertexPerLevel.set(currentLevel,0); 
				tempQ.offer(-1);
			}
			else
			{
				level.set(i, currentLevel);
				tempFlag.set(i, true);
				vertexByLevel.set(++currentVertex, i);
				vertexPerLevel.set(currentLevel, vertexPerLevel.get(currentLevel)+1);
		        
				int v_id = nodeIDList.get(i);
				t   = getNeighbourOf(v_id);
				 for(int k=0;k<t.size();k++) {
				    	closureVertex v= t.get(k);
				    	int id = nodeIDList.indexOf(v.getID());
				    	if(tempFlag.get(id) == false) {
				    		tempQ.offer(id);
							tempFlag.set(id, true);
							parent.set(id, i);
							edgeLabelToParent.set(id, EDGELABEL);
				    	}
				    }
			}
		}
		  System.out.println("parent:" +parent.toString());
		  System.out.println("level: " + level.toString());
		  System.out.println("edgeLabelToParent: " + edgeLabelToParent.toString());
		  
		//now, currentLevel holds the level of the rooted tree


	   //	third step: sorting, bottom up
	
		  System.out.println("third step: sorting, bottom up");
		//accumulated total:
		for (int i = 1; i < currentLevel; i++) 
			vertexPerLevel.set(i,  vertexPerLevel.get(i) + vertexPerLevel.get(i-1));
		  System.out.println("vertexPerLevel:" + vertexPerLevel.toString());
		  System.out.println("currentLevel:" + currentLevel);
		int  j, k, m = 0, n, key = 0;
		//sort vertices, level by level
		for (j = currentLevel; j > 0; j--)
		{
			//insertion sort
			for (k = vertexPerLevel.get(j - 1) + 2; k <= vertexPerLevel.get(j); k++)
			{
				key = vertexByLevel.get(k);
				m = k - 1;
				int tempflag = 0;
				closureVertex vertex1  =  getVertexWithID(key) ;
				closureVertex vertex2 = getVertexWithID(vertexByLevel.get(m)) ;
				if(vertex1.getLabel().get(0).compareTo(vertex2.getLabel().get(0))  < 0) {
					tempflag = 1;
				}else if (vertex1.getLabel().get(0).compareTo(vertex2.getLabel().get(0))  == 0) {
					 int size1 = getNeighbourOf(key).size();
					 int size2 =  getNeighbourOf(vertexByLevel.get(m)).size();
					 if(size1 <= size2) {
						 tempflag = 1;
					 }
				}
				while ((m > vertexPerLevel.get(j - 1)) &&	tempflag == 1)
				{
					vertexByLevel.set(m + 1,  vertexByLevel.get(m));
					m--;
				}
				vertexByLevel.set(m + 1, key);
			}

			//fill in correct order for the given level, order could be equal
			n = vertexPerLevel.get(j - 1) + 1;
			System.out.println("n:" +n);
			vertexOrder.set(vertexByLevel.get(vertexPerLevel.get(j - 1)+1), n);
			for (k = vertexPerLevel.get(j - 1) + 2; k <= vertexPerLevel.get(j); k++)
			{
				
				int tempflag = 0;
				closureVertex vertex1  =  getVertexWithID(vertexByLevel.get(k - 1)) ;
				closureVertex vertex2 = getVertexWithID(vertexByLevel.get(k)) ;
				if(vertex1.getLabel().get(0).compareTo(vertex2.getLabel().get(0))  < 0) {
					tempflag = 1;
				}else if (vertex1.getLabel().get(0).compareTo(vertex2.getLabel().get(0))  == 0) {
					 int size1 = getNeighbourOf(key).size();
					 int size2 =  getNeighbourOf(vertexByLevel.get(m)).size();
					 if(size1 <= size2) {
						 tempflag = 1;
					 }
				}
				if (tempflag== 1)
					n++;
				vertexOrder.set(vertexByLevel.get(k), n);
			}
		}
	    
		//forth step: compute the automorphism

		automorphism.set(root1, root1);
		if (root2 != -1) //double roots
		{
			if (vertexOrder.get(root1)==vertexOrder.get(root2))
				vertexOrder.set(root2, root1);
			else automorphism.set(root2, root2);
		}

		//for each level, first refine sorting so that the automorphism of 
		//parent plays a role, then compute automorphism according to the order
		for (j = 2; j <= currentLevel; j++)
		{
			//insertion sort
			for (k = vertexPerLevel.get(j - 1) + 2; k <= vertexPerLevel.get(j); k++)
			{
				key = vertexByLevel.get(k);
				m = k - 1;
				while ( (m > vertexPerLevel.get(j-1)) &&
					(      (vertexOrder.get(key)< vertexOrder.get(vertexByLevel.get(m))) || //not likely
					((vertexOrder.get(key) == vertexOrder.get(vertexByLevel.get(m))) &&
						(automorphism.get(parent.get(key))< automorphism.get(parent.get(vertexByLevel.get(m) ))  ))))
				{
					vertexByLevel.set(m + 1, vertexByLevel.get(m));
					m--;
				}
				vertexByLevel.set(m + 1, key);
			}
			automorphism.set(vertexByLevel.get(vertexPerLevel.get(j - 1)+1), vertexByLevel.get(vertexPerLevel.get(j-1)+1));
			for (k = vertexPerLevel.get(j - 1) + 2; k <= vertexPerLevel.get(j); k++)
			{
				int value1 = vertexByLevel.get(k);
				int value2 = vertexByLevel.get(k-1);
				int parent1 = parent.get(value1);
				int parent2 = parent.get(value2);
				if (((vertexOrder.get(value1) == vertexOrder.get(value2)) && (automorphism.get(parent1) == automorphism.get(parent2))))
					   automorphism.set(value1, automorphism.get(value2));
				else automorphism.set(value1, value1);   
			}
		}
	
		//fifth step: compute the canonical string

		Boolean foundIndex = false;
		while (!tempQ.isEmpty()) tempQ.poll(); //clear the queue
		for (int i = 0; i < nodeIDList.size(); i++) tempFlag.set(i, false) ; //clear the flag array

		currentVertex = nodeIDList.size(); //remaining number of vertices
		if (root2 == -1) //single root
		{
			CanonicalString.add(1);
			CanonicalString.add(nodeIDList.size()); //number of vertics in the tree
			tempQ.offer(root1);
			tempQ.offer(-1);
			tempFlag.set(root1, true);
		}
		else
		{
			CanonicalString.add(2);
			CanonicalString.add(nodeIDList.size()); //number of vertics in the tree
			if (vertexOrder.get(root1) <= vertexOrder.get(root2))
			{
				tempQ.offer(root1);
				tempQ.offer(root2);
			}
			else
			{
				tempQ.offer(root2);
				tempQ.offer(root1);
			}
			tempQ.offer(-1);
			tempFlag.set(root1, true);
			tempFlag.set(root2, true);
		}
        
		while (true)
		{
			if (currentVertex == 0) //finished
			{
				CanonicalString.add(ENDOFTREETAG);
				break;
			}
			int i = tempQ.poll();
			if (i == -1)
				CanonicalString.add(ENDTAG);
			else
			{
				int idx  = 0;
			//	if ((idx != 0) && !foundIndex && (automorphism[idx] == automorphism[i]))
			//	{
			//		foundIndex = true;
			//		idx = vCount - currentVertex + 1;
			//	}
				tempFlag.set(i, true);
				
			//	CanonicalString.add(edgeLabelToParent.get(i));
				
				closureVertex vertex  =  getVertexWithID(i) ;
				CanonicalString.add(Integer.valueOf(vertex.getLabel().get(0)));
				int v_id = nodeIDList.get(i);
				t   = getNeighbourOf(v_id);
				 for(int kk=0;kk<t.size();kk++) {
				    	closureVertex v= t.get(kk);
				    	int id = nodeIDList.indexOf(v.getID());
				    	if(tempFlag.get(id) == false) {
				    		tempQ.offer(id);
				    	}
				 }
				tempQ.offer(-1);
				currentVertex--;
			}
		}

		System.out.println("CanonicalString : "+CanonicalString.toString());
	}
	
	
	// may have error if used together with vertex deletion....is based on
	// assumption that graph vertices are not deleted.
	public ArrayList<String> getVertexLabelList() {
		return vertexLabelList;
	}

	// may have error if used together with vertex deletion....is based on
	// assumption that graph vertices are not deleted.
	public ArrayList<ArrayList<closureVertex>> getVertexListGroupedByLabel() {
		return vertexLabelList_listOfVertex;
	}

	public void updateEdgeWeights(ArrayList<String> DBEdgeLabel, ArrayList<Float> DBEdgeLabelWt, String tName) {
		lowerEdgeWtBound = 100;
		upperEdgeWtBound = 0;
		threadName = tName;
		closureEdgeWtList = new ArrayList<Integer>();
		for (int i = 0; i < closureEdgeList.size(); i++) {
			closureEdge e = closureEdgeList.get(i);
			String e_label = e.getEdgeLabelString();
			int DBEdgeLabel_index = DBEdgeLabel.indexOf(e_label);
			float e_label_wt = DBEdgeLabelWt.get(DBEdgeLabel_index);
			float e_cluster_wt = (float) e.getGraphIDList().size() / (float) clusterGraphList.size();
			float w = e_label_wt * e_cluster_wt;
			int int_wt = (int) (w * 100);
			if (int_wt == 0)
				int_wt = 1;
			e.setWeight(int_wt);
			closureEdgeWtList.add(int_wt);
			// System.out.println("Edge "+i+" ["+e_label+"]: e_label_wt="+e_label_wt+"
			// e_cluster_wt="+e_cluster_wt+" w="+(float)w+" int_wt="+int_wt+"
			// "+closureEdgeList.get(i).getWeight());
			if (int_wt < lowerEdgeWtBound)
				lowerEdgeWtBound = int_wt;
			if (int_wt > upperEdgeWtBound)
				upperEdgeWtBound = int_wt;
		}
		// System.out.println("********* lowerEdgeWtBound="+lowerEdgeWtBound);
		// System.out.println("********* upperEdgeWtBound="+upperEdgeWtBound);
	}

	// Cluster with only one graph
	public JGraphtClosureGraph(JGraphtGraph loneGraph, int graphID) {
		// System.out.println("JGraphtClosureGraph graphID:"+graphID);
		// loneGraph.print();
		// System.out.println("-----------------------------------");

		g = new SimpleGraph<closureVertex, closureEdge>(closureEdge.class);
		// mcGregorGraph=new SimpleGraph<StringLabeledObject,
		// StringLabeledObject>(StringLabeledObject.class);
		ArrayList<simpleVertex> loneGraphVertexSet = loneGraph.getNodeSet();
		ArrayList<simpleEdge> loneGraphEdgeSet = loneGraph.getEdgeSet();

		for (int i = 0; i < loneGraphVertexSet.size(); i++) {
			// convert each simpleVertex to closureVertex
			simpleVertex sV = loneGraphVertexSet.get(i);
			ArrayList<String> labelList = new ArrayList<String>();
			labelList.add(sV.getLabel());
			closureVertex cV = new closureVertex(sV.getID(), labelList);
			// now add this closureVertex into the closureGraph
			addNode(cV);
		}
		for (int i = 0; i < loneGraphEdgeSet.size(); i++) {
			// convert each simpleEdge to closureEdge
			simpleEdge sE = loneGraphEdgeSet.get(i);
			int sV_sourceID = sE.getSource().getID();
			int sV_targetID = sE.getTarget().getID();
			int sV_sourceIndex = nodeIDList.indexOf(sV_sourceID);
			int sV_targetIndex = nodeIDList.indexOf(sV_targetID);
			// valid closureVertex
			if (sV_sourceIndex != -1 && sV_targetIndex != -1) {
				closureVertex cV_source = closureVertexList.get(sV_sourceIndex);
				closureVertex cV_target = closureVertexList.get(sV_targetIndex);
				ArrayList<Integer> graphIDList = new ArrayList<Integer>();
				graphIDList.add(graphID);
				closureEdge cE = new closureEdge(cV_source, cV_target, graphIDList);
				addEdge(cV_source, cV_target, cE);
			}
		}
		// put this graphID into the graph ID list of this cluster
		clusterGraphList.add(graphID);
	}

	public int getDegreeOf(closureVertex v) {
		return g.degreeOf(v);
	}

	public void removeEdge(closureVertex v1, closureVertex v2) {
		closureEdge e = getClosureEdge(v1, v2);
		if (e == null) {
			System.out.println("NULL EDGE!!! cannot remove :(");
			return;
		} else {
			g.removeEdge(e);
			closureEdgeList.remove(e);
		}
	}

	public void removeEdge(int v1_id, int v2_id) {
		closureEdge e = getClosureEdge(v1_id, v2_id);
		if (e == null) {
			System.out.println("NULL EDGE!!! cannot remove :(");
			return;
		} else {
			g.removeEdge(e);
			closureEdgeList.remove(e);
		}
	}

	public void removeNode(closureVertex v) {
		g.removeVertex(v);
		closureVertexList.remove(v);
		int indexOfID = nodeIDList.indexOf(v.getID());
		nodeIDList.remove(indexOfID);
	}

	public void removeNode(int v_id) {
		closureVertex v = getVertexWithID(v_id);
		System.out.print("[removeNode] ");
		v.print();
		g.removeVertex(v);
		closureVertexList.remove(v);
		int indexOfID = nodeIDList.indexOf(v_id);
		nodeIDList.remove(indexOfID);
	}

	public synchronized ArrayList<closureEdge> getBestPattern(int patternMinSize, boolean PRINT,
			ArrayList<String> DBVertexLabel, ArrayList<Integer> DBVertexLabelValency) {
		int WALK = 100;
		ArrayList<closureEdge> bestPattern = new ArrayList<closureEdge>();
		ArrayList<ArrayList<closureEdge>> cand_patterns = new ArrayList<ArrayList<closureEdge>>();
		if (closureEdgeList.size() <= patternMinSize) {
			// System.out.println(threadName+":: Oh dear!!!!
			// closureEdgeList.size()<patternMinSize. Let's skip this closure");
			return bestPattern;
		}
		for (int i = 0; i < WALK; i++) {
			ArrayList<closureEdge> currPattern = new ArrayList<closureEdge>();
			ArrayList<String> currPattern_edgeIDLabel = new ArrayList<String>();
			ArrayList<closureVertex> currPattern_vertexList = new ArrayList<closureVertex>();
			// 1. get first edge to start with
			int firstEdgeWt = upperEdgeWtBound;
			closureEdge currEdge;
			ArrayList<Integer> cand_firstEdge = new ArrayList<Integer>();
			ArrayList<Integer> edgeWtList = new ArrayList<Integer>();
			ArrayList<Integer> currPattern_vertexList_neighbourCount = new ArrayList<Integer>();
			for (int w = 0; w < closureEdgeWtList.size(); w++)
				edgeWtList.add(closureEdgeWtList.get(w));

			// System.out.println("-------------- WALK "+i+"------------------------");

			boolean CONTINUE = true;
			while (CONTINUE) {
				int index = edgeWtList.indexOf(firstEdgeWt);
				if (index == -1)
					CONTINUE = false;
				else {
					cand_firstEdge.add(index);
					edgeWtList.set(index, -1);
				}
			}
			if (cand_firstEdge.size() > 0) {
				if (cand_firstEdge.size() == 1) {
					int index = cand_firstEdge.get(0);
					currEdge = closureEdgeList.get(index);
				} else {
					// randomly select one to start with
					Random rand = new Random();
					int size = cand_firstEdge.size();
					int n = rand.nextInt(size);
					int index = cand_firstEdge.get(n);
					currEdge = closureEdgeList.get(index);
				}
				currPattern.add(currEdge);
				currPattern_edgeIDLabel.add(currEdge.getEdgeString());
				closureVertex s = currEdge.getSource();
				closureVertex t = currEdge.getTarget();
				if (currPattern_vertexList.contains(s) == false) {
					currPattern_vertexList.add(s);
					currPattern_vertexList_neighbourCount.add(1);// first edge so currPattern_vertexList_neighbourCount
																	// must be quite empty
					// System.out.println("First EDGE ADDED (FRESH) vertex="+s.getLabel().get(0)+"
					// ID="+s.getID()+" currPattern_vertexList_neighbourCount=1");
				}
				if (currPattern_vertexList.contains(t) == false) {
					currPattern_vertexList.add(t);
					currPattern_vertexList_neighbourCount.add(1);// first edge so currPattern_vertexList_neighbourCount
																	// must be quite empty
					// System.out.println("First EDGE ADDED (FRESH) vertex="+t.getLabel().get(0)+"
					// ID="+t.getID()+" currPattern_vertexList_neighbourCount=1");
				}
				CONTINUE = true;
				for (int p = 0; p < patternMinSize - 1 && CONTINUE; p++) {
					// 2. get candidate neighbouring edges
					ArrayList<closureEdge> cand_nextEdges = new ArrayList<closureEdge>();
					for (int v = 0; v < currPattern_vertexList.size(); v++) {
						closureVertex vertex = currPattern_vertexList.get(v);
						String vertex_label = vertex.getLabel().get(0);
						int vertex_neighbourCount = currPattern_vertexList_neighbourCount.get(v);
						int vertex_label_index = DBVertexLabel.indexOf(vertex_label);
						int vertex_maxValency = 0;
						if (vertex_label_index == -1) {
							System.out.println("vertex_label_index is -1!! ERROR!!!");
							vertex.print();
						} else
							vertex_maxValency = DBVertexLabelValency.get(vertex_label_index);
						// int vertex_maxValency=maxValency[Integer.parseInt(vertex_label)];
						if (vertex_neighbourCount < vertex_maxValency) {
							// System.out.println("size:"+p+" vertex_label="+vertex_label+" vertex
							// ID="+vertex.getID()+" vertex_neighbourCount="+vertex_neighbourCount+"
							// vertex_maxValency="+vertex_maxValency);

							ArrayList<closureEdge> edgesOfVertex = getEdgesOf_complement(vertex,
									currPattern_edgeIDLabel);
							for (int e = 0; e < edgesOfVertex.size(); e++) {
								closureEdge edge = edgesOfVertex.get(e);
								if (cand_nextEdges.contains(edge) == false)
									cand_nextEdges.add(edge);
							}
						}
					}
					if (cand_nextEdges.size() > 0) {
						ArrayList<closureEdge> extendedList = new ArrayList<closureEdge>();
                        ////// according to the weight of each edge, assign multiple copies of this edge in extendedList,  so that the edge with larger weight has larger probability to be selected
						for (int e = 0; e < cand_nextEdges.size(); e++)
							randomlyAddToListBasedOnWeight(extendedList, cand_nextEdges.get(e));
						// System.out.println("extendedList: ++++++++++++++++++++++++++++++++++++++++");
						// for(int e=0; e<extendedList.size(); e++)
						// extendedList.get(e).print();
						// System.out.println("extendedList:
						// *****************************************");

						// 3. random select from list
						Random rand = new Random();
						int size = extendedList.size();
						if (size == 0)
							System.out.println("extendedList of " + threadName + "=> extendedList size=" + extendedList.size());
						int n = rand.nextInt(size);
						currEdge = extendedList.get(n);
						currPattern.add(currEdge);
						currPattern_edgeIDLabel.add(currEdge.getEdgeString());
						s = currEdge.getSource();
						t = currEdge.getTarget();

						int s_index = currPattern_vertexList.indexOf(s);
						if (s_index == -1) {
							currPattern_vertexList.add(s);
							currPattern_vertexList_neighbourCount.add(1);
							// System.out.println("NEW EDGE ADDED (FRESH) vertex="+s.getLabel().get(0)+"
							// ID="+s.getID()+" currPattern_vertexList_neighbourCount=1");
						} else {
							int count = currPattern_vertexList_neighbourCount.get(s_index);
							currPattern_vertexList_neighbourCount.set(s_index, count + 1);
							// System.out.println("NEW EDGE ADDED (EXIST) vertex="+s.getLabel().get(0)+"
							// ID="+s.getID()+"
							// currPattern_vertexList_neighbourCount="+currPattern_vertexList_neighbourCount.get(s_index));
						}
						int t_index = currPattern_vertexList.indexOf(t);
						if (t_index == -1) {
							currPattern_vertexList.add(t);
							currPattern_vertexList_neighbourCount.add(1);
							// System.out.println("NEW EDGE ADDED (FRESH) vertex="+t.getLabel().get(0)+"
							// ID="+t.getID()+" currPattern_vertexList_neighbourCount=1");
						} else {
							int count = currPattern_vertexList_neighbourCount.get(t_index);
							currPattern_vertexList_neighbourCount.set(t_index, count + 1);
							// System.out.println("NEW EDGE ADDED (EXIST) vertex="+t.getLabel().get(0)+"
							// ID="+t.getID()+"
							// currPattern_vertexList_neighbourCount="+currPattern_vertexList_neighbourCount.get(t_index));
						}
						// System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
					} else
						CONTINUE = false;
				}
			}
			// System.out.println("see currPattern "+i+"
			// ********************************************");
			// for(int k=0; k<currPattern.size(); k++)
			// currPattern.get(k).print();
			// System.out.println("#################################################################");
			if (currPattern.size() == patternMinSize)
				cand_patterns.add(currPattern);
		}

		if (cand_patterns.size() > 0) {
			// all closureVertex in cand_patterns
			ArrayList<closureVertex> candPattern_closureVertexList = new ArrayList<closureVertex>();
			// the neighbours of closureVertex in cand_patterns
			ArrayList<ArrayList<closureEdge>> candPattern_closureVertexAdjList = new ArrayList<ArrayList<closureEdge>>();
			// all closureEdge in cand_patterns
			ArrayList<closureEdge> p_closureEdgeList = new ArrayList<closureEdge>();
			// the counts of closureEdge in cand_patterns
			ArrayList<Integer> p_closureEdgeList_freq = new ArrayList<Integer>();

			for (int i = 0; i < cand_patterns.size(); i++) {
				ArrayList<closureEdge> p = cand_patterns.get(i);
				for (int j = 0; j < p.size(); j++) {
					closureEdge e = p.get(j);
					closureVertex s = e.getSource();
					closureVertex t = e.getTarget();
					int index = candPattern_closureVertexList.indexOf(s);
					if (index == -1) {
						candPattern_closureVertexList.add(s);
						ArrayList<closureEdge> vertexAdjList = new ArrayList<closureEdge>();
						vertexAdjList.add(e);
						candPattern_closureVertexAdjList.add(vertexAdjList);
					} else {
						ArrayList<closureEdge> vertexAdjList = candPattern_closureVertexAdjList.get(index);
						if (vertexAdjList.contains(e) == false)
							vertexAdjList.add(e);
						
						
						///////// added by Kai ////// 
						candPattern_closureVertexAdjList.set(index, vertexAdjList);
					}

					index = candPattern_closureVertexList.indexOf(t);
					if (index == -1) {
						candPattern_closureVertexList.add(t);
						ArrayList<closureEdge> vertexAdjList = new ArrayList<closureEdge>();
						vertexAdjList.add(e);
						candPattern_closureVertexAdjList.add(vertexAdjList);
					} else {
						ArrayList<closureEdge> vertexAdjList = candPattern_closureVertexAdjList.get(index);
						if (vertexAdjList.contains(e) == false)
							vertexAdjList.add(e);
						
						
						///////// added by Kai ////// 
						candPattern_closureVertexAdjList.set(index, vertexAdjList);
					}

					index = p_closureEdgeList.indexOf(e);
					if (index == -1) {
						p_closureEdgeList.add(e);
						p_closureEdgeList_freq.add(1);
					} else {
						int count = p_closureEdgeList_freq.get(index);
						p_closureEdgeList_freq.set(index, count + 1);
					}
				}
			}

			ArrayList<closureVertex> bestPattern_vertexList = new ArrayList<closureVertex>();
			ArrayList<Integer> bestPattern_vertexList_neighbourCount = new ArrayList<Integer>();

			for (int i = 0; i < patternMinSize; i++) {
				ArrayList<closureEdge> c_closureEdgeList = new ArrayList<closureEdge>();
				ArrayList<Integer> c_closureEdgeList_freq = new ArrayList<Integer>();

				if (i == 0) {
					int maxCount = Collections.max(p_closureEdgeList_freq);
					int index = p_closureEdgeList_freq.indexOf(maxCount);
					closureEdge currEdge = p_closureEdgeList.get(index);
					bestPattern.add(currEdge);
					closureVertex s = currEdge.getSource();
					closureVertex t = currEdge.getTarget();
					if (bestPattern_vertexList.contains(s) == false) {
						bestPattern_vertexList.add(s);
						bestPattern_vertexList_neighbourCount.add(1);
					}
					if (bestPattern_vertexList.contains(t) == false) {
						bestPattern_vertexList.add(t);
						bestPattern_vertexList_neighbourCount.add(1);
					}
					// Set freq of currEdge to 0
					p_closureEdgeList_freq.set(index, 0);
				} else// collect candidate next edge information
				{
					for (int v = 0; v < bestPattern_vertexList.size(); v++) {
						closureVertex cv = bestPattern_vertexList.get(v);
						String vertex_label = cv.getLabel().get(0);
						int cv_neighbourCount = bestPattern_vertexList_neighbourCount.get(v);
						int vertex_label_index = DBVertexLabel.indexOf(vertex_label);
						int cv_maxValency = 0;
						if (vertex_label_index == -1) {
							System.out.println("vertex_label_index is -1!!! ERROR");
							cv.print();
						} else
							cv_maxValency = DBVertexLabelValency.get(vertex_label_index);
						// int cv_maxValency=maxValency[Integer.parseInt(vertex_label)];
						if (cv_neighbourCount < cv_maxValency) {
							// System.out.println("BEST PATTERN CAND EDGE: vertex_label="+vertex_label+"
							// vertex ID="+cv.getID()+" vertex_neighbourCount="+cv_neighbourCount+"
							// vertex_maxValency="+cv_maxValency);

							int cv_index = candPattern_closureVertexList.indexOf(cv);
							ArrayList<closureEdge> adjList = candPattern_closureVertexAdjList.get(cv_index);
							for (int a = 0; a < adjList.size(); a++) {
								closureEdge adjList_e = adjList.get(a);
								if (c_closureEdgeList.contains(adjList_e) == false) {
									int e_index = p_closureEdgeList.indexOf(adjList_e);
									c_closureEdgeList.add(adjList_e);
									c_closureEdgeList_freq.add(p_closureEdgeList_freq.get(e_index));
								}
							}
						}
					}
					if (c_closureEdgeList_freq.size() > 0) {
						// System.out.println("c_closureEdgeList_freq:
						// "+c_closureEdgeList_freq.toString());
						int maxCount = Collections.max(c_closureEdgeList_freq);
						int index = c_closureEdgeList_freq.indexOf(maxCount);
						closureEdge currEdge = c_closureEdgeList.get(index);
						bestPattern.add(currEdge);
						closureVertex s = currEdge.getSource();
						closureVertex t = currEdge.getTarget();
						int s_index = bestPattern_vertexList.indexOf(s);
						if (s_index == -1) {
							bestPattern_vertexList.add(s);
							bestPattern_vertexList_neighbourCount.add(1);
						} else {
							int count = bestPattern_vertexList_neighbourCount.get(s_index);
							bestPattern_vertexList_neighbourCount.set(s_index, count + 1);
						}
						int t_index = bestPattern_vertexList.indexOf(t);
						if (t_index == -1) {
							bestPattern_vertexList.add(t);
							bestPattern_vertexList_neighbourCount.add(1);
						} else {
							int count = bestPattern_vertexList_neighbourCount.get(t_index);
							bestPattern_vertexList_neighbourCount.set(t_index, count + 1);
						}
						// Set freq of currEdge to 0
						int e_index = p_closureEdgeList.indexOf(currEdge);
						p_closureEdgeList_freq.set(e_index, 0);
					}
				}
			}
		}
		if (PRINT) {
			for (int k = 0; k < bestPattern.size(); k++)
				bestPattern.get(k).print();
		}
		return bestPattern;
	}
	
	public synchronized ArrayList<closureEdge> getBestPattern(int patternMinSize, boolean PRINT,
			ArrayList<String> DBVertexLabel, ArrayList<Integer> DBVertexLabelValency, Trie tree) {
		int WALK = 100;
		ArrayList<closureEdge> bestPattern = new ArrayList<closureEdge>();
		ArrayList<ArrayList<closureEdge>> cand_patterns = new ArrayList<ArrayList<closureEdge>>();
		if (closureEdgeList.size() <= patternMinSize) {
			// System.out.println(threadName+":: Oh dear!!!!
			// closureEdgeList.size()<patternMinSize. Let's skip this closure");
			return bestPattern;
		}
		for (int i = 0; i < WALK; i++) {
			ArrayList<closureEdge> currPattern = new ArrayList<closureEdge>();
			ArrayList<String> currPattern_edgeIDLabel = new ArrayList<String>();
			ArrayList<closureVertex> currPattern_vertexList = new ArrayList<closureVertex>();
			// 1. get first edge to start with
			int firstEdgeWt = upperEdgeWtBound;
			closureEdge currEdge;
			ArrayList<Integer> cand_firstEdge = new ArrayList<Integer>();
			ArrayList<Integer> edgeWtList = new ArrayList<Integer>();
			ArrayList<Integer> currPattern_vertexList_neighbourCount = new ArrayList<Integer>();
			for (int w = 0; w < closureEdgeWtList.size(); w++)
				edgeWtList.add(closureEdgeWtList.get(w));

			// System.out.println("-------------- WALK "+i+"------------------------");

			boolean CONTINUE = true;
			while (CONTINUE) {
				int index = edgeWtList.indexOf(firstEdgeWt);
				if (index == -1)
					CONTINUE = false;
				else {
					cand_firstEdge.add(index);
					edgeWtList.set(index, -1);
				}
			}
			if (cand_firstEdge.size() > 0) {
				if (cand_firstEdge.size() == 1) {
					int index = cand_firstEdge.get(0);
					currEdge = closureEdgeList.get(index);
				} else {
					// randomly select one to start with
					Random rand = new Random();
					int size = cand_firstEdge.size();
					int n = rand.nextInt(size);
					int index = cand_firstEdge.get(n);
					currEdge = closureEdgeList.get(index);
				}
				if(tree.isUSEEDGEPRUNE()) {
					 //System.out.println("Before Edge Pruning : " + currEdge.getEdgeLabelString());
					    int id = tree.getEdges().indexOf(currEdge.getEdgeLabelString());
					    if(id != -1) {
					    	 if(tree.getIsPromisingEgde().get(id)==false) {
					     		 //System.out.println("Edge Pruning");
					    		 continue;
					    	 }
					   }
				}
				currPattern.add(currEdge);
				currPattern_edgeIDLabel.add(currEdge.getEdgeString());
				closureVertex s = currEdge.getSource();
				closureVertex t = currEdge.getTarget();
				if (currPattern_vertexList.contains(s) == false) {
					currPattern_vertexList.add(s);
					currPattern_vertexList_neighbourCount.add(1);// first edge so currPattern_vertexList_neighbourCount
																	// must be quite empty
					// System.out.println("First EDGE ADDED (FRESH) vertex="+s.getLabel().get(0)+"
					// ID="+s.getID()+" currPattern_vertexList_neighbourCount=1");
				}
				if (currPattern_vertexList.contains(t) == false) {
					currPattern_vertexList.add(t);
					currPattern_vertexList_neighbourCount.add(1);// first edge so currPattern_vertexList_neighbourCount
																	// must be quite empty
					// System.out.println("First EDGE ADDED (FRESH) vertex="+t.getLabel().get(0)+"
					// ID="+t.getID()+" currPattern_vertexList_neighbourCount=1");
				}
				CONTINUE = true;
				for (int p = 0; p < patternMinSize - 1 && CONTINUE; p++) {
					// 2. get candidate neighbouring edges
					ArrayList<closureEdge> cand_nextEdges = new ArrayList<closureEdge>();
					for (int v = 0; v < currPattern_vertexList.size(); v++) {
						closureVertex vertex = currPattern_vertexList.get(v);
						String vertex_label = vertex.getLabel().get(0);
						int vertex_neighbourCount = currPattern_vertexList_neighbourCount.get(v);
						int vertex_label_index = DBVertexLabel.indexOf(vertex_label);
						int vertex_maxValency = 0;
						if (vertex_label_index == -1) {
							System.out.println("vertex_label_index is -1!! ERROR!!!");
							vertex.print();
						} else
							vertex_maxValency = DBVertexLabelValency.get(vertex_label_index);
						// int vertex_maxValency=maxValency[Integer.parseInt(vertex_label)];
						if (vertex_neighbourCount < vertex_maxValency) {
							// System.out.println("size:"+p+" vertex_label="+vertex_label+" vertex
							// ID="+vertex.getID()+" vertex_neighbourCount="+vertex_neighbourCount+"
							// vertex_maxValency="+vertex_maxValency);

							ArrayList<closureEdge> edgesOfVertex = getEdgesOf_complement(vertex,
									currPattern_edgeIDLabel);
							for (int e = 0; e < edgesOfVertex.size(); e++) {
								closureEdge edge = edgesOfVertex.get(e);
								if (cand_nextEdges.contains(edge) == false)
									cand_nextEdges.add(edge);
							}
						}
					}
					if (cand_nextEdges.size() > 0) {
						ArrayList<closureEdge> extendedList = new ArrayList<closureEdge>();
                        ////// according to the weight of each edge, assign multiple copies of this edge in extendedList,  so that the edge with larger weight has larger probability to be selected
						for (int e = 0; e < cand_nextEdges.size(); e++)
							randomlyAddToListBasedOnWeight(extendedList, cand_nextEdges.get(e));
						// System.out.println("extendedList: ++++++++++++++++++++++++++++++++++++++++");
						// for(int e=0; e<extendedList.size(); e++)
						// extendedList.get(e).print();
						// System.out.println("extendedList:
						// *****************************************");

						// 3. random select from list
						Random rand = new Random();
						int size = extendedList.size();
						if (size == 0)
							System.out.println("extendedList of " + threadName + "=> extendedList size=" + extendedList.size());
						int n = rand.nextInt(size);
						currEdge = extendedList.get(n);
						if(tree.isUSEEDGEPRUNE()) {
							 //System.out.println("Before Edge Pruning : " + currEdge.getEdgeLabelString());
							    int id = tree.getEdges().indexOf(currEdge.getEdgeLabelString());
							    if(id != -1) {
							    	 if(tree.getIsPromisingEgde().get(id)==false) {
							    		 break;
							    	 }
							   }
						}
						currPattern.add(currEdge);
						currPattern_edgeIDLabel.add(currEdge.getEdgeString());
						s = currEdge.getSource();
						t = currEdge.getTarget();

						int s_index = currPattern_vertexList.indexOf(s);
						if (s_index == -1) {
							currPattern_vertexList.add(s);
							currPattern_vertexList_neighbourCount.add(1);
							// System.out.println("NEW EDGE ADDED (FRESH) vertex="+s.getLabel().get(0)+"
							// ID="+s.getID()+" currPattern_vertexList_neighbourCount=1");
						} else {
							int count = currPattern_vertexList_neighbourCount.get(s_index);
							currPattern_vertexList_neighbourCount.set(s_index, count + 1);
							// System.out.println("NEW EDGE ADDED (EXIST) vertex="+s.getLabel().get(0)+"
							// ID="+s.getID()+"
							// currPattern_vertexList_neighbourCount="+currPattern_vertexList_neighbourCount.get(s_index));
						}
						int t_index = currPattern_vertexList.indexOf(t);
						if (t_index == -1) {
							currPattern_vertexList.add(t);
							currPattern_vertexList_neighbourCount.add(1);
							// System.out.println("NEW EDGE ADDED (FRESH) vertex="+t.getLabel().get(0)+"
							// ID="+t.getID()+" currPattern_vertexList_neighbourCount=1");
						} else {
							int count = currPattern_vertexList_neighbourCount.get(t_index);
							currPattern_vertexList_neighbourCount.set(t_index, count + 1);
							// System.out.println("NEW EDGE ADDED (EXIST) vertex="+t.getLabel().get(0)+"
							// ID="+t.getID()+"
							// currPattern_vertexList_neighbourCount="+currPattern_vertexList_neighbourCount.get(t_index));
						}
						// System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++");
					} else
						CONTINUE = false;
				}
			}
			// System.out.println("see currPattern "+i+"
			// ********************************************");
			// for(int k=0; k<currPattern.size(); k++)
			// currPattern.get(k).print();
			// System.out.println("#################################################################");
			if (currPattern.size() == patternMinSize)
				cand_patterns.add(currPattern);
		}

		if (cand_patterns.size() > 0) {
			// all closureVertex in cand_patterns
			ArrayList<closureVertex> candPattern_closureVertexList = new ArrayList<closureVertex>();
			// the neighbours of closureVertex in cand_patterns
			ArrayList<ArrayList<closureEdge>> candPattern_closureVertexAdjList = new ArrayList<ArrayList<closureEdge>>();
			// all closureEdge in cand_patterns
			ArrayList<closureEdge> p_closureEdgeList = new ArrayList<closureEdge>();
			// the counts of closureEdge in cand_patterns
			ArrayList<Integer> p_closureEdgeList_freq = new ArrayList<Integer>();

			for (int i = 0; i < cand_patterns.size(); i++) {
				ArrayList<closureEdge> p = cand_patterns.get(i);
				for (int j = 0; j < p.size(); j++) {
					closureEdge e = p.get(j);
					closureVertex s = e.getSource();
					closureVertex t = e.getTarget();
					int index = candPattern_closureVertexList.indexOf(s);
					if (index == -1) {
						candPattern_closureVertexList.add(s);
						ArrayList<closureEdge> vertexAdjList = new ArrayList<closureEdge>();
						vertexAdjList.add(e);
						candPattern_closureVertexAdjList.add(vertexAdjList);
					} else {
						ArrayList<closureEdge> vertexAdjList = candPattern_closureVertexAdjList.get(index);
						if (vertexAdjList.contains(e) == false)
							vertexAdjList.add(e);
						
						
						///////// added by Kai ////// 
						candPattern_closureVertexAdjList.set(index, vertexAdjList);
					}

					index = candPattern_closureVertexList.indexOf(t);
					if (index == -1) {
						candPattern_closureVertexList.add(t);
						ArrayList<closureEdge> vertexAdjList = new ArrayList<closureEdge>();
						vertexAdjList.add(e);
						candPattern_closureVertexAdjList.add(vertexAdjList);
					} else {
						ArrayList<closureEdge> vertexAdjList = candPattern_closureVertexAdjList.get(index);
						if (vertexAdjList.contains(e) == false)
							vertexAdjList.add(e);
						
						
						///////// added by Kai ////// 
						candPattern_closureVertexAdjList.set(index, vertexAdjList);
					}

					index = p_closureEdgeList.indexOf(e);
					if (index == -1) {
						p_closureEdgeList.add(e);
						p_closureEdgeList_freq.add(1);
					} else {
						int count = p_closureEdgeList_freq.get(index);
						p_closureEdgeList_freq.set(index, count + 1);
					}
				}
			}

			ArrayList<closureVertex> bestPattern_vertexList = new ArrayList<closureVertex>();
			ArrayList<Integer> bestPattern_vertexList_neighbourCount = new ArrayList<Integer>();

			for (int i = 0; i < patternMinSize; i++) {
				ArrayList<closureEdge> c_closureEdgeList = new ArrayList<closureEdge>();
				ArrayList<Integer> c_closureEdgeList_freq = new ArrayList<Integer>();

				if (i == 0) {
					int maxCount = Collections.max(p_closureEdgeList_freq);
					int index = p_closureEdgeList_freq.indexOf(maxCount);
					closureEdge currEdge = p_closureEdgeList.get(index);
					bestPattern.add(currEdge);
					closureVertex s = currEdge.getSource();
					closureVertex t = currEdge.getTarget();
					if (bestPattern_vertexList.contains(s) == false) {
						bestPattern_vertexList.add(s);
						bestPattern_vertexList_neighbourCount.add(1);
					}
					if (bestPattern_vertexList.contains(t) == false) {
						bestPattern_vertexList.add(t);
						bestPattern_vertexList_neighbourCount.add(1);
					}
					// Set freq of currEdge to 0
					p_closureEdgeList_freq.set(index, 0);
				} else// collect candidate next edge information
				{
					for (int v = 0; v < bestPattern_vertexList.size(); v++) {
						closureVertex cv = bestPattern_vertexList.get(v);
						String vertex_label = cv.getLabel().get(0);
						int cv_neighbourCount = bestPattern_vertexList_neighbourCount.get(v);
						int vertex_label_index = DBVertexLabel.indexOf(vertex_label);
						int cv_maxValency = 0;
						if (vertex_label_index == -1) {
							System.out.println("vertex_label_index is -1!!! ERROR");
							cv.print();
						} else
							cv_maxValency = DBVertexLabelValency.get(vertex_label_index);
						// int cv_maxValency=maxValency[Integer.parseInt(vertex_label)];
						if (cv_neighbourCount < cv_maxValency) {
							// System.out.println("BEST PATTERN CAND EDGE: vertex_label="+vertex_label+"
							// vertex ID="+cv.getID()+" vertex_neighbourCount="+cv_neighbourCount+"
							// vertex_maxValency="+cv_maxValency);

							int cv_index = candPattern_closureVertexList.indexOf(cv);
							ArrayList<closureEdge> adjList = candPattern_closureVertexAdjList.get(cv_index);
							for (int a = 0; a < adjList.size(); a++) {
								closureEdge adjList_e = adjList.get(a);
								if (c_closureEdgeList.contains(adjList_e) == false) {
									int e_index = p_closureEdgeList.indexOf(adjList_e);
									c_closureEdgeList.add(adjList_e);
									c_closureEdgeList_freq.add(p_closureEdgeList_freq.get(e_index));
								}
							}
						}
					}
					if (c_closureEdgeList_freq.size() > 0) {
						// System.out.println("c_closureEdgeList_freq:
						// "+c_closureEdgeList_freq.toString());
						int maxCount = Collections.max(c_closureEdgeList_freq);
						int index = c_closureEdgeList_freq.indexOf(maxCount);
						closureEdge currEdge = c_closureEdgeList.get(index);
						bestPattern.add(currEdge);
						closureVertex s = currEdge.getSource();
						closureVertex t = currEdge.getTarget();
						int s_index = bestPattern_vertexList.indexOf(s);
						if (s_index == -1) {
							bestPattern_vertexList.add(s);
							bestPattern_vertexList_neighbourCount.add(1);
						} else {
							int count = bestPattern_vertexList_neighbourCount.get(s_index);
							bestPattern_vertexList_neighbourCount.set(s_index, count + 1);
						}
						int t_index = bestPattern_vertexList.indexOf(t);
						if (t_index == -1) {
							bestPattern_vertexList.add(t);
							bestPattern_vertexList_neighbourCount.add(1);
						} else {
							int count = bestPattern_vertexList_neighbourCount.get(t_index);
							bestPattern_vertexList_neighbourCount.set(t_index, count + 1);
						}
						// Set freq of currEdge to 0
						int e_index = p_closureEdgeList.indexOf(currEdge);
						p_closureEdgeList_freq.set(e_index, 0);
					}
				}
			}
		}
		if (PRINT) {
			for (int k = 0; k < bestPattern.size(); k++)
				bestPattern.get(k).print();
		}
		return bestPattern;
	}

	// randomly insert into list. The number of times to randomly insert=wt of
	// closureEdge
	private void randomlyAddToListBasedOnWeight(ArrayList<closureEdge> list, closureEdge e) {
		int copies = e.getWeight();
		for (int i = 0; i < copies; i++) {
			if (list.size() == 0)
				list.add(e);
			else {
				// randomly add
				Random rand = new Random();
				int newSize = list.size() + 1;
				int n = rand.nextInt(newSize);
				if (n == list.size())
					list.add(e);
				else
					list.add(n, e);
			}
		}
	}

	public closureVertex getVertexAt(int pos) {
		if (pos >= 0 && pos < getNumNodes()) {
			return closureVertexList.get(pos);
		} else {
			return null;
		}
	}

	public int getNumNodes() {
		return g.vertexSet().size();
	}
	public int getNumEdges() {
		return g.edgeSet().size();
	}

	public void addNode(closureVertex v) {
		// StringLabeledObject slObj=new
		// StringLabeledObject(v.getLabel().get(0),v.getID());

		if (closureVertexList.contains(v) == false) {
			closureVertexList.add(v);
			nodeIDList.add(v.getID());
			g.addVertex(v);
			// mcGregorGraph.addVertex(slObj);
			// stringLabeledObjList.add(slObj);

			String l = v.getLabel().get(0);
			int index = vertexLabelList.indexOf(l);
			if (index == -1) {
				ArrayList<closureVertex> vList = new ArrayList<closureVertex>();
				vertexLabelList.add(l);
				vList.add(v);
				vertexLabelList_listOfVertex.add(vList);
			} else
				vertexLabelList_listOfVertex.get(index).add(v);
			// System.out.println("JGraphtClosureGraph addNode: added vertex:
			// "+v.getLabel().get(0)+"["+v.getID()+"]");
		}
		// else
		// System.out.println("JGraphtClosureGraph addNode: Nothing added. already
		// exists! "+v.getLabel().get(0)+"["+v.getID()+"]");
	}

	public boolean hasNode(closureVertex v) {
		return g.containsVertex(v);
	}

	public boolean hasEdge(closureVertex v1, closureVertex v2) {
		closureEdge e = g.getEdge(v1, v2);
		if (e == null)
			return false;
		else
			return true;
	}

	public void addEdge(closureVertex v1, closureVertex v2, closureEdge e) {
		// System.out.println("JGraphtClosureGraph addEdge:
		// v1=>"+v1.getLabel().get(0)+"["+v1.getID()+"]
		// v2=>"+v2.getLabel().get(0)+"["+v2.getID()+"]");
		// StringLabeledObject slObj_v1=null, slObj_v2=null;
		// int slObj_v1_index, slObj_v2_index;
		// slObj_v1_index=closureVertexList.indexOf(v1);
		// slObj_v2_index=closureVertexList.indexOf(v2);
		// System.out.println("JGraphtClosureGraph addEdge:
		// slObj_v1_index=>"+slObj_v1_index+" slObj_v2_index=>"+slObj_v2_index);
		// System.out.println("JGraphtClosureGraph addEdge: stringLabeledObjList
		// size=>"+stringLabeledObjList.size());
		// slObj_v1=stringLabeledObjList.get(slObj_v1_index);
		// slObj_v2=stringLabeledObjList.get(slObj_v2_index);

		if (closureEdgeList.contains(e) == false) {
			closureEdgeList.add(e);
			g.addEdge(v1, v2, e);
			// mcGregorGraph.addEdge(slObj_v1, slObj_v2, new
			// StringLabeledObject(slObj_v1.getLabel()+","+slObj_v2.getLabel(), -1));
			String e_string = e.getEdgeLabelString();
			if (edgeLabelList.contains(e_string) == false)
				edgeLabelList.add(e_string);
		}
	}

	public int getIndexOfVertex(closureVertex v) {
		return closureVertexList.indexOf(v);
	}

	public ArrayList<Integer> getNeighbourIDsOf(closureVertex v) {
		ArrayList<Integer> neighbourIDList = new ArrayList<Integer>();
		// check if graph contains this vertex. If doesn't contain, return null

		if (g.containsVertex(v) == false) {
			System.out.println("[getNeighbourLabelsOf] g DOESN'T contain closureVertex");
			System.out.println(g.toString());
			v.print();
			return null;
		}
		Set eSet = g.edgesOf(v);
		Iterator i = eSet.iterator();
		while (i.hasNext()) {
			closureEdge e = (closureEdge) i.next();
			closureVertex sV = e.getSource();
			closureVertex tV = e.getTarget();
			ArrayList<Integer> id_list = new ArrayList<Integer>();
			if (sV.getID() != v.getID()) {
				id_list.add(sV.getID());
			}
			if (tV.getID() != v.getID()) {
				id_list.add(tV.getID());
			}
			for (int n = 0; n < id_list.size(); n++) {
				neighbourIDList.add(id_list.get(n));
			}
		}
		return neighbourIDList;
	}

	public ArrayList<String> getNeighbourLabelsOf(closureVertex v) {
		ArrayList<String> neighbourLabelList = new ArrayList<String>();
		// check if graph contains this vertex. If doesn't contain, return null

		if (g.containsVertex(v) == false) {
			System.out.println("[getNeighbourLabelsOf] g DOESN'T contain closureVertex");
			System.out.println(g.toString());
			v.print();
			return null;
		}
		Set eSet = g.edgesOf(v);
		Iterator i = eSet.iterator();
		while (i.hasNext()) {
			closureEdge e = (closureEdge) i.next();
			closureVertex sV = e.getSource();
			closureVertex tV = e.getTarget();
			ArrayList<String> l_list = new ArrayList<String>();
			if (sV.getID() != v.getID()) {
				l_list = sV.getLabel();
			}
			if (tV.getID() != v.getID()) {
				l_list = tV.getLabel();
			}
			for (int n = 0; n < l_list.size(); n++) {
				neighbourLabelList.add(l_list.get(n));
			}
		}
		return neighbourLabelList;
	}

	public ArrayList<closureEdge> getEdgesOf(closureVertex v) {
		ArrayList<closureEdge> edgeSet = new ArrayList<closureEdge>();
		Set eSet = g.edgesOf(v);
		Iterator i = eSet.iterator();
		while (i.hasNext()) {
			edgeSet.add((closureEdge) i.next());
		}
		return edgeSet;
	}

	public ArrayList<closureEdge> getEdgesOf_complement(closureVertex v, ArrayList<String> edgeID_excludeList) {
		ArrayList<closureEdge> edgeSet = new ArrayList<closureEdge>();
		Set eSet = g.edgesOf(v);
		Iterator i = eSet.iterator();
		while (i.hasNext()) {
			closureEdge e = (closureEdge) i.next();
			if (edgeID_excludeList.contains(e.getEdgeString()) == false)
				edgeSet.add(e);
		}
		return edgeSet;
	}

	public ArrayList<closureVertex> getNeighbourOf(closureVertex v) {
		ArrayList<closureVertex> neighbourList = new ArrayList<closureVertex>();
		if (g.containsVertex(v) == true) {
			Set eSet = g.edgesOf(v);
			if (eSet.isEmpty() == false) {
				Iterator i = eSet.iterator();
				while (i.hasNext()) {
					closureEdge e = (closureEdge) i.next();
					closureVertex sV = e.getSource();
					closureVertex tV = e.getTarget();
					if (sV.getID() != v.getID()) {
						neighbourList.add(sV);
					}
					if (tV.getID() != v.getID()) {
						neighbourList.add(tV);
					}
				}
			}
		}
		return neighbourList;
	}

	public ArrayList<closureVertex> getClosureVertexList() {
		return closureVertexList;
	}

	public ArrayList<closureEdge> getClosureEdgeList() {
		return closureEdgeList;
	}

	public closureEdge getClosureEdge(int s1_id, int s2_id) {
		closureVertex s1 = getVertexWithID(s1_id);
		closureVertex s2 = getVertexWithID(s2_id);
		closureEdge e = g.getEdge(s1, s2);
		if (e != null)
			return e;
		else
			return g.getEdge(s2, s1);
	}

	public closureEdge getClosureEdge(closureVertex s1, closureVertex s2) {
		closureEdge e = g.getEdge(s1, s2);
		if (e != null)
			return e;
		else
			return g.getEdge(s2, s1);
	}

	public void addToClusterGraphList(int graphID) {
		if (clusterGraphList.contains(graphID) == false) {
			clusterGraphList.add(graphID);
		}
	}

	public void addToClusterGraphList(ArrayList<Integer> graphIDList) {
		for (int i = 0; i < graphIDList.size(); i++) {
			if (clusterGraphList.contains(graphIDList.get(i)) == false) {
				clusterGraphList.add(graphIDList.get(i));
			}
		}
	}

	public ArrayList<Integer> getClusterGraphList() {
		return clusterGraphList;
	}

	public closureVertex getVertexWithID(int v_id) {
		int index = nodeIDList.indexOf(v_id);
		if (index != -1)
			return closureVertexList.get(index);
		else
			return null;
	}

	public ArrayList<closureVertex> getNeighbourOf(int v_id) {
		int index = nodeIDList.indexOf(v_id);
		if (index != -1) {
			closureVertex v = closureVertexList.get(index);
			return getNeighbourOf(v);
		} else
			return null;
	}

	public void print() {
		System.out.println("clusterGraphList:" + clusterGraphList.toString());
		Set vSet = g.vertexSet();
		Iterator i = vSet.iterator();
		while (i.hasNext()) {
			((closureVertex) i.next()).print();
		}

		Set eSet = g.edgeSet();
		i = eSet.iterator();
		int count = 0;
		while (i.hasNext()) {
			System.out.print("Edge " + (count++));
			((closureEdge) i.next()).print();
		}

		System.out.println("numOrphanEdges=" + numOrphanEdges);
	}

}

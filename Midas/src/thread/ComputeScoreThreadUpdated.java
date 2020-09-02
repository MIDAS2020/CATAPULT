package thread;

import thread.ComputeScoreThreadCompleteListener;
import thread.NotificationThread;
import graph.JGraphtClosureGraph;
import graph.VF2;
import graph.VF2_Match;
import graph.closureEdge;
import graph.closureEdgeComparator;
import graph.closureVertex;
import main.HungarianAlgorithm;
import main.PatternResult;
import main.Trie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author Chua Huey Eng
 */
public class ComputeScoreThreadUpdated extends NotificationThread {
	private String threadName;
	private ArrayList<ArrayList<closureEdge>> GUIPatterns;
	private int CSI_A;
	private ArrayList<Float> c_wt;
	private ArrayList<Integer> patternIndex;
	private ArrayList<ArrayList<closureEdge>> distinctCandidatePatterns;
	private ArrayList<ArrayList<Integer>> distinctCandidatePatterns_closureIndex;
	private ArrayList<ArrayList<closureEdge>> candidatePatterns;
	private ArrayList<Double> candidatePatterns_score;
	private ArrayList<ArrayList<Integer>> candidatePatterns_closureIndex;
	private double GUISize;
	private int numBin;
	private Trie trietree;
    private PatternResult  patternresult;
	private ArrayList<JGraphtClosureGraph>   FCT;
	public ComputeScoreThreadUpdated(ArrayList<Integer> pIndex, ArrayList<ArrayList<closureEdge>> entirePatternList,
			ArrayList<ArrayList<Integer>> entirePatternList_closureIndex,
			ArrayList<ArrayList<closureEdge>> selectedPatterns, ArrayList<Float> closure_wt, int threadNum,
			ComputeScoreThreadCompleteListenerUpdated listener, int numOfPatterns, int binNum,  Trie temptrie) {
		candidatePatterns = new ArrayList<ArrayList<closureEdge>>();
		candidatePatterns_score = new ArrayList<Double>();
		candidatePatterns_closureIndex = new ArrayList<ArrayList<Integer>>();
		patternIndex = pIndex;
		distinctCandidatePatterns = entirePatternList;
		distinctCandidatePatterns_closureIndex = entirePatternList_closureIndex;
		GUIPatterns = selectedPatterns;
		c_wt = closure_wt;
		GUISize = (double) (numOfPatterns);
		numBin = binNum;
		super.addListener(listener);
		threadName = "COMPUTE_SCORE_THREAD_#_" + threadNum;
		trietree = temptrie;

		// System.out.println("Initializing thread "+threadName);
	}

	private int getDistinctVertices(ArrayList<closureEdge> pattern) {
		ArrayList<closureVertex> distinctVertices = new ArrayList<closureVertex>();
		for (int i = 0; i < pattern.size(); i++) {
			closureVertex s = pattern.get(i).getSource();
			closureVertex t = pattern.get(i).getTarget();

			if (distinctVertices.contains(s) == false)
				distinctVertices.add(s);
			if (distinctVertices.contains(t) == false)
				distinctVertices.add(t);
		}

		return distinctVertices.size();
	}

	// fl: edge map, Cl: combine set, l: tree level, g_fl: induced edit cost of fl
	// refer to CSI_GED approach from Karam Gouda et al. ICDE 2016
	/*
	 * private void CSI_backtrack(ArrayList<closureVertex> V1,
	 * ArrayList<closureVertex>V2, ArrayList<closureEdge> E1, ArrayList<closureEdge>
	 * E2, ArrayList<closureEdge> fl, ArrayList<closureEdge> Cl, ArrayList<String>
	 * Cl_label, int l, int g_fl, JGraphtClosureGraph G1, JGraphtClosureGraph G2) {
	 * //System.out.println("CSI_backtrack Cl.size="+Cl.size()); for(int i=0;
	 * i<Cl.size(); i++) { closureEdge e_prime=Cl.get(i); fl.add(e_prime); int
	 * emc=CSI_emc(E1.get(l), e_prime); int g_fl_prime=g_fl+emc;
	 * //System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	 * //System.out.print("E1.get(l):");E1.get(l).print();
	 * //System.out.print("e_prime:");e_prime.print();
	 * //System.out.println("l="+l+" g_fl_prime="+g_fl_prime+" g_fl="
	 * +g_fl+" CSI_emc="+emc);
	 * //System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	 * //System.out.println("Cl edge "+i+" "); //Cl.get(i).print();
	 * 
	 * if(!(e_prime.getSource()==null && e_prime.getTarget()==null)) {
	 * e_prime.markMatched(); String e_prime_label=CSI_getReverseEdgeLabel(e_prime);
	 * int e_prime_r_index=Cl_label.indexOf(e_prime_label); if(e_prime_r_index!=-1)
	 * Cl.get(e_prime_r_index).markMatched();
	 * //System.out.println("matched!! Cl.get(i): "+Cl.get(i).getMatched()
	 * +" e_prime_r_index:"+e_prime_r_index+" e_prime_label:"+e_prime_label);
	 * //Cl.get(e_prime_r_index).print(); //Cl.get(i).print(); }
	 * 
	 * if(l<E1.size()-1) { ArrayList<closureEdge> Pl_prime=new
	 * ArrayList<closureEdge>(); for(int j=0; j<Cl.size(); j++) { closureEdge
	 * e=Cl.get(j); if(e.getMatched()==false) Pl_prime.add(e); }
	 * ArrayList<closureEdge> Cl_prime=CSI_combine(fl, Pl_prime, g_fl_prime, CSI_A,
	 * E1.get(l+1), E1, G1, G2); //ArrayList<closureEdge>
	 * E2_prime=CSI_orderTargetEdges(E2_prime, E1.get(0), G1, G2); ArrayList<String>
	 * Cl_prime_label=CSI_getListLabel(Cl_prime);
	 * CSI_backtrack(V1,V2,E1,E2,fl,Cl_prime,Cl_prime_label,l+1,g_fl_prime,G1,G2); }
	 * else//complete CSI {
	 * //System.out.println("complete CSI!! fl.size="+fl.size()+" E1.size="+E1.size(
	 * )); //for(int f=0; f<fl.size(); f++) //{ // System.out.print("MAPPING: "); //
	 * E1.get(f).print(); // System.out.print("TO: "); // fl.get(f).print(); //} int
	 * V1_V2_differences=Math.abs(V1.size()-V2.size()); int
	 * V2DiffV1MapSize=CSI_getV2DiffV1MapSize(V2, fl, E1); int
	 * lambda=CSI_getNumVerticesNotInCommonStructure(V1, V2, E1, fl);//# vertices
	 * not in common structure int delta=lambda+V2DiffV1MapSize-V1_V2_differences;
	 * //System.out.println("delta="+delta); //int
	 * unmatched=CSI_getUnmatched(Cl,CSI_getListLabel(E2),Cl_label); int
	 * unmatched=E2.size()-fl.size(); int currA=g_fl_prime+unmatched+delta;
	 * //System.out.println("currA="+currA+" g_fl_prime="
	 * +g_fl_prime+" CSI_getUnmatched(E2,Cl)="+unmatched+ //
	 * " lambda="+lambda+" CSI_getV2DiffV1MapSize(V2, fl, E1)="
	 * +V2DiffV1MapSize+" V1_V2_differences="+V1_V2_differences); if(currA<CSI_A)
	 * CSI_A=currA; //System.out.println("A="+CSI_A); } fl.remove(e_prime);
	 * if(!(e_prime.getSource()==null && e_prime.getTarget()==null)) {
	 * e_prime.markUnmatched(); String
	 * e_prime_label=CSI_getReverseEdgeLabel(e_prime); int
	 * e_prime_r_index=Cl_label.indexOf(e_prime_label); if(e_prime_r_index!=-1)
	 * Cl.get(e_prime_r_index).markUnmatched(); }
	 * g_fl_prime=g_fl_prime-CSI_emc(E1.get(l),e_prime); } }
	 * 
	 * //CSI_GED heuristics 1: order target edges in increasing order of a cost
	 * value C(e,e') private ArrayList<closureEdge>
	 * CSI_orderTargetEdges(ArrayList<closureEdge> targetEdges, closureEdge
	 * sourceEdge, JGraphtClosureGraph sourceGraph, JGraphtClosureGraph targetGraph)
	 * { //System.out.println("CSI_orderTargetEdges!!"); ArrayList<closureEdge>
	 * orderedTargetEdges=new ArrayList<closureEdge>(); closureEdgeComparator
	 * comparator=new closureEdgeComparator(); PriorityQueue<closureEdge> pq=new
	 * PriorityQueue<closureEdge>(targetEdges.size()-1, comparator);
	 * 
	 * for(int i=0; i<targetEdges.size(); i++) { closureEdge e=targetEdges.get(i);
	 * int costValue=CSI_costValue(sourceEdge, e, sourceGraph, targetGraph);
	 * if(!(e.getSource()==null && e.getTarget()==null)) {
	 * targetEdges.get(i).setCostValue(costValue); pq.add(targetEdges.get(i)); } }
	 * 
	 * // to retrieve the elements in order while (!pq.isEmpty()) { closureEdge
	 * e=pq.poll(); orderedTargetEdges.add(e); //e.print(); }
	 * orderedTargetEdges.add(new closureEdge(null,null));
	 * 
	 * return orderedTargetEdges; }
	 * 
	 * private ArrayList<String> CSI_getListLabel(ArrayList<closureEdge> edgeList) {
	 * ArrayList<String> labelList=new ArrayList<String>();
	 * 
	 * for(int i=0; i<edgeList.size(); i++) { closureEdge e=edgeList.get(i);
	 * if(!(e.getSource()==null && e.getTarget()==null)) { String
	 * label=edgeList.get(i).getEdgeUnorderedString(); labelList.add(label); } else
	 * { labelList.add("NULL"); } }
	 * //System.out.println("[CSI_getListLabel] labelList:"+labelList.toString());
	 * return labelList; }
	 * 
	 * private int CSI_costValue(closureEdge e, closureEdge e_prime,
	 * JGraphtClosureGraph G1, JGraphtClosureGraph G2) {
	 * if(e_prime.getSource()==null && e_prime.getTarget()==null) return 1;
	 * 
	 * int C=0; int emc=CSI_emc(e,e_prime); ArrayList<String>
	 * V1_prime_label=getComplementVerticesLabel(e.getSource(), e.getTarget(),
	 * G1.getClosureVertexList()); ArrayList<String>
	 * V2_prime_label=getComplementVerticesLabel(e_prime.getSource(),
	 * e_prime.getTarget(), G2.getClosureVertexList()); int
	 * V1_prime_label_num=V1_prime_label.size(); int
	 * V2_prime_label_num=V2_prime_label.size();
	 * V1_prime_label.retainAll(V2_prime_label); int V_common=V1_prime_label.size();
	 * int VLabelDifference=V1_prime_label_num+V2_prime_label_num-2*V_common;
	 * C=emc+VLabelDifference;
	 * 
	 * return C; }
	 * 
	 * private String CSI_getReverseEdgeLabel(closureEdge e) { closureVertex
	 * s=e.getSource(); closureVertex t=e.getTarget(); closureEdge e_r=new
	 * closureEdge(t,s); return e_r.getEdgeUnorderedString(); }
	 * 
	 * 
	 * private boolean CSI_validExtension(ArrayList<closureEdge> matchedEdges,
	 * closureEdge unmatched_e, ArrayList<closureEdge> toMatchEdges) { //check
	 * adjacency: if two edges are adjacent in the source graph, they must also be
	 * adjacent on the target graph int toMatchIndex=matchedEdges.size();
	 * closureEdge toMatchEdge=toMatchEdges.get(toMatchIndex); ArrayList<Integer>
	 * indexOf_adjacentEdgesOfToMatchEdge=new ArrayList<Integer>(); closureVertex
	 * toMatchEdge_sourceVertex=toMatchEdge.getSource(); closureVertex
	 * toMatchEdge_targetVertex=toMatchEdge.getTarget(); closureVertex
	 * unmatchedEdge_sourceVertex=unmatched_e.getSource(); closureVertex
	 * unmatchedEdge_targetVertex=unmatched_e.getTarget();
	 * 
	 * //check if preceding matched edges in toMatchEdges (G1=(V1,E1)) are adjacent
	 * to toMatchEdge for(int i=0; i<toMatchIndex; i++) { closureEdge
	 * e=toMatchEdges.get(i); if(e.getSource()!=null && e.getTarget()!=null &&
	 * (e.getSource().getID()==toMatchEdge_sourceVertex.getID() ||
	 * e.getSource().getID()==toMatchEdge_targetVertex.getID() ||
	 * e.getTarget().getID()==toMatchEdge_sourceVertex.getID() ||
	 * e.getTarget().getID()==toMatchEdge_targetVertex.getID()))
	 * indexOf_adjacentEdgesOfToMatchEdge.add(i); } //there are some matched
	 * adjacent edges of toMatchEdge if(indexOf_adjacentEdgesOfToMatchEdge.size()>0
	 * && unmatchedEdge_sourceVertex!=null && unmatchedEdge_targetVertex!=null) {
	 * //check that they correspond to the adjacency condition in G2 for(int i=0;
	 * i<indexOf_adjacentEdgesOfToMatchEdge.size(); i++) { closureEdge
	 * e=matchedEdges.get(indexOf_adjacentEdgesOfToMatchEdge.get(i));
	 * if(e.getSource()!=null && e.getTarget()!=null &&
	 * !(e.getSource().getID()==unmatchedEdge_sourceVertex.getID() ||
	 * e.getSource().getID()==unmatchedEdge_targetVertex.getID() ||
	 * e.getTarget().getID()==unmatchedEdge_sourceVertex.getID() ||
	 * e.getTarget().getID()==unmatchedEdge_targetVertex.getID())) return false; } }
	 * 
	 * return true; }
	 * 
	 * //fl_prime: matched, Pl_prime: unmatched private ArrayList<closureEdge>
	 * CSI_combine(ArrayList<closureEdge> fl_prime, ArrayList<closureEdge> Pl_prime,
	 * int g_fl_prime, int A, closureEdge lookAheadEdge, ArrayList<closureEdge> E,
	 * JGraphtClosureGraph G1, JGraphtClosureGraph G2) { ArrayList<closureEdge>
	 * C=new ArrayList<closureEdge>(); for(int i=0; i<Pl_prime.size(); i++) {
	 * closureEdge e=Pl_prime.get(i); if(CSI_validExtension(fl_prime, e, E)==true &&
	 * g_fl_prime+CSI_emc(lookAheadEdge,e)<A) C.add(e); } C.add(new
	 * closureEdge(null, null));
	 * 
	 * //System.out.println("CSI_combine ++++++++++++++++++++++++++++"); //for(int
	 * i=0; i<C.size(); i++) // C.get(i).print(); return C; }
	 * 
	 * private int CSI_getV2DiffV1MapSize(ArrayList<closureVertex> vList,
	 * ArrayList<closureEdge> mappedEdge, ArrayList<closureEdge> eList) {
	 * ArrayList<closureVertex> mappedVertex=new ArrayList<closureVertex>();
	 * ArrayList<closureVertex> V2=new ArrayList<closureVertex>();
	 * //System.out.println("CSI_getV2DiffV1MapSize V2:"); for(int i=0;
	 * i<vList.size(); i++) { V2.add(vList.get(i)); //vList.get(i).print(); }
	 * //System.out.println("CSI_getV2DiffV1MapSize mappedEdge:"); for(int i=0;
	 * i<mappedEdge.size(); i++) { closureEdge e=mappedEdge.get(i); //e.print();
	 * if(!(e.getSource()==null && e.getTarget()==null)) { closureVertex
	 * s=e.getSource(); closureVertex t=e.getTarget();
	 * if(mappedVertex.contains(s)==false) mappedVertex.add(s);
	 * if(mappedVertex.contains(t)==false) mappedVertex.add(t); } }
	 * V2.removeAll(mappedVertex);
	 * //System.out.println("CSI_getV2DiffV1MapSize V2\f(V1)="+V2.size()); return
	 * V2.size(); }
	 * 
	 * private int CSI_getNumVerticesNotInCommonStructure(ArrayList<closureVertex>
	 * V1, ArrayList<closureVertex> V2, ArrayList<closureEdge> originalEdge,
	 * ArrayList<closureEdge> mappedEdges) { ArrayList<String> L_inV1NotInV=new
	 * ArrayList<String>(); ArrayList<String> L_inFV1NotInV=new ArrayList<String>();
	 * int L_inV1NotInV_size, L_inFV1NotInV_size, L_common_size;
	 * ArrayList<closureVertex> InV1NotInV=new ArrayList<closureVertex>();
	 * ArrayList<closureVertex> InV2NotInV=new ArrayList<closureVertex>();
	 * ArrayList<closureVertex> V=new ArrayList<closureVertex>();
	 * 
	 * //retrieve vertices in V1\V for(int i=0; i<mappedEdges.size(); i++) {
	 * closureEdge V1_mappedEdge=originalEdge.get(i); closureEdge
	 * V2_mappedEdge=mappedEdges.get(i); if(!(V2_mappedEdge.getSource()==null &&
	 * V2_mappedEdge.getTarget()==null)) { closureVertex
	 * s=V1_mappedEdge.getSource(); closureVertex t=V1_mappedEdge.getTarget();
	 * if(V.contains(s)==false) V.add(s); if(V.contains(t)==false) V.add(t); } }
	 * for(int i=0; i<V1.size(); i++) InV1NotInV.add(V1.get(i));
	 * InV1NotInV.removeAll(V); //retrieve labels of V1\V (i.e., L(V1\V))
	 * if(InV1NotInV.size()>0) { for(int i=0; i<InV1NotInV.size(); i++) {
	 * closureVertex v=InV1NotInV.get(i); L_inV1NotInV.add(v.getLabel().get(0)); } }
	 * L_inV1NotInV_size=L_inV1NotInV.size(); //retrieve vertices in f(V1)\V V=new
	 * ArrayList<closureVertex>(); for(int i=0; i<mappedEdges.size(); i++) {
	 * closureEdge V2_mappedEdge=mappedEdges.get(i);
	 * if(!(V2_mappedEdge.getSource()==null && V2_mappedEdge.getTarget()==null)) {
	 * closureVertex s=V2_mappedEdge.getSource(); closureVertex
	 * t=V2_mappedEdge.getTarget(); if(V.contains(s)==false) V.add(s);
	 * if(V.contains(t)==false) V.add(t); } } for(int i=0; i<V2.size(); i++)
	 * InV2NotInV.add(V2.get(i)); InV2NotInV.removeAll(V); //retrieve labels of
	 * f(V1)\V (i.e., L(f(V1)\V)) if(InV2NotInV.size()>0) { for(int i=0;
	 * i<InV2NotInV.size(); i++) { closureVertex v=InV2NotInV.get(i);
	 * L_inFV1NotInV.add(v.getLabel().get(0)); } }
	 * L_inFV1NotInV_size=L_inFV1NotInV.size();
	 * 
	 * //System.out.println("+++ L_inV1NotInV: ["+L_inV1NotInV.size()+"]"+
	 * L_inV1NotInV.toString());
	 * //System.out.println("=== L_inFV1NotInV: ["+L_inFV1NotInV.size()+"]"+
	 * L_inFV1NotInV.toString());
	 * 
	 * //retrieve common labels - retainAll not working as expected
	 * ArrayList<String> smallList, bigList;
	 * if(L_inV1NotInV.size()>L_inFV1NotInV.size()) { bigList=L_inV1NotInV;
	 * smallList=L_inFV1NotInV; } else { bigList=L_inFV1NotInV;
	 * smallList=L_inV1NotInV; } ArrayList<String> common_smallList_index=new
	 * ArrayList<String>(); for(int i=0; i<smallList.size(); i++) { String
	 * s=smallList.get(i); int index=bigList.indexOf(s); if(index!=-1) {
	 * common_smallList_index.add(s); bigList.remove(index); } }
	 * L_common_size=common_smallList_index.size();
	 * 
	 * //System.out.println("%%% common: ["+common_smallList_index.size()+"]"+
	 * common_smallList_index.toString());
	 * 
	 * //System.out.println("V2 mapped vertices===============================:");
	 * //for(int i=0; i<V.size(); i++) // V.get(i).print();
	 * //System.out.println("InV2NotInV vertices+++++++++++++++++++++++++++++++:");
	 * //for(int i=0; i<InV2NotInV.size(); i++) // InV2NotInV.get(i).print();
	 * 
	 * 
	 * return L_inV1NotInV_size+L_inFV1NotInV_size-2*L_common_size; }
	 * 
	 * //return the edge matching cost of assigning e_prime to e private int
	 * CSI_emc(closureEdge e, closureEdge e_prime) { int emc=1;
	 * 
	 * if(e_prime.getSource()==null && e_prime.getTarget()==null) return
	 * emc;//equivalent to deleting edge e else { //c(u->v)+c(u'->v')+c(e->e') //if
	 * l(u)<>l(v), c=1. Otherwise c=0. Since edge has no label in the network we
	 * use, c(e->e')=0 by default closureVertex u=e.getSource(); closureVertex
	 * u_prime=e.getTarget(); closureVertex v=e_prime.getSource(); closureVertex
	 * v_prime=e_prime.getTarget(); String u_label=u.getLabel().get(0); String
	 * u_prime_label=u_prime.getLabel().get(0); String v_label=v.getLabel().get(0);
	 * String v_prime_label=v_prime.getLabel().get(0);
	 * if(u_label.compareTo(v_label)==0) emc=0; else emc=1;
	 * if(u_prime_label.compareTo(v_prime_label)==0) emc=emc+0; else emc=emc+1; }
	 * return emc; }
	 * 
	 * private ArrayList<String> getComplementVerticesLabel(closureVertex v1,
	 * closureVertex v2, ArrayList<closureVertex> vList) { ArrayList<String>
	 * vListComplement_label=new ArrayList<String>();
	 * 
	 * for(int i=0; i<vList.size(); i++) { closureVertex v=vList.get(i);
	 * if(v.getID()!=v1.getID()&& vList.get(i).getID()!=v2.getID())
	 * vListComplement_label.add(v.getLabel().get(0)); }
	 * 
	 * return vListComplement_label; }
	 * 
	 * private ArrayList<closureEdge> CSI_getE2Prime(ArrayList<closureEdge> eList) {
	 * ArrayList<closureEdge> eList_prime=new ArrayList<closureEdge>();
	 * 
	 * for(int i=0; i<eList.size(); i++) { closureEdge e=eList.get(i); closureVertex
	 * s=e.getSource(); closureVertex t=e.getTarget(); ArrayList<Integer>
	 * graphList=e.getGraphIDList(); int wt=e.getWeight(); closureEdge e_r=new
	 * closureEdge(t,s,graphList,wt); eList_prime.add(e); eList_prime.add(e_r); }
	 * closureVertex s=null; closureVertex t=null; eList_prime.add(new
	 * closureEdge(s,t));
	 * 
	 * //for(int i=0; i<eList_prime.size(); i++) //{ // if(eList_prime.get(i)!=null)
	 * // { // System.out.println("CSI_getE2Prime i="+i); //
	 * eList_prime.get(i).print(); // } // else // { //
	 * System.out.println("CSI_getE2Prime i="+i+" IS NULL"); // } //} return
	 * eList_prime; }
	 * 
	 * //we use the CSI_GED approach from Karam Gouda et al. ICDE 2016 private int
	 * PM_computeExactGED(JGraphtClosureGraph G1, JGraphtClosureGraph G2) {
	 * ArrayList<closureVertex> V1=G1.getClosureVertexList();
	 * ArrayList<closureVertex> V2=G2.getClosureVertexList(); ArrayList<closureEdge>
	 * E1=G1.getClosureEdgeList(); ArrayList<closureEdge>
	 * E2=G2.getClosureEdgeList(); ArrayList<closureEdge>
	 * E2_prime=CSI_getE2Prime(E2); ArrayList<closureEdge> fl=new
	 * ArrayList<closureEdge>(); ArrayList<String> E2_prime_label=new
	 * ArrayList<String>(); CSI_A = Integer.MAX_VALUE;//A=infinity...some
	 * ridiculously large number (initial upper bound on GED(G1,G2) int IC =
	 * Math.abs(V1.size()-V2.size());//initial edit cost for each CSI
	 * E2_prime=CSI_orderTargetEdges(E2_prime, E1.get(0), G1, G2);
	 * E2_prime_label=CSI_getListLabel(E2_prime); //System.out.
	 * println("---------------- PM_computeExactGED ---------------------");
	 * //System.out.println("********* E1 ********"); //for(int i=0; i<E1.size();
	 * i++) // E1.get(i).print(); //System.out.println("********* E2 ********");
	 * //for(int i=0; i<E2.size(); i++) // E2.get(i).print();
	 * //System.out.println("*********************");
	 * 
	 * CSI_backtrack(V1,V2,E1,E2,fl,E2_prime,E2_prime_label,0,IC,G1,G2);
	 * System.out.println("********************* CSI_backtrack done! E1.size()="+E1.
	 * size()+" E2.size()="+E2.size()+" CSI_A="+CSI_A);
	 * 
	 * return CSI_A; }
	 * 
	 * private int PM_computeGED(Float expectedGdGED,
	 * ArrayList<ArrayList<closureEdge>> GUIPatterns, ArrayList<closureEdge>
	 * candPattern) { int GED=Integer.MAX_VALUE; ArrayList<Integer> GED_fl=new
	 * ArrayList<Integer>();
	 * 
	 * //only 1 selected pattern in GUIPatterns, no need to compute lower bound, go
	 * directly to compute exact GED if(GUIPatterns.size()==1) return
	 * PM_computeExactGED(new JGraphtClosureGraph(GUIPatterns.get(0)), new
	 * JGraphtClosureGraph(candPattern)); else//1. do fast loose GED lower bound
	 * first (GED_fl) { JGraphtClosureGraph candPatternCGraph=new
	 * JGraphtClosureGraph(candPattern); ArrayList<closureVertex>
	 * candPatternCGraph_closureVertexList=candPatternCGraph.getClosureVertexList();
	 * int candPatternCGraph_numEdges=candPatternCGraph.getClosureEdgeList().size();
	 * int candPatternCGraph_numVertices=candPatternCGraph_closureVertexList.size();
	 * ArrayList<String> candPatternCGraph_closureVertexLabelList=new
	 * ArrayList<String>(); for(int i=0;
	 * i<candPatternCGraph_closureVertexList.size(); i++) { closureVertex
	 * v=candPatternCGraph_closureVertexList.get(i);
	 * candPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0)); } for(int
	 * i=0; i<GUIPatterns.size(); i++) { ArrayList<closureEdge>
	 * sPattern=GUIPatterns.get(i); JGraphtClosureGraph sPatternCGraph=new
	 * JGraphtClosureGraph(sPattern); ArrayList<closureVertex>
	 * sPatternCGraph_closureVertexList=sPatternCGraph.getClosureVertexList(); int
	 * sPatternCGraph_numEdges=sPatternCGraph.getClosureEdgeList().size(); int
	 * sPatternCGraph_numVertices=sPatternCGraph_closureVertexList.size();
	 * ArrayList<String> sPatternCGraph_closureVertexLabelList=new
	 * ArrayList<String>(); for(int j=0; j<sPatternCGraph_closureVertexList.size();
	 * j++) { closureVertex v=sPatternCGraph_closureVertexList.get(j);
	 * sPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0)); } //get
	 * common vertex labels sPatternCGraph_closureVertexLabelList.retainAll(
	 * candPatternCGraph_closureVertexLabelList); int
	 * nodesToAddRemoveRename=Math.abs(sPatternCGraph_numVertices-
	 * candPatternCGraph_numVertices)+ (Math.min(sPatternCGraph_numVertices,
	 * candPatternCGraph_numVertices)-sPatternCGraph_closureVertexLabelList.size());
	 * int
	 * edgesToAddRemove=Math.abs(sPatternCGraph_numEdges-candPatternCGraph_numEdges)
	 * ; GED_fl.add(nodesToAddRemoveRename+edgesToAddRemove);
	 * 
	 * //print candPattern
	 * //System.out.println("PM_computeGED ------------- candPattern: "); //for(int
	 * c=0; c<candPattern.size(); c++) // candPattern.get(c).print();
	 * //System.out.println("sPattern: "); //for(int c=0; c<sPattern.size(); c++) //
	 * sPattern.get(c).print();
	 * //System.out.println("GED_fl="+(nodesToAddRemoveRename+edgesToAddRemove)); }
	 * //ArrayList<Float> sorted_GED_fl=new ArrayList<Float>(); //for(int i=0;
	 * i<GED_fl.size(); i++)
	 * 
	 * //Collections.sort(GED_fl);
	 * System.out.println("GED_fl list: "+GED_fl.toString());
	 * System.out.println("min GED_fl: "+Collections.min(GED_fl)+" max GED_fl: "
	 * +Collections.max(GED_fl));
	 * 
	 * int min_GED_fl=Collections.min(GED_fl); int index=GED_fl.indexOf(min_GED_fl);
	 * int counter=0; boolean CONTINUE=true; while(CONTINUE) { counter++;
	 * //System.out.println("%%%%%%%%%%%%  PM_computeExactGED"); int
	 * curr_GED=PM_computeExactGED(new JGraphtClosureGraph(GUIPatterns.get(index)),
	 * new JGraphtClosureGraph(candPattern));
	 * //System.out.println("%%%%%%%%%%%%  curr_GED="+curr_GED); if(curr_GED<GED) {
	 * GED=curr_GED; if(GED<expectedGdGED) { CONTINUE=false;
	 * System.out.println("Can STOP GED computation!!!"); }
	 * //System.out.println("!!!!!!!!! updated GED to "+GED); } if(CONTINUE) {
	 * for(int i=0; i<GED_fl.size(); i++) { int v=GED_fl.get(i); if(v>=curr_GED &&
	 * v!=Integer.MAX_VALUE) GED_fl.set(i, Integer.MAX_VALUE); } GED_fl.set(index,
	 * Integer.MAX_VALUE); min_GED_fl=Collections.min(GED_fl);
	 * if(min_GED_fl==Integer.MAX_VALUE) CONTINUE=false; else
	 * index=GED_fl.indexOf(min_GED_fl); } }
	 * //System.out.println("************   counter="+counter);
	 * 
	 * } //System.out.println(">>>>>>>>>>>>>>   GED="+GED); return GED; }
	 * 
	 * 
	 * private void computeScore() {
	 * System.out.println("Starting to run compute score thread "+threadName); Float
	 * maxScore=0f; Float minGED_withBetterScore=0f; for(int i=0;
	 * i<patternIndex.size(); i++) { ArrayList<Integer>
	 * dPattern_closureIndexList=distinctCandidatePatterns_closureIndex.get(
	 * patternIndex.get(i));
	 * //System.out.println("dPattern_closureIndexList: "+dPattern_closureIndexList.
	 * toString()); int firstClosureIndex=dPattern_closureIndexList.get(0); Float
	 * clusterWt=c_wt.get(firstClosureIndex); if(dPattern_closureIndexList.size()>0)
	 * { for(int l=0; l<dPattern_closureIndexList.size(); l++) { int
	 * cIndex=dPattern_closureIndexList.get(l);
	 * clusterWt=clusterWt+c_wt.get(cIndex); } } Float minGED=0F; Float
	 * size=(float)(distinctCandidatePatterns.get(patternIndex.get(i)).size());
	 * Float numEdge=size; Float
	 * numVertex=(float)(getDistinctVertices(distinctCandidatePatterns.get(
	 * patternIndex.get(i)))); Float cognitiveCost=1f; Float
	 * density=2*numEdge/(numVertex*(numVertex-1)); Float score; if(size>=6)
	 * cognitiveCost=size*density;
	 * //System.out.println("cognitiveCost="+cognitiveCost);
	 * if(GUIPatterns.size()==0) minGED=1F; else { if(maxScore!=0f)
	 * minGED_withBetterScore=maxScore*cognitiveCost/clusterWt;
	 * minGED=(float)PM_computeGED(minGED_withBetterScore, GUIPatterns,
	 * distinctCandidatePatterns.get(patternIndex.get(i)));
	 * //System.out.println("&&&&&&&&&&&&&&&       minGED="+minGED); }
	 * score=clusterWt*minGED/cognitiveCost; if(score>maxScore) maxScore=score;
	 * if(score!=null) {
	 * candidatePatterns.add(distinctCandidatePatterns.get(patternIndex.get(i)));
	 * candidatePatterns_closureIndex.add(dPattern_closureIndexList);
	 * candidatePatterns_score.add(score); } }
	 * //System.out.println("Finishing compute score thread "
	 * +threadName+" candidatePatterns: "+candidatePatterns.size()
	 * +" candidatePatterns_score:"+candidatePatterns_score.toString()); //for(int
	 * i=0; i<candidatePatterns.size(); i++) //
	 * System.out.println(threadName+" "+i+"of "+candidatePatterns.size()
	 * +" candidatePatterns_score="+candidatePatterns_score.get(i)); }
	 */

	private double[][] createCostArray(double[][] costArr, JGraphtClosureGraph g1, JGraphtClosureGraph g2) {
		ArrayList<closureVertex> n1 = g1.getClosureVertexList();
		ArrayList<closureVertex> n2 = g2.getClosureVertexList();

		for (int i = 0; i < n1.size(); i++) {
			closureVertex n1_node = n1.get(i);
			String n1_nodeLabel = n1_node.getLabel().get(0);
			ArrayList<closureEdge> n1_node_adjEdges = g1.getEdgesOf(n1_node);
			ArrayList<String> n1_node_adjEdges_label = new ArrayList<String>();
			for (int e_index = 0; e_index < n1_node_adjEdges.size(); e_index++) {
				closureEdge e = n1_node_adjEdges.get(e_index);
				closureVertex s = e.getSource();
				closureVertex t = e.getTarget();
				if (s.getID() != n1_node.getID())
					n1_node_adjEdges_label.add(s.getLabel().get(0));
				else
					n1_node_adjEdges_label.add(t.getLabel().get(0));
			}
			int n1_node_adjEdges_label_size = n1_node_adjEdges_label.size();
			for (int j = 0; j < n2.size(); j++) {
				closureVertex n2_node = n2.get(j);
				// System.out.println("assigning x to y");
				// n1_node.print();
				// n2_node.print();

				String n2_nodeLabel = n2_node.getLabel().get(0);
				double cost = 0f;
				if (n1_nodeLabel.compareTo(n2_nodeLabel) != 0)
					cost += 1f;
				ArrayList<closureEdge> n2_node_adjEdges = g2.getEdgesOf(n2_node);
				ArrayList<String> n2_node_adjEdges_label = new ArrayList<String>();
				for (int e_index = 0; e_index < n2_node_adjEdges.size(); e_index++) {
					closureEdge e = n2_node_adjEdges.get(e_index);
					closureVertex s = e.getSource();
					closureVertex t = e.getTarget();
					if (s.getID() != n2_node.getID())
						n2_node_adjEdges_label.add(s.getLabel().get(0));
					else
						n2_node_adjEdges_label.add(t.getLabel().get(0));
				}
				int n2_node_adjEdges_label_size = n2_node_adjEdges_label.size();
				// System.out.println("n1_node_adjEdges_label:"+n1_node_adjEdges_label.toString()+"
				// n2_node_adjEdges_label:"+n2_node_adjEdges_label.toString());
				int commonLabelCount = 0;
				for (int k = 0; k < n1_node_adjEdges_label.size(); k++) {
					String curr_n1_label = n1_node_adjEdges_label.get(k);
					int index = n2_node_adjEdges_label.indexOf(curr_n1_label);
					if (index != -1) {
						n2_node_adjEdges_label.remove(index);
						commonLabelCount++;
					}
				}
				// Huey 
				//	cost = cost + n1_node_adjEdges_label_size + n2_node_adjEdges_label_size - 2 * commonLabelCount;
					
					//Kai
				cost = cost + n1_node_adjEdges_label_size + n2_node_adjEdges_label_size - 2 * commonLabelCount  + 2*Math.abs(g1.getDegreeOf(n1_node) - g2.getDegreeOf(n2_node)) ; 
				
				costArr[i][j] = cost;
			}
		}
		// for(int i=0; i<costArr.length; i++)
		// {
		// ArrayList<Double> c=new ArrayList<Double>();
		// for(int j=0; j<costArr[0].length; j++)
		// c.add(costArr[i][j]);
		// System.out.println("costArr "+i+": "+c.toString()) ;
		// }
		return costArr;
	}

	private int munkres_step1a(int step, double[][] cost) {
		// What STEP 1 does: For each row of the cost matrix, find the smallest element
		// and subtract it from from every other element in its row.
		double minval;

		for (int i = 0; i < cost.length; i++) {
			minval = cost[i][0];
			for (int j = 0; j < cost[i].length; j++) // 1st inner loop finds min val in row.
			{
				if (minval > cost[i][j])
					minval = cost[i][j];
			}
			// System.out.print("minval="+minval);
			if (minval > 0f) {
				for (int j = 0; j < cost[i].length; j++) // 2nd inner loop subtracts it.
					cost[i][j] = cost[i][j] - minval;
			}
		}

		step = 11;
		return step;
	}

	// Aux for munkres_step2, munkres_step5.
	private void clearCovers(int[] rowCover, int[] colCover) {
		for (int i = 0; i < rowCover.length; i++)
			rowCover[i] = 0;
		for (int j = 0; j < colCover.length; j++)
			colCover[j] = 0;
	}

	private int munkres_step1b(int step, double[][] cost) {
		// What STEP 2 does: For each column find the lowest element and subtract it
		// from each element in that column.
		double minval;

		for (int i = 0; i < cost[0].length; i++)// for each column
		{
			minval = cost[0][i];
			for (int j = 0; j < cost.length; j++) // finds min element of each column
			{
				if (minval > cost[j][i])
					minval = cost[j][i];
			}
			// System.out.println("minval="+minval);

			if (minval > 0f) {
				for (int j = 0; j < cost.length; j++) // 2nd inner loop subtracts it.
					cost[j][i] = cost[j][i] - minval;
			}
		}
		step = 2;
		return step;
	}

	private int munkres_step2(int step, double[][] cost, int[][] mask, int[] rowCover, int[] colCover) {
		// What STEP 2 does: Marks uncovered zeros as starred and covers their row and
		// column.
		for (int i = 0; i < cost.length; i++) {
			for (int j = 0; j < cost[i].length; j++) {
				if ((cost[i][j] == 0) && (colCover[j] == 0) && (rowCover[i] == 0)) {
					mask[i][j] = 1;
					colCover[j] = 1;
					rowCover[i] = 1;
				}
			}
		}
		clearCovers(rowCover, colCover); // Reset cover vectors.
		// System.out.println("munkres_step2");
		// printMask(mask);
		step = 3;
		return step;
	}

	private int munkres_step3(int step, int[][] mask, int[] colCover, int k) {
		// What STEP 3 does: Cover columns of starred zeros. Check if all columns are
		// covered.
		for (int i = 0; i < mask.length; i++) // Cover columns of starred zeros.
		{
			for (int j = 0; j < mask[i].length; j++) {
				if (mask[i][j] == 1)
					colCover[j] = 1;
			}
		}
		int count = 0;
		for (int j = 0; j < colCover.length; j++) // Check if all columns are covered.
			count = count + colCover[j];

		if (count >= k) // Should be cost.length but ok, because mask has same dimensions.
			step = 7;
		else
			step = 4;
		// System.out.println("step 3: count="+count+" k="+k+" go to step "+step);
		// System.out.println("munkres_step3");
		// printMask(mask);
		return step;
	}

	// Aux for munkres_step4.
	private int[] findUncoveredZero(int[] row_col, double[][] cost, int[] rowCover, int[] colCover) {
		row_col[0] = -1; // Just a check value. Not a real index.
		row_col[1] = 0;
		int i = 0;
		boolean done = false;
		while (done == false) {
			int j = 0;
			while (j < cost[i].length) {
				if (cost[i][j] == 0 && rowCover[i] == 0 && colCover[j] == 0) {
					row_col[0] = i;
					row_col[1] = j;
					done = true;
				}
				j = j + 1;
			} // end inner while
			i = i + 1;
			if (i >= cost.length)
				done = true;
		} // end outer while
		return row_col;
	}

	private int munkres_step4(int step, double[][] cost, int[][] mask, int[] rowCover, int[] colCover, int[] zero_RC) {
		// What STEP 4 does: Find an uncovered zero in cost and prime it (if none go to
		// step 6). Check for star in same row:
		// if yes, cover the row and uncover the star's column. Repeat until no
		// uncovered zeros are left
		// and go to step 6. If not, save location of primed zero and go to step 5.
		int[] row_col = new int[2]; // Holds row and col of uncovered zero.
		boolean done = false;
		while (done == false) {
			row_col = findUncoveredZero(row_col, cost, rowCover, colCover);
			if (row_col[0] == -1) {
				done = true;
				step = 6;
				// System.out.println("Step 4: no uncovered zeros. go step "+step);
			} else {
				mask[row_col[0]][row_col[1]] = 2; // Prime the found uncovered zero.
				boolean starInRow = false;
				for (int j = 0; j < mask[row_col[0]].length; j++) {
					if (mask[row_col[0]][j] == 1) // If there is a star in the same row...
					{
						starInRow = true;
						row_col[1] = j; // remember its column.
					}
				}
				if (starInRow == true) {
					rowCover[row_col[0]] = 1; // Cover the star's row.
					colCover[row_col[1]] = 0; // Uncover its column.
					// System.out.println("Step 4: starInRow=true. cover row="+row_col[0]+" uncover
					// column="+row_col[1]);
				} else {
					zero_RC[0] = row_col[0]; // Save row of primed zero.
					zero_RC[1] = row_col[1]; // Save column of primed zero.
					done = true;
					step = 5;
					// System.out.println("Step 4: starInRow=false. save primed zero
					// row="+row_col[0]+" column="+row_col[1]+". Go step "+step);
				}
			}
		}
		return step;
	}

	// Aux for munkres_step5.
	private int findStarInCol(int[][] mask, int col) {
		int r = -1; // Again this is a check value.
		for (int i = 0; i < mask.length; i++) {
			if (mask[i][col] == 1)
				r = i;
		}
		return r;
	}

	// Aux for munkres_step5.
	private int findPrimeInRow(int[][] mask, int row) {
		int c = -1;
		for (int j = 0; j < mask[row].length; j++) {
			if (mask[row][j] == 2)
				c = j;
		}
		return c;
	}

	// Aux for munkres_step5.
	private void convertPath(int[][] mask, int[][] path, int count) {
		for (int i = 0; i <= count; i++) {
			if (mask[(path[i][0])][(path[i][1])] == 1)
				mask[(path[i][0])][(path[i][1])] = 0;
			else
				mask[(path[i][0])][(path[i][1])] = 1;
		}
	}

	// Aux for munkres_step5.
	private void erasePrimes(int[][] mask) {
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				if (mask[i][j] == 2)
					mask[i][j] = 0;
			}
		}
	}

	private int munkres_step5(int step, int[][] mask, int[] rowCover, int[] colCover, int[] zero_RC) {
		// What STEP 5 does: Construct series of alternating primes and stars. Start
		// with prime from step 4.
		// Take star in the same column. Next take prime in the same row as the star.
		// Finish at a prime with no star in its column.
		// Unstar all stars and star the primes of the series. Erase any other primes.
		// Reset covers. Go to step 3.
		int count = 0; // Counts rows of the path matrix.
		int[][] path = new int[(mask[0].length * mask.length)][2]; // Path matrix (stores row and col).
		path[count][0] = zero_RC[0]; // Row of last prime.
		path[count][1] = zero_RC[1]; // Column of last prime.
		boolean done = false;
		while (done == false) {
			int r = findStarInCol(mask, path[count][1]);
			if (r >= 0) {
				count = count + 1;
				path[count][0] = r; // Row of starred zero.
				path[count][1] = path[count - 1][1]; // Column of starred zero.
			} else
				done = true;

			if (done == false) {
				int c = findPrimeInRow(mask, path[count][0]);
				count = count + 1;
				path[count][0] = path[count - 1][0]; // Row of primed zero.
				path[count][1] = c; // Col of primed zero.
			}
		} // end while

		convertPath(mask, path, count);
		clearCovers(rowCover, colCover);
		erasePrimes(mask);
		step = 3;
		return step;
	}

	// Aux for munkres_step6.
	private double findSmallest(double[][] cost, int[] rowCover, int[] colCover) {
		double minval = Double.MAX_VALUE; // There cannot be a larger cost than this.
		for (int i = 0; i < cost.length; i++) // Now find the smallest uncovered value.
		{
			for (int j = 0; j < cost[i].length; j++) {
				if (rowCover[i] == 0 && colCover[j] == 0 && (minval > cost[i][j]))
					minval = cost[i][j];
			}
		}
		return minval;
	}

	private int munkres_step6(int step, double[][] cost, int[] rowCover, int[] colCover) {
		// What STEP 6 does: Find smallest uncovered value in cost: a. Add it to every
		// element of covered rows
		// b. Subtract it from every element of uncovered columns. Go to step 4.
		double minval = findSmallest(cost, rowCover, colCover);
		// System.out.println("minval: "+minval);
		for (int i = 0; i < rowCover.length; i++) {
			for (int j = 0; j < colCover.length; j++) {
				if (rowCover[i] == 1)
					cost[i][j] = cost[i][j] + minval;
				if (colCover[j] == 0)
					cost[i][j] = cost[i][j] - minval;
			}
		}
		step = 4;
		return step;
	}

	private void printCost(double[][] c) {
		for (int i = 0; i < c.length; i++) {
			for (int j = 0; j < c[i].length; j++)
				System.out.print(c[i][j] + " ");
			System.out.println("");
		}
	}

	private void printMask(int[][] m) {
		System.out.println("Mask: ");
		for (int i = 0; i < m.length; i++) {
			for (int j = 0; j < m[i].length; j++)
				System.out.print(m[i][j] + " ");
			System.out.println("");
		}
	}

	private double GEDMunkres(JGraphtClosureGraph g1, JGraphtClosureGraph g2) {
		ArrayList<closureVertex> n1 = g1.getClosureVertexList();
		ArrayList<closureVertex> n2 = g2.getClosureVertexList();
	    int k = Math.min(n1.size(), n2.size());
	    if(k ==0) return 0;
	    
	    double[][] cost = new double[n1.size()][n2.size()];
	    double[][] originalCost = new double[n1.size()][n2.size()];
		cost = createCostArray(cost, g1, g2);
		/*
		//////////for test 
		int k =3;
		double[][] cost = new double[3][4];
		double[][] originalCost = new double[3][4];
		cost[0][0]  = 0.45;cost[0][1]  = 0.64;cost[0][2]  = 0.23;cost[0][3]  = 0.67;
		cost[1][0]  = 0.71;cost[1][1]  = 0.68;cost[1][2]  = 0.40;cost[1][3]  =0.24;
		cost[2][0]  = 0.80	;cost[2][1]  = 0.70;cost[2][2]  = 1.00;cost[2][3]  = 0.07;
				*/		
		 for(int i=0; i<cost.length; i++)
	      {
	            for(int j=0; j<cost[0].length; j++)
	                originalCost[i][j]=cost[i][j];
	     }
		 
		int[][] assignment = new int[cost.length][2];
		HungarianAlgorithm huang = new HungarianAlgorithm();
		assignment = huang.hgAlgorithm(cost, "min");	//Call Hungarian algorithm.
		
    	double sum = 0;
	//	for (int i = 0; i < assignment.length; i++) {
			//System.out.println("assignment[i][0]:" + assignment[i][0]+", assignment[i][1]:"+assignment[i][1]);
			//sum = sum + originalCost[assignment[i][0]][assignment[i][1]];
		//}
		 for (int i = 0; i < assignment.length; i++) {
    		 closureVertex v1 = n1.get(assignment[i][0]);
    		 closureVertex v1_g2 = n2.get(assignment[i][1]);
    		 if(v1.getLabel().get(0).compareTo(v1_g2.getLabel().get(0)) != 0)
    			 sum++;
		 }
    	int tempcount = 0;
    	 for (int i = 0; i < assignment.length-1; i++) {
    		 closureVertex v1 = n1.get(assignment[i][0]);
    		 closureVertex v1_g2 = n2.get(assignment[i][1]);
    		for(int j=i+1; j< assignment.length;j++) {
    			closureVertex v2  = n1.get(assignment[j][0]);
    			closureVertex v2_g2  = n2.get(assignment[j][1]);
    			if(g1.hasEdge(v1, v2) ==true  && g2.hasEdge(v1_g2, v2_g2) ==false) {
    				sum++;
    			}
    			if(g2.hasEdge(v1_g2, v2_g2) ==true && g1.hasEdge(v1, v2) ==false) {
    				sum++;
    			}
    			if(g2.hasEdge(v1_g2, v2_g2) ==true) {
    				tempcount++;
    			}
    		}
    	}
    
		 sum +=  g2.getNumEdges()  - tempcount  + g2.getNumNodes() - g1.getNumNodes();
    	
		// System.out.println("GEDMunkres sum="+sum);
		return sum;
		
	}
	
	
	private double GEDMunkresOld(JGraphtClosureGraph g1, JGraphtClosureGraph g2) {
		ArrayList<closureVertex> n1 = g1.getClosureVertexList();
		ArrayList<closureVertex> n2 = g2.getClosureVertexList();
		int k = Math.min(n1.size(), n2.size());
		double[][] cost = new double[n1.size()][n2.size()];
		double[][] originalCost = new double[n1.size()][n2.size()];
		cost = createCostArray(cost, g1, g2);
		for (int i = 0; i < cost.length; i++) {
			for (int j = 0; j < cost[0].length; j++)
				originalCost[i][j] = cost[i][j];
		}

		// double maxCost = findLargest(cost); //Find largest cost matrix element
		// (needed for step 6).
		// System.out.println("maxCost="+maxCost);

		// printCost(cost);

		int[][] mask = new int[cost.length][cost[0].length]; // The mask array.
		int[] rowCover = new int[cost.length]; // The row covering vector.
		int[] colCover = new int[cost[0].length]; // The column covering vector.
		int[] zero_RC = new int[2]; // Position of last zero from Step 4.
		int step = 1;
		boolean done = false;

		clearCovers(rowCover, colCover);

		while (done == false) // main execution loop
		{
			switch (step) {
			case 1:
				step = munkres_step1a(step, cost);
				// System.out.println("After step 1a:");
				// printCost(cost);
				break;
			case 11:
				step = munkres_step1b(step, cost);
				// System.out.println("After step 1b:");
				// printCost(cost);
				break;
			case 2:
				step = munkres_step2(step, cost, mask, rowCover, colCover);
				break;
			case 3:
				step = munkres_step3(step, mask, colCover, k);
				break;
			case 4:
				step = munkres_step4(step, cost, mask, rowCover, colCover, zero_RC);
				break;
			case 5:
				step = munkres_step5(step, mask, rowCover, colCover, zero_RC);
				break;
			case 6:
				step = munkres_step6(step, cost, rowCover, colCover);
				// System.out.println("After step 6:");
				// printCost(cost);
				break;
			case 7:
				done = true;
				break;
			}
		} // end while

		int[][] assignment = new int[cost.length][2]; // Create the returned array.
		for (int i = 0; i < mask.length; i++) {
			for (int j = 0; j < mask[i].length; j++) {
				if (mask[i][j] == 1) {
					assignment[i][0] = i;
					assignment[i][1] = j;
				}
			}
		}
		// System.out.println("print assignment: ");
		// printMask(assignment);

		// If you want to return the min or max sum, in your own main method
		// instead of the assignment array, then use the following code:

		// double sum = 0;
		// for (int i=0; i<assignment.length; i++)
		// {
		// sum = sum + array[assignment[i][0]][assignment[i][1]];
		// }
		// return sum;

		// Of course you must also change the header of the method to:
		// public static double hgAlgorithm (double[][] array, String sumType)

		// return assignment;
		double sum = 0;
		for (int i = 0; i < assignment.length; i++) {
			sum = sum + originalCost[assignment[i][0]][assignment[i][1]];
		}
		// System.out.println("GEDMunkres sum="+sum);
		return sum;
	}

	private double computeGEDMunkres(ArrayList<ArrayList<closureEdge>> GUIPatterns,
			ArrayList<closureEdge> candPattern) {
		double GED = Double.MAX_VALUE;
		ArrayList<Integer> GED_fl = new ArrayList<Integer>();

		// only 1 selected pattern in GUIPatterns, no need to compute lower bound, go
		// directly to compute exact GED
		if (GUIPatterns.size() == 1)
			return GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(0)), new JGraphtClosureGraph(candPattern));
		// GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(0)), new
		// JGraphtClosureGraph(candPattern));
		else// 1. do fast loose GED lower bound first (GED_fl)
		{
			JGraphtClosureGraph candPatternCGraph = new JGraphtClosureGraph(candPattern);
			ArrayList<closureVertex> candPatternCGraph_closureVertexList = candPatternCGraph.getClosureVertexList();
			int candPatternCGraph_numEdges = candPatternCGraph.getClosureEdgeList().size();
			int candPatternCGraph_numVertices = candPatternCGraph_closureVertexList.size();
			ArrayList<String> candPatternCGraph_closureVertexLabelList = new ArrayList<String>();
			for (int i = 0; i < candPatternCGraph_closureVertexList.size(); i++) {
				closureVertex v = candPatternCGraph_closureVertexList.get(i);
				candPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0));
			}
			for (int i = 0; i < GUIPatterns.size(); i++) {
				ArrayList<closureEdge> sPattern = GUIPatterns.get(i);
				JGraphtClosureGraph sPatternCGraph = new JGraphtClosureGraph(sPattern);
				ArrayList<closureVertex> sPatternCGraph_closureVertexList = sPatternCGraph.getClosureVertexList();
				int sPatternCGraph_numEdges = sPatternCGraph.getClosureEdgeList().size();
				int sPatternCGraph_numVertices = sPatternCGraph_closureVertexList.size();
				ArrayList<String> sPatternCGraph_closureVertexLabelList = new ArrayList<String>();
				for (int j = 0; j < sPatternCGraph_closureVertexList.size(); j++) {
					closureVertex v = sPatternCGraph_closureVertexList.get(j);
					sPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0));
				}
				// get common vertex labels
				sPatternCGraph_closureVertexLabelList.retainAll(candPatternCGraph_closureVertexLabelList);
				int nodesToAddRemoveRename = Math.abs(sPatternCGraph_numVertices - candPatternCGraph_numVertices)
						+ (Math.min(sPatternCGraph_numVertices, candPatternCGraph_numVertices)
								- sPatternCGraph_closureVertexLabelList.size());
				int edgesToAddRemove = Math.abs(sPatternCGraph_numEdges - candPatternCGraph_numEdges);
				GED_fl.add(nodesToAddRemoveRename + edgesToAddRemove);

				// print candPattern
				// System.out.println("PM_computeGED ------------- candPattern: ");
				// for(int c=0; c<candPattern.size(); c++)
				// candPattern.get(c).print();
				// System.out.println("sPattern: ");
				// for(int c=0; c<sPattern.size(); c++)
				// sPattern.get(c).print();
				// System.out.println("GED_fl="+(nodesToAddRemoveRename+edgesToAddRemove));
			}
			// ArrayList<Float> sorted_GED_fl=new ArrayList<Float>();
			// for(int i=0; i<GED_fl.size(); i++)

			// Collections.sort(GED_fl);
			//System.out.println("GED_fl list: " + GED_fl.toString());
			//System.out.println("min GED_fl: " + Collections.min(GED_fl) + " max GED_fl: " + Collections.max(GED_fl));

			int min_GED_fl = Collections.min(GED_fl);
			int index = GED_fl.indexOf(min_GED_fl);
			int counter = 0;
			boolean CONTINUE = true;
			while (CONTINUE) {
				counter++;
				// System.out.println("%%%%%%%%%%%% PM_computeExactGED");
				double curr_GED = GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(index)),
						new JGraphtClosureGraph(candPattern));
				// System.out.println("%%%%%%%%%%%% curr_GED="+curr_GED);
				if (curr_GED < GED) {
					GED = curr_GED;
					// if(GED<expectedGdGED)
					// {
					// CONTINUE=false;
					// System.out.println("Can STOP GED computation!!!");
					// }
					// System.out.println("!!!!!!!!! updated GED to "+GED);
				}
				if (CONTINUE) {
					for (int i = 0; i < GED_fl.size(); i++) {
						int v = GED_fl.get(i);
						if (v >= curr_GED && v != Integer.MAX_VALUE)
							GED_fl.set(i, Integer.MAX_VALUE);
					}
					GED_fl.set(index, Integer.MAX_VALUE);
					min_GED_fl = Collections.min(GED_fl);
					if (min_GED_fl == Integer.MAX_VALUE)
						CONTINUE = false;
					else
						index = GED_fl.indexOf(min_GED_fl);
				}
			}
			// System.out.println("************ counter="+counter);

		}

		System.out.println(">>>>>>>>>>>>>>   GED=" + GED);
		return GED;
	}

	private double computeGEDMunkres(double expectedGdGED, ArrayList<ArrayList<closureEdge>> GUIPatterns,
			ArrayList<closureEdge> candPattern) {
		double GED = Double.MAX_VALUE;
		ArrayList<Float> GED_fl = new ArrayList<Float>();
		// only 1 selected pattern in GUIPatterns, no need to compute lower bound, go
		// directly to compute exact GED
		if (GUIPatterns.size() == 1) {
			JGraphtClosureGraph g1 = new JGraphtClosureGraph(GUIPatterns.get(0));
			JGraphtClosureGraph g2 = new  JGraphtClosureGraph(candPattern);
			if(g1.getNumNodes()  < g2.getNumNodes()) 
			       return GEDMunkres(g1, g2);
			else
				 return GEDMunkres(g2, g1);
		}
		// GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(0)), new
		// JGraphtClosureGraph(candPattern));
		else// 1. do fast loose GED lower bound first (GED_fl)
		{
			JGraphtClosureGraph candPatternCGraph = new JGraphtClosureGraph(candPattern);
			ArrayList<closureVertex> candPatternCGraph_closureVertexList = candPatternCGraph.getClosureVertexList();
			int candPatternCGraph_numEdges = candPatternCGraph.getClosureEdgeList().size();
			int candPatternCGraph_numVertices = candPatternCGraph_closureVertexList.size();
			ArrayList<String> candPatternCGraph_closureVertexLabelList = new ArrayList<String>();
			for (int i = 0; i < candPatternCGraph_closureVertexList.size(); i++) {
				closureVertex v = candPatternCGraph_closureVertexList.get(i);
				candPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0));
			}
			for (int i = 0; i < GUIPatterns.size(); i++) {
				ArrayList<closureEdge> sPattern = GUIPatterns.get(i);
				JGraphtClosureGraph sPatternCGraph = new JGraphtClosureGraph(sPattern);
				ArrayList<closureVertex> sPatternCGraph_closureVertexList = sPatternCGraph.getClosureVertexList();
				int sPatternCGraph_numEdges = sPatternCGraph.getClosureEdgeList().size();
				int sPatternCGraph_numVertices = sPatternCGraph_closureVertexList.size();
				ArrayList<String> sPatternCGraph_closureVertexLabelList = new ArrayList<String>();
				for (int j = 0; j < sPatternCGraph_closureVertexList.size(); j++) {
					closureVertex v = sPatternCGraph_closureVertexList.get(j);
					sPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0));
				}
				// get common vertex labels
				sPatternCGraph_closureVertexLabelList.retainAll(candPatternCGraph_closureVertexLabelList);
				int nodesToAddRemoveRename = Math.abs(sPatternCGraph_numVertices - candPatternCGraph_numVertices)
						+ (Math.min(sPatternCGraph_numVertices, candPatternCGraph_numVertices)
								- sPatternCGraph_closureVertexLabelList.size());
				int edgesToAddRemove = Math.abs(sPatternCGraph_numEdges - candPatternCGraph_numEdges);

				//GED_fl.add(nodesToAddRemoveRename + edgesToAddRemove);
				float norm_gedl =  (nodesToAddRemoveRename + edgesToAddRemove)*1.0f/(candPatternCGraph_numEdges + candPatternCGraph_numVertices+sPatternCGraph_numEdges+sPatternCGraph_numVertices);
				GED_fl.add(norm_gedl);
				//System.out.print("GED_fl_" + i + "---" +norm_gedl+","  );
				//System.out.println("fenmu:" +(candPatternCGraph_numEdges + candPatternCGraph_numVertices+sPatternCGraph_numEdges+sPatternCGraph_numVertices));
				//// Tighter lower bound by Kai
				//// Tighter lower bound of  candPatternCGraph  and  sPatternCGraph
			//	int  N  =  getMissingMatchedEdges(candPatternCGraph, sPatternCGraph);
			 //   System.out.println("Boost : " + N );
			//    System.out.println("With boost  " +(nodesToAddRemoveRename + edgesToAddRemove +N  )*1.0f/(candPatternCGraph_numEdges + candPatternCGraph_numVertices+sPatternCGraph_numEdges+sPatternCGraph_numVertices)+","  );
			//    if(N > 0)    System.out.println(", WAH, Good!");
			    
			  //  if(true) {
			    //	double curr_GED = 0;
				//	if(candPatternCGraph.getNumNodes()  < sPatternCGraph.getNumNodes()) 
				//		curr_GED =  GEDMunkres(candPatternCGraph, sPatternCGraph);
				//	else
				//		curr_GED =  GEDMunkres(sPatternCGraph, candPatternCGraph);
					
				//	System.out.println("lowerbound1:"+(nodesToAddRemoveRename + edgesToAddRemove)+ ",lowerbound2:"+(nodesToAddRemoveRename + edgesToAddRemove+N)+",trueGED:" + curr_GED);
				//	if(nodesToAddRemoveRename + edgesToAddRemove+N  > curr_GED) {
				//		System.out.println("Lower Bound Error!");
				//	}
			  //  }
			    
				// print candPattern
				// System.out.println("PM_computeGED ------------- candPattern: ");
				// for(int c=0; c<candPattern.size(); c++)
				// candPattern.get(c).print();
				// System.out.println("sPattern: ");
				// for(int c=0; c<sPattern.size(); c++)
				// sPattern.get(c).print();
				// System.out.println("GED_fl="+(nodesToAddRemoveRename+edgesToAddRemove));
			}
			//System.out.println();
			// ArrayList<Float> sorted_GED_fl=new ArrayList<Float>();
			// for(int i=0; i<GED_fl.size(); i++)

			// Collections.sort(GED_fl);
			//System.out.println("GED_fl list: " + GED_fl.toString());
			//System.out.println("min GED_fl: " + Collections.min(GED_fl) + " max GED_fl: " + Collections.max(GED_fl));

			float min_GED_fl = Collections.min(GED_fl);
			int index = GED_fl.indexOf(min_GED_fl);
			int counter = 0;
			boolean CONTINUE = true;
			while (CONTINUE) {
				counter++;
				// System.out.println("%%%%%%%%%%%% PM_computeExactGED");
				JGraphtClosureGraph g1 = new JGraphtClosureGraph(GUIPatterns.get(index));
				JGraphtClosureGraph g2 = new  JGraphtClosureGraph(candPattern);
				double curr_GED = 0;
				if(g1.getNumNodes()  < g2.getNumNodes()) 
					curr_GED =  GEDMunkres(g1, g2);
				else
					curr_GED =  GEDMunkres(g2, g1);
				/////////////////////
			//	int maxv  = g1.getNumNodes() ; if(maxv < g2.getNumNodes() ) maxv =  g2.getNumNodes()  ;
			
				//System.out.println("curr_GED:" + curr_GED );
				
				curr_GED =  curr_GED*1.0f/(g1.getNumEdges()+g1.getNumNodes()+g2.getNumEdges()+g2.getNumNodes());
				// System.out.println("%%%%%%%%%%%% curr_GED="+curr_GED);
			//	System.out.println("index:" + index +" ,TrueGED="+curr_GED);
				if (curr_GED < GED) {
					GED = curr_GED;
					// if(GED<expectedGdGED)
					// {
					// CONTINUE=false;
					// System.out.println("Can STOP GED computation!!!");
					// }
					// System.out.println("!!!!!!!!! updated GED to "+GED);
				}
				if (CONTINUE) {
					for (int i = 0; i < GED_fl.size(); i++) {
						float v = GED_fl.get(i);
						if (v >= curr_GED && v != Integer.MAX_VALUE)
							GED_fl.set(i, Float.MAX_VALUE);
					}
					GED_fl.set(index, Float.MAX_VALUE);
					min_GED_fl = Collections.min(GED_fl);
					if (min_GED_fl == Float.MAX_VALUE)
						CONTINUE = false;
					else
						index = GED_fl.indexOf(min_GED_fl);
				}
			}
			// System.out.println("************ counter="+counter);

		}

		// System.out.println(">>>>>>>>>>>>>> GED="+GED);
		return GED;
	}

	// no GED lower bound heuristics
	private double computeGEDMunkresWOGEDFl(double expectedGdGED, ArrayList<ArrayList<closureEdge>> GUIPatterns,
			ArrayList<closureEdge> candPattern) {
		System.out.println("computeGEDMunkresWOGEDFl");
		double GED = Double.MAX_VALUE;
		// ArrayList<Integer> GED_fl=new ArrayList<Integer>();

		// only 1 selected pattern in GUIPatterns, no need to compute lower bound, go
		// directly to compute exact GED
		if (GUIPatterns.size() == 1) {
			JGraphtClosureGraph g1 = new JGraphtClosureGraph(GUIPatterns.get(0));
			JGraphtClosureGraph g2 = new  JGraphtClosureGraph(candPattern);
			//if(g1.getNumNodes()  < g2.getNumNodes()) 
			//       return GEDMunkres(g1, g2);
			//else
			//	 return GEDMunkres(g2, g1);
			GEDMunkresOld(g2, g1);
		}
		// GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(0)), new
		// JGraphtClosureGraph(candPattern));
		else// 1. do fast loose GED lower bound first (GED_fl)
		{
			// JGraphtClosureGraph candPatternCGraph=new JGraphtClosureGraph(candPattern);
			// ArrayList<closureVertex>
			// candPatternCGraph_closureVertexList=candPatternCGraph.getClosureVertexList();
			// int candPatternCGraph_numEdges=candPatternCGraph.getClosureEdgeList().size();
			// int candPatternCGraph_numVertices=candPatternCGraph_closureVertexList.size();
			// ArrayList<String> candPatternCGraph_closureVertexLabelList=new
			// ArrayList<String>();
			// for(int i=0; i<candPatternCGraph_closureVertexList.size(); i++)
			// {
			// closureVertex v=candPatternCGraph_closureVertexList.get(i);
			// candPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0));
			// }
			/*
			 * for(int i=0; i<GUIPatterns.size(); i++) { ArrayList<closureEdge>
			 * sPattern=GUIPatterns.get(i); JGraphtClosureGraph sPatternCGraph=new
			 * JGraphtClosureGraph(sPattern); ArrayList<closureVertex>
			 * sPatternCGraph_closureVertexList=sPatternCGraph.getClosureVertexList(); int
			 * sPatternCGraph_numEdges=sPatternCGraph.getClosureEdgeList().size(); int
			 * sPatternCGraph_numVertices=sPatternCGraph_closureVertexList.size();
			 * ArrayList<String> sPatternCGraph_closureVertexLabelList=new
			 * ArrayList<String>(); for(int j=0; j<sPatternCGraph_closureVertexList.size();
			 * j++) { closureVertex v=sPatternCGraph_closureVertexList.get(j);
			 * sPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0)); } //get
			 * common vertex labels sPatternCGraph_closureVertexLabelList.retainAll(
			 * candPatternCGraph_closureVertexLabelList); int
			 * nodesToAddRemoveRename=Math.abs(sPatternCGraph_numVertices-
			 * candPatternCGraph_numVertices)+ (Math.min(sPatternCGraph_numVertices,
			 * candPatternCGraph_numVertices)-sPatternCGraph_closureVertexLabelList.size());
			 * int
			 * edgesToAddRemove=Math.abs(sPatternCGraph_numEdges-candPatternCGraph_numEdges)
			 * ; GED_fl.add(nodesToAddRemoveRename+edgesToAddRemove);
			 * 
			 * //print candPattern
			 * //System.out.println("PM_computeGED ------------- candPattern: "); //for(int
			 * c=0; c<candPattern.size(); c++) // candPattern.get(c).print();
			 * //System.out.println("sPattern: "); //for(int c=0; c<sPattern.size(); c++) //
			 * sPattern.get(c).print();
			 * //System.out.println("GED_fl="+(nodesToAddRemoveRename+edgesToAddRemove)); }
			 * //ArrayList<Float> sorted_GED_fl=new ArrayList<Float>(); //for(int i=0;
			 * i<GED_fl.size(); i++)
			 * 
			 * //Collections.sort(GED_fl);
			 * System.out.println("GED_fl list: "+GED_fl.toString());
			 * System.out.println("min GED_fl: "+Collections.min(GED_fl)+" max GED_fl: "
			 * +Collections.max(GED_fl));
			 */
			// int min_GED_fl=Collections.min(GED_fl);
			// int index=GED_fl.indexOf(min_GED_fl);
			int counter = 0;
			boolean CONTINUE = true;
			int i = 0;
			while (CONTINUE) {
				counter++;
				// System.out.println("%%%%%%%%%%%% PM_computeExactGED");
				// double curr_GED=GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(index)),
				// new JGraphtClosureGraph(candPattern));
				//double curr_GED = GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(i)),
				//		new JGraphtClosureGraph(candPattern));
				
				JGraphtClosureGraph g1 = new JGraphtClosureGraph(GUIPatterns.get(i));
				JGraphtClosureGraph g2 = new  JGraphtClosureGraph(candPattern);
			//	double curr_GED = 0;
			//	if(g1.getNumNodes()  < g2.getNumNodes()) 
			//		curr_GED =  GEDMunkres(g1, g2);
			//	else
			//		curr_GED =  GEDMunkres(g2, g1);
				double curr_GED  = GEDMunkresOld(g1, g2);
				/////////////////////
			//	int maxv  = g1.getNumNodes() ; if(maxv < g2.getNumNodes() ) maxv =  g2.getNumNodes()  ;
			
				//System.out.println("curr_GED:" + curr_GED );
				
			//	curr_GED =  curr_GED*1.0f/(g1.getNumEdges()+g1.getNumNodes()+g2.getNumEdges()+g2.getNumNodes());
				
				// System.out.println("%%%%%%%%%%%% curr_GED="+curr_GED);
				if (curr_GED < GED) {
					GED = curr_GED;
					// if(GED<expectedGdGED)
					// {
					// CONTINUE=false;
					// System.out.println("Can STOP GED computation!!!");
					// }
					// System.out.println("!!!!!!!!! updated GED to "+GED);
				}
				if (CONTINUE) {
					/*
					 * for(int i=0; i<GED_fl.size(); i++) { int v=GED_fl.get(i); if(v>=curr_GED &&
					 * v!=Integer.MAX_VALUE) GED_fl.set(i, Integer.MAX_VALUE); } GED_fl.set(index,
					 * Integer.MAX_VALUE); min_GED_fl=Collections.min(GED_fl);
					 * if(min_GED_fl==Integer.MAX_VALUE) CONTINUE=false; else
					 * index=GED_fl.indexOf(min_GED_fl);
					 */
					i++;
					if (i >= GUIPatterns.size())
						CONTINUE = false;
				}
			}
			System.out.println("************   counter=" + counter + " GUIPatterns size=" + GUIPatterns.size());

		}

		// System.out.println(">>>>>>>>>>>>>> GED="+GED);
		return GED;
	}
	public  ArrayList<closureEdge>  transJGraphtClosureGraphToclosureEdges(JGraphtClosureGraph JG){
		//System.out.println(" JG.getG()1: " + JG.getG().edgeSet().size()); 
		 ArrayList<closureEdge>   ans =  new  ArrayList<closureEdge> ();
		Set eSet =  JG.getG(). edgeSet(); 
		Iterator i  = eSet.iterator();
		int count = 0;
		while (i.hasNext()) {
			//System.out.println("Edge " + (count++));
			//((closureEdge) i.next()).print();
			ans.add((closureEdge) i.next());
		}
	//	System.out.println(" JG.getG()2: " +ans.size()); 
		return ans;
	}
	private int  getMissedEdges(JGraphtClosureGraph  candPatternCGraph, JGraphtClosureGraph sPatternCGraph) {
		   
        int MAX_NUM_RelaxedEdges = 0;
		ArrayList<ArrayList<Integer>> candMappingMatrix =  new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer>          candFCTMappingSize = new  ArrayList<Integer>();
		FCT  =  this.trietree.getFct();
		boolean flag = true;
		if(flag) {
			 ArrayList<closureEdge> Edge1 = candPatternCGraph.getClosureEdgeList();
			 ArrayList<closureEdge> Edge2 = sPatternCGraph.getClosureEdgeList();
			 ArrayList<String> EdgeLabel1  = new  ArrayList<String> ();
			 ArrayList<String> EdgeLabel2  = new  ArrayList<String> ();
			 for(int i=0;i<Edge1.size();i++) {
				 EdgeLabel1.add(Edge1.get(i).getEdgeLabelString());
			 }
			 for(int i=0; i< Edge2.size(); i++){
				 EdgeLabel2.add(Edge2.get(i).getEdgeLabelString());
			 }
			 int minsize = Math.min(EdgeLabel1.size(), EdgeLabel2.size());
			 EdgeLabel1.retainAll(EdgeLabel2);
			 return (minsize -  EdgeLabel1.size());
		}
		if(true) {
			ArrayList<closureEdge> candAlledges  =    candPatternCGraph.getClosureEdgeList();
			MAX_NUM_RelaxedEdges  =  candAlledges.size();
			for(int i=0; i< FCT.size();i++) {
				JGraphtClosureGraph aFCT  =  FCT.get(i);
			//	System.out.println("i: " + i + ", aFCT.getNumEdges(): "+  aFCT.getNumEdges() +" , aFCT.getNumNodes(): " + aFCT.getNumNodes());
				ArrayList<closureEdge>  allEdgesInFCT = transJGraphtClosureGraphToclosureEdges(aFCT);
				ArrayList<String>  EdgeList  = new ArrayList<String> ();
				for(int kk = 0;kk< allEdgesInFCT.size();kk++) {
					closureEdge  e = allEdgesInFCT.get(kk);
					int id1 = e.getSource().getID();
					int id2 = e.getTarget().getID();
					String str = "";
					if(id1 < id2)  str = str+Integer.toString(id1) + Integer.toString(id2);
					else if(id1 > id2)   str = str+Integer.toString(id2) + Integer.toString(id1);
					else System.out.println("Two Id of an edge are the same");
					EdgeList.add(str);
				}
				boolean isSmall =  true;
				if( aFCT.getNumEdges() > candPatternCGraph.getNumEdges()    ||   aFCT.getNumNodes() > candPatternCGraph.getNumNodes())  {
					isSmall = false; 
			    }
				if(isSmall) {
					VF2 VF2_operator = new VF2();
					VF2_Match matchedPairs = new VF2_Match(); 
					 VF2_operator.doVF2FindAllmatchings(matchedPairs, aFCT, candPatternCGraph, false) ;
					  ArrayList<ArrayList<closureVertex>>   matchedDataNodes  =  VF2_operator.getMatchedDataNodes();
					  ArrayList<ArrayList<closureVertex>>   queryDataNodes  =  VF2_operator.getMatchedQueryNodes();
	                  ArrayList<Integer> allBestMatchings   =  VF2_operator.getAllBestMatchings();
	                 if(matchedDataNodes  == null ) { 
	                      candFCTMappingSize.add(0);  continue; 
	                 }
	  				int count = 0;
	  				for(int k=0;k<matchedDataNodes.size();k++) {
	  					if(allBestMatchings.get(k) == aFCT.getClosureVertexList().size()) {
	  						count++;
	  						ArrayList<closureVertex>  matchedvertexs  =  matchedDataNodes.get(k);
	  						ArrayList<Integer>  matchedvertexsIDs  =   new ArrayList<Integer>();
	  						for(int kk=0;kk<matchedvertexs.size();kk++) 
	  						{     
	  							matchedvertexsIDs.add(matchedvertexs.get(kk).getID());
	  						}
	  						ArrayList<closureVertex>   matchedqueryvertexs = queryDataNodes.get(k);
	  						ArrayList<Integer>  matchedqueryvertexsIDs  =   new ArrayList<Integer>();
	  						for(int kk=0;kk<matchedqueryvertexs.size();kk++)   {
	  							matchedqueryvertexsIDs.add(matchedqueryvertexs.get(kk).getID());
	  						}
	  						ArrayList<Integer>   AColumn = new ArrayList<Integer> ();
	  				       for(int t = 0;t< candAlledges.size(); t++ ) {
	  					    	closureEdge e = candAlledges.get(t);
	  					    	int id1 = e.getSource().getID();
	  					    	int id2 = e.getTarget().getID();
	  					    	int index1 = matchedvertexsIDs.indexOf(id1);
	  					    	int index2 = matchedvertexsIDs.indexOf(id2);
	  					    	if(index1 != -1 && index2 !=-1) {
	  					    		  int queryid1 = matchedqueryvertexsIDs.get(index1);
	  					    		  int queryid2 = matchedqueryvertexsIDs.get(index2);
	  					    		  String str = "";
	  					    		 if(queryid1 < queryid2)  str = str+Integer.toString(queryid1) + Integer.toString(queryid2);
	  								 else if(queryid1 > queryid2)   str = str+Integer.toString(queryid2) + Integer.toString(queryid1);
	  					    		 if(EdgeList.indexOf(str) != -1) 	AColumn.add(1);
	  					    		 else  AColumn.add(0);
	  					    	}else {
	  					    		AColumn.add(0);
	  					    	}
	  					    }
	  						candMappingMatrix.add(AColumn);
	  					}
	  				}
	  				candFCTMappingSize.add(count);
				} else {
					candFCTMappingSize.add(0);
				}
			}
		}
		ArrayList<Integer>          candFCTMappingSize2 = new  ArrayList<Integer>();
	    if(true) {
			for(int i=0; i< FCT.size();i++) {
				JGraphtClosureGraph aFCT  =  FCT.get(i);
				boolean isSmall =  true;
				if( aFCT.getNumEdges() > sPatternCGraph.getNumEdges()  ||  aFCT.getNumNodes() > sPatternCGraph.getNumNodes())  {
					 isSmall = false; 
			    }
				if(isSmall) {
					VF2 VF2_operator = new VF2();
					VF2_Match matchedPairs = new VF2_Match(); 
					 VF2_operator.doVF2FindAllmatchings(matchedPairs, aFCT, sPatternCGraph, false) ;
					  ArrayList<ArrayList<closureVertex>>   matchedDataNodes  =  VF2_operator.getMatchedDataNodes();
					  ArrayList<ArrayList<closureVertex>>   queryDataNodes  =  VF2_operator.getMatchedQueryNodes();
	                  ArrayList<Integer> allBestMatchings   =  VF2_operator.getAllBestMatchings();
	                 if(matchedDataNodes  == null ) { 
	                	 candFCTMappingSize2.add(0);  continue; }
	  				int count = 0;
	  				for(int k=0;k<matchedDataNodes.size();k++) {
	  					if(allBestMatchings.get(k) == aFCT.getClosureVertexList().size()) {
	  						count++;
	  					}
	  				}
	  				candFCTMappingSize2.add(count);
				} else {
					candFCTMappingSize2.add(0);
				}
			}
	    }
		int N = 0;
		if(candFCTMappingSize.size() != candFCTMappingSize2.size()) {
			System.out.println("candFCTMappingSize.size() != candFCTMappingSize2.size()");
		}
		int ans = 0;
		for(int i=0;i<candFCTMappingSize.size();i++) {
			if(candFCTMappingSize.get(i)  > candFCTMappingSize2.get(i)) {
				ans++;
				break;
			}
		}
		while(true) {
			if(ans > 0  ||N >= MAX_NUM_RelaxedEdges  ||  candMappingMatrix.size() <= 0)  break;
			ArrayList<ArrayList<Integer>> candMappingMatrix_Temp =  new ArrayList<ArrayList<Integer>>();
			for(int i=0;i< candMappingMatrix.size();i++) {
				ArrayList<Integer> temp = new ArrayList<Integer>();
				for(int j=0;j<candMappingMatrix.get(i).size();j++) {
					temp.add(candMappingMatrix.get(i).get(j));
				}
				candMappingMatrix_Temp.add(temp);
			}
			N++;	
			///greedily  delete N edges 
			int Row = candMappingMatrix_Temp.size();
			int Col = candMappingMatrix_Temp.get(0).size();
			ArrayList< Boolean>  flaglist = new  ArrayList< Boolean>();
			for(int i=0;i<Col; i++)  flaglist.add(false);
			
			for(int time = 1; time <= N; time++) {
				int index = 0;
				int max = 0;
			    for(int i=0;i<Col;i++) {
			    	 if(flaglist.get(i)==true) continue;
			    	 int sum = 0;
			    	 for(int j=0;j < Row;j++) {
			    		 sum += candMappingMatrix_Temp.get(j).get(i);
			    		 if(sum > max)  {
			    			 max = sum;
			    			 index = i;
			    		 }
			    	 }
			    }
			    flaglist.set(index, true);
			}
		
		   for(int j=0;j<Col;j++) {
				if(flaglist.get(j) == true) {
					ArrayList<Integer> affectedrow = new ArrayList<Integer> ();
						for(int i=0;i<Row;i++) {
							if(candMappingMatrix_Temp.get(i).get(j) == 1) {
								affectedrow.add(i);
							}
						}
					/// set affected row to be zero
					/// set j col to be zero
				   for(int k1=0;k1<Row;k1++) {
					   ArrayList<Integer> arow = candMappingMatrix_Temp.get(k1);
					   for(int k2=0;k2<Col;k2++) {
						   if(k2  == j)  {
							   arow.set(k2, 0);
						   }
						   if(affectedrow.indexOf(k1)!=-1) {
							   arow.set(k2, 0);
						   }
					   }
					   candMappingMatrix_Temp.set(k1, arow);
				   }
				}
		    }
			
			
		   int sum_column_start = 0;
		   int sum_column_end = 0;
		   ans = 0;
			for(int i=0;i<candFCTMappingSize.size();i++) {
				if(candFCTMappingSize.get(i) == 0) continue;
				sum_column_start = sum_column_end;
				sum_column_end =  sum_column_end + candFCTMappingSize.get(i);
			//	System.out.println("sum_column_start: "+ sum_column_start + " , sum_column_end:" +sum_column_end);
				int counttemp = 0;
				for(int k1 = sum_column_start; k1 < sum_column_end;k1++) {
					int sum = 0;
					for(int k2= 0;k2 < Col;k2++) {
						sum += candMappingMatrix_Temp.get(k1).get(k2);
					}
					if(sum != 0 ) counttemp++;
				}
				if(counttemp > candFCTMappingSize2.get(i)) {
					ans++;
					break;
				}
			}
		}
		return N;
	}
	public double computeGEDMunkresWithLowerBoundGED(double expectedGdGED, ArrayList<ArrayList<closureEdge>> GUIPatterns,
			ArrayList<closureEdge> candPattern) {
		double GED = Double.MAX_VALUE;
		ArrayList<Float> GED_fl = new ArrayList<Float>();

		// only 1 selected pattern in GUIPatterns, no need to compute lower bound, go
		// directly to compute exact GED
		if (GUIPatterns.size() == 1) {
			JGraphtClosureGraph g1 = new JGraphtClosureGraph(GUIPatterns.get(0));
			JGraphtClosureGraph g2 = new  JGraphtClosureGraph(candPattern);
			if(g1.getNumNodes()  < g2.getNumNodes()) 
			       return GEDMunkres(g1, g2);
			else
				 return GEDMunkres(g2, g1);
		}
		// GEDMunkres(new JGraphtClosureGraph(GUIPatterns.get(0)), new
		// JGraphtClosureGraph(candPattern));
		else// 1. do fast loose GED lower bound first (GED_fl)
		{
			JGraphtClosureGraph candPatternCGraph = new JGraphtClosureGraph(candPattern);
			ArrayList<closureVertex> candPatternCGraph_closureVertexList = candPatternCGraph.getClosureVertexList();
			int candPatternCGraph_numEdges = candPatternCGraph.getClosureEdgeList().size();
			int candPatternCGraph_numVertices = candPatternCGraph_closureVertexList.size();
			ArrayList<String> candPatternCGraph_closureVertexLabelList = new ArrayList<String>();
			for (int i = 0; i < candPatternCGraph_closureVertexList.size(); i++) {
				closureVertex v = candPatternCGraph_closureVertexList.get(i);
				candPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0));
			}
			for (int i = 0; i < GUIPatterns.size(); i++) {
				ArrayList<closureEdge> sPattern = GUIPatterns.get(i);
				JGraphtClosureGraph sPatternCGraph = new JGraphtClosureGraph(sPattern);
				ArrayList<closureVertex> sPatternCGraph_closureVertexList = sPatternCGraph.getClosureVertexList();
				int sPatternCGraph_numEdges = sPatternCGraph.getClosureEdgeList().size();
				int sPatternCGraph_numVertices = sPatternCGraph_closureVertexList.size();
				ArrayList<String> sPatternCGraph_closureVertexLabelList = new ArrayList<String>();
				for (int j = 0; j < sPatternCGraph_closureVertexList.size(); j++) {
					closureVertex v = sPatternCGraph_closureVertexList.get(j);
					sPatternCGraph_closureVertexLabelList.add(v.getLabel().get(0));
				}
				// get common vertex labels
				sPatternCGraph_closureVertexLabelList.retainAll(candPatternCGraph_closureVertexLabelList);
				int nodesToAddRemoveRename = Math.abs(sPatternCGraph_numVertices - candPatternCGraph_numVertices)
						+ (Math.min(sPatternCGraph_numVertices, candPatternCGraph_numVertices)
								- sPatternCGraph_closureVertexLabelList.size());
				int edgesToAddRemove = Math.abs(sPatternCGraph_numEdges - candPatternCGraph_numEdges);

				//GED_fl.add(nodesToAddRemoveRename + edgesToAddRemove);
				
				//int  N  =  getMissingMatchedEdges(candPatternCGraph, sPatternCGraph);
				int  N  =  getMissedEdges(candPatternCGraph, sPatternCGraph);
				
				float norm_gedl =  (nodesToAddRemoveRename + edgesToAddRemove + N)*1.0f/(candPatternCGraph_numEdges + candPatternCGraph_numVertices+sPatternCGraph_numEdges+sPatternCGraph_numVertices);
				GED_fl.add(norm_gedl);
				
				System.out.print("GED_fl_" + i + "---" +norm_gedl+","  );
			//	System.out.println("fenmu:" +(candPatternCGraph_numEdges + candPatternCGraph_numVertices+sPatternCGraph_numEdges+sPatternCGraph_numVertices));
				//// Tighter lower bound by Kai
				//// Tighter lower bound of  candPatternCGraph  and  sPatternCGraph
				
			   // System.out.println("Boost : " + N );
			   // System.out.println("With boost  " +(nodesToAddRemoveRename + edgesToAddRemove +N  )*1.0f/(candPatternCGraph_numEdges + candPatternCGraph_numVertices+sPatternCGraph_numEdges+sPatternCGraph_numVertices)+","  );
			 //   if(N > 0)    System.out.println(", WAH, Good!");
			    
			 //   if(true) {
			  //  	double curr_GED = 0;
			//		if(candPatternCGraph.getNumNodes()  < sPatternCGraph.getNumNodes()) 
			//			curr_GED =  GEDMunkres(candPatternCGraph, sPatternCGraph);
			//		else
			//			curr_GED =  GEDMunkres(sPatternCGraph, candPatternCGraph);
					
			//		System.out.println("lowerbound1:"+(nodesToAddRemoveRename + edgesToAddRemove)+ ",lowerbound2:"+(nodesToAddRemoveRename + edgesToAddRemove+N)+",trueGED:" + curr_GED);
			//		if(nodesToAddRemoveRename + edgesToAddRemove+N  > curr_GED) {
			//			System.out.println("Lower Bound Error!");
			//		}
			//    }
			    
				// print candPattern
				// System.out.println("PM_computeGED ------------- candPattern: ");
				// for(int c=0; c<candPattern.size(); c++)
				// candPattern.get(c).print();
				// System.out.println("sPattern: ");
				// for(int c=0; c<sPattern.size(); c++)
				// sPattern.get(c).print();
				// System.out.println("GED_fl="+(nodesToAddRemoveRename+edgesToAddRemove));
			}
			System.out.println();
			// ArrayList<Float> sorted_GED_fl=new ArrayList<Float>();
			// for(int i=0; i<GED_fl.size(); i++)

			// Collections.sort(GED_fl);
			//System.out.println("GED_fl list: " + GED_fl.toString());
			//System.out.println("min GED_fl: " + Collections.min(GED_fl) + " max GED_fl: " + Collections.max(GED_fl));

			float min_GED_fl = Collections.min(GED_fl);
			int index = GED_fl.indexOf(min_GED_fl);
			int counter = 0;
			boolean CONTINUE = true;
			while (CONTINUE) {
				counter++;
				// System.out.println("%%%%%%%%%%%% PM_computeExactGED");
				JGraphtClosureGraph g1 = new JGraphtClosureGraph(GUIPatterns.get(index));
				JGraphtClosureGraph g2 = new  JGraphtClosureGraph(candPattern);
				double curr_GED = 0;
				if(g1.getNumNodes()  < g2.getNumNodes()) 
					curr_GED =  GEDMunkres(g1, g2);
				else
					curr_GED =  GEDMunkres(g2, g1);
				/////////////////////
				//int maxv  = g1.getNumNodes() ; if(maxv < g2.getNumNodes() ) maxv =  g2.getNumNodes()  ;
			
				//System.out.println("curr_GED:" + curr_GED );
				
				curr_GED =  curr_GED*1.0f/(g1.getNumEdges()+g1.getNumNodes()+g2.getNumEdges()+g2.getNumNodes());
				// System.out.println("%%%%%%%%%%%% curr_GED="+curr_GED);
				System.out.println("index:" + index +" ,TrueGED="+curr_GED);
				if (curr_GED < GED) {
					GED = curr_GED;
					// if(GED<expectedGdGED)
					// {
					// CONTINUE=false;
					// System.out.println("Can STOP GED computation!!!");
					// }
					// System.out.println("!!!!!!!!! updated GED to "+GED);
				}
				if (CONTINUE) {
					for (int i = 0; i < GED_fl.size(); i++) {
						float v = GED_fl.get(i);
						if (v >= curr_GED && v != Integer.MAX_VALUE)
							GED_fl.set(i, Float.MAX_VALUE);
					}
					GED_fl.set(index, Float.MAX_VALUE);
					min_GED_fl = Collections.min(GED_fl);
					if (min_GED_fl == Float.MAX_VALUE)
						CONTINUE = false;
					else
						index = GED_fl.indexOf(min_GED_fl);
				}
			}
			// System.out.println("************ counter="+counter);

		}

		// System.out.println(">>>>>>>>>>>>>> GED="+GED);
		return GED;
	}
	
	private void computeScore() {
		System.out.println("Starting to run compute score thread " + threadName);
		double maxScore = 0f;
		double minGED_withBetterScore = 0f;
		for (int i = 0; i < patternIndex.size(); i++) {
			System.out.print(patternIndex.get(i)+",");
		}
		System.out.println();
		for (int i = 0; i < distinctCandidatePatterns_closureIndex.size(); i++) {
			System.out.print(distinctCandidatePatterns_closureIndex.get(i)+",");
		}
		System.out.println();
		for (int i = 0; i < patternIndex.size(); i++) {
			int id  =  patternIndex.get(i);
			//added by Kai
			if(distinctCandidatePatterns_closureIndex.size() <= id ) continue;
			
			ArrayList<Integer> dPattern_closureIndexList = distinctCandidatePatterns_closureIndex
					.get(patternIndex.get(i));
			//System.out.println("dPattern_closureIndexList: " + dPattern_closureIndexList.toString());
			int firstClosureIndex = dPattern_closureIndexList.get(0);
			Float clusterWt = c_wt.get(firstClosureIndex);
			if (dPattern_closureIndexList.size() > 0) {
				for (int l = 0; l < dPattern_closureIndexList.size(); l++) {
					int cIndex = dPattern_closureIndexList.get(l);
					clusterWt = clusterWt + c_wt.get(cIndex);
				}
			}
			double minGED = 0F;
			double size = (float) (distinctCandidatePatterns.get(patternIndex.get(i)).size());
			double numEdge = size;
			double numVertex = (float) (getDistinctVertices(distinctCandidatePatterns.get(patternIndex.get(i))));
			double cognitiveCost = 1f;
			double density = 2 * numEdge / (numVertex * (numVertex - 1));
			double score;
			// if(size>=6)
			cognitiveCost = size * density;
			// System.out.println("cognitiveCost="+cognitiveCost);
			if (GUIPatterns.size() == 0)
				minGED = 1F;
			else {
				if (maxScore != 0f)
					minGED_withBetterScore = maxScore * cognitiveCost / clusterWt;
				// minGED_withBetterScore=maxScore+cognitiveCost-clusterWt;
              	minGED = computeGEDMunkres(minGED_withBetterScore, GUIPatterns,distinctCandidatePatterns.get(patternIndex.get(i)));
              	//minGED=computeGEDMunkresWOGEDFl(minGED_withBetterScore, GUIPatterns,distinctCandidatePatterns.get(patternIndex.get(i)));
				
				
				// System.out.println("&&&&&&&&&&&&&&& minGED="+minGED);
			}
			score = clusterWt * minGED / cognitiveCost;
			// score=clusterWt+minGED-cognitiveCost;
			if (score > maxScore)
				maxScore = score;
			candidatePatterns.add(distinctCandidatePatterns.get(patternIndex.get(i)));
			candidatePatterns_closureIndex.add(dPattern_closureIndexList);
			candidatePatterns_score.add(score);
		}
		// System.out.println("Finishing compute score thread "+threadName+"
		// candidatePatterns: "+candidatePatterns.size()+"
		// candidatePatterns_score:"+candidatePatterns_score.toString());
		// for(int i=0; i<candidatePatterns.size(); i++)
		// System.out.println(threadName+" "+i+"of "+candidatePatterns.size()+"
		// candidatePatterns_score="+candidatePatterns_score.get(i));
	}
	
	
	private void computeScoreUpdated() {
		System.out.println("Starting to run compute score thread " + threadName);
		double maxScore = 0f;
		double minGED_withBetterScore = 0f;
		for (int i = 0; i < patternIndex.size(); i++) {
			System.out.print(patternIndex.get(i)+",");
		}
		System.out.println();
		for (int i = 0; i < distinctCandidatePatterns_closureIndex.size(); i++) {
			System.out.print(distinctCandidatePatterns_closureIndex.get(i)+",");
		}
		System.out.println();
		for (int i = 0; i < patternIndex.size(); i++) {
			int id  =  patternIndex.get(i);
			//added by Kai
			if(distinctCandidatePatterns_closureIndex.size() <= id ) continue;
			
			ArrayList<Integer> dPattern_closureIndexList = distinctCandidatePatterns_closureIndex
					.get(patternIndex.get(i));
			//System.out.println("dPattern_closureIndexList: " + dPattern_closureIndexList.toString());
			int firstClosureIndex = dPattern_closureIndexList.get(0);
			Float clusterWt = c_wt.get(firstClosureIndex);
			if (dPattern_closureIndexList.size() > 0) {
				for (int l = 0; l < dPattern_closureIndexList.size(); l++) {
					int cIndex = dPattern_closureIndexList.get(l);
					clusterWt = clusterWt + c_wt.get(cIndex);
				}
			}
			double minGED = 0F;
			double size = (float) (distinctCandidatePatterns.get(patternIndex.get(i)).size());
			double numEdge = size;
			double numVertex = (float) (getDistinctVertices(distinctCandidatePatterns.get(patternIndex.get(i))));
			double cognitiveCost = 1f;
			double density = 2 * numEdge / (numVertex * (numVertex - 1));
			double score;
			// if(size>=6)
			cognitiveCost = size * density;
			// System.out.println("cognitiveCost="+cognitiveCost);
			if (GUIPatterns.size() == 0)
				minGED = 1F;
			else {
				if (maxScore != 0f)
					minGED_withBetterScore = maxScore * cognitiveCost / clusterWt;
				// minGED_withBetterScore=maxScore+cognitiveCost-clusterWt;
				
				if(trietree.isUSEIGED() == false)
					   minGED = computeGEDMunkres(minGED_withBetterScore, GUIPatterns, distinctCandidatePatterns.get(patternIndex.get(i)));
					//minGED from Kai
					else 
						minGED =  computeGEDMunkresWithLowerBoundGED(minGED_withBetterScore, GUIPatterns, distinctCandidatePatterns.get(patternIndex.get(i)));
				
              	//minGED = computeGEDMunkres(minGED_withBetterScore, GUIPatterns,distinctCandidatePatterns.get(patternIndex.get(i)));
              	//minGED=computeGEDMunkresWOGEDFl(minGED_withBetterScore, GUIPatterns,distinctCandidatePatterns.get(patternIndex.get(i)));
				
				
				// System.out.println("&&&&&&&&&&&&&&& minGED="+minGED);
			}
			score = clusterWt * minGED / cognitiveCost;
			// score=clusterWt+minGED-cognitiveCost;
			if (score > maxScore)
				maxScore = score;
			candidatePatterns.add(distinctCandidatePatterns.get(patternIndex.get(i)));
			candidatePatterns_closureIndex.add(dPattern_closureIndexList);
			candidatePatterns_score.add(score);
		}
		// System.out.println("Finishing compute score thread "+threadName+"
		// candidatePatterns: "+candidatePatterns.size()+"
		// candidatePatterns_score:"+candidatePatterns_score.toString());
		// for(int i=0; i<candidatePatterns.size(); i++)
		// System.out.println(threadName+" "+i+"of "+candidatePatterns.size()+"
		// candidatePatterns_score="+candidatePatterns_score.get(i));
	}

	public ArrayList<ArrayList<closureEdge>> getCandidatePatterns() {
		return candidatePatterns;
	}

	public ArrayList<Double> getCandidatePatternsScore() {
		return candidatePatterns_score;
	}

	public ArrayList<ArrayList<Integer>> getCandidatePatternsClosureIndex() {
		return candidatePatterns_closureIndex;
	}

	public String getName() {
		return threadName;
	}

	@Override
	public void doWork() {
		// System.out.println("HERE:: "+threadName);
		//computeScore();
		computeScoreUpdated();
	}
}

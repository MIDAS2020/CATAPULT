#pragma once
#ifndef MCCS_H_
#define MCCS_H_
#include "boost/graph/adjacency_list.hpp"
#include "SingletonStatistic.h"
#include "T_com.h"
#include "boost/graph/graphviz.hpp"
#include <cmath>
#include <assert.h>
#include <iostream>
#include <algorithm>
#include <stdlib.h>
#include <boost/graph/prim_minimum_spanning_tree.hpp>
#include <boost/graph/properties.hpp>
#include <boost/graph/graphviz.hpp>
#include "boost/tuple/tuple.hpp"
#include "SeqTree.h"
#include <list>
#include "prim.h"
#include <limits>
using namespace  std;
using namespace boost;
template<typename Graph>
class mccs {
public:
	SingletonStatistic<Graph>* my_singleton;
	bool FixHeadVerify(int delta, Graph g,
		Graph& q, vector<T_com<Graph> >& seq,
		int l, vector<int>& H,
		vector<int>& F, Graph& mccs_c);
	int MissingBackwardEdges(int l, vector<T_com<Graph> > seq, Graph g, Graph q,
		vector<int> H, vector<int> F, vector<int>& matchingEdges);
	bool Verify(int K, int delta, Graph g, Graph q, vector<T_com<Graph> > seq, Graph& mccs_c);
	int matchSize(Graph g1, Graph g2);
	mccs(Graph newg, Graph newq) {
		g = newg;
		q = newq;
		g_ver_ind_m = get(vertex_index, g);
		q_ver_ind_m = get(vertex_index, q);
		my_singleton = NULL;
	}
	Graph  getMccsValue(Graph q, Graph g);
	bool Verity_Graphs(Graph q, Graph g, int delta, Graph& mccs_c);
	void setSingleton(SingletonStatistic<Graph>* newMYS) {
		this->my_singleton = newMYS;
	}
	mccs() {
		my_singleton = NULL;
		newSeq_mc.setSingleton(my_singleton);
	}
	~mccs() {
	}
private:
	SeqTree<Graph> newSeq_mc;
	typedef typename  graph_traits<Graph>::edges_size_type edge_s;
	typedef typename graph_traits<Graph>::vertices_size_type ver_s;
	typedef typename graph_traits<Graph>::vertex_descriptor ver_t;
	typedef typename  graph_traits<Graph>::edge_descriptor edge_t;
	typedef typename graph_traits<Graph>::vertex_iterator ver_i;
	typedef typename  graph_traits<Graph>::edge_iterator edge_i;
	typedef typename  graph_traits<Graph>::adjacency_iterator adj_i;
	typedef typename graph_traits<Graph>::degree_size_type degree_s;
	typedef typename property_map<Graph, vertex_name_t>::type ver_nam;
	typedef typename  property_map<Graph, edge_index_t>::type edge_ind;
	typedef typename property_map<Graph, vertex_index_t>::type ver_ind;
	typedef typename property_map<Graph, vertex_distance_t>::type ver_dis;
	//typedef typename property_map<Graph, edge_weight_t>::type edge_wei;
	ver_ind g_ver_ind_m, q_ver_ind_m;
	Graph g, q;
};
template <typename Graph>
Graph  mccs<Graph>::getMccsValue(Graph q, Graph g) {
	int firsThreshold = num_edges(q) - 1;
	ver_i ver_i_b, ver_i_e;
	Graph mccs;
	while (firsThreshold >= 0) {
		Graph returnGraph(num_vertices(q));
		bool testFlag_t = Verity_Graphs(q, g, firsThreshold, returnGraph);
		if (testFlag_t) {
			mccs = returnGraph;
			firsThreshold = firsThreshold - 1;
		}
		else {
			break;
		}
	}
	return mccs;
}
template <typename Graph>
bool mccs<Graph>::Verity_Graphs(Graph q, Graph g, int delat, Graph & returnGraph) {
	vector<T_com<Graph> > Seq_tees = newSeq_mc.GenerateSEQ(q);
	Graph newGraph, newGraphTmp1;
	int K = num_edges(q);
	if (Verify(K, delat, g, q, Seq_tees, newGraph)) {
		returnGraph = newGraph;
		return true;
	}
	else
		return false;
}
template <typename Graph>
bool mccs<Graph>::Verify(int K, int delta, Graph g, Graph q, vector<T_com<Graph> > seq, Graph& returnGraph) {
	vector<int> H(num_vertices(q));
	vector<int> F(num_vertices(g));
	if (FixHeadVerify(delta, g, q, seq, 1, H, F, returnGraph)) {
		return true;
	}
	else {
		vector<vector< T_com<Graph> > > SEQ_sub = newSeq_mc.Decompose(seq, q, K, delta);
		typedef typename  vector<vector< T_com<Graph> > >::iterator SEQ_sub_I_t;
		for (SEQ_sub_I_t SEQ_sub_i = SEQ_sub.begin();
			SEQ_sub_i != SEQ_sub.end();
			SEQ_sub_i++) {
			Graph tempGraph = newSeq_mc.SEQtoGraph(*SEQ_sub_i, q);
			if (Verify(K, delta - (K - num_edges(tempGraph)), g, q, *SEQ_sub_i, returnGraph)) {
				return true;
			}
		}

		return false;

	}
}
template <typename Graph>
bool mccs<Graph>::FixHeadVerify(int delta, Graph g, Graph& q, vector< T_com<Graph> >& seq,
	int l, vector<int>& H, vector<int>& F,
	Graph& returnGraph) {
	//    cout <<"l: "<<l << endl;
	if (newSeq_mc.UnmatchedEdges(seq, l) <= delta)
		return true;
	vector<int> candidateV;
	candidateV.clear();
	T_com<Graph> tempT_com = seq[l - 1];
	string ver_label = tempT_com.label;
	int ver_pre = tempT_com.previous;
	ver_t ver_v = tempT_com.ver;
	vector<int> back_edge_set = tempT_com.edge;
	ver_ind g_ver_index = get(vertex_index, g);
	ver_ind q_ver_index = get(vertex_index, q);
	ver_nam g_ver_name = get(vertex_name, g);
	int q_ver_pre_tmp1 = q_ver_index[seq[ver_pre - 1].ver];
	int q_ver_now_tmp1 = q_ver_index[ver_v];
	/* add_edge( vertex(  q_ver_pre_tmp1, returnGraph), */
	/*           vertex(q_ver_now_tmp1, returnGraph), */
	/*           returnGraph); */
	if (l == 1) {
		ver_i temp_i_b, temp_i_e;
		tie(temp_i_b, temp_i_e) = vertices(g);
		for (ver_i temp_i = temp_i_b; temp_i != temp_i_e; temp_i++) {
			if (g_ver_name[*temp_i].compare(ver_label) == 0) {
				candidateV.push_back(g_ver_index[*temp_i]);
				F[g_ver_index[*temp_i]] = 0;
			}
		}
	}
	else {
		ver_t ver_pre_v = seq[ver_pre - 1].ver;
		int ver_pre_ind = q_ver_index[ver_pre_v];
		int g_ind_v = H[ver_pre_ind];
		ver_t ver_g_v = vertex(g_ind_v, g);
		adj_i v_adj_b, v_adj_e;
		tie(v_adj_b, v_adj_e) = adjacent_vertices(ver_g_v, g);
		for (adj_i adj_i_tmp = v_adj_b; adj_i_tmp != v_adj_e; adj_i_tmp++) {
			int g_ver_index1 = g_ver_index[*adj_i_tmp];
			if ((F[g_ver_index1] == 0) && ((g_ver_name[*adj_i_tmp]).compare(ver_label) == 0)) {
				candidateV.push_back(g_ver_index[*adj_i_tmp]);
			}
		}

	}
	vector<int>::iterator temp_v_int_b;
	for (temp_v_int_b = candidateV.begin(); temp_v_int_b != candidateV.end(); temp_v_int_b++) {
		//               cout << " index g: "<< *temp_v_int_b<< endl;
		for (vector<int>::iterator int_i_tmp1 = H.begin(); int_i_tmp1 != H.end(); int_i_tmp1++) {
			if ((*int_i_tmp1) == *temp_v_int_b) {
				H[int_i_tmp1 - H.begin()] = -1;
			}
		}

		H[q_ver_now_tmp1] = *temp_v_int_b;
		F[(*temp_v_int_b)] = 1;
		vector<int> matchingEdges;
		/* ostream_iterator<int> testIS(cout, ","); */
		/* cout <<" F: " << endl; */
		/* copy(F.begin(), F.end(), testIS); */
		/* cout << endl; */
		/*         cout <<" H: " << endl; */
		/* copy(H.begin(), H.end(), testIS); */
		/* cout << endl; */

		int epsilon = MissingBackwardEdges(l, seq, g, q, H, F, matchingEdges);
		//        cout << "epsilon : "<< epsilon << endl;
		for (vector<int>::iterator vec_int_i = matchingEdges.begin(); vec_int_i != matchingEdges.end(); vec_int_i++) {
			int previous_int = *vec_int_i;
			//            add_edge( vertex(  previous_int, returnGraph), vertex(q_ver_now_tmp1, returnGraph),returnGraph);
		}
		if (epsilon <= delta) {
			if (FixHeadVerify(delta - epsilon, g, q, seq, l + 1, H, F, returnGraph)) {
				Graph tempGraph_1 = newSeq_mc.SEQtoGraph(seq, q);
				edge_i edge_i_b_t, edge_i_e_t;
				tie(edge_i_b_t, edge_i_e_t) = edges(tempGraph_1);
				for (edge_i edge_i_tmp = edge_i_b_t; edge_i_tmp != edge_i_e_t; edge_i_tmp++) {
					edge_t newEdge = *edge_i_tmp;
					ver_t ver_source = source(newEdge, tempGraph_1);
					ver_t ver_target = target(newEdge, tempGraph_1);
					ver_ind ver_ind_t = get(vertex_index, q);
					int ver_source_in = ver_ind_t[ver_source];
					int ver_target_in = ver_ind_t[ver_target];
					int g_index_source = H[ver_source_in];
					int g_index_target = H[ver_target_in];
					if ((g_index_source == -1) || (g_index_target == -1)) {
						remove_edge(ver_source, ver_target, tempGraph_1);
					}
					else {
						ver_t ver_g_source = vertex(g_index_source, g);
						ver_t ver_g_target = vertex(g_index_target, g);
						edge_t tempedge_t;
						bool testFlag;
						tie(tempedge_t, testFlag) = edge(ver_g_source, ver_g_target, g);
						if (!testFlag) {
							remove_edge(ver_source, ver_target, tempGraph_1);
						}
					}
				}
				returnGraph = tempGraph_1;
				return true;
			}
		}
		F[(*temp_v_int_b)] = 0;
	}
	if ((delta >= 1) && (l >= 2)) {
		//        cout <<" l: " <<l << "delat :"<< delta<< endl;
		int newEpsilon;
		bool fromPre = false;
		fromPre = (seq[l - 1].edge.size()>0) ? false : true;
		vector<T_com<Graph> > newSeq = newSeq_mc.NewSeqPre(seq, l, q, fromPre, newEpsilon);
		if (newSeq.size() >= l) {
			if (newEpsilon <= delta) {
				if (FixHeadVerify(delta - newEpsilon, g, q, newSeq, l, H, F, returnGraph)) {
					return true;
				}
			}
		}
	}
	return false;
}

template <typename Graph>
int mccs<Graph>::MissingBackwardEdges(int l, vector<T_com<Graph> > seq, Graph g, Graph q, vector<int>  H, vector<int> F, vector<int>& matchingEdges) {
	if (l == 1) {
		return 0;
	}
	int missingEdges = 0;
	T_com<Graph> tempT_com = seq[l - 1];
	ver_ind q_ver_ind = get(vertex_index, q);
	ver_t ver_v = tempT_com.ver;
	int q_ver_now = q_ver_ind[ver_v];
	int g_ver_now = H[q_ver_now];
	ver_t ver_v_g_now = vertex(g_ver_now, g);
	vector<int> back_edge_set = tempT_com.edge;
	if (back_edge_set.size() == 0) {
		return 0;
	}
	else {
		for (vector<int>::iterator vec_temp = back_edge_set.begin(); vec_temp != back_edge_set.end(); vec_temp++) {
			int tree_ind = *vec_temp;
			T_com<Graph> tempT_pre = seq[tree_ind - 1];
			ver_t ver_v_pre = tempT_pre.ver;
			int q_ver_pre = q_ver_ind[ver_v_pre];
			int g_ver_pre = H[q_ver_pre];
			ver_t ver_v_g_pre = vertex(g_ver_pre, g);
			edge_t temp_edge;
			bool hasEdges;
			tie(temp_edge, hasEdges) = edge(ver_v_g_now, ver_v_g_pre, g);
			if (hasEdges == false) {
				missingEdges += 1;
			}
			else {
				matchingEdges.push_back(q_ver_pre);
			}
		}
		return missingEdges;
	}

}
#endif

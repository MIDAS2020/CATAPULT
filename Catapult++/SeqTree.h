#pragma once
#ifndef SEQTREE_H_
#define SEQTREE_H_
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
#include <list>
#include "prim.h"
#include "boost/graph/connected_components.hpp"

#include <limits>
using namespace  std;
using namespace boost;
template<typename Graph>
class SeqTree {
public:
	SingletonStatistic<Graph>* my_singleton;
	//test
	int UnmatchedEdges(vector<T_com<Graph> >  seq, int l);
	//test
	Graph SEQtoGraph(vector<T_com<Graph> > seq, Graph g1) const;
	//test
	vector<T_com<Graph> > GenerateSEQPre(Graph g1, vector<int> previous);
	//test
	int selectfirstV(Graph newGraph);
	//test
	vector< vector<T_com<Graph> > > Decompose(vector<T_com<Graph> > preSeq, Graph initialGraph, int K, int threshold_New);
	//test
	void setSingleton(SingletonStatistic<Graph>* newMYS) {
		this->my_singleton = newMYS;
	}
	//test
	vector<std::tuple<Graph, int> >  get_connected_sub(Graph origin_graph);
	//test
	vector<T_com<Graph> > NewSeqPre(vector<T_com<Graph> > seq, int l, Graph q, bool fromSe, int& epsilon);
	//    vector<T_com<Graph> > GetNewSeq(vector<T_com<Graph> > seq, int l, Graph q, bool& firstflag);
	//test

	vector<T_com<Graph> > GenerateSEQ(Graph g1);
	SeqTree() {
	}
	~SeqTree() {
	}
private:
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
	typedef typename property_map<Graph, edge_weight_t>::type edge_wei;
};

template <typename Graph>
vector< vector<T_com<Graph> > > SeqTree<Graph>::Decompose(vector<T_com<Graph> > preSeq,
	Graph initialGraph, int K, int newTHreshold) {
	Graph newGraph_t = SEQtoGraph(preSeq, initialGraph);
	initialGraph = newGraph_t;
	ver_ind ver_ind_ini = get(vertex_index, initialGraph);
	int first_v = ver_ind_ini[preSeq[0].ver];
	typedef typename graph_traits<Graph>::out_edge_iterator out_ed_i;
	out_ed_i out_ed_b, out_ed_e;
	tie(out_ed_b, out_ed_e) = out_edges(preSeq[0].ver, initialGraph);
	vector<tuple<int, int> > adjacent_v;
	for (out_ed_i out_ed_tmp = out_ed_b; out_ed_tmp != out_ed_e; out_ed_tmp++) {
		adjacent_v.push_back(make_tuple(
			ver_ind_ini[source(*out_ed_tmp, initialGraph)],
			ver_ind_ini[target(*out_ed_tmp, initialGraph)]));
	}
	for (vector<tuple<int, int> >::iterator tup_i_tmp_1 = adjacent_v.begin(); tup_i_tmp_1 != adjacent_v.end();
		tup_i_tmp_1++) {
		remove_edge(
			vertex(get<0>(*tup_i_tmp_1), initialGraph),
			vertex(get<1>(*tup_i_tmp_1), initialGraph),
			initialGraph);
	}
	vector<tuple<Graph, int> > SubGraphs = get_connected_sub(initialGraph);
	vector<vector<T_com<Graph> > > Seq_candidats;
	typedef typename vector<tuple<Graph, int> >::iterator Graph_int_i;
	for (Graph_int_i sub_i = SubGraphs.begin(); sub_i != SubGraphs.end(); sub_i++) {
		vector<T_com<Graph> > Seq_tmp;
		int num_tre_ver = get<1>(*sub_i);
		if (num_tre_ver >= 1) {
			if ((K - num_tre_ver) <= newTHreshold) {
				Seq_candidats.push_back(GenerateSEQ(get<0>(*sub_i)));
			}
		}
	}
	return Seq_candidats;
}

template <typename Graph>
vector<std::tuple<Graph, int> > SeqTree<Graph>::get_connected_sub(Graph initialGraph) {
	vector<int> component(num_vertices(initialGraph));
	int totalNum = connected_components(initialGraph, &component[0]);
	ver_ind ver_ind_ini = get(vertex_index, initialGraph);
	vector<vector<int> > sub_graph_ver(totalNum);
	vector<int>::size_type i;
	for (i = 0; i != component.size(); i++) {
		sub_graph_ver[component[i]].push_back(i);
	}
	vector<tuple<Graph, int> > sub_graphs;
	edge_i edge_i_b, edge_i_e;
	tie(edge_i_b, edge_i_e) = edges(initialGraph);
	for (vector<vector<int> >::iterator sub_graph_ver_i = sub_graph_ver.begin(); sub_graph_ver_i != sub_graph_ver.end();
		sub_graph_ver_i++) {
		vector<int> containV = *sub_graph_ver_i;
		vector<tuple<int, int> > to_be_removed_edges;
		for (edge_i edge_i_tmp = edge_i_b; edge_i_tmp != edge_i_e; edge_i_tmp++) {
			int source_ind = ver_ind_ini[source(*edge_i_tmp, initialGraph)];
			int target_ind = ver_ind_ini[target(*edge_i_tmp, initialGraph)];
			if ((find(containV.begin(), containV.end(), source_ind) == containV.end()) && ((find(containV.begin(), containV.end(), target_ind) == containV.end()))) {
				to_be_removed_edges.push_back(make_tuple(source_ind, target_ind));
			}
		}
		Graph newGraph = initialGraph;
		for (vector<tuple<int, int> >::iterator tup_i_tmp = to_be_removed_edges.begin();
			tup_i_tmp != to_be_removed_edges.end();
			tup_i_tmp++) {
			remove_edge(vertex(get<0>(*tup_i_tmp), initialGraph),
				vertex(get<1>(*tup_i_tmp), initialGraph),
				newGraph);
		}
		sub_graphs.push_back(make_tuple(newGraph, num_edges(newGraph)));
	}
	return sub_graphs;
}

// get Unmatched Edges
template <typename Graph>
int  SeqTree<Graph>::UnmatchedEdges(vector<T_com<Graph> > seq, int l) {
	size_t seq_s = seq.size();
	int toMis = 0;
	for (int i = l - 1; i < seq_s; i++) {
		toMis += seq[i].edge.size();
	}
	toMis += seq_s - l + 1;
	return toMis;
}
//1 2 3 4
template <typename Graph>
vector< T_com<Graph> > SeqTree<Graph>::GenerateSEQ(Graph g1) {
	int index_first = selectfirstV(g1);
	vector<T_com<Graph> > tempTree_n;
	vector<int> newVes;
	newVes.push_back(index_first);
	tempTree_n = GenerateSEQPre(g1, newVes);
	return tempTree_n;
}

// generate SEQ previous
template <typename Graph>
vector< T_com<Graph> > SeqTree<Graph>::GenerateSEQPre(Graph g1, vector<int> previous) {
	edge_wei weightmap = get(edge_weight, g1);
	std::vector < int>  p(num_vertices(g1));
	edge_i it1_ed, it2_ed;
	ver_dis distance = get(vertex_distance, g1);
	ver_ind indexmap = get(vertex_index, g1);
	ver_nam  ve_name = get(vertex_name, g1);
	vector<ver_t> testV;
	vector<edge_t> edge_set;
	map<ver_t, int> find_time;
	prim<Graph> testprim(g1);
	vector<int> test1 = previous;
	testprim.generatePrim(test1);
	p = testprim.parent;
	edge_set = testprim.backEdges;
	vector<int> find_temp = testprim.findtime;
	for (vector<int>::iterator vec_int_tmp = find_temp.begin(); vec_int_tmp != find_temp.end(); vec_int_tmp++) {
		find_time[(vertex(vec_int_tmp - find_temp.begin(), g1))] = *vec_int_tmp;
	}
	ver_i it1, it2;
	testV = testprim.vertex_S;
	vector<T_com<Graph> > SEQ(testV.size());
	typedef typename vector<ver_t>::iterator Vec_i;
	int i = 0;
	for (Vec_i ve_it1 = testV.begin(); ve_it1 != testV.end(); ve_it1++) {
		T_com<Graph> newCom;
		if (p[indexmap[*ve_it1]] != indexmap[*ve_it1]) {
			int previous_index = p[indexmap[*ve_it1]];
			newCom.previous = find_time[vertex(previous_index, g1)] + 1;
		}
		else
			newCom.previous = 0;
		newCom.label = ve_name[*ve_it1];
		degree_s new_size_d;
		new_size_d = out_degree(*ve_it1, g1);
		int size_d = lexical_cast<int>(new_size_d);
		newCom.degree = size_d;
		newCom.ver = *ve_it1;
		SEQ[ve_it1 - testV.begin()] = newCom;
	}
	typedef typename vector<edge_t>::iterator vec_ie;
	for (vec_ie e_it1 = edge_set.begin(); e_it1 != edge_set.end(); e_it1++) {
		ver_t sour_ve = source(*e_it1, g1);
		ver_t tar_ve = target(*e_it1, g1);
		int index1 = find_time[sour_ve];
		int index2 = find_time[tar_ve];
		int index_min = min(index1, index2);
		int index_max = max(index1, index2);
		SEQ[index_max].edge.push_back(index_min + 1);
	}
	return SEQ;

}

template<typename Graph>
int SeqTree<Graph>::selectfirstV(Graph newGraph) {
	edge_wei edge_wei_n = get(edge_weight, newGraph);
	edge_i edge_i_b, edge_i_e;
	tie(edge_i_b, edge_i_e) = edges(newGraph);
	vector<edge_t> P;
	double max_weight = numeric_limits<double>::max();
	for (edge_i edge_i_tmp = edge_i_b; edge_i_tmp != edge_i_e; edge_i_tmp++) {
		double weight_now = edge_wei_n[*edge_i_tmp];
		if (weight_now < max_weight) {
			max_weight = weight_now;
			P.clear();
			P.push_back(*edge_i_tmp);
		}
		else if (abs(max_weight - weight_now) < 0.00000001) {
			P.push_back(*edge_i_tmp);
		}

	}
	vector<int> degreeSize;
	typedef typename vector<edge_t>::iterator edge_i_11;
	for (edge_i_11 edges_i_t = P.begin(); edges_i_t != P.end(); edges_i_t++) {
		degreeSize.push_back(out_degree(source(*edges_i_t, newGraph), newGraph) +
			out_degree(target(*edges_i_t, newGraph), newGraph));
	}
	vector<int>::iterator min_element_i = std::min_element(degreeSize.begin(), degreeSize.end());
	//    cout <<"selected minimum element "<< min_element_i - degreeSize.begin() << endl;
	edge_t newEdge1 = P[min_element_i - degreeSize.begin()];
	ver_t ver_t_source, ver_t_target;
	ver_t_source = source(newEdge1, newGraph);
	ver_t_target = target(newEdge1, newGraph);
	ver_nam ver_nam_ind_t = get(vertex_name, newGraph);
	string ver_t_source_s = ver_nam_ind_t[ver_t_source];
	string ver_t_target_s = ver_nam_ind_t[ver_t_target];
	ver_ind ver_ind_t = get(vertex_index, newGraph);
	ver_t first_v;
	if (my_singleton == NULL) {
		return ver_ind_t[ver_t_source];
	}
	else {
		double source_average = this->my_singleton->v_average_count[ver_t_source_s];
		double target_average = this->my_singleton->v_average_count[ver_t_target_s];
		if (abs(source_average - target_average) < 0.00000001) {
			first_v = (out_degree(ver_t_source, newGraph) < out_degree(ver_t_target, newGraph)) ? ver_t_target : ver_t_source;
		}
		else {
			first_v = (source_average < target_average) ? ver_t_source : ver_t_target;
		}
		return ver_ind_t[first_v];
	}

}
// get New Seq
template <typename Graph>
vector<T_com<Graph> > SeqTree<Graph>::NewSeqPre(vector<T_com<Graph> > seq,
	int l, Graph q, bool fromPre,
	int& epsilon) {
	T_com<Graph> T_com_cur = seq[l - 1];
	int pre_ind = T_com_cur.previous;
	ver_t ver_now_t = T_com_cur.ver;
	ver_t ver_pre_t = seq[pre_ind - 1].ver;
	if (!fromPre) {
		epsilon = 1;
		vector<int> newEdges = seq[l - 1].edge;
		int size_t = newEdges.size();
		assert(size_t > 0);
		int newPre = newEdges[0];
		seq[l - 1].previous = newPre;
		seq[l - 1].edge.erase((seq[l - 1]).edge.begin());
		return seq;
	}
	else {
		Graph tempGraph = SEQtoGraph(seq, q);
		remove_edge(ver_now_t, ver_pre_t, tempGraph);
		vector<int> BackEdges = seq[l - 1].edge;
		for (vector<int>::iterator vec_int_tmp = BackEdges.begin(); vec_int_tmp != BackEdges.end();
			vec_int_tmp++) {
			ver_t ver_pre_n = seq[*vec_int_tmp - 1].ver;
			remove_edge(ver_now_t, ver_pre_n, tempGraph);
		}
		epsilon = seq[l - 1].edge.size() + 1;
		vector<int> NewVertices;
		ver_ind ver_ind_t = get(vertex_index, tempGraph);
		for (int i = 0; i< l - 1; i++) {
			NewVertices.push_back(ver_ind_t[seq[i].ver]);
		}
		assert(NewVertices.size() == l - 1);
		return GenerateSEQPre(tempGraph, NewVertices);
	}


}
template <typename Graph>
Graph SeqTree<Graph>::SEQtoGraph(vector<T_com<Graph> >  seq, Graph g1) const {
	edge_i edge_i_b, edge_i_e;
	tie(edge_i_b, edge_i_e) = edges(g1);
	/* for( edge_i edge_i_tmp = edge_i_b; edge_i_tmp != edge_i_e; edge_i_tmp++){ */
	/*     cout << "index1---------" << endl; */
	/* } */
	Graph tempGraph1 = g1;
	edge_ind edge_ind_m = get(edge_index, g1);
	int pre_tmp;
	ver_t ver_tmp_n;
	T_com<Graph> tempTSEQ;
	for (typename vector<T_com<Graph> >::iterator seq_i_b = seq.begin(); seq_i_b != seq.end(); seq_i_b++) {
		tempTSEQ = *seq_i_b;
		pre_tmp = tempTSEQ.previous;
		ver_tmp_n = tempTSEQ.ver;
		vector<int> tempEdge = tempTSEQ.edge;
		edge_t edge_tmp;
		ver_t pre_ver_t;
		if (pre_tmp > 0) {
			pre_ver_t = seq[pre_tmp - 1].ver;
			remove_edge(pre_ver_t, ver_tmp_n, tempGraph1);
		}
		if (tempEdge.size()>0) {
			for (vector<int>::iterator temp_i = tempEdge.begin(); temp_i != tempEdge.end(); temp_i++) {
				pre_ver_t = seq[(*temp_i) - 1].ver;
				remove_edge(pre_ver_t, ver_tmp_n, tempGraph1);
			}
		}
	}

	tie(edge_i_b, edge_i_e) = edges(tempGraph1);
	for (edge_i edge_i_tmp = edge_i_b; edge_i_tmp != edge_i_e; edge_i_tmp++) {
		ver_t source_ver = source(*edge_i_tmp, tempGraph1);
		ver_t target_ver = target(*edge_i_tmp, tempGraph1);
		remove_edge(source_ver, target_ver, g1);
	}

	return g1;
	/* return g1; */
}
/* template <typename Graph> */
/* vector<T_com<Graph> > SeqTree<Graph>::GetNewSeq(vector<T_com<Graph> > seq, int l, Graph q, bool& firstFlag){ */
/*     T_com<Graph> newTreeCom = seq[l-1]; */
/*     vector<int> edge_tmp = newTreeCom.edge; */
/*     int T_cur_pre = newTreeCom.previous; */
/*     ver_t T_cur_v = newTreeCom.ver; */
/*     if(edge_tmp.size() > 0){ */
/*         firstFlag = true; */
/*         int newpre = edge_tmp[0]; */
/*         edge_tmp.erase(edge_tmp.begin()); */
/*         seq[l-1].previous = newpre; */
/*         seq[l-1].edge = edge_tmp; */
/*         return seq; */
/*     }else{ */
/*         firstFlag = false; */
/*         ver_ind ver_ind_m = get(vertex_index, q); */
/*         vector<int> preVer(l-1); */
/*         for(int i = 0; i< l-1 ;i++){ */
/*             preVer[i] = ver_ind_m[  (seq[i].ver)]; */
/*         } */
/*         Graph newQ = q; */
/*         remove_edge( seq[l-1].ver, seq[( seq[l-1].previous -1)].ver, newQ); */
/*     } */
/* } */

#endif

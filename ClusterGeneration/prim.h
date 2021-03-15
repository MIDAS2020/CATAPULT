#pragma once
#ifndef PRIM_H_
#define PRIM_H_
#include "boost/graph/adjacency_list.hpp"
#include "T_com.h"
#include <algorithm>
#include <iostream>
#include <stdlib.h>
#include <boost/graph/prim_minimum_spanning_tree.hpp>
#include "boost/heap/fibonacci_heap.hpp"
#include "boost/tuple/tuple.hpp"
#include <climits>
#include <iomanip>
using namespace std;
using namespace boost;
using boost::heap::fibonacci_heap;
class EdgeWeight
{
public:
	int edgeindex;
	int weight;
	std::tuple<int, int> edges;
	EdgeWeight(int x, int newWeight) {
		edgeindex = x;
		weight = newWeight;
		edges = make_tuple(edgeindex, weight);
	}

	bool operator<(const EdgeWeight& other) const {
		return get<1>(edges) >= get<1>(other.edges);
	}
};

template <typename Graph>
class prim {
public:
	typedef typename  graph_traits<Graph>::edge_descriptor edge_t;
	typedef typename graph_traits<Graph>::vertex_descriptor ver_t;
	Graph G;
	fibonacci_heap<EdgeWeight> Q;
	vector<int> findtime;
	vector<int>  parent;
	vector<edge_t> backEdges;
	vector<ver_t> vertex_S;
	prim() {
	}
	prim(Graph g) {
		G = g;
	}
	void generatePrim(vector<int> preVertices);
	void getBackEdges(vector<int> parent, Graph g);
private:
	typedef typename  graph_traits<Graph>::edges_size_type edge_s;
	typedef typename graph_traits<Graph>::vertices_size_type ver_s;
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
void  prim<Graph>::getBackEdges(vector<int> parent, Graph g) {
	ver_ind ver_ind_m = get(vertex_index, g);
	vector<edge_t> tempEdges;
	int index_v;
	for (vector<int>::iterator vec_i = parent.begin(); vec_i != parent.end(); vec_i++) {
		index_v = vec_i - parent.begin();
		if ((index_v != *vec_i) && (*vec_i != -1)) {
			remove_edge(vertex(*vec_i, g), vertex(index_v, g), g);
		}
	}
	edge_i edge_b, edge_e;
	tie(edge_b, edge_e) = edges(g);
	edge_t edge_tmp_n;
	for (edge_i edge_tmp = edge_b; edge_tmp != edge_e; edge_tmp++) {
		edge_tmp_n = *edge_tmp;
		ver_t source_v_t = source(edge_tmp_n, g);
		int source_int_t = ver_ind_m[source_v_t];
		ver_t target_v_t = target(edge_tmp_n, g);
		int target_int_t = ver_ind_m[target_v_t];
		if ((parent[source_int_t] != -1) && (parent[target_int_t] != -1))
			tempEdges.push_back(*edge_tmp);
	}
	backEdges = tempEdges;
}
template<typename Graph>
void prim<Graph>::generatePrim(vector<int> preVertices) {
	findtime.clear();
	parent.clear();
	backEdges.clear();
	vertex_S.clear();
	int pre_ver_numbers = preVertices.size();
	int r_index = preVertices[0];
	ver_t r = vertex(r_index, G);
	ver_i ver_b, ver_e;
	ver_dis ver_dis_m = get(vertex_distance, G);
	ver_ind ver_ind_m = get(vertex_index, G);
	edge_wei edge_wei_m = get(edge_weight, G);
	ver_s  ver_size = num_vertices(G);
	int  parent_tmp[ver_size];
	int ver_find_time[ver_size];
	int ver_visited[ver_size];
	std::fill_n(ver_visited, ver_size, 1);
	std::fill_n(parent_tmp, ver_size, -1);
	std::fill_n(ver_find_time, ver_size, -1);
	tie(ver_b, ver_e) = vertices(G);
	for (ver_i ver_tmp = ver_b; ver_tmp != ver_e; ver_tmp++) {
		ver_dis_m[*ver_tmp] = INT_MAX;
	}
	ver_dis_m[r] = 0;
	list<ver_t> tmp_list;
	tmp_list.push_back(r);
	int tempV_s_l[ver_size];
	std::fill_n(tempV_s_l, ver_size, 0);
	tempV_s_l[r_index] = 1;
	while (!tmp_list.empty()) {
		adj_i i_adj_tmp_b, i_adj_tmp_e;
		ver_t ver_now_t = tmp_list.front();
		Q.push(EdgeWeight(ver_ind_m[(ver_now_t)], ver_dis_m[ver_now_t]));
		tmp_list.pop_front();
		tie(i_adj_tmp_b, i_adj_tmp_e) = adjacent_vertices(ver_now_t, G);
		for (adj_i adj_i_tmp = i_adj_tmp_b; adj_i_tmp != i_adj_tmp_e; adj_i_tmp++) {
			if ((tempV_s_l[ver_ind_m[*adj_i_tmp]] == 0)) {
				tempV_s_l[ver_ind_m[*adj_i_tmp]] = 1;
				tmp_list.push_back(*adj_i_tmp);
			}

		}
	}

	parent_tmp[r_index] = r_index;
	int find_time_ind = 0;
	int i_tmp_v = 0;
	while (!Q.empty()) {
		int ver_index, weight_tmp;
		if (i_tmp_v < pre_ver_numbers) {
			int current_ind = preVertices[i_tmp_v];
			fibonacci_heap<EdgeWeight> tempQ;
			while (!Q.empty()) {
				int ver_index_1, weight_tmp_1;
				tie(ver_index_1, weight_tmp_1) = Q.top().edges;
				if (ver_index_1 == current_ind) {
					tie(ver_index, weight_tmp) = Q.top().edges;
				}
				else {

					tempQ.push(Q.top());
				}
				Q.pop();
			}
			Q = tempQ;
			i_tmp_v++;
		}
		else {
			tie(ver_index, weight_tmp) = Q.top().edges;
			Q.pop();
		}
		vertex_S.push_back(vertex(ver_index, G));
		ver_find_time[ver_index] = find_time_ind++;
		ver_t u = vertex(ver_index, G);
		ver_visited[ver_index] = 0;
		adj_i adj_i_b, adj_i_e;
		tie(adj_i_b, adj_i_e) = adjacent_vertices(u, G);
		for (adj_i adj_tmp = adj_i_b; adj_tmp != adj_i_e; adj_tmp++) {
			ver_t ver_tmp = *adj_tmp;
			if (ver_visited[ver_ind_m[ver_tmp]] == 1) {
				edge_t tempEdge;
				bool tempBool;
				tie(tempEdge, tempBool) = edge(u, ver_tmp, G);
				if (edge_wei_m[tempEdge] < ver_dis_m[ver_tmp]) {
					int v_tmp_ind = ver_ind_m[ver_tmp];
					parent_tmp[v_tmp_ind] = ver_index;
					ver_dis_m[ver_tmp] = edge_wei_m[tempEdge];
					fibonacci_heap<EdgeWeight> tempQ;
					while (!Q.empty()) {
						EdgeWeight tempEdgeW = Q.top();
						Q.pop();
						int v_now_ind = tempEdgeW.edgeindex;
						int weight_tmp;
						if (v_now_ind == v_tmp_ind) {
							weight_tmp = ver_dis_m[ver_tmp];
						}
						else {
							weight_tmp = tempEdgeW.weight;
						}
						tempQ.push(EdgeWeight(v_now_ind, weight_tmp));
					}
					Q = tempQ;
				}
			}

		}
	}

	for (int i = 0; i<ver_size; i++) {
		findtime.push_back(ver_find_time[i]);
		parent.push_back(parent_tmp[i]);
	}
	getBackEdges(parent, G);
}

#endif

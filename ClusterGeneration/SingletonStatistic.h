#pragma once
#ifndef SINGLETONSTATISTIC_H_
#define SINGLETONSTATISTIC_H_
#include <map>
#include <iostream>
#include "boost/tuple/tuple.hpp"
#include "boost/tuple/tuple_comparison.hpp"
using namespace std;
using namespace boost;
template <typename Graph>
class SingletonStatistic {
public:

	map<string, long int> v_inner_count;
	map<string, long int> v_total_count;
	map<string, double> v_average_count;

	map< std::tuple<string, string>, long int> e_inner_count;
	map< std::tuple<string, string>, long int> e_total_count;
	map< std::tuple<string, string>, double> e_average_count;

	SingletonStatistic() {
	}
	void add_graph(Graph newGraph);
private:
	string VertexLabel;
	std::tuple<string, string> Edges;
	typedef typename graph_traits<Graph>::vertex_iterator ver_i;
	typedef typename graph_traits<Graph>::edge_iterator edge_i;
	typedef typename property_map<Graph, vertex_name_t>::type ver_nam;

};
template <typename Graph>
void SingletonStatistic<Graph>::add_graph(Graph newGraph) {

	ver_i ver_it_b, ver_it_e;
	tie(ver_it_b, ver_it_e) = vertices(newGraph);
	ver_nam ver_nam_tmp_1 = get(vertex_name, newGraph);
	map<string, int> TempVerCount;
	for (ver_i ver_i_tmp = ver_it_b; ver_i_tmp != ver_it_e; ver_i_tmp++) {
		string ver_name_str = ver_nam_tmp_1[*ver_i_tmp];
		if (TempVerCount.find(ver_name_str) == TempVerCount.end())
			TempVerCount[ver_name_str] = 1;
		else
			TempVerCount[ver_name_str] = TempVerCount[ver_name_str] + 1;
	}
	/* map<string,unsigned int> v_inner_count; */


	/* map<string,unsigned long int> v_total_count; */
	map<string, int>::iterator map_i_t;
	for (map_i_t = TempVerCount.begin(); map_i_t != TempVerCount.end(); map_i_t++) {
		string map_str_11 = map_i_t->first;
		int value_tmp = map_i_t->second;
		if (v_inner_count.find(map_str_11) == v_inner_count.end())
			v_inner_count[map_str_11] = 1;
		else
			v_inner_count[map_str_11] = v_inner_count[map_str_11] + 1;
		if (v_total_count.find(map_str_11) == v_total_count.end())
			v_total_count[map_str_11] = 1;
		else
			v_total_count[map_str_11] = v_total_count[map_str_11] + value_tmp;


	}
	edge_i edge_i_b, edge_i_e;
	tie(edge_i_b, edge_i_e) = edges(newGraph);
	map<std::tuple<string, string>, int> TempEdgeCount;
	for (edge_i edge_i_tmp = edge_i_b; edge_i_tmp != edge_i_e; edge_i_tmp++) {
		string sourceStr = ver_nam_tmp_1[(source(*edge_i_tmp, newGraph))];
		string targetStr = ver_nam_tmp_1[target(*edge_i_tmp, newGraph)];
		string maxString, minString;

		if (sourceStr.compare(targetStr)>0) {
			maxString = sourceStr;
			minString = targetStr;
		}
		else {
			maxString = targetStr;
			minString = sourceStr;
		}

		std::tuple<string, string> keyStr = make_tuple(maxString, minString);
		if (TempEdgeCount.find(keyStr) == TempEdgeCount.end())
			TempEdgeCount[keyStr] = 1;
		else
			TempEdgeCount[keyStr] = TempEdgeCount[keyStr] + 1;
	}
	map<std::tuple<string, string>, int>::iterator map_i_t_tup;
	for (map_i_t_tup = TempEdgeCount.begin(); map_i_t_tup != TempEdgeCount.end(); map_i_t_tup++) {
		std::tuple<string, string> map_str_11_tup = map_i_t_tup->first;
		int value_tmp = map_i_t_tup->second;
		if (e_inner_count.find(map_str_11_tup) == e_inner_count.end())
			e_inner_count[map_str_11_tup] = 1;
		else
			e_inner_count[map_str_11_tup] = e_inner_count[map_str_11_tup] + 1;
		if (e_total_count.find(map_str_11_tup) == e_total_count.end())
			e_total_count[map_str_11_tup] = 1;
		else
			e_total_count[map_str_11_tup] = e_total_count[map_str_11_tup] + value_tmp;


	}
	map<string, long int>::iterator map_i_t_111;
	for (map_i_t_111 = v_inner_count.begin(); map_i_t_111 != v_inner_count.end(); map_i_t_111++) {
		string map_str_11 = map_i_t_111->first;
		int value_tmp = map_i_t_111->second;
		v_average_count[map_str_11] = (float)(v_total_count[map_str_11]) / (float)value_tmp;

	}


	/* map< tuple<string, string>, unsigned long int> e_inner_count; */
	/* map< tuple<string, string>, unsigned  long int> e_total_count */
	map<std::tuple<string, string>, long int>::iterator map_i_t_tup111;
	for (map_i_t_tup111 = e_total_count.begin(); map_i_t_tup111 != e_total_count.end(); map_i_t_tup111++) {
		std::tuple<string, string> map_str_11_tup = map_i_t_tup111->first;
		int value_tmp = map_i_t_tup111->second;
		e_average_count[map_str_11_tup] = (float)(value_tmp) / (float)(e_inner_count[map_str_11_tup]);

	}



}
#endif

#pragma once
#ifndef ENUMERATEISE_H
#define ENUMERATEISE_H

#include "SmallGraphClustering.h"
#include <vector>
#include <fstream>
#include <iostream>
#include "boost/tuple/tuple.hpp"
#include <string>
#include <boost/graph/iteration_macros.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/property_iter_range.hpp>
#include <boost/config.hpp>
#include <boost/graph/graphviz.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/graph/properties.hpp>
#include <boost/property_map/shared_array_property_map.hpp>
#include "boost/tuple/tuple.hpp"
#include <numeric>
#include <iterator>
#include <omp.h>
#include <iterator>
#include <algorithm>
using namespace std;
namespace EnumerateISE {
	

	struct ISEnode {
		int ind_in_graph;
		vertex_t ver_des;
		int order;
		vector<int> children;
	};
	struct ISEtree {
		vector<ISEnode>   tree;
	};
	void getZakiString(vector<int>& ZakiString,int index);
	vector<ISEtree> ISE2File(graphClosure g);
	void REC2File(ISEtree S, vector<ISEnode> BS, graphClosure g, vector<ISEnode> GraphNodeSet, int&count, vector<ISEtree>&returnIreeVec);
	void ISEtree2File_fct(int id, ISEtree tree, ofstream&output, graphClosure g);
	void ISEtree2File(int id, ISEtree tree, ofstream& output, graphClosure g);
	vector<ISEtree> ISE(graphClosure g);
	void REC(ISEtree S, vector<ISEnode> BS, graphClosure g, vector<ISEnode> GraphNodeSet, int&count, vector<ISEtree>&returnIreeVec);
	vector <short> ISEtree2CanonStr(int tid, ISEtree tree, graphClosure g);

	vector<ISEnode> calcBorder(ISEtree S, graphClosure g, vector<ISEnode> GraphNodeSet);
}
#endif

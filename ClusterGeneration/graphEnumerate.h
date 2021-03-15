#pragma once
#ifndef GRAPHENUMERATE_H
#define GRAPHENUMERATE_H
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
#include "SmallGraphClustering.h"
using namespace std;
namespace GraphEnumerate {
	struct defedge {
		int s;
		int t;
		ed_it it;
	};
	struct freetree {
		set<int> verset;
		vector<defedge>   edgeset;
	};

	vector<freetree> enumerateMaxFreeTrees(graphClosure g);
	void extendTree(freetree& result, ed_it itera_edg1, vector<defedge>&blockededges, graphClosure g);
}
#endif

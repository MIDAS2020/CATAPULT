#pragma once
//This file read graphs from file

#ifndef GRAPHUNITY_H
#define GRAPHUNITY_H
#include <vector>
#include <fstream>
#include <iostream>
#include <string>
#include <boost/graph/iteration_macros.hpp>
#include <boost/graph/vf2_sub_graph_iso.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/property_iter_range.hpp>
#include <boost/config.hpp>
#include <boost/graph/graphviz.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/graph/properties.hpp>
#include <boost/property_map/shared_array_property_map.hpp>
#include <numeric>
#include <iterator>
#include   "Graph.h"

using namespace std;
using namespace boost;
//template <typename Graph>
class GraphUnity {
public:
	GraphUnity() {

	}
	void writegraphs(vector<GraphName::graph> graphset, int startIndex, int endIndex);
	char readcommand(FILE *file);
	int readInt(FILE *input);
	GraphName::graph  readFromFile(FILE*  input);
	GraphUnity(string newfilename) {
		this->filename = newfilename;
	}
	void readGraphsFromFile(FILE* input, int size);

	int getSize() {
		return this->graphs.size();
	}
	vector<GraphName::graph> getGraphs() {
		return graphs;
	}
	void writeGraphsToOri(vector<GraphName::graph> graphset, string filename);

	bool testIso(const  GraphName::graph& g1, const GraphName::graph& g2);

	

private:
	string filename;
	vector<GraphName::graph>  graphs;
};
#endif


#pragma once
//copyright version 1.0 readGraph.h,
//This file read graphs from file

#ifndef GRAPH_H
#define GRAPH_H
#include <vector>
#include <fstream>
#include <iostream>
#include <string>
#include <boost/graph/iteration_macros.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/property_iter_range.hpp>
#include <boost/config.hpp>
#include <boost/graph/graphviz.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/graph/properties.hpp>
#include <boost/property_map/shared_array_property_map.hpp>
#include <numeric>
#include <iterator>
using namespace std;
using namespace boost;
namespace GraphName {

	typedef  adjacency_list<vecS, vecS, undirectedS, property<vertex_name_t, std::string >
		, no_property> graph;
	struct  patternInfo
	{
		vector<int> containIndex;
		vector<int> containList;
		int coverageNum;
		int totalNum;
	};

	struct  queryInfo
	{
		vector<int> containPattern;
	};
	typedef graph_traits<graph>::vertices_size_type vertices_number;
	typedef graph_traits<graph>::vertex_descriptor vertex_t;
	typedef graph_traits<graph>::edge_descriptor edge_t;
	typedef property_map<graph, vertex_name_t>::type VertexNameMap;
	static    string vertexLable1[] = {
		"C", "O", "Cu", "N", "S", "P", "Cl", "Zn", "B", "Br", "Co", "Mn", "As", "Al", "Ni", "Se",
		"Si", "V", "Sn", "I", "F", "Li", "Sb", "Fe", "Pd", "Hg", "Bi", "Na", "Ca", "Ti", "Ho", "Ge",
		"Pt", "Ru", "Rh", "Cr", "Ga", "K", "Ag", "Au", "Tb", "Ir", "Te", "Mg", "Pb", "W", "Cs", "Mo",
		"Re", "Cd", "Os", "Pr", "Nd", "Sm", "Gd", "Yb", "Er", "U", "Tl", "Ac"
	};
	static vector<string> vertexLable(vertexLable1, vertexLable1 + 60);
	class Graphs {
		//test
	public:
		Graphs() {

		}
		void writegraphs(vector<graph> graphset, int startIndex, int endIndex);
		char readcommand(FILE *file);
		int readInt(FILE *input);
		graph  readFromFile(FILE*  input);
		Graphs(string newfilename) {
			this->filename = newfilename;
		}
		void readGraphsFromFile(FILE* input, int size);

		int getSize() {
			return this->graphs.size();
		}
		vector<graph> getGraphs() {
			return graphs;
		}
	private:
		string filename;
		vector<graph>  graphs;
	};
}
#endif


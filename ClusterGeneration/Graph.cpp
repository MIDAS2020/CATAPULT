#include "Graph.h"
#include <fstream>
#include <iostream>
#include <string>
#include <boost/lexical_cast.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/filtered_graph.hpp>
#include <boost/graph/graph_utility.hpp>
#include <boost/graph/iteration_macros.hpp>
#include <boost/graph/mcgregor_common_subgraphs.hpp>
#include <boost/property_map/shared_array_property_map.hpp>
using namespace std;
using namespace GraphName;
char  Graphs::readcommand(FILE* input) {
	char car = fgetc(input);
	while (car < 'a' || car > 'z') {
		if (feof(input))
			return -1;
		car = fgetc(input);
	}
	return car;
}

int Graphs::readInt(FILE* input) {
	char car = fgetc(input);
	while (car < '0' || car > '9') {
		if (feof(input))
			return -1;
		car = fgetc(input);
	}
	int n = car - '0';
	car = fgetc(input);
	while (car >= '0' && car <= '9') {
		n = n * 10 + car - '0';
		car = fgetc(input);
	}
	return n;
}
void Graphs::writegraphs(vector<GraphName::graph> graphset, int startIndex = 0, int endIndex = 1) {
	for (int i = startIndex; i< endIndex + 1; i++) {
		string firstName = "output/graphs/";
		string newfilename = firstName + lexical_cast<string>(i) + ".dot";
		const char* filename = newfilename.c_str();
		ofstream ofile(filename);
		dynamic_properties dp;
		GraphName::graph newgraph = graphset[i];
		dp.property("node_id", get(boost::vertex_index, newgraph));
		dp.property("label", get(boost::vertex_name, newgraph));
		write_graphviz_dp(ofile, newgraph, dp);
		cout << num_vertices(newgraph) << endl;
	}
}
void Graphs::readGraphsFromFile(FILE* input, int size) {
	for (int i = 0; i< size; i++) {
		graphs.push_back(readFromFile(input));
		cout << i << endl;
	}
}
adjacency_list<vecS, vecS, undirectedS,
	property<vertex_name_t, std::string>, no_property>  Graphs::readFromFile(FILE* input) {
	graph newgraph;
	vector<int> newcontaiList;
	vector<vertex_t> vertexes;
	int graphid = readInt(input);
	int graphvertexNum = readInt(input);
	char c = readcommand(input);
	while (c == 'v') {
		int vertexId = readInt(input);
		int vertexLabelId = readInt(input);
		string vertexName = vertexLable[vertexLabelId];
		vertexes.push_back(add_vertex(vertexName, newgraph));
		c = readcommand(input);

	}
	while (c == 'e') {
		int firstEdge = readInt(input);
		int secondEdge = readInt(input);
		add_edge(vertexes[firstEdge], vertexes[secondEdge], newgraph);
		c = readcommand(input);
	}
	return newgraph;
}


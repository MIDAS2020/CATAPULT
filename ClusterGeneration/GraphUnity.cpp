#include "GraphUnity.h"
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

char  GraphUnity::readcommand(FILE* input) {
	char car = fgetc(input);
	while (car < 'a' || car > 'z') {
		if (feof(input))
			return -1;
		car = fgetc(input);
	}
	return car;
}

int GraphUnity::readInt(FILE* input) {
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
void GraphUnity::writegraphs(vector<GraphName::graph> graphset, int startIndex = 0, int endIndex = 1) {
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
		ofile.close();
	}
}
void GraphUnity::readGraphsFromFile(FILE* input, int size) {
	for (int i = 0; i< size; i++) {
		graphs.push_back(readFromFile(input));
		cout << i << endl;
	}
}
//adjacency_list<vecS, vecS, undirectedS,
//property<vertex_name_t, std::string>, no_property>  GraphUnity<GraphName::graph>::readFromFile(FILE* input) {
GraphName::graph GraphUnity::readFromFile(FILE* input) {

	GraphUnity tempgraph;

	GraphName::graph newgraph;
	vector<int> newcontaiList;
	vector<vertex_t> vertexes;
	int graphid = tempgraph.readInt(input);
	int graphvertexNum = tempgraph.readInt(input);
	char c = tempgraph.readcommand(input);
	while (c == 'v') {
		int vertexId = tempgraph.readInt(input);
		int vertexLabelId = tempgraph.readInt(input);
		string vertexName = GraphName::vertexLable[vertexLabelId];
		vertexes.push_back(add_vertex(vertexName, newgraph));
		c = tempgraph.readcommand(input);
	}
	while (c == 'e') {
		int firstEdge = tempgraph.readInt(input);
		int secondEdge = tempgraph.readInt(input);
		add_edge(vertexes[firstEdge], vertexes[secondEdge], newgraph);
		c = tempgraph.readcommand(input);
	}
	return newgraph;
}



void GraphUnity::writeGraphsToOri(vector<GraphName::graph> graphSet, string filename) {
	cout << "test1" << endl;
	std::ofstream oStream(filename.c_str());
	cout << "test" << filename << endl;
	typedef boost::graph_traits<GraphName::graph>::vertex_iterator vertex_iter;
	typedef boost::graph_traits<GraphName::graph>::vertices_size_type vertex_size;
	typedef boost::graph_traits<GraphName::graph>::edge_iterator edge_iter;
	typedef boost::graph_traits<GraphName::graph>::edges_size_type edge_size;
	vertex_iter ite1, ite2;
	edge_iter ite3, ite4;
	if (oStream.is_open()) {
		for (int i = 0; i < graphSet.size(); i++) {
			GraphName::graph tempGraph = graphSet[i];
			tie(ite1, ite2) = vertices(tempGraph);
			vertex_size vertex_num = num_vertices(tempGraph);
			property_map <GraphName::graph, vertex_name_t>::type vertex_name_map =
				get(vertex_name, tempGraph);
			property_map<GraphName::graph, vertex_index_t>::type vertex_index_map =
				get(vertex_index, tempGraph);
			oStream << "t # " << i << " " << vertex_num << endl;
			for (; ite1 != ite2; ite1++) {
				oStream << "v " << get(vertex_index_map, *ite1) << " " <<
					find(GraphName::vertexLable.begin(), GraphName::vertexLable.end(), get(vertex_name_map, *ite1)) - GraphName::vertexLable.begin() << endl;
			}
			tie(ite3, ite4) = edges(tempGraph);
			for (; ite3 != ite4; ite3++) {
				oStream << "e " << get(vertex_index_map, source(*ite3, tempGraph))
					<< " " << get(vertex_index_map, target(*ite3, tempGraph)) << " 0 " << endl;
			}
		}
		oStream.close();
	}
	else {
		cerr << "file " << filename << "   can not open!!" << endl;
	}
}

std::vector<GraphName::vertex_t> vertex_order_by_mult(const GraphName::graph& newgraph) {

	std::vector<GraphName::vertex_t> vertex_order;
	std::copy(vertices(newgraph).first, vertices(newgraph).second, std::back_inserter(vertex_order));

	detail::sort_vertices(newgraph, get(vertex_index, newgraph), vertex_order);
	return vertex_order;
}


template <typename Graph>
class deletePattern_callback {

public:

	typedef typename graph_traits<Graph>::vertices_size_type VertexSizeFirst;

	deletePattern_callback(bool& newHasPattern) : testHasPattern(newHasPattern) { }
	template <typename CorrespondenceMapFirstToSecond,
		typename CorrespondenceMapSecondToFirst>
		bool operator()(CorrespondenceMapFirstToSecond correspondence_map_1_to_2,
			CorrespondenceMapSecondToFirst correspondence_map_2_to_1) {

		testHasPattern = true;

		return false;


	}
private:
	bool& testHasPattern;

};
bool GraphUnity::testIso(const  GraphName::graph& g1, const GraphName::graph& g2) {

	bool testFlag = false;
	deletePattern_callback<GraphName::graph> user_callback(testFlag);
	vf2_subgraph_iso(g1, g2, user_callback,
		get(vertex_index, g1), get(vertex_index, g2),
		vertex_order_by_mult(g1),
		always_equivalent(),

		make_property_map_equivalent(get(vertex_name, g1), get(vertex_name, g2)));
	bool testFlag1 = false;

	deletePattern_callback<GraphName::graph> user_callback1(testFlag1);
	vf2_subgraph_iso(g2, g1, user_callback1,
		get(vertex_index, g2), get(vertex_index, g1),
		vertex_order_by_mult(g2),
		always_equivalent(),

		make_property_map_equivalent(get(vertex_name, g2), get(vertex_name, g1)));
	return testFlag && testFlag1;


}

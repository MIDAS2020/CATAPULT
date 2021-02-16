#include "readGraphs.h"
using namespace std;
using namespace boost;
using namespace graphsName;

char  ReadGraphs::readcommand(FILE* input) {

	char car = fgetc(input);
	while (car < 'a' || car > 'z') {
		if (feof(input))
			return -1;
		car = fgetc(input);
	}
	return car;
}

int ReadGraphs::readInt(FILE* input) {

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
void ReadGraphs::writegraphs(char* filename, int indexn) {
	ofstream ofile(filename);
	dynamic_properties dp;
	graphsName::graph testgraph = this->getGraphs()[indexn - 1];
	dp.property("node_id", get(boost::vertex_index, testgraph));

	//          dp.property("label",get(graphsName::vertex_t,testgraph));
	write_graphviz_dp(ofile, testgraph, dp);

}
void ReadGraphs::readGraphsFromFile(FILE* input, int size) {
	for (int i = 0; i< size; i++) {

		graphs.push_back(readFromFile(input));

	}


}
adjacency_list<vecS, vecS, undirectedS,
	graphsName::Vertex_Info, no_property>  ReadGraphs::readFromFile(FILE* input) {
	graph newgraph;
	vector<int> newcontaiList;
	vector<vertex_t> vertexes;
	int graphid = readInt(input);
	newcontaiList.push_back(graphid);
	int graphvertexNum = readInt(input);
	char c = readcommand(input);
	graphsName::Vertex_Info newVertex;
	while (c == 'v') {
		int vertexId = readInt(input);
		int vertexLabelId = readInt(input);
		string vertexName = vertexLable[vertexLabelId];
		newVertex.name = vertexName;
		newVertex.containList = newcontaiList;
		vertexes.push_back(add_vertex(newVertex, newgraph));
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



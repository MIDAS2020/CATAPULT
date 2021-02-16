#pragma once
#ifndef T_COM_H_
#define T_COM_H_
#include "boost/graph/adjacency_list.hpp"
#include <iostream>
#include <iterator>
#include <algorithm>
#include <vector>
using namespace boost;
using namespace std;
template <typename Graph>
class T_com;

template <typename Graph>
ostream& operator<<(ostream& out, const  T_com<Graph>& newT);
template <typename Graph>
class T_com
{
public:
	typedef typename graph_traits<Graph>::vertex_descriptor ver_t;

	int previous;
	string label;
	ver_t ver;
	int degree;
	vector<int> edge;
	T_com() {

	}
	friend ostream& operator<< <Graph>(ostream& out, const  T_com<Graph> & newT);
	virtual ~T_com() {

	}
};
template <typename Graph>
ostream& operator<<(ostream& out, const  T_com<Graph>& newT) {
	out << "Tree component" << endl << "\t previous:" << newT.previous << endl
		<< "\t label" << newT.label << endl
		<< "\t degree" << newT.degree << endl
		<< "\t edge set size:" << newT.edge.size() << endl;
	ostream_iterator<int> testEdges(out, ",");
	copy(newT.edge.begin(), newT.edge.end(), testEdges);
	out << endl;
	return out;
}

#endif

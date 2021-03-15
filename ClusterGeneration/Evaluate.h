#pragma once
//copyright version 1.0 readGraph.h,
//This file read graphs from file

#ifndef EVALUATE_H
#define EVALUATE_H
#include "boost/lexical_cast.hpp"
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
#include <algorithm>
#include "boost/algorithm/minmax_element.hpp"
#include <numeric>
#include <iterator>
#include "GraphUnity.h"
using namespace std;
using namespace boost;
using namespace GraphName;
class Evaluate {
	//test	 
public:

	Evaluate(vector<GraphName::graph> newquerySet, vector<GraphName::graph> newcandiSet) :querySet(newquerySet), candiSet(newcandiSet) {

	}

	Evaluate() {

	}
	//delete the MCS from query
	void deleteCommon(const GraphName::graph& patternGraph, GraphName::graph& queryGraph);

	void getTopk(const vector<GraphName::graph> & patternSet, const vector<GraphName::graph> & querySet, int & threshold, int & topk);

	void getCoverage(const vector<GraphName::graph> & patternSet, const vector<GraphName::graph>& querySet, int& threshold);

	//get the coverage value of a pattern

	int getCoverageValue(const vector<GraphName::graph>&  patternSet, const GraphName::graph & query, const int& threshold);

	queryInfo evaluateGraph(const vector<GraphName::graph> & patternSet, GraphName::graph& query, int threshold);
	void EvaluateCoverage(int threshold);

	void evaluatePattern(int threshold);
	void displayInfo();

	vector<int> topPatternid;
	vector<int> patternCoverage;
	vector<GraphName::patternInfo> patternSta;
	vector<GraphName::graph> topPattern;
	vector<GraphName::queryInfo> querSta;
	vector<GraphName::graph> querySet;
	vector<GraphName::graph> candiSet;
	vector<int> delta;
};

#endif

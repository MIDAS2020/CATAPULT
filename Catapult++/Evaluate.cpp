#include <fstream>
#include <iostream>
#include <string>
#include <algorithm>
#include<iterator>
#include<vector>
#include "boost/algorithm/minmax.hpp"
#include "boost/algorithm/minmax_element.hpp"

#include <boost/lexical_cast.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/filtered_graph.hpp>
#include <boost/graph/graph_utility.hpp>
#include <boost/graph/iteration_macros.hpp>
#include <boost/graph/mcgregor_common_subgraphs.hpp>
#include <boost/property_map/shared_array_property_map.hpp>

#include "Evaluate.h"
#include "GraphUnity.h"
#include "boost/tuple/tuple.hpp"
bool mycompfunc(int i, int j) {
	return (i<j);
}
template <typename Graph>
struct getCoverage_callback {
	typedef typename graph_traits<Graph>::vertices_size_type VertexSizeFirst;
	getCoverage_callback(const Graph& query, const int & newpatternSize, bool& newhascommon, int& newsubgraphSize, const int& newthreshold) :
		m_graph1(query), first(true), patternSize(newpatternSize), hascommon(newhascommon), subgraphSize(newsubgraphSize), threshold(newthreshold) { }

	template <typename CorrespondenceMapFirstToSecond,
		typename CorrespondenceMapSecondToFirst>
		bool operator()(CorrespondenceMapFirstToSecond correspondence_map_1_to_2,
			CorrespondenceMapSecondToFirst correspondence_map_2_to_1,
			VertexSizeFirst subgraph_size) {
		if (first) {
			typedef typename property_map<Graph, vertex_index_t>::type VertexIndexMap;
			typedef shared_array_property_map<bool, VertexIndexMap> MembershipMap;
			typedef typename graph_traits<Graph>::edges_size_type edge_size;
			MembershipMap membership_map1(num_vertices(m_graph1),
				get(vertex_index, m_graph1));
			fill_membership_map<Graph>(m_graph1, correspondence_map_1_to_2, membership_map1);
			// Generate filtered graphs using membership map
			typedef typename membership_filtered_graph_traits<Graph, MembershipMap>::graph_type
				MembershipFilteredGraph;
			MembershipFilteredGraph subgraph1 =
				make_membership_filtered_graph(m_graph1, membership_map1);
			typedef typename graph_traits<MembershipFilteredGraph>::edge_iterator filter_edge_it;
			filter_edge_it it1, it2;
			tie(it1, it2) = edges(subgraph1);
			int i = 0;
			for (; it1 != it2; it1++) {
				i++;
			}
			first = false;
			if (i >= patternSize - threshold) {
				subgraphSize = i;
				hascommon = true;
			}
		}
		return (true);
	}
private:
	bool first;
	const Graph& m_graph1;
	bool& hascommon;
	int& subgraphSize;
	const int& threshold;
	const int & patternSize;

};

// this is for delete the maximum common structure from the graph
template <typename Graph>
struct example_callback {
	typedef typename graph_traits<Graph>::vertices_size_type VertexSizeFirst;
	example_callback(Graph& graph1) : m_graph1(graph1), first(true) { }
	template <typename CorrespondenceMapFirstToSecond, typename CorrespondenceMapSecondToFirst>
	bool operator()(CorrespondenceMapFirstToSecond correspondence_map_1_to_2, CorrespondenceMapSecondToFirst correspondence_map_2_to_1, VertexSizeFirst subgraph_size) {
		if (first) {
			first = false;
			typedef typename graph_traits<Graph>::vertex_iterator iterator;
			typedef typename boost::graph_traits<Graph>::edge_descriptor edge_t;
			edge_t found_edge;
			iterator it1, it2, next, it3, next1;
			tie(it1, it2) = vertices(m_graph1);
			for (next = it1; it1 != it2; it1 = next) {
				next++;
				for (next1 = it1, it3 = it1; it3 != it2; it3 = next1) {
					next1++;
					if (get(correspondence_map_2_to_1, *it1) != graph_traits<Graph>::null_vertex() && get(correspondence_map_2_to_1, *it3) != graph_traits<Graph>::
						null_vertex()) {
						std::pair<edge_t, bool> p = boost::edge(*it1, *it3, m_graph1);

						if (!p.second) {

						}
						else {
							remove_edge(*it1, *it3, m_graph1);
						}
					}
				}
			}
		}
		return (true);
	}
private:
	Graph& m_graph1;
	bool first;
};
//this is for get the size of the maximum common substructure
template <typename Graph>
struct example_callback1 {
	typedef typename graph_traits<Graph>::vertices_size_type VertexSizeFirst;
	example_callback1(int& newSize, int& newpatternSize) :tempflag(true), size(newSize), patternSize(newpatternSize) { }
	template <typename CorrespondenceMapFirstToSecond, typename CorrespondenceMapSecondToFirst>
	bool operator()(CorrespondenceMapFirstToSecond correspondence_map_1_to_2, CorrespondenceMapSecondToFirst correspondence_map_2_to_1, VertexSizeFirst subgraph_size) {
		if (subgraph_size >= patternSize  && tempflag)
			tempflag = false;
		size = subgraph_size;
		return (true);
	}
private:
	int& size;
	bool tempflag;
	int& patternSize;
};


// this is for testint whether the query has the common structure below the threshold

template <typename Graph>
struct example_callback2 {


	typedef typename graph_traits<Graph>::vertices_size_type VertexSizeFirst;

	example_callback2(int& newSize, int& newpatternSize, bool& newFlag) :
		tempflag(true), size(newSize), patternSize(newpatternSize), testFlag(newFlag) { }

	template <typename CorrespondenceMapFirstToSecond,
		typename CorrespondenceMapSecondToFirst>
		bool operator()(CorrespondenceMapFirstToSecond correspondence_map_1_to_2,
			CorrespondenceMapSecondToFirst correspondence_map_2_to_1,
			VertexSizeFirst subgraph_size) {
		if (subgraph_size >= patternSize && tempflag) {

			testFlag = true;

			tempflag = false;
		}
		return (true);
	}

private:
	int& size;
	int& patternSize;
	bool& testFlag;
	bool tempflag;

};

//delete the pattern from the query graph
void Evaluate::deleteCommon(const GraphName::graph& pattern, GraphName::graph& query) {
	example_callback<GraphName::graph> user_callback(query);
	mcgregor_common_subgraphs_maximum_unique(pattern, query, true, user_callback,
		vertices_equivalent(make_property_map_equivalent(get(vertex_name, pattern), get(vertex_name, query))));
}

void Evaluate::getCoverage(const vector<GraphName::graph> & patternSet, const vector<GraphName::graph>& querySet, int& threshold) {
	vector<GraphName::graph>::const_iterator it1;
	for (it1 = patternSet.begin(); it1 != patternSet.end(); it1++) {
		int tmpCoverage = getCoverageValue(querySet, *it1, threshold);
		//cout << "tmpCoverage: " << tmpCoverage << endl;
		this->patternCoverage.push_back(tmpCoverage);

	}
}
bool huangkaicompfunc(int i, int j) {
	return (i>j);
}
/*
void Evaluate::getTopk(const vector<GraphName::graph> & patternSet, const vector<GraphName::graph> & querySet, int & threshold, int & topk){
std::cout << "getTopk begining1..........." << endl;
getCoverage(patternSet, querySet, threshold);
std::cout << "getTopk begining2..........." << endl;
vector<int> tempCov = this->patternCoverage;
///////////////////////////////////////////////////////////////////////////////////
std::nth_element(tempCov.begin(), tempCov.begin() + topk, tempCov.end(), mycompfunc);
vector<int>::iterator it1;
for (it1 = this->patternCoverage.begin(); it1 != this->patternCoverage.end(); it1++){
if (*it1 >= *(tempCov.begin() + topk)){
this->topPattern.push_back(patternSet[(it1 - patternCoverage.begin())]);
this->topPatternid.push_back(it1 - patternCoverage.begin());
}
}
std::cout << "getTopk begining3..........." << endl;
}
*/
/*
* Function getTopk is updated by Kai Huang,4/30/2016
*/
void Evaluate::getTopk(const vector<GraphName::graph> & patternSet, const vector<GraphName::graph> & querySet, int & threshold, int & topk) {
	getCoverage(patternSet, querySet, threshold);
	vector<int> tempCov = this->patternCoverage;
	std::nth_element(tempCov.begin(), tempCov.begin() + topk - 1, tempCov.end(), huangkaicompfunc);
	vector<int>::iterator it1;
	for (it1 = this->patternCoverage.begin(); it1 != this->patternCoverage.end(); it1++) {
		if (*it1 >= *(tempCov.begin() + topk - 1)) {
			this->topPattern.push_back(patternSet[(it1 - patternCoverage.begin())]);
			this->topPatternid.push_back(it1 - patternCoverage.begin());
		}
	}
}
int Evaluate::getCoverageValue(const vector<GraphName::graph>& QuerySet, const GraphName::graph& pattern, const int& threshold) {
	vector<GraphName::graph>::const_iterator it1;
	int patternSize = num_edges(pattern);
	int pattern_size_int = lexical_cast<int>(patternSize);
	int totalCoverage = 0;
	for (it1 = QuerySet.begin(); it1 != QuerySet.end(); it1++) {
		GraphName::graph tempGraph = *it1;
		int edge_size_query = num_edges(tempGraph);
		while (edge_size_query > 2) {
			bool hascommon = false;
			int subgraphSize;
			getCoverage_callback<GraphName::graph> getCoverage(pattern, pattern_size_int, hascommon, subgraphSize, threshold);
			mcgregor_common_subgraphs_maximum_unique(pattern, tempGraph, true, getCoverage,
				vertices_equivalent(make_property_map_equivalent(get(vertex_name, pattern), get(vertex_name, tempGraph))));
			if (!hascommon) {
				break;
			}
			totalCoverage = totalCoverage + subgraphSize;
			deleteCommon(pattern, tempGraph);
			edge_size_query = num_edges(tempGraph);
		}
	}
	return totalCoverage;
}
//evaluate a set of patterns
void Evaluate::EvaluateCoverage(int threshold) {

	for (int i = 0; i< querySet.size(); i++) {
		queryInfo newInfo;
		newInfo = evaluateGraph(this->candiSet, this->querySet[i], threshold);
		this->querSta.push_back(newInfo);
		this->delta.push_back(num_edges(this->querySet[i]));
	}


}

//Evaluate a grpah
queryInfo  Evaluate::evaluateGraph(const vector<GraphName::graph> & patternSet, GraphName::graph & query, int threshold) {
	// typedef graph_traits<graph>::edge_siez_type edge_number;

	queryInfo newQueryInfo;
	while (num_edges(query) > 2) {
		vector<int> tempSize(patternSet.size(), 0);
		bool test = false;
		for (int i = 0; i< patternSet.size(); i++) {
			const GraphName::graph& pattern = patternSet[i];
			graph_traits<GraphName::graph>::edges_size_type num1 = num_edges(pattern);
			int patternSize1 = lexical_cast<int>(num1 - threshold);
			example_callback1<GraphName::graph> user_callback1(tempSize[i], patternSize1);

			mcgregor_common_subgraphs_maximum_unique(pattern, query, true, user_callback1,
				vertices_equivalent(make_property_map_equivalent(get(vertex_name, pattern), get(vertex_name, query))));


		}
		typedef vector<int>::const_iterator ite1;

		ite1 firstIte, seconIte;
		tie(firstIte, seconIte) = boost::minmax_element(tempSize.begin(),
			tempSize.end());

		if ((*seconIte) <= 2)
			break;
		int patternIndex = seconIte - tempSize.begin();



		newQueryInfo.containPattern.push_back(patternIndex);
		deleteCommon(patternSet[patternIndex], query);


	}
	return newQueryInfo;
}




void Evaluate::evaluatePattern(int threshold) {

	for (int i = 0; i < this->candiSet.size(); i++) {
		vector<GraphName::graph> newQuerySet(this->querySet);
		GraphName::patternInfo newPatternIn;
		for (int j = 0; j< this->querySet.size(); j++) {
			bool testFlag;
			do {
				testFlag = false;
				graph_traits<GraphName::graph>::edges_size_type num1 = num_edges(candiSet[i]);
				int patternSize1 = lexical_cast<int>(num1 - threshold);
				int subgraphSize;
				example_callback2<GraphName::graph> user_callback2(subgraphSize, patternSize1, testFlag);
				mcgregor_common_subgraphs_maximum_unique(this->candiSet[i], newQuerySet[j], true, user_callback2,
					vertices_equivalent(make_property_map_equivalent(get(vertex_name, this->candiSet[i]), get(vertex_name, newQuerySet[j]))));
				deleteCommon(candiSet[i], newQuerySet[j]);

				if (testFlag == true) {
					/*
					updated by KaiHuang
					vector<int> containIndex;
					vector<int> containList;  which one??????????????????
					the member variable coverageNum, totalNum have also been assigned
					*/
					newPatternIn.containList.push_back(j);
					newPatternIn.containIndex.push_back(j);
					newPatternIn.coverageNum++;
					newPatternIn.totalNum = newPatternIn.totalNum + subgraphSize;
				}
			} while (testFlag == true);
		}
		this->patternSta.push_back(newPatternIn);
	}
}

void Evaluate::displayInfo() {
	for (int i = 0; i < this->querSta.size(); i++) {
		cout << "quer graph n  " << i << " contains : " << endl;
		cout << "query graph size: " << num_edges(this->querySet[i]) << endl;
		vector<int> newcon = this->querSta[i].containPattern;
		for (int j = 0; j< newcon.size(); j++) {

			cout << "Pattern " << newcon[j] << endl;
		}
	}

	cout << "now evaluate patterns" << endl;

	for (int i = 0; i< this->candiSet.size(); i++) {

		cerr << "pattern Number: " << i << " contains in : " << endl;
		for (int j = 0; j < this->patternSta[i].containList.size(); j++) {

			cerr << "graph number" << this->patternSta[i].containList[j] << endl;
		}

	}
	cout << "top pattern ids" << endl;

	ostream_iterator<int> testStream(std::cout, " , ");
	copy(this->topPatternid.begin(), this->topPatternid.end(), testStream);
	cout << endl;

}

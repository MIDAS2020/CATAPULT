#pragma once
#ifndef QUICKSI_H_
#define QUICKSI_H_
#include "SmallGraphClustering.h"
#include "T_com.h"
#include <boost/graph/prim_minimum_spanning_tree.hpp>
#include <stdlib.h>
#include <algorithm>
using namespace std;
using namespace boost;
class QuickSI {
public:
	vector<T_com<graphClosure>> generateSEQ(graphClosure g1);
	bool quickSI(vector<T_com<graphClosure>> seq, graphClosure query, graphClosure& g, vector<vertex_t>& H, vector<int>& F, int d, bool& firstF, bool deleteSub);
	bool testSubIso(graphClosure query, graphClosure& g, bool delteSub);
};
#endif

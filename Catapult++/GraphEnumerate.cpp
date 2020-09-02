#include "graphEnumerate.h"
#include <string>
#include <boost/graph/vf2_sub_graph_iso.hpp>
#include <fstream>
#include<cstdlib>
#include <ctime>
#include<queue>
#include "boost/tuple/tuple.hpp"
#include <iterator>
using namespace std;
using namespace GraphEnumerate;
vector<freetree> GraphEnumerate::enumerateMaxFreeTrees(graphClosure g) {
	vector<freetree> returnvector;
	vector<defedge> blockededges;
	blockededges.clear();
	ed_it itera_edg1, itera_edg2;
	tie(itera_edg1, itera_edg2) = edges(g);

	vertex_ind g_index;
	g_index = get(vertex_index, g);

	for (; itera_edg1 != itera_edg2; itera_edg1++) {
		vertex_t  u = source(*itera_edg1, g);
		vertex_t  v = target(*itera_edg1, g);
		cout << "edge:" << g_index[u] << "->" << g_index[v] << endl;
		freetree resulttree;
		extendTree(resulttree, itera_edg1, blockededges, g);
		returnvector.push_back(resulttree);
	}
	return returnvector;

}

void GraphEnumerate::extendTree(freetree& result, ed_it itera_edg1, vector<defedge>&blockededges, graphClosure g) {

	vertex_t  uu = source(*itera_edg1, g);
	vertex_t  vv = target(*itera_edg1, g);
	vertex_ind g_index;
	g_index = get(vertex_index, g);
	defedge insertedge;
	insertedge.s = g_index[uu];
	insertedge.t = g_index[vv];
	insertedge.it = itera_edg1;
	// blockededges   B
	blockededges.push_back(insertedge);
	cout << "blockededges size:" << blockededges.size() << endl;
	//freetree        T
	result.verset.insert(g_index[uu]);
	result.verset.insert(g_index[vv]);
	result.edgeset.push_back(insertedge);

	cout << "******111111111******" << endl;
	cout << "vertex:" << result.verset.size() << endl;
	set<int>::iterator verit;
	for (verit = result.verset.begin(); verit != result.verset.end(); verit++) {
		cout << *verit << " ";
	}
	cout << endl;
	cout << "edges:" << result.edgeset.size() << endl;
	for (int k = 0; k < result.edgeset.size(); k++) {
		cout << result.edgeset[k].s << "->" << result.edgeset[k].t << endl;
	}
	cout << "******222222222******" << endl;

	if (result.verset.size() < num_edges(g)) {
		vector<defedge> F;
		vector<defedge>::iterator it;

		//calculting F
		for (it = blockededges.begin(); it != blockededges.end(); it++) {
			int ver_u = it->s;
			int ver_v = it->t;

			bool uflag = false;
			bool vflag = false;
			set<int>::iterator it1;
			for (it1 = result.verset.begin(); it1 != result.verset.end(); ++it1) {
				if (*it1 == ver_u) {
					uflag = true;
				}
				if (*it1 == ver_v) {
					vflag = true;
				}
				if (uflag == true && vflag == true) {
					break;
				}
			}
			if ((uflag == true && vflag == false)) {
				cout << "here!!!!!!!!!!" << endl;
				F.push_back(*it);
			}
		}

		//for e \in F, extendtree(T,e)
		for (vector<defedge>::iterator iter = F.begin(); iter != F.end(); iter++) {
			extendTree(result, iter->it, blockededges, g);
		}

		//B <-- B\F
		vector<defedge> returnblockedges;
		returnblockedges.clear();
		for (int i = 0; i < blockededges.size(); i++) {
			bool flag = false;
			for (int j = 0; j < F.size(); j++) {
				if (blockededges[i].s == F[j].s && blockededges[i].t == F[j].t) {
					flag = true;
					break;
				}
			}
			if (!flag) {
				returnblockedges.push_back(blockededges[i]);
			}
		}
		blockededges.clear();
		blockededges = returnblockedges;
	}

}
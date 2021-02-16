#include "quickSI.h"
using namespace  std;
using namespace boost;



class dji_new :public default_dijkstra_visitor {
	vector<vertex_t>& newVs;
	int i;
	vector<edge_t>& edgeSet_p;
	map<vertex_t, int>& newFindTime;
	int j;
public:
	vector<vertex_t> getVerSet() {
		return newVs;
	}
	dji_new(vector<vertex_t>& newVe, vector<edge_t>& newEdges, map<vertex_t, int>& newFind) :newVs(newVe), edgeSet_p(newEdges), newFindTime(newFind) {
		i = 0;

		j = 0;
	}
	void discover_vertex(vertex_t u, const graphClosure& g) {
		newVs.push_back(u);
		newFindTime[u] = ++i;
	}
	void edge_not_relaxed(edge_t e, const graphClosure& g) {
		edgeSet_p.push_back(e);
	}
};
vector<T_com<graphClosure>> QuickSI::generateSEQ(graphClosure g1) {
	edge_weight_new weightmap = get(edge_newweight, g1);
	std::vector < graph_traits < graphClosure >::vertex_descriptor >
		p(num_vertices(g1));

	ed_it it1_ed, it2_ed;
	ver_distance distance = get(vertex_distance, g1);
	vertex_ind indexmap = get(vertex_index, g1);
	vertex_nam  ve_name = get(vertex_name, g1);
	vector<vertex_t> testV;
	vector<edge_t> edge_set;
	map<vertex_t, int> find_time;
	dji_new new_dji(testV, edge_set, find_time);

	prim_minimum_spanning_tree
	(g1, *vertices(g1).first, &p[0], distance, weightmap, indexmap, new_dji);

	vector<T_com<graphClosure>> SEQ(num_vertices(g1));
	ve_it it1, it2;

	for (vector<vertex_t>::iterator ve_it1 = testV.begin(); ve_it1 != testV.end(); ve_it1++) {
		T_com<graphClosure> newCom;
		if (p[indexmap[*ve_it1]] != indexmap[*ve_it1]) {

			int previous_index = p[indexmap[*ve_it1]];
			ve_it it1_temp, it2_temp;

			newCom.previous = find_time[vertex(previous_index, g1)];
		}

		else
			newCom.previous = 0;
		newCom.label = ve_name[*ve_it1];
		degree_size new_size_d;
		new_size_d = out_degree(*ve_it1, g1);
		int size_d = lexical_cast<int>(new_size_d);
		newCom.degree = size_d;
		newCom.ver = *ve_it1;
		SEQ[ve_it1 - testV.begin()] = newCom;
	}

	for (vector<edge_t>::iterator e_it1 = edge_set.begin(); e_it1 != edge_set.end(); e_it1++) {
		vertex_t sour_ve = source(*e_it1, g1);
		vertex_t tar_ve = target(*e_it1, g1);
		int index1 = find_time[sour_ve];
		int index2 = find_time[tar_ve];
		int index_min = min(index1, index2);
		int index_max = max(index1, index2);

		SEQ[index_max - 1].edge.push_back(index_min - 1);

	}
	return SEQ;


}
bool QuickSI::quickSI(vector<T_com<graphClosure>> seq, graphClosure query, graphClosure& g, vector<vertex_t>& H, vector<int>& F, int d, bool& firstF, bool deleteSub) {
	int belta = seq.size();
	int alfa = num_vertices(g);
	vertex_nam nameMap_g = get(vertex_name, g);
	vertex_ind indMap_g = get(vertex_index, g);
	vertex_ind indMap_q = get(vertex_index, query);
	ed_it ed_it1, ed_it2;

	if (d>belta) {
		if (firstF && deleteSub) {
			firstF = false;

			for (tie(ed_it1, ed_it2) = edges(query); ed_it1 != ed_it2; ed_it1++) {
				vertex_t souV = source(*ed_it1, query);
				int index_s = indMap_q[souV];
				int index_s_s;
				vector<T_com<graphClosure>>::iterator t_it_1;
				for (t_it_1 = seq.begin(); t_it_1 != seq.end(); t_it_1++) {
					if (indMap_q((*t_it_1).ver) == index_s) {
						index_s_s = t_it_1 - seq.begin();
						break;
					}
				}
				vertex_t souV_g = H[index_s_s];
				vertex_t tarV = target(*ed_it1, query);
				int index_t = indMap_q[tarV];
				int index_t_t;
				for (t_it_1 = seq.begin(); t_it_1 != seq.end(); t_it_1++) {
					if (indMap_q((*t_it_1).ver) == index_t) {
						index_t_t = t_it_1 - seq.begin();
						break;
					}
				}

				vertex_t tarV_g = H[index_t_t];
				remove_edge(souV_g, tarV_g, g);

			}
			ve_it temp_it1, temp_it2;
			vector<vertex_t> temPves;
			for (tie(temp_it1, temp_it2) = vertices(g); temp_it1 != temp_it2; temp_it1++) {
				int size = lexical_cast<int>(out_degree(*temp_it1, g));
				if (size == 0) {
					temPves.push_back(*temp_it1);
				}

			}
			for (vector<vertex_t>::iterator it1 = temPves.begin(); it1 != temPves.end(); it1++) {
				remove_vertex(*it1, g);
			}
		}
		return true;
	}

	T_com<graphClosure> newT = seq[d - 1];
	vector<vertex_t> newV;
	if (d == 1) {
		ve_it ve_it1, ve_it2;
		for (tie(ve_it1, ve_it2) = vertices(g); ve_it1 != ve_it2; ve_it1++) {
			if ((nameMap_g[*ve_it1] == newT.label) && F[indMap_g[*ve_it1]] == 0) {
				newV.push_back(*ve_it1);

			}
		}
	}
	else {
		ve_it ve_it1, ve_it2;

		for (tie(ve_it1, ve_it2) = vertices(g); ve_it1 != ve_it2; ve_it1++) {

			bool testFlag = edge(*ve_it1, H[(newT.previous) - 1], g).second;

			if ((nameMap_g[*ve_it1] == newT.label) && (F[indMap_g[*ve_it1]] == 0) && (testFlag == true)) {
				newV.push_back(*ve_it1);

			}
		}
	}
	vector<vertex_t>::iterator it1 = newV.begin();
	for (; it1 != newV.end(); it1++) {
		vertex_t newVertex = *it1;
		degree_size new_size_d;
		new_size_d = out_degree(newVertex, g);
		int size_d = lexical_cast<int>(new_size_d);
		int size_T = newT.degree;
		bool testFlag = true;
		if (size_d < size_T) {
			testFlag = false;
		}
		if (testFlag == false)
			continue;
		vector<int> edges_set = newT.edge;
		for (vector<int>::iterator it11 = edges_set.begin(); it11 != edges_set.end(); it11++) {

			bool testFlagE = edge(newVertex, H[*it11], g).second;
			if (testFlagE == false) {
				testFlag = false;
				break;
			}
		}
		if (testFlag == false) {
			continue;
		}
		H[d - 1] = newVertex;
		F[indMap_g[newVertex]] = 1;
		if (quickSI(seq, query, g, H, F, d + 1, firstF, deleteSub) == true) {
			return true;
		}
		F[indMap_g[newVertex]] = 0;


	}
	return false;
}
bool QuickSI::testSubIso(graphClosure query, graphClosure& g, bool deleteSub) {
	vector<T_com<graphClosure>> SEQ = generateSEQ(query);
	vector<vertex_t> H;
	bool firstFlag = true;
	H.resize(num_vertices(query));
	vector<int> F(num_vertices(g), 0);
	bool testSUb = quickSI(SEQ, query, g, H, F, 1, firstFlag, deleteSub);
	return testSUb;

}
/*
int main(int argc, char *argv[])
{
graphClosure g1;
add_vertex(o)
cout << "test" << endl;
return 0;
}
*/


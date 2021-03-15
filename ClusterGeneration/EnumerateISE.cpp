#include "EnumerateISE.h"
#include "CanonicalTree.h"
#include <string>
#include <boost/graph/vf2_sub_graph_iso.hpp>
#include <fstream>
#include<cstdlib>
#include <ctime>
#include<queue>
#include "boost/tuple/tuple.hpp"
#include <iterator>
using namespace std;
using namespace EnumerateISE;
vector<int> index_in_originalgraph;

vector<int> id_of_tree;
vector<int> label_of_tree;
vector<vector<int>> neighobourlist;
vector<int> flag;

int number_closedtree = 0;

static bool ISEnode_compare(ISEtree a, ISEtree b)
{
	return (a.tree.size() < b.tree.size());
}
struct custom_dfs_visitor :public boost::default_dfs_visitor
{
	custom_dfs_visitor(vector<int>& vis, vector<ISEnode>& nset) : visit(vis), nodeset(nset) {}
	template < typename Vertex, typename Graph >
	void discover_vertex(Vertex u, const Graph & g) const
	{
		static int i = 0;
		//std::cout << u << std::endl;
		vertex_ind g_index;
		g_index = get(vertex_index, g);
		visit.push_back(g_index[u]);
		ISEnode node;
		node.ind_in_graph = g_index[u];
		node.ver_des = u;
		node.order = ++i;
		//cout << " node.order :" << node.order << " indexingraph" << node.ind_in_graph << endl;
		adj_it index1, index2;
		tie(index1, index2) = adjacent_vertices(u, g);
		for (index1; index1 != index2; index1++) {
			//cout << "g_index[*index1]:" << g_index[*index1]<< " ";
			node.children.push_back((int)g_index[*index1]);
		}
		//cout << endl;
		nodeset.push_back(node);
	}
private:
	vector<int>& visit;
	vector<ISEnode>& nodeset;

};
void EnumerateISE::getZakiString(vector<int>& ZakiString,int index)
{
	ZakiString.push_back(label_of_tree[index]);
	flag[index] = 1;

	vector<int> neighbours = neighobourlist[index];
	bool tempflag = false;
	for (auto x : neighbours) {
		if (flag[x] == 1) continue;
		getZakiString(ZakiString,x);
		tempflag = true;
	}
	//if (!tempflag) {
	//	ZakiString.push_back(-1);
	//}
	ZakiString.push_back(-1);
}
vector<ISEtree> EnumerateISE::ISE2File(graphClosure g) {
	vector<int> visit;
	vector<ISEnode> nodeset;
	//DFS
	custom_dfs_visitor vis(visit, nodeset);
	boost::depth_first_search(g, visitor(vis));
	ISEtree S;
	S.tree.clear();
	int count = 0;
	vector<ISEtree> returnTreeVec;
	REC2File(S, nodeset, g, nodeset, count, returnTreeVec);
	return returnTreeVec;
}
vector<ISEtree> EnumerateISE::ISE(graphClosure g) {
	vector<int> visit;
	vector<ISEnode> nodeset;
	//DFS
	custom_dfs_visitor vis(visit, nodeset);
	boost::depth_first_search(g, visitor(vis));
	ISEtree S;
	S.tree.clear();
	int count = 0;
	vector<ISEtree> returnTreeVec;
	REC(S, nodeset, g, nodeset, count, returnTreeVec);
	//cout << "ssss" << endl;
	return returnTreeVec;
}
void EnumerateISE::REC(ISEtree S, vector<ISEnode> BS, graphClosure g, vector<ISEnode> GraphNodeSet, int & count, vector<ISEtree>&returnTreeVec) {
	if (count >= 100) return;

	if (!S.tree.empty()) {
		count++;
		returnTreeVec.push_back(S);
		if (count >= 100) return;
	}
	vector<ISEnode>::iterator it;
	for (it = BS.begin(); it != BS.end(); it++) {
		ISEnode x = *it;
		//ch
		ISEtree T = S;
		T.tree.push_back(x);
		vector<ISEnode> BT = calcBorder(T, g, GraphNodeSet);
		REC(T, BT, g, GraphNodeSet, count, returnTreeVec);
		vector<ISEnode>().swap(BT);
	}

}

void EnumerateISE::ISEtree2File_fct(int id, ISEtree tree, ofstream& output, graphClosure g) {
	if (false) {
		ISEtree temp = tree;
		int treesize = temp.tree.size();
		if (treesize < 2) return;
		// index vector for mapping, construct it from head node
		output << id + 1 << endl;
		output << treesize << endl;
		vector<int> index;
		for (int ind = 0; ind < treesize; ind++) {
			index.push_back(temp.tree[ind].ind_in_graph);
			vertex_t vt = temp.tree[ind].ver_des;
			vertex_nam vnamemap = get(vertex_name, g);
			string name = vnamemap[vt];
			if (find(vertexLable.begin(), vertexLable.end(), name) == vertexLable.end()) continue;
			int nameindex = find(vertexLable.begin(), vertexLable.end(), name) - vertexLable.begin();
			output << nameindex + 1 << endl;
		}
		// map children to index vector for edge formulating
		for (int ind2 = 0; ind2 < treesize; ind2++) {

			vector<int> child = temp.tree[ind2].children;
			for (int k = 0; k < child.size(); k++) {
				if (find(index.begin(), index.end(), child[k]) == index.end()) continue;
				int childindex = find(index.begin(), index.end(), child[k]) - index.begin();
				if (ind2 < childindex)
					output << 1 << " " << ind2 + 1 << " " << childindex + 1 << endl;
			}
		}
	}
	
	index_in_originalgraph.clear();
	id_of_tree.clear();
	label_of_tree.clear();
	neighobourlist.clear();
	flag.clear();


	ISEtree temp = tree;
	int treesize = temp.tree.size();
	if (treesize < 2) return;
	for (int ind = 0; ind < treesize; ind++) {
		index_in_originalgraph.push_back(temp.tree[ind].ind_in_graph);
		id_of_tree.push_back(ind);
		vertex_t vt = temp.tree[ind].ver_des;
		vertex_nam vnamemap = get(vertex_name, g);
		string name = vnamemap[vt];
		if (find(vertexLable.begin(), vertexLable.end(), name) == vertexLable.end()) continue;
		int nameindex = find(vertexLable.begin(), vertexLable.end(), name) - vertexLable.begin();
		label_of_tree.push_back(nameindex);
		flag.push_back(0);
	}
	// map children to index vector for edge formulating
	for (int ind2 = 0; ind2 < treesize; ind2++) {
		vector<int> child = temp.tree[ind2].children;
		vector<int> child2;
		vector<int>::iterator it;
		for (int k = 0; k < child.size(); k++) {
			it = find(index_in_originalgraph.begin(), index_in_originalgraph.end(), child[k]);
			if (it == index_in_originalgraph.end()) {
				continue;
			}
			int childindex = it - index_in_originalgraph.begin();
			//cout << childindex << endl;
			child2.push_back(id_of_tree[childindex]);
		}
		neighobourlist.push_back(child2);
	}
	vector<int> ZakiString;
	getZakiString(ZakiString,0);
	//特别注意：最后一位都会多加一个-1，因此需要少输出一位-1
	output << number_closedtree << "\t" << number_closedtree << "\t" << ZakiString.size() - 1 << "\t";
	for (int i = 0; i < ZakiString.size()-1; i++) {
		output << ZakiString[i]<< "\t";
	}
	output << endl;
	number_closedtree++;
}
void EnumerateISE::ISEtree2File(int id, ISEtree tree, ofstream& output, graphClosure g)
{
	ISEtree temp = tree;
	int treesize = temp.tree.size();
	if (treesize < 2) return;
	// index vector for mapping, construct it from head node
	output << id + 1 << endl;
	output << treesize << endl;
	vector<int> index;
	for (int ind = 0; ind < treesize; ind++) {
		index.push_back(temp.tree[ind].ind_in_graph);
		vertex_t vt = temp.tree[ind].ver_des;
		vertex_nam vnamemap = get(vertex_name, g);
		string name = vnamemap[vt];
		if (find(vertexLable.begin(), vertexLable.end(), name) == vertexLable.end()) continue;
		int nameindex = find(vertexLable.begin(), vertexLable.end(), name) - vertexLable.begin();
		output << nameindex + 1 << endl;
	}
	// map children to index vector for edge formulating
	for (int ind2 = 0; ind2 < treesize; ind2++) {

		vector<int> child = temp.tree[ind2].children;
		for (int k = 0; k < child.size(); k++) {
			if (find(index.begin(), index.end(), child[k]) == index.end()) continue;
			int childindex = find(index.begin(), index.end(), child[k]) - index.begin();
			if (ind2 < childindex)
				output << 1 << " " << ind2 + 1 << " " << childindex + 1 << endl;
		}
	}
}
/*
void EnumerateISE::ISEtree2File(int id, ISEtree tree, ofstream&output, graphClosure g) {

	ISEtree temp = tree;
	int treesize = temp.tree.size();
	if (treesize < 2) return;
	// index vector for mapping, construct it from head node
	output << id + 1 << endl;
	output << treesize << endl;
	vector<int> index;
	for (int ind = 0; ind < treesize; ind++) {
		index.push_back(temp.tree[ind].ind_in_graph);
		vertex_t vt = temp.tree[ind].ver_des;
		vertex_nam vnamemap = get(vertex_name, g);
		string name = vnamemap[vt];
		if (find(vertexLable.begin(), vertexLable.end(), name) == vertexLable.end()) continue;
		int nameindex = find(vertexLable.begin(), vertexLable.end(), name) - vertexLable.begin();
		output << nameindex + 1 << endl;
	}
	// map children to index vector for edge formulating
	for (int ind2 = 0; ind2 < treesize; ind2++) {

		vector<int> child = temp.tree[ind2].children;
		for (int k = 0; k < child.size(); k++) {
			if (find(index.begin(), index.end(), child[k]) == index.end()) continue;
			int childindex = find(index.begin(), index.end(), child[k]) - index.begin();
			if (ind2<childindex)
				output << 1 << " " << ind2 + 1 << " " << childindex + 1 << endl;
		}
	}
}
*/

vector <short> EnumerateISE::ISEtree2CanonStr(int tid, ISEtree tree, graphClosure g) {
	long t = (long)tid;	//tid
	short v = tree.tree.size();	//number of vertices
	short p1, p2;
	short vLabel, eLabel;
	//FreeTree pft;
	CanonicalTree* pct;
	pct = new CanonicalTree(v, t);

	pct->adj.resize(v + 1);
	pct->vertexLabel.resize(v + 1);
	pct->degree.resize(v + 1);

	for (short i = 0; i <= v; i++)
	{
		pct->adj[i] = 0;
		pct->degree[i] = 0;
	}
	pct->tid = t;
	pct->vCount = v;
	pct->vertexLabel[0] = 0;	//null


	vector<short> canonicalstr;
	ISEtree temp = tree;
	int treesize = temp.tree.size();
	if (treesize < 2)
	{
		//cout << "treesize is less than 2!" << endl;
		return canonicalstr;
	}
	// index vector for mapping, construct it from head node
	vector<int> index;
	for (int ind = 0; ind < treesize; ind++) {
		index.push_back(temp.tree[ind].ind_in_graph);
		vertex_t vt = temp.tree[ind].ver_des;
		vertex_nam vnamemap = get(vertex_name, g);
		string name = vnamemap[vt];
		if (find(vertexLable.begin(), vertexLable.end(), name) == vertexLable.end()) continue;
		int nameindex = find(vertexLable.begin(), vertexLable.end(), name) - vertexLable.begin();
		//output << nameindex + 1 << endl;
		pct->vertexLabel[ind + 1] = nameindex + 1;
	}
	// map children to index vector for edge formulating
	for (int ind2 = 0; ind2 < treesize; ind2++) {
		vector<int> child = temp.tree[ind2].children;
		for (int k = 0; k < child.size(); k++) {
			if (find(index.begin(), index.end(), child[k]) == index.end()) continue;
			int childindex = find(index.begin(), index.end(), child[k]) - index.begin();
			if (ind2<childindex)
				//output << 1 << " " << ind2 + 1 << " " << childindex + 1 << endl;
				pct->insertEdge(Edge(ind2 + 1, childindex + 1, 1));
		}
	}
	pct->normalize();
	canonicalstr = pct->canonicalString;
	delete pct;
	return canonicalstr;
}



void EnumerateISE::REC2File(ISEtree S, vector<ISEnode> BS, graphClosure g, vector<ISEnode> GraphNodeSet, int & count, vector<ISEtree>& returnIreeVec) {

	if (count >= 50)
	{
		vector<ISEtree> returntree;
		ISEtree maxtree = *max_element(returnIreeVec.begin(), returnIreeVec.end(), ISEnode_compare);
		for (int i = 0; i < returnIreeVec.size(); i++) {
			if (returnIreeVec[i].tree.size() == maxtree.tree.size()) {
				returntree.push_back(returnIreeVec[i]);
				if (returntree.size() >= 5)
					break;
			}
		}
		vector<ISEtree>().swap(returnIreeVec);
		returnIreeVec = returntree;
		vector<ISEtree>().swap(returntree);
		return;
	}

	if (!S.tree.empty()) {
		count++;
		returnIreeVec.push_back(S);
	}
	vector<ISEnode>::iterator it;
	for (it = BS.begin(); it != BS.end(); it++) {
		ISEnode x = *it;
		//ch
		ISEtree T = S;
		T.tree.push_back(x);
		vector<ISEnode> BT = calcBorder(T, g, GraphNodeSet);
		REC2File(T, BT, g, GraphNodeSet, count, returnIreeVec);
		vector<ISEnode>().swap(BT);
	}
}
vector<ISEnode>  EnumerateISE::calcBorder(ISEtree S, graphClosure g, vector<ISEnode> GraphNodeSet) {
	vector<ISEnode> BS;
	// x \in V\S
	vector<ISEnode> retain;
	vector<ISEnode>::iterator it;
	for (it = GraphNodeSet.begin(); it != GraphNodeSet.end(); it++) {
		bool flag = false;
		ISEnode temp = *it;
		for (int i = 0; i < S.tree.size(); i++) {
			if (temp.order == S.tree[i].order) {
				flag = true;
				break;
			}
		}
		if (!flag) {
			retain.push_back(temp);
		}
	}
	//cout << retain.size() << endl;
	//root(S) <= x
	ISEnode root;
	int minorder = 1000000;
	for (int i = 0; i < S.tree.size(); i++) {
		if (S.tree[i].order < minorder) {
			minorder = S.tree[i].order;
			root = S.tree[i];
		}
	}
	//cout << "minorder: " << minorder << endl;
	//if root(S) <= x then other two conditions
	vector<ISEnode>::iterator it2;
	for (it2 = retain.begin(); it2 != retain.end(); it2++) {
		ISEnode x = *it2;
		bool flag1, flag2, flag3;
		flag1 = flag2 = flag3 = false;
		if (root.order <= x.order) {
			flag1 = true;
			// wmax
			int wmax = -1;
			ISEtree Stemp = S;
			Stemp.tree.push_back(x);
			vector<int>  existNeib;
			ISEnode newxInS;
			for (int k1 = 0; k1 < Stemp.tree.size(); k1++) {
				existNeib.push_back(Stemp.tree[k1].ind_in_graph);
			}
			for (int k2 = 0; k2 < Stemp.tree.size(); k2++) {
				for (vector<int>::iterator iter = Stemp.tree[k2].children.begin(); iter != Stemp.tree[k2].children.end();)
				{
					vector<int>::iterator it = std::find(existNeib.begin(), existNeib.end(), *iter);
					if (it == existNeib.end())
						iter = Stemp.tree[k2].children.erase(iter);
					else
						iter++;
				}
				if (Stemp.tree[k2].children.size() == 1 && Stemp.tree[k2].order > wmax) {
					wmax = Stemp.tree[k2].order;
					newxInS = Stemp.tree[k2];
				}
			}
			if (wmax == x.order) {
				flag2 = true;
				//cnt
				int  cnt = 0;
				if (newxInS.children.size() == 1) {
					cnt = 1;
					flag3 = true;
				}
			}
		}
		if (flag1 && flag2 && flag3) {
			BS.push_back(*it2);
		}
	}
	return BS;
}


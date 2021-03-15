#pragma once
#include "Tree.h"
typedef multimap <int, int> Graph;
typedef map <int, int> BipartiteGraph;


class UnorderedTree : public Tree {
protected:
	struct SupportId {
		int Id;
		::Integer iNode; //Node occurrence
	};
	typedef list<SupportId> ListSupport;
public:
	UnorderedTree();
	UnorderedTree(UnorderedTree* t);
	UnorderedTree(TreeDataSet* D);
	UnorderedTree(TreeDataSet* D, int label);
	inline void compress() { IdTrans.clear(); }
	inline int Id_Front() { return IdTrans.front().Id; };

	bool isCanonicalRepresentative(Node ext);
	bool isSubtreeOf(TreeNodes* t0);
	int supportInc(TreeDataSet* D, int min_sup);

	inline ListSupport* get_ListSupport() { return &IdTrans; }
	static TreeNodes CanonicalForm(TreeNodes t);

protected:
	virtual bool isSubtreeUnOrdInc(TreeNodes* supertree, ::Integer* iNode);
	static bool isSubtree(TreeNodes* a, TreeNodes* b);
	static bool recurse(int v, map <int, list<int> >* preds, map<int, int>* pred, list<int>* unmatched, BipartiteGraph* matching);
	static void bipartiteMatch(Graph* graph, BipartiteGraph* matching);
	static inline setTreeNodes TreeComponents(TreeNodes* t) { return TreeComponents(t, false); };
	static setTreeNodes TreeComponents(TreeNodes* t, bool bCanonical);
	static TreeNodes SubtreeAtNode(TreeNodes* supertree, ::Integer* iNode);

	ListSupport IdTrans;
	vectorTreeNodes CanonicalNew;
	vectorTreeNodes CanonicalOld;

};

class UnorderedTree_TopDown : public UnorderedTree {
public:
	UnorderedTree_TopDown() :UnorderedTree() {};
	UnorderedTree_TopDown(UnorderedTree_TopDown* t) :UnorderedTree((UnorderedTree*)t) {};
	UnorderedTree_TopDown(TreeDataSet* D) :UnorderedTree(D) {};
	UnorderedTree_TopDown(TreeDataSet* D, int label);
private:
	bool isSubtreeUnOrdInc(TreeNodes* supertree, ::Integer* iNode) { return isSubtree(&tree, supertree); };
};


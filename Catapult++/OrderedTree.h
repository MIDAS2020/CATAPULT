#pragma once
#include "Tree.h"

typedef int Integer;

class OrderedTree : public Tree {
protected:
	struct SupportId {
		int Id;
		::Integer iNode; //Node occurrence
		::Integer sup; //Position of subtree
		::Integer pos; //Position of tree
	};
	typedef list<SupportId> ListSupport;
public:
	OrderedTree();
	OrderedTree(OrderedTree* t);
	OrderedTree(TreeDataSet* D);
	OrderedTree(TreeDataSet* D, int label);
	inline void compress() { IdTrans.clear(); }
	inline int Id_Front() { return IdTrans.front().Id; };

	inline bool isCanonicalRepresentative(Node ext) { return true; };
	inline bool isSubtreeOf(TreeNodes* t0);
	int supportInc(TreeDataSet* D, int min_sup);

	inline ListSupport* get_ListSupport() { return &IdTrans; }

protected:
	virtual bool isSubtreeInc(TreeNodes* supertree, ::Integer* post0, ::Integer* post1, ::Integer* iNode);

	ListSupport IdTrans;

};

class OrderedTree_TopDown : public OrderedTree {
public:
	OrderedTree_TopDown() :OrderedTree() {};
	OrderedTree_TopDown(OrderedTree_TopDown* t) :OrderedTree((OrderedTree*)t) {};
	OrderedTree_TopDown(TreeDataSet* D) :OrderedTree(D) {};
	OrderedTree_TopDown(TreeDataSet* D, int label);
private:
	bool isSubtreeInc(TreeNodes* supertree, ::Integer* post0, ::Integer* post1, ::Integer* iNode);
};


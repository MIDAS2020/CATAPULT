#pragma once
#include "OrderedTree.h"
#include "UnorderedTree.h"
class TreeFactory {

public:

	virtual Tree* create_tree() = 0;
	virtual Tree* create_tree(TreeDataSet* D) = 0;
	virtual Tree* create_tree(TreeDataSet* D, int label) = 0;
	virtual Tree* create_tree(Tree* t) = 0;

};



class OrderedTreeFactory : public TreeFactory {

public:

	Tree* create_tree() { return new OrderedTree(); }
	Tree* create_tree(TreeDataSet* D) { return new OrderedTree(D); };
	Tree* create_tree(TreeDataSet* D, int label) { return new OrderedTree(D, label); };
	Tree* create_tree(Tree* t) { return new OrderedTree((OrderedTree*)t); };

};

class UnorderedTreeFactory : public TreeFactory {

public:

	Tree* create_tree() { return new UnorderedTree(); }
	Tree* create_tree(TreeDataSet* D) { return new UnorderedTree(D); };
	Tree* create_tree(TreeDataSet* D, int label) { return new UnorderedTree(D, label); };
	Tree* create_tree(Tree* t) { return new UnorderedTree((UnorderedTree*)t); };

};

class OrderedTree_TopDown_Factory : public TreeFactory {

public:

	Tree* create_tree() { return new OrderedTree_TopDown(); }
	Tree* create_tree(TreeDataSet* D) { return new OrderedTree_TopDown(D); };
	Tree* create_tree(TreeDataSet* D, int label) { return new OrderedTree_TopDown(D, label); };
	Tree* create_tree(Tree* t) { return new OrderedTree_TopDown((OrderedTree_TopDown*)t); };

};

class UnorderedTree__TopDown_Factory : public TreeFactory {

public:

	Tree* create_tree() { return new UnorderedTree_TopDown(); }
	Tree* create_tree(TreeDataSet* D) { return new UnorderedTree_TopDown(D); };
	Tree* create_tree(TreeDataSet* D, int label) { return new UnorderedTree_TopDown(D, label); };
	Tree* create_tree(Tree* t) { return new UnorderedTree_TopDown((UnorderedTree_TopDown*)t); };

};
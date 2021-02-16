#pragma once
#include "Adwin.h"
#include <vector>
#include<list>
#include<vector>
#include<set>
#include<map>
#include <iostream>
#include <iterator>
#include <fstream>
#include "DatasetReader.h"
using namespace std;
//added by kai 
//typedef int Integer;
//typedef Integer Node;
//typedef vector<Integer> TreeNodes;


typedef vector<TreeNodes> vectorTreeNodes; //Canonical
typedef vector<TreeNodes> TreeDataSet;
typedef multiset<TreeNodes> setTreeNodes; //TreeComponents

class Tree {
public:
	Tree();
	Tree(Tree* t);
	virtual ~Tree();

#if defined(LABELLED)
	inline void extend(Node node) {
		//Extends a tree one step : adds node at depth ext   
		tree.push_back(node);
	};
	int inline currentLabel() {
		return tree.back().label;
	};
	vector<int> parentsLabel;
	int inline currentAncestorLabel(int depth) { return parentsLabel.at(lenTail - depth); };
	int inline currentFatherLabel() { return parentsLabel.at(lenTail - 1); };
	int inline currentGrandFatherLabel() { return parentsLabel.at(lenTail - 2); };
#else
	inline void extend(Integer node) {
		//Extends a tree one step : adds node at depth ext   
		tree.push_back(node);
	};
#endif

	void print(ofstream * ofile, DatasetReader * mdataset_reader);
	void print2(int treeid, ofstream* ofile, DatasetReader* mdataset_reader);
	void cout_print();
	void print_l(TreeNodes  tree); // Debug
	void printSupport(ofstream * ofile);
	virtual bool isCanonicalRepresentative(Node ext) = 0;
	virtual bool isSubtreeOf(TreeNodes * t0) = 0;
	virtual int supportInc(TreeDataSet * D, int min_sup) = 0;
	virtual void compress() = 0;
	virtual int Id_Front() = 0;

	inline int get_Size() { return tree.size(); }
	inline TreeNodes* get_TreeNodes() { return &tree; };
	inline TreeNodes get_TreeNodes_() { return tree; };
	inline void set_TreeNodes(TreeNodes tn) { tree = tn; };
	inline ::Integer get_Support() { return Support; }
	inline void set_Support(int sup) { Support = sup; }
	inline ::Integer get_lenTail() { return lenTail; }
	inline void set_lenTail(int len) { lenTail = len; }
	inline bool get_isNotMaximal() { return isNotMaximal; }
	inline void set_isNotMaximal(bool is) { isNotMaximal = is; }
	inline bool get_Update() { return Update; }
	inline void set_Update(bool is) { Update = is; }
	Adwin* AdSupport;

protected:
	TreeNodes  tree;
	::Integer Support;
	::Integer lenTail;
	bool isNotMaximal;
	bool Update;
private:
};


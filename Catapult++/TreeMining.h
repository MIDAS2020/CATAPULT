#pragma once
#include "TreeFactory.h"
#include "DatasetReader.h"
#include "IsLabeled.h"
#include <map>
typedef multimap<int, Tree*> mapTree;
typedef multimap<int, Tree*>::iterator mapTreeIterator;
typedef multimap<int, Tree*>::reverse_iterator mapTreeRevIterator;

class TreeMining {
public:
	TreeMining(TreeFactory* tree_factory, DatasetReader* dataset_reader, int min_sup);
	virtual ~TreeMining();
	void print_Closed_Trees(const char* filenameOrig);
	int  print_Closed_Trees2(const char* filenameOrig);
	inline mapTree* get_ClosedMapTree() { return mClosedMapTree; };
	void Compress_MapTree();
	char readcommand(FILE* file);
	int readInt(FILE* input);
private:
	void closed_subtree_mining(Tree* t);
	DatasetReader* mdataset_reader;
	TreeDataSet* D;
	int min_sup;
	mapTree* mClosedMapTree;
	TreeFactory* mtree_factory;
};

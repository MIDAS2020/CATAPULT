#pragma once
#include "ClosedTrees.h"
#include <map>
#define ADD_OPTION 1
#define DELETE_OPTION 2 

/// The code is from https://github.com/abifet/adatreenat

class AdaTreeNat_Mining {
public:
	AdaTreeNat_Mining();                       // Constructor
	AdaTreeNat_Mining(DatasetReader* dataset_reader, bool is_Relaxed_Support, bool is_Log_Relaxed_Support,
		int NumberRelaxedIntervals, double dblmin_sup, int BatchSize, TreeFactory* tree_factory,
		bool is_Adaptive1, bool is_Adaptive2, int SlidingWindowSize_orig, int min_sup_orig, bool is_SlidingWindow);
	virtual ~AdaTreeNat_Mining() { delete support_factory; delete mclosedTrees; };  // Destructor

	void Add(mapTree* ClosedMapTree);
#if defined(LABELLED)
	void Delete(TreeDataSet* D, TreeNodesAux* Daux, int min_sup);
#else
	void Delete(TreeDataSet* D, int min_sup);
#endif

protected:
private:
	ClosedTrees* mclosedTrees;

	double mdblmin_sup;
	int mBatchSize;
	int* mWindowSize;
	TreeFactory* mtree_factory;
	Support_Factory* support_factory;
	DatasetReader* mdataset_reader;

#if defined(LABELLED)
	TreeNodesAux* mDatasetAux;
#endif

	void recursive_Add(Tree* t, TreeDataSet* D, SupportContainer* SupportDataset, list<TreeNodes*>* list_Insert_TreeNodes, list<int>* list_Insert_Support);
	void recursive_Delete(Tree* t, TreeDataSet* D, int min_sup, SupportContainer* SupportDataset, listTree* TreesCheckClosureList);
	TreeDataSet* MapTreeToDataset(mapTree* ClosedMapTree, SupportContainer* SupportDataset);
	TreeNodes treesIntersection(TreeNodes t1, TreeNodes t2);

};

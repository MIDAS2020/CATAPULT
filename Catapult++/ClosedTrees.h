#pragma once
#include "TreeMining.h"
#include "SupportFactory.h"
//#include "TreeFactory.h"
#define ADD_OPTION 1
#define DELETE_OPTION 2 

typedef map<int, mapTree> mapSupLenTree;
typedef map<int, mapTree>::iterator mapSupLenTreeIterator;

typedef list<Tree*> listTree;
typedef listTree::iterator listTreeIterator;
typedef vector<int> SupportContainer;
typedef vector<int>::iterator SupportContainerIterator;

class ClosedTrees {
public:
	ClosedTrees(Support_Factory* support_factory, TreeFactory* tree_factory, bool is_Adaptive2, int* BatchSize, int* WindowSize, double dblmin_sup);                       // Constructor
	virtual ~ClosedTrees() { delete LatticeTreeList; delete LatticeSupLenTree; };              // Destructor
	inline double NumberClosedTrees() {
		double NumberTrees = 0;
		for (mapSupLenTreeIterator iter1 = LatticeSupLenTree->begin(); iter1 != LatticeSupLenTree->end(); ++iter1) {
			if (!isInsufficientRelaxedInputSupport(iter1->first))
				NumberTrees += iter1->second.size();
		}
		return NumberTrees;
	};
	void EraseNotClosedTrees();
	inline int get_size() { return LatticeTreeList->size(); };
	void print();
	void Check_Closure_List(listTree* TreesCheckClosureList);
	void Check_Closure_List_Delete(listTree* TreesCheckClosureList);
	inline bool isInsufficientRelaxedSupport(int NewSupport) {
		return (NewSupport < msupport_factory->RelaxedSupport(mdblmin_sup));
	};
	inline bool isInsufficientRelaxedInputSupport(int NewSupport) {
		cout << NewSupport << "---" << msupport_factory->RelaxedSupport(mdblmin_sup * 2) << endl;
		return (NewSupport < msupport_factory->RelaxedSupport(mdblmin_sup * 2)); //Must be variable not fix
	};
	inline int get_Change() { return mChange; };
	inline void set_Change(int m) { mChange = m; };
	void insert(TreeNodes* tn, int iSupport, bool blUpdate = true);
	void insertMapSupLenTree(Tree* LatticeNode, int iSupport);
	void eraseMapSupLenTree(TreeNodes tn, int iSupport);
	bool updateSupportAdd(TreeNodes tn, int incSupport);
	void updateSupportDelete(TreeNodes tn, int incSupport, listTree* TreesCheckClosureList);
	TreeNodes closure(Tree* t, int* support);

	void UpdateSupport(int intOption);
protected:
private:
	mapTree* LatticeTreeList;
	mapSupLenTree* LatticeSupLenTree;
	bool mis_Adaptive2;
	int* mWindowSize;
	Support_Factory* msupport_factory;
	TreeFactory* mtree_factory;
	double mdblmin_sup;
	int* mBatchSize;
	int mChange;
};

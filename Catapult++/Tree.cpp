#include "Tree.h"



Tree::Tree() {
	// Implementation of constructor: Initialize an empty tree 
#if defined(LABELLED)
#else
	tree.push_back(0);
#endif
	Support = 0;
	lenTail = 0;
	isNotMaximal = false;
	Update = false;
}

////////////////////////////////////////////////////////////////////////////////

Tree::Tree(Tree* t) {
	// Implementation of constructor: Initialize a tree as a copy of a tree t
	tree = t->tree;
	lenTail = t->lenTail;
	Support = t->Support;
	isNotMaximal = false;
	Update = t->Update;
#if defined(LABELLED)
	parentsLabel = t->parentsLabel;
#endif
}

////////////////////////////////////////////////////////////////////////////////

Tree::~Tree() {
	// Implementation of destructor
}

////////////////////////////////////////////////////////////////////////////////

void Tree::print(ofstream* ofile, DatasetReader* mdataset_reader) {
	//Prints Tree

	int iSupport = Support;
#if defined(LABELLED)
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) * ofile << "(" << it->depth << "," << it->label << ") ";
#else
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) * ofile << *it << " ";
#endif
	//* ofile << "Support : " << iSupport << endl;
	* ofile <<  endl;
}
//////////////////////Added by Kai/////////////////////////
void Tree::print2(int treeid, ofstream* ofile, DatasetReader* mdataset_reader) {
	//Prints Tree
	vector<int> parentsindex;
	*ofile << "t # " << treeid << " " << tree.size() << endl;
	int i = 0;
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++){
		* ofile << "v " << i << " " << it->label << endl;
		i++;
	}
    i = 0;
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) {
		if (parentsindex.size() == it->depth)
			parentsindex.push_back(i);
		else
			parentsindex.at(it->depth) = i;
		if (i == 0) {
			i++;
			continue;
		}
		int parentid = parentsindex[it->depth -1];
		*ofile << "e " << parentid << " " << i << " 0" << endl;
		i++;
	}
    *ofile << endl;

}

////////////////////////////////////////////////////////////////////////////////

void Tree::cout_print() {
	//Prints Tree
#if defined(LABELLED)
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) cout << "(" << it->depth << "," << it->label << ") ";
#else
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) cout << *it << " ";
#endif
	int iSupport = Support;
	cout << "Support : " << iSupport << endl;
}

////////////////////////////////////////////////////////////////////////////////

void Tree::print_l(TreeNodes  tree) {
	//Prints Tree
#if defined(LABELLED)
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) cout << "(" << it->depth << "," << it->label << ") ";
#else
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) cout << *it << " ";
#endif
}

////////////////////////////////////////////////////////////////////////////////

void Tree::printSupport(ofstream * ofile) {
	//Prints Tree
	if (tree.size() == 1) return;
	Integer old = -1;
	for (TreeNodes::iterator it = tree.begin(); it != tree.end(); it++) {
#if defined(LABELLED)
		if (it->depth <= old)
			for (int j = 0; j <= (old - it->depth); j++)
				* ofile << "]";
		old = it->depth;
		*ofile << "[" << it->label;
#else
		if (*it <= old)
			for (int j = 0; j <= old - *it; j++)
				* ofile << "]";
		old = *it;
		*ofile << "["
#endif
			;
	}
	for (int j = 0; j <= old; j++) * ofile << "]";
	int iSupport = Support;

	*ofile << " " << iSupport << endl;
}

#include "TreeMining.h"
#include "SmallGraphClustering.h"
#include <boost\graph\vf2_sub_graph_iso.hpp>


TreeMining::TreeMining(TreeFactory* tree_factory, DatasetReader* dataset_reader, int sup) {
	// Implementation of constructor
	mapTree* cmt = new mapTree;
	mClosedMapTree = cmt;
	mtree_factory = tree_factory;
	mdataset_reader = dataset_reader;
	D = dataset_reader->dataset();
	min_sup = sup;

#if defined(LABELLED)
	for (Labels_iterator label = mdataset_reader->get_labels()->begin(); label != mdataset_reader->get_labels()->end(); ++label) {
		Tree* init_tree = mtree_factory->create_tree(D, *label);
		//cout <<"init_tree->get_Support() :"<< init_tree->get_Support() << endl;
		if (init_tree->get_Support() >= min_sup) {
			init_tree->parentsLabel.push_back(*label);
			closed_subtree_mining(init_tree);
		}
	}
#else
	Tree* init_tree = mtree_factory->create_tree(D);
	closed_subtree_mining(init_tree);
#endif
}


////////////////////////////////////////////////////////////////////////////////

TreeMining::~TreeMining() {
	// Implementation of destructor
	delete mClosedMapTree;
}

////////////////////////////////////////////////////////////////////////////////
char TreeMining::readcommand(FILE* file) {

	char car = fgetc(file);
	while (car < 'a' || car > 'z') {
		if (feof(file))
			return -1;
		car = fgetc(file);
	}
	return car;

}
int TreeMining::readInt(FILE* input) {

	char car = fgetc(input);
	while (car < '0' || car > '9') {
		if (feof(input))
			return -1;
		car = fgetc(input);
	}
	int n = car - '0';
	car = fgetc(input);
	while (car >= '0' && car <= '9') {
		n = n * 10 + car - '0';
		car = fgetc(input);
	}
	//std::cout << n << endl;
	return n;

}
int TreeMining::print_Closed_Trees2(const char* filenameOrig) {

	//Prints list of tree to fileputput
	char* filename = "tempfct.txt";
	ofstream ofile;
	ofile.open(filename);
	int count = 0;
	if (ofile.is_open()) {
		int auxsupport = 0;
		mapTreeIterator iter_First_Same_Support = mClosedMapTree->begin();
		
		for (mapTreeIterator iter = mClosedMapTree->begin(); iter != mClosedMapTree->end(); ++iter) {
			(iter->second)->print2(count, &ofile, mdataset_reader);
			//Check if there is a tree with the same support list
			if (auxsupport != iter->first) {
				auxsupport = iter->first;
				iter_First_Same_Support = iter;
			}
			count++;
		}
		ofile.close();
	}
	else //error("Unable to open output file");
		cout << "Unable to open output file" << endl;

	FILE* input = fopen(filename, "r");
	vector<graphClosure> AllfrequentSubtree;
	for (int graphId = 0; graphId < count; graphId++) {
		vector<int> newcontaiList;
		vector<vertex_t> vertexes;
		int graphid = readInt(input);
		int graphvertexNum = readInt(input);
		//std::cout << "graphid:" << graphid << " graphvertexNum:" << graphvertexNum << endl;
		graphClosure newgraph(graphvertexNum);
		ve_it vi, vi_end;
		vertex_nam newname_map = get(vertex_name, newgraph);
		containList newcontaiList1 = get(vertex_containlist, newgraph);
		edgecontainList newcontaiListedge = get(edge_containlist, newgraph);
		char c = readcommand(input);
		for (boost::tie(vi, vi_end) = vertices(newgraph); vi != vi_end; ++vi) {
			int vertexId = readInt(input);
			int vertexLabelId = readInt(input);
			string vertexName = vertexLable[vertexLabelId];
			vertexes.push_back(*vi);
			c = readcommand(input);
			newname_map[*vi] = vertexName;
			std::set<int> test;
			test.insert(graphId);
			newcontaiList1[*vi] = test;
		}
		while (c == 'e') {
			int firstEdge = readInt(input);
			int secondEdge = readInt(input);
			edge_t newEdge;
			bool testFlag;
			tie(newEdge, testFlag) = add_edge(vertexes[firstEdge], vertexes[secondEdge], newgraph);
			newcontaiListedge[newEdge].insert(graphId);
			c = readcommand(input);
		}
		AllfrequentSubtree.push_back(newgraph);
	}
	fclose(input);

	vector<int> SelectedfrequentSubtreeIndex;
	//frequentsubtree.push_back(AllfrequentSubtree[0]);
	for (int i = 0; i < AllfrequentSubtree.size(); i++) {
		graphClosure g1 = AllfrequentSubtree[i];
		bool flag = true;// true mean can join 
		for (int j = 0; j < i; j++) {
			graphClosure g2 = AllfrequentSubtree[j];
			bool Ans = true;
			if (num_vertices(g1) != num_vertices(g2) || num_edges(g1) != num_edges(g2)) 
				Ans = false;
			else {
				bool testFlag1 = false;
				deletePattern_callback<graphClosure> user_callback1(testFlag1);
				vf2_subgraph_mono(g1, g2, user_callback1,
					get(vertex_index, g1), get(vertex_index, g2),
					vertex_order_by_mult(g1),
					always_equivalent(),
					make_property_map_equivalent(get(vertex_name, g1), get(vertex_name, g2)));
				if (!testFlag1) {
					Ans = false; 
				}
				else {
					bool testFlag2 = false;
					deletePattern_callback<graphClosure> user_callback2(testFlag2);
					vf2_subgraph_mono(g2, g1, user_callback2,
						get(vertex_index, g2), get(vertex_index, g1),
						vertex_order_by_mult(g2),
						always_equivalent(),
						make_property_map_equivalent(get(vertex_name, g2), get(vertex_name, g1)));
					if (!testFlag2) {
						Ans = false;
					}
				}
			}
			if (Ans) {
				flag = false;  // can not join
				break;
			}
		}
		if (flag) {
			SelectedfrequentSubtreeIndex.push_back(i);
		}
	}

	if (true) {
		//Prints list of tree to fileputput
		ofstream ofile;
		ofile.open(filenameOrig);
		int count = 0;
		int i = 0;
		if (ofile.is_open()) {
			int auxsupport = 0;
			mapTreeIterator iter_First_Same_Support = mClosedMapTree->begin();
			for (mapTreeIterator iter = mClosedMapTree->begin(); iter != mClosedMapTree->end(); ++iter) {
				vector<int>::iterator it;
				it = find(SelectedfrequentSubtreeIndex.begin(), SelectedfrequentSubtreeIndex.end(), count);
				if (it != SelectedfrequentSubtreeIndex.end()) {
					(iter->second)->print2(i, &ofile, mdataset_reader);
					//Check if there is a tree with the same support list
					if (auxsupport != iter->first) {
						auxsupport = iter->first;
						iter_First_Same_Support = iter;
					}
					i++;
				}
				count++;
			}
			ofile.close();
		}
		else //error("Unable to open output file");
			cout << "Unable to open output file" << endl;
	}
	return SelectedfrequentSubtreeIndex.size();

}

void TreeMining::print_Closed_Trees(const char* filenameOrig) {
	//Prints list of tree to fileputput
	ofstream ofile;
	ofile.open(filenameOrig);
	if (ofile.is_open()) {
		int auxsupport = 0;
		mapTreeIterator iter_First_Same_Support = mClosedMapTree->begin();
		int i = 0;
		for (mapTreeIterator iter = mClosedMapTree->begin(); iter != mClosedMapTree->end(); ++iter) {
			//Print tree data
			//if ((iter->second)->get_isNotMaximal() == false)  ofile << "M ";
			///!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!//////////////////
			//(iter->second)->print(&ofile, mdataset_reader);
			(iter->second)->print2(i, &ofile, mdataset_reader);

			//Check if there is a tree with the same support list
			if (auxsupport != iter->first) {
				auxsupport = iter->first;
				iter_First_Same_Support = iter;
			}
			i++;
		}
		ofile.close();
	}
	else //error("Unable to open output file");
		cout << "Unable to open output file" << endl;
}

////////////////////////////////////////////////////////////////////////////////

void TreeMining::closed_subtree_mining(Tree* t) {
	//Implicit parameters: TreeDataSet *D, int min_sup, mapTree *mClosedMapTree
	bool is_closed = true;
	int len_tail = t->get_lenTail();
	//cout << "1111111:" << len_tail << endl;
	//For  every tt  that can be extended from t in one step 
#if defined(LABELLED) 
	for (int depth = len_tail + 1; depth > 0; depth--) {
		//cout << "2222222:" << depth << endl;
		Labels* labels;
		int level = len_tail + 1 - depth;
		if (depth == 0)
			labels = mdataset_reader->get_labels();
		else if (depth == 1)
			labels = mdataset_reader->get_labels(t->currentAncestorLabel(level));
		else if (depth == 2)
			labels = mdataset_reader->get_labels(t->currentAncestorLabel(level), t->currentAncestorLabel(level + 1));
		else
			labels = mdataset_reader->get_labels(t->currentAncestorLabel(level), t->currentAncestorLabel(level + 1), t->currentAncestorLabel(level + 2));
		for (Labels_iterator label = labels->begin(); label != labels->end(); ++label) {
			Tree* tt = mtree_factory->create_tree(t);
			Node extension(depth, *label);
#else
	for (int extension = len_tail + 1; extension > 0; extension--) {
		Tree* tt = mtree_factory->create_tree(t);
#endif
		tt->extend(extension);
		//cout << "333333:" << endl;
		if (tt->isCanonicalRepresentative(extension)) {
			tt->supportInc(D, min_sup);
			//cout << "44444444:" << tt->get_Support() << endl;
			if (tt->get_Support() >= min_sup) {
				t->set_isNotMaximal(true);
#if defined(LABELLED) 					
				tt->set_lenTail(depth);
				if (tt->parentsLabel.size() == depth)
					tt->parentsLabel.push_back(*label);
				else
					tt->parentsLabel.at(depth) = *label;
#else
				tt->set_lenTail(extension);
#endif
				//cout << "555555555:"  << endl;
				closed_subtree_mining(tt);
				//cout << "666666666:" << endl;
			}
			else
				delete tt;
			if (tt->get_Support() == t->get_Support()) {
				is_closed = false;
			}
		}
		else delete tt;
#if defined(LABELLED)    
	}
#endif
		}
if (is_closed) {
	//Check that it is not included in subtrees of mClosedMapTree               
	/*for (mapTreeIterator cpos = mClosedMapTree->begin(); cpos != mClosedMapTree->end(); ++cpos) {
		Tree* tpos = cpos->second;
		cout << "7777777777:" << endl;
		if (tpos->get_Support() == t->get_Support()) {
			if (tpos->get_Size() > t->get_Size()) {
				if (t->isSubtreeOf(tpos->get_TreeNodes())) is_closed = false;
			}
			else {
				if (tpos->isSubtreeOf(t->get_TreeNodes())) {
					cout << "fffffff:" << endl;
					mClosedMapTree->erase(cpos);
				}
			}
		}
		else { //Check for maximals
			if (tpos->get_Size() > t->get_Size()) {
				if (t->isSubtreeOf(tpos->get_TreeNodes())) t->set_isNotMaximal(true);
			}
			else {
				if (tpos->isSubtreeOf(t->get_TreeNodes())) {
					((cpos->second))->set_isNotMaximal(true);
				}
			}
		}
		cout << "8888888888:" << endl;
	}*/
	for (mapTreeIterator cpos = mClosedMapTree->begin(); cpos != mClosedMapTree->end(); ) {
		Tree* tpos = cpos->second;
		if (tpos->get_Support() == t->get_Support()) {
			if (tpos->get_Size() > t->get_Size()) {
				if (t->isSubtreeOf(tpos->get_TreeNodes())) is_closed = false;
				++cpos;
			}
			else {
				if (tpos->isSubtreeOf(t->get_TreeNodes())) {
					cpos = mClosedMapTree->erase(cpos);
				}
				else
					++cpos;
			}
		}
		else { //Check for maximals
			if (tpos->get_Size() > t->get_Size()) {
				if (t->isSubtreeOf(tpos->get_TreeNodes())) t->set_isNotMaximal(true);
			}
			else {
				if (tpos->isSubtreeOf(t->get_TreeNodes())) {
					((cpos->second))->set_isNotMaximal(true);
				}
			}
			++cpos;
		}
	}

	if (is_closed) {
		int iSup = t->get_Support();
#if !defined(LABELLED)
		if (t->get_Size() == 2) {
			iSup = mdataset_reader->size() - mdataset_reader->oneTreesDataSet_size();
			t->set_Support(iSup);
		}
#endif
		//cout << "9999999999:" << endl;
		mClosedMapTree->insert(pair<int, Tree*>(iSup, t));
		//cout << "100000000:" << endl;
	}
	else delete t;
}
else
delete t;
	}

	////////////////////////////////////////////////////////////////////////////////


	void TreeMining::Compress_MapTree() {
		for (mapTreeIterator iter = mClosedMapTree->begin(); iter != mClosedMapTree->end(); ++iter) {
			iter->second->compress();
		}
	}


#pragma once
#include<iostream>
#include<list>
#include<vector>
#include<set>
#include<map>
#include<fstream>
#include <ctime>
using namespace std;
void error(string  s) {
	cerr << s << '\n'; //cin.get(); 
	exit(1);
}


void error(char* s, char* s2 = (char*)"") {
	cerr << s << ' ' << s2 << '\n'; //cin.get(); 
	exit(1);
}

#include "Adwin.h"	
#include "DatasetReader.h"
#include "Tree.h"
#include "OrderedTree.h"
#include "UnorderedTree.h"
#include "TreeFactory.h"
#include "TreeMining.h"
#include "SupportFactory.h"
#include "ClosedTrees.h"
#include "AdaTreeNat_Mining.h"
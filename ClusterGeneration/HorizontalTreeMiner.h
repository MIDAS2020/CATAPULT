#pragma once
#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <algorithm>
#include <functional>
#include <vector>
#include <list>
#include <iterator>
#include <cmath>
#include <ctime>
#include <utility>
#include "misc.h"
#include "FreeTree.h"
#include "CanonicalTree.h"
#include "TupleTree.h"
#include "FrequentTreeList.h"
#include "CoreList.h"

class HorizontalTreeMiner {
public:
	vector<map<CanonicalTree, supportNode>> getFrequentTreeList(int support, string path1, string path2);
};
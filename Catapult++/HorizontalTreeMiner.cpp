// HorizontalTreeMiner.cpp : Defines the entry point for the console application.
#include "HorizontalTreeMiner.h"
using namespace std;
long SUPPORT_THRESHOLD = 0;
bool NEED_MAXIMUM = false;
vector<map<CanonicalTree, supportNode>> HorizontalTreeMiner::getFrequentTreeList(int support, string path1, string path2)
{
	vector<map<CanonicalTree, supportNode>> vecFreTreeList;

	int argc = 4;
	//not use argv[1]
	char* argv[4];
	argv[0] = "TreeMiner"; argv[1] = "0"; argv[2] = (char*)path1.c_str(); argv[3] = (char*)path2.c_str();
	if (argc != 4)
	{
		cout << "Usage: FreeTreeMiner support input_file output_file" << endl;
		exit(1);
	}
	SUPPORT_THRESHOLD = support;
	long totalFrequent = 0;
	long totalFrequentMaximum = 0;
	long temp;
	string inputFile = argv[2];
	string outputFile = argv[3];
	ofstream dataFile(outputFile.c_str());
	if (!dataFile)
	{
		cerr << "cannot open file!" << endl;
		exit(1);
	}
	ifstream inFile(inputFile.c_str());
	if (!inFile)
	{
		cerr << "cannot open INPUT file!" << endl;
		exit(1);
	}
	//read in the database
	ptrFreeTree pft;
	vector<ptrFreeTree> database;
	char c;
	long counter = 1;
	database.push_back(new FreeTree()); //a dummy tree
	while (!inFile.eof())
	{
		c = inFile.get();
		if (inFile.eof()) break;
		else inFile.putback(c);
		pft = new FreeTree();
		inFile >> *pft;
		//pft->tid = counter++; //force the tid list to be
		database.push_back(pft);
		c = inFile.get(); //to eat the last "end of line"
						  //delete pft;
	}
	inFile.close();
	//dataFile << "#Output Statistics for " << argv[2] << endl;
	//dataFile << "#with support " << support << endl << endl;
	FrequentTreeList* ftl[500]; //at most 500 vertices
	ftl[0] = new FrequentTreeList();
	ftl[0]->populate2(database);
	totalFrequent += ftl[0]->returnSize();
	ftl[0]->printfrequentList(dataFile);

	vecFreTreeList.push_back(ftl[0]->getfrequentList());

	//dataFile << "number of 2-frequent tree: " << ftl[0]->returnSize() << endl;
	cout << "number of 2-frequent tree: " << ftl[0]->returnSize() << endl;
	CoreList* pcl;
	short i = 0;
	while (ftl[i]->returnSize() != 0)
	{
		pcl = new CoreList();
		ftl[i + 1] = new FrequentTreeList();
		pcl->readFrom(*ftl[i]);
		pcl->writeTo(*ftl[i + 1]);
		ftl[i + 1]->checkDownward(*ftl[i]);
		//dataFile << "before couting support, the candidate" << i + 3 << " tree set is: " << ftl[i + 1]->returnSize() << endl;
		cout << "before couting support, the candidate" << i + 3 << " tree set is: " << ftl[i + 1]->returnSize() << endl;
		ftl[i + 1]->countSupport(database);
		ftl[i + 1]->finalize(SUPPORT_THRESHOLD);
		ftl[i + 1]->printfrequentList(dataFile);
		vecFreTreeList.push_back(ftl[i + 1]->getfrequentList());
		//dataFile << "the size of frequent" << i + 3 << " tree set is: " << ftl[i + 1]->returnSize() << endl;
		cout << "the size of frequent" << i + 3 << " tree set is: " << ftl[i + 1]->returnSize() << endl;
		delete pcl;
		totalFrequent += ftl[i + 1]->returnSize();
		delete ftl[i];
		i++;
	}
	for (long s = 0; s < database.size(); s++)
	{
		delete database[s];
	}
	//dataFile << "*****Number of Frequent Subtrees: " << totalFrequent << endl;
	cout << "*****Number of Frequent Subtrees: " << totalFrequent << endl;
	dataFile.close();
	return vecFreTreeList;
}

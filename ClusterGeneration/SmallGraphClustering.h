#pragma once
#include <vector>
#include <fstream>
#include <iostream>
#include "boost/tuple/tuple.hpp"
#include "FeatureVector.h"
#include <string>
#include <boost/graph/iteration_macros.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/property_iter_range.hpp>
#include <boost/config.hpp>
#include <boost/graph/graphviz.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/graph/properties.hpp>
#include <boost/property_map/shared_array_property_map.hpp>
#include <numeric>
#include <iterator>
#include <omp.h>
#include "RunningTime.h"
#include "process_stat.h" 
#include "HorizontalTreeMiner.h"
#include "KmeansPP.h"
#include <icrsint.h>
#include <boost/algorithm/string/classification.hpp>
#include <CString>
#include<iomanip>
#include <atlstr.h>
namespace boost {
	enum vertex_containlist_t {
		vertex_containlist
	};
	enum edge_containlist_t {
		edge_containlist
	};
	enum vertex_matched_t {
		vertex_matched = false

	};
	enum edge_visited_t {
		edge_visited = false
	};
	enum vertex_visited_t {
		vertex_visited = false

	};
	enum edge_newweight_t {
		edge_newweight = 1
	};
	enum edge_score_t {
		edge_score
	};
	BOOST_INSTALL_PROPERTY(vertex, containlist);
	BOOST_INSTALL_PROPERTY(edge, containlist);
	BOOST_INSTALL_PROPERTY(vertex, matched);
	BOOST_INSTALL_PROPERTY(edge, visited);
	BOOST_INSTALL_PROPERTY(vertex, visited);
	BOOST_INSTALL_PROPERTY(edge, score);
	BOOST_INSTALL_PROPERTY(edge, newweight);
}

using namespace boost;
using namespace std;
typedef adjacency_list< vecS, vecS, undirectedS,
	property<vertex_name_t, std::string,
	property<vertex_containlist_t, std::set<int>,
	property<vertex_matched_t, bool, property <
	vertex_visited_t, bool, property<vertex_distance_t, int>  > > > >,
	property<edge_containlist_t, std::set<int>,
	property<edge_visited_t, bool,
	property<edge_score_t, int, property<edge_index_t, int, property<edge_newweight_t, int> >  > > > >  graphClosure;
template <typename Graph>
class deletePattern_callback {
public:
	typedef typename graph_traits<Graph>::vertices_size_type VertexSizeFirst;
	deletePattern_callback(bool& newHasPattern) : testHasPattern(newHasPattern) { }
	template <typename CorrespondenceMapFirstToSecond,
		typename CorrespondenceMapSecondToFirst>
		bool operator()(CorrespondenceMapFirstToSecond correspondence_map_1_to_2,
			CorrespondenceMapSecondToFirst correspondence_map_2_to_1) {
		testHasPattern = true;
		return false;
	}
private:
	bool& testHasPattern;
};

typedef property_map<graphClosure, edge_index_t>::type edge_ind;
typedef graph_traits<graphClosure>::vertex_iterator ve_it;
typedef property_map<graphClosure, vertex_index_t>::type vertex_ind;
typedef property_map<graphClosure, vertex_name_t>::type vertex_nam;
typedef property_map<graphClosure, vertex_containlist_t>::type containList;
typedef graph_traits<graphClosure>::adjacency_iterator adj_it;
typedef graph_traits<graphClosure>::vertices_size_type vertices_size;
typedef graph_traits<graphClosure>::vertex_descriptor vertex_t;
typedef property_map<graphClosure, edge_containlist_t>::type edgecontainList;
typedef graph_traits<graphClosure>::edge_descriptor edge_t;
typedef graph_traits<graphClosure>::edges_size_type edge_size;
typedef graph_traits<graphClosure>::edge_iterator ed_it;
typedef property_map<graphClosure, vertex_matched_t>::type vertex_mat;
typedef property_map<graphClosure, edge_visited_t>::type edge_vis;
typedef property_map<graphClosure, vertex_visited_t>::type vertex_vis;
typedef graph_traits<graphClosure>::degree_size_type degree_size;
typedef property_map<graphClosure, edge_score_t>::type edge_sco;
typedef property_map<graphClosure, edge_newweight_t>::type edge_weight_new;
typedef property_map<graphClosure, vertex_distance_t>::type ver_distance;


//AIDS dataset
static string vertexLable1[] = {
	"C", "O", "Cu", "N", "S", "P", "Cl", "Zn", "B", "Br", "Co", "Mn", "As", "Al", "Ni", "Se",
	"Si", "V", "Sn", "I", "F", "Li", "Sb", "Fe", "Pd", "Hg", "Bi", "Na", "Ca", "Ti", "Ho", "Ge",
	"Pt", "Ru", "Rh", "Cr", "Ga", "K", "Ag", "Au", "Tb", "Ir", "Te", "Mg", "Pb", "W", "Cs", "Mo",
	"Re", "Cd", "Os", "Pr", "Nd", "Sm", "Gd", "Yb", "Er", "U", "Tl", "Ac"
};
static  vector<string> vertexLable(vertexLable1, vertexLable1 + 60);

/*
//emolecule dataset, 74->130
static string vertexLable1[] = {
"Cs", "Cu", "Yb", "Cl", "Pt", "Pr", "Co", "Cr", "Li", "Cd", "Ce", "Hg", "Hf", "La", "Lu",
"Pd", "Tl", "Tm", "Ho", "Pb", "*", "Ti", "Te", "Dy", "Ta", "Os", "Mg", "Tb", "Au", "Se",
"F", "Sc", "Fe", "In", "Si", "B", "C", "As", "Sn", "N", "Ba", "O", "Eu", "H", "Sr", "I", "Mo",
"Mn", "K", "Ir", "Er", "Ru", "Ag", "W", "V", "Ni", "P", "S", "Nb",
"Y", "Na", "Sb", "Al", "Ge", "Rb", "Re", "Gd", "Ga", "Br", "Rh", "Ca", "Bi", "Zn", "Zr",
"R#", "R","X","R1","A","U",	"Ar",	"Kr",	"Xe",	"e", ".",	"Tc",	 "Mu", "Mu-", "He",	"Ps",	"At",
"Po",	"Be",	"Ne","Rn",	"Fr",	"Ra",	"Ac",	"Rf",	"Db", "Sg","Bh",	"Hs",		"Mt",
"Ds",	"Rg",	"Nd","Pm",		"Sm",	"Th",	"Pa",	"Np","Pu",	"Am",	"Cm",	"Bk",
"Cf","Es",	"Fm",	"Md", "No",	"Lr","0",	"Uub", "R2",	"R3",	"R4",	"D", "R5",	"ACP"
};
static  vector<string> vertexLable(vertexLable1, vertexLable1 + 130);
*/
/*
//modified: added a new label  "Lr"
//pubchem dataset
static string vertexLable1[] = {
"H", "C", "O", "N", "Cl", "S", "F", "P", "Br", "I", "Na", "Si",
"As", "Hg", "Ca", "K", "B", "Sn", "Se", "Al", "Fe", "Mg", "Zn", "Pb", "Co", "Cu",
"Cr", "Mn", "Sb", "Cd", "Ni", "Be", "Ag", "Li", "Tl", "Sr", "Bi", "Ce", "Ba", "U", "Ge",
"Pt", "Te", "V", "Zr", "Cs", "Au", "Mo", "W", "La", "Ti", "Rh", "Lu", "Pd", "In", "Eu", "Ga",
"Pr", "Ho", "Th", "Ta", "Tc", "Tb", "Ir", "Nd", "Nb", "Rb", "Kr", "Yb", "Cm", "Pu", "Cf", "Hf",
"He", "Pa", "Tm", "Pm", "Po", "Xe", "Dy", "Os", "Md", "Sc", "Ar", "At", "Sm", "Er", "Ru",
"Es", "Ac", "Am", "Ne", "Y", "Re", "Gd", "No", "Rn", "Np", "Fm", "Bk", "Lr"
};
static  vector<string> vertexLable(vertexLable1, vertexLable1 + 101); 
*/


//static bool is_fctAsFeatureVactor = true;
static bool is_fctAsFeatureVactor = false;

static string databasefilename = "AIDS40k";  
//static string databasefilename = "pubchem29613"; 
//static string databasefilename = "emolecul10000"; 

//static string databasefilename = "pubchem1000000clean.txt"; 

//static int initialsizeofgraph = 950000;
static int initialsizeofgraph = 30000;
//static int initialsizeofgraph = 23238;
//static int initialsizeofgraph = 15000;
//static int initialsizeofgraph = 29613;
//static int initialsizeofsampledgraph = initialsizeofgraph;
static int initialsizeofsampledgraph = initialsizeofgraph;

//static int addedsizeofgraph = 6375;
//static int addedsizeofgraph = 5000;
static int addedsizeofgraph = 0;
static int addedsizeofsampledgraph = addedsizeofgraph;

static int removedsizeofgraph = 0;
static int removedsizeofsampledgraph = removedsizeofgraph;

static string initialclustername = "initialcluster.txt";

static string updateclustername = "updatecluster.txt";

//static string addedfinalclustername = "addedfinalcluster.txt";
//static string removedfinalclustername = "removedfinalcluster.txt";

static string initialclusterAuxInfo = "initialclusterAuxInfo.txt";

static string updateclusterAuxInfo = "updateclusterAuxInfo.txt";

//static string addedfinalclusterAuxInfo = "addedfinalclusterAuxInfo.txt";
//static string removedfinalclusterAuxInfo = "removedfinalclusterAuxInfo.txt";

static int    IsAOldCluster = 0;
static int    IsANewCluster = 1;
//static double closureconceptdrift_threshold = 0.499999;

static string initialgraphs2ftfilename = "initialgraph2freetree";

static string updategraphs2ftfilename = "updategraph2freetree";

//static string addedupdatedgraphs2ftfilename = "addedupdatedgraphs2ftfilename";
//static string removedupdatedgraphs2ftfilename = "removedupdatedgraphs2ftfilename";

static string initialfresubtreefilename = "initialfrequentsubtree";

static string updatefresubtreefilename = "updatefresubtree";

//static string addedfinalfresubtreefilename = "addedfinalfresubtreefilename";
//static string removedfinalfresubtreefilename = "removedfinalfresubtreefilename";

//string  ModificationType;
//static double alphagraphclusterchanged = 0.3;
//static double betachangedinsize = 0.3;
//static double delta = 0.5;

//hyper-parameter
//const float high_threshold = 0.01f;       // support (>=support is frquent); 
//const float low_threshold = 0.01f;        //low support (>=support is frquent); 


const float high_threshold = 0.2f;       // support (>=support is frquent); 
const float low_threshold = 0.17f;        //low support (>=support is frquent); 

										  
										  //const float high_threshold = 0.1f;       // support (>=support is frquent); 
//const float low_threshold = 0.1f;        //low support (>=support is frquent); 
const float thresholdforcluster = 0.2f;
const int thresholdfretree = 10000;  // total number of frequent subtree allowed
const float featureselratio = 1.0f; // ratio of features selection 

const float epislon = 0.1;
//const float epislon = 0.1;
const float X = 0.1;

const float dist = 0.1;


static bool IsReadInitialDataSet = false;

class SmallGraphClustering
{
private:
	vector<vector<int> >  globalClusters;
	vector<vector<FeatureVector> >  globalClustersFeatureVector;
	vector<int>  globalNewOrOldClusters;
	map<int, graphClosure> globalMapgraphs;

	int numberofgraphlets = 29;
	double  bonus_weight = 0.1;
	int clustersize = 20;
	// for each dimension,the final threshold is closureconceptdrift_threshold*sqrt(|dimension|)
	double datasetconceptdrift_distancethreshold = 0.01;
public:
	SmallGraphClustering(ofstream& logfile);

	SmallGraphClustering();
	~SmallGraphClustering();
	void readGraphsFromFile(FILE* input, int start, int end);
	graphClosure  readFromFile(FILE* input, int graphId);
	char readcommand(FILE* file);
	int readInt(FILE* input);
	int  transGraph2ft_fct(string infilename, string outfilename, int start1, int end1, int start2, int end2);
	int  transGraph2ft_fct4Cluster(string outfilename, vector<int>& graphids);

	int  transGraph2ft(string infilename, string outfilename, int start1, int end1, int start2, int end2);
	int  AdaTreeNat_fct(int totalsubtree, string infile, string outfile, float threshold);
	int  AdaTreeNat_fct2(int totalsubtree, string infile, string outfile, float threshold);

	void freuentFtMinerSampling(ofstream& oStream, int totalsubtree, vector<vector<short>>& allfeature, const int thresholdfretree, const float low_threshold, string infile, string outfile);
	vector<FeatureVector>* featureSelection_fct(const int SizeOfSelectFeature, ofstream& oStream, string infilename, string dbfilename, int start1, int end1, int start2, int end2);
	void readSelectedFrequentSubtreesFromFile(vector<graphClosure>& frequentsubtree, vector<int> selectedfeature, int sizeofallfeature, string filename);
	//void writeGraphsToOri(std::vector<graphClosure> graphSet, string filename);
	void readFctFromFile(vector<graphClosure>& allfctinclusters, int sizeofallfeature, string filename);
	vector<graphClosure>  selectDistinctFct(vector<graphClosure>& AllfrequentSubtree);
	vector<FeatureVector>* featureSelection(const int SizeOfSelectFeature, vector<vector<short>>& allfeature, ofstream& oStream, string infilename, string dbfilename, int start1, int end1, int start2, int end2);
	
	bool test(const int SizeOfSelectFeature,  ofstream& oStream, string infilename, string dbfilename, int start1, int end1, int start2, int end2);


	graphClosure getClosure(std::set<int>& graphIdList);
	graphClosure findaGraphFromMap(int graphid);
	graphClosure getClosure2(graphClosure& g1, graphClosure& g2);
	void getWeightMatrix(graphClosure& g1, graphClosure& g2, vector< vector<double> >& W);
	graphClosure generateClosure(graphClosure& g1, graphClosure& g2, std::map<ve_it, ve_it>& map1, std::map<ve_it, ve_it>& map2);
	void evaluateClosure(graphClosure newClosure, set<int>& graphIdList, ofstream& oStream);
	void divideIn2Parts(std::set<int>& graphPartionList, std::set<int>& inList1, std::set<int>& inList2);
	double getSimScoreMCCS(graphClosure& g1, graphClosure& g2);
	double cosine_similarity(const vector<double>& v1, const vector<double>& v2);
	double euclidean(const vector<double>& v1, const vector<double>& v2);
	double minkowsky(const vector<double>& v1, const vector<double>& v2, double m);
	void finepartitionWithClusterFlag(vector<FeatureVector>& featurevec, std::set<int>& graphIdList, ofstream& oStream, ofstream& oStream2, ofstream& AuxInfofile, int neworold);
	vector<vector<int> > generateCluster(vector<FeatureVector>& featurevec, const int SizeOfSelectFeature, const int K, vector<datapoint>& centers);
	//void updateCSGThreeTypesModification(int initialdbsize, int adddbsize, int removedbsize, string addgraphfromdbname, ofstream& outfile);
	float getSubtreeSim(vector<short>& tree1, vector<short>& tree2);
	int longestCommonSubsequence(vector<short>& A, vector<short>& B);
	//void maintainClusterForSignificantChange(ofstream& logfile);
	void finepartition(std::set<int>& graphIdList, ofstream& oStream);
	void maintainClusterForMajorModification(vector<int>& NewOrOldVector, vector<vector<int>>& newclusters, ofstream& logfile);
	void finepartition2(std::set<int>& graphIdList, ofstream& oStream, ofstream& oStream2, ofstream& AuxInfofile, int neworold);

	vector<int> graph2GraphletVec(vector<int>& graphletvecAfterUpdated, int adddbsize, int  removedbsize, int initialdbsize, string graphdbname, string graphletdbname, ofstream& outfile);
	void updateCluster(int initialdbsize, int adddbsize, int removedbsize, string addgraphfromdbname, ofstream& outfile);
};


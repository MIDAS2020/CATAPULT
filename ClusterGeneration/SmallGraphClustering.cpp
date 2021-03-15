#include "SmallGraphClustering.h"
#include "EnumerateISE.h"
#include <string>
#include <boost/graph/vf2_sub_graph_iso.hpp>
#include <fstream>
#include<cstdlib>
#include <ctime>
#include<queue>
#include "boost/tuple/tuple.hpp"
#include <iterator>
#include "mccs.h"
#include <boost/algorithm/string/split.hpp>
#include "KmeansPP.h"

#include "TreeNat.h"
vector<graphClosure> frequentsubtree;
template <typename Graph>
struct example_callback {
	typedef typename graph_traits<Graph>::vertices_size_type VertexSizeFirst;

	example_callback(const Graph& graph1, int& graphsize) :
		m_graph1(graph1), returngraphsize(graphsize) {}

	template <typename CorrespondenceMapFirstToSecond,
		typename CorrespondenceMapSecondToFirst>
		bool operator()(CorrespondenceMapFirstToSecond correspondence_map_1_to_2,
			CorrespondenceMapSecondToFirst correspondence_map_2_to_1,
			VertexSizeFirst subgraph_size) {

		// Fill membership map for first graph
		typedef typename property_map<Graph, vertex_index_t>::type VertexIndexMap;
		typedef shared_array_property_map<bool, VertexIndexMap> MembershipMap;

		MembershipMap membership_map1(num_vertices(m_graph1),
			get(vertex_index, m_graph1));

		fill_membership_map<Graph>(m_graph1, correspondence_map_1_to_2, membership_map1);

		// Generate filtered graphs using membership map
		typedef typename membership_filtered_graph_traits<Graph, MembershipMap>::graph_type
			MembershipFilteredGraph;

		MembershipFilteredGraph subgraph1 =
			make_membership_filtered_graph(m_graph1, membership_map1);

		// Print the graph out to the console
		//std::std::cout << "Found common subgraph (size " << subgraph_size << ")" << std::endl;
		//print_graph(subgraph1);
		//std::std::cout << std::endl;
		typedef typename graph_traits<MembershipFilteredGraph>::edge_iterator filter_edge_it;
		filter_edge_it it1, it2;
		int i = 0;
		tie(it1, it2) = edges(subgraph1);
		for (; it1 != it2; it1++) {
			i++;
		}
		if (i > returngraphsize)
		{
			returngraphsize = i;
		}
		static int count = 0;
		count++;
		if (count > 100) {
			count = 0;
			return false;
		}
		// Explore the entire space
		return (true);
	}
private:
	const Graph& m_graph1;
	int& returngraphsize;
	VertexSizeFirst m_max_subgraph_size;
};
template <class T>
class mycoparison
{
public:
	bool operator()(const T& lhs, const T& rhs) const {
		return get<1>(lhs) < get<1>(rhs);
	}
};
vector<vector<int> > SmallGraphClustering::generateCluster(vector<FeatureVector>& featurevec,
	const int SizeOfSelectFeature, const int K, vector<datapoint>& centers) {
	// transform featurevec into datapoint for clustering
	vector<datapoint> data;
	for (int i = 0; i < featurevec.size(); i++) {
		short* p = featurevec[i].feature;
		datapoint dp;
		for (int j = 0; j < SizeOfSelectFeature; j++, p++) {
			dp.push_back((double)*p);
		}
		data.push_back(dp);
	}
	vector<datapoint> normalized_data;
	// K-Means Plus Plus for clustering
	KmeansPP KMPP(data);
	vector<vector<int> >  clusters_ = KMPP.RunKMeansPP(K, normalized_data);
	centers = normalized_data;
	return clusters_;
}
SmallGraphClustering::SmallGraphClustering(ofstream& logfile)
{

	////if (test(40, logfile, initialfresubtreefilename, databasefilename, 0, initialsizeofgraph, initialsizeofgraph, initialsizeofgraph)) {
	//	return;
	//}

	clock_t smallclusterstart, smallclusterend;
	smallclusterstart = clock();
	cout << "***StartSmallGraphClustering ***" << endl;
	clock_t fttimestart, fttimeend;
	fttimestart = clock();
	int totalsubtree = 0;
	if (is_fctAsFeatureVactor)
		totalsubtree = transGraph2ft_fct(databasefilename, initialgraphs2ftfilename, 0, initialsizeofsampledgraph, initialsizeofsampledgraph, initialsizeofsampledgraph); //for frequent closed tree
	else
		totalsubtree = transGraph2ft(databasefilename, initialgraphs2ftfilename, 0, initialsizeofsampledgraph, initialsizeofsampledgraph, initialsizeofsampledgraph); // for frequent subtree
	vector<vector<short>> allfeature;
	int SizeOfSelectFeature = 0;
	if (is_fctAsFeatureVactor)
		SizeOfSelectFeature = AdaTreeNat_fct(totalsubtree, initialgraphs2ftfilename, initialfresubtreefilename, low_threshold);
	else
		freuentFtMinerSampling(logfile, totalsubtree, allfeature, thresholdfretree, low_threshold, initialgraphs2ftfilename, initialfresubtreefilename);
	fttimeend = clock();
	logfile << "Feature extracting time: " << (fttimeend - fttimestart) << endl;

	clock_t seltimestart, seltimeend;
	seltimestart = clock();
	vector<FeatureVector> featurevec;
	featurevec.clear();
	if (is_fctAsFeatureVactor)
		featurevec = *featureSelection_fct(SizeOfSelectFeature, logfile, initialfresubtreefilename, databasefilename, 0, initialsizeofgraph, initialsizeofgraph, initialsizeofgraph);
	else {
		SizeOfSelectFeature = (int)(allfeature.size() * featureselratio);
		if (SizeOfSelectFeature > 40)  SizeOfSelectFeature = 40;
		featurevec = *featureSelection(SizeOfSelectFeature, allfeature, logfile, initialfresubtreefilename, databasefilename, 0, initialsizeofgraph, initialsizeofgraph, initialsizeofgraph);
	}
	seltimeend = clock();
	logfile << "Feature Selection time: " << (seltimeend - seltimestart) << endl;
	vector<datapoint> centers;
	clock_t clusterstart, clusterend;
	clusterstart = clock();
	const int K = initialsizeofgraph / this->clustersize;// K-means
	logfile << featurevec.size() << " " << K << endl;
	vector<vector<int> > clusters_ = generateCluster(featurevec, SizeOfSelectFeature, K, centers);
	clusterend = clock();
	logfile << "K-means time: " << (clusterend - clusterstart) << endl;
	ofstream clusterfile(initialclustername);
	ofstream AuxInfofile(initialclusterAuxInfo);
	int totalSum = 0;
	double zscore = 1.96;   //interval = 0.95
	double wide = 0.06;
	int sampleSize = (int)((zscore * zscore * 0.5 * 0.5) / ((wide / 2) * (wide / 2)) + 0.5);
	for (int i = 0; i < clusters_.size(); i++) {
		if (clusters_[i].size() <= this->clustersize) continue;
		totalSum += clusters_[i].size();
	}
	if (totalSum < sampleSize) {
		logfile << "error:totalSum:" << totalSum << " sampleSize:" << sampleSize << endl;
		sampleSize = totalSum;
	}
	set<int> TotalgraphIdList;
	vector<set<int> > sampledclusters;
	for (int i = 0; i < clusters_.size(); i++) {
		set<int> ACluster;
		if (clusters_[i].size() <= 0) continue;
		vector<int> temp;
		for (int j = 0; j < clusters_[i].size(); j++) {
			temp.push_back(clusters_[i][j]);
		}
		if (temp.size() > this->clustersize) {
			random_shuffle(temp.begin(), temp.end());
			int sample_size = (int)((double)sampleSize / (double)totalSum * temp.size());
			if (temp.size() < sample_size) logfile << "error:temp.size() < sample_size" << endl;
			if (sample_size < 2)  sample_size = 2;
			for (int i = 0; i < sample_size; i++) {
				TotalgraphIdList.insert(temp[i]);
				ACluster.insert(temp[i]);
			}
		}
		else {
			for (int i = 0; i < temp.size(); i++) {
				TotalgraphIdList.insert(temp[i]);
				ACluster.insert(temp[i]);
			}
		}
		sampledclusters.push_back(ACluster);
	}
	FILE* input = fopen(databasefilename.c_str(), "r");
	this->globalMapgraphs.clear();
	for (int i = 0; i < initialsizeofgraph; i++) {
		graphClosure  g = readFromFile(input, i);
		set<int>::iterator iter = TotalgraphIdList.find(i);
		if (iter != TotalgraphIdList.end())
			this->globalMapgraphs.insert(pair<int, graphClosure>(i, g));
	}
	fclose(input);
	for (int i = 0; i < sampledclusters.size(); i++) {
		set<int> graphIdList = sampledclusters[i];
		if (graphIdList.size() < 2) {
			//std::cout << "graphIdList.size()< 2" << endl;
			continue;
		}
		else if (graphIdList.size() <= this->clustersize) {
			AuxInfofile << IsAOldCluster << endl;
			globalNewOrOldClusters.push_back(IsAOldCluster);
			set<int>::iterator it;
			vector<int> graphinacluster;
			vector<FeatureVector> featurevectorincluster;
			for (it = graphIdList.begin(); it != graphIdList.end(); it++) {
				clusterfile << *it << "\t";
				graphinacluster.push_back(*it);
				featurevectorincluster.push_back(featurevec[*it]);
			}
			clusterfile << endl;
			globalClusters.push_back(graphinacluster);
			globalClustersFeatureVector.push_back(featurevectorincluster);
			graphClosure  newClosure = getClosure(graphIdList);
			evaluateClosure(newClosure, graphIdList, clusterfile);
		}
		else {
			//cout << "start with size: " << graphIdList.size() << endl;
			finepartitionWithClusterFlag(featurevec, graphIdList, logfile, clusterfile, AuxInfofile, IsAOldCluster);
			//cout << "end with size: " << graphIdList.size() << endl;
		}
	}
	smallclusterend = clock();
	logfile << "Small graph clustering time(ms) (Original): " << (smallclusterend - smallclusterstart) << endl;
	cout << "Small graph clustering time(ms) (Original): " << (smallclusterend - smallclusterstart) << endl;

	if (is_fctAsFeatureVactor) {
		clock_t smallclusterstart2, smallclusterend2;
		string graphletdbname = "graphlet";
		/// 1. graphletvec After Updated
		vector<int> graphletvecAfterUpdated;
		/// 3. original graphletvec 
		smallclusterstart2 = clock();
		vector<int> graphletvec = graph2GraphletVec(graphletvecAfterUpdated, addedsizeofgraph, removedsizeofgraph, initialsizeofgraph, databasefilename, graphletdbname, logfile);
		double sum = 0.0;
		for (int i = 0; i < graphletvec.size(); i++) {
			sum += abs(graphletvec[i] * 1.0 / initialsizeofgraph - graphletvecAfterUpdated[i] * 1.0 / (initialsizeofgraph + addedsizeofgraph - removedsizeofgraph));
		}
		sum = sqrt(sum);
		smallclusterend2 = clock();
		int tempt = (smallclusterend2 - smallclusterstart2) * 1.0 / (initialsizeofgraph + addedsizeofgraph) * (addedsizeofgraph + removedsizeofgraph + 0.000001);
		//cout << "smallclusterend2:" << smallclusterend2 << " ,smallclusterstart2:" << smallclusterstart2 << endl;
		logfile << "Small graph clustering time (Changedetection): " << tempt << endl;
		

		smallclusterstart2 = clock();
		if (sum > dist) {
			logfile << "distance is: " << sum << ", threshold is: " << dist << endl;
			logfile << "Need to maintain..." << endl;
			updateCluster(initialsizeofgraph, addedsizeofgraph, removedsizeofgraph, databasefilename, logfile);
		}
		else {
			logfile << "distance is: " << sum << ", threshold is: " << dist << endl;
			logfile << "No need to maintain..." << endl;
		}
		smallclusterend2 = clock();
		logfile << "Small graph clustering time(ms) (Maintenance): " << (smallclusterend2 - smallclusterstart2) << endl;
		cout << "Small graph clustering time(ms) (Maintenance): " << (smallclusterend2 - smallclusterstart2) << endl;
	}

}

void SmallGraphClustering::updateCluster(int initialdbsize, int adddbsize, int removedbsize, string addgraphfromdbname, ofstream& outfile)
{
	outfile << "*******updateCluster*********" << endl;
	vector<vector<int> >  newGlobalClusters = globalClusters;
	int NumberOfExistingCluster = globalClusters.size();
	/////////// 1.  AssignToCluster  ////////////////////
	outfile << " 1.AssignToCluster " << endl;
	if (adddbsize != 0) {
		outfile << "****addsize!=0****" << endl;
		if (globalClusters.size() != globalClustersFeatureVector.size()) cout << "Error!!!" << endl;
		if (globalClusters.size() != globalNewOrOldClusters.size()) cout << "Error!!!" << endl;
		vector<FeatureVector> globalClustersFeatureVectorCenter;
		globalClustersFeatureVectorCenter.resize(NumberOfExistingCluster);
		for (int cluster = 0; cluster < NumberOfExistingCluster; cluster++) {

			vector<FeatureVector> fvinthiscluster = globalClustersFeatureVector[cluster];
			int sizeofgraphsincluster = fvinthiscluster.size();
			int featurevectorlen = fvinthiscluster[0].getSize();

			// for each cluster, calculate its center feature vector
			FeatureVector acenter(featurevectorlen);
			for (int i = 0; i < sizeofgraphsincluster; i++) {
				FeatureVector  fv = fvinthiscluster[i];
				for (int j = 0; j < featurevectorlen; j++) {
					acenter.feature[j] += fv.feature[j];
				}
			}
			for (int j = 0; j < featurevectorlen; j++) {
				acenter.feature[j] = acenter.feature[j] * 1.0 / sizeofgraphsincluster;
			}
			globalClustersFeatureVectorCenter[cluster] = acenter;
		}

		FILE* input = fopen(addgraphfromdbname.c_str(), "r");
		//vector<graphClosure> addedGraphs;
		// 1. Redistribute clusters
		for (int batch = 0; batch < (initialdbsize + adddbsize); batch++) {
			if (batch < initialsizeofgraph) {
				readFromFile(input, batch);
				continue;
			}
			graphClosure g = readFromFile(input, batch);
			this->globalMapgraphs.insert(pair<int, graphClosure>(batch, g));
			//	addedGraphs.push_back(g);
			FeatureVector featurev(frequentsubtree.size());
			// Create callback to print mappings
			for (int i = 0; i < frequentsubtree.size(); i++) {
				if (num_vertices(g) < num_vertices(frequentsubtree[i]) || num_edges(g) < num_edges(frequentsubtree[i]))
					continue;
				bool testFlag = false;
				deletePattern_callback<graphClosure> user_callback(testFlag);
				vf2_subgraph_mono(frequentsubtree[i], g, user_callback,
					get(vertex_index, frequentsubtree[i]), get(vertex_index, g),
					vertex_order_by_mult(frequentsubtree[i]),
					always_equivalent(),
					make_property_map_equivalent(get(vertex_name, frequentsubtree[i]), get(vertex_name, g)));
				if (testFlag) {
					featurev.feature[i] = 1;
				}
			}
			//////////////update newfeaturevec///////////////////////////
			//newfeaturevec.push_back(featurev);
			double mindis = 10000000;
			int    ind = 0;
			for (int k = 0; k < NumberOfExistingCluster; k++) {
				double sum = 0;
				for (int k2 = 0; k2 < frequentsubtree.size(); k2++) {
					//sum += (centers[k][k2] - featurev.feature[k2]) * (centers[k][k2] - featurev.feature[k2]);
					sum += abs(globalClustersFeatureVectorCenter[k].feature[k2] - featurev.feature[k2]);
				}
				if (sum < mindis) {
					mindis = sum;
					ind = k;
				}
			}
			/////////////////update newGlobalClusters///////////////////////
			newGlobalClusters[ind].push_back(batch);
		}
	}

	/////////// 2.  RemoveFromCluster  ////////////////////
	outfile << " 2.RemoveFromCluster " << endl;
	if (removedbsize != 0) {
		outfile << "****removedbsize!=0****" << endl;
		if (globalClusters.size() != globalClustersFeatureVector.size()) cout << "Error!!!" << endl;
		if (globalClusters.size() != globalNewOrOldClusters.size()) cout << "Error!!!" << endl;
		vector<vector<int> >  newGlobalClusters2;
		vector<int> deletedGraphs;
		for (int batch = 0; batch < (initialdbsize + adddbsize); batch++) {
			if (batch < (initialdbsize - removedbsize) || batch >= initialdbsize) continue;
			deletedGraphs.push_back(batch);
		}
		for (int i = 0; i < newGlobalClusters.size(); i++) {
			vector<int> acluster2;
			vector<int> acluster = newGlobalClusters[i];
			for (int j = 0; j < acluster.size(); j++) {
				int graphid = acluster[j];
				vector<int>::iterator it = find(deletedGraphs.begin(), deletedGraphs.end(), graphid);
				if (it == deletedGraphs.end()) {
					acluster2.push_back(graphid);
				}
			}
			newGlobalClusters2.push_back(acluster2);
		}
		vector<vector<int> >().swap(newGlobalClusters);
		newGlobalClusters = newGlobalClusters2;
	}
	////////////////////////Update  Cluster  Flag/////////////////////////
	outfile << "NumberOfExistingCluster:" << NumberOfExistingCluster << endl;
	for (int cluster = 0; cluster < NumberOfExistingCluster; cluster++) {
		vector<int> graphIdInOldCluster = globalClusters[cluster];
		vector<int> graphIdInNewCluster = newGlobalClusters[cluster];
		outfile << "******cluster:" << cluster << ",graphIdInOldCluster.size():" << graphIdInOldCluster.size() << ",graphIdInNewCluster.size():" << graphIdInNewCluster.size() << endl;
		if (graphIdInOldCluster.size() == graphIdInNewCluster.size()) {
			globalNewOrOldClusters[cluster] = IsAOldCluster;
		}
		else {
			globalNewOrOldClusters[cluster] = IsANewCluster;
		}
	}
	maintainClusterForMajorModification(globalNewOrOldClusters, newGlobalClusters, outfile);

}

vector<int> SmallGraphClustering::graph2GraphletVec(vector<int>& graphletvecAfterUpdated, int adddbsize, int  removedbsize, int initialdbsize, string graphdbname, string graphletdbname, ofstream& outfile) {
	vector<int> ans;
	/// 0. read graphlet 
	vector<graphClosure> graphletvec;
	FILE* in = fopen(graphletdbname.c_str(), "r");
	for (int i = 0; i < numberofgraphlets; i++) {
		graphClosure tempgraph = readFromFile(in, i);
		graphletvec.push_back(tempgraph);
		ans.push_back(0);
		graphletvecAfterUpdated.push_back(0);
	}
	fclose(in);
	/// 1. read and represent each graph 
	FILE* input = fopen(graphdbname.c_str(), "r");
	for (int i = 0; i < initialdbsize + adddbsize; i++) {
		graphClosure tempgraph = readFromFile(input, i);
		for (int j = 0; j < graphletvec.size(); j++) {
			graphClosure glet = graphletvec[j];
			bool testFlag = false;
			deletePattern_callback<graphClosure> user_callback(testFlag);
			vf2_subgraph_mono(glet, tempgraph, user_callback,
				get(vertex_index, glet), get(vertex_index, tempgraph),
				vertex_order_by_mult(glet),
				always_equivalent(),
				always_equivalent());
			if (testFlag) {
				if (i < initialdbsize - removedbsize) {
					ans[j] = ans[j] + 1;
					graphletvecAfterUpdated[j] = graphletvecAfterUpdated[j] + 1;
				}
				else if (i < initialdbsize) {
					ans[j] = ans[j] + 1;
					//graphletvecAfterUpdated[j] = graphletvecAfterUpdated[j] - 1;
				}
				else {
					graphletvecAfterUpdated[j] = graphletvecAfterUpdated[j] + 1;
				}
			}
		}
	}
	fclose(input);

	for (int i = 0; i < ans.size(); i++) {
		outfile << ans[i] << " ";
	}
	outfile << endl;
	for (int i = 0; i < graphletvecAfterUpdated.size(); i++) {
		outfile << graphletvecAfterUpdated[i] << " ";
	}
	outfile << endl;

	return ans;
}


double SmallGraphClustering::cosine_similarity(const vector<double>& v1, const vector<double>& v2) {
	double mul = 0.0;
	double d_a = 0.0;
	double d_b = 0.0;
	auto B_iter = v2.begin();
	auto A_iter = v1.begin();
	for (; A_iter != v1.end(); A_iter++, B_iter++)
	{
		mul += *A_iter * *B_iter;
		d_a += *A_iter * *A_iter;
		d_b += *B_iter * *B_iter;
	}
	return mul / (sqrt(d_a) * sqrt(d_b));
}
void SmallGraphClustering::maintainClusterForMajorModification(vector<int>& NewOrOldVector, vector<vector<int>>& newclusters, ofstream& logfile) {
	logfile << "***maintainClusterForModerateModification ***" << endl;
	ofstream  clusterfile(updateclustername);
	ofstream  AuxInfofile(updateclusterAuxInfo);
	int totalSum = 0;
	double zscore = 1.96;   //interval = 0.95
	double wide = 0.06;
	int sampleSize = (int)((zscore * zscore * 0.5 * 0.5) / ((wide / 2) * (wide / 2)) + 0.5);
	for (int i = 0; i < newclusters.size(); i++) {
		//////////////////////////////////////////////////////////
		if (NewOrOldVector[i] == 0) continue;

		if (newclusters[i].size() <= this->clustersize) continue;
		totalSum += newclusters[i].size();
	}
	if (totalSum < sampleSize) {
		logfile << "error:totalSum:" << totalSum << " sampleSize:" << sampleSize << endl;
		sampleSize = totalSum;
	}
	set<int> TotalgraphIdList;
	vector<set<int> > sampledclusters;
	for (int i = 0; i < newclusters.size(); i++) {
		//////////////////////////////////////////////////////////
		if (NewOrOldVector[i] == 0) continue;
		set<int> ACluster;
		if (newclusters[i].size() <= 0) continue;
		vector<int> temp;
		for (int j = 0; j < newclusters[i].size(); j++) {
			temp.push_back(newclusters[i][j]);
		}
		if (temp.size() > this->clustersize) {
			random_shuffle(temp.begin(), temp.end());
			int sample_size = (int)((double)sampleSize / (double)totalSum * temp.size());
			if (temp.size() < sample_size) logfile << "error:temp.size() < sample_size" << endl;
			if (sample_size < 2)  sample_size = 2;
			for (int i = 0; i < sample_size; i++) {
				TotalgraphIdList.insert(temp[i]);
				ACluster.insert(temp[i]);
			}
		}
		else {
			for (int i = 0; i < temp.size(); i++) {
				TotalgraphIdList.insert(temp[i]);
				ACluster.insert(temp[i]);
			}
		}
		sampledclusters.push_back(ACluster);
	}
	for (int i = 0; i < sampledclusters.size(); i++) {
		set<int> graphIdList = sampledclusters[i];
		if (graphIdList.size() < 2) {
			//std::cout << "graphIdList.size()< 2" << endl;
			continue;
		}
		else if (graphIdList.size() <= this->clustersize) {
			//AuxInfofile << NewOrOldVector[i] << endl;
			set<int>::iterator it;
			for (it = graphIdList.begin(); it != graphIdList.end(); it++) {
				clusterfile << *it << "\t";
			}
			clusterfile << endl;
			graphClosure  newClosure = getClosure(graphIdList);
			evaluateClosure(newClosure, graphIdList, clusterfile);
		}
		else {
			finepartition(graphIdList, clusterfile);
		}
	}
}

void SmallGraphClustering::finepartition(std::set<int>& graphIdList, ofstream& oStream) {
	graphClosure newClosure;
	vector<std::tuple<graphClosure, int> > patternSet;
	if (graphIdList.size() < 2)
	{
		// do nothing
	}
	else if (graphIdList.size() <= this->clustersize) {
		// output cluster to file
		set<int>::iterator it;
		for (it = graphIdList.begin(); it != graphIdList.end(); it++) {
			oStream << *it << "\t";
		}
		oStream << endl;
		newClosure = getClosure(graphIdList);
		evaluateClosure(newClosure, graphIdList, oStream);
	}
	else {
		set<int> graphPartionList1, graphPartionList2;
		divideIn2Parts(graphIdList, graphPartionList1, graphPartionList2);
		finepartition(graphPartionList1, oStream);
		finepartition(graphPartionList2, oStream);
	}
}
double SmallGraphClustering::minkowsky(const vector<double>& v1, const vector<double>& v2, double m)
{
	assert(v1.size() == v2.size());
	double ret = 0.0;
	if (m < 0) {
		double max = 0;
		for (vector<double>::size_type i = 0; i != v1.size(); ++i)
		{
			// cout << v1[i] << " " << v2[i] << " "<< abs(v1[i] - v2[i]) << endl;

			if (abs(v1[i] - v2[i]) > max) {
				max = abs(v1[i] - v2[i]);
			}
		}
		//  cout << "max:" << max << endl;
		return max;
	}
	for (vector<double>::size_type i = 0; i != v1.size(); ++i)
	{
		double ans = abs(v1[i] - v2[i]);
		//	cout << v1[i] << "," << v2[i] << ",ans:" << ans << ",m:" << m << endl;
		ret += pow(ans, m);
	}
	//	cout << "ret:" << ret << ",m:"<<m<<"," << pow(ret, 1.0 / m) << endl;
	return pow(ret, 1.0 / m);
}
double SmallGraphClustering::euclidean(const vector<double>& v1, const vector<double>& v2)
{
	assert(v1.size() == v2.size());
	return minkowsky(v1, v2, 2.0);
}

void SmallGraphClustering::finepartition2(std::set<int>& graphIdList, ofstream& oStream, ofstream& oStream2, ofstream& AuxInfofile, int neworold) {
	graphClosure newClosure;
	vector<std::tuple<graphClosure, int> > patternSet;
	if (graphIdList.size() < 2)
	{
		//AuxInfofile << neworold << endl;
		//set<int>::iterator it;
		//for (it = graphIdList.begin(); it != graphIdList.end(); it++) {
		//	oStream2 << *it << "\t";
		//}
		//oStream2 << endl;
	}
	else if (graphIdList.size() <= this->clustersize) {
		// output cluster to file
		AuxInfofile << neworold << endl;
		set<int>::iterator it;
		for (it = graphIdList.begin(); it != graphIdList.end(); it++) {
			oStream2 << *it << "\t";
		}
		oStream2 << endl;
		newClosure = getClosure(graphIdList);
		evaluateClosure(newClosure, graphIdList, oStream2);
	}
	else {
		set<int> graphPartionList1, graphPartionList2;
		divideIn2Parts(graphIdList, graphPartionList1, graphPartionList2);
		finepartition2(graphPartionList1, oStream, oStream2, AuxInfofile, neworold);
		finepartition2(graphPartionList2, oStream, oStream2, AuxInfofile, neworold);
	}
}


void SmallGraphClustering::finepartitionWithClusterFlag(vector<FeatureVector>& featurevec, std::set<int>& graphIdList, ofstream& oStream, ofstream& oStream2, ofstream& AuxInfofile, int neworold) {
	graphClosure newClosure;
	vector<std::tuple<graphClosure, int> > patternSet;
	if (graphIdList.size() < 2)
	{
		//AuxInfofile << neworold << endl;
		//set<int>::iterator it;
		//for (it = graphIdList.begin(); it != graphIdList.end(); it++) {
		//	oStream2 << *it << "\t";
		//}
		//oStream2 << endl;
	}
	else if (graphIdList.size() <= this->clustersize) {
		// output cluster to file
		AuxInfofile << neworold << endl;
		globalNewOrOldClusters.push_back(neworold);
		set<int>::iterator it;
		vector<int> graphinacluster;
		vector<FeatureVector> featurevectorincluster;
		for (it = graphIdList.begin(); it != graphIdList.end(); it++) {
			oStream2 << *it << "\t";
			graphinacluster.push_back(*it);
			featurevectorincluster.push_back(featurevec[*it]);
		}
		oStream2 << endl;
		globalClusters.push_back(graphinacluster);
		globalClustersFeatureVector.push_back(featurevectorincluster);

		newClosure = getClosure(graphIdList);
		evaluateClosure(newClosure, graphIdList, oStream2);
	}
	else {
		//cout << "!!!!graphIdList.size() :" << graphIdList.size() << endl;
		//for (set<int>::iterator it_1 = graphIdList.begin(); it_1 != graphIdList.end(); it_1++) {
		//	cout << *it_1 << ",";
		//}
		//cout << endl;
		set<int> graphPartionList1, graphPartionList2;
		divideIn2Parts(graphIdList, graphPartionList1, graphPartionList2);
		//cout << "****graphIdList.size() :" << graphIdList.size() << endl;
		finepartitionWithClusterFlag(featurevec, graphPartionList1, oStream, oStream2, AuxInfofile, neworold);
		finepartitionWithClusterFlag(featurevec, graphPartionList2, oStream, oStream2, AuxInfofile, neworold);
	}
}
void SmallGraphClustering::divideIn2Parts(std::set<int>& graphPartionList, std::set<int>& inList1,
	std::set<int>& inList2) {
	int size = lexical_cast<int>(graphPartionList.size());
	//cout << "size: " << size << endl;
	int idex_r = rand() % (size);
	set<int>::iterator it1 = graphPartionList.begin();
	std::advance(it1, idex_r);
	int idex = lexical_cast<int>(*it1);
	graphClosure g1 = findaGraphFromMap(idex);
	set<int>::iterator it_1;
	int idx2 = 0;
	double minsim = 1000.0;
	vector<double> tempScore;
	for (it_1 = graphPartionList.begin(); it_1 != graphPartionList.end(); it_1++) {
		double sim1;
		sim1 = getSimScoreMCCS(findaGraphFromMap(idex), findaGraphFromMap(*it_1));
		tempScore.push_back(sim1);
		if (sim1 < minsim) {
			idx2 = *it_1;
			minsim = sim1;
		}
	}
	graphClosure g2 = findaGraphFromMap(idx2);
	int count = 0;
	for (it_1 = graphPartionList.begin(); it_1 != graphPartionList.end(); it_1++) {
		double simS1, siS2;
		simS1 = tempScore[count];
		count++;
		siS2 = getSimScoreMCCS(findaGraphFromMap(idx2), findaGraphFromMap(*it_1));
		if (simS1 > siS2) {
			inList1.insert(*it_1);
		}
		else if (siS2 - simS1 <= 0.001) {
			if (inList1.size() > inList2.size()) inList2.insert(*it_1);
			else inList1.insert(*it_1);
		}
		else {
			inList2.insert(*it_1);
		}
	}
	//std::cout << "finishi partition : part1 size:" << inList1.size() << "  part2 size: " << inList2.size() << endl;
	vector <double>().swap(tempScore);
}
double SmallGraphClustering::getSimScoreMCCS(graphClosure& g1, graphClosure& g2) {
//	cout << "hhaaa" << endl;
	int edgeq = num_edges(g1);
//	cout << edgeq << ",";
	int edgeg = num_edges(g2);
//	cout  << edgeg << endl;
	int minedge = edgeq;
	if (edgeq > edgeg) {
		minedge = edgeg;
	}
	/*
	int graphsize = minedge;
	int edgemccs = rand() % (graphsize);
	*/
	
	int graphsize = 0;
	example_callback<graphClosure> user_callback(g1, graphsize);
	vertex_nam vname_map_simple1 = get(vertex_name, g1);
	vertex_nam vname_map_simple2 = get(vertex_name, g2);
	mcgregor_common_subgraphs
	(g1, g2, true, user_callback,
		vertices_equivalent(make_property_map_equivalent(vname_map_simple1, vname_map_simple2)));
	int edgemccs = graphsize;
	
	if (minedge == 0) return 0;
	double answer = ((double)edgemccs) / minedge;
	//std::cout << "minedge:" << minedge << " edgemccs:" << edgemccs << " answer:" << answer << endl;
	return answer;
}
void SmallGraphClustering::evaluateClosure(graphClosure newClosure, set<int>& graphIdList, ofstream& oStream) {
	float ratio1 = 0.4f;
	int threshold1 = (int)(graphIdList.size() * ratio1);
	if (threshold1 < 2)
		threshold1 = 2;
	float ratio2 = 0.5f;
	int threshold2 = (int)(graphIdList.size() * ratio2);
	if (threshold2 < 2)
		threshold2 = 2;
	float ratio3 = 0.6f;
	int threshold3 = (int)(graphIdList.size() * ratio3);
	if (threshold3 < 2)
		threshold3 = 2;
	ed_it ed_it1, ed_it2;
	tie(ed_it1, ed_it2) = edges(newClosure);
	edgecontainList edgeConMap = get(edge_containlist, newClosure);
	double totalEdges = 0;
	double mapedEdges1 = 0;
	double mapedEdges2 = 0;
	double mapedEdges3 = 0;
	edge_ind newEds = get(edge_index, newClosure);
	for (; ed_it1 != ed_it2; ed_it1++) {
		int containidlist = lexical_cast<int>(edgeConMap[*ed_it1].size());
		totalEdges += containidlist;
		if (containidlist >= threshold1)
			mapedEdges1++;
		if (containidlist >= threshold2)
			mapedEdges2++;
		if (containidlist >= threshold3)
			mapedEdges3++;
	}

	oStream << "maped0.4f:" << mapedEdges1 << " maped0.5f:" << mapedEdges2 << " maped0.6f:" << mapedEdges3
		<< " totalEdges:" << totalEdges << " edgeclosure:" << num_edges(newClosure) << endl;

}
graphClosure SmallGraphClustering::findaGraphFromMap(int graphid) {
	map<int, graphClosure>::iterator iter = this->globalMapgraphs.find(graphid);
	if (iter != this->globalMapgraphs.end())
	{
		return iter->second;
	}
	else {
		cout << "can not find graph " << graphid << "in globalMapgraphs" << endl;
		return NULL;
	}

}
void SmallGraphClustering::getWeightMatrix(graphClosure& g1, graphClosure& g2, vector< vector<double> >& W) {
	ve_it it, it_end;
	tie(it, it_end) = vertices(g1);
	ve_it it_2, it_end_2;
	vertex_ind vertex_index_g1 = get(vertex_index, g1);
	vertex_ind vertex_index_g2 = get(vertex_index, g2);
	typedef property_map<graphClosure, vertex_name_t>::type vertex_nam1;
	vertex_nam1 vertex_name_g1 = get(vertex_name, g1);
	vertex_nam1 vertex_name_g2 = get(vertex_name, g2);
	for (; it != it_end; it++) {
		tie(it_2, it_end_2) = vertices(g2);
		for (; it_2 != it_end_2; it_2++) {
			string lable_g1 = vertex_name_g1[*it];
			string lable_g2 = vertex_name_g2[*it_2];
			int index_g1 = vertex_index_g1[*it];
			int index_g2 = vertex_index_g2[*it_2];
			if (lable_g1.compare(lable_g2) != 0) {
				W[index_g1][index_g2] = 0;
				continue;
			}
			double intersection = 0;
			double unionNum = 0;
			adj_it new_it, new_it2, new_new_it, new_new_it2;
			ve_it it_test = it;
			tie(new_new_it, new_new_it2) = adjacent_vertices(*it_test, g1);
			adj_it new_it3, new_it4, new_new_it3, new_new_it4;
			tie(new_new_it3, new_new_it4) = adjacent_vertices(*it_2, g2);

			//////////////////////////////
			set<string> vertexLocLab;
			set<string>::iterator it1;
			vertexLocLab.clear();
			for (new_it = new_new_it, new_it2 = new_new_it2; new_it != new_it2; new_it++) {
				vertexLocLab.insert(vertex_name_g1[*new_it]);
			}
			for (new_it3 = new_new_it3, new_it4 = new_new_it4; new_it3 != new_it4; new_it3++) {
				vertexLocLab.insert(vertex_name_g2[*new_it3]);
			}
			////////////////////////////
			for (it1 = vertexLocLab.begin(); it1 != vertexLocLab.end(); it1++) {
				int cnt1 = 0, cnt2 = 0;
				for (new_it = new_new_it, new_it2 = new_new_it2; new_it != new_it2; new_it++) {
					string tempString = vertex_name_g1[*new_it];
					if (tempString.compare(*it1) == 0)
						cnt1++;
				}
				for (new_it3 = new_new_it3, new_it4 = new_new_it4; new_it3 != new_it4; new_it3++) {
					string tempString1 = vertex_name_g2[*new_it3];
					if (tempString1.compare(*it1) == 0)
						cnt2++;
				}
				intersection = intersection + min(cnt1, cnt2);
				unionNum = unionNum + max(cnt1, cnt2);
			}
			if (unionNum == 0) {
				W[index_g1][index_g2] = 1;
			}
			else {
				intersection = intersection + 0.0;
				//W[index_g1][index_g2] = 1 + intersection*intersection / unionNum;
				W[index_g1][index_g2] = 1 + intersection / unionNum;
			}
		}
	}
}

graphClosure SmallGraphClustering::getClosure2(graphClosure& g1, graphClosure& g2) {
	typedef std::tuple< std::tuple< ve_it, ve_it >, double> querMem;
	std::priority_queue< querMem, std::vector<querMem>, mycoparison< querMem> > pq;
	vertex_nam ve_name_g1 = get(vertex_name, g1);
	vertex_nam ve_name_g2 = get(vertex_name, g2);
	vertex_mat ve_match_g1 = get(vertex_matched, g1);
	vertex_mat ve_match_g2 = get(vertex_matched, g2);
	vertices_size g1_size = num_vertices(g1);
	vertices_size g2_size = num_vertices(g2);
	vector< vector <double> > W;
	W.resize(g1_size);
	for (int i = 0; i < lexical_cast<int>(g1_size); i++)
		W[i].resize(lexical_cast<int>(g2_size));
	getWeightMatrix(g1, g2, W);
	ve_it g1_it_begin, g1_it_end, g1_it;
	ve_it g2_it_begin, g2_it_end, g2_it;
	tie(g1_it_begin, g1_it_end) = vertices(g1);
	vertex_ind g1_index, g2_index;
	g1_index = get(vertex_index, g1);
	g2_index = get(vertex_index, g2);
	map<ve_it, ve_it> map1;
	map<ve_it, ve_it> map2;
	map<ve_it, ve_it> mate;
	map<ve_it, double> maxW;
	for (g1_it = g1_it_begin; g1_it != g1_it_end; g1_it++) {
		vector<double> tempvector = W[(g1_index[*g1_it])];
		vector<double>::iterator temp_it_vec1 = max_element(tempvector.begin(), tempvector.end());
		int Vm_g2 = lexical_cast<int>(temp_it_vec1 - tempvector.begin());
		tie(g2_it_begin, g2_it_end) = vertices(g2);
		for (; g2_it_begin != g2_it_end;
			g2_it_begin++) {
			if (g2_index[*g2_it_begin] == Vm_g2)
				break;
		}
		mate[g1_it] = g2_it_begin;
		maxW[g1_it] = (*temp_it_vec1);
		if (*temp_it_vec1 > 0) {
			pq.push(make_tuple(make_tuple(g1_it, g2_it_begin), *temp_it_vec1));
		}
	}
	while (!pq.empty()) {
		typedef std::tuple<ve_it, ve_it> ve_pair;
		ve_pair newVePair = get<0>(pq.top());
		pq.pop();
		if (ve_match_g1[*get<0>(newVePair)])
			continue;
		if (ve_match_g2[*get<1>(newVePair)]) {
			vector<double> tempvector1 = W[(g1_index[*get<0>(newVePair)])];
			vector<double> newWeght;
			vector<int> index_temp;
			ve_it tempIt_g1;
			tie(g2_it_begin, g2_it_end) = vertices(g2);
			for (tempIt_g1 = g2_it_begin; g2_it_begin != g2_it_end;
				g2_it_begin++) {
				if (!ve_match_g2[*g2_it_begin]) {
					newWeght.push_back(tempvector1[g2_it_begin - tempIt_g1]);
					index_temp.push_back(g2_it_begin - tempIt_g1);
				}
			}
			if (newWeght.empty())
				continue;
			vector<double>::iterator temp_it_vec1 = max_element(newWeght.begin(),
				newWeght.end());
			int Vm_g2 = index_temp[temp_it_vec1 - newWeght.begin()];
			tie(g2_it_begin, g2_it_end) = vertices(g2);
			for (; g2_it_begin != g2_it_end;
				g2_it_begin++) {
				if (g2_index[*g2_it_begin] == Vm_g2)
					break;
			}
			mate[get<0>(newVePair)] = g2_it_begin;
			maxW[get<0>(newVePair)] = (*temp_it_vec1);
			if (*temp_it_vec1 > 0) {
				pq.push(make_tuple(make_tuple(get<0>(newVePair), g2_it_begin), *temp_it_vec1));
			}
			continue;
		}
		ve_match_g1[*get<0>(newVePair)] = true;
		map1[get<0>(newVePair)] = get<1>(newVePair);
		ve_match_g2[*get<1>(newVePair)] = true;

		map2[get<1>(newVePair)] = get<0>(newVePair);
		adj_it  neighb_g1_begin, neighb_g1_end, neighb_g2_begin, neighb_g2_end;


		tie(neighb_g1_begin, neighb_g1_end) = adjacent_vertices(*get<0>(newVePair), g1);
		for (; neighb_g1_begin != neighb_g1_end; neighb_g1_begin++) {
			tie(neighb_g2_begin, neighb_g2_end) = adjacent_vertices(*get<1>(newVePair), g2);

			for (; neighb_g2_begin != neighb_g2_end; neighb_g2_begin++) {
				if ((!ve_match_g1[*neighb_g1_begin]) && !(ve_match_g2[*neighb_g2_begin]))
				{
					if (ve_name_g1[*neighb_g1_begin] == ve_name_g2[*neighb_g2_begin]) {
						int newg1Index = g1_index[*neighb_g1_begin];
						int newg2Index = g2_index[*neighb_g2_begin];
						ve_it it1, it2;
						ve_it g1_ve, g2_ve;
						tie(it1, it2) = vertices(g1);
						for (; it1 != it2; it1++) {
							if (g1_index[*it1] == newg1Index) {
								g1_ve = it1;
								break;
							}
						}

						tie(it1, it2) = vertices(g2);
						for (; it1 != it2; it1++) {
							if (g2_index[*it1] == newg2Index) {
								g2_ve = it1;
								break;
							}
						}

						W[newg1Index][newg2Index] = W[newg1Index][newg2Index] + this->bonus_weight;
						if (W[newg1Index][newg2Index] > maxW[g1_ve]) {
							mate[g1_ve] = g2_ve;
							map<ve_it, double>::iterator it_begin1;

							if ((it_begin1 = maxW.find(g1_ve)) != maxW.end()) {

								if (maxW[g1_ve] != W[newg1Index][newg2Index]) {
									maxW[g1_ve] = W[newg1Index][newg2Index];
									pq.push(make_tuple(make_tuple(g1_ve, g2_ve), maxW[g1_ve]));
								}
							}
							else {
								maxW[g1_ve] = W[newg1Index][newg2Index];
								pq.push(make_tuple(make_tuple(g1_ve, g2_ve), maxW[g1_ve]));

							}

						}
					}
				}
			}
		}

	}
	/*
	map<ve_it, ve_it>::iterator it1;
	for(it1= map1.begin(); it1!= map1.end(); it1++){
	std::cout << "index1 : " << g1_index[*(it1->first)] << " maps index2 " << g2_index[*(it1->second)] << endl;
	}
	std::cout << "maps end"<< endl;
	*/
	graphClosure newclosure = generateClosure(g1, g2, map1, map2);
	ve_it cle_it1, cle_it2;
	ve_match_g1 = get(vertex_matched, newclosure);
	tie(cle_it1, cle_it2) = vertices(newclosure);
	for (; cle_it1 != cle_it2; cle_it1++) {
		ve_match_g1[*cle_it1] = false;
	}
	/*
	print_graph(newclosure);
	ed_it ed_it1, ed_it2;
	tie(ed_it1, ed_it2) = edges(newclosure);
	edgecontainList edgeConMap = get(edge_containlist, newclosure);
	double simEdges = 0;
	edge_ind newEds = get(edge_index, newclosure);
	for (; ed_it1 != ed_it2; ed_it1++){
	std::cout << lexical_cast<int>(edgeConMap[*ed_it1].size()) << endl;
	set<int>::iterator it;
	for (it = edgeConMap[*ed_it1].begin(); it != edgeConMap[*ed_it1].end(); it++)
	std::cout << *it <<" ";
	std::cout << endl;
	}
	*/
	return newclosure;
}
graphClosure SmallGraphClustering::generateClosure(graphClosure& g1, graphClosure& g2, std::map<ve_it, ve_it>& map1, std::map<ve_it, ve_it>& map2) {

	vertex_nam vertex_name_g1, vertex_name_g2;
	vertex_name_g1 = get(vertex_name, g1);
	vertex_name_g2 = get(vertex_name, g2);
	vertex_ind vertex_index_g2 = get(vertex_index, g2);
	vertex_ind vertex_index_g1 = get(vertex_index, g1);
	containList vertex_contain_g1, vertex_contain_g2;
	edgecontainList edge_contain_g1, edge_contain_g2;
	vertex_contain_g1 = get(vertex_containlist, g1);
	vertex_contain_g2 = get(vertex_containlist, g2);
	edge_contain_g1 = get(edge_containlist, g1);
	edge_contain_g2 = get(edge_containlist, g2);
	ve_it ve_begin_g1, ve_end_g1, ve_begin_g2, ve_end_g2;
	tie(ve_begin_g1, ve_end_g1) = vertices(g1);
	tie(ve_begin_g2, ve_end_g2) = vertices(g2);
	for (; ve_begin_g2 != ve_end_g2; ve_begin_g2++) {
		if (map2.find(ve_begin_g2) == map2.end()) {
			//not find map
			vertex_t newVp = add_vertex(g1);
			ve_it ve_temp1, ve_temp2;
			tie(ve_temp1, ve_temp2) = vertices(g1);
			int newindex1 = vertex_index_g1[newVp];
			for (; ve_temp1 != ve_temp2; ve_temp1++) {
				if (vertex_index_g1[*ve_temp1] == newindex1) {
					map2[ve_begin_g2] = ve_temp1;
					break;
				}
			}
			vertex_contain_g1[newVp].insert(vertex_contain_g2[*ve_begin_g2].begin(), vertex_contain_g2[*ve_begin_g2].end());
			vertex_name_g1[newVp] = vertex_name_g2[*ve_begin_g2];
		}
		else {
			map<ve_it, ve_it>::iterator ite1, ite2;
			ite1 = map2.find(ve_begin_g2);
			ve_it u = ite1->second;
			ve_it v = ite1->first;
			/*
			set<int>::iterator it;
			for (it = vertex_contain_g2[*v].begin(); it != vertex_contain_g2[*v].end(); it++){
			std::cout << *it << " ";
			}
			std::cout << "###########"<<endl;
			set<int>::iterator it2;
			for (it2 = vertex_contain_g1[*u].begin(); it2 != vertex_contain_g1[*u].end(); it2++){
			std::cout << *it2 << " ";
			}
			std::cout << "###########" << endl;
			*/
			(vertex_contain_g1[*u]).insert(vertex_contain_g2[*v].begin(),
				vertex_contain_g2[*v].end());
		}
	}
	ed_it itera_edg1, itera_edg2;
	tie(itera_edg1, itera_edg2) = edges(g2);
	for (; itera_edg1 != itera_edg2; itera_edg1++) {
		ve_it uu, vv;
		vertex_t  u = source(*itera_edg1, g2);
		vertex_t v = target(*itera_edg1, g2);
		ve_it it1, it2;
		tie(it1, it2) = vertices(g2);
		for (; it1 != it2; it1++) {
			if (vertex_index_g2[*it1] == vertex_index_g2[u]) {
				uu = map2[it1];
			}
			if (vertex_index_g2[*it1] == vertex_index_g2[v]) {
				vv = map2[it1];
			}
		}
		edge_t newEdge;
		bool testFlag;
		tie(newEdge, testFlag) = edge(*uu, *vv, g1);
		if (testFlag) {
			/*
			set<int>::iterator it;
			for (it = edge_contain_g2[*itera_edg1].begin(); it != edge_contain_g2[*itera_edg1].end(); it++){
			std::cout << *it << " ";
			}
			*/
			(edge_contain_g1[newEdge]).insert(edge_contain_g2[*itera_edg1].begin(), edge_contain_g2[*itera_edg1].end());
		}
		else {
			tie(newEdge, testFlag) = add_edge(*uu, *vv, g1);
			edge_contain_g1[newEdge] = edge_contain_g2[*itera_edg1];
		}
	}
	return g1;
}
graphClosure SmallGraphClustering::getClosure(std::set<int>& graphIdList) {
	graphClosure returnGraph;
	if (graphIdList.empty()) {
		return returnGraph;
	}
	returnGraph = findaGraphFromMap(*graphIdList.begin());
	set<int>::iterator new_it1;
	new_it1 = graphIdList.begin();
	for (new_it1++; new_it1 != graphIdList.end(); new_it1++) {
		returnGraph = getClosure2(findaGraphFromMap(*new_it1), returnGraph);
	}
	return returnGraph;

}

SmallGraphClustering::SmallGraphClustering()
{
	cout << "***StartSmallGraphClustering ***" << endl;
}


SmallGraphClustering::~SmallGraphClustering()
{
	cout << "***End SmallGraphClustering***" << endl;
}

void SmallGraphClustering::readGraphsFromFile(FILE* input, int start, int end)
{
	for (int i = start; i < end; i++) {
		this->globalMapgraphs.insert(pair<int, graphClosure>(i, readFromFile(input, i)));
	}
}
char SmallGraphClustering::readcommand(FILE* file) {

	char car = fgetc(file);
	while (car < 'a' || car > 'z') {
		if (feof(file))
			return -1;
		car = fgetc(file);
	}
	return car;

}
int SmallGraphClustering::readInt(FILE* input) {

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
int SmallGraphClustering::transGraph2ft_fct4Cluster(string outfilename, vector<int>& graphids) {
	int totalsubtree = 0;
	ofstream outfile(outfilename.c_str());
	//cout << "reslut.size():" << endl;
	for (int i = 0; i < graphids.size(); i++) {
		vector<EnumerateISE::ISEtree> reslut;
		int graphid = graphids[i];
		graphClosure tempgraph = findaGraphFromMap(graphid);
		reslut = EnumerateISE::ISE2File(tempgraph);
		//totalsubtree += reslut.size();
		//cout << reslut.size() << ",";
		for (int k = 0; k < reslut.size(); k++) {
			// for each returned ISEtree
			if (reslut[k].tree.size() < 2) continue;
			EnumerateISE::ISEtree2File_fct(i, reslut[k], outfile, tempgraph);
			totalsubtree++;
		}
	}
	//cout << endl;
	outfile.close();
	return totalsubtree;
}

int SmallGraphClustering::transGraph2ft_fct(string infilename, string outfilename, int start1, int end1, int start2, int end2)
{
	string filename = outfilename;
	int totalsubtree = 0;
	FILE* input = fopen(infilename.c_str(), "r");
	ofstream outfile(filename.c_str());
	for (int i = 0; i < end2; i++) {
		if ((i >= start1 && i < end1) || (i >= start2 && i < end2)) {
			vector<EnumerateISE::ISEtree> reslut;
			graphClosure tempgraph = readFromFile(input, i);
			reslut = EnumerateISE::ISE2File(tempgraph);
			//totalsubtree += reslut.size();
			for (int k = 0; k < reslut.size(); k++) {
				if (reslut[k].tree.size() < 2) continue;
				// for each returned ISEtree
				EnumerateISE::ISEtree2File_fct(i, reslut[k], outfile, tempgraph);
				totalsubtree++;
			}
			//cout << i << endl;
		}

	}
	outfile.close();
	fclose(input);
	return totalsubtree;
}
int SmallGraphClustering::transGraph2ft(string infilename, string outfilename, int start1, int end1, int start2, int end2)
{
	string filename = outfilename;
	int totalsubtree = 0;
	FILE* input = fopen(infilename.c_str(), "r");
	ofstream outfile(filename.c_str());
	for (int i = 0; i < end2; i++) {
		if ((i >= start1 && i < end1) || (i >= start2 && i < end2)) {
			vector<EnumerateISE::ISEtree> reslut;
			graphClosure tempgraph = readFromFile(input, i);
			reslut = EnumerateISE::ISE2File(tempgraph);
			//totalsubtree += reslut.size();
			for (int k = 0; k < reslut.size(); k++) {
				if (reslut[k].tree.size() < 2) continue;
				// for each returned ISEtree
				EnumerateISE::ISEtree2File(i, reslut[k], outfile, tempgraph);
				totalsubtree++;
			}
		}
	}
	outfile.close();
	fclose(input);
	return totalsubtree;
}
int SmallGraphClustering::AdaTreeNat_fct(int totalsubtree, string infile, string outfile, float threshold)
{
	///////////////////////////////////////////////////////////////////////
	int  BatchSize = totalsubtree;
	const char* fileinput = infile.c_str();
	const char* fileoutput = outfile.c_str();
	int min_sup = 0;
	double relative_min_sup = threshold;
	//////////////////////////////////////////////////////////////////////


	bool is_OrderedTree = true;
	//bool is_TopDownSubtree = false;
	bool is_TopDownSubtree = true;
#if defined(LABELLED)
	bool is_labelled = true;
#else
	bool is_labelled = false;
#endif
	//Variables for AdaTreeNat
	bool is_Incremental = false;
	bool is_SlidingWindow = false;
	bool is_Adaptive1 = false;
	bool is_Adaptive2 = false; // Not used
	bool is_Relaxed_Support = false;
	bool is_Log_Relaxed_Support = false;
	//int  BatchSize = 10000;
	//int  BatchSize = 60;
	int SlidingWindowSize = 2 * BatchSize;
	//int NumberRelaxedIntervals = 10000;
	//int NumberRelaxedIntervals = 10;
	int NumberRelaxedIntervals = BatchSize;
	//std::cout << (is_OrderedTree ? "Ordered Trees - " : "Unordered Trees - ") <<
	//	(is_TopDownSubtree ? "Top-Down Subtrees " : "Induced Subtrees") << endl;
	//std::cout << "Input file:" << fileinput << " - Output file:" << fileoutput << endl;

	bool is_TreeNat = (!is_Incremental && !is_SlidingWindow && !is_Adaptive1 && !is_Adaptive2);
	/*
	if (is_TreeNat == true)
		std::cout << "<TreeNat> ";
	else {
		if (is_Incremental) std::cout << "<IncTreeNat> ";
		if (is_SlidingWindow) std::cout << "<WinTreeNat> ";
		if (is_Adaptive1) std::cout << "<AdaTreeNat1> ";
		if (is_Adaptive2) std::cout << "<AdaTreeNat2> ";
		std::cout << " BatchSize:" << BatchSize;
		if (is_Adaptive1) is_SlidingWindow = true;
		if (is_SlidingWindow) std::cout << " SlidingWindowSize:" << SlidingWindowSize;
		if (is_Relaxed_Support) {
			std::cout << endl << " Relaxed Intervals:" << NumberRelaxedIntervals;
			if (is_Log_Relaxed_Support)
				std::cout << " Log Relaxed Support";
			else
				std::cout << " Linear Relaxed Support";
		}
		std::cout << endl;
	}
	*/


	TreeFactory* tree_factory;
	if (is_OrderedTree == true) {
		if (is_TopDownSubtree == true)
			// Here, we use ordered-topdown factory 
			tree_factory = new OrderedTree_TopDown_Factory;
		else
			tree_factory = new OrderedTreeFactory;
	}
	else {
		if (is_TopDownSubtree == true)
			tree_factory = new UnorderedTree__TopDown_Factory;
		else
			tree_factory = new UnorderedTreeFactory;
	}



	time_t start_time = time(0);
	time_t reading_time;

	if (is_TreeNat == true) {
		//Read Dataset	
		DatasetReader* dataset_reader;
		dataset_reader = new DatasetReader(fileinput, is_OrderedTree, is_labelled, relative_min_sup, 0);
		//std::cout << "dataset_reader->size():" << dataset_reader->size() << endl;
		dataset_reader->close();

		if (relative_min_sup < 1)
			min_sup = (int)(relative_min_sup * dataset_reader->size());

		////////Added by Kai /////////////////////
		if (min_sup < 1)
			min_sup = 1;
		///////////////////////////////////////////

		//std::cout << "Minimum support: " << min_sup;//" ( "<< dataset_reader->size() <<" )"<<endl;
		if (min_sup == 0)
			error(" Support can not be zero.");
		reading_time = time(0);
		//std::cout << " Dataset Size: " << dataset_reader->size();
#if defined(LABELLED)
		//std::cout << " Number of Labels: " << dataset_reader->number_labels();
#endif
		//std::cout << endl;

		//Tree Mining
		TreeMining* closed_tree_mining = new TreeMining(tree_factory, dataset_reader, min_sup);
		int numoffct = closed_tree_mining->print_Closed_Trees2(fileoutput);
		delete dataset_reader;
		delete closed_tree_mining;
		return numoffct;
		//return closed_tree_mining->get_ClosedMapTree()->size();
	}

	if (is_TreeNat == false) {
		if (relative_min_sup > 1)
			error(" Support must be a number between 0 and 1.");
		relative_min_sup /= 2;
		//Read first batch of data		
		DatasetReader* dataset_reader;
		dataset_reader = new DatasetReader(fileinput, is_OrderedTree, is_labelled, relative_min_sup, BatchSize);
		reading_time = time(0);

		if (relative_min_sup < 1)
			min_sup = (int)(relative_min_sup * dataset_reader->size());
		//std::cout << "Minimum support: " << relative_min_sup;
		if (is_SlidingWindow == true && is_Adaptive1 == false)
		//	std::cout << "-" << min_sup << " ( " << dataset_reader->size() << " )";
		//std::cout << endl;
		if (min_sup == 0)
			min_sup = 1;
		//std::cout << min_sup << ",,,," << dataset_reader->size() << endl;
		AdaTreeNat_Mining* adatrenat = new AdaTreeNat_Mining(dataset_reader, is_Relaxed_Support,
			is_Log_Relaxed_Support, NumberRelaxedIntervals, relative_min_sup, BatchSize,
			tree_factory, is_Adaptive1, is_Adaptive2, SlidingWindowSize, min_sup, is_SlidingWindow);

		dataset_reader->close();
		delete dataset_reader;
	}

	//Output time results
	//std::cout << "Time reading " << difftime(reading_time, start_time) << endl;
	//std::cout << "Time processing " << difftime(time(0), reading_time) << endl;


	//std::cout << "Press ENTER to continue..." << endl; cin.get();
	//// Added by Kai
	return 0;
}
int SmallGraphClustering::AdaTreeNat_fct2(int totalsubtree, string infile, string outfile, float threshold)
{
	///////////////////////////////////////////////////////////////////////
	int  BatchSize = totalsubtree;
	const char* fileinput = infile.c_str();
	const char* fileoutput = outfile.c_str();
	int min_sup = 0;
	double relative_min_sup = threshold;
	//////////////////////////////////////////////////////////////////////


	bool is_OrderedTree = true;
	//bool is_TopDownSubtree = false;
	bool is_TopDownSubtree = true;
#if defined(LABELLED)
	bool is_labelled = true;
#else
	bool is_labelled = false;
#endif
	//Variables for AdaTreeNat
	bool is_Incremental = false;
	bool is_SlidingWindow = false;
	bool is_Adaptive1 = false;
	bool is_Adaptive2 = false; // Not used
	bool is_Relaxed_Support = false;
	bool is_Log_Relaxed_Support = false;
	//int  BatchSize = 10000;
	//int  BatchSize = 60;
	int SlidingWindowSize = 2 * BatchSize;
	//int NumberRelaxedIntervals = 10000;
	//int NumberRelaxedIntervals = 10;
	int NumberRelaxedIntervals = BatchSize;
	//std::cout << (is_OrderedTree ? "Ordered Trees - " : "Unordered Trees - ") <<
	//	(is_TopDownSubtree ? "Top-Down Subtrees " : "Induced Subtrees") << endl;
	//std::cout << "Input file:" << fileinput << " - Output file:" << fileoutput << endl;

	bool is_TreeNat = (!is_Incremental && !is_SlidingWindow && !is_Adaptive1 && !is_Adaptive2);
	ofstream out(outfile);
	/*
	if (is_TreeNat == true)
		std::cout << "<TreeNat> ";
	else {
		if (is_Incremental) std::cout << "<IncTreeNat> ";
		if (is_SlidingWindow) std::cout << "<WinTreeNat> ";
		if (is_Adaptive1) std::cout << "<AdaTreeNat1> ";
		if (is_Adaptive2) std::cout << "<AdaTreeNat2> ";
		std::cout << " BatchSize:" << BatchSize;
		if (is_Adaptive1) is_SlidingWindow = true;
		if (is_SlidingWindow) std::cout << " SlidingWindowSize:" << SlidingWindowSize;
		if (is_Relaxed_Support) {
			std::cout << endl << " Relaxed Intervals:" << NumberRelaxedIntervals;
			if (is_Log_Relaxed_Support)
				std::cout << " Log Relaxed Support";
			else
				std::cout << " Linear Relaxed Support";
		}
		std::cout << endl;
	}
	*/


	TreeFactory* tree_factory;
	if (is_OrderedTree == true) {
		if (is_TopDownSubtree == true)
			// Here, we use ordered-topdown factory 
			tree_factory = new OrderedTree_TopDown_Factory;
		else
			tree_factory = new OrderedTreeFactory;
	}
	else {
		if (is_TopDownSubtree == true)
			tree_factory = new UnorderedTree__TopDown_Factory;
		else
			tree_factory = new UnorderedTreeFactory;
	}



	time_t start_time = time(0);
	time_t reading_time;

	if (is_TreeNat == true) {
		//Read Dataset	
		DatasetReader* dataset_reader;
		dataset_reader = new DatasetReader(fileinput, is_OrderedTree, is_labelled, relative_min_sup, 0);
		//std::cout << "dataset_reader->size():" << dataset_reader->size() << endl;
		dataset_reader->close();
		if (relative_min_sup < 1)
			min_sup = (int)(relative_min_sup * dataset_reader->size());
		//std::cout << "Minimum support: " << min_sup;//" ( "<< dataset_reader->size() <<" )"<<endl;
		out << "min_sup:" << min_sup << endl;
		if (min_sup == 0)
			error(" Support can not be zero.");
		out << "min_sup:" << min_sup << endl;
		reading_time = time(0);
		//std::cout << " Dataset Size: " << dataset_reader->size();
#if defined(LABELLED)
		//std::cout << " Number of Labels: " << dataset_reader->number_labels();
#endif
		//std::cout << endl;
		//Tree Mining
		TreeMining* closed_tree_mining = new TreeMining(tree_factory, dataset_reader, min_sup);
		int numoffct = closed_tree_mining->print_Closed_Trees2(fileoutput);
		delete dataset_reader;
		delete closed_tree_mining;
		return numoffct;
		//return closed_tree_mining->get_ClosedMapTree()->size();
	}

	if (is_TreeNat == false) {
		if (relative_min_sup > 1)
			error(" Support must be a number between 0 and 1.");
		relative_min_sup /= 2;
		//Read first batch of data		
		DatasetReader* dataset_reader;
		dataset_reader = new DatasetReader(fileinput, is_OrderedTree, is_labelled, relative_min_sup, BatchSize);
		reading_time = time(0);

		if (relative_min_sup < 1)
			min_sup = (int)(relative_min_sup * dataset_reader->size());
		//std::cout << "Minimum support: " << relative_min_sup;
		if (is_SlidingWindow == true && is_Adaptive1 == false)
		//	std::cout << "-" << min_sup << " ( " << dataset_reader->size() << " )";
		//std::cout << endl;
		if (min_sup == 0)
			min_sup = 1;
		//std::cout << min_sup << ",,,," << dataset_reader->size() << endl;
		AdaTreeNat_Mining* adatrenat = new AdaTreeNat_Mining(dataset_reader, is_Relaxed_Support,
			is_Log_Relaxed_Support, NumberRelaxedIntervals, relative_min_sup, BatchSize,
			tree_factory, is_Adaptive1, is_Adaptive2, SlidingWindowSize, min_sup, is_SlidingWindow);

		dataset_reader->close();
		delete dataset_reader;
	}

	//Output time results
	//std::cout << "Time reading " << difftime(reading_time, start_time) << endl;
	//std::cout << "Time processing " << difftime(time(0), reading_time) << endl;


	//std::cout << "Press ENTER to continue..." << endl; cin.get();
	//// Added by Kai
	return 0;
}
void SmallGraphClustering::freuentFtMinerSampling(ofstream& oStream, int totalsubtree, vector<vector<short>>& allfeature, const int thresholdfretree, const float low_threshold, string infile, string outfile)
{
	HorizontalTreeMiner treeminer;
	vector<map<CanonicalTree, supportNode>> frequenttree = treeminer.getFrequentTreeList((int)(totalsubtree * low_threshold), infile, outfile);
	for (int k = 0; k < frequenttree.size(); k++) {
		map<CanonicalTree, supportNode>::iterator it;
		map<CanonicalTree, supportNode> canonical = frequenttree[k];
		for (it = canonical.begin(); it != canonical.end(); it++) {
			vector<short> canstr = it->first.canonicalString;
			allfeature.push_back(canstr);
			if (allfeature.size() >= thresholdfretree)
				break;
		}
		if (allfeature.size() >= thresholdfretree)
			break;
	}
}
vector<FeatureVector>* SmallGraphClustering::featureSelection_fct(const int SizeOfSelectFeature, ofstream& oStream, string infilename, string dbfilename, int start1, int end1, int start2, int end2)
{
	vector<int> selectedfeature;
	for (int i = 0; i < SizeOfSelectFeature; i++) {
		selectedfeature.push_back(i);
	}
	// read fct from file
	readSelectedFrequentSubtreesFromFile(frequentsubtree, selectedfeature, SizeOfSelectFeature, infilename);
	//std::cout << "frequentsubtree size:" << frequentsubtree.size() << endl;

	//represent each graph
	string querygraph = dbfilename;
	FILE* input = fopen(querygraph.c_str(), "r");
	vector<FeatureVector>* featurevec = new vector<FeatureVector>();
	//featurevec.clear();
	// represent each graph as a vector
	for (int batch = 0; batch < end2; batch++) {
		if ((batch >= start1 && batch < end1) || (batch >= start2 && batch < end2)) {
			graphClosure g = readFromFile(input, batch);
			//this->graphs.push_back(g);
			FeatureVector featurev(SizeOfSelectFeature);
			// Create callback to print mappings
			for (int i = 0; i < selectedfeature.size(); i++) {
				if (num_vertices(g) < num_vertices(frequentsubtree[i]) || num_edges(g) < num_edges(frequentsubtree[i]))
					continue;
				bool testFlag = false;
				deletePattern_callback<graphClosure> user_callback(testFlag);
				vf2_subgraph_mono(frequentsubtree[i], g, user_callback,
					get(vertex_index, frequentsubtree[i]), get(vertex_index, g),
					vertex_order_by_mult(frequentsubtree[i]),
					always_equivalent(),
					make_property_map_equivalent(get(vertex_name, frequentsubtree[i]), get(vertex_name, g)));
				if (testFlag) {
					featurev.feature[i] = 1;
				}
			}
			featurevec->push_back(featurev);
			for (int k = 0; k < SizeOfSelectFeature; k++)
			{
				oStream << featurev.feature[k] << " ";
			}
			oStream << endl;
		}

	}
	return featurevec;
}
void SmallGraphClustering::readFctFromFile(vector<graphClosure>& allfctinclusters, int sizeofallfeature, string filename)
{
	FILE* input = fopen(filename.c_str(), "r");
	for (int i = 0; i < sizeofallfeature; i++) {
		graphClosure g = readFromFile(input, i);
		allfctinclusters.push_back(g);
	}
	std::fclose(input);
}
vector<graphClosure> SmallGraphClustering::selectDistinctFct(vector<graphClosure>& AllfrequentSubtree)
{
	vector<graphClosure> SelectedfrequentSubtree;
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
			SelectedfrequentSubtree.push_back(AllfrequentSubtree[i]);
		}
	}
	return SelectedfrequentSubtree;
}
void SmallGraphClustering::readSelectedFrequentSubtreesFromFile(vector<graphClosure>& frequentsubtree, vector<int> selectedfeature, int sizeofallfeature, string filename)
{
	//cout << "selectedfeature.size():" << selectedfeature.size() << endl;
	//cout << "filename:" << filename << endl;
	//cout << "readSelectedFrequentSubtreesFromFile..in" << endl;
	//cout << "sizeofallfeature:" << sizeofallfeature << endl;
	
	//for (int i = 0; i < selectedfeature.size(); i++) {
	//	cout << selectedfeature[i]<< "\t";
	//}
	//cout << endl;

	frequentsubtree.clear();
	FILE* input = fopen(filename.c_str(), "r");
	for (int i = 0; i < sizeofallfeature; i++) {
		vector<int>::iterator it = find(selectedfeature.begin(), selectedfeature.end(), i);
		if (it != selectedfeature.end()) {
			graphClosure g = readFromFile(input, i);
			frequentsubtree.push_back(g);
		}
		else {
			readFromFile(input, i);
		}
	}
	fclose(input);
	//cout << "frequentsubtree.size:" << frequentsubtree.size() << endl;
	//cout << "readSelectedFrequentSubtreesFromFile..out" << endl;
}
int SmallGraphClustering::longestCommonSubsequence(vector<short>& A, vector<short>& B) {
	if (A.size() < B.size()) {
		return longestCommonSubsequence(B, A);
	}

	// table[i][j] means the longest length of common subsequence
	// of A[0 : i] and B[0 : j].
	vector<vector<int>> table(2, vector<int>(A.size() + 1, 0));

	// if A[i - 1] != B[j - 1]:
	//     table[i][j] = max(table[i - 1][j], table[i][j - 1])
	// if A[i - 1] == B[j - 1]:
	//     table[i][j] = table[i - 1][j - 1] + 1
	for (int i = 1; i < A.size() + 1; ++i) {
		for (int j = 1; j < B.size() + 1; ++j) {
			if (A[i - 1] != B[j - 1]) {
				table[i % 2][j] = max(table[(i - 1) % 2][j],
					table[i % 2][j - 1]);
			}
			else {
				table[i % 2][j] = table[(i - 1) % 2][j - 1] + 1;
			}
		}
	}

	return table[A.size() % 2][B.size()];
}
float  SmallGraphClustering::getSubtreeSim(vector<short>& tree1, vector<short>& tree2) {
	float sim = 0.0f;

	int lcs = longestCommonSubsequence(tree1, tree2);
	//std::cout << "lcs:" << lcs << endl;
	if (tree1.size() > tree2.size()) {
		sim = (float)lcs / tree1.size();
	}
	else {
		sim = (float)lcs / tree2.size();
	}
	return sim;
}
bool SmallGraphClustering::test(const int SizeOfSelectFeature, ofstream& oStream, string infilename, string dbfilename, int start1, int end1, int start2, int end2) {
	vector<int> selectedfeature;
	for (int i = 0; i < SizeOfSelectFeature; i++) {
		selectedfeature.push_back(i);
	}
	oStream << "selectedfeature size:" << endl;
	//////////////////////////////////////////////////////////////////////////////
	for (int k = 0; k < selectedfeature.size(); k++) {
		oStream << selectedfeature[k] << " ";
	}
	oStream << endl;
	//int size = selectedfeature.size();
	//selectedfeature.clear();
	//for (int k = 0; k < size; k++) {
	//	selectedfeature.push_back(k);
	//}
	//oStream << endl;


	oStream << "infilename : " << infilename << endl;
	oStream << "SizeOfSelectFeature :" << SizeOfSelectFeature << endl;
	readSelectedFrequentSubtreesFromFile(frequentsubtree, selectedfeature, SizeOfSelectFeature, infilename);
	//readSelectedFrequentSubtreesFromFile(frequentsubtree, selectedfeature, SizeOfSelectFeature, initialfresubtreefilename);
	oStream << "frequentsubtree size:" << frequentsubtree.size() << endl;

	//represent each graph
	string querygraph = dbfilename;
	FILE* input = fopen(querygraph.c_str(), "r");
	vector<FeatureVector>* featurevec = new vector<FeatureVector>();
	//featurevec.clear();
	// represent each graph as a vector
	for (int batch = 0; batch < end2; batch++) {
		if ((batch >= start1 && batch < end1) || (batch >= start2 && batch < end2)) {
			graphClosure g = readFromFile(input, batch);
			//this->graphs.push_back(g);
			FeatureVector featurev(SizeOfSelectFeature);
			// Create callback to print mappings
			for (int i = 0; i < frequentsubtree.size(); i++) {
				if (num_vertices(g) < num_vertices(frequentsubtree[i]) || num_edges(g) < num_edges(frequentsubtree[i]))
					continue;
				bool testFlag = false;
				deletePattern_callback<graphClosure> user_callback(testFlag);
				vf2_subgraph_mono(frequentsubtree[i], g, user_callback,
					get(vertex_index, frequentsubtree[i]), get(vertex_index, g),
					vertex_order_by_mult(frequentsubtree[i]),
					always_equivalent(),
					make_property_map_equivalent(get(vertex_name, frequentsubtree[i]), get(vertex_name, g)));
				if (testFlag) {
					featurev.feature[i] = 1;
				}
			}
			featurevec->push_back(featurev);
			for (int k = 0; k < SizeOfSelectFeature; k++)
			{
				oStream << featurev.feature[k] << " ";
			}
			oStream << endl;
		}
	}
	oStream << "featurevec size11111:" << featurevec->size() << endl;
	return true;
}

vector<FeatureVector>* SmallGraphClustering::featureSelection(const int SizeOfSelectFeature, vector<vector<short>>& allfeature, ofstream& oStream, string infilename, string dbfilename, int start1, int end1, int start2, int end2) {
	short* visitflag = new short[allfeature.size()];
	for (int i = 0; i < allfeature.size(); i++) {
		visitflag[i] = 0;
	}
	vector<int> selectedfeature;
	selectedfeature.clear();
	//select first feature
	int firstfeatureind = -1;
	float maxfeature = -1;
	int* maxvaluerow = new int[allfeature.size()];//store max similarity in each row
												  // iteration for each column
	bool flag = true;
	for (int k2 = 0; k2 < allfeature.size(); k2++) {
		float sum = 0;
		for (int k1 = 0; k1 < allfeature.size(); k1++) {
			//sum += selectFeature(sqlSp, k1, k2, flag);
			sum += getSubtreeSim(allfeature[k1], allfeature[k2]);
			if (!flag)  oStream << "error!!!" << endl;
		}
		if (sum > maxfeature) {
			maxfeature = sum;
			firstfeatureind = k2;
		}
	}
	selectedfeature.push_back(firstfeatureind);
	visitflag[firstfeatureind] = 1;
	for (int i = 0; i < allfeature.size(); i++) {
		//maxvaluerow[i] = selectFeature(sqlSp, i, firstfeatureind, flag);
		maxvaluerow[i] = getSubtreeSim(allfeature[i], allfeature[firstfeatureind]);
	}
	// select other features
	for (int index = 1; index < SizeOfSelectFeature; index++) {
		int selectindex = -1;
		float maxsum = 0.0f;
		for (int k2 = 0; k2 < allfeature.size(); k2++) {// which feature to be selected
			if (visitflag[k2] == 1) continue;
			float sum = 0.0f;
			for (int k1 = 0; k1 < allfeature.size(); k1++) {//for each feature to be selected, consider max Wij where j \in S;			
				//sum += max(selectFeature(sqlSp, k1, k2, flag), maxvaluerow[k1], flag);
				sum += max(getSubtreeSim(allfeature[k1], allfeature[k2]), maxvaluerow[k1], flag);
			}
			if (sum > maxsum) {
				maxsum = sum;
				selectindex = k2;
			}
		}
		if (selectindex == -1)
			oStream << "error,selectindex is -1!" << endl;
		else {
			selectedfeature.push_back(selectindex);
			visitflag[selectindex] = 1;
			for (int i = 0; i < allfeature.size(); i++) {
				//maxvaluerow[i] = max(selectFeature(sqlSp, i, selectindex, flag), maxvaluerow[i]);
				maxvaluerow[i] = max(getSubtreeSim(allfeature[i], allfeature[selectindex]), maxvaluerow[i], flag);
			}
		}
	}
	delete[] maxvaluerow;
	delete[] visitflag;

	oStream << "selectedfeature size:" << endl;
	//////////////////////////////////////////////////////////////////////////////
	for (int k = 0; k < selectedfeature.size(); k++) {
		oStream << selectedfeature[k] << " ";
	}
	oStream << endl;
	//int size = selectedfeature.size();
	//selectedfeature.clear();
	//for (int k = 0; k < size; k++) {
	//	selectedfeature.push_back(k);
	//}
	//oStream << endl;


	oStream << "infilename : " << infilename << endl;
	oStream << "SizeOfSelectFeature :" << SizeOfSelectFeature << endl;
	readSelectedFrequentSubtreesFromFile(frequentsubtree, selectedfeature, allfeature.size(), infilename);
	//readSelectedFrequentSubtreesFromFile(frequentsubtree, selectedfeature, SizeOfSelectFeature, initialfresubtreefilename);
	oStream << "frequentsubtree size:" << frequentsubtree.size() << endl;

	//represent each graph
	string querygraph = dbfilename;
	FILE* input = fopen(querygraph.c_str(), "r");
	vector<FeatureVector>* featurevec = new vector<FeatureVector>();
	//featurevec.clear();
	// represent each graph as a vector
	for (int batch = 0; batch < end2; batch++) {
		if ((batch >= start1 && batch < end1) || (batch >= start2 && batch < end2)) {
			graphClosure g = readFromFile(input, batch);
			//this->graphs.push_back(g);
			FeatureVector featurev(SizeOfSelectFeature);
			// Create callback to print mappings
			for (int i = 0; i < frequentsubtree.size(); i++) {
				if (num_vertices(g) < num_vertices(frequentsubtree[i]) || num_edges(g) < num_edges(frequentsubtree[i]))
					continue;
				bool testFlag = false;
				deletePattern_callback<graphClosure> user_callback(testFlag);
				vf2_subgraph_mono(frequentsubtree[i], g, user_callback,
					get(vertex_index, frequentsubtree[i]), get(vertex_index, g),
					vertex_order_by_mult(frequentsubtree[i]),
					always_equivalent(),
					make_property_map_equivalent(get(vertex_name, frequentsubtree[i]), get(vertex_name, g)));
				if (testFlag) {
					featurev.feature[i] = 1;
				}
			}
			featurevec->push_back(featurev);
			for (int k = 0; k < SizeOfSelectFeature; k++)
			{
				oStream << featurev.feature[k] << " ";
			}
			oStream << endl;
		}
	}
	oStream << "featurevec size11111:" << featurevec->size() << endl;
	return featurevec;
}
graphClosure SmallGraphClustering::readFromFile(FILE* input, int graphId)
{
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
	return newgraph;
}



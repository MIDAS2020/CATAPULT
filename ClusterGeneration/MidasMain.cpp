#include "MidasMain.h"
#include "GraphUnity.h"
int main(){
	LOGFILE.open(LOGFILENAME);
	//CLUSTERFILE.open(CLUSTERFILENAME);
	clock_t clusterstart, clusterend;
	clusterstart = clock();
	SmallGraphClustering graphcluster(LOGFILE);
	clusterend = clock();
	LOGFILE << "Small graph clustering time: " << (clusterend - clusterstart) << endl;
	LOGFILE.close();
	//CLUSTERFILE.close();
	system("pause");
	return 0;
}




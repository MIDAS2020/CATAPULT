#pragma once
#ifndef RUNNINGTIME_H
#define RUNNINGTIME_H
#include<ctime>
class RunningTime {
public:
	RunningTime() {
		clustertime = 0; closuretime = 0; miningtime = 0;
		selectiontime = 0; othertime = 0; totaltime = 0;
	}

	int clustertime;
	int closuretime;
	int miningtime;
	int selectiontime;
	int othertime;
	int totaltime;
};
#endif
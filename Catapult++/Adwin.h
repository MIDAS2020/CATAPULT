#pragma once
#include "List.h"

class Adwin {

public:

	Adwin(int _M);

	double getEstimation() const;
	bool update(const double& value);
	void print() const;
	int length() const { return W; }

private:

	void insertElement(const double& value);
	void compressBuckets();
	bool checkDrift();

	void deleteElement();

	bool cutExpression(int n0,
		int n1,
		const double& u0,
		const double& u1);


private:

	const int MINTCLOCK;
	const int MINLENGTHWINDOW;
	const double DELTA;
	const int MAXBUCKETS;

private:

	int mintTime;
	int mintClock;
	double mdblError;
	double mdblWidth;

	//BUCKET

	int bucketNumber;
	List bucketList;

	int W; // Width

	int lastBucketRow;

	double sum; // running sum
	double var; // running variance


};


#pragma once
#include <vector>
#include "IsLabeled.h"
class ListNode {

public:

	ListNode(int M);
	void addBack(const double& value, const double& var);
	void dropFront(int n = 1);

public:

	const int M;
	int size;
	std::vector<double> sum, variance;


	ListNode* next;
	ListNode* prev;

};


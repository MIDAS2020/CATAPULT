#pragma once
#include "ListNode.h"

class List {
public:

	List(int M);
	~List();

	void addToTail();
	void removeFromTail();

public:

	const int M;
	int count;

	ListNode* head;
	ListNode* tail;

private:

	void addToHead();
	void removeFromHead();

};



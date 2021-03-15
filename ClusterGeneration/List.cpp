#include "List.h"


List::List(int _M) :M(_M)
{
	// post: initializes the list to be empty.  
	head = NULL;
	tail = NULL;
	count = 0;
	addToHead();
}

////////////////////////////////////////////////////////////////////////////////

List::~List()
{
	while (head != NULL) removeFromHead();
}

////////////////////////////////////////////////////////////////////////////////  

void List::addToHead()
{
	//	 pre: anObject is non-null
	//	 post: the object is added to the beginning of the list

	ListNode* temp = new ListNode(M);

	if (head) {
		temp->next = head;
		head->prev = temp;
	}
	head = temp;
	if (!tail)  tail = head;
	count++;
}

////////////////////////////////////////////////////////////////////////////////

void List::addToTail()
{
	//			 pre: anObject is non-null
	//			 post: the object is added at the end of the list

	ListNode* temp = new ListNode(M);
	if (tail) {
		temp->prev = tail;
		tail->next = temp;
	}
	tail = temp;
	if (!head) head = tail;
	count++;
}

////////////////////////////////////////////////////////////////////////////////	

void List::removeFromHead()
{
	//		 pre: list is not empty
	//		 post: removes and returns first object from the list
	ListNode* temp;
	temp = head;
	head = head->next;
	if (head != NULL)
		head->prev = NULL;
	else
		tail = NULL;
	count--;
	delete temp;
	return;
}
////////////////////////////////////////////////////////////////////////////////

void List::removeFromTail()
{
	//			 pre: list is not empty
	//			 post: the last object in the list is removed and returned
	ListNode* temp;
	temp = tail;
	tail = tail->prev;
	if (tail == NULL)
		head = NULL;
	else
		tail->next = NULL;
	count--;
	delete temp;
	return;
}

#include "ListNode.h"

using namespace std;

////////////////////////////////////////////////////////////////////////////////

ListNode::ListNode(int _M)
	:M(_M),
	size(0),
	sum(M + 1, 0.0),
	variance(M + 1, 0.0),
	next(NULL),
	prev(NULL)
{}

////////////////////////////////////////////////////////////////////////////////

void ListNode::addBack(const double& value, const double& var)
{
	// insert a Bucket at the end
	sum[size] = value;
	variance[size] = var;
	size++;
}

//////////////////////////////////////////////////////////////////////////////// 

void ListNode::dropFront(int n)
{
	// drop first n elements
	for (int k = n; k <= M; k++) {
		sum[k - n] = sum[k];
		variance[k - n] = variance[k];
	}

	for (int k = 1; k <= n; k++) {
		sum[M - k + 1] = 0;
		variance[M - k + 1] = 0;
	}

	size -= n;

}

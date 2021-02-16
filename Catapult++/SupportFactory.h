#pragma once
#include <cmath>

class Support_Factory {

public:

	Support_Factory(int NumberRelaxedIntervals, double dblmin_sup) {
		mNumberRelaxedIntervals = NumberRelaxedIntervals;
		mdblmin_sup = dblmin_sup;
	}
	virtual int RelaxedSupport(double dblSupport) = 0;
	virtual double RelativeSupport(int iSupport) = 0;
	inline int RelaxedSupport(int AbsSupport, int Size) {
		return RelaxedSupport(((double)AbsSupport) / ((double)Size));
	};

protected:
	int mNumberRelaxedIntervals;
	double mdblmin_sup;
};



class Log_Relaxed_Support_Factory : public Support_Factory {

public:

	Log_Relaxed_Support_Factory(int NumberRelaxedIntervals, double dblmin_sup) :Support_Factory(NumberRelaxedIntervals, dblmin_sup) {};
	inline int RelaxedSupport(double dblSupport) {
		return (int)((double)mNumberRelaxedIntervals - ((double)(mNumberRelaxedIntervals - 1)) * log(dblSupport) / log(mdblmin_sup));
	};
	inline double RelativeSupport(int iSupport) { //From Relaxed to Relative
		return exp((((double)mNumberRelaxedIntervals - (double)iSupport) * log(mdblmin_sup)) / ((double)(mNumberRelaxedIntervals - 1)));
	};

};

class Linear_Relaxed_Support_Factory : public Support_Factory {

public:
	Linear_Relaxed_Support_Factory(int NumberRelaxedIntervals, double dblmin_sup) :Support_Factory(NumberRelaxedIntervals, dblmin_sup) {};
	inline int RelaxedSupport(double dblSupport) {
		//cout << "RELAXED SUPPORT "<<dblSupport << "-"<< (((double) mNumberRelaxedIntervals))<< endl;
		return (int)(((double)mNumberRelaxedIntervals) * dblSupport);
	};
	inline double RelativeSupport(int iSupport) { //From Relaxed to Relative
		return ((double)iSupport / (double)mNumberRelaxedIntervals);
	};
};
/*
class Not_Relaxed_Support_Factory : public Support_Factory {
//Is a Linear_Relaxed_Support_Factory where mNumberRelaxedIntervals is the SlidingWindowSize
public:
	Not_Relaxed_Support_Factory(int NumberRelaxedIntervals,double dblmin_sup):Support_Factory(NumberRelaxedIntervals,dblmin_sup){};
	inline int RelaxedSupport(double dblSupport) {
		return (int) (((double) mNumberRelaxedIntervals)*dblSupport);
	};
	inline double RelativeSupport(int iSupport) { //From Relaxed to Relative
		return ((double) iSupport/(double)mNumberRelaxedIntervals);
	};
};
*/
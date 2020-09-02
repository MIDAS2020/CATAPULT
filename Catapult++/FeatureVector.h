#pragma once
#include<iostream>
using namespace std;

class FeatureVector {
public:
	/*
	vector<short> feature;
	FeatureVector(int size) {
		for (int i = 0; i < size; i++) {
			feature.push_back(0);
		}
	}
	*/
	int FvSize;
	short* feature;
	FeatureVector() {
		
	}
	FeatureVector(int size) {
		FvSize = size;
		feature = new short[size];
		for (int i = 0; i < size; i++) {
			feature[i] = 0;
		}
	}
	~FeatureVector(){
		//delete feature;
	}
	int getSize() {
		return FvSize;
	}
};
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package db;

/**
 *
 * @author cjjin
 */
public class Parameters {

	private String dbname;
	private int datasize;
	private float supportThreshold; // support threshold
	private int mfThreshold; // Fragment size to divide into MF & DF;
	private int similarityThreshold; // similarity search

	public void setparameters(String name, int k, float avalue, int bvalue, int sigma) {
		dbname = name;
		datasize = k;
		supportThreshold = avalue;
		mfThreshold = bvalue;
		similarityThreshold = sigma;
	}

	public int getdatasize() {
		return datasize;
	}

	public float getSupportThreshold() {
		return supportThreshold;
	}

	public int getMFThreshold() {
		return mfThreshold;
	}

	public String getDBName() {
		return dbname;
	}

	public int getSimilarityThreshold() {
		return similarityThreshold;
	}
}

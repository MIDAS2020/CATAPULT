/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package andyfyp;

/**
 *
 * @author Andy
 */
public class PatternSetInfo {
	private int freqNum;
	private int difNum;
	private int nifNum;

	private int patternSizeMin;
	private int patternSizeMax;

	public PatternSetInfo(int freqNum, int difNum, int nifNum, int patternSizeMin, int patternSizeMax) {
		this.freqNum = freqNum;
		this.difNum = difNum;
		this.nifNum = nifNum;

		this.patternSizeMin = patternSizeMin;
		this.patternSizeMax = patternSizeMax;
	}

	public int getPatternSizeMin() {
		return patternSizeMin;
	}

	public int getPatternSizeMax() {
		return patternSizeMax;
	}

	public int getFreqNum() {
		return freqNum;
	}

	public int getDifNum() {
		return difNum;
	}

	public int getNifNum() {
		return nifNum;
	}
}

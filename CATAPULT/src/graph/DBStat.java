/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

public class DBStat {
	private String label;
	private int frequency;

	public DBStat() {
		label = "";
		frequency = 0;
	}

	public DBStat(String l, int f) {
		label = l;
		frequency = f;
	}

	public int getFreq() {
		return frequency;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String s) {
		label = s;
	}

	public void setFreq(int f) {
		frequency = f;
	}

	public void print() {
		System.out.println("label=" + label + " frequency=" + frequency);
	}
}

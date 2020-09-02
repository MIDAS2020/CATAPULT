/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
* Copyright 2010, Center for Advanced Information Systems,Nanyang Technological University
*
* File name: Record.java
*
* Abstract: record the prequery time and current candidate size
* Current Version:      0.1
* Auther:               Jin Changjiu
* Modified Date:        Jun.3,2010
*
*/
package result;

/**
 *
 * @author c4jin
 */
public class Record {

	private float time = 0;
	private int exactSize = 0;
	private int simSize = 0;

	public void insertRecord(float t, int e, int s) {
		time = t;
		exactSize = e;
		simSize = s;
	}

	public float getTime() {
		return time;
	}

	public int getExaSize() {
		return exactSize;
	}

	public int getSimSize() {
		return simSize;
	}

}

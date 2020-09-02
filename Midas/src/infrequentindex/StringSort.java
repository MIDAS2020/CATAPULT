package infrequentindex;

/*
* Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
*
* File name: StringSort.java
*
* Abstract:
*
* Current Version:      0.1
* Auther:               Jin Changjiu
* Modified Date:        Jun.16,2009
*
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Comparator;

/**
 *
 * @author cjjin
 */

public class StringSort implements Comparator {

	public int compare(Object o1, Object o2) {
		String e1 = (String) o1;
		String e2 = (String) o2;
		if (e1.length() > e2.length()) {
			return 1;
		} else if (e1.length() == e2.length()) {
			return 0;
		} else {
			return -1;
		}
	}

}

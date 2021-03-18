/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package similarity;

import java.util.Comparator;

/**
 *
 * @author cjjin
 */
public class IdComparator implements Comparator {

	public int compare(Object o1, Object o2) {
		int e1 = (Integer) o1;
		int e2 = (Integer) o2;

		if (e1 > e2) {
			return 1;
		} else if (e1 == e2) {
			return 0;
		} else {
			return -1;
		}
	}
}

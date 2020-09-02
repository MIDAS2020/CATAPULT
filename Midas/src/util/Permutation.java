/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

import java.util.*;

/**
 *
 * @author cjjin
 */
public class Permutation {

	public void trail(int m, int n, int[] a, Vector<String> seqlist) {
		if (m >= n) {
			String seq = "";
			for (int i = 0; i < n; i++) {
				seq += a[i] + " ";
			}
			seqlist.addElement(seq);
		} else {
			int j = 0, i = 0;
			for (j = 1; j < n + 1; j++) {
				boolean f = true;
				for (i = 0; i < m; i++) {
					if (a[i] == j) {
						f = false;
						break;
					}
				}
				if (f) {
					a[m] = j;
					trail(m + 1, n, a, seqlist);
				}

				a[m] = 0;
			}
		}
	}

}

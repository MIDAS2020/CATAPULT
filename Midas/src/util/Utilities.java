/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util;

/**
 *
 * @author nguyenhhien
 */
public class Utilities {
	public static final int base = 1000000;

	public static int getPatternId(int patId, int coeff) {
		int ans = base + patId * 20;
		ans = ans * coeff;
		return ans;
	}

	public static int getPatternId1(int value) {
		int coeff;
		for (coeff = 1; coeff < 100; coeff++) {
			if (value / coeff >= base && value / coeff < 2 * base)
				break;
		}
		return (value / coeff - base) / 20;
	}

	public static int getPatternCoeff(int value) {
		int coeff;
		for (coeff = 1; coeff < 100; coeff++) {
			if (value / coeff >= base && value / coeff < 2 * base)
				break;
		}

		return coeff;
	}

	public static int getPatternVertexId(int patId, int vId, int coeff) {
		return (base + patId * 20 + vId + 1) * coeff;
	}

	public static int getPatternVertexId1(int value) {
		int coeff;
		for (coeff = 1; coeff < 10; coeff++) {
			if (value / coeff >= base && value / coeff < 2 * base)
				break;
		}

		int ans = (value / coeff - base) % 20 - 1;
		return ans;
	}
}

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

public class DP {

	public static List<String> GetProductModelList(List<String> attrList) {

		List<String> productArray = Arrays.asList(attrList.get(0).split("-"));

		for (int i = 1; i < attrList.size(); i++) {
			productArray = JoinPart(productArray, attrList.get(i).split("-"));
		}
		return productArray;
	}

	// Descartes Product
	public static List<String> JoinPart(List<String> part1, String[] part2) {
		List<String> result = new ArrayList<String>();
		for (String str1 : part1) {
			for (String str2 : part2) {
				result.add(str1 + "-" + str2);
			}
		}
		return result;
	}

}

/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: Nodecompare.java
 *
 * Abstract:   Compare node based on label and degree
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Jun.16,2009
 *
 */
package infrequentindex;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.Comparator;
import frequentindex.Vertex;

/**
 *
 * @author cjjin
 */
public class Nodecompare implements Comparator {

	// Campare label -> degree;
	// similar meaning as o2-o1
	public int compare(Object o1, Object o2) {
		Vertex e1 = (Vertex) o1;
		Vertex e2 = (Vertex) o2;

		if (Integer.parseInt(e1.getLabel()) > Integer.parseInt(e2.getLabel())
				|| (e1.getLabel().equals(e2.getLabel()) && e1.getDegree() > e2.getDegree())) {
			return -1;
		} else if (e1.getLabel().equals(e2.getLabel()) && e1.getDegree() == e2.getDegree()) {
			return 0;
		} else {
			return 1;
		}
	}
}

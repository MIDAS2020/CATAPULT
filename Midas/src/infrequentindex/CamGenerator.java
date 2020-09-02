/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * Copyright 2009, Center for Advanced Information Systems,Nanyang Technological University
 *
 * File name: CamGenerator.java
 *
 * Abstract: Build the CAM code for a graph
 *
 * Current Version:      0.1
 * Auther:               Jin Changjiu
 * Modified Date:        Oct.7,2009
 *
 */
package infrequentindex;

import adjlistgraph.Graph;
import frequentindex.Vertex;
import util.DP;
import util.Permutation;
import java.util.*;

/**
 *
 * @author cjjin
 */
public class CamGenerator {

	private static Permutation pm = new Permutation();
	private static DP dp = new DP();

	public static String buildCam(Graph gi) {
		List list = new ArrayList();
		Vector<Vertex> qn = new Vector<Vertex>();
		for (int m = 0; m < gi.getVertexNum(); m++) {
			Vertex v = new Vertex();
			v.setDegree(gi.getNode(m).getDegree());
			v.setLabel(gi.getNode(m).getLabel());
			v.setNeighbor("");

			v.setID(m);
			qn.addElement(v);

		}

		Nodecompare mc = new Nodecompare();
		Collections.sort(qn, mc);

		Vector<String> part = new Vector<String>();
		String po = "";

		for (int index = 0; index < qn.size(); index++) {
			po += index + " ";
			if ((index < qn.size() - 1 && mc.compare(qn.elementAt(index), qn.elementAt(index + 1)) != 0)
					|| index == qn.size() - 1) {
				part.addElement(po);
				po = "";

			}
		}

		for (int i = 0; i < part.size(); i++) {
			String[] s = part.elementAt(i).split("\\s");
			int l = Integer.parseInt(s[0]);

			Vector<String> seqlist = new Vector<String>();
			int[] b = new int[s.length];
			pm.trail(0, s.length, b, seqlist);

			String tmp = "";

			for (int t = 0; t < seqlist.size(); t++) {
				String[] sq = seqlist.elementAt(t).split("\\s");

				for (int p = 0; p < sq.length; p++) {
					tmp += (Integer.parseInt(sq[p]) + l) + " ";
				}

				tmp += "-";

				if (t > 2000)// should be less than 5040
				{
					break;
				}
			}

			list.add(tmp);
		}

		List list2 = dp.GetProductModelList(list);

		Iterator it = list2.iterator();
		String cam = "";

		String[] maxcc = new String[qn.size()];
		for (int m = 0; m < qn.size(); m++) {
			maxcc[m] = "";
		}

		while (it.hasNext()) {
			String tmp = (String) it.next();
			String cc = "";
			for (int m = 0; m < qn.size(); m++) {
				qn.elementAt(m).setNeighbor("");
			}
			String[] two = tmp.split("-");

			Vector<String> nodeset = new Vector<String>();

			for (int t = 0; t < two.length; t++) {

				String[] tm = two[t].split("\\s");
				for (int tt = 0; tt < tm.length; tt++) {
					nodeset.addElement(tm[tt]);
				}
			}

			for (int m = 0; m < nodeset.size(); m++) {

				int mm = Integer.parseInt(nodeset.elementAt(m)) - 1;

				if (m == 0) {
					cc += "(" + qn.elementAt(mm).getLabel() + ")";
				} else {
					cc += qn.elementAt(mm).getNeighbor() + "(" + qn.elementAt(mm).getLabel() + ")";
				}

				if (cc.compareTo(maxcc[m]) < 0) {
					break;
				} else if (cc.compareTo(maxcc[m]) > 0) {
					maxcc[m] = cc;
				}

				int f = qn.elementAt(mm).getId();
				for (int n = 0; n < nodeset.size(); n++) {
					int nn = Integer.parseInt(nodeset.elementAt(n)) - 1;
					int t = qn.elementAt(nn).getId();

					String neighbor = qn.elementAt(nn).getNeighbor();

					if (gi.getEdgeLabel(f, t) > 0) {
						qn.elementAt(nn).setNeighbor(neighbor.concat("1"));
					} else {
						qn.elementAt(nn).setNeighbor(neighbor.concat("0"));
					}
				}
			}

			nodeset.clear();
			if (cc.compareTo(cam) > 0) {
				cam = cc;
			}

		}

		return cam;

	}
}

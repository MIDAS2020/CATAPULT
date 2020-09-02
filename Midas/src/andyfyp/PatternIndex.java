/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package andyfyp;

import adjlistgraph.Graph;
import exactquery.NewUllmanVerify;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;

/**
 *
 * @author Andy
 */
public class PatternIndex {

	private ArrayList<PatternNode> nodeList = new ArrayList<PatternNode>();

	public PatternIndex(ArrayList<Graph> patternSet) throws IOException {
		int patternMinSize;
		Graph unindexedPattern;
		NewUllmanVerify exactVerifier = new NewUllmanVerify();

		/* Sort the list of patterns by size from the smallest to the largest */
		Collections.sort(patternSet, new PatternSizeComparator());

		patternMinSize = patternSet.get(0).getEdgeNum();

		for (int i = 0; i < patternSet.size(); i++) {
			unindexedPattern = patternSet.get(i);
			PatternNode unindexedNode = new PatternNode(i, unindexedPattern.getGraphID(), unindexedPattern.getIdList());

			if (unindexedPattern.getEdgeNum() == patternMinSize) {
				nodeList.add(unindexedNode);
			} else {
				for (int j = 0; j < this.nodeList.size(); j++) {
					PatternNode indexedNode = this.nodeList.get(j);

					Graph indexedPattern = patternSet.get(indexedNode.nodeID);

					/*
					 * if ((indexedNode.patternID == 0) && (unindexedNode.patternID == 5)) {
					 * System.out.println("1> " + indexedPattern.getCam()); for (int k = 0; k <
					 * indexedPattern.getVertexNum(); k++) {
					 * System.out.println(indexedPattern.getNode(k).getLabel() + " " +
					 * indexedPattern.getNode(k).getDegree()); }
					 * 
					 * System.out.println("2> " + unindexedPattern.getCam()); for (int k = 0; k <
					 * unindexedPattern.getVertexNum(); k++) {
					 * System.out.println(unindexedPattern.getNode(k).getLabel() + " " +
					 * unindexedPattern.getNode(k).getDegree()); }
					 * 
					 * System.out.println(exactVerifier.verify(indexedPattern, unindexedPattern)); }
					 */

					if (((indexedPattern.getEdgeNum() + 1) == unindexedPattern.getEdgeNum())
							&& exactVerifier.verify(indexedPattern, unindexedPattern)) {
						indexedNode.addChild(unindexedNode.patternID);
						indexedNode.delIDList.removeAll(unindexedNode.delIDList);
					}
				}

				nodeList.add(unindexedNode);
			}
		}

		/* Sort the index by increasing pattern ID */
		Collections.sort(nodeList, new PatternIDComparator());

		/*
		 * for (int i = 0; i < patternSet.size(); i++) {
		 * System.out.println(patternSet.get(i).getEdgeNum() + " " +
		 * patternSet.get(i).getGraphID()); for (Iterator iter =
		 * patternSet.get(i).getIdList().iterator(); iter.hasNext();) { int graphID =
		 * (Integer)iter.next();
		 * 
		 * System.out.print(graphID + " "); } System.out.println(); }
		 */
	}

	ArrayList<PatternNode> getNodeList() {
		return nodeList;
	}

	class PatternIDComparator implements Comparator<PatternNode> {
		public int compare(PatternNode a, PatternNode b) {
			if (a.patternID > b.patternID)
				return 1;
			else if (a.patternID == b.patternID)
				return 0;
			else
				return -1;
		}
	}

	class PatternSizeComparator implements Comparator<Graph> {
		public int compare(Graph a, Graph b) {
			if (a.getEdgeNum() > b.getEdgeNum())
				return 1;
			else if (a.getEdgeNum() == b.getEdgeNum())
				return 0;
			else
				return -1;
		}
	}

	class PatternNode {
		int nodeID;
		int patternID;
		HashSet<Integer> delIDList;
		HashSet<Integer> childrenList = new HashSet<Integer>();

		public PatternNode(int nodeID, int patternID, HashSet<Integer> containingIDSet) {
			this.nodeID = nodeID;
			this.patternID = patternID;
			delIDList = (HashSet<Integer>) containingIDSet.clone();
		}

		public void addChild(int childPatternID) {
			childrenList.add(childPatternID);
		}
	}
}

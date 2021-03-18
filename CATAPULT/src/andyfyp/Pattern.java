package andyfyp;

import java.util.Vector;

/**
 *
 * @author nguyenhhien
 */
public class Pattern {
	public static int lastCoeff = 1;

	public int coeff;

	public int ID;

	public int vertexNum;
	public PatternVertex[] vertexSet;

	public int edgeNum;
	public Vector<Vector<PatternAdjacentElement>> edgeSet;

	public int[] labelCnt; // lblId -> number of occurence;

	public Vector<Integer> containingIdList;

	public Pattern(int nodeNum) {
		ID = -1;

		vertexNum = nodeNum;
		vertexSet = new PatternVertex[vertexNum];

		edgeSet = new Vector<Vector<PatternAdjacentElement>>();
		for (int i = 0; i < nodeNum; i++)
			edgeSet.add(new Vector<PatternAdjacentElement>());

		containingIdList = new Vector<Integer>();
	}

	public void addVertex(PatternVertex vertex, int pos) {
		vertexSet[pos] = vertex;
	}

	public void increaseVertexDegree(int vId) {
		vertexSet[vId].degree++;
	}

	public void addEdge(int eId, int v1, int v2, String label) {
		PatternAdjacentElement tmpAdjElem;

		tmpAdjElem = new PatternAdjacentElement(eId, label, v2);
		tmpAdjElem.addToGraphList(this.ID);
		edgeSet.get(v1).add(tmpAdjElem);

		tmpAdjElem = new PatternAdjacentElement(eId, label, v1);
		tmpAdjElem.addToGraphList(this.ID);
		edgeSet.get(v2).add(tmpAdjElem);
	}

	public void addEdge(int eId, int v1, int v2, String label, Vector<Integer> graphIdSet) {
		PatternAdjacentElement tmpAdjElem;

		tmpAdjElem = new PatternAdjacentElement(eId, label, v2);
		tmpAdjElem.addToGraphList(graphIdSet);
		edgeSet.get(v1).add(tmpAdjElem);

		tmpAdjElem = new PatternAdjacentElement(eId, label, v1);
		tmpAdjElem.addToGraphList(graphIdSet);
		edgeSet.get(v2).add(tmpAdjElem);

		vertexSet[v1].degree++;
		vertexSet[v2].degree++;
	}

	public boolean hasEdge(int u, int v) {
		Vector<PatternAdjacentElement> tmpEdgeSet = edgeSet.get(u);

		for (int i = 0; i < tmpEdgeSet.size(); i++) {
			if (tmpEdgeSet.get(i).adjVertexId == v)
				return true;
		}
		return false;
	}

	@Override
	public Pattern clone() {
		Pattern tmpPattern = new Pattern(this.vertexNum);
		tmpPattern.ID = this.ID;
		tmpPattern.edgeNum = this.edgeNum;
		tmpPattern.vertexNum = this.vertexNum;
		tmpPattern.coeff = lastCoeff;
		lastCoeff++;

		for (int i = 0; i < this.vertexNum; i++) {
			PatternVertex vertex = new PatternVertex();
			vertex.Id = this.vertexSet[i].Id;
			vertex.labelId = this.vertexSet[i].labelId;
			vertex.x = this.vertexSet[i].x;
			vertex.y = this.vertexSet[i].y;
			tmpPattern.addVertex(vertex, vertex.Id);
		}

		for (int u = 0; u < this.vertexNum; u++) {
			Vector<PatternAdjacentElement> adjU = this.edgeSet.get(u);
			for (int i = 0; i < adjU.size(); i++) {
				int v = adjU.get(i).adjVertexId;

				if (u > v)
					continue;
				int _eId = adjU.get(i).eId;
				String _eLabel = adjU.get(i).edgeLabel;

				tmpPattern.addEdge(_eId, u, v, _eLabel);
				tmpPattern.increaseVertexDegree(u);
				tmpPattern.increaseVertexDegree(v);
			}
		}

		return tmpPattern;
	}

}

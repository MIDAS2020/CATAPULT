package andyfyp;

import java.util.Vector;

/**
 *
 * @author nguyenhhien
 */
public class PatternAdjacentElement {
	public int eId;
	public String edgeLabel;
	public int adjVertexId;

	public Vector<Integer> graphList;

	public PatternAdjacentElement(int _eId, String _edgeLabel, int _adjVertexId) {
		eId = _eId;
		edgeLabel = _edgeLabel;
		adjVertexId = _adjVertexId;

		graphList = new Vector<Integer>();
	}

	public void addToGraphList(int graphId) {
		graphList.add(graphId);
	}

	public void addToGraphList(Vector<Integer> graphIdSet) {
		graphList.addAll(graphIdSet);
	}
}

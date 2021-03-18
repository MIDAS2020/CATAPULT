package andyfyp;

import java.util.Vector;
//import org.netbeans.api.visual.widget.LabelWidget;

/**
 *
 * @author nguyenhhien
 */
public class PatternVertex {

	public int Id;
	public String labelId;
	// public LabelWidget attachedLabelWidget;
	public int degree;

	public int x, y;

	public Vector<Integer> graphList;

	public PatternVertex() {
		Id = -1;
		labelId = null;
		degree = 0;

		graphList = new Vector<Integer>();
	}

	public PatternVertex(int _id, String _label) {
		Id = _id;
		labelId = _label;
		degree = 0;

		graphList = new Vector<Integer>();
	}

	public void addToGraphList(int graphId) {
		graphList.add(graphId);
	}

	public void addToGraphList(Vector<Integer> graphIdSet) {
		graphList.addAll(graphIdSet);
	}

	public String getLabel() {
		return labelId;
	}

	public int getDegree() {
		return degree;
	}

	public int getId() {
		return Id;
	}
}

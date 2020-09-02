/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package graph;

public class simpleEdge {
	private simpleVertex source;
	private simpleVertex target;
	private int weight;

	public simpleEdge(simpleVertex s, simpleVertex t) {
		source = s;
		target = t;
		weight = 1;
	}

	public simpleEdge(simpleVertex s, simpleVertex t, int w) {
		source = s;
		target = t;
		weight = w;
	}

	public simpleVertex getSource() {
		return source;
	}

	public simpleVertex getTarget() {
		return target;
	}

	public int getWeight() {
		return weight;
	}

	public String getEdgeLabelString() {
		String sourceLabel = getSource().getLabel();
		String targetLabel = getTarget().getLabel();
		if (sourceLabel.compareTo(targetLabel) <= 0)
			return sourceLabel + " " + targetLabel;
		else
			return targetLabel + " " + sourceLabel;
	}

	public void print() {
		System.out.println(source.getID() + " [" + source.getLabel() + "] -- " + target.getID() + " ["
				+ target.getLabel() + "] weight=" + weight);
	}
}

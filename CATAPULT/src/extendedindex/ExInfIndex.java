package extendedindex;

import adjlistgraph.Graph;
import exactquery.NewUllmanVerify;
import frequentindex.Vertex;
import infrequentindex.CamGenerator;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

/**
 *
 * @author nguyenhhien
 */
public class ExInfIndex {
	private ArrayList<ExFragment> exInfIndex;
	private ExIndex exIndex;

	public ExInfIndex(ExIndex e) {
		exIndex = e;
		exInfIndex = new ArrayList<ExFragment>();
	}

	public ArrayList<ExFragment> getExInfIndex() {
		return exInfIndex;
	}

	public void build() {
		NewUllmanVerify exactVerifier = new NewUllmanVerify();

		for (int i = 600; i < exIndex.getPatternSet().size(); i++) {
			for (int j = i; j < exIndex.getPatternSet().size(); j++) {
				Graph pat1 = exIndex.getPatternSet().get(i);
				Graph pat2 = exIndex.getPatternSet().get(j);

				for (int ii = 0; ii < pat1.getVertexNum(); ii++) {
					for (int jj = 0; jj < pat2.getVertexNum(); jj++) {
						ExFragment tmpExFragment = new ExFragment();

						Graph curGraph = getGraph(pat1, ii, pat2, jj);
						Graph curView = getView(i, ii, j, jj);

						System.out.println("here...");
						System.out.println(curGraph.getCam());
						System.out.println(curView.getCam());

						tmpExFragment.realCam = curGraph.getCam();
						tmpExFragment.vNum = pat1.getVertexNum() + pat2.getVertexNum();
						tmpExFragment.viewCamSet.add(curView.getCam());

						ArrayList<Integer> idList = new ArrayList<Integer>();
						idList.addAll(pat1.getIdList());
						idList.retainAll(pat2.getIdList());

						for (int k = 0; k < idList.size(); k++) {
							int gId = idList.get(k);
							if (exactVerifier.verify(curGraph, exIndex.getGraphSet().get(gId))) {
								tmpExFragment.idList.add(gId);
							}
						}

						if (!tmpExFragment.idList.isEmpty()) {
							System.out.println("At " + i + ", " + j + ", " + ii + ", " + jj);
							System.out.println("answer set..." + tmpExFragment.idList.size());
							System.out.println("e.g: " + tmpExFragment.idList.get(0));
							System.out.println("----------------------------");

							exInfIndex.add(tmpExFragment);
						}
					}
				}
			}
		} // end i;

		writeToFile();
	}

	private Graph getGraph(Graph pat1, int vId1, Graph pat2, int vId2) {
		int vNum1 = pat1.getVertexNum();
		int vNum2 = pat2.getVertexNum();

		Graph resGraph = new Graph();
		resGraph.setVertexNum(vNum1 + vNum2);
		resGraph.setEdgeNum(pat1.getEdgeNum() + pat2.getEdgeNum() + 1);

		// copy vertices;
		for (int i = 0; i < vNum1; i++) {
			Vertex tmpVertex = new Vertex();
			tmpVertex.setLabel(pat1.getNode(i).getLabel());
			tmpVertex.setID(i);
			resGraph.addNode(tmpVertex);
		}
		for (int i = 0; i < vNum2; i++) {
			Vertex tmpVertex = new Vertex();
			tmpVertex.setLabel(pat2.getNode(i).getLabel());
			tmpVertex.setID(vNum1 + i);
			resGraph.addNode(tmpVertex);
		}

		// copy edges
		for (int i = 0; i < vNum1; i++) {
			for (int j = i + 1; j < vNum1; j++) {
				if (pat1.getEdgeLabel(i, j) == 1) {
					resGraph.addEdge(i, j);
					resGraph.getNode(i).setIn(j);
					resGraph.getNode(i).incDegree();
					resGraph.getNode(j).setIn(i);
					resGraph.getNode(j).incDegree();
				}
			}
		}
		for (int i = 0; i < vNum2; i++) {
			for (int j = i + 1; j < vNum2; j++) {
				if (pat2.getEdgeLabel(i, j) == 1) {
					int ii = i + vNum1;
					int jj = j + vNum1;
					resGraph.addEdge(ii, jj);
					resGraph.getNode(ii).setIn(jj);
					resGraph.getNode(ii).incDegree();
					resGraph.getNode(jj).setIn(ii);
					resGraph.getNode(jj).incDegree();
				}
			}
		}
		int i = vId1;
		int j = vNum1 + vId2;
		resGraph.addEdge(i, j);
		resGraph.getNode(i).setIn(j);
		resGraph.getNode(i).incDegree();
		resGraph.getNode(j).setIn(i);
		resGraph.getNode(j).incDegree();

		resGraph.setCam(CamGenerator.buildCam(resGraph));
		return resGraph;
	}

	private Graph getView(int patId1, int patVId1, int patId2, int patVId2) {
		Graph resGraph = new Graph();
		resGraph.setVertexNum(4);
		resGraph.setEdgeNum(3);

		Vertex tmpVertex;

		tmpVertex = new Vertex();
		tmpVertex.setLabel(Utilities.getLabel(patId1));
		tmpVertex.setID(0);
		resGraph.addNode(tmpVertex);

		tmpVertex = new Vertex();
		tmpVertex.setLabel(Utilities.getLabel(patId1, patVId1));
		tmpVertex.setID(1);
		resGraph.addNode(tmpVertex);

		tmpVertex = new Vertex();
		tmpVertex.setLabel(Utilities.getLabel(patId2));
		tmpVertex.setID(2);
		resGraph.addNode(tmpVertex);

		tmpVertex = new Vertex();
		tmpVertex.setLabel(Utilities.getLabel(patId2, patVId2));
		tmpVertex.setID(3);
		resGraph.addNode(tmpVertex);

		resGraph.addEdge(0, 1);
		resGraph.getNode(0).setIn(1);
		resGraph.getNode(0).incDegree();
		resGraph.getNode(1).setIn(0);
		resGraph.getNode(1).incDegree();

		resGraph.addEdge(1, 2);
		resGraph.getNode(1).setIn(2);
		resGraph.getNode(1).incDegree();
		resGraph.getNode(2).setIn(1);
		resGraph.getNode(2).incDegree();

		resGraph.addEdge(2, 3);
		resGraph.getNode(2).setIn(3);
		resGraph.getNode(2).incDegree();
		resGraph.getNode(3).setIn(2);
		resGraph.getNode(3).incDegree();

		resGraph.setCam(CamGenerator.buildCam(resGraph));
		return resGraph;
	}

	private void writeToFile() {
		String fileName = "ExtendedIndex/e_IIndex";
		File fout = new File(fileName);

		PrintStream ps;
		try {
			ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(fout)));

			for (int i = 0; i < exInfIndex.size(); i++) {
				ps.println(exInfIndex.get(i).realCam);

				ArrayList<String> viewCamSet = exInfIndex.get(i).viewCamSet;
				for (int j = 0; j < viewCamSet.size(); j++)
					ps.print(viewCamSet.get(j) + " ");
				ps.println();

				ArrayList<Integer> idList = exInfIndex.get(i).idList;
				for (int j = 0; j < idList.size(); j++) {
					ps.print(idList.get(j) + " ");
				}
				ps.println();
			}

			ps.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

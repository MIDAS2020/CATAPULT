package extendedindex;

import adjlistgraph.Graph;
import exactquery.NewUllmanVerify;
import frequentindex.Vertex;
import infrequentindex.CamGenerator;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author nguyenhhien
 */
public class ExFreqIndex {

	private ArrayList<ExFragment> exFreqIndex;
	private ArrayList<Graph> freqFragSet;

	public ExFreqIndex() {
		exFreqIndex = new ArrayList<ExFragment>();
		freqFragSet = new ArrayList<Graph>();
	}

	public ArrayList<ExFragment> getExFreqIndex() {
		return exFreqIndex;
	}

	public ArrayList<Graph> getFreqFragSet() {
		return freqFragSet;
	}

	public void readFreqFragSet(String fileName) {
		File fin = new File(fileName);
		String strLine;

		try {
			BufferedReader br = new BufferedReader(
					new InputStreamReader(new BufferedInputStream(new FileInputStream(fin))));

			int gId = -1; // 0-based
			int vNum = 0;
			int eNum = 0;

			// Read the list of frequent graphs;
			while ((strLine = br.readLine()).contains("idlist") == false) {
				if (strLine.contains("#")) {
					gId++;

					// read the number of vertex;
					strLine = br.readLine();
					vNum = Integer.parseInt(strLine);
					Graph tmpgraph = new Graph();
					tmpgraph.setGraphid(gId);

					// read vertex labels;
					for (int i = 0; i < vNum; i++) {
						strLine = br.readLine();
						Vertex node = new Vertex();
						node.setID(i);
						node.setLabel(strLine);
						tmpgraph.addNode(node);
					}

					// read the number of edges;
					strLine = br.readLine();
					eNum = Integer.parseInt(strLine);

					// read in the pairs of edge Ids;
					for (int i = 0; i < eNum; i++) {
						strLine = br.readLine();
						String[] strnode = strLine.split("\\s");
						int e1 = Integer.parseInt(strnode[0]);
						int e2 = Integer.parseInt(strnode[1]);
						tmpgraph.addEdge(e1, e2);
						tmpgraph.getNode(e1).incDegree();
						tmpgraph.getNode(e2).incDegree();
					}

					tmpgraph.setVertexNum(vNum);
					tmpgraph.setEdgeNum(eNum);

					if (eNum <= 2) {

						freqFragSet.add(tmpgraph);
					}
				}
			}

			// Read the list of graphs containing frequent graphs;
			if (strLine.contains("idlist")) {
				for (int idx = 0; idx < freqFragSet.size(); idx++) {
					strLine = br.readLine();
					String[] idset = strLine.split("\\s");

					for (int i = 0; i < idset.length; i++) {
						freqFragSet.get(idx).addToIdlist(Integer.parseInt(idset[i]));
					}
				}
			}
			br.close();

		} catch (Exception ex) {
			System.out.println("Exception: ");
			ex.printStackTrace();
		}

		System.out.println(freqFragSet.size() + " frequent fragments loaded!");
	}

	public void build(ExIndex exIndex) {
		NewUllmanVerify exactVerifier = new NewUllmanVerify();

		Graph curGraph;
		Graph curView;

		for (int i = 796; i < exIndex.getPatternSet().size(); i++) {
			boolean[] exist = new boolean[20];
			Arrays.fill(exist, false);

			exFreqIndex.clear();

			// Just the pattern;
			ExFragment tmpExFragment = new ExFragment();
			Graph curPattern = exIndex.getPatternSet().get(i);
			curView = getView(i);
			tmpExFragment.realCam = CamGenerator.buildCam(curPattern);
			tmpExFragment.viewCamSet.add(curView.getCam());
			tmpExFragment.vNum = curPattern.getVertexNum();

			for (int j = 0; j < exIndex.getGraphSet().size(); j++) {
				if (exactVerifier.verify(curPattern, exIndex.getGraphSet().get(j))) {
					tmpExFragment.idList.add(j);
					curPattern.addToIdlist(j);
				}
			}

			exFreqIndex.add(tmpExFragment);

			exist[curPattern.getVertexNum() + 1] = true;
			// Combine pattern with frequent fragments
			for (int j = 0; j < freqFragSet.size(); j++) {
				Graph curFrag = freqFragSet.get(j);

				if (!exist[curFrag.getVertexNum() + curPattern.getVertexNum() - 1])
					break;

				for (int ii = 0; ii < curPattern.getVertexNum(); ii++) {
					for (int jj = 0; jj < curFrag.getVertexNum(); jj++) {
						curGraph = getGraph(curPattern, ii, curFrag, jj);
						curView = getView(i, ii, curFrag, jj);

						boolean isCamNotExist = true;
						for (int k = 0; k < exFreqIndex.size(); k++) {
							if (curGraph.getCam().equals(exFreqIndex.get(k).realCam)) {
								exFreqIndex.get(k).viewCamSet.add(curView.getCam());
								isCamNotExist = false;
								break;
							}
						}
						if (isCamNotExist) {
							tmpExFragment = new ExFragment();
							tmpExFragment.vNum = curGraph.getVertexNum();
							tmpExFragment.realCam = curGraph.getCam();
							tmpExFragment.viewCamSet.add(curView.getCam());

							ArrayList<Integer> idList = new ArrayList<Integer>();
							idList.addAll(curPattern.getIdList());
							idList.retainAll(curFrag.getIdList());

							ArrayList<String> subViewCamList = getSubViewCamList(curView);
							for (int k = 0; k < exFreqIndex.size(); k++) {

								boolean isContain = false;
								for (int kk = 0; kk < subViewCamList.size(); kk++) {
									if (exFreqIndex.get(k).viewCamSet.contains(subViewCamList.get(kk))) {
										isContain = true;
										break;
									}
								}

								if (isContain) {
									idList.retainAll(exFreqIndex.get(k).idList);
								}
							}

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

								exFreqIndex.add(tmpExFragment);
								exist[tmpExFragment.vNum] = true;
							}
						} // end if isCamNotExist;
					}
				}
			}

			writeToFile(i, exFreqIndex);
		} // end for i;
	}

	public Graph getView(int patternId) {
		Graph res = new Graph();

		Vertex node = new Vertex();
		node.setID(0);
		String patternLabel = Utilities.getLabel(patternId);
		node.setLabel(patternLabel);
		res.addNode(node);

		res.setVertexNum(1);
		res.setEdgeNum(0);

		res.setCam(CamGenerator.buildCam(res));
		return res;
	}

	private Graph getGraph(Graph pattern, int patternVId, Graph frag, int fragVId) {
		int nPattern = pattern.getVertexNum();
		int nFrag = frag.getVertexNum();

		int vNum = nPattern + nFrag;
		Graph resGraph = new Graph();
		resGraph.setVertexNum(vNum);

		// Copy vertices;
		for (int i = 0; i < nPattern; i++) {
			Vertex tmpVertex = new Vertex();
			tmpVertex.setLabel(pattern.getNode(i).getLabel());
			tmpVertex.setID(i);
			resGraph.addNode(tmpVertex);
		}
		for (int i = 0; i < nFrag; i++) {
			Vertex tmpVertex = new Vertex();
			tmpVertex.setLabel(frag.getNode(i).getLabel());
			tmpVertex.setID(i + nPattern);
			resGraph.addNode(tmpVertex);
		}

		// Copy edges
		for (int i = 0; i < nPattern; i++) {
			for (int j = i + 1; j < nPattern; j++) {
				if (pattern.getEdgeLabel(i, j) == 1) {
					resGraph.addEdge(i, j);
					resGraph.getNode(i).setIn(j);
					resGraph.getNode(i).incDegree();
					resGraph.getNode(j).setIn(i);
					resGraph.getNode(j).incDegree();
				}
			}
		}
		for (int i = 0; i < nFrag; i++) {
			for (int j = i + 1; j < nFrag; j++) {
				if (frag.getEdgeLabel(i, j) == 1) {
					resGraph.addEdge(nPattern + i, nPattern + j);
					resGraph.getNode(i + nPattern).setIn(j + nPattern);
					resGraph.getNode(i + nPattern).incDegree();
					resGraph.getNode(j + nPattern).setIn(i + nPattern);
					resGraph.getNode(j + nPattern).incDegree();
				}
			}
		}
		int i = patternVId;
		int j = fragVId + nPattern;
		resGraph.addEdge(i, j);
		resGraph.getNode(i).setIn(j);
		resGraph.getNode(i).incDegree();
		resGraph.getNode(j).setIn(i);
		resGraph.getNode(j).incDegree();

		resGraph.setCam(CamGenerator.buildCam(resGraph));
		resGraph.setEdgeNum(pattern.getEdgeNum() + frag.getEdgeNum() + 1);

		return resGraph;
	}

	private Graph getView(int patternId, int patternVId, Graph frag, int fragVId) {

		int nFrag = frag.getVertexNum();
		int vNum = 2 + nFrag;
		Graph view = new Graph();
		view.setEdgeNum(2 + frag.getEdgeNum());
		view.setVertexNum(vNum);

		// Copy vertices;
		{
			Vertex tmpVertex = new Vertex();
			tmpVertex.setLabel(Utilities.getLabel(patternId));
			tmpVertex.setID(0);
			view.addNode(tmpVertex);

			Vertex tmpVertex2 = new Vertex();
			tmpVertex2.setLabel(Utilities.getLabel(patternId, patternVId));
			tmpVertex2.setID(1);
			view.addNode(tmpVertex2);
		}
		for (int i = 0; i < nFrag; i++) {
			Vertex tmpVertex = new Vertex();
			tmpVertex.setLabel(frag.getNode(i).getLabel());
			tmpVertex.setID(i + 2);
			view.addNode(tmpVertex);
		}

		// copy edges;
		view.addEdge(0, 1);
		view.getNode(0).setIn(1);
		view.getNode(0).incDegree();
		view.getNode(1).setIn(0);
		view.getNode(1).incDegree();

		view.addEdge(1, fragVId + 2);
		view.getNode(0).setIn(fragVId + 2);
		view.getNode(0).incDegree();
		view.getNode(fragVId + 2).setIn(0);
		view.getNode(fragVId + 2).incDegree();

		for (int i = 0; i < nFrag; i++) {
			for (int j = i + 1; j < nFrag; j++) {
				if (frag.getEdgeLabel(i, j) == 1) {
					view.addEdge(2 + i, 2 + j);
					view.getNode(i + 2).setIn(j + 2);
					view.getNode(i + 2).incDegree();
					view.getNode(j + 2).setIn(i + 2);
					view.getNode(j + 2).incDegree();
				}
			}
		}

		view.setCam(CamGenerator.buildCam(view));
		return view;
	}

	private ArrayList<String> getSubViewCamList(Graph curView) {
		ArrayList<String> res = new ArrayList<String>();

		for (int i = 2; i < curView.getVertexNum(); i++)
			if (curView.getNode(i).getDegree() == 1) {
				Graph subView = deleteVertex(curView, i);
				res.add(subView.getCam());
			}

		for (int i = 2; i < curView.getVertexNum(); i++) {
			for (int j = i + 1; j < curView.getVertexNum(); j++) {
				if (curView.getEdgeLabel(i, j) == 1 && !isBridge(curView, i, j)) {
					Graph subView = deleteEdge(curView, i, j);
					res.add(subView.getCam());
				}
			}
		}
		return res;
	}

	private Graph deleteVertex(Graph curView, int vId) {
		int vNum = curView.getVertexNum();
		Graph resGraph = new Graph();
		resGraph.setEdgeNum(curView.getEdgeNum() - 1);
		resGraph.setVertexNum(vNum - 1);

		// Copy vertices;
		for (int i = 0; i < vNum; i++)
			if (i != vId) {
				Vertex tmpVertex = new Vertex();
				tmpVertex.setLabel(curView.getNode(i).getLabel());
				int ii = (i > vId) ? (i - 1) : i;
				tmpVertex.setID(ii);
				resGraph.addNode(tmpVertex);
			}

		// Copy edges;
		for (int i = 0; i < vNum; i++)
			if (i != vId) {
				for (int j = i + 1; j < vNum; j++)
					if (j != vId) {
						if (curView.getEdgeLabel(i, j) == 1) {
							int ii = (i > vId) ? (i - 1) : i;
							int jj = (j > vId) ? (j - 1) : j;
							resGraph.addEdge(ii, jj);
							resGraph.getNode(ii).setIn(jj);
							resGraph.getNode(ii).incDegree();
							resGraph.getNode(jj).setIn(ii);
							resGraph.getNode(jj).incDegree();
						}
					}
			}

		resGraph.setCam(CamGenerator.buildCam(curView));
		return resGraph;
	}

	private Graph deleteEdge(Graph curView, int vId1, int vId2) {
		if (vId1 > vId2) {
			int tmp = vId1;
			vId1 = vId2;
			vId2 = tmp;
		}

		int vNum = curView.getVertexNum();
		Graph resGraph = new Graph();
		resGraph.setEdgeNum(curView.getEdgeNum() - 1);
		resGraph.setVertexNum(vNum);

		// Copy vertices;
		for (int i = 0; i < vNum; i++) {
			Vertex tmpVertex = new Vertex();
			tmpVertex.setLabel(curView.getNode(i).getLabel());
			tmpVertex.setID(i);
			resGraph.addNode(tmpVertex);
		}

		// Copy edges;
		for (int i = 0; i < vNum; i++) {
			for (int j = i + 1; j < vNum; j++) {
				if (curView.getEdgeLabel(i, j) == 1 && (i != vId1 || j != vId2)) {
					resGraph.addEdge(i, j);
					resGraph.getNode(i).setIn(j);
					resGraph.getNode(i).incDegree();
					resGraph.getNode(j).setIn(i);
					resGraph.getNode(j).incDegree();
				}
			}
		}

		resGraph.setCam(CamGenerator.buildCam(curView));
		return resGraph;
	}

	private boolean isBridge(Graph g, int vId1, int vId2) {

		boolean[] visited = new boolean[g.getVertexNum()];
		Arrays.fill(visited, false);
		Queue<Integer> qu = new LinkedList<Integer>();

		qu.offer(vId1);
		visited[vId1] = true;

		while (!qu.isEmpty()) {
			int v1 = qu.poll();
			for (int v2 = 0; v2 < g.getVertexNum(); v2++)
				if (!visited[v2] && g.getEdgeLabel(v1, v2) == 1) {
					if (v1 != vId1 || v2 != vId2) {
						visited[v2] = true;
						qu.offer(v2);
					}
				}
		}

		if (!visited[vId2])
			return true;
		else
			return false;
	}

	private void writeToFile(int patternId, ArrayList<ExFragment> exFreqIndex) {
		String fileName = "ExtendedIndex/e_" + patternId + "_FIndex";
		File fout = new File(fileName);

		PrintStream ps;
		try {
			ps = new PrintStream(new BufferedOutputStream(new FileOutputStream(fout)));

			for (int i = 0; i < exFreqIndex.size(); i++) {
				ps.println(exFreqIndex.get(i).realCam);

				ArrayList<String> viewCamSet = exFreqIndex.get(i).viewCamSet;
				for (int j = 0; j < viewCamSet.size(); j++)
					ps.print(viewCamSet.get(j) + " ");
				ps.println();

				ArrayList<Integer> idList = exFreqIndex.get(i).idList;
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

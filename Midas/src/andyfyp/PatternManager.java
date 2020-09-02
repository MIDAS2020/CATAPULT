/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package andyfyp;

import andyfyp.PatternIndex.PatternNode;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 *
 * @author Andy
 */
public class PatternManager {

	public static ArrayList<Pattern> patternSet;
	public static int[][] scores;
	private static final int coeff = 1;
	private static final int[] numPerRow = { -1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	private static final int Width = 150;
	public static Hashtable idSetCache = new Hashtable();
	public static HashMap<String, Object[][]> thumbnailTables = new HashMap<String, Object[][]>();
	public static ArrayList<ArrayList<Integer>> idSet = new ArrayList<ArrayList<Integer>>();
	PatternSetInfo patternSetInfo;

	PrintWriter patternsStructPrinter;
	PrintWriter patternsIndexPrinter;
	PrintWriter vertexLocPrinter;
	String freqImageFolderPath;

	ArrayList<ArrayList<Integer>> patternSizeNumPerRowMap;

	public static PatternIndex patternIndex;

	public PatternManager(PatternSetInfo patternSetInfo, PatternIndex patternIndex, ArrayList<ArrayList<Integer>> map) {
		this.patternSetInfo = patternSetInfo;
		this.patternIndex = patternIndex;
		patternSizeNumPerRowMap = map;
	}

	public void preparePatterns() {
		/*
		 * Load structural infomation of the patterns including vertices' locations of
		 * each pattern
		 */
		// loadPatterns();
		// loadScores();
		loadThumbnails();
	}

	private void loadPatterns() {
		patternSet = new ArrayList<Pattern>();

		// IO handlers;
		String strLine;
		File patternsFile = new File("patterns/patterns");
		File vertexLocFile = new File("patterns/vertex_locations");

		if (patternsFile.exists()) {
			try {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(new DataInputStream(new FileInputStream(patternsFile))));

				Pattern tmpPattern = null;
				int patternId = -1;
				int edgeNum = 0;

				while ((strLine = br.readLine()) != null) {

					// 1.Beginning line of a graph data;
					if (strLine.contains("t #")) {
						++patternId;
						edgeNum = 0;

						String[] str = strLine.split("\\s");
						tmpPattern = new Pattern(Integer.parseInt(str[3]));
						tmpPattern.ID = patternId;
					}

					// 2.Read in a vertex
					else if (strLine.contains("v")) {
						PatternVertex vertex = new PatternVertex();

						String[] vertexLine = strLine.split("\\s");
						vertex.Id = Integer.parseInt(vertexLine[1]);
						vertex.labelId = vertexLine[2];
						vertex.addToGraphList(patternId);
						tmpPattern.addVertex(vertex, vertex.Id);
					}

					// 3.Read in an edge;
					else if (strLine.contains("e")) {
						++edgeNum;
						String[] edgeLine = strLine.split("\\s");
						int v1 = Integer.parseInt(edgeLine[1]);
						int v2 = Integer.parseInt(edgeLine[2]);
						String edgeLabel = edgeLine[3];

						tmpPattern.addEdge(edgeNum - 1, v1, v2, edgeLabel);
						tmpPattern.increaseVertexDegree(v1);
						tmpPattern.increaseVertexDegree(v2);
					}

					// 4.Empty line => end of a graph;
					else {
						tmpPattern.edgeNum = edgeNum;
						patternSet.add(tmpPattern);
					}
				} // end while;

				br.close();

				System.out.println(patternSet.size() + " DnD patterns loaded");
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			if (vertexLocFile.exists()) {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new DataInputStream(new FileInputStream(vertexLocFile))));
					while ((strLine = br.readLine()) != null) {
						strLine = strLine.trim();
						int idx = Integer.parseInt(strLine.substring(0, strLine.length() - 1));
						strLine = br.readLine();
						String[] nums = strLine.split("\\s");

						for (int i = 0; 2 * i < nums.length; i++) {
							patternSet.get(idx).vertexSet[i].x = Integer.parseInt(nums[2 * i]);
							patternSet.get(idx).vertexSet[i].y = Integer.parseInt(nums[2 * i + 1]);
						}

					} // end while;

					br.close();

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void loadScores() {
		scores = new int[patternSet.size()][2];

		File freqencyFile = new File("patterns/frequency");
		BufferedReader br;
		if (freqencyFile.exists()) {
			try {
				String strLine;
				br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(freqencyFile))));

				while ((strLine = br.readLine()) != null) {
					String[] nums = strLine.split("\\s");

					int i = Integer.parseInt(nums[0].substring(0, nums[0].length() - 1));
					int score0 = Integer.parseInt(nums[1]);
					int score1 = Integer.parseInt(nums[2]);

					scores[i][0] = score0;
					scores[i][1] = score1;
				}
				br.close();
				System.out.println("Frequency scores loaded!");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void loadThumbnails() {
		String imageFolderPath;

		for (int i = patternSetInfo.getPatternSizeMin(); i <= patternSetInfo.getPatternSizeMax(); i++) {
			imageFolderPath = "patterns/thumbnails/S" + i;
			Object[][] thumbnailTable = loadImagesFromFolder(imageFolderPath);

			thumbnailTables.put("S" + i, thumbnailTable);
		}

		System.out.println("Pattern images loaded!");
	}

	private Object[][] loadImagesFromFolder(String _freqFolderPath) {

		File file = new File(_freqFolderPath);
		File[] fileList = file.listFiles();

		Object[][] imageArray = new Object[fileList.length / numPerRow[1]][numPerRow[1]];

		ArrayList<Pair> sortedIds = new ArrayList<Pair>();
		for (int i = 0; i < fileList.length; i++) {

			int patId = Integer.parseInt(fileList[i].getName().substring(0, fileList[i].getName().length() - 4));
			int score = scores[patId][0] + coeff * scores[patId][1];

			Pair tmp = new Pair(score, i);

			sortedIds.add(tmp);
		}
		Collections.sort(sortedIds);

		for (int i = 0; i < imageArray.length; i++) {
			for (int j = 0; j < imageArray[0].length; j++) {

				int iconIndex = i * numPerRow[1] + j;
				int imageIndex = sortedIds.get(iconIndex).v;

				String idStr = fileList[imageIndex].getName().substring(0, fileList[imageIndex].getName().length() - 4);
				int patId = Integer.parseInt(idStr);
				DnDImage tmpButton = new DnDImage(fileList[imageIndex].getAbsolutePath(), patId);
				imageArray[i][j] = tmpButton;
			}
		}

		return imageArray;
	}

	public static ThumbnailTable getTable(String patternType, int columnSize) {
		numPerRow[1] = columnSize;
		Object[] columnNames = new Object[numPerRow[1]];
		// Object[] columnNames = new Object[3];
		for (int i = 0; i < columnNames.length; i++) {
			columnNames[i] = "";
		}

		Object[][] data = thumbnailTables.get(patternType);

		ThumbnailTable table = new ThumbnailTable(columnNames, data, Width / numPerRow[1]);
		// ThumbnailTable table = new ThumbnailTable(columnNames, data, Width /4);
		return table;
	}

	public static ThumbnailTable getTable(String patternType) {
		Object[] columnNames = new Object[numPerRow[1]];
		// Object[] columnNames = new Object[3];
		for (int i = 0; i < columnNames.length; i++) {
			columnNames[i] = "";
		}

		Object[][] data = thumbnailTables.get(patternType);

		ThumbnailTable table = new ThumbnailTable(columnNames, data, Width / numPerRow[1]);
		// ThumbnailTable table = new ThumbnailTable(columnNames, data, Width /4);
		return table;
	}

	public static boolean isLoaded(String patternType) {

		return !thumbnailTables.isEmpty();
	}

	public static ArrayList<Integer> getIdSet(int patId) {
		if (idSetCache.containsKey(patId)) {
			return (ArrayList<Integer>) idSetCache.get(patId);
		}

		ArrayList<Integer> ans = new ArrayList<Integer>();

		File fin = new File("images/IdSet");
		BufferedReader br;
		try {
			String strLine;
			br = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(fin))));

			for (int i = 0; i < patId; i++) {
				br.readLine();
				br.readLine();
			}

			strLine = br.readLine();
			System.out.println(patId + "v.s" + strLine);

			strLine = br.readLine();
			String[] nums = strLine.split("\\s");
			for (int i = 0; i < nums.length; i++) {
				ans.add(Integer.parseInt(nums[i]));
			}
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		idSetCache.put(patId, ans);
		return ans;
	}

	public static HashSet<Integer> getExactSubCandidates(ArrayList<Pattern> usedPatternSet) {
		HashSet<Integer> candidateSet = new HashSet<Integer>();

		for (int i = 0; i < usedPatternSet.size(); i++) {
			int patternID = usedPatternSet.get(i).ID;

			if (candidateSet.isEmpty()) {
				candidateSet.addAll(getCandidatesFromPatternID(patternID));
			} else {
				candidateSet.retainAll(getCandidatesFromPatternID(patternID));
			}
		}

		return candidateSet;
	}

	public static HashSet<Integer> getCandidatesFromPatternID(int patternID) {
		HashSet<Integer> patternCandidateSet = new HashSet<Integer>();

		Queue<PatternNode> queue = new LinkedList<PatternNode>();
		queue.add(patternIndex.getNodeList().get(patternID));

		while (!queue.isEmpty()) {
			PatternNode patternNode = queue.poll();

			patternCandidateSet.addAll(patternNode.delIDList);

			for (Iterator iter = patternNode.childrenList.iterator(); iter.hasNext();) {
				int chilePatternID = (Integer) iter.next();

				queue.add(patternIndex.getNodeList().get(chilePatternID));
			}
		}

		/*
		 * System.out.println(); for (Iterator iter = patternCandidateSet.iterator();
		 * iter.hasNext();) { int chilePatternID = (Integer)iter.next();
		 * 
		 * System.out.print(chilePatternID + " "); } System.out.println();
		 */

		return patternCandidateSet;
	}
}

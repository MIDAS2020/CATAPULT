/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package similarity;

import adjlistgraph.Graph;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import frequentindex.Vertex;

/**
 *
 * @author c4jin
 */
public class SimTest {
	public static void main(String[] args) throws FileNotFoundException {
		formatDotFile("newtest.txt", 1);
	}

	/**********************************************************************/
	public static void formatDotFile(String filename, int missededge) {
		Vector<Graph> graphlist = new Vector<Graph>();
		Graph tmpgraph = null;

		String strLine = null;
		FileInputStream inStream;
		DataInputStream in;
		BufferedReader br;

		File fin = new File(filename);

		if (fin.exists()) {
			try {

				inStream = new FileInputStream(fin);
				// Connect print stream to the output stream
				// Get the object of DataInputStream
				in = new DataInputStream(inStream);
				br = new BufferedReader(new InputStreamReader(in));
				// Read file line by line
				try {

					int graphnum = -1;
					int edgenum = 0;

					while ((strLine = br.readLine()) != null) {
						// Print the content on the console
						if (strLine.contains("t #")) {
							String[] line = strLine.split("\\s");
							tmpgraph = new Graph();
							tmpgraph.setVertexNum(Integer.parseInt(line[3]));
							graphnum++;
							edgenum = 0;
						} else if (strLine.contains("v")) {
							String[] line = strLine.split("\\s");
							Vertex node = new Vertex();
							node.setLabel(line[2]);
							tmpgraph.addNode(node);
						} else if (strLine.contains("e")) {
							String[] line = strLine.split("\\s");
							int s = Integer.parseInt(line[1]);
							int t = Integer.parseInt(line[2]);

							tmpgraph.addEdge(s, t);
							tmpgraph.getNode(s).incDegree();
							tmpgraph.getNode(t).incDegree();

							tmpgraph.getNode(s).setIn(t);
							tmpgraph.getNode(t).setIn(s);

							edgenum++;
						} else {
							tmpgraph.setEdgeNum(edgenum);
							graphlist.addElement(tmpgraph);
						}

					}

					SimVerify sv = new SimVerify(graphlist.elementAt(0));

					long t1 = System.currentTimeMillis();
					if (sv.verify(graphlist.elementAt(0), graphlist.elementAt(3), missededge)) {
						System.out.println(graphlist.elementAt(3).getVertexNum() + " Correct!");
					} else {
						System.out.println(graphlist.elementAt(3).getVertexNum() + "Fail!");
					}

					long t2 = System.currentTimeMillis() - t1;
					System.out.println(t2 + " ms");
					// Close the input stream
					in.close();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}

package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import graph.JGraphtClosureGraph;
import graph.VF2;
import graph.VF2_Match;
import graph.closureEdge;

public class GenerateQueryWithout6Carbon {
	public static void main(String[] args) {
		patternrefresher gen = new patternrefresher();
		ArrayList<Integer> graphIdList = new ArrayList<Integer>();
		String filename1 = "AIDS40k";
		
		int position=0;
        String[] bufstring=new String[1024];
        BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("C:\\Users\\Kai\\Desktop\\text.txt"));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        String line=null;
        try {
			while((line=br.readLine())!=null) {
				bufstring[position]=line;
				position++;
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			br.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        ArrayList<Integer> IDS = new ArrayList<Integer>();
        for(String s : bufstring) {
        	if(s==null) continue;
        	System.out.println(s);
        	int id = Integer.valueOf(s);
        	/////////////////////////////////////////
        	IDS.add(id-1);
        	/////////////////////////////////////////
        }
        
		for(int i=0;i < 40000;i++)  {
			graphIdList.add(i);
		}
		ArrayList<JGraphtClosureGraph> UpdatePatterns2 = gen.readJGraphtClosureGraphFromFile(filename1, graphIdList, false);
		
		ArrayList<JGraphtClosureGraph> UpdatePatterns = new ArrayList<JGraphtClosureGraph>();
		for(int i=0;i<IDS.size();i++) {
			UpdatePatterns.add(UpdatePatterns2.get(IDS.get(i)));
		}
		
		ArrayList<ArrayList<closureEdge>> UpPatterns = new ArrayList<ArrayList<closureEdge>>();
		for (int i = 0; i < 1000; i++)
			UpPatterns.add(gen.transJGraphtClosureGraphToclosureEdges(UpdatePatterns.get(i)));
		// process database-END
		String pathToSaveImage = "C://path//";

		try {
			gen.PM_savePatternsToFile(pathToSaveImage, UpPatterns);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main2(String[] args) {
		patternrefresher gen = new patternrefresher();
		ArrayList<Integer> graphIdList = new ArrayList<Integer>();
		String filename1 = "SixCarbon";
		for(int i=0;i < 1;i++)  graphIdList.add(i);
		ArrayList<JGraphtClosureGraph> sixcarbon = gen.readJGraphtClosureGraphFromFile(filename1, graphIdList, false);
		ArrayList<JGraphtClosureGraph>  UpdatePatterns = new ArrayList<JGraphtClosureGraph>();
		graphIdList.clear();
		String filename2 = "AIDS40k";
		for(int i=0;i<40000;i++)  graphIdList.add(i);
		ArrayList<JGraphtClosureGraph> graphs = gen.readJGraphtClosureGraphFromFile(filename2, graphIdList, false);
		 VF2 VF2_operator = new VF2();
		for(int i=0; i< 20000;i++) {
			JGraphtClosureGraph  datagraph = graphs.get(i);
		    for(int j=0; j<sixcarbon.size();j++) {
		    	    //System.out.println("sixcarbon.size(): "+sixcarbon.size());
				    JGraphtClosureGraph querygraph = sixcarbon.get(j);
					VF2_Match matchedPairs = new VF2_Match();
					matchedPairs = VF2_operator.doVF2(matchedPairs, querygraph, datagraph, false);
					if (matchedPairs == null)
						continue;
					if (matchedPairs.getBestNumMatch() != querygraph.getClosureVertexList().size())
					{
						UpdatePatterns.add(datagraph);
						System.out.println(i + " ");
					}
				  
		     }
		}
		
		ArrayList<ArrayList<closureEdge>> UpPatterns = new ArrayList<ArrayList<closureEdge>>();
		for (int i = 0; i < 1000; i++)
			UpPatterns.add(gen.transJGraphtClosureGraphToclosureEdges(UpdatePatterns.get(i)));
		// process database-END
		String pathToSaveImage = "C://path//";

		try {
			gen.PM_savePatternsToFile(pathToSaveImage, UpPatterns);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

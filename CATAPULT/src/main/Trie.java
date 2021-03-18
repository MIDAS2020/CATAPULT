package main;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import graph.JGraphtClosureGraph;

public class Trie {
    private TrieNode root;
    
    private ArrayList <JGraphtClosureGraph> fct = new ArrayList <JGraphtClosureGraph> ();
    private ArrayList<ArrayList<Integer>>   containedGraphIDs  =  new ArrayList<ArrayList<Integer>>();
    private ArrayList<ArrayList<Integer>>   containedTimesofGraph = new ArrayList<ArrayList<Integer>>();
    private ArrayList<String>               edges  =  new ArrayList<String>  ();
    private ArrayList<Boolean>               isPromisingEgde  = new  ArrayList<Boolean> () ;
    private ArrayList<ArrayList<Integer>>   containedGraphIDs_ByEdges = new ArrayList<ArrayList<Integer>>();
    private ArrayList<ArrayList<Integer>>   containedTimesofGraph_ByEdges = new ArrayList<ArrayList<Integer>>();
    private boolean USEIGED;
    private boolean USEINDEX;
    private boolean USEEDGEPRUNE; 
    private double  MINSCOV;
    
    
    public Trie() {
        root = new TrieNode();
        setFct(new  ArrayList <JGraphtClosureGraph> ());
    	setContainedGraphIDs(new ArrayList<ArrayList<Integer>>  ());
    	setContainedTimesofGraph(new ArrayList<ArrayList<Integer>> ());
    	setContainedGraphIDs_ByEdges(new ArrayList<ArrayList<Integer>>  ());
 		setContainedTimesofGraph_ByEdges(new ArrayList<ArrayList<Integer>>  ());
    }
    public void  update(PatternResult existingpr ,  double kappa, ArrayList<Integer> graphIdList ) {
		ArrayList<ArrayList<Integer>> ExistedCoveredGraphIds = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < existingpr.getSubgraphcoverage().size(); i++) {
			ExistedCoveredGraphIds.add(existingpr.getCoveredgraphIDs().get(i));
		}
		//System.out.println(" existingpr.getSubgraphcoverage().size(): "+ existingpr.getSubgraphcoverage().size());
		//System.out.println("ExistedCoveredGraphIds.size(): "+ ExistedCoveredGraphIds.size());
		//System.out.println("ExistedCoveredGraphIds: "+ ExistedCoveredGraphIds.toString());
		
		///// 1. get minimum loss score 
		double  minimumLossScore = 100000;
		for (int i = 0; i < ExistedCoveredGraphIds.size(); i++) {
			/// get loss score for i 
			double lossscore =  0;
			ArrayList<Integer> coveredids = new ArrayList<Integer> ();
			for(int j=0;j<ExistedCoveredGraphIds.size();j++) {
				if(j == i ) continue;
				ArrayList<Integer>  ids = ExistedCoveredGraphIds.get(j);
				for(int k=0;k<ids.size();k++)
					coveredids.add(ids.get(k));
			}
			ArrayList<Integer> existedids = ExistedCoveredGraphIds.get(i);
			//System.out.println("existedids: " + existedids.toString());
			//System.out.println("coveredids: " + coveredids.toString());
			double tempcount = 0;
			for(int k=0;k<existedids.size();k++) {
			    if(coveredids.indexOf(existedids.get(k)) == -1) {
			    	tempcount++;
			    }
			}
			//System.out.println("LossScore: " + tempcount);
			if(tempcount < minimumLossScore)  minimumLossScore = tempcount;
		}
		
		//System.out.println("minimumLossScore: " + minimumLossScore);
		
    	////// 2. get benefit score
		Set<Integer> set = new HashSet<Integer>();
		for(int i=0;i< ExistedCoveredGraphIds.size();i++) {
			ArrayList<Integer> ids = ExistedCoveredGraphIds.get(i);
			for(int j=0;j<ids.size();j++) {
				set.add(ids.get(j));
			}
		}
		for(int i =0;i<containedGraphIDs_ByEdges.size();i++) {
			ArrayList<Integer> coveredids = containedGraphIDs_ByEdges.get(i);
			Set<Integer> set1 = new HashSet<Integer>();
			for(int j=0;j<coveredids.size();j++) {
				int id = coveredids.get(j);
				if(graphIdList.indexOf(id) != -1) set1.add(id);
			}
			set1.retainAll(set);
			int size = set1.size();
			if(size  <= (1+kappa) * minimumLossScore) {
				isPromisingEgde.add(false);
			}else
				isPromisingEgde.add(true);
		}
		//System.out.println("Egde: " + this.edges.toString());
		//System.out.println("isPromisingEgde: " + isPromisingEgde.toString());
    }
    
    public void insert(String[] words, ArrayList<Integer>  containedGraphIDs, ArrayList<Integer>  containedTimesofGraph) {
        TrieNode current = root;
       // String[] words = word.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            current = current.getChildren().computeIfAbsent(words[i], c -> new TrieNode());
        }
        current.setEndOfWord(true);
        current.setContainedGraphIDs(containedGraphIDs);
        current.setContainedTimesofGraph(containedTimesofGraph);
    }

    public  boolean delete(String[] words) {
        return delete(root, words, 0);
    }
   
    public    ArrayList<Integer>  findGraphsIDFromTrie(String[] words) {
        TrieNode current = root;
        for (int i = 0; i < words.length; i++) {
            String ch = words[i];
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return  new ArrayList<Integer>();
            }
            current = node;
        }
        return current.getContainedGraphIDs();
    }
    
    public    ArrayList<Integer>  findTimesOfGraphFromTrie(String[] words) {
        TrieNode current = root;
        for (int i = 0; i < words.length; i++) {
            String ch = words[i];
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return  new ArrayList<Integer>();
            }
            current = node;
        }
        return current.getContainedTimesofGraph();
    }
    
    
    public  boolean containsNode(String[] words) {
        TrieNode current = root;
        
        for (int i = 0; i < words.length; i++) {
            String ch = words[i];
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord();
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean delete(TrieNode current, String[] words, int index) {
    	 
        if (index == words.length) {
            if (!current.isEndOfWord()) {
                return false;
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }
        String ch = words[index];
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, words, index + 1) && !node.isEndOfWord();

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }
        return false;
    }

	public ArrayList <JGraphtClosureGraph> getFct() {
		return fct;
	}

	public void setFct(ArrayList <JGraphtClosureGraph> fct) {
		this.fct = fct;
	}

	public ArrayList<ArrayList<Integer>> getContainedGraphIDs() {
		return containedGraphIDs;
	}

	public void setContainedGraphIDs(ArrayList<ArrayList<Integer>> containedGraphIDs) {
		this.containedGraphIDs = containedGraphIDs;
	}

	public ArrayList<ArrayList<Integer>> getContainedTimesofGraph() {
		return containedTimesofGraph;
	}

	public void setContainedTimesofGraph(ArrayList<ArrayList<Integer>> containedTimesofGraph) {
		this.containedTimesofGraph = containedTimesofGraph;
	}

	public ArrayList<ArrayList<Integer>> getContainedGraphIDs_ByEdges() {
		return containedGraphIDs_ByEdges;
	}

	public void setContainedGraphIDs_ByEdges(ArrayList<ArrayList<Integer>> containedGraphIDs_ByEdges) {
		this.containedGraphIDs_ByEdges = containedGraphIDs_ByEdges;
	}

	public ArrayList<ArrayList<Integer>> getContainedTimesofGraph_ByEdges() {
		return containedTimesofGraph_ByEdges;
	}

	public void setContainedTimesofGraph_ByEdges(ArrayList<ArrayList<Integer>> containedTimesofGraph_ByEdges) {
		this.containedTimesofGraph_ByEdges = containedTimesofGraph_ByEdges;
	}

	public boolean isUSEIGED() {
		return USEIGED;
	}

	public void setUSEIGED(boolean uSEIGED) {
		USEIGED = uSEIGED;
	}

	public boolean isUSEINDEX() {
		return USEINDEX;
	}

	public void setUSEINDEX(boolean uSEINDEX) {
		USEINDEX = uSEINDEX;
	}

	public boolean isUSEEDGEPRUNE() {
		return USEEDGEPRUNE;
	}

	public void setUSEEDGEPRUNE(boolean uSEEDGEPRUNE) {
		USEEDGEPRUNE = uSEEDGEPRUNE;
	}

	public double getMINSCOV() {
		return MINSCOV;
	}

	public void setMINSCOV(double mINSCOV) {
		MINSCOV = mINSCOV;
	}

	public ArrayList<String> getEdges() {
		return edges;
	}

	public void setEdges(ArrayList<String> edges) {
		this.edges = edges;
	}
	public ArrayList<Boolean> getIsPromisingEgde() {
		return isPromisingEgde;
	}
	public void setIsPromisingEgde(ArrayList<Boolean> isPromisingEgde) {
		this.isPromisingEgde = isPromisingEgde;
	}
}
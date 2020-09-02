package main;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class TrieNode {
    private final Map<String, TrieNode> children = new HashMap<>();
    private boolean endOfWord;
    private ArrayList<Integer>  containedGraphIDs;
    private ArrayList<Integer>  containedTimesofGraph;
    
    TrieNode(){
    	endOfWord = false;
    	containedGraphIDs = new  ArrayList<Integer> ();
    	containedTimesofGraph= new  ArrayList<Integer> ();
    }
     
    Map<String, TrieNode> getChildren() {
        return children;
    }

    boolean isEndOfWord() {
        return endOfWord;
    }
   
    void setEndOfWord(boolean endOfWord) {
        this.endOfWord = endOfWord;
    }

	public ArrayList<Integer> getContainedGraphIDs() {
		return containedGraphIDs;
	}

	public void setContainedGraphIDs(ArrayList<Integer> containedGraphIDs) {
		this.containedGraphIDs = containedGraphIDs;
	}

	public ArrayList<Integer> getContainedTimesofGraph() {
		return containedTimesofGraph;
	}

	public void setContainedTimesofGraph(ArrayList<Integer> containedTimesofGraph) {
		this.containedTimesofGraph = containedTimesofGraph;
	}
}
package main;

/**
 * 
 * @author David Rankin
 *
 */
public class Processor {

	private int[][] L1Cache;
	private String name;
	
	public int[][] getL1Cache() {
		return L1Cache;
	}

	public String getName() {
		return name;
	}

	public Processor(String name, int linesInCache, int wordsInLine){
		
		L1Cache = new int[linesInCache][wordsInLine];
		this.name = name;
	}
	
	public String toString(){
		
		return name;
		
	}
	
}

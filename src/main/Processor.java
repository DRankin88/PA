package main;

/**
 * 
 * @author David Rankin
 *
 */
public class Processor {

	private int[][] L1Cache;
	private String name;
	private int cacheMisses;
	private int cacheHits;

	public int[][] getL1Cache() {
		return L1Cache;
	}

	public String getName() {
		return name;
	}

	public Processor(String name, int linesInCache, int wordsInLine){

		L1Cache = new int[linesInCache][2];
		this.name = name;

	}

	public void performRead(int mainMemoryLine, int cacheLine, int tag){

		// Get the MSIbit and the tag out of the cache for this line
		int MSIbit = L1Cache[cacheLine][0];
		int cachedTag = L1Cache[cacheLine][1];

		// Read miss and the block is invalid
		if (cachedTag != tag && MSIbit == 0){

			// Copy the block in and set the tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to shared
			L1Cache[cacheLine][0] = 1;

		}

		// Read hit and the block is shared
		else if (cachedTag == tag && MSIbit == 1){
			//Do nothing special locally
		}

		// Read miss and block is shared
		else if (cachedTag != tag && MSIbit == 1){
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;

		}
		// Read hit and block is modified
		else if (cachedTag == tag && MSIbit == 2){
			// Do nothing special locally


		}
		// Read miss and block is modified
		else if (cachedTag != tag && MSIbit == 2){
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to shared
			L1Cache[cacheLine][0] = 1;			
		}	
	}

	public void performWrite(int mainMemoryLine, int cacheLine, int tag){

		// Get the MSIbit and the tag out of the cache for this line
		int MSIbit = L1Cache[cacheLine][0];
		int cachedTag = L1Cache[cacheLine][1];

		// Tag is correct but block is invalid so write miss
		if (cachedTag == tag && MSIbit == 0){
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	

		}
		// Tag is incorrect and block is invalid so write miss
		else if (cachedTag != tag && MSIbit == 0){
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	

		}

		// Tag is correct and block is in shared state so write hit
		else if (cachedTag == tag && MSIbit == 1){
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	

		}

		// Block in shared state but tag is incorrect so write miss
		else if (cachedTag != tag && MSIbit == 1){
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	

		}
		// Tag is incorrect and block is modified is a write miss
		else if (cachedTag != tag && MSIbit == 2){
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			
		}
		// Tag is correct and block is modified so write hit
		else if (cachedTag == tag && MSIbit == 2){
			// Do nothing special locally
		}
	}

	public String toString(){

		return name;

	}

}

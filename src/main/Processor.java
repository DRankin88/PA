package main;

/**
 * 
 * @author David Rankin
 *
 */
public class Processor {

	public int[][] L1Cache;
	private String name;
	public int readMisses;
	public int readHit;
	public int writeMisses;
	public int writeHit;
	public int coherenceMiss;
	public int[] accessedLine;
	public int uniqueAccesses;

	public int[][] getL1Cache() {
		return L1Cache;
	}

	public String getName() {
		return name;
	}

	public Processor(String name, int linesInCache, int wordsInLine){

		L1Cache = new int[linesInCache][3];
		this.name = name;
		this.accessedLine = new int[linesInCache];

	}

	public void performRead(int mainMemoryLine, int cacheLine, int tag){

		accessedLine[cacheLine]++;
		// Get the MSIbit and the tag out of the cache for this line
		int MSIbit = L1Cache[cacheLine][0];
		int cachedTag = L1Cache[cacheLine][1];

		// Read miss and the block is invalid
		if (cachedTag != tag && MSIbit == 0){

			readMisses++;
			// Copy the block in and set the tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to shared
			L1Cache[cacheLine][0] = 1;
			busSnoop(0, cacheLine, tag);

		}

		// Read miss and block is invalid
		if (cachedTag == tag && MSIbit == 0){

			// Is this because of coherence
			if (L1Cache[cacheLine][2] == 1){
				coherenceMiss++;
				L1Cache[cacheLine][2] = 0;
			}
			readMisses++;
			// Copy the block in and set the tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to shared
			L1Cache[cacheLine][0] = 1;
			busSnoop(0, cacheLine, tag);

		}

		// Read hit and the block is shared
		else if (cachedTag == tag && MSIbit == 1){
			//Do nothing special locally
			readHit++;
		}

		// Read miss and block is shared
		else if (cachedTag != tag && MSIbit == 1){

			readMisses++;
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			busSnoop(0, cacheLine, tag);

		}

		// Read hit and block is modified
		else if (cachedTag == tag && MSIbit == 2){
			// Do nothing special locally
			readHit++;

		}

		// Read miss and block is modified
		else if (cachedTag != tag && MSIbit == 2){

			readMisses++;
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to shared
			L1Cache[cacheLine][0] = 1;
			busSnoop(0, cacheLine, tag);

		}	
	}

	public void performWrite(int mainMemoryLine, int cacheLine, int tag){

		accessedLine[cacheLine]++;
		// Get the MSIbit and the tag out of the cache for this line
		int MSIbit = L1Cache[cacheLine][0];
		int cachedTag = L1Cache[cacheLine][1];

		// Tag is correct but block is invalid so write miss
		if (cachedTag == tag && MSIbit == 0){

			if (L1Cache[cacheLine][2] == 1){
			
				coherenceMiss++;
				L1Cache[cacheLine][2] = 0;
			
			}

			writeMisses++;
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	
			busSnoop(1, cacheLine, tag);

		}

		// Tag is incorrect and block is invalid so write miss
		else if (cachedTag != tag && MSIbit == 0){

			writeMisses++;
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	
			busSnoop(1, cacheLine, tag);

		}

		// Tag is correct and block is in shared state so write miss
		else if (cachedTag == tag && MSIbit == 1){

			writeMisses++;
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	
			busSnoop(1, cacheLine, tag);
		
		}

		// Block in shared state but tag is incorrect so write miss
		else if (cachedTag != tag && MSIbit == 1){

			writeMisses++;
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to modified
			L1Cache[cacheLine][0] = 2;	
			busSnoop(1, cacheLine, tag);

		}

		// Tag is incorrect and block is modified is a write miss
		else if (cachedTag != tag && MSIbit == 2){

			writeMisses++;
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			busSnoop(1, cacheLine, tag);

		}

		// Tag is correct and block is modified so write hit
		else if (cachedTag == tag && MSIbit == 2){
			writeHit++;
			// Do nothing special locally
		}
	}

	public void busSnoop(int typeOfTransaction, int cacheline, int tag){
		// tOt; 0 = remote read miss, 1 = remote write miss

		Processor[] processors = Main.arrayOfProcessors;

		for (Processor processor : processors){

			if (processor.getName().equals(name)){

				continue;

			}

			int MSIbit = processor.getL1Cache()[cacheline][0];
			int localTag = processor.getL1Cache()[cacheline][1];

			if(tag != localTag){

				continue;

			}

			if (typeOfTransaction == 0){

				if (MSIbit == 2){

					//modified state and remote read miss
					processor.L1Cache[cacheline][0] = 1;

				}

				else if (MSIbit == 1){

					//shared state and remote read miss
					processor.L1Cache[cacheline][0] = 1;

				}
			}
			if (typeOfTransaction == 1){

				if (MSIbit == 2){

					//remote write miss in modified state
					processor.L1Cache[cacheline][0] = 0;
					processor.L1Cache[cacheline][2] = 1;

				}

				else if (MSIbit == 1){

					//remote write miss in shared state
					processor.L1Cache[cacheline][0] = 0;
					processor.L1Cache[cacheline][2] = 1;

				}
			}
		}
	}

	public void printReport(){

		StringBuilder report = new StringBuilder();

		report.append("Processor name: " + name + "\n\n");
		int totalReads = readMisses + readHit;
		report.append("Total Reads: " + totalReads + "\n");
		report.append("Total Read Hits: " + readHit + "\n");
		report.append("Total Read Misses: " + readMisses + "\n");
		double readPercentage = (double) readMisses/totalReads * 100;
		report.append("Read Miss Percentage: " + readPercentage + "%" + "\n");

		int totalWrites = writeMisses + writeHit;
		report.append("Total Writes: " + totalWrites + "\n");
		report.append("Total Write Hits: " + writeHit + "\n");
		report.append("Total Write Misses: " + writeMisses + "\n");
		double writePercentage = (double) writeMisses/totalWrites * 100;
		report.append("Write Miss Percentage: " + writePercentage + "%" + "\n\n");

		int totalReadsAndWrites = totalReads + totalWrites;
		int totalMisses = writeMisses + readMisses;
		double totalMissPercentage = (double) totalMisses/totalReadsAndWrites * 100;
		report.append("Miss rate for this local cache: " + totalMissPercentage + "%" + "\n");

		for (Integer value : accessedLine){

			if (value != 0){
				uniqueAccesses++;
			}
		}
		
	//	coherenceMiss = coherenceMiss - uniqueAccesses;
		double coherenceMissPercentage = (double) coherenceMiss / totalMisses * 100;
		report.append("Percentage misses caused by coherence: " + coherenceMissPercentage + "%" + "\n");

		System.out.println(report);

	}

	public String toString(){

		return name;

	}
}

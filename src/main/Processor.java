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
	public int writeMisses;
	public int PJM;


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

			readMisses++;
			// Copy the block in and set the tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to shared
			L1Cache[cacheLine][0] = 1;
			PJM++;
			busSnoop(0, cacheLine, tag);

		}

		// Read miss and block is invalid
		if (cachedTag == tag && MSIbit == 0){
			
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


		}

		// Read miss and block is modified
		else if (cachedTag != tag && MSIbit == 2){

			readMisses++;
			// Load block in and set tag
			L1Cache[cacheLine][1] = tag;
			// Set the MSI bit to shared
			L1Cache[cacheLine][0] = 1;
			PJM++;
			busSnoop(0, cacheLine, tag);

		}	
	}

	public void performWrite(int mainMemoryLine, int cacheLine, int tag){

		// Get the MSIbit and the tag out of the cache for this line
		int MSIbit = L1Cache[cacheLine][0];
		int cachedTag = L1Cache[cacheLine][1];

		// Tag is correct but block is invalid so write miss
		if (cachedTag == tag && MSIbit == 0){

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
					processor.PJM++;

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

				}

				else if (MSIbit == 1){

					//remote write miss in shared state
					processor.L1Cache[cacheline][0] = 0;

				}
			}
		}
	}

	public String toString(){

		return name;

	}
}

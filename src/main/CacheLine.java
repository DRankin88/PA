package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class CacheLine {

	public int memoryLocation;
	public HashMap<Processor, ArrayList<ArrayList<Object>>> accessorInformation;
	public int numberOfAccessors;
	public boolean isPrivate = true;
	public boolean wasSharedReadOnly = false;
	public boolean wasSharedReadWrite = false;
	public int operationsToPrivateLine = 0;
	public int operationsToSharedReadOnlyLine = 0;
	public int operationsToSharedReadWriteLine = 0;

	public CacheLine(int memoryLocation){

		this.memoryLocation = memoryLocation;
		accessorInformation = new HashMap<Processor, ArrayList<ArrayList<Object>>>();

	}

	public void addAccessInformation(Processor processor, int tag, int accessType){

		ArrayList<Integer> operations = new ArrayList<Integer>();
		ArrayList<Integer> tags = new ArrayList<Integer>();

		// So we want to check for this line if the same line and tag exist in someone elses processor. As then the operation would not be to a private line
		for (Processor otherProcessor : Main.arrayOfProcessors){

			int tagOfSameLine = otherProcessor.L1Cache[memoryLocation][1];
			int operationOnLine = otherProcessor.L1Cache[memoryLocation][3];


			operations.add(operationOnLine);
			if (!processor.equals(otherProcessor)){
				tags.add(tagOfSameLine);
			}
		}

		if (!tags.contains(tag)){

			operationsToPrivateLine++;

		}
		else if (operations.contains(1) && !operations.contains(2) && !operations.contains(3)){

			operationsToSharedReadOnlyLine++;

		}
		else if (operations.contains(1) && (operations.contains(2) || operations.contains(3))){

			operationsToSharedReadWriteLine++;

		}

	}

	public String toString(){

		return Integer.toString(memoryLocation) + " " + accessorInformation; 

	}
}
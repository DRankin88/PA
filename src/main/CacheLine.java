package main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class CacheLine {

	public int memoryLocation;
	public HashMap<Processor, String> accessorInformation;
	public int numberOfAccessors;
	public boolean wasSharedReadOnly = false;
	public boolean wasSharedReadWrite = false;

	public CacheLine(int memoryLocation){

		this.memoryLocation = memoryLocation;
		accessorInformation = new HashMap<Processor, String>();

	}

	public void collectData(){

		Iterator it = accessorInformation.entrySet().iterator();
		numberOfAccessors = accessorInformation.size();
		ArrayList<String> typeOfAccess = new ArrayList<String>();
		if (numberOfAccessors > 1){
			while (it.hasNext()) {
				
				Map.Entry pairs = (Map.Entry)it.next();
				typeOfAccess.add((String) pairs.getValue());



				//System.out.println(pairs.getKey() + " = " + pairs.getValue());
				//it.remove(); // avoids a ConcurrentModificationException
			}
			if (typeOfAccess.contains("R") && !typeOfAccess.contains("W") && !typeOfAccess.contains("R W") ){
				wasSharedReadOnly = true;
			}
			else if (typeOfAccess.contains("W") && !typeOfAccess.contains("R") && !typeOfAccess.contains("R W")){
			}
			else{
				wasSharedReadWrite = true;
			}
		}
	}
}

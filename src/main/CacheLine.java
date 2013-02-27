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

	public CacheLine(int memoryLocation){

		this.memoryLocation = memoryLocation;
		accessorInformation = new HashMap<Processor, ArrayList<ArrayList<Object>>>();

	}

	public void addAccessInformation(Processor processor, int tag, int accessType){

		ArrayList<Object> information = new ArrayList<Object>();
		information.add(tag);
		information.add(accessType);
		ArrayList<ArrayList<Object>> currentInfo = accessorInformation.get(processor);
		if (currentInfo != null){

			currentInfo.add(information);
			accessorInformation.put(processor, currentInfo);

		}
		else{

			ArrayList<ArrayList<Object>> newInformation = new ArrayList<ArrayList<Object>>();
			ArrayList<Object> newFirstInfo = new ArrayList<Object>();
			newFirstInfo.add(tag);
			newFirstInfo.add(accessType);
			newInformation.add(newFirstInfo);

			accessorInformation.put(processor, newInformation);

		}
	}

	public String toString(){

		return Integer.toString(memoryLocation) + " " + accessorInformation; 

	}


	public void collectData(){

		ArrayList<Integer> tagsForP0 = new ArrayList<Integer>();
		ArrayList<Integer> tagsForP1 = new ArrayList<Integer>();
		ArrayList<Integer> tagsForP2 = new ArrayList<Integer>();
		ArrayList<Integer> tagsForP3 = new ArrayList<Integer>();
		Iterator it = accessorInformation.entrySet().iterator();
		numberOfAccessors = accessorInformation.size();

		while(it.hasNext()){

			Map.Entry thisEntry = (Entry) it.next();
			ArrayList<ArrayList<Object>> processorEntry = (ArrayList<ArrayList<Object>>) thisEntry.getValue();
			if (thisEntry.getKey().equals(Main.arrayOfProcessors[0])){
				for (ArrayList<Object> tagAndAccess: processorEntry){

					int tag = (Integer) tagAndAccess.get(0);
					tagsForP0.add(tag);

				}	
			}	
			else if (thisEntry.getKey().equals(Main.arrayOfProcessors[1])){
				for (ArrayList<Object> tagAndAccess: processorEntry){

					int tag = (Integer) tagAndAccess.get(0);
					tagsForP1.add(tag);

				}	
			}
			else if (thisEntry.getKey().equals(Main.arrayOfProcessors[2])){
				for (ArrayList<Object> tagAndAccess: processorEntry){

					int tag = (Integer) tagAndAccess.get(0);
					tagsForP2.add(tag);

				}	
			}	
			else if (thisEntry.getKey().equals(Main.arrayOfProcessors[3])){
				for (ArrayList<Object> tagAndAccess: processorEntry){

					int tag = (Integer) tagAndAccess.get(0);
					tagsForP3.add(tag);

				}	
			}	
		}

		// Now work out if the tag arrays are totally disparate
		int totalAccesses = tagsForP0.size() + tagsForP1.size() + tagsForP2.size() + tagsForP3.size();

		for (Integer tag : tagsForP0){

			if ( tag != null && (tagsForP1.contains(tag) || tagsForP2.contains(tag) || tagsForP3.contains(tag))){

				isPrivate = false;

			}
		}
		for (Integer tag : tagsForP1){

			if (tag != null && (tagsForP0.contains(tag) || tagsForP2.contains(tag) || tagsForP3.contains(tag))){

				isPrivate = false;

			}
		}
		for (Integer tag : tagsForP2){

			if (tag != null && (tagsForP1.contains(tag) || tagsForP0.contains(tag) || tagsForP3.contains(tag))){

				isPrivate = false;

			}
		}
	}
}
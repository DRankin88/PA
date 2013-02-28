package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Main {

	private static int numberOfLines;
	private static int wordsPerLine;
	private static int totalReads;
	private static int totalWrites;
	private static HashMap<String, Processor> processors;
	public static Processor[] arrayOfProcessors = new Processor[4];
	private static ArrayList<CacheLine> cacheLines;
	private static HashMap<Integer, HashSet<Processor>> memoryAddresses;
	private static double[] processorPercentages = new double[3];
	private static double[] cacheLinePercentages = new double[3];
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		memoryAddresses = new HashMap<Integer, HashSet<Processor>>();
		//args format 0 = numberOfLines, 1 = wordsPerLine, 2 = fileToRead
		numberOfLines = Integer.parseInt(args[0]);
		wordsPerLine = Integer.parseInt(args[1]);
		String input = readFile(args[2]);
		cacheLines = new ArrayList<CacheLine>();
		processors = new HashMap<String, Processor>();

		// Make all the cacheLines
		for (int i = 0; i < numberOfLines; i++){

			cacheLines.add(new CacheLine(i));

		}


		// We need four processors
		Processor p0 = new Processor("P0", numberOfLines, wordsPerLine);
		processors.put(p0.getName(), p0);
		arrayOfProcessors[0] = p0;

		Processor p1 = new Processor("P1", numberOfLines, wordsPerLine);
		processors.put(p1.getName(), p1);
		arrayOfProcessors[1] = p1;

		Processor p2 = new Processor("P2", numberOfLines, wordsPerLine);
		processors.put(p2.getName(), p2);
		arrayOfProcessors[2] = p2;

		Processor p3 = new Processor("P3", numberOfLines, wordsPerLine);
		processors.put(p3.getName(), p3);
		arrayOfProcessors[3] = p3;

		String[] inputLines = input.split("\n");

		// Now read through the trace file one line at a time
		for(String line : inputLines){

			String[] currentLine = line.split(" ");
			Processor processor = processors.get(currentLine[0]);
			String operation = currentLine[1];

			// Calculate addresses, tags, lines etc
			int addressAsDecimalInt = Integer.parseInt(currentLine[2].replaceAll("\r", ""));
			int mainMemoryLine = addressAsDecimalInt/wordsPerLine;
			int cacheLine = mainMemoryLine % numberOfLines;
			int tag = mainMemoryLine/numberOfLines;

			// Store the information about who is accessing what line
			HashSet<Processor> processors = memoryAddresses.get(addressAsDecimalInt);
			if (processors != null){

				processors.add(processor);
				memoryAddresses.put(addressAsDecimalInt, processors);

			}
			else{
				HashSet<Processor> newProcessor = new HashSet<Processor>();
				newProcessor.add(processor);
				memoryAddresses.put(addressAsDecimalInt, newProcessor);
			}

			// After finding out which operation is desired tell the 
			// processor to execute a read or a write
			if (operation.equals("R")){

				CacheLine thisLine = cacheLines.get(cacheLine);
				thisLine.addAccessInformation(processor, tag, 1);
				totalReads++;
				processor.performRead(mainMemoryLine, cacheLine, tag);

			}

			if (operation.equals("W")){

				CacheLine thisLine = cacheLines.get(cacheLine);
				totalWrites++;
				thisLine.addAccessInformation(processor, tag, 2);
				processor.performWrite(mainMemoryLine, cacheLine, tag);

			}
		}	
		collectData();
		printReport();

	}

	private static void collectData(){

		int nonPrivateLines = 0;
		int numberOfSharedReadOnly = 0;
		int numberOfSharedReadWrite = 0;

		for (CacheLine cacheLine : cacheLines){

			numberOfSharedReadOnly += cacheLine.operationsToSharedReadOnlyLine;
			numberOfSharedReadWrite += cacheLine.operationsToSharedReadWriteLine;
			nonPrivateLines += cacheLine.operationsToPrivateLine;

		}

		ArrayList<CacheLine> temp = cacheLines;

		double totalOperations = totalReads + totalWrites;		
		double privatePercent = (nonPrivateLines/totalOperations) * 100;
		double sharedReadWritePercent = (numberOfSharedReadWrite/totalOperations) * 100;
		double sharedReadOnlyPercent = (numberOfSharedReadOnly/totalOperations) * 100;

		cacheLinePercentages[0] = privatePercent;
		cacheLinePercentages[1] = sharedReadOnlyPercent;
		cacheLinePercentages[2] = sharedReadWritePercent;

		// 1, 2 and > 2 addresses
		processorPercentages = accessPercentages();

	}

	private static double[] accessPercentages(){

		double One = 0;
		double two = 0;
		double moreThanTwo = 0;

		Iterator it = memoryAddresses.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry pairs = (Entry) it.next();

			HashSet<Processor> processors = (HashSet<Processor>) pairs.getValue();
			int number = processors.size();

			if(number == 1) {

				One++;

			}
			if (number == 2){

				two++;

			}
			if (number > 2){

				moreThanTwo++;

			}
		}

		double[] answer = new double[3];

		answer[0] = One / memoryAddresses.size() * 100; 
		answer[1] = two / memoryAddresses.size() * 100; 
		answer[2] = moreThanTwo / memoryAddresses.size() * 100; 

		return answer;
	}

	private static String readFile( String file ) throws IOException {
		BufferedReader reader = new BufferedReader( new FileReader (file));
		String         line = null;
		StringBuilder  stringBuilder = new StringBuilder();
		String         ls = System.getProperty("line.separator");

		while( ( line = reader.readLine() ) != null ) {
			stringBuilder.append( line );
			stringBuilder.append( ls );
		}

		return stringBuilder.toString();

	}	

	private static void printReport(){

		StringBuilder report = new StringBuilder();
		report.append("Final results\n\n");
		// Append all totals first
		report.append("Line size: " + wordsPerLine + " words per line\n");
		report.append("Number of lines: " + numberOfLines + "\n");
		report.append("Total Reads: " + totalReads + "\n");
		report.append("Total Writes: " + totalWrites + "\n");
		int totalReadMisses = arrayOfProcessors[0].readMisses + arrayOfProcessors[1].readMisses + arrayOfProcessors[2].readMisses + arrayOfProcessors[3].readMisses;
		report.append("Total Read Misses: " + totalReadMisses + "\n");
		report.append("Percentage Total Read Misses: " + (double) totalReadMisses/totalReads * 100 + "%\n");
		int totalWriteMisses = arrayOfProcessors[0].writeMisses + arrayOfProcessors[1].writeMisses + arrayOfProcessors[2].writeMisses + arrayOfProcessors[3].writeMisses;
		report.append("Total Write Misses: " + totalWriteMisses + "\n");
		report.append("Percentage Total Write Misses: " + (double) totalWriteMisses/totalWrites * 100 + "%\n");

		System.out.println(report);

		// Now print per processor numbers
		for (Processor processor : arrayOfProcessors){
			processor.printReport();
			System.out.println("\n");
		}

		System.out.println("The percentage of memory access that were to one processor is " + processorPercentages[0] + "%");
		System.out.println("The percentage of memory access that were to two processors is " + processorPercentages[1] + "%");
		System.out.println("The percentage of memory access that were to more than two processors is " + processorPercentages[2] + "%"  + "\n\n");

		System.out.println("The percentage of memory accesses that were to private cache lines is " + cacheLinePercentages[0] + "%");
		System.out.println("The percentage of memory accesses that were to shared read only lines is " + cacheLinePercentages[1] + "%");
		System.out.println("The percentage of memory accesses that were to shared read and write lines is " + cacheLinePercentages[2] + "%"  + "\n");
	}

}
package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Main {

	private static int numberOfLines;
	private static int wordsPerLine;
	private static int totalReads;
	private static int totalWrites;
	private static HashMap<String, Processor> processors;
	public static Processor[] arrayOfProcessors = new Processor[4];
	private static ArrayList<CacheLine> cacheLines;
	private static double percentageOfPrivateCacheAccesses;
	private static double percentageOfSharedReadOnlyAccesses;
	private static double percentageOfSharedReadWriteAccesses;
	private static double percentageOfOneAccesses;
	private static double percentageOfTwoAccesses;
	private static double percentageOfGreaterThanTwoAccesses;
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

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

		for(String line : inputLines){

			String[] currentLine = line.split(" ");
			Processor processor = processors.get(currentLine[0]);
			String operation = currentLine[1];
			int addressAsDecimalInt = Integer.parseInt(currentLine[2].replaceAll("\r", ""));
			int mainMemoryLine = addressAsDecimalInt/wordsPerLine;
			int cacheLine = mainMemoryLine % numberOfLines;
			int tag = mainMemoryLine/numberOfLines;

			if (operation.equals("R")){

				CacheLine thisLine = cacheLines.get(cacheLine);
				if (thisLine.accessorInformation.containsKey(processor)){
					String accessType = thisLine.accessorInformation.get(processor);
					if (accessType.equals("R")){
						thisLine.accessorInformation.put(processor, "R");
					}
					else if (accessType.equals("R W")){
					}
					else if (accessType.equals("W")){
						thisLine.accessorInformation.put(processor, "R W");
					}
					else{
						thisLine.accessorInformation.put(processor, "R");
					}
				}
				else{
					thisLine.accessorInformation.put(processor, "R");
				}
				totalReads++;
				processor.performRead(mainMemoryLine, cacheLine, tag);

			}

			if (operation.equals("W")){

				CacheLine thisLine = cacheLines.get(cacheLine);
				String accessType = thisLine.accessorInformation.get(processor);
				if (thisLine.accessorInformation.containsKey(processor)){

					if (accessType.equals("W")){
						thisLine.accessorInformation.put(processor, "W");
					}
					else if (accessType.equals("R W")){
					}
					else if (accessType.equals("R")){
						thisLine.accessorInformation.put(processor, "R W");
					}
					else{
						thisLine.accessorInformation.put(processor, "W");
					}
				}
				else{
					thisLine.accessorInformation.put(processor, "W");
				}
				totalWrites++;
				processor.performWrite(mainMemoryLine, cacheLine, tag);

			}
		}	

		ArrayList<CacheLine>temp = cacheLines;
		collectData();
		printReport();

	}

	private static void collectData(){

		int privateLines = 0;
		int numberOfSharedReadOnly = 0;
		int numberOfSharedReadWrite = 0;
		
		for (CacheLine cacheLine : cacheLines){
			cacheLine.collectData();
		}
		
		for (CacheLine cacheLine : cacheLines){
			if(cacheLine.numberOfAccessors == 1){
				privateLines++;
			}
			if(cacheLine.wasSharedReadOnly == true){
				numberOfSharedReadOnly++;
			}
			if(cacheLine.wasSharedReadWrite == true){
				numberOfSharedReadWrite++;
			}
		}
		
		percentageOfPrivateCacheAccesses = privateLines / numberOfLines;
		percentageOfSharedReadOnlyAccesses = numberOfSharedReadOnly / numberOfLines;
		percentageOfSharedReadWriteAccesses = numberOfSharedReadWrite / numberOfLines;
		System.out.println("lkbdfl");
		
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

		System.out.println("dflkhbdlf");

	}

}
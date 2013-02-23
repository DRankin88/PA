package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Main {

	private static int numberOfLines;
	private static int wordsPerLine;
	private static int totalReads = 0;
	private static int totalWrites = 0;
	private static HashMap<String, Processor> processors;
	public static Processor[] arrayOfProcessors = new Processor[4];
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		//args format 0 = numberOfLines, 1 = wordsPerLine, 2 = fileToRead
		numberOfLines = Integer.parseInt(args[0]);
		wordsPerLine = Integer.parseInt(args[1]);
		String input = readFile(args[2]);
		
		processors = new HashMap<String, Processor>();
		
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
			String addressAsDecimalString = currentLine[2];
			int addressAsDecimalInt = Integer.parseInt(addressAsDecimalString.replaceAll("\r", ""));
			int mainMemoryLine = addressAsDecimalInt/wordsPerLine;
			int cacheLine = mainMemoryLine % numberOfLines;
			int tag = mainMemoryLine/numberOfLines;
			if (operation.equals("R")){
				
				totalReads++;
				processor.performRead(mainMemoryLine, cacheLine, tag);
			
			}
			
			if (operation.equals("W")){
				
				totalWrites++;
				processor.performWrite(mainMemoryLine, cacheLine, tag);
			
			}
		}	
		System.out.println("dkfljnd");
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
}
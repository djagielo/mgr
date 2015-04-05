package pl.polsl.starters;

import pl.polsl.hashes.ApacheSparkHashTest;

public class ApacheSparkHashTestLocalStarter {

	public static void main(String[] args) {
		// Usage: ApacheSparkHashTest <master> <file> <partitionSize>
		String [] arguments = {
				"local",
				"testdata/john.txt",
				"100"};
		
		ApacheSparkHashTest.main(arguments);
	}

}

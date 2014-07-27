package pl.polsl.starter;

import pl.polsl.hashes.JppfHashTest;

public class JppfHashTestStarter {

	public static void main(String[] args) {
		//Usage: JppfHashTest <file> <partitionSize>
		String [] arguments = {
				"testdata/john.txt",
				"100"};
		
		JppfHashTest.main(arguments);
	}

}

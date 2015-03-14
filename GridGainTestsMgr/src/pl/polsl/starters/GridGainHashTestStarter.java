package pl.polsl.starters;

import org.apache.ignite.IgniteException;

import pl.polsl.hashes.GridGainHashTest;

public class GridGainHashTestStarter {

	public static void main(String[] args) throws IgniteException {
		// Usage: GridGainHashTest <file> <partitionSize>
		String [] arguments = {
				"config/example-compute-local.xml",
				"testdata/john.txt",
				"100"
		};
		
		GridGainHashTest.main(arguments);
	}

}

package pl.polsl.starters;

import org.gridgain.grid.GridException;

import pl.polsl.hashes.GridGainHashTest;

public class GridGainHashTestStarter {

	public static void main(String[] args) throws GridException {
		// Usage: GridGainHashTest <file> <partitionSize>
		String [] arguments = {
				"config/example-compute-local.xml",
				"testdata/john.txt",
				"100"
		};
		
		GridGainHashTest.main(arguments);
	}

}

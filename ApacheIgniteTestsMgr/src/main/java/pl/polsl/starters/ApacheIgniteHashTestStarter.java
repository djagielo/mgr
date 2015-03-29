package pl.polsl.starters;

import org.apache.ignite.IgniteException;

import pl.polsl.hashes.ApacheIgniteHashTest;

public class ApacheIgniteHashTestStarter {

	public static void main(String[] args) throws IgniteException {
		// Usage: GridGainHashTest <file> <partitionSize>
		String [] arguments = {
				ApacheIgniteHashTestStarter.class.getClassLoader().getResource("config/example-compute-local.xml").getFile(),
				ApacheIgniteHashTestStarter.class.getClassLoader().getResource("testdata/john.txt").getFile(),
				"100"
		};
		
		ApacheIgniteHashTest.main(arguments);
	}

}

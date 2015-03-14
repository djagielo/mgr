package pl.polsl.starters;

import java.io.FileNotFoundException;

import org.apache.ignite.IgniteException;
import org.apache.log4j.Logger;

import pl.polsl.kmeans.GridGainKmeansTest;
import pl.polsl.kmeans4.GridGainKmeansTest4;

public class GridGainKmeansTestStarter4 {
	private static Logger logger = Logger.getLogger(GridGainKmeansTest.class);
	public static void main(String[] args) throws FileNotFoundException, IgniteException {
			// Usage: JavaKMeans <master> <file> <k> <partitionSize> <convergeDist>;
		 String [] arguments = new String[] {
				  "config/example-compute-local.xml",
	    		  "C:\\Work\\Programming\\MA\\dane\\dailySportsActivitiesCumulatedLarge.txt",
	    		  "19",
	    		  "10",
	    		  "10"};
			GridGainKmeansTest4.main(arguments);
	}

}

package pl.polsl.starters;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.gridgain.grid.GridException;

import pl.polsl.kmeans.GridGainKmeansTest;
import pl.polsl.kmeans3.GridGainKmeansTest3;

public class GridGainKmeansTestStarter3 {
	private static Logger logger = Logger.getLogger(GridGainKmeansTest.class);
	public static void main(String[] args) throws FileNotFoundException, GridException {
			// Usage: JavaKMeans <master> <file> <k> <partitionSize> <convergeDist>;
		 String [] arguments = new String[] {
				  "config/example-compute-local.xml",
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "100",
	    		  "10"};
			GridGainKmeansTest3.main(arguments);
	}

}

package pl.polsl.starters;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.gridgain.grid.GridException;

import pl.polsl.kmeans.GridGainKmeansTest;
import pl.polsl.kmeans2.GridGainKmeansTest2;

public class GridGainKmeansTestStarter2 {
	private static Logger logger = Logger.getLogger(GridGainKmeansTest.class);
	public static void main(String[] args) throws FileNotFoundException, GridException {
			// Usage: JavaKMeans <master> <file> <k> <partitionSize> <convergeDist>;
		 String [] arguments = new String[] {
				  "config/example-compute-local.xml",
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "10",
	    		  "10"};
			GridGainKmeansTest2.main(arguments);
	}

}

package pl.polsl.starters;

import java.io.FileNotFoundException;

import org.apache.log4j.Logger;
import org.gridgain.grid.GridException;

import pl.polsl.kmeans.GridGainKmeansTest;

public class GridGainKmeansTestStarter {
	private static Logger logger = Logger.getLogger(GridGainKmeansTest.class);
	public static void main(String[] args) throws FileNotFoundException, GridException {
			// Usage: JavaKMeans <master> <file> <k> <convergeDist>;
		 String [] arguments = new String[] {
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "10"};
		 logger.debug("Test");
			GridGainKmeansTest.main(arguments);
	}

}

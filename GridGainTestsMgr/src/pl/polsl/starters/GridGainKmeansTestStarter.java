package pl.polsl.starters;

import java.io.FileNotFoundException;

import org.apache.ignite.IgniteException;
import org.apache.log4j.Logger;

import pl.polsl.kmeans.GridGainKmeansTest;

public class GridGainKmeansTestStarter {
	private static Logger logger = Logger.getLogger(GridGainKmeansTest.class);
	public static void main(String[] args) throws FileNotFoundException, IgniteException {
			// Usage: JavaKMeans <master> <file> <k> <convergeDist>;
		 String [] arguments = new String[] {
				  "config/example-compute-local.xml",
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "10"};
			GridGainKmeansTest.main(arguments);
	}

}

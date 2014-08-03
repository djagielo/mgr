package pl.polsl.starter;

import pl.polsl.kmeans3.JppfKMeansTest3;

public class JppfKmeansTestStarter3 {

	public static void main(String[] args) throws Exception {
		//Usage: JppfKMeans <file> <k> <submitQueSize> <tasksPerJob> <convergeDist> <partitionSize>
		  String [] arguments = new String[] { 
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "40",
	    		  "2000000",
	    		  "10",
	    		  "100"};
		JppfKMeansTest3.main(arguments);
	}

}

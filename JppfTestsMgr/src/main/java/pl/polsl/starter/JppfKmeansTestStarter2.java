package pl.polsl.starter;

import pl.polsl.kmeans2.JppfKMeansTest2;

public class JppfKmeansTestStarter2 {

	public static void main(String[] args) throws Exception {
		//Usage: JppfKMeans <file> <k> <submitQueSize> <tasksPerJob> <convergeDist> <partitionSize>
		  String [] arguments = new String[] { 
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "40",
	    		  "2000000",
	    		  "10",
	    		  "100"};
		JppfKMeansTest2.main(arguments);
	}

}

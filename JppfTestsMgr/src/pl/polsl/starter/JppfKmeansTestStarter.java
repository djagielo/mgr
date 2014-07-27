package pl.polsl.starter;

import pl.polsl.kmeans.JppfKMeansTest;

public class JppfKmeansTestStarter {

	public static void main(String[] args) throws Exception {
		//Usage: JppfKMeans <file> <k> <submitQueSize> <tasksPerJob> <convergeDist>
		  String [] arguments = new String[] { 
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "40",
	    		  "100",
	    		  "10"};
		JppfKMeansTest.main(arguments);
	}

}

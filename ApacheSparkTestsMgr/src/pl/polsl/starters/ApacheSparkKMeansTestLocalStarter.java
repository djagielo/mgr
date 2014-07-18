package pl.polsl.starters;

import pl.polsl.kmeans.ApacheSparkKMeansTest;

public class ApacheSparkKMeansTestLocalStarter {

	public static void main(String[] args) throws Exception {
		  String [] arguments = new String[] {"local", 
	    		  "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "0.1"};
		  
		  ApacheSparkKMeansTest.main(arguments);
	}

}

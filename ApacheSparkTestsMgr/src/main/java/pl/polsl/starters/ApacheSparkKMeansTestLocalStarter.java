package pl.polsl.starters;

import pl.polsl.kmeans.ApacheSparkKMeansTest;

public class ApacheSparkKMeansTestLocalStarter {

	public static void main(String[] args) throws Exception {
		  String [] arguments = new String[] {"H:\\Magisterka\\praca_magisterska\\dane testowe\\dailySportsActivitiesCumulated.txt",
	    		  "19",
	    		  "0.1", "local"};
		  
		  ApacheSparkKMeansTest.main(arguments);
	}

}

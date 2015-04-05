package pl.polsl.starters;

import pl.polsl.kmeans.ApacheSparkKMeansMLTest;

public class ApacheSparkKMeansMLTestLocalStarter {

	public static void main(String[] args) {
	 // Usage: JavaKMeans <input_file> <k> <max_iterations> <master> [<runs>]
		String [] arguments = new String [] {"H:\\Magisterka\\praca_magisterska\\dane testowe\\dailySportsActivitiesCumulated.txt",
				"19", 
				"1000",
				"local"
		};
		ApacheSparkKMeansMLTest.main(arguments);
	}

}

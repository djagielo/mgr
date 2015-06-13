package pl.polsl.starters;

import pl.polsl.kmeans.ApacheIgniteKmeansTest;

public class ApacheIgniteKmeansTestStarter {
	public static void main(String[] args) throws Exception {
				String [] arguments = {
						ApacheIgniteHashTestStarter.class.getClassLoader().getResource("config/example-compute-local.xml").getFile(),
						ApacheIgniteHashTestStarter.class.getClassLoader().getResource("testdata/dailySportsActivitiesCumulated.txt").getFile(),
						"19",
						"0.1"
				};
				ApacheIgniteKmeansTest.main(arguments);
	}
}

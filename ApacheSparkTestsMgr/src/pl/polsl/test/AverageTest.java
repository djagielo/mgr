package pl.polsl.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.spark.util.Vector;

import pl.polsl.kmeans.ApacheSparkKMeansTest;

public class AverageTest {
	private static final String SPLIT_MARK = ",";
	public static void main(String[] args) throws FileNotFoundException {
		File f = new File("X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt");
		Scanner sc = new Scanner(f);
		List<Vector> vectors = new LinkedList<>();
		while(sc.hasNextLine()){
			String line = sc.nextLine();
			Vector v = parseVector(line);
			vectors.add(v);
		}
		
		System.out.println(ApacheSparkKMeansTest.average(vectors));
	}
	
	static Vector parseVector(String line) {
	    String[] splits = line.split(SPLIT_MARK);
	    double[] data = new double[splits.length];
	    int i = 0;
	    for (String s : splits)
	      data[i] = Double.parseDouble(splits[i++]);
	    return new Vector(data);
	  }

}

package pl.polsl.tests;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import pl.polsl.kmeans.KMeansHelper;

public class DistanceTest {

	public static void main(String[] args) throws FileNotFoundException {
		List<RealVector> vectors = KMeansHelper.readDataFromFile("X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt", ",");
		
		RealVector start = vectors.get(0);
		
		for(RealVector v: vectors){
			System.out.println(start.getDistance(v));
		}

	}

}

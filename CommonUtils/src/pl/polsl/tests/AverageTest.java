package pl.polsl.tests;

import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.math3.linear.RealVector;

import pl.polsl.data.RealVectorDataPreparator;
import pl.polsl.kmeans.KMeansHelper;

public class AverageTest {

	public static void main(String[] args) throws FileNotFoundException {
		RealVectorDataPreparator dp = new RealVectorDataPreparator("X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\dailySportsActivitiesCumulated.txt", ",");
		List<RealVector> vectors = dp.getAllData();
		
		System.out.println(KMeansHelper.average(vectors));

	}

}

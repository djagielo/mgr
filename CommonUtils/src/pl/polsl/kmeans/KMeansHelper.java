package pl.polsl.kmeans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class KMeansHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8959271541874214233L;

	public static int closestPoint(RealVector p, List<RealVector> centers) {
		int bestIndex = 0;
		double closest = Double.POSITIVE_INFINITY;
		for (int i = 0; i < centers.size(); i++) {
			double tempDist = p.getDistance(centers.get(i));
			if (tempDist < closest) {
				closest = tempDist;
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	/** Computes the mean across all vectors in the input set of vectors */
	public static RealVector average(List<RealVector> ps) {
		int numVectors = ps.size();
		RealVector out = new ArrayRealVector(ps.get(0).toArray());
		// start from i = 1 since we already copied index 0 above
		for (int i = 1; i < numVectors; i++) {
			out = out.add(ps.get(i));
		}
		return out.mapDivide(numVectors);
	}

	public static RealVector parseVector(String line, String splitMark) {
		String[] splits = line.split(splitMark);
		double[] data = new double[splits.length];
		int i = 0;
		for (String s : splits)
			data[i] = Double.parseDouble(splits[i++]);
		return new ArrayRealVector(data);
	}

	public static List<RealVector> takeSample(List<RealVector> data, int size) {
		List<RealVector> out = new LinkedList<>();
		Random random = new Random(System.currentTimeMillis());
		int s = data.size();
		List<Integer> usedIndexes = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			int randomIndex = -1;
			do {
				randomIndex = random.nextInt(s - 1);
			} while (randomIndex < 0 || usedIndexes.contains(randomIndex));
			out.add(data.get(randomIndex));
			usedIndexes.add(randomIndex);
		}

		return out;
	}

	public static Collection<? extends Pair<Integer, List<RealVector>>> toListOfPairs(
			Set<Entry<Integer, List<RealVector>>> entrySet) {
		List<Pair<Integer, List<RealVector>>> out = new LinkedList<>();

		for (Entry<Integer, List<RealVector>> entry : entrySet) {
			out.add(new ImmutablePair<Integer, List<RealVector>>(entry.getKey(), entry.getValue()));
		}

		return out;
	}
}

package pl.polsl.utils;

import java.util.Iterator;
import java.util.List;

import org.apache.spark.util.Vector;

public class ApacheSparkKMeansHelper {
	/** Parses numbers split by whitespace to a vector */
	public static Vector parseVector(String line, String splitMark) {
		String[] splits = line.split(splitMark);
		double[] data = new double[splits.length];
		int i = 0;
		for (String s : splits)
			data[i] = Double.parseDouble(splits[i++]);
		return new Vector(data);
	}

	/**
	 * Computes the vector to which the input vector is closest using squared
	 * distance
	 */
	public static int closestPoint(Vector p, List<Vector> centers) {
		int bestIndex = 0;
		double closest = Double.POSITIVE_INFINITY;
		for (int i = 0; i < centers.size(); i++) {
			double tempDist = p.squaredDist(centers.get(i));
			if (tempDist < closest) {
				closest = tempDist;
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	/** Computes the mean across all vectors in the input set of vectors */
	public static Vector average(List<Vector> ps) {
		int numVectors = ps.size();
		Vector out = new Vector(ps.get(0).elements());
		// start from i = 1 since we already copied index 0 above
		for (int i = 1; i < numVectors; i++) {
			out.addInPlace(ps.get(i));
		}
		return out.divide(numVectors);
	}

	public static Vector average(Iterable<Vector> ps) {
		int numVectors = 0;
		Iterator<Vector> it = ps.iterator();
		Vector out = null;
		while (it.hasNext()) {
			numVectors++;
			if (out == null) {
				out = new Vector(it.next().elements());
			} else {
				out.addInPlace(it.next());
			}
		}

		return out.divide(numVectors);
	}
}

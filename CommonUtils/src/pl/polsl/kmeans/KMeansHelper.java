package pl.polsl.kmeans;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class KMeansHelper implements Serializable {
	  /**
	 * 
	 */
	private static final long serialVersionUID = -8959271541874214233L;
	static int closestPoint(RealVector p, List<RealVector> centers) {
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
	    return  out.mapDivide(numVectors);
	  }
	  
	  public static RealVector parseVector(String line, String splitMark) {
		    String[] splits = line.split(splitMark);
		    double[] data = new double[splits.length];
		    int i = 0;
		    for (String s : splits)
		      data[i] = Double.parseDouble(splits[i++]);
		    return new ArrayRealVector(data);
	  }
	  
		public static List<RealVector> readDataFromFile(String pathToData, String splitMark) throws FileNotFoundException{
			File f = new File(pathToData);
			List<RealVector> out = new LinkedList<>();
			try(Scanner sc = new Scanner(f)){
			while(sc.hasNextLine()){
				String line = sc.nextLine();
				out.add(parseVector(line, splitMark));
			}
			
				return out;
			}
		}
}

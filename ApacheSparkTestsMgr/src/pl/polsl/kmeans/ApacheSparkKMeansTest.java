package pl.polsl.kmeans;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.util.Vector;

import scala.Tuple2;

import com.google.common.collect.Lists;

/**
 * K-means clustering using Java API.
 */
@SuppressWarnings("deprecation")
public class ApacheSparkKMeansTest {
	private static final boolean USE_ITERATOR = false;
	private static final String SPLIT_MARK = ",";
  /** Parses numbers split by whitespace to a vector */

static Vector parseVector(String line) {
    String[] splits = line.split(SPLIT_MARK);
    double[] data = new double[splits.length];
    int i = 0;
    for (String s : splits)
      data[i] = Double.parseDouble(splits[i++]);
    return new Vector(data);
  }

  /** Computes the vector to which the input vector is closest using squared distance */
  static int closestPoint(Vector p, List<Vector> centers) {
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
  static Vector average(List<Vector> ps) {
    int numVectors = ps.size();
    Vector out = new Vector(ps.get(0).elements());
    // start from i = 1 since we already copied index 0 above
    for (int i = 1; i < numVectors; i++) {
      out.addInPlace(ps.get(i));
    }
    return out.divide(numVectors);
  }
  
  static Vector average(Iterable<Vector> ps) {
	    int numVectors = 0;
	    Iterator<Vector> it = ps.iterator();
	    Vector out = null;
	    while(it.hasNext()){
	    	numVectors++;
	    	if(out == null){
	    		out = new Vector(it.next().elements());
	    	}
	    	else{
	    		out.addInPlace(it.next());
	    	}
	    }
	    
	   /* Vector out = new Vector(ps.get(0).elements());
	    // start from i = 1 since we already copied index 0 above
	    for (int i = 1; i < numVectors; i++) {
	      out.addInPlace(ps.get(i));
	    }*/
	    return out.divide(numVectors);
	  }

  public static void main(String[] args) throws Exception {
    if (args.length < 4) {
      System.err.println("Usage: JavaKMeans <master> <file> <k> <convergeDist>");
      System.exit(1);    
    }
    JavaSparkContext sc = new JavaSparkContext(args[0], "JavaKMeans",
      System.getenv("SPARK_HOME"), System.getenv("SPARK_EXAMPLES_JAR"));
    String path = args[1];
    int K = Integer.parseInt(args[2]);
    double convergeDist = Double.parseDouble(args[3]);

    JavaRDD<Vector> data = sc.textFile(path).map(
      new Function<String, Vector>() {
        @Override
        public Vector call(String line) throws Exception {
          return parseVector(line);
        }
      }
    ).cache();

    final List<Vector> centroids = data.takeSample(false, K, 42);

    double tempDist;
    do {

      // allocate each vector to closest centroid   	
      JavaPairRDD<Integer, Vector> closest = data.mapToPair(
        new PairFunction<Vector, Integer, Vector>() {
          @Override
          public Tuple2<Integer, Vector> call(Vector vector) throws Exception {
            return new Tuple2<Integer, Vector>(
              closestPoint(vector, centroids), vector);
          }
        }
      );

      // group by cluster id and average the vectors within each cluster to compute centroids
     
      JavaPairRDD<Integer, Iterable<Vector>> pointsGroup = closest.groupByKey();
      Map<Integer, Vector> newCentroids = pointsGroup.mapValues(
        new Function<Iterable<Vector>, Vector>() {
          public Vector call(Iterable<Vector> ps) throws Exception {
        	  if(USE_ITERATOR){
        		  return average(ps);
        	  }
        	  else{
        		  return average(Lists.newArrayList(ps));        		  
        	  }
          }
        }).collectAsMap();
      tempDist = 0.0;
      for (int i = 0; i < K; i++) {
        tempDist += centroids.get(i).squaredDist(newCentroids.get(i));
      }
      for (Map.Entry<Integer, Vector> t: newCentroids.entrySet()) {
        centroids.set(t.getKey(), t.getValue());
      }
      System.out.println("Finished iteration (delta = " + tempDist + ")");
    } while (tempDist > convergeDist);

    System.out.println("Final centers:");
    for (Vector c : centroids)
      System.out.println(c);

    System.exit(0);

  }
}

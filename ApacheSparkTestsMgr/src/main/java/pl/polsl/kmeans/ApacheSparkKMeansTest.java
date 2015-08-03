package pl.polsl.kmeans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealVector;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.util.Vector;

import pl.polsl.utils.ApacheSparkKMeansHelper;
import scala.Tuple2;

import com.google.common.collect.Lists;

/**
 * K-means clustering using Java API.
 */
@SuppressWarnings("deprecation")
public class ApacheSparkKMeansTest{
	private static final boolean USE_ITERATOR = false;
	private static final String SPLIT_MARK = ",";

  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      System.err.println("Usage: JavaKMeans <file> <k> <convergeDist>");
      System.exit(1);    
    }

    String path = args[0];
    int K = Integer.parseInt(args[1]);
    double convergeDist = Double.parseDouble(args[2]);
    String master = null;
    if(args.length == 4)
    	master = args[3];
    
    SparkConf sparkConf = new SparkConf().setAppName("JavaKMeans");
    if(master != null)
    	sparkConf.setMaster(master);
    
    JavaSparkContext sc = new JavaSparkContext(sparkConf);
    long start = System.currentTimeMillis();
    JavaRDD<Vector> data = sc.textFile(path).map(
    		line -> ApacheSparkKMeansHelper.parseVector(line, SPLIT_MARK)
    ).cache();

    final List<Vector> centroids = data.takeSample(false, K, System.currentTimeMillis());
    //RealVectorDataPreparator rvdp = new RealVectorDataPreparator("C:\\Work\\Programming\\MA\\dane\\sample.txt", SPLIT_MARK);
    //final List<Vector> centroids = realVectorToVector(rvdp.getAllData());
    double tempDist;
    do {
      // allocate each vector to closest centroid   	
      JavaPairRDD<Integer, Vector> closest = data.mapToPair(
    		  vector -> new Tuple2<Integer, Vector>(ApacheSparkKMeansHelper.closestPoint(vector, centroids), vector)
      );
      
      //TODO - think about partitioning by Integer key - it will distribute data in a way that all vectors of
      // centroid will be on the same node - saving network traffic
      // check performance of hashpartitioner and customPartitioner
      
      //closest.partitionBy(new HashPartitioner(4));
      
      // group by cluster id and average the vectors within each cluster to compute centroids
      JavaPairRDD<Integer, Iterable<Vector>> pointsGroup = closest.groupByKey();
      Map<Integer, Vector> newCentroids = pointsGroup.mapValues(
    		  ps -> (USE_ITERATOR ? ApacheSparkKMeansHelper.average(ps) 
    				  				: ApacheSparkKMeansHelper.average(Lists.newArrayList(ps)))
    		  ).collectAsMap();
      tempDist = 0.0;
  	
      for (int i = 0; i < K; i++) {
        tempDist += centroids.get(i).squaredDist(newCentroids.get(i));
      }

      for (Map.Entry<Integer, Vector> t: newCentroids.entrySet()) {
        centroids.set(t.getKey(), t.getValue());
      }
  	
      System.out.println("Finished iteration (delta = " + tempDist + ")");
    } while (tempDist > convergeDist);

    System.out.println(String.format("Final centers(%s):", centroids.size()));
    for (Vector c : centroids)
      System.out.println(c);
    
    System.out.println(String.format("ApacheSparkKMeansTest executed in: %s[ms]", (System.currentTimeMillis() - start)));

    sc.close();
    System.exit(0);
  }

	private static List<Vector> realVectorToVector(List<RealVector> allData) {
		List<Vector> result = new ArrayList<Vector>();
		
		for(RealVector vector: allData){
			result.add(new Vector(vector.toArray()));
		}
		
		return result;
	}
}

package pl.polsl.kmeans;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealVector;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;

public class JppfKMeansTest {
	private static final String SPLIT_MARK = ",";
	public static void main(String[] args) throws Exception {
		if (args.length < 3) {
			System.err.println("Usage: JavaKMeans <file> <k> <convergeDist>");
			System.exit(1);
		}
		try(JPPFClient client = new JPPFClient()){
			String path = args[0];
		    int K = Integer.parseInt(args[1]);
		    double convergeDist = Double.parseDouble(args[2]);
		    
		    // wczytanie pliku do kolekcji
		    List<RealVector> data = KMeansHelper.readDataFromFile(path, SPLIT_MARK);
		    // pobranie próbki K punktów z kolekcji
		    final List<RealVector> centroids = KMeansHelper.takeSample(data, K);
		
		    double tempDist;
		    do{
		    	// 1. allocate each vector to closest centroid and group by id
		    	JobProvider jobProvider = new JobProvider();
		    	JPPFJob allocateJob = jobProvider.createClosestCentroidsJob(data, centroids);
		    	// submit job to compute on grid
		    	client.submitJob(allocateJob);
		    	
		    	// waiting for all jobs gets done
		    	Object lock = new Object();
		        // wait until all job results have been processed
		        while (jobProvider.getProcessedTasksCount() < jobProvider.getSubmittedTasksCount()) {
		          synchronized(lock) {
		            lock.wait(1L);
		          }
		        }
		        
		    	// 2. average the vectors within each cluster to compute centroids
		    	
		        JPPFJob groupByJob = jobProvider.createGroupByAndAverageJob(KMeansHelper.toListOfPairs(jobProvider.getClosestCentroidsMerger().getMergedResults().entrySet()));
		        client.submitJob(groupByJob);
		        
		     // waiting for all jobs gets done
		    	Object lock2 = new Object();
		        // wait until all job results have been processed
		        while (jobProvider.getProcessedTasksCount() < jobProvider.getSubmittedTasksCount()) {
		          synchronized(lock2) {
		            lock2.wait(1L);
		          }
		        }
		        
		        Map<Integer, RealVector> newCentroids = jobProvider.getNewCentroidsMerger().getMergedResults();
		        
		    	// 3. compute new centroids
		    	
		    	tempDist = 0.0;
		        for (int i = 0; i < K; i++) {
		          tempDist += centroids.get(i).getDistance(newCentroids.get(i));
		        }
		        for (Map.Entry<Integer, RealVector> t: newCentroids.entrySet()) {
		          centroids.set(t.getKey(), t.getValue());
		        }
		        
		        
		        System.out.println("Finished iteration (delta = " + tempDist + ")");
		    	
		    }while(tempDist > convergeDist);
		
		}

	}

}

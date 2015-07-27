package pl.polsl.kmeans3;

import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealVector;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;

import pl.polsl.data.RealVectorDataPreparator;
import pl.polsl.data.beans.CacheEntry;
import pl.polsl.kmeans.KMeansHelper;

public class JppfKMeansTest3 {
	private static final String SPLIT_MARK = ",";
	public static void main(String[] args) throws Exception {
		if (args.length < 6) {
			System.err.println("Usage: JppfKMeans <file> <k> <submitQueSize> <tasksPerJob> <convergeDist> <partitionSize>");
			System.exit(1);
		}
		try(JPPFClient client = new JPPFClient()){
			String path = args[0];
		    int K = Integer.parseInt(args[1]);
		    int submitQueSize = Integer.parseInt(args[2]);
		    int tasksPerJob = Integer.parseInt(args[3]);
		    double convergeDist = Double.parseDouble(args[4]);
		    int partitionSize = Integer.parseInt(args[5]);
		    
		    RealVectorDataPreparator dp = new RealVectorDataPreparator(path, SPLIT_MARK);
		    // reading all data to list
		    List<CacheEntry<RealVector>> allData = dp.getAllDataCacheEntry(partitionSize);
		    //dp.refreshDataSource();
		    //List<List<RealVector>> data = dp.getPartitionedData(partitionSize);
		    
		    // take sample of K size
		    final List<RealVector> centroids = KMeansHelper.takeSample(allData, K);
		   
		    /*FAKING takeSample method for testing
		    RealVectorDataPreparator rvdp = new RealVectorDataPreparator(JppfKMeansTest3.class.getClassLoader().getResource("testdata/sample.txt").getFile(), SPLIT_MARK);
		    final List<RealVector> centroids = rvdp.getAllData();*/
		    
		    SubmitQueue queue = new SubmitQueue(submitQueSize, client);
		    long start = System.currentTimeMillis();
		    double tempDist;
		    do{
		    	// 1. allocate each vector to closest centroid and group by id
		    	JobProvider jobProvider = new JobProvider();
		    	List<JPPFJob> allocateJobs = jobProvider.createClosestCentroidsJobs(allData, centroids);
		    	System.out.println(String.format("Allocate jobs size: %s", allocateJobs.size()));
		    	for(JPPFJob job: allocateJobs)
		    		queue.submit(job);
		    	
		    	// waiting for all jobs gets done
		    	Object lock = new Object();
		        // wait until all job results have been processed
		        while (jobProvider.getProcessedTasksCount() < jobProvider.getSubmittedTasksCount()) {
		          synchronized(lock) {
		            lock.wait(1L);
		          }
		        }
		        
		        Map<Integer, List<RealVector>> partialCentroids = jobProvider.getClosestCentroidsMerger().getMergedResults();
		        
		        Map<Integer, RealVector> newCentroids = KMeansHelper.averagePartialCentroidsList(partialCentroids);
		        
		    	// 3. compute new centroids
		    	tempDist = 0.0;
		        for (int i = 0; i < K; i++) {
		          tempDist += centroids.get(i).getDistance(newCentroids.get(i));
		        }
		        for (Map.Entry<Integer, RealVector> t: newCentroids.entrySet()) {
		          centroids.set(t.getKey(), t.getValue());
		        }
		        
		        // nulling vectors for each cache entry to prevent sending allData through network
		        // it is already cached on nodes
		        allData.stream().forEach(entry -> entry.setVectors(null));
		        
		        System.out.println("Finished iteration (delta = " + tempDist + ")");
		    	
		    }while(tempDist > convergeDist);
		    
		    for (RealVector c : centroids)
		        System.out.println(c);
		    queue.stop();
		    System.out.println(String.format("JppfKMeansTest executed in %s[ms]", (System.currentTimeMillis() - start)));
		
		}

	}

}

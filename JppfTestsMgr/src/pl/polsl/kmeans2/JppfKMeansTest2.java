package pl.polsl.kmeans2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.linear.RealVector;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.task.storage.DataProvider;
import org.jppf.task.storage.MemoryMapDataProvider;

import pl.polsl.data.RealVectorDataPreparator;
import pl.polsl.kmeans2.JobProvider;
import pl.polsl.kmeans.KMeansHelper;
import pl.polsl.kmeans.SubmitQueue;

public class JppfKMeansTest2 {
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
		    List<RealVector> data = dp.getAllData();
		    // take sample of K size
		    final List<RealVector> centroids = KMeansHelper.takeSample(data, K);
		    SubmitQueue queue = new SubmitQueue(submitQueSize, client);
		    List<List<Integer>> partitionedIndexes = getPartitionedData(data, partitionSize);
		    long start = System.currentTimeMillis();
		    
		    // data provider mechanism
		    DataProvider dataProvider = new MemoryMapDataProvider();
		    dataProvider.setParameter("data", data);
//		    
		    double tempDist;
		    do{
		    	// 1. allocate each vector to closest centroid and group by id
		    	JobProvider jobProvider = new JobProvider(dataProvider);
		    	List<JPPFJob> allocateJobs = jobProvider.createClosestCentroidsJobs(partitionedIndexes, centroids,tasksPerJob);
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
		        
		    	// 2. average the vectors within each cluster to compute centroids
		        List<JPPFJob> groupByJobs = jobProvider.createGroupByAndAverageJobs(KMeansHelper.toListOfPairs(jobProvider.getClosestCentroidsMerger().getMergedResults().entrySet()),tasksPerJob);
		        //client.submitJob(groupByJob);
		        System.out.println(String.format("GroupByJobs size: %s", groupByJobs.size()));
		        for(JPPFJob job: groupByJobs)
		        	queue.submit(job);
		        
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
		    System.out.println(String.format("JppfKMeansTest executed in %s[ms]", (System.currentTimeMillis() - start)));
		
		}

	}
	
	private static List<Integer> takeSample(List<RealVector> data, int k){
		Random random = new Random(System.currentTimeMillis());
		int s = data.size();
		List<Integer> usedIndexes = new ArrayList<Integer>();
		for (int i = 0; i < s; i++) {
			int randomIndex = -1;
			do {
				randomIndex = random.nextInt(s - 1);
			} while (randomIndex < 0 || usedIndexes.contains(randomIndex));
			usedIndexes.add(randomIndex);
		}

		return usedIndexes;
	}
	
	private static List<List<Integer>> getPartitionedData(List<RealVector> data, int partitionSize){
		if(partitionSize > 0){
			List<List<Integer>> result = new LinkedList<>();
		
				List<Integer> embeddedList = new LinkedList<>();
				int counter = 0;
				int cnt = 0;
				while(cnt <= data.size()-1){
					embeddedList.add(cnt++);
					counter++;
					if(counter == partitionSize){
						result.add(embeddedList);
						embeddedList = new LinkedList<>();
						counter = 0;
					}
				}
				if(embeddedList != null && embeddedList.size() > 0){
					result.add(embeddedList);
				}

			
			return result;
		}
		else
			return null;
	}

}

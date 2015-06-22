package pl.polsl.kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteCallable;

import pl.polsl.kmeans.data.ApacheIgniteKmeansDataStreamer;

public class ApacheIgniteKmeansTest {
	private static Logger logger = Logger.getLogger("ApacheIgniteKmeansTest");
	private static final String CACHE_NAME = "dataCache";
	public static void main(String[] args) throws Exception {
		if (args.length < 4) {
		      System.err.println("Usage: ApacheIgniteKmeansTest <config> <file> <K> <convergeDist>");
		      System.exit(1);    
		}
		
		String config = args[0];
		String file = args[1];
		int K = Integer.parseInt(args[2]);
		double convergeDist = Double.parseDouble(args[3]);
		
		try(Ignite ignite = Ignition.start(config)){
			int clusterSize = ignite.cluster().forRemotes().nodes().size();
			ApacheIgniteKmeansDataStreamer data = new ApacheIgniteKmeansDataStreamer(ignite, file, CACHE_NAME, clusterSize);	
	
			System.out.println(String.format("Remote nodes cluster size: %s", clusterSize));
			// loading data to Data Grid
			data.load();
			
			// first random centroids
			final List<RealVector> centroids = data.takeSample(K);
			
			// for debug reason - testing method testSample
			KMeansHelper.checkDuplicates(centroids);
			
			/** Displaying local cache storage - for tests **/
		/*	ignite.compute().broadcast( () -> {
    			IgniteCache<Object, Object> c= ignite.cache("dataCache");
				System.out.println("RUN");
    			for(Cache.Entry<Object, Object> obj: c.localEntries(CachePeekMode.PRIMARY)){
    			System.out.println(String.format("%s: %s", obj.getKey(), obj.getValue()));
				}
    			
    		});	*/
			
			// K-means iterations
			double tempDist = 0.0;
			long start = System.currentTimeMillis();
			do{
				// partial Centroids - each element is result from one node in cluster
				List<Map<Integer, RealVector>> partialCentroids = new ArrayList<>();
				IntStream.range(0, clusterSize).forEach(cacheKey -> partialCentroids.add(ignite.compute(ignite.cluster().forRemotes()).affinityCall(CACHE_NAME, cacheKey, new IgniteCallable<Map<Integer, RealVector>>() {
					private static final long serialVersionUID = 2298427843275191441L;

					@Override
					public Map<Integer, RealVector> call() throws Exception {
						// get vectors from local cache - affinityKey
						List<RealVector> vectors = (List<RealVector>) Ignition.ignite().cache(CACHE_NAME).get(cacheKey);
						List<Pair<Integer, RealVector>> tmp = new LinkedList<>();
						for(RealVector vector: vectors){
							int i = KMeansHelper.closestPoint(vector, centroids);
							tmp.add(new ImmutablePair<Integer, RealVector>(i, vector));
						}
						Map<Integer, List<RealVector>> tmpResult = new HashMap<>();
						
						for(Pair<Integer, RealVector> pair: tmp){
							if(tmpResult.containsKey(pair.getLeft())){
								// get collection and add vector
								List<RealVector> list = tmpResult.get(pair.getLeft());
								list.add(pair.getRight());
								tmpResult.put(pair.getLeft(), list);
							}
							else{
								List<RealVector> list = new LinkedList<>();
								list.add(pair.getRight());
								tmpResult.put(pair.getLeft(), list);
							}
						}
						Map<Integer, RealVector> result = new HashMap<>();
						for(Entry<Integer, List<RealVector>> entry: tmpResult.entrySet()){
							result.put(entry.getKey(), KMeansHelper.average(entry.getValue()));
						}
						
						return result;
					}
				})));
				
				// average partial centroids from all nodes
				Map<Integer, RealVector> newCentroids = KMeansHelper.averagePartialCentroids(partialCentroids);
		    	
				// computing distance
				tempDist = 0.0;
		        for (int i = 0; i < K; i++) {
		          tempDist += centroids.get(i).getDistance(newCentroids.get(i));
		        }
		        // setting centroids for new iteration
		        for (Map.Entry<Integer, RealVector> t: newCentroids.entrySet()) {
		          centroids.set(t.getKey(), t.getValue());
		        }
		        
		        System.out.println("Finished iteration (delta = " + tempDist + ")");
			}while(tempDist > convergeDist);
		   
			System.out.println(String.format("Algorithm finished: %s[ms]", (System.currentTimeMillis() - start)));
			
		}
		finally{
			
		}
		
	}


}

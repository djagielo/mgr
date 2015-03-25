package pl.polsl.kmeans;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.ignite.lang.IgniteReducer;

import pl.polsl.data.RealVectorDataPreparator;

public class GridGainKmeansTest {
	
	private static final String SPLIT_MARK = ",";
	public static void main(String[] args) throws IgniteException, FileNotFoundException {
		if (args.length < 4) {
			System.err.println("Usage: JavaKMeans <config> <file> <k> <convergeDist>");
			System.exit(1);
		}
		String config = args[0];
	    String path = args[1];
	    int K = Integer.parseInt(args[2]);
	    double convergeDist = Double.parseDouble(args[3]);
		try(Ignite g = Ignition.start(config)){		    
		    RealVectorDataPreparator dp = new RealVectorDataPreparator(path, SPLIT_MARK);
		    // reading all data to list
		    System.out.println("Get all data");
		    List<RealVector> data = dp.getAllData();
		    // take sample of K size
		    System.out.println("Take sample");
		    final List<RealVector> centroids = KMeansHelper.takeSample(data, K);
		    long start = System.currentTimeMillis();
		    double tempDist;
		    do{
		    	
		    	// allocate each vector to closest centroid 
		    	Map<Integer, List<RealVector>> pointsGroup = g.compute(g.cluster().forRemotes()).apply(new IgniteClosure<RealVector, Pair<Integer, RealVector>>() {
					@Override
					public Pair<Integer,RealVector> apply(RealVector vector) {
						int i = KMeansHelper.closestPoint(vector, centroids);
						System.out.println("Closest point " + i);
						return new ImmutablePair<Integer, RealVector>(i, vector);
					}
				}, 
				
				data,
				
				// group by cluster id and average the vectors within each cluster to compute centroids
				new IgniteReducer<Pair<Integer, RealVector>, Map<Integer, List<RealVector>>>() {	
					Map<Integer, List<RealVector>> result = new HashMap<>();
					Map<Integer, List<RealVector>> synchroResult = Collections.synchronizedMap(result);
					
					@Override
					public boolean collect(Pair<Integer, RealVector> pair) {
						System.out.println(String.format("collect pair: %s", pair));
						if(synchroResult.containsKey(pair.getLeft())){
							List<RealVector> results = synchroResult.get(pair.getLeft());
							results.add(pair.getRight());
							synchroResult.put(pair.getLeft(), results);
		
						}
						else{
							List<RealVector> values = Collections.synchronizedList(new LinkedList<RealVector>());
							values.add(pair.getRight());
							synchroResult.put(pair.getLeft(), values);
						}
						//System.out.println(String.format("synchroResult keySet: %s", synchroResult.keySet()));
						return true;
					}

					@Override
					public Map<Integer, List<RealVector>> reduce() {
						return synchroResult;
					}	
				});
		    	System.out.println("New centroids");
		    	Map<Integer, RealVector> newCentroids = g.compute(g.cluster().forRemotes()).apply(new IgniteClosure<Pair<Integer, List<RealVector>>, Pair<Integer, RealVector>>() {

					@Override
					public Pair<Integer, RealVector> apply(Pair<Integer, List<RealVector>> pair) {
						System.out.println("Computing average");
						return new ImmutablePair<Integer, RealVector>(pair.getLeft(),KMeansHelper.average(pair.getRight()));
					}
				},
				toListOfPairs(pointsGroup.entrySet()),
				
				new IgniteReducer<Pair<Integer, RealVector>, Map<Integer, RealVector>>() {
					Map<Integer, RealVector> result = new HashMap<>();
					Map<Integer, RealVector> synchroResult = Collections.synchronizedMap(result);
					@Override
					public boolean collect(Pair<Integer, RealVector> pair) {
						//System.out.println(String.format("collect operation on key: %s", pair.getLeft()));
						synchroResult.put(pair.getLeft(), pair.getRight());
						return true;
					}

					@Override
					public Map<Integer, RealVector> reduce() {
						return synchroResult;
					}
				});
		    	
		    	tempDist = 0.0;
		        for (int i = 0; i < K; i++) {
		          tempDist += centroids.get(i).getDistance(newCentroids.get(i));
		        }
		        for (Map.Entry<Integer, RealVector> t: newCentroids.entrySet()) {
		          centroids.set(t.getKey(), t.getValue());
		        }
		        
		        
		        System.out.println("Finished iteration (delta = " + tempDist + ")");
   	
		    }while(tempDist > convergeDist);
		    System.out.println(String.format("Algorithm finished: %s[ms]", (System.currentTimeMillis() - start)));
		}
	}
	
	private static Collection<? extends Pair<Integer, List<RealVector>>> toListOfPairs(Set<Entry<Integer, List<RealVector>>> entrySet) {
		List<Pair<Integer, List<RealVector>>> out = new LinkedList<>();
		
		for(Entry<Integer, List<RealVector>> entry: entrySet){
			out.add(new ImmutablePair<Integer, List<RealVector>>(entry.getKey(), entry.getValue()));
		}
		
		return out;
	}

}

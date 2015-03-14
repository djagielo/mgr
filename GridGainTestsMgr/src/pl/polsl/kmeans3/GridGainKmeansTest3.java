package pl.polsl.kmeans3;

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
import pl.polsl.kmeans.KMeansHelper;

// implementacja zwracania w pierwszym roku algorytmu mapy ze œrednimi wektorami zamiast
// listy wektorów
public class GridGainKmeansTest3 {
	
	private static final String SPLIT_MARK = ",";
	public static void main(String[] args) throws IgniteException, FileNotFoundException {
		if (args.length < 5) {
			System.err.println("Usage: JavaKMeans <file> <k> <partitionSize> <convergeDist>");
			System.exit(1);
		}
		String config = args[0];
	    String path = args[1];
	    int K = Integer.parseInt(args[2]);
	    int partitionSize = Integer.parseInt(args[3]);
	    double convergeDist = Double.parseDouble(args[4]);
		try(Ignite g = Ignition.start(config)){		    
		    RealVectorDataPreparator dp = new RealVectorDataPreparator(path, SPLIT_MARK);
		    // reading all data to list
		    List<RealVector> data = dp.getAllData();
		    dp.refreshDataSource();
		    List<List<RealVector>> partitionedData = dp.getPartitionedData(partitionSize);
		    // take sample of K size
		    final List<RealVector> centroids = KMeansHelper.takeSample(data, K);
		    long start = System.currentTimeMillis();
		    double tempDist;
		    do{
		    	// allocate each vector to closest centroid 
		    	Map<Integer, List<RealVector>> pointsGroup = g.compute().apply(new IgniteClosure<List<RealVector>, Map<Integer, RealVector>>() {
					@Override
					public Map<Integer, RealVector> apply(List<RealVector> vectors) {
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
				}, 
				
				partitionedData,
				
				// group by cluster id and average the vectors within each cluster to compute centroids
				new IgniteReducer<Map<Integer, RealVector>, Map<Integer, List<RealVector>>>() {	
					Map<Integer, List<RealVector>> result = new HashMap<>();
					Map<Integer, List<RealVector>> synchroResult = Collections.synchronizedMap(result);
					
					@Override
					public boolean collect(Map<Integer, RealVector> pairs) {
						for(Entry<Integer, RealVector> pair: pairs.entrySet()){
							if(synchroResult.containsKey(pair.getKey())){
								List<RealVector> results = synchroResult.get(pair.getKey());
								results.add(pair.getValue());
								synchroResult.put(pair.getKey(), results);
			
							}
							else{
								List<RealVector> values = Collections.synchronizedList(new LinkedList<RealVector>());
								values.add(pair.getValue());
								synchroResult.put(pair.getKey(), values);
							}
						}
						return true;
					}

					@Override
					public Map<Integer, List<RealVector>> reduce() {
						return synchroResult;
					}	
				});
		    	
		    	Map<Integer, RealVector> newCentroids = g.compute().apply(new IgniteClosure<Pair<Integer, List<RealVector>>, Pair<Integer, RealVector>>() {

					@Override
					public Pair<Integer, RealVector> apply(Pair<Integer, List<RealVector>> pair) {
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

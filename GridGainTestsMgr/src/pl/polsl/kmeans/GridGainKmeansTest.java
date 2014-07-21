package pl.polsl.kmeans;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.apache.log4j.Logger;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.lang.GridClosure;
import org.gridgain.grid.lang.GridReducer;

public class GridGainKmeansTest {
	
	private static final String SPLIT_MARK = ",";
	public static void main(String[] args) throws GridException, FileNotFoundException {
		if (args.length < 3) {
			System.err.println("Usage: JavaKMeans <file> <k> <convergeDist>");
			System.exit(1);
		}
		try(Grid g = GridGain.start()){
		    String path = args[0];
		    int K = Integer.parseInt(args[1]);
		    double convergeDist = Double.parseDouble(args[2]);
		    
		    // wczytanie pliku do kolekcji
		    List<RealVector> data = KMeansHelper.readDataFromFile(path, SPLIT_MARK);
		    // pobranie próbki K punktów z kolekcji
		    final List<RealVector> centroids = KMeansHelper.takeSample(data, K);
		    
		    double tempDist;
		    do{
		    	// allocate each vector to closest centroid 
		    	Map<Integer, List<RealVector>> pointsGroup = g.compute().apply(new GridClosure<RealVector, Pair<Integer, RealVector>>() {
		    		List<Integer> list = new LinkedList<Integer>();
					@Override
					public Pair<Integer,RealVector> apply(RealVector vector) {
						int i = KMeansHelper.closestPoint(vector, centroids);
						return new ImmutablePair<Integer, RealVector>(i, vector);
					}
				}, 
				
				data,
				
				// group by cluster id and average the vectors within each cluster to compute centroids
				new GridReducer<Pair<Integer, RealVector>, Map<Integer, List<RealVector>>>() {	
					Map<Integer, List<RealVector>> result = new HashMap<>();
					Map<Integer, List<RealVector>> synchroResult = Collections.synchronizedMap(result);
					
					@Override
					public boolean collect(Pair<Integer, RealVector> pair) {
						//System.out.println(String.format("collect pair: %s", pair));
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
				}).get();
		    	
		    	Map<Integer, RealVector> newCentroids = g.compute().apply(new GridClosure<Pair<Integer, List<RealVector>>, Pair<Integer, RealVector>>() {

					@Override
					public Pair<Integer, RealVector> apply(Pair<Integer, List<RealVector>> pair) {
						return new ImmutablePair<Integer, RealVector>(pair.getLeft(),KMeansHelper.average(pair.getRight()));
					}
				},
				toListOfPairs(pointsGroup.entrySet()),
				
				new GridReducer<Pair<Integer, RealVector>, Map<Integer, RealVector>>() {
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
				}).get();
		    	
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
	

	
	private static Collection<? extends Pair<Integer, List<RealVector>>> toListOfPairs(Set<Entry<Integer, List<RealVector>>> entrySet) {
		List<Pair<Integer, List<RealVector>>> out = new LinkedList<>();
		
		for(Entry<Integer, List<RealVector>> entry: entrySet){
			out.add(new ImmutablePair<Integer, List<RealVector>>(entry.getKey(), entry.getValue()));
		}
		
		return out;
	}

}

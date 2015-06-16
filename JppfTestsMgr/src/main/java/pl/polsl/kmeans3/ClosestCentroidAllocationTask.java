package pl.polsl.kmeans3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.jppf.node.protocol.AbstractTask;

import pl.polsl.kmeans.KMeansHelper;

public class ClosestCentroidAllocationTask extends AbstractTask<Map<Integer, RealVector>> {
	
	private static final long serialVersionUID = 7651750573322796575L;
	private List<RealVector> vectors;
	private List<RealVector> centroids;
	private Integer key;
	
	public ClosestCentroidAllocationTask(Integer key, List<RealVector> vectors, List<RealVector> centroids){
		this.vectors = vectors;
		this.centroids = centroids;
		this.key = key;
	}
	
	@Override
	public void run() {
		/*
		 * Store sent vectors in local-cache
		 */
		System.out.println("********* RUN *********");
		List<RealVector> tmpVectors = null;
		if(vectors == null){
			System.out.println("Get vectors from local cache");
			// it's not first iteration - get data from local cache
			tmpVectors = LocalVectorsCacheStore.getInstance().getVectors().get(key);
		}
		else{
			// store data to local cache
			System.out.println(String.format("Store vectors in local cache. Key: %s, vectors size: %s", key, vectors.size()));
			LocalVectorsCacheStore.getInstance().getVectors().put(key, vectors);
			tmpVectors = vectors;
		}
		
		System.out.println("after reading/writing cache");
		
		List<Pair<Integer, RealVector>> tmp = new LinkedList<>();
		for(RealVector vector: tmpVectors){
			int i = KMeansHelper.closestPoint(vector, this.centroids);
			System.out.println(String.format("Closest point: %s", i));
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
		
		setResult(result);
	}

}

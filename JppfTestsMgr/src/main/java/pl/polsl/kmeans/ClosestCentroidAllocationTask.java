package pl.polsl.kmeans;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.jppf.node.protocol.AbstractTask;

public class ClosestCentroidAllocationTask extends AbstractTask<List<Pair<Integer, RealVector>>> {
	
	private static final long serialVersionUID = 7651750573322796575L;
	private List<RealVector> vectors;
	private List<RealVector> centroids;
	
	public ClosestCentroidAllocationTask(List<RealVector> vectors, List<RealVector> centroids){
		this.vectors = vectors;
		this.centroids = centroids;
	}
	
	@Override
	public void run() {
		List<Pair<Integer, RealVector>> result = new LinkedList<>();
		for(RealVector vector: vectors){
			int i = KMeansHelper.closestPoint(vector, this.centroids);
			System.out.println(String.format("Closest point: %s", i));
			result.add(new ImmutablePair<Integer, RealVector>(i, vector));
		}
		
		setResult(result);
	}

}

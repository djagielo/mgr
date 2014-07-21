package pl.polsl.kmeans;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.jppf.node.protocol.AbstractTask;

public class ClosestCentroidAllocationTask extends AbstractTask<Pair<Integer, RealVector>> {
	
	private static final long serialVersionUID = 7651750573322796575L;
	private RealVector vector;
	private List<RealVector> centroids;
	
	public ClosestCentroidAllocationTask(RealVector vector, List<RealVector> centroids){
		this.vector = vector;
		this.centroids = centroids;
	}
	
	@Override
	public void run() {
		int i = KMeansHelper.closestPoint(this.vector, this.centroids);
		System.out.println(String.format("Closest point: %s", i));
		setResult(new ImmutablePair<Integer, RealVector>(i, vector));
	}

}

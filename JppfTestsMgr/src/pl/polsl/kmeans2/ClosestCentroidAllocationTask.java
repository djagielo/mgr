package pl.polsl.kmeans2;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.jppf.node.protocol.AbstractTask;
import org.jppf.node.protocol.DataProvider;

import pl.polsl.kmeans.KMeansHelper;

public class ClosestCentroidAllocationTask extends AbstractTask<List<Pair<Integer, RealVector>>> {
	
	private static final long serialVersionUID = 7651750573322796575L;
	private List<Integer> vectorIds;
	private List<RealVector> centroids;
	
	public ClosestCentroidAllocationTask(List<Integer> vectorIds, List<RealVector> centroids){
		this.vectorIds = vectorIds;
		this.centroids = centroids;
	}
	
	@Override
	public void run() {
		List<Pair<Integer, RealVector>> result = new LinkedList<>();
		List<RealVector> vectors = getVectorsFromDataProvider(this.vectorIds);
		for(RealVector vector: vectors){
			int i = KMeansHelper.closestPoint(vector, this.centroids);
			//System.out.println(String.format("Closest point: %s", i));
			result.add(new ImmutablePair<Integer, RealVector>(i, vector));
		}
		
		setResult(result);
	}

	private List<RealVector> getVectorsFromDataProvider(List<Integer> vectorIds2) {
		long start = System.currentTimeMillis();
		DataProvider dataProvider = getDataProvider();
		List<RealVector> result = new LinkedList<>();
		List<RealVector> data = dataProvider.getParameter("data");
		for(Integer id: vectorIds2){
			result.add(data.get(id));
		}
		System.out.println(String.format("getVectorsFromDataProvider: %s[ms]", (System.currentTimeMillis() - start)));
		return result;
	}

}

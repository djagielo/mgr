package pl.polsl.kmeans2;

import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.jppf.node.protocol.AbstractTask;

import pl.polsl.kmeans.KMeansHelper;

public class NewCentroidsTask extends AbstractTask<Pair<Integer, RealVector>> {
	private static final long serialVersionUID = -7070093936300520265L;
	
	private Pair<Integer, List<RealVector>> pair;
	public NewCentroidsTask(Pair<Integer, List<RealVector>> pair){
		this.pair = pair;
	}
	
	@Override
	public void run() {
		setResult(new ImmutablePair<Integer, RealVector>(pair.getLeft(),KMeansHelper.average(pair.getRight())));
	}

}

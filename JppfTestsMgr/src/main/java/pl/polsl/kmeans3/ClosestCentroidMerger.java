package pl.polsl.kmeans3;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.linear.RealVector;
import org.jppf.node.protocol.Task;

import pl.polsl.kmeans.utils.AbstractMerger;

public class ClosestCentroidMerger extends AbstractMerger<Map<Integer, List<RealVector>>> {

	public ClosestCentroidMerger(AtomicInteger mergedTaskCounter) {
		super(mergedTaskCounter);
	}

	@Override
	public Runnable getMerger(final List<Task<?>> tasks) {
		Runnable result = new Runnable() {
			
			@Override
			public void run() {
				//System.out.println("*** MERGER ***");
				if(tasks != null){
					for(Task<?> task: tasks){
						if(task instanceof ClosestCentroidAllocationTask){
							ClosestCentroidAllocationTask t = (ClosestCentroidAllocationTask) task;
							Map<Integer, RealVector> pairs = t.getResult();
							for(Entry<Integer, RealVector> pair: pairs.entrySet()){
								if(getMergedResults().containsKey(pair.getKey())){
									List<RealVector> results = getMergedResults().get(pair.getKey());
									results.add(pair.getValue());
									getMergedResults().put(pair.getKey(), results);
				
								}
								else{
									List<RealVector> values = new LinkedList<RealVector>();
									values.add(pair.getValue());
									getMergedResults().put(pair.getKey(), values);
								}
							}
						}
						
						mergedTasksCounter.addAndGet(1);
					}
				}
			}
		};
		
		return result;
	}

	@Override
	public Map<Integer, List<RealVector>> createResultCollection() {
		return new HashMap<Integer, List<RealVector>>();
	}
}

package pl.polsl.kmeans;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.jppf.client.event.JobEvent;
import org.jppf.client.event.JobListenerAdapter;
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
							Pair<Integer, RealVector> pair = t.getResult();
							
							if(getMergedResults().containsKey(pair.getLeft())){
								List<RealVector> results = getMergedResults().get(pair.getLeft());
								results.add(pair.getRight());
								getMergedResults().put(pair.getLeft(), results);
			
							}
							else{
								List<RealVector> values = new LinkedList<RealVector>();
								values.add(pair.getRight());
								getMergedResults().put(pair.getLeft(), values);
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

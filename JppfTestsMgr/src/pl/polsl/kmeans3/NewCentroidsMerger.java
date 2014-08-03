package pl.polsl.kmeans3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.jppf.node.protocol.Task;

import pl.polsl.kmeans.utils.AbstractMerger;


public class NewCentroidsMerger extends AbstractMerger<Map<Integer, RealVector>>{

	public NewCentroidsMerger(AtomicInteger mergedTaskCounter) {
		super(mergedTaskCounter);
	}

	@Override
	public Runnable getMerger(final List<Task<?>> tasks) {
		Runnable result = new Runnable() {
			
			@Override
			public void run() {
				if(tasks != null){
					for(Task<?> task: tasks){
						if(task instanceof NewCentroidsTask){
							NewCentroidsTask t = (NewCentroidsTask)task;
							Pair<Integer, RealVector> pair = t.getResult();
							getMergedResults().put(pair.getLeft(), pair.getRight());
						}
						
						mergedTasksCounter.addAndGet(1);
					}
				}
			}
		};
		
		return result;
	}

	@Override
	public Map<Integer, RealVector> createResultCollection() {
		return new HashMap<Integer, RealVector>();
	}


}

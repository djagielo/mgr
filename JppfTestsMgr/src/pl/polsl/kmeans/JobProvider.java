package pl.polsl.kmeans;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.ml.clustering.CentroidCluster;
import org.jppf.JPPFException;
import org.jppf.client.JPPFJob;

public class JobProvider {
	
	private AtomicInteger submittedTasks;
	private AtomicInteger finishedTasks;
	
	private ClosestCentroidMerger closestCentroidsMerger;
	private NewCentroidsMerger newCentroidsMerger;
	
	public JobProvider(){
		this.submittedTasks = new AtomicInteger();
		this.finishedTasks = new AtomicInteger();
	}
	
	public JPPFJob createClosestCentroidsJob(List<RealVector> data, List<RealVector> centroids) throws JPPFException{
		submittedTasks.set(0);
		finishedTasks.set(0);
		JPPFJob job = new JPPFJob();
    	job.setName("closestCentroids");
    	this.closestCentroidsMerger = new ClosestCentroidMerger(finishedTasks);
    	job.addJobListener(this.closestCentroidsMerger);
    	for(RealVector vector: data){
    		job.add(new ClosestCentroidAllocationTask(vector, centroids), null);
    		submittedTasks.addAndGet(1);
    	}
    	
    	return job;
	}
	
	public JPPFJob createGroupByAndAverageJob(Collection<? extends Pair<Integer, List<RealVector>>> pairs) throws JPPFException{
		submittedTasks.set(0);
		finishedTasks.set(0);
		JPPFJob job = new JPPFJob();
    	job.setName("groupByAndAverage");
    	this.newCentroidsMerger = new NewCentroidsMerger(finishedTasks);
    	job.addJobListener(this.newCentroidsMerger);
    	for(Pair<Integer, List<RealVector>> pair: pairs){
    		job.add(new NewCentroidsTask(pair), null);
    		submittedTasks.addAndGet(1);
    	}
    	
    	return job;
	}
	
	public int getProcessedTasksCount(){
		return this.finishedTasks.get();
	}
	
	public int getSubmittedTasksCount(){
		return this.submittedTasks.get();
	}

	public ClosestCentroidMerger getClosestCentroidsMerger() {
		return closestCentroidsMerger;
	}

	public void setClosestCentroidsMerger(
			ClosestCentroidMerger closestCentroidsMerger) {
		this.closestCentroidsMerger = closestCentroidsMerger;
	}

	public NewCentroidsMerger getNewCentroidsMerger() {
		return newCentroidsMerger;
	}

	public void setNewCentroidsMerger(NewCentroidsMerger newCentroidsMerger) {
		this.newCentroidsMerger = newCentroidsMerger;
	}

}

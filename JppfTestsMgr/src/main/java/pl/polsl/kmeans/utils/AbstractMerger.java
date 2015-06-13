package pl.polsl.kmeans.utils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.jppf.client.event.JobEvent;
import org.jppf.client.event.JobListenerAdapter;
import org.jppf.node.protocol.Task;

public abstract class AbstractMerger<T> extends JobListenerAdapter{
	protected AtomicInteger mergedTasksCounter;
	protected ExecutorService executor = Executors.newSingleThreadExecutor();
	protected T mergedResults;
	public AbstractMerger(AtomicInteger mergedTaskCounter){
		this.mergedTasksCounter = mergedTaskCounter;
		this.mergedResults = createResultCollection();
	}
	
	@Override
	public void jobReturned(JobEvent event) {
		if (event.getJobTasks() != null) executor.submit(getMerger(event.getJobTasks()));
	}
	
	public abstract Runnable getMerger(final List<Task<?>> tasks);
	
	public abstract T createResultCollection();
	
	public void incrementMergedTasks(){
		this.mergedTasksCounter.addAndGet(1);
	}

	public T getMergedResults() {
		return mergedResults;
	}

	public void setMergedResults(T mergedResults) {
		this.mergedResults = mergedResults;
	}
	
}

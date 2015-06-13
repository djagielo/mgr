package pl.polsl.kmeans.data;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.linear.RealVector;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;

import pl.polsl.data.RealVectorDataPreparator;
import pl.polsl.kmeans.KMeansHelper;

public class ApacheIgniteKmeansDataStreamer {
	private static final String SPLIT_MARK = ",";
	
	private File file;
	private Ignite ignite;
	private String cacheName;
	private int parts;
	private List<List<RealVector>> allData = null;
	
	public ApacheIgniteKmeansDataStreamer(Ignite ignite, String file, String cacheName, int parts){
		this.ignite = ignite;
		this.file = new File(file);
		this.cacheName = cacheName;
		this.parts = parts;
	}
	
	public void load(){
		try(IgniteDataStreamer streamer = ignite.dataStreamer(cacheName)){
			RealVectorDataPreparator dp = new RealVectorDataPreparator(file.getAbsolutePath(), SPLIT_MARK);
			this.allData = dp.getAllData(parts);
			
			final AtomicInteger i = new AtomicInteger(0);
			streamer.allowOverwrite(true);
			this.allData.forEach(el -> streamer.addData(i.getAndIncrement(), el));
		}
		finally{
			
		}
	}

	public List<RealVector> takeSample(int k){
		if(this.allData != null){
			return KMeansHelper.takeSample(this.allData, k);
		}
		else{
			throw new IllegalStateException("First use load() method to load data before taking samples");
		}
	}
}

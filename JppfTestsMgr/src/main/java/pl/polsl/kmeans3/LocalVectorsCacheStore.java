package pl.polsl.kmeans3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.linear.RealVector;

public class LocalVectorsCacheStore {
	private Map<Integer, List<RealVector>> vectors;
	
	private static LocalVectorsCacheStore instance;
	static{
		instance = new LocalVectorsCacheStore();
	}
	
	private LocalVectorsCacheStore(){
		vectors = new HashMap<Integer, List<RealVector>>();
	}
	
	public static LocalVectorsCacheStore getInstance(){
		return instance;
	}

	public Map<Integer,List<RealVector>> getVectors() {
		return vectors;
	}

	public void setVectors(Map<Integer, List<RealVector>> vectors) {
		this.vectors = vectors;
	}	
}

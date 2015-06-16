package pl.polsl.data.beans;

import java.util.List;

public class CacheEntry<T> {
	private int key;
	private List<T> vectors;
	public int getKey() {
		return key;
	}
	public void setKey(int key) {
		this.key = key;
	}
	public List<T> getVectors() {
		return vectors;
	}
	public void setVectors(List<T> vectors) {
		this.vectors = vectors;
	}	
}

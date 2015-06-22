package pl.polsl.kmeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import pl.polsl.data.beans.CacheEntry;

public class KMeansHelper implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8959271541874214233L;

	public static int closestPoint(RealVector p, List<RealVector> centers) {
		int bestIndex = 0;
		double closest = Double.POSITIVE_INFINITY;
		for (int i = 0; i < centers.size(); i++) {
			double tempDist = p.getDistance(centers.get(i));
			if (tempDist < closest) {
				closest = tempDist;
				bestIndex = i;
			}
		}
		return bestIndex;
	}

	/** Computes the mean across all vectors in the input set of vectors */
	public static RealVector average(List<RealVector> ps) {
		int numVectors = ps.size();
		RealVector out = new ArrayRealVector(ps.get(0).toArray());
		// start from i = 1 since we already copied index 0 above
		for (int i = 1; i < numVectors; i++) {
			out = out.add(ps.get(i));
		}
		return out.mapDivide(numVectors);
	}

	public static RealVector parseVector(String line, String splitMark) {
		String[] splits = line.split(splitMark);
		double[] data = new double[splits.length];
		int i = 0;
		for (String s : splits)
			data[i] = Double.parseDouble(splits[i++]);
		return new ArrayRealVector(data);
	}

	public static List<RealVector> takeSample(List<?> data, int size){
		List<RealVector> out = new LinkedList<>();
		Random random = new Random(System.currentTimeMillis());
		int s = data.size();
		List<Integer> usedIndexes = new ArrayList<Integer>();
		List<String> usedDoubleIndexes = new ArrayList<String>();
		for (int i = 0; i < size; i++) {
			int randomIndex = -1;
			if(s > 1){
				do {
					randomIndex = random.nextInt(s - 1);
				} while (randomIndex < 0 || usedIndexes.contains(randomIndex));
			}
			else{
				randomIndex = 0;
			}
			if(data.get(randomIndex) instanceof RealVector){
				out.add((RealVector)data.get(randomIndex));
				usedIndexes.add(randomIndex);
			}
			else if(data.get(randomIndex) instanceof List<?>){
				int randomIndexSecond = -1;
				RealVector randomVector = null;
				do {
					randomIndexSecond = random.nextInt(((List<?>)data.get(randomIndex)).size() - 1);
					randomVector = (RealVector) ((List<RealVector>)data.get(randomIndex)).get(randomIndexSecond);
				} while (usedDoubleIndexes.contains(String.format("%s_%s", randomIndex, randomIndexSecond)) || out.contains(randomVector));
				out.add(randomVector);
				String key = String.format("%s_%s", randomIndex, randomIndexSecond);
				usedDoubleIndexes.add(key);
			}
			else if(data.get(randomIndex) instanceof CacheEntry<?>){
				int randomIndexSecond = -1;
				RealVector randomVector = null;
				do {
					randomIndexSecond = random.nextInt(((CacheEntry<?>)data.get(randomIndex)).getVectors().size() - 1);
					randomVector = (RealVector) ((CacheEntry<RealVector>)data.get(randomIndex)).getVectors().get(randomIndexSecond);
				} while (usedDoubleIndexes.contains(String.format("%s_%s", randomIndex, randomIndexSecond)) || out.contains(randomVector));
				out.add(randomVector);
				String key = String.format("%s_%s", randomIndex, randomIndexSecond);
				usedDoubleIndexes.add(key);
			}
			
		}

		return out;
	}

	public static Collection<? extends Pair<Integer, List<RealVector>>> toListOfPairs(
			Set<Entry<Integer, List<RealVector>>> entrySet) {
		List<Pair<Integer, List<RealVector>>> out = new LinkedList<>();

		for (Entry<Integer, List<RealVector>> entry : entrySet) {
			out.add(new ImmutablePair<Integer, List<RealVector>>(entry.getKey(), entry.getValue()));
		}

		return out;
	}
	
	public static void checkDuplicates(List<RealVector> in) throws Exception{
		for(int i=0; i < in.size(); i++){
			for(int j=0; j < in.size(); j++){
				if(i != j){
					if(in.get(i).equals(in.get(j)))
						throw new Exception("DUPLICATE");
				}
			}
		}
	}
	
	public static Map<Integer, RealVector> averagePartialCentroids(List<Map<Integer, RealVector>> partialCentroids) {
		Map<Integer, List<RealVector>> tmp = new HashMap<>();
		
		partialCentroids.forEach(el -> {
			el.entrySet().forEach(mapEl -> {
				if(tmp.containsKey(mapEl.getKey())){
					List<RealVector> tmpList = tmp.get(mapEl.getKey());
					tmpList.add(mapEl.getValue());
				}
				else{
					List<RealVector> tmpList = new LinkedList<>();
					tmpList.add(mapEl.getValue());
					tmp.put(mapEl.getKey(), tmpList);
				}
			});
		});
		
		Map<Integer, RealVector> result =new HashMap<>();
		
		tmp.entrySet().stream().forEach(entry -> {
			result.put(entry.getKey(), KMeansHelper.average(entry.getValue()));
		});
		
		return result;
	}
}

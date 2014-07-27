package pl.polsl.data;

import org.apache.commons.math3.linear.RealVector;

import pl.polsl.kmeans.KMeansHelper;

public class RealVectorDataPreparator extends AbstractDataPreparator<RealVector> {
	private String splitMark = "";
	public RealVectorDataPreparator(String file, String splitMark) {
		super(file);
		this.splitMark = splitMark;
	}

	@Override
	public RealVector createObjectFromString(String str) {
		return KMeansHelper.parseVector(str, splitMark);
	}

}

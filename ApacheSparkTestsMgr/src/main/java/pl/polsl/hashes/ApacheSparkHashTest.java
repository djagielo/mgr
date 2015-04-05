package pl.polsl.hashes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import pl.polsl.data.StringDataPreparator;
import pl.polsl.utils.hashes.AvailableHashes;
import pl.polsl.utils.hashes.MultipleHashUtil;

public class ApacheSparkHashTest{
	private static final AvailableHashes [] ALL_HASHES_ARRAY = {AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384};
	
	public static void main(String[] args) {
		if (args.length < 3) {
		      System.err.println("Usage: ApacheSparkHashTest <master> <file> <partitionSize>");
		      System.exit(1);    
		}
		String master = args[0];
		String file = args[1];
		Integer partitionSize = Integer.parseInt(args[2]);
		 
		SparkConf sparkConf = new SparkConf().setAppName("ApacheSparkHashTest").setMaster(master);
	    JavaSparkContext jsc = new JavaSparkContext(sparkConf);
	    
	    List<List<String>> data = prepareDataForTest(partitionSize, file);
	    
	    long start = System.currentTimeMillis();
	    JavaRDD<List<String>> parallelData = jsc.parallelize(data);
	    
	    @SuppressWarnings("resource")
		JavaRDD<Map<String, Map<String, String>>> result = parallelData.map( particle -> {
				MultipleHashUtil hashUtil = new MultipleHashUtil(ALL_HASHES_ARRAY);
				Map<String, Map<String, String>> results = new HashMap<>();
				for(String s: particle){
					System.out.println(String.format("Computing hashes for %s", s));
					results.put(s, hashUtil.getHashes(s));
				}
				
				return results;
			}
		);
	    
	    result.collect();
	    
	    if(jsc != null)
	    	jsc.close();
	    System.out.println(String.format("ApacheSparkHashTest executed in: %s[ms]", (System.currentTimeMillis() - start)));
	}
	
	private static List<List<String>> prepareDataForTest(int partitionSize, String path){
		StringDataPreparator dp = new StringDataPreparator(path);
		
		return dp.getPartitionedData(partitionSize);
	}

}

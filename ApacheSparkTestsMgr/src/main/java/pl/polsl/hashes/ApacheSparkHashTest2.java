package pl.polsl.hashes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import pl.polsl.data.ByteArrayDataPreparator;
import pl.polsl.utils.hashes.AvailableHashes;
import pl.polsl.utils.hashes.MultipleHashUtil;

public class ApacheSparkHashTest2{
	private static final AvailableHashes [] ALL_HASHES_ARRAY = {AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384};
	
	public static void main(String[] args) {
		if (args.length < 3) {
		      System.err.println("Usage: ApacheSparkHashTest <master> <file> <minPartitions>");
		      System.exit(1);    
		}
		String master = args[0];
		String file = args[1];
		Integer partitionSize = Integer.parseInt(args[2]);
		 
		SparkConf sparkConf = new SparkConf().setAppName("ApacheSparkHashTest").setMaster(master);
	    JavaSparkContext jsc = new JavaSparkContext(sparkConf);
	    
	    //List<List<byte[]>> data = prepareDataForTest(partitionSize, file);
	    
	    long start = System.currentTimeMillis();
	    JavaRDD<String> parallelData = jsc.textFile(file, partitionSize);
	   
	    JavaRDD<Map<String, Map<String, byte[]>>> result = parallelData.map(new Function<String, Map<String, Map<String, byte[]>>>() {
			private static final long serialVersionUID = -8810693240842746512L;

			@Override
			public Map<String, Map<String, byte[]>> call(String word)throws Exception {
				MultipleHashUtil hashUtil = new MultipleHashUtil(ALL_HASHES_ARRAY);
				Map<String, Map<String, byte[]>> results = new HashMap<>();

				System.out.println(String.format("Computing hashes for %s", word));
				results.put(word, hashUtil.getHashes(word));

				return results;
			}
		});
	    
	    result.collect();
	    
	    if(jsc != null)
	    	jsc.close();
	    System.out.println("*\n*\n*\n*\n");
	    System.out.println(String.format("ApacheSparkHashTest executed in: %s[ms]", (System.currentTimeMillis() - start)));
	    System.out.println("*\n*\n*\n*\n");
	}
	
	private static List<List<byte[]>> prepareDataForTest(int partitionSize, String path){
		//StringDataPreparator dp = new StringDataPreparator(path);
		ByteArrayDataPreparator dp = new ByteArrayDataPreparator(path);
		return dp.getPartitionedData(partitionSize);
	}

}

package pl.polsl.hashes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.Ignition;
import org.apache.ignite.lang.IgniteClosure;
import org.apache.log4j.Logger;

import pl.polsl.data.ByteArrayDataPreparator;
import pl.polsl.utils.hashes.AvailableHashes;
import pl.polsl.utils.hashes.MultipleHashUtil;

public class ApacheIgniteHashTest {
	private static Logger logger = Logger.getLogger(ApacheIgniteHashTest.class);
	private static final AvailableHashes [] ALL_HASHES_ARRAY = {AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384};
	
	public static void main(String[] args) throws IgniteException {
		if (args.length < 3) {
		      System.err.println("Usage: GridGainHashTest <config> <file> <partitionSize>");
		      System.exit(1);    
		}
		
		String config = args[0];
		String file = args[1];
		Integer partitionSize = Integer.parseInt(args[2]);
		
		try (Ignite g = Ignition.start(config)) {
			List<List<byte[]>> data = prepareDataForTest(file, partitionSize);
			long start = System.currentTimeMillis();
			Collection<Map<String, Map<String, byte[]>>> result = g.compute(g.cluster().forRemotes()).apply(new IgniteClosure<List<byte[]>, Map<String, Map<String, byte[]>>>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Map<String, Map<String, byte[]>> apply(List<byte[]> particle) {
					MultipleHashUtil hashUtil = new MultipleHashUtil(ALL_HASHES_ARRAY);
					Map<String, Map<String, byte[]>> results = new HashMap<>();
					for(byte[] s: particle){
						System.out.println(String.format("Computing hashes for %s", s));
						results.put(new String(s), hashUtil.getHashes(s));
					}
					
					return results;
				}
			}, data);
			  
			System.out.println(String.format("*\n*\n*\nGridGainHashTest executed in: %s[ms]*\n*\n*\n", (System.currentTimeMillis() - start)));
		}
	}
	
	private static List<List<byte[]>> prepareDataForTest(String path, int partitionSize){
		//StringDataPreparator dp = new StringDataPreparator(path);
		ByteArrayDataPreparator dp = new ByteArrayDataPreparator(path);
		return dp.getPartitionedData(partitionSize);
	}

}

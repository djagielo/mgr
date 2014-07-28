package pl.polsl.hashes;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.lang.GridClosure;

import pl.polsl.data.StringDataPreparator;
import pl.polsl.utils.hashes.AvailableHashes;
import pl.polsl.utils.hashes.MultipleHashUtil;

public class GridGainHashTest {
	private static Logger logger = Logger.getLogger(GridGainHashTest.class);
	private static final AvailableHashes [] ALL_HASHES_ARRAY = {AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384};
	
	public static void main(String[] args) throws GridException {
		if (args.length < 3) {
		      System.err.println("Usage: GridGainHashTest <config> <file> <partitionSize>");
		      System.exit(1);    
		}
		
		String config = args[0];
		String file = args[1];
		Integer partitionSize = Integer.parseInt(args[2]);
		
		try (Grid g = GridGain.start(config)) {
			List<List<String>> data = prepareDataForTest(file, partitionSize);
			long start = System.currentTimeMillis();
			Collection<Map<String, Map<String, String>>> result = g.compute().apply(new GridClosure<List<String>, Map<String, Map<String, String>>>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Map<String, Map<String, String>> apply(List<String> particle) {
					MultipleHashUtil hashUtil = new MultipleHashUtil(ALL_HASHES_ARRAY);
					Map<String, Map<String, String>> results = new HashMap<>();
					for(String s: particle){
						System.out.println(String.format("Computing hashes for %s", s));
						results.put(s, hashUtil.getHashes(s));
					}
					
					return results;
				}
			}, data).get();
			  
			System.out.println(String.format("GridGainHashTest executed in: %s[ms]", (System.currentTimeMillis() - start)));
		}
	}
	
	private static List<List<String>> prepareDataForTest(String path, int partitionSize){
		StringDataPreparator dp = new StringDataPreparator(path);
		
		return dp.getPartitionedData(partitionSize);
	}

}

package pl.polsl.hashes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.lang.GridClosure;

import pl.polsl.utils.hashes.AvailableHashes;
import pl.polsl.utils.hashes.DataPreparator;
import pl.polsl.utils.hashes.MultipleHashUtil;

public class GridGainHashTest {
	private static Logger logger = Logger.getLogger(GridGainHashTest.class);
	private static final int PARTITION_SIZE = 100;
	//private static String PATH_TO_DICTIONARY = "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\rockyou.txt";
	private static String PATH_TO_DICTIONARY = "testdata/john.txt";
	private static final AvailableHashes [] ALL_HASHES_ARRAY = {AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384};
	
	public static void main(String[] args) throws GridException {
		try (Grid g = GridGain.start()) {
			List<List<String>> data = prepareDataForTest();
			
			g.compute().apply(new GridClosure<List<String>, Map<String, Map<String, String>>>() {
				private static final long serialVersionUID = 1L;

				@Override
				public Map<String, Map<String, String>> apply(List<String> particle) {
					//HashUtil hashUtil = new HashUtil();
					MultipleHashUtil hashUtil = new MultipleHashUtil(ALL_HASHES_ARRAY);
					Map<String, Map<String, String>> results = new HashMap<>();
					for(String s: particle){
						System.out.println(String.format("Computing hashes for %s", s));
						/*try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}*/
						results.put(s, hashUtil.getHashes(s));
					}
					
					return results;
				}
			}, data);
		}
	}
	
	private static List<List<String>> prepareDataForTest(){
		DataPreparator dp = new DataPreparator(PATH_TO_DICTIONARY);
		
		return dp.getPartitionedData(PARTITION_SIZE);
	}

}

package pl.polsl.hashes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.Task;
import org.jppf.server.protocol.JPPFTask;

import pl.polsl.utils.hashes.AvailableHashes;
import pl.polsl.utils.hashes.DataPreparator;
import pl.polsl.utils.hashes.HashUtil;
import pl.polsl.utils.hashes.MultipleHashUtil;

public class HashTest {
	private static Logger logger = Logger.getLogger(HashTest.class);
	private static final int PARTITION_SIZE = 100;
	//private static String PATH_TO_DICTIONARY = "X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\rockyou.txt";
	private static String PATH_TO_DICTIONARY = "testdata/john.txt";
	private static final AvailableHashes [] ALL_HASHES_ARRAY = {AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384};
	
	public static void main(String[] args) {
		try(JPPFClient client = new JPPFClient()){
			JPPFJob job = new JPPFJob();
			job.setName("ComputeHashJob");
			
			//List<List<String>> data = prepareFakeDataForTest();
			List<List<String>> data = prepareDataForTest();
			
			logger.debug("Adding tasks to job");
			for(final List<String> particle: data){
					job.add(new JPPFTask() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void run() {
						
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
						setResult(results);
						//setResult(String.format("Hash of string 'RESULT': %s",hashUtil.getHash("RESULT")));
					}
				});
			}	
			
			job.setBlocking(true);
			
			System.out.println("Job submit");
			
			long start = System.currentTimeMillis();
			List<Task<?>> results = client.submitJob(job);
			System.out.println(String.format("Tasks executed in %s[ms]", (System.currentTimeMillis() - start)));
			
			for(Task<?> result: results){
				//System.out.println(String.valueOf(result.getResult()));
			}
			
		} catch (JPPFException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static List<List<String>> prepareFakeDataForTest() {
		List<List<String>> out = new ArrayList<>();
		
		out.add(new ArrayList<String>(Arrays.asList("test1", "test2", "test3")));
		
		return out;
	}
	
	private static List<List<String>> prepareDataForTest(){
		DataPreparator dp = new DataPreparator(PATH_TO_DICTIONARY);
		
		return dp.getPartitionedData(PARTITION_SIZE);
	}

}

package pl.polsl.hashes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.Task;
import org.jppf.server.protocol.JPPFTask;

import pl.polsl.data.StringDataPreparator;
import pl.polsl.utils.hashes.AvailableHashes;
import pl.polsl.utils.hashes.MultipleHashUtil;

public class JppfHashTest {
	private static Logger logger = Logger.getLogger(JppfHashTest.class);
	private static final AvailableHashes [] ALL_HASHES_ARRAY = {AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384};
	
	public static void main(String[] args) {
		if (args.length < 2) {
		      System.err.println("Usage: JppfHashTest <file> <partitionSize>");
		      System.exit(1);    
		}
		
		String file = args[0];
		Integer partitionSize = Integer.parseInt(args[1]);
		
		try(JPPFClient client = new JPPFClient()){
			JPPFJob job = new JPPFJob();
			job.setName("JppfHashTest");
			
			List<List<String>> data = prepareDataForTest(file, partitionSize);
			
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
							results.put(s, hashUtil.getHashes(s));
						}
						setResult(results);
					}
				});
			}	
			
			job.setBlocking(true);
			
			System.out.println("Job submit");
			
			long start = System.currentTimeMillis();
			List<Task<?>> results = client.submitJob(job);
			System.out.println(String.format("JppfHashTest executed in %s[ms]", (System.currentTimeMillis() - start)));
			
		} catch (JPPFException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<List<String>> prepareDataForTest(String path, int partitionSize){
		StringDataPreparator dp = new StringDataPreparator(path);
		
		return dp.getPartitionedData(partitionSize);
	}

}

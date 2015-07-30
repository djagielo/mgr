package pl.polsl.hashes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.JPPFTask;
import org.jppf.node.protocol.Task;

import pl.polsl.data.ByteArrayDataPreparator;
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
			
			List<List<byte[]>> data = prepareDataForTest(file, partitionSize);
			
			long start = System.currentTimeMillis();
			
			logger.debug("Adding tasks to job");
			for(final List<byte[]> particle: data){
					job.add(new JPPFTask() {
					
					private static final long serialVersionUID = 1L;

					@Override
					public void run() {
						
						//HashUtil hashUtil = new HashUtil();
						MultipleHashUtil hashUtil = new MultipleHashUtil(ALL_HASHES_ARRAY);
						Map<String, Map<String, byte[]>> results = new HashMap<>();
						for(byte[] s: particle){
							System.out.println(String.format("Computing hashes for %s", s));
							results.put(new String(s), hashUtil.getHashes(s));
						}
						setResult(results);
					}
				});
			}	
			
			job.setBlocking(true);
			
			System.out.println(String.format("*\n*\n*\nJob submit after %s[ms]*\n*\n*\n", (System.currentTimeMillis() - start)));
			
			List<Map<String, Map<String, byte[]>>> result = new ArrayList<>();
			List<Task<?>> results = client.submitJob(job);
			for(Task<?> resultTask: results){
				result.add((Map<String, Map<String, byte[]>>)resultTask.getResult());
			}
			System.out.println(String.format("JppfHashTest executed in %s[ms]", (System.currentTimeMillis() - start)));
			
		} catch (JPPFException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static List<List<byte[]>> prepareDataForTest(String path, int partitionSize){
		//StringDataPreparator dp = new StringDataPreparator(path);
		ByteArrayDataPreparator dp = new ByteArrayDataPreparator(path);
		return dp.getPartitionedData(partitionSize);
	}

}

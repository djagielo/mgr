package pl.polsl.hashes;

import java.util.List;

import org.jppf.JPPFException;
import org.jppf.client.JPPFClient;
import org.jppf.client.JPPFJob;
import org.jppf.node.protocol.Task;
import org.jppf.server.protocol.JPPFTask;

import pl.polsl.utils.hashes.HashUtil;

public class HashTest {
	public static void main(String[] args) {
		try(JPPFClient client = new JPPFClient()){
			JPPFJob job = new JPPFJob();
			job.setName("HelloWorldJob");
			
			job.addTask(new JPPFTask() {
				
				private static final long serialVersionUID = 1L;

				@Override
				public void run() {
					System.out.println("*** Test ***");
					HashUtil hashUtil = new HashUtil();
					setResult(String.format("Hash of string 'RESULT': %s",hashUtil.getHash("RESULT")));
				}
			});
			
			job.setBlocking(true);
			
			List<Task<?>> results = client.submitJob(job);
			
			for(Task<?> result: results){
				System.out.println(String.valueOf(result.getResult()));
			}
			
		} catch (JPPFException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

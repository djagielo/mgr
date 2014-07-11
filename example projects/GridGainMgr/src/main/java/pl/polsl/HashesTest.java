package pl.polsl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.gridgain.grid.Grid;
import org.gridgain.grid.GridException;
import org.gridgain.grid.GridGain;
import org.gridgain.grid.lang.GridCallable;
import org.gridgain.grid.lang.GridClosure;
import org.gridgain.grid.resources.GridInstanceResource;

import pl.polsl.utils.DataPreparator;
import pl.polsl.utils.HashUtil;


public class HashesTest {

	public static void main(String[] args) throws GridException, IOException {
		long start = System.currentTimeMillis();
/*		File dictionaryFile = new File("C:\\Users\\dariu_000\\Desktop\\rockyou.txt");
		FileReader fr = new FileReader(dictionaryFile);
		List<String> dictionary = new LinkedList<>();
		//FileInputStream fis = new FileInputStream(dictionaryFile);
		BufferedReader br = new BufferedReader(fr);
		String line = null;
		
		while((line = br.readLine()) != null){
			dictionary.add(line);
		}*/
	/*	Scanner sc = new Scanner(dictionaryFile);
		while(sc.hasNextLine()){
			dictionary.add(sc.nextLine());
		}*/
		DataPreparator dp = new DataPreparator("C:\\Users\\dariu_000\\Desktop\\rockyou.txt");
		//dp.getAllData();
		List<List<String>> data = dp.getPartitionedData(1000);
		int size = 0;
		for(List<String> internal: data){
			size += internal.size();
		}
		
		System.out.println(String.format("Reading dictionary size %s in: %s[ms]", size,(System.currentTimeMillis() - start)));
		try(Grid g = GridGain.start("config//example-compute.xml")){
			System.out.println("GridGain started");
			final HashUtil hashUtil = new HashUtil();
			
			g.forRemotes().compute().apply(new GridClosure<List<String>, Map<String, String>>() {
				
				@Override
				public Map<String, String> apply(List<String> passes) {
					Map<String, String> hashes = new HashMap<>();
					System.out.println(String.format("Getting hashes of %s", passes));
					for(String pass: passes){
						hashes.put(pass, hashUtil.getHash(pass));
					}
					return hashes;
				}
			}, data);
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}

}

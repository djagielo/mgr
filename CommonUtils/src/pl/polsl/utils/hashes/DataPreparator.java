package pl.polsl.utils.hashes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;


public class DataPreparator {
	private File dictionaryFile;
	private BufferedReader br;
	public DataPreparator(String file){
		dictionaryFile = new File(file);
		FileReader fr;
		try {
			fr = new FileReader(dictionaryFile);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	public List<String> getAllData(){
		if(br != null){
			List<String> result = new LinkedList<>();
			String line;
			try {
				while((line = br.readLine()) != null){
					result.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				result=null;
			}
			
			return result;
		}
		else
			return null;
	}
	
	public List<List<String>> getPartitionedData(int partitionSize){
		if(br != null && partitionSize > 0){
			List<List<String>> result = new LinkedList<>();
			String line;
			try {
				List<String> embeddedList = new LinkedList<>();
				int counter = 0;
				while((line = br.readLine()) != null){
					embeddedList.add(line);
					counter++;
					if(counter == partitionSize){
						result.add(embeddedList);
						embeddedList = new LinkedList<>();
						counter = 0;
					}
				}
				if(embeddedList != null && embeddedList.size() > 0){
					result.add(embeddedList);
				}
			} catch (IOException e) {
				e.printStackTrace();
				result=null;
			}
			
			return result;
		}
		else
			return null;	
	}
}

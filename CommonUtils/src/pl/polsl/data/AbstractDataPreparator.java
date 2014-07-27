package pl.polsl.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public abstract class AbstractDataPreparator<T> {
	public abstract T createObjectFromString(String str);
	private File dictionaryFile;
	private FileReader fr;
	private BufferedReader br;
	
	public AbstractDataPreparator(String file){
		dictionaryFile = new File(file);
		try {
			fr = new FileReader(dictionaryFile);
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshDataSource() throws FileNotFoundException{
		fr = new FileReader(this.dictionaryFile);
		br = new BufferedReader(this.fr);
	}
	
	public List<T> getAllData(){
		if(br != null){
			List<T> result = new LinkedList<>();
			String line;
			try {
				while((line = br.readLine()) != null){
					result.add(createObjectFromString(line));
				}
			} catch (IOException e) {
				e.printStackTrace();
				result=null;
			}
			finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if(fr != null){
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			return result;
		}
		else
			return null;
	}
	
	public List<List<T>> getPartitionedData(int partitionSize){
		if(br != null && partitionSize > 0){
			List<List<T>> result = new LinkedList<>();
			String line;
			try {
				List<T> embeddedList = new LinkedList<>();
				int counter = 0;
				while((line = br.readLine()) != null){
					embeddedList.add(createObjectFromString(line));
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
			finally{
				if(br != null){
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fr != null){
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			return result;
		}
		else
			return null;	
	}
}

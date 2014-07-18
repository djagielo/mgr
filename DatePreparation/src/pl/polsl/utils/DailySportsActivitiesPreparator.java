package pl.polsl.utils;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


public class DailySportsActivitiesPreparator {

	public static void main(String[] args) throws IOException {
		File basePath = new File("X:\\Politechnika\\Magisterka\\praca magisterka\\dane testowe\\data\\data");
		
		StringBuilder sb = new StringBuilder();
		
		File [] activities = basePath.listFiles();
		
		for(File activity: activities){
			File [] persons = activity.listFiles();
			for(File person: persons){
				File [] segments = person.listFiles();
				if(segments != null && segments.length > 0){
					// pobierz pierwszy segment i dopisz do pliku
					sb.append(FileUtils.readFileToString(segments[0]));
				}
			}
		}
		
		System.out.println(sb.length());
		
		FileUtils.writeStringToFile(new File("X:\\dailySportsActivitiesCumulated.txt"), sb.toString());
	
	}

}

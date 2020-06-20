package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

public class FileCacheManager implements CacheManager<String, String> {
	File file;
	HashSet<String> solvedProblems;
	
	public FileCacheManager(String fileName) {
		solvedProblems = new HashSet<String>();
		file = new File(fileName);
	}

	@Override
	public boolean Search(String problem) {
		return solvedProblems.contains(problem);
	}

	@Override
	public void addCache(String problem, String solution) {
		if(this.Search(problem))
			return;
		
		try {
			file.createNewFile();
			
			PrintWriter writer = new PrintWriter(file);
			writer.println(problem);
			writer.println(solution);
			writer.close();
			
			solvedProblems.add(problem);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getCache(String problem) {
		BufferedReader bufferedReader;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			
			String line;
	        while((line = bufferedReader.readLine()) != null) {
	            if(line.equals(problem)) {
	            	bufferedReader.close();
	            	return bufferedReader.readLine();
	            }
	        }
			
			bufferedReader.close();
		} catch(FileNotFoundException e) {
			// File DNE, do nothing
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}

}

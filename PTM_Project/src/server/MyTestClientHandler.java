package server;

import java.io.*;

public class MyTestClientHandler implements ClientHandler {
	private StringReverser stringReverser;
	private FileCacheManager fileCacheManager;
	
	public MyTestClientHandler() {
		this.stringReverser = new StringReverser();
		this.fileCacheManager = new FileCacheManager("cache.dat");
	}
	
	@Override
	public void handleClient(InputStream in, OutputStream out) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		PrintWriter writer = new PrintWriter(out);
		String line;
		
		while(true) {
			try {
				line = reader.readLine();
				if(line == null)
					continue;
				if(line.equals("end"))
					break;
				
				if(fileCacheManager.Search(line))
					writer.println(fileCacheManager.getCache(line));
				else {
					String solution = stringReverser.solve(line);
					fileCacheManager.addCache(line, solution);
					writer.println(solution);
				}
	
				writer.flush();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

}

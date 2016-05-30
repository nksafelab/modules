package jinke.readings.download;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpDownloader {
	private static URL url = null;
	
	public static String download(String urlStr)throws Exception{
		
		
		//System.out.println("httpdownload------");
		
		StringBuffer sb = new StringBuffer();
		String line = null;
		BufferedReader buffer = null;
		
			url = new URL(urlStr);
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			buffer = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"gb2312"));
			
			while((line = buffer.readLine()) != null){
				sb.append(line);
			}
			
			//System.out.println("httpdownload------"+sb.toString());
		return sb.toString();
	}
	
	
	
	

}

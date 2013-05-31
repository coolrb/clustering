package main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {
	
	public static String http_get(String url){
		URL u = null;
		HttpURLConnection con = null;
		try {
			u = new URL(url);
			con = (HttpURLConnection) u.openConnection();
			con.setRequestMethod("GET");
			con.setDoInput(true);
			con.setUseCaches(false);
			
			
		} catch (Exception e) {
			return null;
			//e.printStackTrace();
		}
		
		StringBuffer buffer = new StringBuffer();
		try {
			if(con.getResponseCode() == HttpURLConnection.HTTP_OK){
				BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String temp = null;
				while ( (temp = br.readLine()) != null ){
					buffer.append(temp);
					buffer.append("\n");
				}
			}
			
		} catch (Exception e) {
			return null;
			//e.printStackTrace();
		}finally{
			if(con != null){
				con.disconnect();
			}
		}
		
		String ret = null;
		try {
			ret = new String(buffer.toString().getBytes("ISO8859_1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
			//e.printStackTrace();
		}
		
		return ret;
	}
}

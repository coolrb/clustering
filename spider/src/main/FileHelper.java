package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileHelper {
	
	public static String read(String fileName){
		
		StringBuffer sb = new StringBuffer();
		String tmp = null;
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			
			while( (tmp = br.readLine()) != null ){
				sb.append(tmp);
				sb.append("\n");
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return sb.toString();
	}
	
	public static void write(String fileName , String content){
		try {
			FileWriter fw = new FileWriter(new File(fileName));
			fw.write(content);
			fw.close();	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

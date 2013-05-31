package main;

import java.security.*;
import org.apache.commons.codec.binary.Base64;
public class NewsItem {
	
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',  
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  

	String url;
	String head;
	String body;
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getHead() {
		return head;
	}
	public void setHead(String head) {
		this.head = head;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public NewsItem(String url, String head, String body) {
		super();
		this.url = url;
		this.head = head;
		this.body = body;
	}
	@Override
	public String toString() {
		return "NewsItem [url=" + url + ", head=" + head + ", body=" + body
				+ "]";
	}
	
	public String toEncryptString(){
		String md5url = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("md5");
			byte[] md5bytes = md5.digest(url.getBytes());
			md5url = new String(encodeHex(md5bytes));
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return md5url+"\t"+getBASE64(head)+"\t"+getBASE64(body);
	}
	
    protected char[] encodeHex(byte[] data) {  
        int l = data.length;  
        char[] out = new char[l << 1];  
        for (int i = 0, j = 0; i < l; i++) {  
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];  
            out[j++] = DIGITS_LOWER[0x0F & data[i]];  
        }  
        return out;  
    }  
    
    protected String getBASE64(String str){
    	if(str == null)
    		return null;
    	return Base64.encodeBase64String(str.getBytes());
    }
}

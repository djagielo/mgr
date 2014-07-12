package pl.polsl.utils.hashes;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtil implements Serializable{
	private MessageDigest md;
	public HashUtil(){
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public String getHash(String msg){
		if(md != null){
			md.update(msg.getBytes());
			byte[] bytes = md.digest();
			 StringBuilder sb = new StringBuilder();
	         for(int i=0; i< bytes.length ;i++)
	         {
	             sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	         }
	         return sb.toString();
		}
		else
			return null;

	}
}

package pl.polsl.utils.hashes;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultipleHashUtil {
	List<MessageDigest> mds = new ArrayList<>();
	
	public MultipleHashUtil(AvailableHashes ... hashes){
		for(AvailableHashes hash: hashes){
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance(hash.toString());
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			if(md != null)
				mds.add(md);
		}
	}
	
	public Map<String, byte[]> getHashes(byte[] word){
		Map<String, byte[]> out = new HashMap<String, byte[]>();
		for(MessageDigest md: mds){
			out.put(md.getAlgorithm(), getHashForMd(md, word));
		}
		
		return out;
	}
	
	public Map<String, byte[]> getHashes(String word){
		return getHashes(word.getBytes());
	}
	
	private byte[] getHashForMd(MessageDigest md, byte[] word){
		if(md != null){
			md.update(word);
			return md.digest();
			/*byte[] bytes = md.digest();
			 StringBuilder sb = new StringBuilder();
	         for(int i=0; i< bytes.length ;i++)
	         {
	             sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	         }
	         return sb.toString();*/
		}
		else
			return null;
	}
	
	public static void main(String[] args) {
		/*long start = System.nanoTime();
		MultipleHashUtil util = new MultipleHashUtil(AvailableHashes.SHA256);
		System.out.println(util.getHashes("test"));
		System.out.println(String.format("Sha-256 time: %s[ns]", (System.nanoTime() - start)));
		*/
		long start = System.nanoTime();
		MultipleHashUtil util = new MultipleHashUtil(AvailableHashes.SHA256, AvailableHashes.SHA512, AvailableHashes.MD5, AvailableHashes.MD2, AvailableHashes.SHA384);
		System.out.println(util.getHashes("afdasa".getBytes()));
		System.out.println(String.format("All time: %s[ns]", (System.nanoTime() - start)));
	}
}

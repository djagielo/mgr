package pl.polsl.utils.hashes;

public enum AvailableHashes {
	SHA256("SHA-256"),
	SHA384("SHA-384"),
	SHA512("SHA-512"),
	MD5("MD5"),
	MD2("MD2");
	private final String hashName;
	
	private AvailableHashes(String hashName){
		this.hashName = hashName;
	}
	
	public String toString(){
		return hashName;
	}
}

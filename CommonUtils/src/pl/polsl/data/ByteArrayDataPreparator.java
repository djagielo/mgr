package pl.polsl.data;

public class ByteArrayDataPreparator extends AbstractDataPreparator<byte[]> {

	public ByteArrayDataPreparator(String file) {
		super(file);
	}

	@Override
	public byte[] createObjectFromString(String str) {
		return str.getBytes();
	}

}

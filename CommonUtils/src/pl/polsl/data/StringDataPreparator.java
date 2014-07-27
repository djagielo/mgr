package pl.polsl.data;


public class StringDataPreparator extends AbstractDataPreparator<String> {

	public StringDataPreparator(String file) {
		super(file);
	}

	@Override
	public String createObjectFromString(String str) {
		return str;
	}

}

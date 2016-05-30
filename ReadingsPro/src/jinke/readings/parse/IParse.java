package jinke.readings.parse;

import java.io.InputStream;

public interface IParse {
	public boolean doParse(InputStream oIS) throws Exception;
	public Object output();
	public int totalPage();
}

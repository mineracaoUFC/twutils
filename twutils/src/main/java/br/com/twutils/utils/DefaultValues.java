package br.com.twutils.utils;

/**
 * 
 * @author Adail Carvalho
 * @since 1.0.1
 *
 */
public class DefaultValues {
	
	public static final int DEFAULT_COUNT = 100;
	
	public static final String DEFAULT_CSV_SEPARATOR = ";";

	public static final int DEFAULT_THREAD_WAIT_TIME = 15;
	
	public int getDefaultCount() {
		return DEFAULT_COUNT;
	}
	
	public String getDefaultCsvSeparator() {
		return DEFAULT_CSV_SEPARATOR;
	}
	
	public int getDefaultThreadWaitTime() {
		return DEFAULT_THREAD_WAIT_TIME;
	}
}

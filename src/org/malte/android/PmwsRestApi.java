package org.malte.android;

public interface PmwsRestApi {
	
	//public static final String REST_CONTEXT = "http://10.253.44.235:8087/pmws/rest/";
	public static final String REST_CONTEXT = "http://10.253.42.111:8080/pmws/rest/";
	public static final String URL_SUFFIX_LOGIN = "login";
	
	public static final String URL_BLOCK_QUERY = "query/blocks/";
	
	public static final String URL_CLIENT_FEE = "query/custFees/";

	public static final String LOGIN = "LOGIN";

	public static final String GOLD_BLOCK_QUERY = "GOLD_BLOCK_QUERY";
	
	public static final String CLIENT_FEE_QUERY = "CLIENT_FEE_QUERY";

}

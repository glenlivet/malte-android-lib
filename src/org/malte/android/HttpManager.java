package org.malte.android;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 提供HttpClient及其属性配置等
 * TODO 属性配置和开关
 * 
 * @author shulai.zhang
 *
 */
public class HttpManager {
	
	private static HttpClient client= new DefaultHttpClient();
	
	public static HttpClient getClientInstance(){
		return client;
	}

}

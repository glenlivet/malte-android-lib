package org.malte.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.kingstar.ngbf.s.ntp.SimpleMessage;

public class HttpAsyncTask extends AsyncTask<Void, Integer, HttpResponse> {

	public static final int TASK_TYPE_GET = 0;
	public static final int TASK_TYPE_POST = 1;
	
	public static final int TASK_PROGRESS_START = 600;
	public static final int TASK_PROGRESS_DONE = 800;
	

	public static final int MSG_HTTP_FAILURE = 500;
	public static final int MSG_HTTP_SUCCESS = 200;
	
	public static final int MSG_HTTP_STATE_CHANGE = 700;

	private String url;
	
	private int requestType;
	
	private SimpleMessage message;
	
	private Messenger messenger;
	
	private boolean publishState = false;

	/**
	 * 构建一个HttpAsyncTask
	 * 
	 * @param url	请求URL
	 * @param requestType	HTTP请求类型
	 * @param message	POST请求附带的MESSAGE
	 */
	public HttpAsyncTask(String url, int requestType, SimpleMessage message) {
		this.url = url;
		this.requestType = requestType;
		this.message = message;
	}
	
	public HttpAsyncTask(String url, Messenger messenger) {
		this.url = url;
		this.requestType = TASK_TYPE_GET;
		this.messenger = messenger;
	}



	public HttpAsyncTask(String url, int requestType, SimpleMessage message,
			Messenger messenger) {
		this.url = url;
		this.requestType = requestType;
		this.message = message;
		this.messenger = messenger;
	}
	
	public HttpAsyncTask(String url, int requestType, SimpleMessage message,
			Messenger messenger, boolean publishState) {
		this.url = url;
		this.requestType = requestType;
		this.message = message;
		this.messenger = messenger;
		this.publishState = publishState;
	}

	@Override
	protected HttpResponse doInBackground(Void... params) {
		
		if(publishState){
			publishProgress(TASK_PROGRESS_START);
		}
		// 返回值
		HttpResponse result = null;
		// 请求
		HttpUriRequest request;

		try {
			switch (requestType) {
			case TASK_TYPE_POST:
				request = new HttpPost(url);
				((HttpPost) request).setEntity(new UrlEncodedFormEntity(
						buildHttpParam(message)));
				break;
			case TASK_TYPE_GET:
			default:
				request = new HttpGet(url);
				break;
			}
			result = HttpManager.getClientInstance().execute(request);
//			result = ConvertRespToSm(resp);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(publishState){
				publishProgress(TASK_PROGRESS_DONE);
			}
		}
		return result;
	}
	
	@Override
	protected void onProgressUpdate(Integer... values) {
		try {
			messenger.send(Message.obtain(null, MSG_HTTP_STATE_CHANGE,values[0], 0));
		} catch (RemoteException e) {
			//client dead
			// do nothing
		}
	}

	private String ConvertRespToSm(HttpResponse resp)
			throws IOException {
		InputStream is = resp.getEntity().getContent();
		String sm = inputStreamToString(is);
		return sm;
	}

	private String inputStreamToString(InputStream is) throws IOException {

		String line = "";
		StringBuilder total = new StringBuilder();

		// Wrap a BufferedReader around the InputStream
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		// Read response until the end
		while ((line = rd.readLine()) != null) {
			total.append(line);
		}

		// Return full string
		return total.toString();
	}

	private List<NameValuePair> buildHttpParam(SimpleMessage smParam) {
		List<NameValuePair> result = new ArrayList<NameValuePair>();

		String str = smParam.toJson();
		NameValuePair nvp = new BasicNameValuePair("data", str);
		result.add(nvp);

		return result;
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		try {
			if(messenger != null){
				if(result == null){
					messenger.send(Message.obtain(null, MSG_HTTP_FAILURE, 0, 0));
				}else {
					if(result.getStatusLine().getStatusCode() != 200){
						messenger.send(Message.obtain(null, MSG_HTTP_FAILURE, 1, result.getStatusLine().getStatusCode()));
					} else {
						messenger.send(Message.obtain(null, MSG_HTTP_SUCCESS, ConvertRespToSm(result)));
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

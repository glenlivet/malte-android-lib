package org.malte.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;

import android.os.AsyncTask;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;


/**
 * Http Asynchronized Task
 * 
 * @author shulai.zhang
 *
 */
public class HttpAsyncTask extends AsyncTask<Void, Integer, HttpResponse> {

	public static final int TASK_PROGRESS_START = 600;
	public static final int TASK_PROGRESS_DONE = 800;
	

	public static final int MSG_HTTP_FAILURE = 500;
	public static final int MSG_HTTP_SUCCESS = 200;
	
	public static final int MSG_HTTP_STATE_CHANGE = 700;

	private HttpUriRequest request;
	
	private Messenger messenger;
	
	private boolean publishState = false;

	/**
	 * 构建一个HttpAsyncTask
	 * 
	 * @param url	请求URL
	 * @param requestType	HTTP请求类型
	 * @param message	POST请求附带的MESSAGE
	 */
	public HttpAsyncTask(HttpUriRequest request) {
		this.request = request;
	}
	
	public HttpAsyncTask(HttpUriRequest request, Messenger messenger) {
		this.request = request;
		this.messenger = messenger;
	}

	public HttpAsyncTask(HttpUriRequest request,
			Messenger messenger, boolean publishState) {
		this.request = request;
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

		try {
			
			result = HttpManager.getClientInstance().execute(request);
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
						messenger.send(Message.obtain(null, MSG_HTTP_SUCCESS, result));
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

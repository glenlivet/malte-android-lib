package org.malte.android;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

/**
 * 页面绑定Service 为页面提供数据服务
 * 作用类同Spring的Service层
 * 
 * @since 0.1.0.0
 * @author shulai.zhang
 *
 */
public abstract class EliteService extends Service {
	
	public static final String MESSENGER_KEY_IN_BUNDLE = "ELITE ACTIVITY MESSENGER";

	public static final int MSG_NEED_UPDATE_VIEW = 600;
	public static final int MSG_NEED_UPDATE_TITLE = 900;
	
	
	/**
	 * 绑定返回binder
	 */
	protected final IBinder mBinder = new LocalBinder();
	
	protected Messenger uiMessenger;
	
	protected boolean showHttpStateInActionBar = false;
	
	/**
	 * 页面数据模型
	 */
	protected ViewContent viewContent;
	
	final Messenger mMessenger = new Messenger(new EliteServiceIncomingHandler(this));
	
	/**
	 * 获取该Service相连的数据模型
	 * 
	 * @return
	 */
	protected abstract ViewContent getViewContent();

	/**
	 * 页面Activity主动要求数据更新
	 */
	public abstract void updateViewContent();
	
	public abstract void onHttpFailure(Integer httpCode);
	
	public abstract void onHttpSuccess(String sm);
	
	/**
	 * http 开始和结束的回调
	 * @param state
	 */
	protected void onHttpStateChange(int state) {
		
	}
	
	public void httpStateChange(int state) {
		
		if(showHttpStateInActionBar){
			switch(state){
			case HttpAsyncTask.TASK_PROGRESS_START:
				getViewContent().loading();
				break;
			case HttpAsyncTask.TASK_PROGRESS_DONE:
				getViewContent().loadingDone();
				break;
			}
		}
		onHttpStateChange(state);
		if(showHttpStateInActionBar){
			notifyHttpSateUpdated();
		}
	}
	
	/**
	 * 
	 * 
	 * @param what	发生了什么事
	 * @param arg1	参数1
	 * @param arg2	参数2
	 * @param obj	参数对象
	 */
	protected void sendUiMessage(int what, int arg1, int arg2, Object obj){
		try {
			uiMessenger.send(Message.obtain(null, what, arg1, arg2, obj));
		} catch (RemoteException e) {
			
		}
	}
	
	private void notifyHttpSateUpdated() {
		// TODO Auto-generated method stub
		try {
			uiMessenger.send(Message.obtain(null, MSG_NEED_UPDATE_TITLE));
		} catch (RemoteException e) {
			// The client is dead. 
			// do nothing 
			// can NULL the uiMessenger, but may cause NullPointerException
		}
	}

	public Object getViewValue(int viewId){
		return viewContent.getViewValue(viewId);
	}
	
	public String getTitle(){
		return viewContent.getTitle();
	}
	
	public int getDrawableId(){
		return viewContent.getDrawableId();
	}
	
	public Messenger getMessenger() {
		return mMessenger;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//绑定数据模型
		this.viewContent = getViewContent();
	}

	@Override
	public IBinder onBind(Intent intent) {
		//获取和Activity通讯的Messenger
		uiMessenger = intent.getParcelableExtra(MESSENGER_KEY_IN_BUNDLE);
		updateViewContent();
		return mBinder;
	}

	/**
	 * 通知前台更新页面
	 */
	protected void notifyViewContentUpdated(){
		try {
			uiMessenger.send(Message.obtain(null, MSG_NEED_UPDATE_VIEW));
		} catch (RemoteException e) {
			// The client is dead. 
			// do nothing 
			// can NULL the uiMessenger, but may cause NullPointerException
		}
	}

	/**
	 * 执行HTTP GET
	 * @param url
	 */
	public void executeHttpGet(String url){
		HttpUriRequest request = new HttpGet(url);
		HttpAsyncTask t = new HttpAsyncTask(request, getMessenger());
		t.execute();
	}
	
	/**
	 * 执行HTTP 请求
	 * @param url
	 * @param reqType
	 * @param sm
	 */
	public void executeHttpRequest(HttpUriRequest request){
		HttpAsyncTask t = new HttpAsyncTask(request, getMessenger());
		t.execute();
	}
	
	public void executeHttpRequest(HttpUriRequest request, boolean publishSate){
		HttpAsyncTask t = new HttpAsyncTask(request, getMessenger(), publishSate);
		t.execute();
	}
	
	/**
	 * 执行HTTP 请求 不关心执行结果
	 * @param url
	 * @param reqType
	 * @param sm
	 */
	public void executeHttpRequestWithoutResponse(HttpUriRequest request){
		HttpAsyncTask t = new HttpAsyncTask(request);
		t.execute();
	}
	
	/**
	 * 绑定对象
	 * 
	 * @author shulai.zhang
	 *
	 */
	public class LocalBinder extends Binder {
		
		EliteService getService(){
			return EliteService.this;
		}
	}
	
	static class EliteServiceIncomingHandler extends Handler {
		
		private EliteService ls;
		
		EliteServiceIncomingHandler(EliteService ls) {
			this.ls = ls;
		}
		
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case HttpAsyncTask.MSG_HTTP_FAILURE:
				Integer code = (msg.arg1 == 0)?null:msg.arg2;
				ls.onHttpFailure(code);
				break;
			case HttpAsyncTask.MSG_HTTP_SUCCESS:
				ls.onHttpSuccess((String)msg.obj);
				break;
			case HttpAsyncTask.MSG_HTTP_STATE_CHANGE:
				ls.httpStateChange(msg.arg1);
			default:
				super.handleMessage(msg);
				break;
			}
			
		}
	}

	public boolean isShowHttpStateInActionBar() {
		return showHttpStateInActionBar;
	}

	public void setShowHttpStateInActionBar(boolean showHttpStateInActionBar) {
		this.showHttpStateInActionBar = showHttpStateInActionBar;
	}

}

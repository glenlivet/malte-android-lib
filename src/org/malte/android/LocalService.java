package org.malte.android;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.kingstar.ngbf.s.ntp.SimpleMessage;

/**
 * 页面绑定Service 为页面提供数据服务
 * 作用类同Spring的Service层
 * 
 * @since 0.1.0.0
 * @author shulai.zhang
 *
 */
public abstract class LocalService extends Service {
	
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
	
	final Messenger mMessenger = new Messenger(new LocalServiceIncomingHandler(this));
	
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
		HttpAsyncTask t = new HttpAsyncTask(url, getMessenger());
		t.execute();
	}
	
	/**
	 * 执行HTTP POST
	 * @param url
	 * @param sm
	 */
	public void executeHttpPost(String url, SimpleMessage sm){
		HttpAsyncTask t = new HttpAsyncTask(url, HttpAsyncTask.TASK_TYPE_POST, sm, getMessenger());
		t.execute();
	}
	
	/**
	 * 执行HTTP 请求
	 * @param url
	 * @param reqType
	 * @param sm
	 */
	public void executeHttpRequest(String url, int reqType, SimpleMessage sm){
		HttpAsyncTask t = new HttpAsyncTask(url, reqType, sm, getMessenger());
		t.execute();
	}
	
	public void executeHttpRequest(String url, int reqType, SimpleMessage sm, boolean publishSate){
		HttpAsyncTask t = new HttpAsyncTask(url, reqType, sm, getMessenger(), publishSate);
		t.execute();
	}
	
	/**
	 * 执行HTTP 请求 不关心执行结果
	 * @param url
	 * @param reqType
	 * @param sm
	 */
	public void executeHttpRequestWithoutResponse(String url, int reqType, SimpleMessage sm){
		HttpAsyncTask t = new HttpAsyncTask(url, reqType, sm);
		t.execute();
	}
	
	/**
	 * 绑定对象
	 * 
	 * @author shulai.zhang
	 *
	 */
	public class LocalBinder extends Binder {
		
		LocalService getService(){
			return LocalService.this;
		}
	}
	
	static class LocalServiceIncomingHandler extends Handler {
		
		private LocalService ls;
		
		LocalServiceIncomingHandler(LocalService ls) {
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

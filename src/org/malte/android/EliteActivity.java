package org.malte.android;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

public abstract class EliteActivity extends Activity {

	final Messenger uiMessenger = new Messenger(
			new EliteActivityIncomingHandler(this));

	private EliteService localService;

	public abstract void drawView();

	protected abstract Class<? extends EliteService> getLocalServiceClass();
	
	protected abstract int getLayoutId();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		drawView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent = new Intent(this, getLocalServiceClass());
		intent.putExtra(EliteService.MESSENGER_KEY_IN_BUNDLE, uiMessenger);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unbindService(mConnection);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			EliteService.LocalBinder b = (EliteService.LocalBinder) binder;
			localService = b.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			localService = null;
		}
	};

	static class EliteActivityIncomingHandler extends Handler {

		EliteActivity ea;

		public EliteActivityIncomingHandler(EliteActivity ea) {
			this.ea = ea;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EliteService.MSG_NEED_UPDATE_VIEW:
				ea.drawView();
				break;
			case EliteService.MSG_NEED_UPDATE_TITLE:
				ea.updateTitleInActionBar();
				break;
			default:
				ea.handleServiceMessage(msg);
				break;
			}
		}
		
	}

	public void updateTitleInActionBar() {
		getActionBar().setTitle(getLocalService().getTitle());
	}
	
	public EliteService getLocalService() {
		return localService;
	}
	
	/**
	 * 处理Service发送过来的信息
	 * @param msg
	 */
	public void handleServiceMessage(Message msg){
		
	};
}

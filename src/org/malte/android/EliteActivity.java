package org.malte.android;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;

/**
 * 
 * 
 * @since 0.1.0.0
 * @author shulai.zhang
 * 
 */
public abstract class EliteActivity extends ListActivity {

	final Messenger uiMessenger = new Messenger(
			new EliteActivityIncomingHandler(this));

	private LocalService localService;

	public abstract void drawView();

	protected abstract Class<? extends LocalService> getLocalServiceClass();
	
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
		intent.putExtra(LocalService.MESSENGER_KEY_IN_BUNDLE, uiMessenger);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unbindService(mConnection);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			LocalService.LocalBinder b = (LocalService.LocalBinder) binder;
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
			case LocalService.MSG_NEED_UPDATE_VIEW:
				ea.drawView();
				break;
			case LocalService.MSG_NEED_UPDATE_TITLE:
				ea.updateTitleInActionBar();
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	public void updateTitleInActionBar() {
		getActionBar().setTitle(getLocalService().getTitle());
	}
	
	public LocalService getLocalService() {
		return localService;
	}

}

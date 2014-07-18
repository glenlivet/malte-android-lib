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

/**
 * The normal activity built with an inside messenger and automatically binding
 * a local service when resumed.
 * 
 * @author shulai.zhang
 * 
 */
public abstract class EliteActivity extends Activity {

	/**
	 * The messenger that will be passed to other components and is used to
	 * communicate with the UI thread.
	 */
	private final Messenger uiMessenger = new Messenger(
			new EliteActivityIncomingHandler(this));

	/**
	 * The bound local service, with which to handle background work, such as
	 * execute HTTP request, do Database operations, etc.
	 */
	private EliteService localService;

	/**
	 * draw the whole activity. Populate views in this activity with data stored
	 * in the corresponded {@link ViewContent}.
	 */
	protected abstract void drawView();

	/**
	 * The bound local service's calss.
	 * 
	 * @return
	 */
	protected abstract Class<? extends EliteService> getLocalServiceClass();

	/**
	 * The layout ID of the activity.
	 * 
	 * @return
	 */
	protected abstract int getLayoutId();

	/**
	 * handle the custom message from background service. should be overridden,
	 * if you have custom message. Just be aware, two number
	 * {@link EliteService#MSG_NEED_UPDATE_TITLE} and
	 * {@link EliteService#MSG_NEED_UPDATE_VIEW} has been occupied.
	 * 
	 * @param msg
	 *            the custom message from bound service.
	 */
	protected void handleServiceMessage(Message msg) {

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set content view
		setContentView(getLayoutId());
		// draw view on activity's creation.
		drawView();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// bind the local service, passing the ui messenger.
		Intent intent = new Intent(this, getLocalServiceClass());
		intent.putExtra(EliteService.MESSENGER_KEY_IN_BUNDLE, uiMessenger);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unbind the local service
		unbindService(mConnection);
	}

	/**
	 * the service connection obj.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			EliteService.LocalBinder b = (EliteService.LocalBinder) binder;
			// get the service object.
			localService = b.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			// remove the service objecct.
			localService = null;
		}
	};

	/**
	 * The incoming message handler.
	 * 
	 * @author shulai.zhang
	 * 
	 */
	private static class EliteActivityIncomingHandler extends Handler {

		// the corresponded activity
		EliteActivity ea;

		public EliteActivityIncomingHandler(EliteActivity ea) {
			this.ea = ea;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// when view needs to be updated, usually because the view content
			// has changed in background.
			case EliteService.MSG_NEED_UPDATE_VIEW:
				// redraw the view
				ea.drawView();
				break;
			// when view title needs to be updated, usually the state of
			// activity has changed.
			case EliteService.MSG_NEED_UPDATE_TITLE:
				// update title display in ActionBar
				ea.updateTitleInActionBar();
				break;
			default:
				// call {@link EliteActivity#handleServiceMessage} to handle
				// other custom message.
				ea.handleServiceMessage(msg);
				break;
			}
		}

	}

	/**
	 * update the title in action bar
	 */
	protected void updateTitleInActionBar() {
		getActionBar().setTitle(getLocalService().getTitle());
	}

	/**
	 * Get the local service.
	 * 
	 * @return
	 */
	protected EliteService getLocalService() {
		return localService;
	}

}

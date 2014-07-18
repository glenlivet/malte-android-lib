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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * A list activity with built-in an inside messenger and automatically binding a
 * local service when resumed.
 * 
 * @author shulai.zhang
 * 
 * @param <T>
 */
public abstract class EliteListActivity<T> extends ListActivity {

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
	 * The layout ID of the activity.
	 * 
	 * @return
	 */
	protected abstract int getLayoutId();

	/**
	 * the list adapter.
	 */
	protected EliteArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set content view
		setContentView(getLayoutId());
		// draw the view
		drawView();
		// set the adapter
		adapter = new EliteArrayAdapter();
		setListAdapter(adapter);
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

	protected void drawView() {
		drawExtraView();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * the service connection obj.
	 */
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			EliteService.LocalBinder b = (EliteService.LocalBinder) binder;
			localService = b.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			localService = null;
		}
	};

	/**
	 * draw the other area except the list view.
	 */
	protected void drawExtraView() {

	}

	/**
	 * Row Layout ID
	 * 
	 * @return
	 */
	protected abstract int getRowLayoutId();

	/**
	 * Get the view content.
	 * 
	 * @return
	 */
	protected abstract ListViewContent<T> getViewContent();

	/**
	 * Get the local service class.
	 * 
	 * @return
	 */
	protected abstract Class<? extends EliteListService<T>> getLocalServiceClass();

	/**
	 * draw a row in listview
	 * 
	 * @param position
	 *            The position of the item within the adapter's data set of the
	 *            item whose view we want.
	 * @param item
	 *            current row's data.
	 * @param convertView
	 *            The old view to reuse, if possible. Note: You should check
	 *            that this view is non-null and of an appropriate type before
	 *            using. If it is not possible to convert this view to display
	 *            the correct data, this method can create a new view.
	 *            Heterogeneous lists can specify their number of view types, so
	 *            that this View is always of the right type (see
	 *            getViewTypeCount() and getItemViewType(int)).
	 * @param rowView
	 *            a new inflated row view.
	 * @param parent
	 *            The parent that this view will eventually be attached to
	 * @return the row view.
	 */
	protected View drawRow(int position, T item, View convertView,
			View rowView, ViewGroup parent) {
		return adapter.getDefaultRow(position, convertView, parent);
	}

	/**
	 * the list view adapter.
	 * 
	 * @author shulai.zhang
	 * 
	 */
	class EliteArrayAdapter extends ArrayAdapter<T> {

		EliteArrayAdapter() {
			super(EliteListActivity.this, getRowLayoutId(), getViewContent()
					.getList());
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// get current row object.
			T item = getViewContent().getList().get(position);

			// get the inflater
			LayoutInflater inflater = (LayoutInflater) EliteListActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			// do the inflation
			View rowView = inflater.inflate(getRowLayoutId(), parent, false);

			View row = drawRow(position, item, convertView, rowView, parent);

			return row;
		}

		public View getDefaultRow(int position, View convertView,
				ViewGroup parent) {
			return super.getView(position, convertView, parent);
		}
	}

	/**
	 * the built-in incoming message handler.
	 * 
	 * @author shulai.zhang
	 * 
	 */
	private static class EliteActivityIncomingHandler extends Handler {

		// the corresponded activity
		EliteListActivity<?> ea;

		public EliteActivityIncomingHandler(EliteListActivity<?> ea) {
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
				// call {@link EliteListActivity#handleServiceMessage} to handle
				// other custom message.
				ea.handleServiceMessage(msg);
				break;
			}
		}
	}

	/**
	 * update the title in action bar
	 */
	public void updateTitleInActionBar() {
		getActionBar().setTitle(getLocalService().getTitle());
	}

	/**
	 * Get the local service.
	 * 
	 * @return
	 */
	public EliteService getLocalService() {
		return localService;
	}

	/**
	 * 处理Service发送过来的信息
	 * 
	 * @param msg
	 */
	public void handleServiceMessage(Message msg) {

	};
}

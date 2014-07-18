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

public abstract class EliteListActivity<T> extends ListActivity {
	
	final Messenger uiMessenger = new Messenger(
			new EliteActivityIncomingHandler(this));

	private EliteService localService;
	
	protected abstract int getLayoutId();
	
	protected EliteArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayoutId());
		drawView();
		adapter = new EliteArrayAdapter();
		setListAdapter(adapter);
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
	
	public void drawView() {
		drawExtraView();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
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
	
	/**
	 * 画除ListView以外的部分
	 */
	protected void drawExtraView(){
		
	}

	/**
	 * Row Layout ID
	 * @return
	 */
	protected abstract int getRowLayoutId();
	
	/**
	 * 获取页面数据对象
	 * 
	 * @return
	 */
	protected abstract ListViewContent<T> getViewContent();
	
	/**
	 * 获取于该Activity绑定的后台Service
	 * 
	 * @return
	 */
	protected abstract Class<? extends EliteListService<T>> getLocalServiceClass();
	
	/**
	 * 画某一行，大部分情况需要在子类中Override
	 * 
	 * @param position	The position of the item within the adapter's data set of the item whose view we want.
	 * @param item	当前行的Domain对象
	 * @param convertView	The old view to reuse, if possible. Note: You should check that this view is non-null and of an appropriate type before using. If it is not possible to convert this view to display the correct data, this method can create a new view. Heterogeneous lists can specify their number of view types, so that this View is always of the right type (see getViewTypeCount() and getItemViewType(int)).
	 * @param rowView	新的空的行控件对象
	 * @param parent	The parent that this view will eventually be attached to
	 * @return
	 */
	public View drawRow(int position, T item, View convertView, View rowView,
			ViewGroup parent) {
		return adapter.getDefaultRow(position, convertView, parent);
	}
	
	class EliteArrayAdapter extends ArrayAdapter<T> {
		
		EliteArrayAdapter(){
			super(EliteListActivity.this, getRowLayoutId(), getViewContent().getList());
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//行数据对象
			T item = getViewContent().getList().get(position);
			
			// 获取填充服务
			LayoutInflater inflater = (LayoutInflater) EliteListActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			// 新填充一行
			View rowView = inflater.inflate(getRowLayoutId(), parent, false);
			
			View row = drawRow(position, item, convertView, rowView, parent);
			
			return row;
		}
		
		public View getDefaultRow(int position, View convertView, ViewGroup parent) {
			return super.getView(position, convertView, parent);
		}
	}
	
	static class EliteActivityIncomingHandler extends Handler {

		EliteListActivity<?> ea;

		public EliteActivityIncomingHandler(EliteListActivity<?> ea) {
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

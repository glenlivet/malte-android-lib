package org.malte.android;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public abstract class EliteListActivity<T> extends EliteActivity {
	
	protected EliteArrayAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new EliteArrayAdapter();
		setListAdapter(adapter);
	}
	
	@Override
	public void drawView() {
		drawExtraView();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 画除ListView以外的部分
	 */
	protected abstract void drawExtraView();

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
	
	protected abstract Class<? extends LocalListService<T>> getLocalServiceClass();
	
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
	
	

}

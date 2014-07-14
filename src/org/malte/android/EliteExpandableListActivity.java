package org.malte.android;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

public abstract class EliteExpandableListActivity<T extends Expandable<?>>
		extends EliteBasicActivity {
	
	protected EliteExpandableListAdapater adapter;
	
	protected ExpandableListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		adapter = new EliteExpandableListAdapater();
		listView = (ExpandableListView) findViewById(getExpandableListViewId());
		listView.setAdapter(adapter);
	}

	/**
	 * 获取ExpandableListView的ID
	 * @return
	 */
	protected abstract int getExpandableListViewId();
	
	/**
	 * child layout id
	 * @return
	 */
	public abstract int getChildLayoutId();
	
	/**
	 * group layout id
	 * @return
	 */
	public abstract int getGroupLayoutId();
	
	/**
	 * see {@link BaseExpandableListAdapter#getChildView(int, int, boolean, View, ViewGroup)}
	 * 
	 * @see BaseExpandableListAdapter
	 * @param child 当前画的child对象
	 * @param groupPosition
	 * @param childPosition
	 * @param isLastChild
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public abstract View drawChildView(Object child, int groupPosition,
			int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent);

	/**
	 * see {@link BaseExpandableListAdapter#getGroupView(int, boolean, View, ViewGroup)}
	 * 
	 * @see BaseExpandableListAdapter
	 * @param group	当前绘制的Expandable对象
	 * @param groupPosition
	 * @param isExpanded
	 * @param convertView
	 * @param parent
	 * @return
	 */
	public abstract View drawGroupView(T group, int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent);

	
	
	@Override
	public void drawView() {
		drawExtraView();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * 画出了ListView以外的部分
	 */
	private void drawExtraView() {
		
	}

	@Override
	protected abstract Class<? extends LocalExpandableListService<T>> getLocalServiceClass();

	public abstract ExpandableListViewContent<T> getViewContent();

	class EliteExpandableListAdapater extends BaseExpandableListAdapter {

		LayoutInflater inflater;

		public EliteExpandableListAdapater() {
			inflater = (LayoutInflater) EliteExpandableListActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getGroupCount() {
			return getViewContent().getGroups().size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return getViewContent().getGroups().get(groupPosition)
					.getChildren().size();
		}

		@Override
		public T getGroup(int groupPosition) {
			return getViewContent().getGroups().get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return getGroup(groupPosition).getChildren().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return EliteExpandableListActivity.this.getGroupId(groupPosition);
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return EliteExpandableListActivity.this.getChildId(groupPosition,
					childPosition);
		}

		@Override
		public boolean hasStableIds() {
			return EliteExpandableListActivity.this.hasStableIds();
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = inflater.inflate(
						EliteExpandableListActivity.this.getGroupLayoutId(),
						null);
			}
			T group = getGroup(groupPosition);

			return EliteExpandableListActivity.this.drawGroupView(group,
					groupPosition, isExpanded, convertView, parent);
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			Object child = getChild(groupPosition, childPosition);
			if (convertView == null) {
				convertView = inflater.inflate(
						EliteExpandableListActivity.this.getChildLayoutId(),
						null);
			}
			return EliteExpandableListActivity.this.drawChildView(child,
					groupPosition, childPosition, isLastChild, convertView,
					parent);
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return EliteExpandableListActivity.this.isChildSelectable(
					groupPosition, childPosition);
		}

	}

	/**
	 * See {@link BaseExpandableListAdapter#getGroupId(int)}
	 * 
	 * @see BaseExpandableListAdapter
	 * @param groupPosition
	 * @return
	 */
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * See {@link BaseExpandableListAdapter#isChildSelectable(int, int)}
	 * 
	 * @see BaseExpandableListAdapter
	 * @param groupPosition
	 * @param childPosition
	 * @return
	 */
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	

	



	/**
	 * See {@link BaseExpandableListAdapter#hasStableIds()}
	 * 
	 * @see BaseExpandableListAdapter
	 * @return
	 */
	public boolean hasStableIds() {
		return false;
	}

	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}
}

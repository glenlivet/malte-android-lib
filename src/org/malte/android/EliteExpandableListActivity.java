package org.malte.android;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

/**
 * A activity with built-in ExpandableListView and the corresponded adapter.
 * 
 * @author shulai.zhang
 *
 * @param <T> a class implements Expandable. The data structure of the expandable list.
 */
public abstract class EliteExpandableListActivity<T extends Expandable<?>>
		extends EliteActivity {
	
	/**
	 * the expandable list adapter.
	 */
	protected EliteExpandableListAdapater adapter;
	
	/**
	 * the expandable list view
	 */
	protected ExpandableListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set and config the list view.
		adapter = new EliteExpandableListAdapater();
		listView = (ExpandableListView) findViewById(getExpandableListViewId());
		listView.setAdapter(adapter);
	}

	/**
	 * Get the expandable list view ID.
	 * @return
	 */
	protected abstract int getExpandableListViewId();
	
	/**
	 * child layout id
	 * @return
	 */
	protected abstract int getChildLayoutId();
	
	/**
	 * group layout id
	 * @return
	 */
	protected abstract int getGroupLayoutId();
	
	/**
	 * see {@link BaseExpandableListAdapter#getChildView(int, int, boolean, View, ViewGroup)}
	 * 
	 * @see {@link BaseExpandableListAdapter}
	 * @param child the displaying data in this child view.
	 * @param groupPosition
	 * @param childPosition
	 * @param isLastChild
	 * @param convertView
	 * @param parent
	 * @return
	 */
	protected abstract View drawChildView(Object child, int groupPosition,
			int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent);

	/**
	 * see {@link BaseExpandableListAdapter#getGroupView(int, boolean, View, ViewGroup)}
	 * 
	 * @see BaseExpandableListAdapter
	 * @param group	the displaying data in this group
	 * @param groupPosition
	 * @param isExpanded
	 * @param convertView
	 * @param parent
	 * @return
	 */
	protected abstract View drawGroupView(T group, int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent);

	
	
	@Override
	protected void drawView() {
		drawExtraView();
		if(adapter != null){
			adapter.notifyDataSetChanged();
		}
	}
	
	/**
	 * draw the other area except the expandable view.
	 */
	private void drawExtraView() {
		
	}

	@Override
	protected abstract Class<? extends EliteExpandableListService<T>> getLocalServiceClass();

	protected abstract ExpandableListViewContent<T> getViewContent();

	/**
	 * the built-in adapter.
	 * 
	 * @author shulai.zhang
	 *
	 */
	private class EliteExpandableListAdapater extends BaseExpandableListAdapter {

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
	protected long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/**
	 * See {@link BaseExpandableListAdapter#getChildId(int, int)}
	 * 
	 * @see BaseExpandableListAdapter
	 * @param groupPosition
	 * @param childPosition
	 * @return
	 */
	protected long getChildId(int groupPosition, int childPosition) {
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
	protected boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * See {@link BaseExpandableListAdapter#hasStableIds()}
	 * 
	 * @see BaseExpandableListAdapter
	 * @return
	 */
	protected boolean hasStableIds() {
		return false;
	}

}

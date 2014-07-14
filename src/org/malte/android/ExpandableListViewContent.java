package org.malte.android;

import android.util.SparseArray;

public abstract class ExpandableListViewContent<T extends Expandable<?>> extends ViewContent {
	
	protected SparseArray<T> groups;

	protected ExpandableListViewContent(int layoutId) {
		super(layoutId);
		groups = new SparseArray<T>();
	}
	
	protected ExpandableListViewContent(int layoutId, SparseArray<T> groups){
		super(layoutId);
		this.groups = groups;
	}
	
	protected ExpandableListViewContent(int layoutId, String title){
		super(layoutId, title);
		groups = new SparseArray<T>();
	}
	
	protected ExpandableListViewContent(int layoutId, String title, SparseArray<T> groups){
		super(layoutId, title);
		this.groups = groups;
	}

	@Override
	public Object getViewValue(int viewId){
		return null;
	}

	public SparseArray<T> getGroups() {
		return groups;
	}
	
	

}

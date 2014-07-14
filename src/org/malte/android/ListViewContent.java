package org.malte.android;

import java.util.LinkedList;
import java.util.List;

public abstract class ListViewContent<T> extends ViewContent {
	
	protected List<T> list;

	protected ListViewContent(int layoutId) {
		super(layoutId);
		list = new LinkedList<T>();
	}
	
	protected ListViewContent(int layoutId, List<T> list) {
		super(layoutId);
		this.list = list;
	}
	
	protected ListViewContent(int layoutId, String title){
		super(layoutId, title);
		list = new LinkedList<T>();
	}
	
	protected ListViewContent(int layoutId, String title, List<T> list){
		super(layoutId, title);
		this.list = list;
	}
	
	protected ListViewContent(int layoutId, String title, int drawableId){
		super(layoutId, title, drawableId);
		list = new LinkedList<T>();
	}
	
	protected ListViewContent(int layoutId, String title, int drawableId, List<T> list){
		super(layoutId, title, drawableId);
		this.list = list;
	}

	public List<T> getList() {
		return list;
	}

}

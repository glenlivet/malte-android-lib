package org.malte.android;


public abstract class LocalListService<T> extends LocalService {

	@Override
	protected abstract ListViewContent<T> getViewContent();
	
}

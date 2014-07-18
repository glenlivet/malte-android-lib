package org.malte.android;


public abstract class EliteListService<T> extends EliteService {

	@Override
	protected abstract ListViewContent<T> getViewContent();

}

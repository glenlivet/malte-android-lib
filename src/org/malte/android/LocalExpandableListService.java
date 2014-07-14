package org.malte.android;

public abstract class LocalExpandableListService<T extends Expandable<?>> extends LocalService {

	@Override
	protected abstract ExpandableListViewContent<T> getViewContent();
	
}

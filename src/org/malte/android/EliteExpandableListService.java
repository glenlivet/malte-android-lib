package org.malte.android;

public abstract class EliteExpandableListService<T extends Expandable<?>> extends EliteService {

	@Override
	protected abstract ExpandableListViewContent<T> getViewContent();
	
}

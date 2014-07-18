package org.malte.android;

/**
 * bound with an {@link EliteExpandableListActivity}.
 * 
 * @author shulai.zhang
 *
 * @param <T>
 */
public abstract class EliteExpandableListService<T extends Expandable<?>> extends EliteService {

	@Override
	protected abstract ExpandableListViewContent<T> getViewContent();
	
}

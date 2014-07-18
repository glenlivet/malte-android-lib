package org.malte.android;


/**
 * bound with a EliteListActivity.
 * 
 * @author shulai.zhang
 *
 * @param <T>
 */
public abstract class EliteListService<T> extends EliteService {

	@Override
	protected abstract ListViewContent<T> getViewContent();

}

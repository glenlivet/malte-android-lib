package org.malte.android;


public abstract class LocalListService<T> extends LocalService {

	@Override
	protected abstract ListViewContent<T> getViewContent();

	@Override
	public abstract void updateViewContent();

	@Override
	public abstract void onHttpFailure(Integer httpCode);

	@Override
	public abstract void onHttpSuccess(String sm);
	
	

}

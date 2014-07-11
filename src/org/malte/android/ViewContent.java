package org.malte.android;

/**
 * 页面数据对象
 * 
 * @since 0.1.0.0
 * @author shulai.zhang
 *
 */
public abstract class ViewContent {
	
	public static final String LOADING = "Loading...";
	
	/**
	 * 页面ID layout ID
	 */
	protected int layoutId;
	
	/**
	 * 标题 用于ActionBar该页标题
	 */
	protected String title;
	
	/**
	 * 标题的备份 用于在loading和title之间切换用
	 */
	protected String titleHiden;
	
	/**
	 * 该页的图标
	 */
	protected int drawableId;
	
	
	protected ViewContent(int layoutId){
		this.layoutId = layoutId;
	}
	
	protected ViewContent(int layoutId, String title){
		this.layoutId = layoutId;
		this.title = title;
		this.titleHiden = title;
	}
	
	protected ViewContent(int layoutId, String title, int drawableId){
		this(layoutId, title);
		this.drawableId = drawableId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		this.titleHiden = title;
	}

	public void loading(){
		this.title = LOADING;
	}
	
	public void loadingDone(){
		this.title = this.titleHiden;
	}
	
	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}
	
	/**
	 * 获取某个页面控件的值
	 * @param viewId
	 * @return
	 */
	public abstract Object getViewValue(int viewId);
	

}

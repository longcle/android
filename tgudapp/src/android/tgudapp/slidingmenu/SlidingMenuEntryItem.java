package android.tgudapp.slidingmenu;

public class SlidingMenuEntryItem implements SlidingMenuItem{
	private int imageResourceId;
	private int titleEntryItem;
	public SlidingMenuEntryItem(int imageResourceId, int titleEntryItem) {
		super();
		this.imageResourceId = imageResourceId;
		this.titleEntryItem = titleEntryItem;
	}
	public int getImageResourceId() {
		return imageResourceId;
	}
	public void setImageResourceId(int imageResourceId) {
		this.imageResourceId = imageResourceId;
	}
	public int getTitleEntryItem() {
		return titleEntryItem;
	}
	public void setTitleEntryItem(int titleEntryItem) {
		this.titleEntryItem = titleEntryItem;
	}
	@Override
	public boolean isSection() {
		return false;
	}
	
}

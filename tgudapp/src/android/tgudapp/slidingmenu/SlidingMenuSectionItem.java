package android.tgudapp.slidingmenu;

public class SlidingMenuSectionItem implements SlidingMenuItem {

	private String titleSectionItem;
	
	public SlidingMenuSectionItem(String titleSectionItem) {
		super();
		this.titleSectionItem = titleSectionItem;
	}

	public String getTitleSectionItem() {
		return titleSectionItem;
	}

	public void setTitleSectionItem(String titleSectionItem) {
		this.titleSectionItem = titleSectionItem;
	}

	@Override
	public boolean isSection() {
		return true;
	}

}

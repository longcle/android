package android.tgudapp.model;

public class Banner {

	private String bannerId;
	private String bannerName;
	private String bannerImageUrl;
	private String bannerUrl;
	private String bannerOrder;
	
	public Banner(String bannerId, String bannerName, String bannerImageUrl,
			String bannerUrl, String bannerOrder) {
		super();
		this.bannerId = bannerId;
		this.bannerName = bannerName;
		this.bannerImageUrl = bannerImageUrl;
		this.bannerUrl = bannerUrl;
		this.bannerOrder = bannerOrder;
	}
	public String getBannerId() {
		return bannerId;
	}
	public void setBannerId(String bannerId) {
		this.bannerId = bannerId;
	}
	public String getBannerName() {
		return bannerName;
	}
	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}
	public String getBannerImageUrl() {
		return bannerImageUrl;
	}
	public void setBannerImageUrl(String bannerImageUrl) {
		this.bannerImageUrl = bannerImageUrl;
	}
	public String getBannerUrl() {
		return bannerUrl;
	}
	public void setBannerUrl(String bannerUrl) {
		this.bannerUrl = bannerUrl;
	}
	public String getBannerOrder() {
		return bannerOrder;
	}
	public void setBannerOrder(String bannerOrder) {
		this.bannerOrder = bannerOrder;
	}
	
}

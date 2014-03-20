package android.tgudapp.model;

import java.io.Serializable;

public class Place implements Serializable {
	private String id;
	private String namePlace;
	private String addressPlace;
	private String telPlace;
	private String proPercent;
	private String imageURL;
	private String expireDate;
	private String category;
	private String district;
	private String features;
	private String conditions;
	private String lat;
	private String lng;
	private String description;
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	public Place(String id, String namePlace, String addressPlace,
			String telPlace, String proPercent, String imageURL,
			String expireDate, String category, String district,String features,String lat, String lng, String conditions, String description) {
		super();
		this.id = id;
		this.namePlace = namePlace;
		this.addressPlace = addressPlace;
		this.telPlace = telPlace;
		this.proPercent = proPercent;
		this.imageURL = imageURL;
		this.expireDate = expireDate;
		this.category = category;
		this.district = district;
		this.features = features;
		this.lat = lat;
		this.lng = lng;
		this.conditions = conditions;
		this.description = description;
	}
	public String getConditions() {
		return conditions;
	}
	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	public String getFeatures() {
		return features;
	}
	public void setFeatures(String features) {
		this.features = features;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNamePlace() {
		return namePlace;
	}
	public void setNamePlace(String namePlace) {
		this.namePlace = namePlace;
	}
	public String getAddressPlace() {
		return addressPlace;
	}
	public void setAddressPlace(String addressPlace) {
		this.addressPlace = addressPlace;
	}
	public String getTelPlace() {
		return telPlace;
	}
	public void setTelPlace(String telPlace) {
		this.telPlace = telPlace;
	}
	public String getProPercent() {
		return proPercent;
	}
	public void setProPercent(String proPercent) {
		this.proPercent = proPercent;
	}
	public String getImageURL() {
		return imageURL;
	}
	public void setImageURL(String imageURL) {
		this.imageURL = imageURL;
	}
	public String getExpireDate() {
		return expireDate;
	}
	public void setExpireDate(String expireDate) {
		this.expireDate = expireDate;
	}
	
}

package android.tgudapp.model;

import java.io.Serializable;

public class CardInfomation implements Serializable {
	public final static String CARDINFORMATION = "cardInformation";
	private String cardNumberOrEmail;
	private String dateOfBirth;
	private String responseCode;
	private String spentAmount;
	private String savedAmount;
	private String redeemedPoints;
	private String remainingPoints;
	private String memberId;
	
	
	public CardInfomation(String responseCode, String spentAmount,
			String savedAmount, String redeemedPoints, String remainingPoints, String memberId) {
		super();
		this.responseCode = responseCode;
		this.spentAmount = spentAmount;
		this.savedAmount = savedAmount;
		this.redeemedPoints = redeemedPoints;
		this.remainingPoints = remainingPoints;
		this.memberId = memberId;
	}
	public CardInfomation(String cardNumberOrEmail, String dateOfBirth,
			String responseCode, String spentAmount, String savedAmount,
			String redeemedPoints, String remainingPoints, String memberId) {
		super();
		this.cardNumberOrEmail = cardNumberOrEmail;
		this.dateOfBirth = dateOfBirth;
		this.responseCode = responseCode;
		this.spentAmount = spentAmount;
		this.savedAmount = savedAmount;
		this.redeemedPoints = redeemedPoints;
		this.remainingPoints = remainingPoints;
		this.memberId = memberId;
	}
	public String getCardNumberOrEmail() {
		return cardNumberOrEmail;
	}
	public void setCardNumberOrEmail(String cardNumberOrEmail) {
		this.cardNumberOrEmail = cardNumberOrEmail;
	}
	public String getDateOfBirth() {
		return dateOfBirth;
	}
	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getSpentAmount() {
		return spentAmount;
	}
	public void setSpentAmount(String spentAmount) {
		this.spentAmount = spentAmount;
	}
	public String getSavedAmount() {
		return savedAmount;
	}
	public void setSavedAmount(String savedAmount) {
		this.savedAmount = savedAmount;
	}
	public String getRedeemedPoints() {
		return redeemedPoints;
	}
	public void setRedeemedPoints(String redeemedPoints) {
		this.redeemedPoints = redeemedPoints;
	}
	public String getRemainingPoints() {
		return remainingPoints;
	}
	public void setRemainingPoints(String remainingPoints) {
		this.remainingPoints = remainingPoints;
	}
	
	public String getMemberId() {
		return this.memberId;
	}
	
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	
}

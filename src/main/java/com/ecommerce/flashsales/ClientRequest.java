package com.ecommerce.flashsales;

public class ClientRequest {
	public String sessionID;
	public String clientIP;
	public String userID;
	public int userLevel;
	public String goodsSKU;
	public int goodsQuantity;
		
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}	
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public int getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	public String getGoodsSKU() {
		return goodsSKU;
	}
	public void setGoodsSKU(String goodsSKU) {
		this.goodsSKU = goodsSKU;
	}
	public int getGoodsQuantity() {
		return goodsQuantity;
	}
	public void setGoodsQuantity(int goodsQuantity) {
		this.goodsQuantity = goodsQuantity;
	}
	@Override
	public String toString() {
		return "ClientRequest [sessionID=" + sessionID + ", clientIP=" + clientIP + ", userID=" + userID
				+ ", userLevel=" + userLevel + ", goodsSKU=" + goodsSKU + ", goodsQuantity=" + goodsQuantity + "]";
	}
	
	
}

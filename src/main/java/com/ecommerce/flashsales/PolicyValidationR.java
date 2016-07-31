package com.ecommerce.flashsales;

import java.io.Serializable;

/***
 * 
 * @author wuwesley
 * the class for user's order info
 */

@SuppressWarnings("serial")
public class PolicyValidationR implements Serializable {
	public String sessionID;
	public String userID;
	public String goodsSKU;
	public int userLevel;
	public int orderQuantity;
	public boolean isAllowed = true;
	public boolean isThrottled = false;
	
	
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getGoodsSKU() {
		return goodsSKU;
	}
	public void setGoodsSKU(String goodsSKU) {
		this.goodsSKU = goodsSKU;
	}
	public int getUserLevel() {
		return userLevel;
	}
	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}
	public int getOrderQuantity() {
		return orderQuantity;
	}
	public void setOrderQuantity(int orderQuantity) {
		this.orderQuantity = orderQuantity;
	}
	public boolean getIsAllowed() {
		return isAllowed;
	}
	public void setIsAllowed(boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
	public boolean getIsThrottled() {
		return isThrottled;
	}
	public void setIsThrottled(boolean isThrottled) {
		this.isThrottled = isThrottled;
	}
	@Override
	public String toString() {
		return "PolicyValidationR [sessionID=" + sessionID + ", userID=" + userID + ", goodsSKU=" + goodsSKU
				+ ", userLevel=" + userLevel + ", orderQuantity=" + orderQuantity + ", isAllowed=" + isAllowed
				+ ", isThrottled=" + isThrottled + "]";
	}
	
	
}

package com.ecommerce.flashsales;

import java.io.Serializable;

/***
 * 
 * @author wuwesley
 * the class for bad guy info
 */

@SuppressWarnings("serial")
public class SafeValidationR implements Serializable{
	public String sessionID;
	public String userID;
	public Boolean isBadGuy = false;
	public String clientIP;
	public Boolean isBadIP = false;
	public Boolean isAllowed = true;
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
	public Boolean getIsBadGuy() {
		return isBadGuy;
	}
	public void setIsBadGuy(Boolean isBadGuy) {
		this.isBadGuy = isBadGuy;
	}
	public String getClientIP() {
		return clientIP;
	}
	public void setClientIP(String clientIP) {
		this.clientIP = clientIP;
	}
	public Boolean getIsBadIP() {
		return isBadIP;
	}
	public void setIsBadIP(Boolean isBadIP) {
		this.isBadIP = isBadIP;
	}
	
	public Boolean getIsAllowed() {
		return isAllowed;
	}
	public void setIsAllowed(Boolean isAllowed) {
		this.isAllowed = isAllowed;
	}
	public void doSafeValidation(){
		if (this.isBadGuy == true || this.isBadIP ==true){
			this.setIsAllowed(false);
		}
    }
	public boolean getIsThrottled() {
		return isThrottled;
	}
	public void setIsThrottled(boolean isThrottled) {
		this.isThrottled = isThrottled;
	}
	@Override
	public String toString() {
		return "SafeValidationR [sessionID=" + sessionID + ", userID=" + userID + ", isBadGuy=" + isBadGuy
				+ ", clientIP=" + clientIP + ", isBadIP=" + isBadIP + ", isAllowed=" + isAllowed + ", isThrottled="
				+ isThrottled + "]";
	}	

}

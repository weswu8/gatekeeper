package com.ecommerce.flashsales;

public class ClientResponse {
	public String reponseMsg;
	public Boolean repResult;
	public Boolean isThrottled = false;
	
	public String getReponseMsg() {
		return reponseMsg;
	}
	public void setReponseMsg(String reponseMsg) {
		this.reponseMsg = reponseMsg;
	}
	public Boolean getRepResult() {
		return repResult;
	}
	public void setRepResult(Boolean repResult) {
		this.repResult = repResult;
	}
	public Boolean getIsThrottled() {
		return isThrottled;
	}
	public void setIsThrottled(Boolean isThrottled) {
		this.isThrottled = isThrottled;
	}
	@Override
	public String toString() {
		return "ClientResponse [reponseMsg=" + reponseMsg + ", repResult=" + repResult + ", isThrottled=" + isThrottled
				+ "]";
	}
	
	
}

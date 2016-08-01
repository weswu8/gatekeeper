package com.ecommerce.flashsales;

public class ClientResponse {
	public String reponseMsg;
	public Boolean repResult;
	public Boolean isThrottled = false;
	public String version = "1.0";

	
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
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	@Override
	public String toString() {
		return "ClientResponse [reponseMsg=" + reponseMsg + ", repResult=" + repResult + ", isThrottled=" + isThrottled
				+ ", version=" + version + "]";
	}
	
}

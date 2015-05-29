package com.sample.contentProvider;

import com.parse.ParseClassName;
import com.parse.ParseObject;
@ParseClassName("Patients")
public class Patients extends ParseObject {
	public void setUserName(String name) {
		put("username", name);
	}
	public void setEmail(String mail) {
		put("email", mail);
	}
	public void setMobileNo(String mobileno) {
		put("mobileNo", mobileno);
	}
	public void setNoVerified(boolean verifyed) {
		put("noVerified", verifyed);
	}
	
	public boolean getNoVerified(){
		return getBoolean("noVerified");
	}
	public String getMobileNo(){
		return getString("mobileNo");
	}
	public String getEmail(String mail) {
		return getString("email");
	}
	public String getUserName() {
		return getString("username");
	}
}

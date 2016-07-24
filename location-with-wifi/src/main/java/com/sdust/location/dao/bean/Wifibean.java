package com.sdust.location.dao.bean;

import java.io.Serializable;

public class Wifibean implements Serializable{
	private String macaddress = null;
	private Double rssValue = null;
	private String name = null;
	public Wifibean() {
		// TODO Auto-generated constructor stub
	}

	public Wifibean(String macaddress,Double rssValue) {
		// TODO Auto-generated constructor stub
		this.macaddress = macaddress;
		this.rssValue = rssValue;
	}
	
	public Wifibean(String macaddress,Double rssValue,String name) {
		// TODO Auto-generated constructor stub
		this.macaddress = macaddress;
		this.rssValue = rssValue;
		this.name = name;
	}

	public String getMacaddress() {
		return macaddress;
	}

	public void setMacaddress(String macaddress) {
		this.macaddress = macaddress;
	}

	public Double getRssValue() {
		return rssValue;
	}

	public void setRssValue(Double rssValue) {
		this.rssValue = rssValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

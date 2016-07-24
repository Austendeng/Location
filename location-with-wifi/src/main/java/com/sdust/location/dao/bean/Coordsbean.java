package com.sdust.location.dao.bean;

import java.io.Serializable;

public class Coordsbean implements Serializable{
	private Double ref_x;
	private Double ref_y;

	public Coordsbean() {
		// TODO Auto-generated constructor stub
	}
	
	public Coordsbean(Double ref_x,Double ref_y) {
		// TODO Auto-generated constructor stub
		this.ref_x = ref_x;
		this.ref_y = ref_y;
	}
	public Double getRef_x() {
		return ref_x;
	}

	public void setRef_x(Double ref_x) {
		this.ref_x = ref_x;
	}

	public Double getRef_y() {
		return ref_y;
	}

	public void setRef_y(Double ref_y) {
		this.ref_y = ref_y;
	}
}

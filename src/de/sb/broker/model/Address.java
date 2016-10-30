package de.sb.broker.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Address {
	@Size(min=0,max=63)
	private String street;
	@Size(min=0,max=15)
	private String postCode;
	@NotNull
	@Size(min=1,max=63)
	private String city;

	public String getStreet(){
		return street;
	}
	
	public void setStreet(String street) {
		this.street = street;
	}
	
	public String getPostCode(){
		return postCode;
	}
	
	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}
	
	public String getCity(){
		return city;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
}



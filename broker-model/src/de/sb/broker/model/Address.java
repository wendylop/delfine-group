package de.sb.broker.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;

@Embeddable
public class Address {
	
	@Column(nullable = true, updatable = true, length = 63)
	@Size (min=0, max=63)
	@XmlElement(name="street")
	private String street;
	
	@Column(nullable = true, updatable = true, length = 15)
	@Size (min=0, max=15)
	@XmlElement(name="postCode")
	private String postCode;
	
	@Column(nullable = true, updatable = true, length = 63)
	@Size (min=1, max=63)
	@NotNull
	@XmlElement(name="city")
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



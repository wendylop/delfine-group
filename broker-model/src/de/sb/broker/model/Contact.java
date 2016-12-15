package de.sb.broker.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.sun.istack.internal.NotNull;

@Embeddable
@XmlType
public class Contact {
	
	@Column(nullable = false, updatable = true, length = 63, unique = true)
	@Pattern(regexp="^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$") //nur auf @
	@Size (min = 1, max = 63)
	@NotNull
	@XmlElement
	private String email;
	
	@Column(nullable = true, updatable = true, length = 63)
	@Size (min = 0, max = 63)
	@NotNull
	@XmlElement
	private String phone;
	
	//Attributen
	public String getEmail(){ //nur mit diese Methoden zugreifen 
		return email;
	}
	
	public void setEmail(String email) { //set : entsprechenden Variable Werten zugewiesen werden
		this.email = email;
		
	}
	
	public String getPhone(){
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}

}


package de.sb.broker.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class Contact {
	@NotNull
	@Size(min=1,max=63)
	@Pattern(regexp=".@.")
	private String email;

	@Size(min=0,max=63)
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


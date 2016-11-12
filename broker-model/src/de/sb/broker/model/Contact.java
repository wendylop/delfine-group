package de.sb.broker.model;

public class Contact {
	
	private String email;
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


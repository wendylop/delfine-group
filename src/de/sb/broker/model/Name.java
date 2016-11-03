package de.sb.broker.model;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class Name {
	@NotNull
	@Size(min=1,max=31)
	private String family;
	
	@NotNull
	@Size(min=1,max=31)
	private String given;
	
	public String getFamily(){
		return family;
	}
	
	public void setFamily(String family) {
		this.family = family;
	}

	public String getGiven(){
		return given;
	}
	
	public void setGiven(String given) {
		this.given = given;
	}
	
}




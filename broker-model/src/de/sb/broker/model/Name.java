package de.sb.broker.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.sun.istack.internal.NotNull;

@Embeddable
@XmlType
public class Name {
	
	@Column(name = "familyName", nullable = true, updatable = true, insertable=true, length = 31)
	@Size (min=1, max=31)
	@NotNull
	@XmlElement
	private String family;
	
	@Column(name = "givenName", nullable = true, updatable = true, insertable=true, length = 31)
	@Size (min=1, max=31)
	@NotNull
	@XmlElement
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




package de.sb.broker.model;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(schema = "broker", name = "BaseEntity")
@Inheritance(strategy=InheritanceType.JOINED)
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.STRING)
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name="")
@XmlSeeAlso({Auction.class, Bid.class, Person.class})
public class BaseEntity implements Comparable<BaseEntity>{
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@XmlElement
	private final long identity;
	
	@Column(nullable = false, updatable = false)
	@XmlElement
	private volatile int version;
	
	@Column(nullable = false, updatable = false)
	@XmlElement
	private final long creationTimestamp;
	
	public BaseEntity(){
		this.identity = 0;
		this.version = 1;
		this.creationTimestamp = System.currentTimeMillis();
	}
	
	public long getIdentity(){
		return this.identity;
	}
	
	@Min(value = 1)
	public int getVersion(){
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	
	public long getCreationTimestamp(){
		return creationTimestamp;
	}

	@Override
	public int compareTo(BaseEntity o) {
		return Long.compare(this.identity, o.identity);
	}
	

}


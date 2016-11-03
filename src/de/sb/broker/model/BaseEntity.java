package de.sb.broker.model;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import javax.validation.constraints.Min;

@Entity
@Table(name="BASEENTITY",schema="")
@Inheritance
@DiscriminatorColumn 
public class BaseEntity implements Comparable<BaseEntity>{
	@GeneratedValue
	@Id
	private final long identity;
	
	@Min(1)
	private volatile int version;
	private final long creationTimestamp;
	
	public BaseEntity(){
		this.identity = 0;
		this.version = 1;
		this.creationTimestamp = System.currentTimeMillis();
	}
	
	public long getIdentity(){
		return this.identity;
	}
	
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


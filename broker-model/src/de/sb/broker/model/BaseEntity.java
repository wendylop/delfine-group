package de.sb.broker.model;

public class BaseEntity implements Comparable<BaseEntity>{
	
	private final long identity;
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


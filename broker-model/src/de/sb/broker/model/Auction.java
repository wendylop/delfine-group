package de.sb.broker.model;

import java.util.HashSet;
import java.util.Set;

public class Auction extends BaseEntity {

	private String title;
	private short unitCount;
	private long askingPrice;
	private long closureTimestamp;
	private String description;
	private final Person seller;
	private Set<Bid> bids;

	public Auction(Person seller) {
		//TODO defaults
		this.title = title;
		this.unitCount = unitCount;
		this.askingPrice = askingPrice;
		this.closureTimestamp = closureTimestamp;// +30*24*60*60*1000 oder Duration
		this.description = description;
		this.seller = seller;
		bids = new HashSet<Bid>();
	}
	
	protected Auction(){
		this(null);
	}

	//TODO getTitle
	
	public void setTitle(String title) {
		this.title = title;
	}

	public short getUnitCount() {
		return unitCount;
	}

	//TODO setUnitCount
	
	public long getAskingPrice() {
		return askingPrice;
	}
	
	//TODO setAskingPrice

	public long getClosureTimestamp() {
		return closureTimestamp;
	}
	
	//TODO setClosureTimestamp

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Person getSeller() {
		return seller;
	}

	public long getSellerReference() {
		return this.seller == null ? 0 : this.seller.getIdentity();
	}

	public Bid getBid(Person bidder) {
		for (Bid bid : this.bids) {
			if (bid.getBidder().getIdentity() == bidder.getIdentity()) {
				return bid;
			}
		}
		return null;
	}

	public boolean isClosed() {
		return this.closureTimestamp > System.currentTimeMillis();
	}

	public boolean isSealed() {
		return this.isClosed() || !bids.isEmpty();
	}

}

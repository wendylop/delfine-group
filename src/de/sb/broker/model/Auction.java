package de.sb.broker.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Embeddable;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.sb.java.validation.Inequal;

@Embeddable
@Inequal(leftAccessPath = { "" }, rightAccessPath = { "" })
public class Auction extends BaseEntity {

	@NotNull
	@Size(min=1,max=255)
	private String title;
	@Min(1)
	private short unitCount;
	@Min(0)
	private long askingPrice;
	private long closureTimestamp;
	
	@NotNull
	@Size(min=1,max=8189)
	private String description;
	//kein @NotNull
	private final Person seller;

	@Size(min=0)
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

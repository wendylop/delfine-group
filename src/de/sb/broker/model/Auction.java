package de.sb.broker.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.sb.java.validation.Inequal;

@Entity
@Table(schema = "broker", name = "Auction", indexes = @Index(columnList = "closureTimestamp", unique = true))
@PrimaryKeyJoinColumn(name = "auctionIdentity")
@Inequal(leftAccessPath = { "closureTimestamp" }, rightAccessPath = { "creationTimestamp" })

public class Auction extends BaseEntity {

	@Column(nullable = false, updatable = true, length = 255)
	@Size(min = 1, max = 255)
	@NotNull
	private String title;
	
	@Column(nullable = false, updatable = true)
	@Min(1)
	@NotNull
	private short unitCount;
	
	@Column(nullable = false, updatable = true)
	@NotNull
	@Min(0)
	private long askingPrice;
	
	@Column(nullable = false, updatable = true)
	@NotNull
	private long closureTimestamp;
	
	@Column(nullable = false, updatable = true, length = 8189)
	@Size(min = 1, max = 8189)
	@NotNull
	private String description;
	
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "sellerReference", nullable = false, updatable = false)
	//@NotNull
	private Person seller;
	
	@OneToMany(mappedBy = "auction")
	//@NotNull
	private Set<Bid> bids;

	public Auction(Person seller) {
		//TODO defaults
		this.title = "";
		this.unitCount = 0;
		this.askingPrice = 0;
		this.closureTimestamp = System.currentTimeMillis() + (30*24*60*60*1000);;// +30*24*60*60*1000 oder Duration
		this.description = "";
		this.seller = seller;
		bids = new HashSet<Bid>();
	}
	
	protected Auction(){
		this(null);
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public short getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(short unitCount) {
		this.unitCount = unitCount;
	}
	
	public long getAskingPrice() {
		return askingPrice;
	}
	
	public void setAskingPrice(long askingPrice) {
		this.askingPrice = askingPrice;
	}

	public long getClosureTimestamp() {
		return closureTimestamp;
	}
	
	public void setClosureTimestamp(long closureTimestamp) {
		this.closureTimestamp = closureTimestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Person getSeller() {
		return seller;
	}
	
	public void setSeller (Person seller){
		this.seller = seller;
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

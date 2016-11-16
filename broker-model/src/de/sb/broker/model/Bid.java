package de.sb.broker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;


import de.sb.java.validation.Inequal;

@Entity
@Table(schema = "broker", name = "Bid", uniqueConstraints = @UniqueConstraint(columnNames = { "bidderReference", "auctionReference" }))
@PrimaryKeyJoinColumn(name = "bidIdentity")
@Inequal(leftAccessPath = "price", rightAccessPath = { "auction", "askingPrice" } , operator = Inequal.Operator.GREATER_EQUAL )
@Inequal(leftAccessPath = { "auction", "seller" , "identity" }, rightAccessPath = {  "bidder" , "identity" } , operator = Inequal.Operator.NOT_EQUAL)

public class Bid extends BaseEntity  {

    @Min(value = 1)
    @Column(nullable= false, updatable= true)
	private long price;
	
    @ManyToOne
	@JoinColumn(name ="auctionReference")
	private final Auction auction;
	
	@JoinColumn(name ="bidderReference")
	@ManyToOne
	private final Person bidder;

	public Bid(Auction auction,Person bidder){
		
		this.price = auction == null ? 1 : auction.getAskingPrice();
		this.auction = auction;		
		this.bidder = bidder;
	}
	
	protected Bid(){
		this(null,null);
	}
	
	public long getPrice(){
		return this.price;
	}
	
	public void setPrice(long price) {
		this.price =  price;
	}
	public Auction getAuction() {
		return auction;
	}
	
	public long getAuctionReference() {
		return this.auction == null ? 0 : this.auction.getIdentity();
	}
	
	public Person getBidder() {
		return bidder;
	}
	
	public long getBidderReference() {
		return this.bidder == null ? 0 : this.bidder.getIdentity();
	}
	
}


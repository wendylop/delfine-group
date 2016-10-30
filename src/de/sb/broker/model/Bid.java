package de.sb.broker.model;

/*import de.sb.java.validation.Inequal;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;*/

import javax.validation.constraints.Min;
//import javax.validation.constraints.NotNull;

public class Bid extends BaseEntity  {

	@Min(1)
	private long price;
	
	//hier kein NotNull
	private final Auction auction;
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


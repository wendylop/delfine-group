package de.sb.broker.rest;

import java.util.Set;

import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Bid;
import de.sb.broker.model.Person;

@Path("/people")
public class AuctionService {
	@GET 
	@Path("/people/{identity}")
	public Person getPeople (@PathParam("identity") Long identity) {
		return null;
		
	}
	
	@GET
	@Path("/people/{identity}/auctions")
	public Set<Auction> getAuctions (@PathParam("identity") Long identity) {
		return null;
		
	}
	
	@GET
	@Path("/people/{identity}/bids")
	public Set<Bid> getBids (@PathParam("identity") Long identity) {
		return null;
		
	}
	
}



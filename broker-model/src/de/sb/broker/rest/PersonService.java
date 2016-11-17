package de.sb.broker.rest;

import javax.websocket.server.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import de.sb.broker.model.Auction;
import de.sb.broker.model.Person;

@Path("/auctions")
public class PersonService {
	
	@GET 
	@Path("/auctions/{identity}")
	public Auction getAuction (@PathParam("identity") Long identity) {
		return null;
		
	}
	
	@PUT
	@Path("/auctions")
	public void modAuction () {}
}

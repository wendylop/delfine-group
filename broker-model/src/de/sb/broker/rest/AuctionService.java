package de.sb.broker.rest;


import java.util.List;
//import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import de.sb.broker.model.Auction;

@Path("/auctions")
public class AuctionService {
	
	static EntityManagerFactory emf;
	EntityManager em = emf.createEntityManager();
	/*
	 * GET /auctions: Returns the auctions matching the given criteria, 
	 * with null or missing parameters identifying omitted criteria.
	 * ---
	 * no path needed since the one we are referencing 
	 * was set before the class definition
	 */	
	@GET
	public List<Auction> getAuctions(
			@QueryParam("title") String title, 
			@QueryParam("minUC") Short minUnitCount, 
			@QueryParam("maxUC") Short maxUnitCount,
			@QueryParam("minAP") Long minAskingPrice,
			@QueryParam("maxAP") Long maxAskingPrice,
			@QueryParam("maxClosureTimestamp") Long maxClosureTimestamp,
			@QueryParam("description") String description){
		
		TypedQuery<Auction> qa = em.createQuery("select a from Auction as a where"
				+ "(:title is null or a.title = :title) and"
				+ "(:minUC is null or a.unitCount >= :minUC) and"
				+ "(:maxUC is null or a.unitCount <= :maxUC) and"
				+ "(:minAP is null or a.askingPrice >= :minAP) and"
				+ "(:maxAP is null or a.askingPrice <= :maxAP) and"
				+ "(:maxClosureTimestamp is null or a.closureTimestamp <= :maxClosureTimestamp) and"
				+ "(:description is null or a.description = :description)", 
				Auction.class);
		
		qa.setParameter("title", title);
		qa.setParameter("minUC", minUnitCount);
		qa.setParameter("maxUC", maxUnitCount);
		qa.setParameter("minAP", minAskingPrice);
		qa.setParameter("maxAP", maxAskingPrice);
		qa.setParameter("maxClosureTimestamp", maxClosureTimestamp);
		qa.setParameter("description", description);
		
		List<Auction> auctions = qa.getResultList();
		
		return auctions;
	}
	/*
	PUT /auctions: Creates or modifies an auction from the given template data. 
	Note that an auction may only be modified as long as it is not sealed 
	(i.e. is open and still without bids).
	*/
	@PUT
	public void putAuctions(){
		
	}

	/*
	GET /auctions/{identity}: Returns the auction matching the given identity
	  */
	@GET
	@Path("/{identity}")
	public Auction getAuction(@PathParam("identity") Long identity){
		TypedQuery<Auction> query = em.createQuery("select a from Auction as a where"
				+ "(:identity = a.identity)", 
				Auction.class);
		
		query.setParameter("identity", identity);
		
		Auction auction = query.getSingleResult();
		
		return auction;
	}
	
	/** old code ... **/
	/*
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
	}*/
	
}




package de.sb.broker.rest;


import java.util.ArrayList;
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
import de.sb.broker.model.Person;

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
			@QueryParam("description") String description,
			@QueryParam("firstResult") int firstResult,
			@QueryParam("maxResults") int maxResults){
		
		TypedQuery<Long> qa = em.createQuery("select a.identity from Auction as a where"
				+ "(:title is null or a.title = :title) and"
				+ "(:minUC is null or a.unitCount >= :minUC) and"
				+ "(:maxUC is null or a.unitCount <= :maxUC) and"
				+ "(:minAP is null or a.askingPrice >= :minAP) and"
				+ "(:maxAP is null or a.askingPrice <= :maxAP) and"
				+ "(:maxClosureTimestamp is null or a.closureTimestamp <= :maxClosureTimestamp) and"
				+ "(:description is null or a.description = :description)", 
				Long.class);
		
		qa.setParameter("title", title);
		qa.setParameter("minUC", minUnitCount);
		qa.setParameter("maxUC", maxUnitCount);
		qa.setParameter("minAP", minAskingPrice);
		qa.setParameter("maxAP", maxAskingPrice);
		qa.setParameter("maxClosureTimestamp", maxClosureTimestamp);
		qa.setParameter("description", description);
		if(maxResults > 0) qa.setMaxResults(maxResults);		
		if(firstResult > 0) qa.setFirstResult(firstResult);
		
		
		List<Long> auctionIds = qa.getResultList();
		List<Auction> auctions = new ArrayList<Auction>();
		
		//for-Schleife über alle IDs
		for(Long id : auctionIds){
			auctions.add(em.find(Auction.class, id));
		}
		
		//sort by close date
		auctions.sort((a,b) -> Long.compare(a.getClosureTimestamp(),b.getClosureTimestamp()));
		
		return auctions;
	}
	/*
	PUT /auctions: Creates or modifies an auction from the given template data. 
	Note that an auction may only be modified as long as it is not sealed 
	(i.e. is open and still without bids).
	*/
	@PUT
	public void putAuctions(Auction template){
		final boolean insert = template.getIdentity() == 0;
		/*
		 * TODO implement
		 */
		/*
		cache = em.getEntityManagerFactory().getCache();
		cache.evict(entity.getClass(), entity.getIdentity());
		 */
		
	}

	/*
	GET /auctions/{identity}: Returns the auction matching the given identity
	  */
	@GET
	@Path("/{identity}")
	public Auction getAuction(@PathParam("identity") Long identity){
		
		Auction auction = em.find(Auction.class, identity);
		
		return auction;
	}
	
}




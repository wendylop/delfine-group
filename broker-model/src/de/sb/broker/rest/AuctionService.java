package de.sb.broker.rest;


import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cache;
//import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import de.sb.broker.model.Auction;
import de.sb.broker.model.Person;

@Path("/auctions")
public class AuctionService {
	
	//static EntityManagerFactory emf;
	//EntityManager em = emf.createEntityManager();
	
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
		
		EntityManager em = LifeCycleProvider.brokerManager();
		
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
	* PUT /auctions: Creates or modifies an auction from the given template data. 
	* Note that an auction may only be modified as long as it is not sealed 
	* (i.e. is open and still without bids).
	*
	* PUT /auctions: 
	* Additionally constrain that an auction may only be altered 
	* if the requester is the same as the auction's seller.
	*/
	@PUT
	public void putAuctions(Auction template){
		EntityManager em = LifeCycleProvider.brokerManager();
		
		try {
			final boolean insert = template.getIdentity() == 0;
			/*
			 * TODO implement
			 */
	
			TypedQuery<Long> qa = em.createQuery("select a.identity from Auction as a where"
					+ "(:title is null or a.title = :title) and"
					+ "(:unitCount is null or a.unitCount = :unitCount) and"
					+ "(:askingPrice is null or a.askingPrice = :askingPrice) and"
					+ "(:closureTimestamp is null or a.closureTimestamp = :closureTimestamp) and"
					+ "(:description is null or a.description = :description) and"
					+ "(:seller is null or a.seller = :seller)" , 
					Long.class);
			
			qa.setParameter("title", template.getTitle());
			qa.setParameter("unitCount", template.getUnitCount());
			qa.setParameter("askingPrice", template.getAskingPrice());
			qa.setParameter("closureTimestamp", template.getClosureTimestamp());
			qa.setParameter("description", template.getDescription());
			qa.setParameter("seller", template.getSeller());
	
			
			
			List<Long> auctionIds = qa.getResultList();
			List<Auction> auctions = new ArrayList<Auction>();
			
			//for-Schleife über alle IDs
			for(Long id : auctionIds){
				auctions.add(em.find(Auction.class, id));
			}
			
			//Frage: Wie Zugriff auf alle Auctions
			
			//Verwendung? cache = em.getEntityManagerFactory().getCache();
			//cache.evict(entity.getClass(), entity.getIdentity());
			
			em.getTransaction().commit();
		} finally {
			em.getTransaction().begin();
		}
	}

	/*
	 * GET /auctions/{identity}: Returns the auction matching the given identity
	 */
	@GET
	@Path("/{identity}")
	public Auction getAuction(@PathParam("identity") Long identity){
		
		EntityManager em = LifeCycleProvider.brokerManager();
		
		Auction auction = em.find(Auction.class, identity);
		
		return auction;
	}
	
	/*
	 * GET /auctions/{identity}/bid (new): 
	 * Returns the requester's bid for the given auction, or null if none exists.
	 */
	
	/*
	 * POST /auctions/{identity}/bid (new): 
	 * Creates or modifies the requester's bid for the given auction, 
	 * depending on the requester and the price (in cent) within the given request body. 
	 * If the price is zero, then the requester's bid is removed instead.
	 */
	
}




package de.sb.broker.rest;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import de.sb.broker.model.Address;
import de.sb.broker.model.Auction;
import de.sb.broker.model.Bid;
import de.sb.broker.model.Contact;
import de.sb.broker.model.Name;
import de.sb.broker.model.Person;
import de.sb.broker.model.Person.Group;

@Path("/people")
public class PersonService {
	
	static EntityManagerFactory emf;
	EntityManager em = emf.createEntityManager();
	
	/*
	 * GET /people: Returns the people matching the given criteria, 
	 * with null or missing parameters identifying omitted criteria.
	 */
	@GET
	public List<Person> getPeople(
			@QueryParam("alias") String alias,
			@QueryParam("group") Group group,
			@QueryParam("name") Name name,
			@QueryParam("address") Address address,
			@QueryParam("contact") Contact contact,
			@QueryParam("firstResult") int firstResult,
			@QueryParam("maxResults") int maxResults
			){
		
		TypedQuery<Long> query = em.createQuery("select p.identity from Person as p where"
				+ "(:alias is null or p.alias = :alias) and"
				+ "(:group is null or p.group = :group) and"
				+ "(:name is null or p.name = :name) and"
				+ "(:address is null or p.address = :address) and"
				+ "(:contact is null or p.contact = :contact)" , 
				Long.class);
		
		query.setParameter("alias", alias);
		query.setParameter("group", group);
		query.setParameter("name", name);
		query.setParameter("address", address);
		query.setParameter("contact", contact);
		if(maxResults > 0) query.setMaxResults(maxResults);		
		if(firstResult > 0) query.setFirstResult(firstResult);
		
		List<Long> peopleIds = query.getResultList();
		List<Person> people = new ArrayList<Person>();
		
		//for-Schleife über alle IDs
		for(Long id : peopleIds){
			people.add(em.find(Person.class, id));
		}
		
		//sort by alias
		people.sort((a,b) -> a.getAlias().compareTo(b.getAlias()));
		
		return people;
	}
	
	
	/*
	 * GET /people/{identity}: Returns the person matching the given identity.
	 */
	@GET
	@Path("/{identity}")
	public Person getPerson(@PathParam("identity") long identity){
		
		Person person = em.find(Person.class, identity);
		
		return person;
	}
	
	/*
	 * GET /people/{identity}/auctions: Returns all auctions associated with the person 
	 * matching the given identity (as seller or bidder).
	 */
	@GET
	@Path("/{identity}/auctions")
	public Set<Auction> getSomeonesAuctions(@PathParam("identity") long identity){
		
		Person person = em.find(Person.class, identity);

		return person.getAuctions();//TODO rückgabe sortieren: toArray(new Auction[0])  

	}
	
	/*
	GET /people/{identity}/bids: Returns all bids for closed auctions 
	associated with the bidder matching the given identity.
	 */
	@GET
	@Path("/{identity}/bids")
	public Set<Bid> getSomeonesBids(@PathParam("identity") long identity) {
		
		Person person = em.find(Person.class, identity);
		//TODO define getter
		Set<Bid> bids = person.getBids();
		//TODO rückgabe sortieren
		Bid[] bidArray = bids.toArray(new Bid[0]);		
		
		return bids;
		
		
	}
	
	/*
	PUT /{identity}: Creates or modifies a person
	*/
	@PUT
	public void putPerson(Person p){
		final boolean insert = p.getIdentity() == 0;
		/*
		 * TODO write putPerson
		 * 
		 * if existent: update person
		 * 
		 * else: create new person
		 * 
		 */
		
		/*
		cache = em.getEntityManagerFactory().getCache();
		cache.evict(entity.getClass(), entity.getIdentity());
		 */
		
	}
}

package de.sb.broker.rest;


import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.ws.rs.GET;
//import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import de.sb.broker.model.Address;
import de.sb.broker.model.Auction;
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
			@QueryParam("contact") Contact contact){
		
		TypedQuery<Person> query = em.createQuery("select p from Person as p where"
				+ "(:alias is null or p.alias = :alias) and"
				+ "(:group is null or p.group = :group) and"
				+ "(:name is null or p.name = :name) and"
				+ "(:address is null or p.address = :address) and"
				+ "(:contact is null or p.contact = :contact)" , 
				Person.class);
		
		query.setParameter("alias", alias);
		query.setParameter("group", group);
		query.setParameter("name", name);
		query.setParameter("address", address);
		query.setParameter("contact", contact);
		
		List<Person> people = query.getResultList();
		
		return people;
	}
	
	
	/*
	 * GET /people/{identity}: Returns the person matching the given identity.
	 */
	@GET
	@Path("/{identity}")
	public Person getPerson(@PathParam("identity") Long identity){
		
		TypedQuery<Person> query = em.createQuery("select p from Person as p where"
				+ "(:identity = p.identity)", 
				Person.class);
		
		query.setParameter("identity", identity);
		
		Person person = query.getSingleResult();
		
		return person;
	}
	
	/*
	 * GET /people/{identity}/auctions: Returns all auctions associated with the person 
	 * matching the given identity (as seller or bidder).
	 */
	@GET
	@Path("/{identity}/auctions")
	public Set<Auction> getSomeonesAuctions(@PathParam("identity") Long identity){
		
		TypedQuery<Person> query = em.createQuery("select p from Person as p where"
				+ "(:identity = p.identity)", 
				Person.class);
		
		query.setParameter("identity", identity);
		
		Person person = query.getSingleResult();
		
		return person.auctions;
	}
	
	/*
	GET /people/{identity}/bids: Returns all bids for closed auctions 
	associated with the bidder matching the given identity.
	 */
	
	
	/** old code ... **/
	/*
	@GET 
	@Path("/auctions/{identity}")
	public Auction getAuction (@PathParam("identity") Long identity) {
		return null;
	}
	
	@PUT
	@Path("/auctions")
	public void modAuction () {}
	*/
}

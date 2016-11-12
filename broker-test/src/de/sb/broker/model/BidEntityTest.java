package de.sb.broker.model;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BidEntityTest extends EntityTest{
	private EntityManager em;
	private Validator validator;
	
	Set<ConstraintViolation<Bid>> constrainViolations;
	
	@Test
	public void testConstraints()
	{
		validator = this.getEntityValidatorFactory().getValidator();
		
		Bid bid1 = new Bid();
		bid1.setPrice(40);
		
		Bid bid2 = new Bid();
		bid2.setPrice(-40);
		
		//test good bid
		constrainViolations = validator.validate(bid1);
		//check if there are errors - should be 0
		assertEquals(constrainViolations.size(), 0);
		
		//test bad bid
		constrainViolations = validator.validate(bid2);
		//check if there are errors - should be 1
		assertEquals(constrainViolations.size(), 1);		
	}
	
	@Test
	public void testLifeCycle()
	{
		em = this.getEntityManagerFactory().createEntityManager();
		em.getTransaction().begin();
				
		//get a Person fromn Database
		Person person = em.find(Person.class, 1l);

		//get a Auction with Sascha as seller
		TypedQuery<Auction> query = em.createQuery("SELECT a FROM Auction a", Auction.class);
		List<Auction> allAuctions = query.getResultList();

		// construct new Bid
		Bid bid = new Bid(allAuctions.get(4), person);
		
		em.persist(bid);
		em.getTransaction().commit();
		this.getWasteBasket().add(bid.getIdentity());
		
		bid = em.getReference(Bid.class, bid.getIdentity());
		assertEquals(bid.getBidder(), person);
		
		em.getTransaction().begin();
		em.remove(bid);
		bid = em.getReference(Bid.class, bid.getIdentity());
		em.getTransaction().commit();
		
		em.close();
		
	}

}

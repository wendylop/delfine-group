package de.sb.broker.model;

import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.After;
import org.junit.Assert;
import org.junit.Test;


public class BidEntityTest extends EntityTest{
	private Validator validator;	
	Set<ConstraintViolation<Bid>> constrainViolations;
	private final EntityManager em = this.getEntityManagerFactory().createEntityManager();
	
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
		Assert.assertEquals(constrainViolations.size(), 0);
		
		//test bad bid
		constrainViolations = validator.validate(bid2);
		//check if there are errors - should be 1
		Assert.assertEquals(constrainViolations.size(), 1);		
	}
	
	@Test
	public void testLifeCycle()
	{
		em.getTransaction().begin();
				
		//get a person from database with name "Ines"
		Person person = em.find(Person.class, 1l);
		Assert.assertEquals(person.getName().getGiven(), "Ines");

		//get all auctions with Sascha as seller - there should be 3
		TypedQuery<Auction> query = em.createQuery("SELECT a FROM Auction a", Auction.class);
		List<Auction> allAuctions = query.getResultList();
		Assert.assertEquals(allAuctions.size(), 5);
		
		//construct new bid
		Bid bid = new Bid(allAuctions.get(4), person);
		bid.setPrice(2020);
		
		em.persist(bid);
		em.getTransaction().commit();
		this.getWasteBasket().add(bid.getIdentity());
		
		//test if entity exists in database
		bid = em.find(Bid.class, bid.getIdentity());
		Assert.assertEquals(bid.getBidder().getName().getGiven(), "Ines");
		Assert.assertEquals(bid.getPrice(), 2020);
		
		//remove bid from database and check if it has been deleted properly
		em.getTransaction().begin();
		em.remove(bid);
		em.getTransaction().commit();
		
		bid = em.find(Bid.class, bid.getIdentity());
		Assert.assertNull(bid);
		
		em.clear();
		em.close();
		
	}
	
	@After
	public void finializeTests(){
		if (em.isOpen()){
			em.clear();
			em.close();
		} 
	}

}

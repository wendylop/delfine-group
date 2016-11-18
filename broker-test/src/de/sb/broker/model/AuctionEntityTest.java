package de.sb.broker.model;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.After;
import org.junit.Assert;

public class AuctionEntityTest extends EntityTest{
	private Validator validator;	
	Set<ConstraintViolation<Auction>> constrainViolations;	
	private final EntityManager em = this.getEntityManagerFactory().createEntityManager();
	
	@Test
	public void testConstraints()
	{		
		validator = this.getEntityValidatorFactory().getValidator();
		//create valid person
		Person seller = new Person();
		seller.setAlias("Batman");
		seller.getName().setFamily("Bruce");
		seller.getName().setGiven("Wayne");
		seller.getAddress().setStreet("Wayne Street 1");
		seller.getAddress().setCity("Gotham");
		seller.getAddress().setPostCode("30456");
		seller.getContact().setEmail("bruce.wayne@wayne-enterprise.com");
		seller.getContact().setPhone("01506060601");
		
		Auction auction = new Auction(seller);
		auction.setTitle("Batarang for sale");
		auction.setDescription("Selling brand new Batarang");
		//auction.setSeller(seller);
		auction.setAskingPrice(8000l);
		//auction.setClosureTimestamp(System.currentTimeMillis() +30*24*60*60*1000);
		auction.setUnitCount((short) 1);
		
		constrainViolations = validator.validate(auction);
		//check if there are errors - should be 0
		assertEquals(constrainViolations.size(), 0);
		
		//set bad values
		auction.setTitle("");
		auction.setDescription("");
		//auction.setSeller(null);
		
		constrainViolations = validator.validate(auction);
		//check if there are errors - should be 2
		assertEquals(constrainViolations.size(), 2);
	}
	
	@Test
	public void testLifeCycle()
	{
		em.getTransaction().begin();
				
		//get a person from database
		Person person = em.find(Person.class, 1l);
		Assert.assertNotNull(person);
		
		//construct a new auction with given user
		Auction auction = new Auction(person);
		auction.setTitle("Batarang for sale");
		auction.setDescription("Selling brand new Batarang");
		auction.setAskingPrice(8000l);
		auction.setUnitCount((short) 1);
		
		em.persist(auction);
		em.getTransaction().commit();
		this.getWasteBasket().add(auction.getIdentity());
		
		//test if entity exists in database
		//TODO "find" benutzen
		auction = em.find(Auction.class, auction.getIdentity());
		assertEquals(auction.getTitle(), "Batarang for sale");
		//TODO em.clear();
		
		//remove auction from database and check if it has been deleted properly
		em.getTransaction().begin();		
		em.remove(auction);
		em.getTransaction().commit();

		auction = em.find(Auction.class, auction.getIdentity());
		Assert.assertNull(auction);
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
